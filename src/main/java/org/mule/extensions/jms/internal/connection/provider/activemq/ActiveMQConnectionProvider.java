/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.connection.provider.activemq;

import static org.mule.extensions.jms.api.connection.JmsSpecification.JMS_2_0;
import static org.mule.extensions.jms.internal.connection.provider.activemq.ActiveMQConnectionProvider.ACTIVEMQ_VERSION;
import static org.mule.extensions.jms.internal.connection.provider.activemq.ActiveMQConnectionProvider.BROKER_CLASS;
import static org.mule.extensions.jms.internal.connection.provider.activemq.ActiveMQConnectionProvider.BROKER_GA;
import static org.mule.extensions.jms.internal.connection.provider.activemq.ActiveMQConnectionProvider.CONNECTION_FACTORY_CLASS;
import static org.mule.extensions.jms.internal.connection.provider.activemq.ActiveMQConnectionProvider.KAHA_DB_STORE_CLASS;
import static org.mule.runtime.api.meta.ExternalLibraryType.DEPENDENCY;
import static org.slf4j.LoggerFactory.getLogger;

import org.mule.extensions.jms.api.exception.JmsMissingLibraryException;
import org.mule.extensions.jms.internal.connection.JmsConnection;
import org.mule.extensions.jms.internal.connection.JmsTransactionalConnection;
import org.mule.extensions.jms.internal.connection.exception.ActiveMQException;
import org.mule.extensions.jms.internal.connection.provider.BaseConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.core.api.util.proxy.TargetInvocationHandler;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.ExternalLib;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;

/**
 * A {@link ConnectionProvider} that contains custom logic to handle ActiveMQ connections in particular
 *
 * @since 1.0
 */
@Alias("active-mq")
@ExternalLib(name = "ActiveMQ Client", description = "The ActiveMQ JMS Client implementation.", type = DEPENDENCY,
    requiredClassName = CONNECTION_FACTORY_CLASS, coordinates = "org.apache.activemq:activemq-client:" + ACTIVEMQ_VERSION)
@ExternalLib(name = "ActiveMQ Broker",
    description = "The ActiveMQ Message Broker implementation. Only required when using an in-memory broker based on the VM transport, like the one configured by default.",
    type = DEPENDENCY, requiredClassName = BROKER_CLASS, coordinates = BROKER_GA + ":" + ACTIVEMQ_VERSION,
    optional = true)
@ExternalLib(name = "ActiveMQ KahaDB",
    description = "The ActiveMQ KahaDB Store Implementation. Only required if using a persistent in-memory broker. For example: 'vm://localhost?broker.persistent=true'",
    type = DEPENDENCY, requiredClassName = KAHA_DB_STORE_CLASS,
    coordinates = ActiveMQConnectionProvider.KAHA_DB_GA + ":" + ACTIVEMQ_VERSION,
    optional = true)
public class ActiveMQConnectionProvider extends BaseConnectionProvider {

  private static final Logger LOGGER = getLogger(ActiveMQConnectionProvider.class);
  static final String CONNECTION_FACTORY_CLASS = "org.apache.activemq.ActiveMQConnectionFactory";
  static final String BROKER_CLASS = "org.apache.activemq.broker.Broker";
  static final String KAHA_DB_STORE_CLASS = "org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter";
  static final String ACTIVEMQ_VERSION = "5.14.5";
  static final String BROKER_GA = "org.apache.activemq:activemq-broker";
  static final String KAHA_DB_GA = "org.apache.activemq:activemq-kahadb-store";

  /**
   * a provider for an {@link ActiveMQConnectionFactory}
   */
  @ParameterGroup(name = "Connection Factory")
  @Placement(order = 1)
  private ActiveMQConnectionFactoryProvider connectionFactoryProvider;

  private ConnectionFactory connectionFactory;

