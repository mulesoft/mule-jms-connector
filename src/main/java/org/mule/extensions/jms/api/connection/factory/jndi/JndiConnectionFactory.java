/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.connection.factory.jndi;


import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Objects.hash;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.disposeIfNeeded;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.initialiseIfNeeded;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.startIfNeeded;
import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.stopIfNeeded;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.mule.extensions.jms.api.connection.LookupJndiDestination;
import org.mule.extensions.jms.api.exception.JmsExtensionException;
import org.mule.extensions.jms.internal.ExcludeFromGeneratedCoverage;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.lifecycle.Lifecycle;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.connection.DelegatingConnectionFactory;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSContext;
import javax.naming.NamingException;

/**
 * A {@link ConnectionFactory} that wraps a {@link ConnectionFactory delegate} that is discovered using a {@link JndiNameResolver}
 *
 * @since 1.0
 */
public class JndiConnectionFactory extends DelegatingConnectionFactory implements Lifecycle {

  private static final Logger LOGGER = LoggerFactory.getLogger(JndiConnectionFactory.class);

  /**
   * Name of the ConnectionFactory to be discovered using Jndi and used as a delegate of {@code this} {@link ConnectionFactory}
   */
  @Parameter
  private String connectionFactoryJndiName;

  /**
   * The {@link LookupJndiDestination} policy to use when creating {@link Destination}s
   */
  @Parameter
  @Optional(defaultValue = "NEVER")
  private LookupJndiDestination lookupDestination;

  /**
   * Provider for the {@link JndiNameResolver}
   */
  @ParameterGroup(name = "Jndi Name Resolver")
  private JndiNameResolverProvider nameResolverProvider;


  private JndiNameResolver nameResolver;
  private ConnectionFactory connectionFactory;


  public java.util.Optional<Destination> getJndiDestination(String name) {

    try {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(format("Looking up %s from JNDI", name));
      }

      Object temp = lookupFromJndi(name);

      return temp instanceof Destination ? of((Destination) temp) : empty();

    } catch (NamingException e) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(format("Failed to look up destination [%s]: ", name), e);
      }

      return empty();
    }
  }

  @Override
  public ConnectionFactory getTargetConnectionFactory() {
    if (connectionFactory != null) {
      return connectionFactory;
    }

    try {
      Object temp = getJndiNameResolver().lookup(connectionFactoryJndiName);
      if (temp instanceof ConnectionFactory) {
        return connectionFactory = (ConnectionFactory) temp;
      }
    } catch (Exception e) {
      throw new JmsExtensionException(e.getMessage(), e);
    }
    throw new JmsExtensionException("No valid ConnectionFactory was provided.");
  }

  @Override
  public void initialise() throws InitialisationException {
    setupNameResolver();
  }

  @Override
  public void start() throws MuleException {
    startIfNeeded(getJndiNameResolver());
  }

  @Override
  public void stop() throws MuleException {
    stopIfNeeded(getJndiNameResolver());
  }

  @Override
  public void dispose() {
    disposeIfNeeded(getJndiNameResolver(), LOGGER);
  }

  private void setupNameResolver() throws InitialisationException {
    JndiNameResolver customJndiNameResolver = nameResolverProvider.getCustomJndiNameResolver();
    if (customJndiNameResolver != null) {
      nameResolver = customJndiNameResolver;
    } else {
      nameResolver = nameResolverProvider.createDefaultJndiResolver();
    }

    initialiseIfNeeded(nameResolver);
  }

  private Object lookupFromJndi(String jndiName) throws NamingException {
    try {
      return getJndiNameResolver().lookup(jndiName);
    } catch (NamingException e) {
      // TODO MULE-10959: mark transaction for rollback
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(format("Failed to resolve lookup for name [%s]", jndiName), e);
      }
      throw e;
    }
  }

  @ExcludeFromGeneratedCoverage
  private JndiNameResolver getJndiNameResolver() {
    return nameResolver;
  }

  @Override
  public JMSContext createContext() {
    // We'll use the classic API
    return null;
  }

  @Override
  public JMSContext createContext(String userName, String password) {
    // We'll use the classic API
    return null;
  }

  @Override
  public JMSContext createContext(String userName, String password, int sessionMode) {
    // We'll use the classic API
    return null;
  }

  @Override
  public JMSContext createContext(int sessionMode) {
    // We'll use the classic API
    return null;
  }

  @ExcludeFromGeneratedCoverage
  public String getConnectionFactoryJndiName() {
    return connectionFactoryJndiName;
  }

  @ExcludeFromGeneratedCoverage
  public void setConnectionFactoryJndiName(String connectionFactoryJndiName) {
    this.connectionFactoryJndiName = connectionFactoryJndiName;
  }

  @ExcludeFromGeneratedCoverage
  public LookupJndiDestination getLookupDestination() {
    return lookupDestination;
  }

  @ExcludeFromGeneratedCoverage
  public void setLookupDestination(LookupJndiDestination lookupDestination) {
    this.lookupDestination = lookupDestination;
  }

  @ExcludeFromGeneratedCoverage
  public JndiNameResolverProvider getNameResolverProvider() {
    return nameResolverProvider;
  }

  @ExcludeFromGeneratedCoverage
  public void setNameResolverProvider(JndiNameResolverProvider nameResolverProvider) {
    this.nameResolverProvider = nameResolverProvider;
  }

  @ExcludeFromGeneratedCoverage
  public JndiNameResolver getNameResolver() {
    return nameResolver;
  }

  @ExcludeFromGeneratedCoverage
  public void setNameResolver(JndiNameResolver nameResolver) {
    this.nameResolver = nameResolver;
  }

  public ConnectionFactory getConnectionFactory() {
    return connectionFactory;
  }

  @ExcludeFromGeneratedCoverage
  public void setConnectionFactory(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  @Override
  public int hashCode() {
    return hash(connectionFactoryJndiName, lookupDestination, nameResolverProvider, nameResolver, connectionFactory);
  }

  @Override
  @ExcludeFromGeneratedCoverage
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }

    if (this == other) {
      return true;
    }

    if (!(other instanceof JndiConnectionFactory)) {
      return false;
    }

    JndiConnectionFactory otherFactory = (JndiConnectionFactory) other;

    EqualsBuilder equalsBuilder = new EqualsBuilder().append(connectionFactoryJndiName, otherFactory.connectionFactoryJndiName)
        .append(lookupDestination, otherFactory.lookupDestination);

    if (nameResolver != null) {
      equalsBuilder.append(nameResolver, otherFactory.nameResolver);

      if (connectionFactory != null) {
        equalsBuilder.append(connectionFactory, otherFactory.connectionFactory);
      }
    } else {
      equalsBuilder.append(nameResolverProvider, otherFactory.nameResolverProvider);
    }

    return equalsBuilder.isEquals();
  }

}
