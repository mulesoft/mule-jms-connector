/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.connection.provider;

import static java.lang.String.format;
import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.disposeIfNeeded;
import static org.mule.runtime.extension.api.annotation.param.ParameterGroup.CONNECTION;
import static org.slf4j.LoggerFactory.getLogger;

import org.mule.extensions.jms.api.connection.JmsSpecification;
import org.mule.extensions.jms.api.connection.caching.CachingStrategy;
import org.mule.extensions.jms.api.connection.caching.DefaultCachingStrategy;
import org.mule.extensions.jms.internal.connection.param.GenericConnectionParameters;
import org.mule.extensions.jms.internal.connection.param.XaPoolParameters;
import org.mule.extensions.jms.internal.connection.session.JmsSessionManager;
import org.mule.jms.commons.internal.connection.JmsConnection;
import org.mule.jms.commons.internal.connection.JmsTransactionalConnection;
import org.mule.jms.commons.internal.connection.provider.ConnectionFactoryDecoratorFactory;
import org.mule.jms.commons.internal.connection.provider.JmsConnectionProvider;
import org.mule.jms.commons.internal.support.JmsSupportFactory;
import org.mule.runtime.api.artifact.Registry;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.RefName;

import java.util.function.Supplier;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Session;

import org.slf4j.Logger;

/**
 * Base implementation of a {@link PoolingConnectionProvider} for {@link JmsConnection}s
 *
 * @since 1.0
 */
public abstract class BaseConnectionProvider
    implements CachedConnectionProvider<JmsTransactionalConnection>, Initialisable, Disposable {

  private static final Logger LOGGER = getLogger(BaseConnectionProvider.class);

  /**
   * Versions of the {@link JmsSpecification} to be used by the extension.
   * This version should be compatible with the implementation of the {@link ConnectionFactory}
   * configured. Functionality available only for certain versions of the spec
   * will throw an error if the version requirement is not met.
   */
  @Parameter
  @Optional(defaultValue = "JMS_1_1")
  @Expression(NOT_SUPPORTED)
  private JmsSpecification specification;

  @ParameterGroup(name = CONNECTION)
  private GenericConnectionParameters connectionParameters;

  @ParameterGroup(name = "XA Connection Pool", showInDsl = true)
  private XaPoolParameters xaPoolParameters;

  /**
   * the strategy to be used for caching of {@link Session}s and {@link Connection}s
   */
  @Parameter
  @Optional
  @NullSafe(defaultImplementingType = DefaultCachingStrategy.class)
  @ParameterDsl(allowReferences = false)
  @Expression(value = NOT_SUPPORTED)
  private CachingStrategy cachingStrategy;

  @Inject
  JmsSessionManager jmsSessionManager;

  @Inject
  private MuleContext muleContext;

  @Inject
  private Registry registry;

  @RefName
  String configName;

  /**
   * Used to ignore handling of ExceptionListener#onException when in the process of disconnecting
   */
  private JmsConnectionProvider jmsConnectionProvider;

  /**
   * Template method for obtaining the {@link ConnectionFactory} to be used for creating the {@link JmsConnection}s
   * @return an instance of {@link ConnectionFactory} to be used for creating the {@link JmsConnection}s
   * @throws Exception if an error occurs while creting the {@link ConnectionFactory}
   */
  public abstract ConnectionFactory getConnectionFactory() throws Exception;

  @Override
  public void initialise() throws InitialisationException {
    jmsConnectionProvider =
        new JmsConnectionProvider(jmsSessionManager,
                                  getConnectionFactorySupplier(),
                                  specification.getJmsSpecification(),
                                  connectionParameters,
                                  xaPoolParameters,
                                  cachingStrategy,
                                  enableXa(),
                                  getJmsSupportFactory(),
                                  new ConnectionFactoryDecoratorFactory(muleContext, registry),
                                  configName);
  }

  // TODO (EE-6615): JmsSupportyFactory is not part of jms-client API.
  protected JmsSupportFactory getJmsSupportFactory() {
    return JmsSupportFactory.DEFAULT;
  }

  protected abstract boolean enableXa();

  protected abstract Supplier<ConnectionFactory> getConnectionFactorySupplier();

  @Override
  public JmsTransactionalConnection connect() throws ConnectionException {
    return jmsConnectionProvider.connect();
  }

  @Override
  public ConnectionValidationResult validate(JmsTransactionalConnection jmsConnection) {
    return jmsConnectionProvider.validate(jmsConnection);
  }

  @Override
  public void disconnect(JmsTransactionalConnection jmsConnection) {
    jmsConnectionProvider.disconnect(jmsConnection);
  }

  protected void doClose(JmsConnection jmsConnection) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(format("Perform doClose: [%s]", getClass().getName()));
    }
    disposeIfNeeded(jmsConnection, LOGGER);
  }

  @Override
  public void dispose() {
    jmsConnectionProvider.dispose();
  }

  public JmsSpecification getSpecification() {
    return specification;
  }

}
