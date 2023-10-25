/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.connection.factory.jndi;

import static java.util.Objects.hash;
import static java.lang.String.format;
import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.ExclusiveOptionals;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a {@link JndiNameResolver} or the set of properties required to
 * create one, represented by {@link JndiNameResolverProperties}
 *
 * @since 1.0
 */
@ExclusiveOptionals(isOneRequired = true)
public final class JndiNameResolverProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(JndiNameResolverProvider.class);

  /**
   * Reference to a custom {@link JndiNameResolver} implementation
   */
  @Parameter
  @Optional
  @Expression(NOT_SUPPORTED)
  private JndiNameResolver customJndiNameResolver;

  /**
   * Properties required to build a {@link SimpleJndiNameResolver}
   */
  @Parameter
  @Optional
  @Expression(NOT_SUPPORTED)
  private JndiNameResolverProperties nameResolverBuilder;


  public JndiNameResolver getCustomJndiNameResolver() {
    return customJndiNameResolver;
  }

  public void setCustomJndiNameResolver(JndiNameResolver customJndiNameResolver) {
    this.customJndiNameResolver= customJndiNameResolver;
  }

  public JndiNameResolverProperties getNameResolverBuilder() {
    return nameResolverBuilder;
  }

  public void setNameResolverBuilder(JndiNameResolverProperties nameResolverBuilder) {
    this.nameResolverBuilder= nameResolverBuilder;
  }

  JndiNameResolver createDefaultJndiResolver() throws InitialisationException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Creating default JndiNameResolver");
      LOGGER.debug(format("Provider Url: [%s], InitialContextFactory: [%s]",
                          nameResolverBuilder.getJndiProviderUrl(), nameResolverBuilder.getJndiInitialContextFactory()));
    }

    SimpleJndiNameResolver nameResolver = new SimpleJndiNameResolver();
    nameResolver.setJndiProviderUrl(nameResolverBuilder.getJndiProviderUrl());
    nameResolver.setJndiInitialFactory(nameResolverBuilder.getJndiInitialContextFactory());
    nameResolver.setJndiProviderProperties(nameResolverBuilder.getProviderProperties());

    return nameResolver;
  }

  @Override
  public int hashCode() {
    return hash(customJndiNameResolver, nameResolverBuilder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }

    if (!(other instanceof JndiNameResolverProvider)) {
      return false;
    }

    JndiNameResolverProvider otherJndiNameResolverProvider = (JndiNameResolverProvider) other;

    return new EqualsBuilder()
        .append(customJndiNameResolver, otherJndiNameResolverProvider.customJndiNameResolver)
        .append(nameResolverBuilder, otherJndiNameResolverProvider.nameResolverBuilder)
        .isEquals();
  }
}
