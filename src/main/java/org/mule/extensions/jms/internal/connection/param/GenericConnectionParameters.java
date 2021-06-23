/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.connection.param;

import org.mule.extensions.jms.internal.connection.provider.BaseConnectionProvider;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.sdk.api.annotation.semantics.security.ClientId;
import org.mule.sdk.api.annotation.semantics.security.Username;

import javax.jms.Connection;

/**
 * Common connection parameters for the {@link BaseConnectionProvider}
 *
 * @since 1.0
 */
public class GenericConnectionParameters implements org.mule.jms.commons.internal.connection.param.GenericConnectionParameters {

  /**
   * Username to be used when providing credentials for authentication.
   */
  @Parameter
  @Optional
  @Username
  private String username;

  /**
   * Password to be used when providing credentials for authentication.
   */
  @Parameter
  @Optional
  @Password
  private String password;

  /**
   *  Client identifier to be assigned to the {@link Connection} upon creation.
   *  The purpose of client identifier is to associate a connection and its objects
   *  with a state maintained on behalf of the client by a provider. By definition,
   *  the client state identified by a client identifier can be "in use" by only one
   *  client at a time.
   *  <p>
   *  The only use of a client identifier defined by JMS is its mandatory use in
   *  identifying an unshared durable subscription or its optional use in identifying
   *  a shared durable or non-durable subscription.
   */
  @Parameter
  @Optional
  @ClientId
  private String clientId;


  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getClientId() {
    return clientId;
  }

}