  @Override
  public JmsTransactionalConnection connect() throws ConnectionException {
    try {
      return super.connect();
    } catch (ConnectionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof JMSException) {
        checkMissingBrokerLib(e);
        checkMissingPersistenceLib(e);
      }
      throw e;
    }
  }

  @Override
  public ConnectionFactory getConnectionFactory() throws ActiveMQException {
    if (connectionFactory != null) {
      return connectionFactory;
    }

    createConnectionFactory();
    return connectionFactory;
  }

  private void createConnectionFactory() throws ActiveMQException {
    connectionFactory = connectionFactoryProvider.getConnectionFactory();
    if (connectionFactory == null) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("No custom connection factory provided, creating the default for ActiveMq");
      }
      if (JMS_2_0.equals(getSpecification())) {

        //TODO we could support a JMS 2.0 default using ActiveMQ Artemis (HornetQ) instead of ActiveMQ 5.x
        throw new ActiveMQException(
                                    "No ConnectionFactory was provided, but JMS 2.0 specification was selected."
                                        + " Default ActiveMQConnectionFactory implementation provides support only for JMS 1.1 and 1.0.2b versions");
      }

      connectionFactory = connectionFactoryProvider.createDefaultConnectionFactory();
    }
  }

  @Override
  protected void doClose(JmsConnection jmsConnection) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Performing custom doClose for ActiveMQ");
    }

    Connection connection = jmsConnection.get();

    try {
      executeCleanup(connection);
    } catch (Exception e) {
      LOGGER.warn("Exception cleaning up ActiveMQ JMS connection: ", e);
    } finally {
      super.doClose(jmsConnection);
    }
  }

  private void executeCleanup(Connection connection) throws Exception {
    Method cleanupMethod = null;

    try {
      final Class clazz = connection.getClass();
      if (Proxy.isProxyClass(clazz)) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(connection);

        // When using caching-connection-factory, the connections are proxy objects that do nothing on the
        // close and stop methods so that they remain open when returning to the cache. In that case, we don't
        // need to do any custom cleanup, as the connections will be closed when destroying the cache. The
        // type of the invocation handler for these connections is SharedConnectionInvocationHandler.

        if (invocationHandler instanceof TargetInvocationHandler) {
          // this is really an XA connection, bypass the java.lang.reflect.Proxy as it
          // can't delegate to non-interfaced methods (like proprietary 'cleanup' one)
          TargetInvocationHandler targetInvocationHandler = (TargetInvocationHandler) invocationHandler;
          connection = (Connection) targetInvocationHandler.getTargetObject();
          Class realConnectionClass = connection.getClass();
          cleanupMethod = realConnectionClass.getMethod("cleanup", (Class[]) null);
        } else {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("InvocationHandler of the JMS connection proxy is of type %s, not doing " +
                "any extra cleanup", invocationHandler.getClass().getName()));
          }
        }
      } else {
        cleanupMethod = clazz.getMethod("cleanup", (Class[]) null);
      }
    } catch (NoSuchMethodException e) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Failed to perform a deep cleanup on ActiveMQ connection: ", e);
      }
    }

    if (cleanupMethod != null) {
      cleanupMethod.invoke(connection, (Object[]) null);
    }
  }

  private String getAdviceMessage(String library) {
    return "Validate that the Mule Application has the required library: \"" + library
        + "\" as a Shared Library. Connecting to broker: [" + connectionFactoryProvider.getFactoryConfiguration().getBrokerUrl()
        + "]";
  }

  void checkMissingPersistenceLib(ConnectionException e) {
    boolean unableToCreatePersistenceAdapter =
        e.getMessage().contains("Class 'org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter' not found in classloader");
    if (unableToCreatePersistenceAdapter) {
      throw new JmsMissingLibraryException(new ConnectionException(e),
                                           "Unable to create a local in-memory broker with persistence mode enabled. "
                                               + getAdviceMessage(KAHA_DB_GA));
    }
  }

  void checkMissingBrokerLib(ConnectionException e) {
    boolean unableToCreateInVMTransport = e.getMessage().contains("Transport scheme NOT recognized: [vm]");
    if (unableToCreateInVMTransport) {
      throw new JmsMissingLibraryException(new ConnectionException(e),
                                           "Unable to create a connection to a broker based on the VM Transport. "
                                               + getAdviceMessage(BROKER_GA));
    }
  }

  public ActiveMQConnectionFactoryProvider getConnectionFactoryProvider() {
    return connectionFactoryProvider;
  }
}
