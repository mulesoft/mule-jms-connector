/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.connection.factory.jndi;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import java.util.Map;

import javax.naming.InitialContext;

import static java.util.Objects.hash;

/**
 * Declares the properties required to create a {@link JndiNameResolver}
 *
 * @since 1.0
 */
public class JndiNameResolverProperties {

  /**
   * The fully qualified class name of the factory class that will create an {@link InitialContext}
   */
  @Parameter
  @Summary("The fully qualified class name of the factory class that will create an initial context")
  private String jndiInitialContextFactory;

  /**
   * The JNDI service provider URL
   */
  @Parameter
  @Optional
  @Summary("The JNDI service provider URL")
  private String jndiProviderUrl;

  /**
   * Properties to be passed on to the JNDI Name Resolver Context
   */
  @Parameter
  @Optional
  @Summary("Properties to be passed on to the JNDI Name Resolver Context")
  private Map<String, Object> providerProperties;

  public String getJndiInitialContextFactory() {
    return jndiInitialContextFactory;
  }

  public void setJndiInitialContextFactory(String jndiInitialContextFactory) {
    this.jndiInitialContextFactory = jndiInitialContextFactory;
  }

  public Map<String, Object> getProviderProperties() {
    return providerProperties;
  }

  public void setProviderProperties(Map<String, Object> providerProperties) {
    this.providerProperties = providerProperties;
  }

  public String getJndiProviderUrl() {
    return jndiProviderUrl;
  }

  public void setJndiProviderUrl(String jndiProviderUrl) {
    this.jndiProviderUrl = jndiProviderUrl;
  }

  @Override
  public int hashCode() {
    return hash(jndiProviderUrl, providerProperties, jndiInitialContextFactory);
  }

  @Override
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }

    if (this == other) {
      return true;
    }

    if (!(other instanceof JndiNameResolverProperties)) {
      return false;
    }

    JndiNameResolverProperties otherJndiNameResolverProps = (JndiNameResolverProperties) other;

    return new EqualsBuilder()
        .append(jndiInitialContextFactory, otherJndiNameResolverProps.jndiInitialContextFactory)
        .append(jndiProviderUrl, otherJndiNameResolverProps.jndiProviderUrl)
        .append(providerProperties, otherJndiNameResolverProps.providerProperties)
        .isEquals();
  }

}
