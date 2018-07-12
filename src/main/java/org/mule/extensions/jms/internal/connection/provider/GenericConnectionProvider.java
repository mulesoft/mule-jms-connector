/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.connection.provider;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.api.meta.ExternalLibraryType.DEPENDENCY;

import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.ExternalLib;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import java.util.function.Supplier;

import javax.jms.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic implementation of a JMS {@link ConnectionProvider}.
 * This provider uses any {@link ConnectionFactory} that the user configures in order to create a {@link JmsConnection}.
 *
 * @since 1.0
 */
@DisplayName("Generic Connection")
@Alias("generic")
@ExternalLib(name = "JMS Client", description = "Client which lets communicate with a JMS broker", type = DEPENDENCY)
public class GenericConnectionProvider extends BaseConnectionProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseConnectionProvider.class);

  /**
   * a JMS {@link ConnectionFactory} implementation
   */
  @Parameter
  @Expression(NOT_SUPPORTED)
  private ConnectionFactory connectionFactory;


  @Override
  public ConnectionFactory getConnectionFactory() {
    return connectionFactory;
  }

  @Override
  protected boolean enableXa() {
    return false;
  }

  @Override
  protected Supplier<ConnectionFactory> getConnectionFactorySupplier() {
    return this::getConnectionFactory;
  }
}
