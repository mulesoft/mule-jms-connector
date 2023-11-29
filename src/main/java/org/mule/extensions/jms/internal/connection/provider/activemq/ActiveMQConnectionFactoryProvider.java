/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.connection.provider.activemq;

import static java.lang.String.format;
import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.core.api.util.ClassUtils.instantiateClass;

import org.apache.activemq.util.URISupport;
import org.mule.extensions.jms.api.connection.factory.activemq.ActiveMQConnectionFactoryConfiguration;
import org.mule.extensions.jms.api.exception.JmsMissingLibraryException;
import org.mule.extensions.jms.internal.connection.exception.ActiveMQException;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.ExclusiveOptionals;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link ActiveMQConnectionFactory} provider.
 * If no custom {@link ConnectionFactory} is given, then this provider knows how to build
 * a default {@link ActiveMQConnectionFactory} and how to configure it using the given
 * {@link ActiveMQConnectionFactoryConfiguration}
 *
 * @since 1.0
 */
@ExclusiveOptionals
public class ActiveMQConnectionFactoryProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMQConnectionFactoryProvider.class);

  private static final String ACTIVEMQ_CONNECTION_FACTORY_CLASS = "org.apache.activemq.ActiveMQConnectionFactory";
  private static final String ACTIVEMQ_SSL_CONNECTION_FACTORY_CLASS = "org.apache.activemq.ActiveMQSslConnectionFactory";
  private static final String ACTIVEMQ_XA_CONNECTION_FACTORY_CLASS = "org.apache.activemq.ActiveMQXAConnectionFactory";
  private static final String ACTIVEMQ_XA_SSL_CONNECTION_FACTORY_CLASS = "org.apache.activemq.ActiveMQXASslConnectionFactory";

  private static final int REDELIVERY_IGNORE = -1;
  private static final String VERIFY_HOSTNAME = "socket.verifyHostName";

  /**
   * Parameters required to configure a default {@link ActiveMQConnectionFactory}
   */
  @Parameter
  @Optional
  @NullSafe
  @Expression(NOT_SUPPORTED)
  @Placement(order = 1)
  private ActiveMQConnectionFactoryConfiguration factoryConfiguration;

  /**
   * A custom {@link ConnectionFactory} that relates to an {@link ActiveMQConnectionFactory}
   */
  @Parameter
  @Optional
  @Expression(NOT_SUPPORTED)
  @Placement(order = 2)
  private ConnectionFactory connectionFactory;

  public ConnectionFactory getConnectionFactory() {
    return connectionFactory;
  }

  ActiveMQConnectionFactoryConfiguration getFactoryConfiguration() {
    return factoryConfiguration;
  }

  ConnectionFactory createDefaultConnectionFactory(boolean useSsl) throws ActiveMQException {
    String factoryClass = getFactoryClass(useSsl);

    try {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(format("Creating new [%s]", factoryClass));
      }
      this.connectionFactory =
          (ConnectionFactory) instantiateClass(factoryClass, setPropertiesInURL(factoryConfiguration.getBrokerUrl(), factoryClass,
                                                                                factoryConfiguration));
      applyVendorSpecificConnectionFactoryProperties(connectionFactory);

      return connectionFactory;
    } catch (ClassNotFoundException e) {
      String message =
          format("Failed to create a default Connection Factory for ActiveMQ using the [%s] implementation because the Class was not found. \n "
              +
              "Please verify that you have configured the ActiveMQ Client Dependency as a Shared Library of your application.",
                 factoryClass);
      LOGGER.error(message, e);
      throw new JmsMissingLibraryException(e, message);
    } catch (Exception e) {
      String message = format("Failed to create a default Connection Factory for ActiveMQ using the [%s] implementation: %s",
                              factoryClass, e.getMessage());
      LOGGER.error(message, e);
      throw new ActiveMQException(message, e);
    }
  }

  private void applyVendorSpecificConnectionFactoryProperties(ConnectionFactory connectionFactory) {
    try {
      Method getRedeliveryPolicyMethod = connectionFactory.getClass().getMethod("getRedeliveryPolicy");
      Object redeliveryPolicy = getRedeliveryPolicyMethod.invoke(connectionFactory);
      setMaximumRedeliveries(redeliveryPolicy);
      setInitialRedeliveryDelay(redeliveryPolicy);
      setRedeliveryDelay(redeliveryPolicy);
      setTrustedPackages(connectionFactory);
      setTrustAllPackages(connectionFactory);
      setXAAckMode(connectionFactory, factoryConfiguration.getXaAckMode().getAckMode());
    } catch (Exception e) {
      LOGGER.error("Failed to set custom ConnectionFactoryProperties for ActiveMQ RedeliveryPolicy: " + e.getMessage(), e);
    }
  }

  private void setXAAckMode(ConnectionFactory factory,
                            int xaAckMode)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    String factoryClassName = factory.getClass().getCanonicalName();
    if (isActiveMqXaFactory(factoryClassName)) {
      Class[] parameters = new Class[1];
      parameters[0] = int.class;
      factory.getClass().getMethod("setXaAckMode", parameters).invoke(factory, xaAckMode);
    }
  }

  private boolean isActiveMqXaFactory(String factoryClassName) {
    return factoryClassName == ACTIVEMQ_XA_CONNECTION_FACTORY_CLASS ||
        factoryClassName == ACTIVEMQ_XA_SSL_CONNECTION_FACTORY_CLASS;
  }

  private String setPropertiesInURL(String brokerURL, String factoryClass,
                                    ActiveMQConnectionFactoryConfiguration factoryConfiguration)
      throws URISyntaxException {
    if (isSslFactoryClass(factoryClass)) {
      URI brokerURI = createURI(brokerURL);
      Map<String, String> map = (brokerURI.getQuery() != null) ? URISupport.parseQuery(brokerURI.getQuery()) : new HashMap<>();
      map.put(VERIFY_HOSTNAME, String.valueOf(factoryConfiguration.getVerifyHostName()));
      brokerURI = URISupport.createRemainingURI(brokerURI, map);
      return brokerURI.toString();
    }
    return brokerURL;
  }

  private boolean isSslFactoryClass(String factoryClass) {
    return factoryClass == ACTIVEMQ_XA_SSL_CONNECTION_FACTORY_CLASS
        || factoryClass == ACTIVEMQ_SSL_CONNECTION_FACTORY_CLASS;
  }

  private static URI createURI(String url) {
    try {
      return new URI(url);
    } catch (URISyntaxException e) {
      throw new RuntimeException("Invalid broker URI: " + url, e);
    }
  }

  private void setMaximumRedeliveries(Object redeliveryPolicy)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    Method setMaximumRedeliveriesMethod = redeliveryPolicy.getClass().getMethod("setMaximumRedeliveries", Integer.TYPE);
    int maxRedelivery = factoryConfiguration.getMaxRedelivery();
    setMaximumRedeliveriesMethod.invoke(redeliveryPolicy, maxRedelivery);
  }

  private void setInitialRedeliveryDelay(Object redeliveryPolicy)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    Method setInitialRedeliveryDelayMethod = redeliveryPolicy.getClass().getMethod("setInitialRedeliveryDelay", Long.TYPE);
    setInitialRedeliveryDelayMethod.invoke(redeliveryPolicy, factoryConfiguration.getInitialRedeliveryDelay());
  }

  private void setRedeliveryDelay(Object redeliveryPolicy)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    Method setRedeliveryDelayMethod = redeliveryPolicy.getClass().getMethod("setRedeliveryDelay", Long.TYPE);
    setRedeliveryDelayMethod.invoke(redeliveryPolicy, factoryConfiguration.getRedeliveryDelay());
  }

  private void setTrustedPackages(ConnectionFactory connectionFactory) throws InvocationTargetException, IllegalAccessException {
    try {
      List<String> trustedPackages = factoryConfiguration.getTrustedPackages();
      if (trustedPackages != null && !trustedPackages.isEmpty()) {
        Method setTrustedPackages = connectionFactory.getClass().getMethod("setTrustedPackages", List.class);
        setTrustedPackages.invoke(connectionFactory, trustedPackages);
      }
    } catch (NoSuchMethodException e) {
      LOGGER.warn("Trusted Packages were tried to be configured, but the current ActiveMQ Client version doesn't support it");
    }
  }

  private void setTrustAllPackages(ConnectionFactory connectionFactory) throws InvocationTargetException, IllegalAccessException {
    try {
      boolean trustAllPackages = factoryConfiguration.isTrustAllPackages();
      if (trustAllPackages) {
        Method setTrustedPackages = connectionFactory.getClass().getMethod("setTrustAllPackages", boolean.class);
        setTrustedPackages.invoke(connectionFactory, trustAllPackages);
      }
    } catch (NoSuchMethodException e) {
      LOGGER.warn("Trusted Packages were tried to be configured, but the current ActiveMQ Client version doesn't support it");
    }
  }

  private String getFactoryClass(boolean useSsl) {
    String factoryClass;
    if (factoryConfiguration.isEnableXA()) {
      if (useSsl) {
        factoryClass = ACTIVEMQ_XA_SSL_CONNECTION_FACTORY_CLASS;
      } else {
        factoryClass = ACTIVEMQ_XA_CONNECTION_FACTORY_CLASS;
      }
    } else {
      if (useSsl) {
        factoryClass = ACTIVEMQ_SSL_CONNECTION_FACTORY_CLASS;
      } else {
        factoryClass = ACTIVEMQ_CONNECTION_FACTORY_CLASS;
      }
    }
    return factoryClass;
  }

}
