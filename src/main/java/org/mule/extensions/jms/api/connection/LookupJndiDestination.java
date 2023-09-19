/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.connection;

import org.mule.extensions.jms.api.exception.JmsExtensionException;

/**
 * Defines the behaviours that are supported when looking for a destination based
 * on its name while using a JNDI name resolver.
 *
 * NEVER: Will never lookup for jndi destinations.
 * ALWAYS: Will always lookup the destinations through JNDI. It will fail if the destination does not exists.
 * TRY_ALWAYS: Will always try to lookup the destinations through JNDI but if it does not exists it will create a new one.
 *
 * @since 1.0
 */
public enum LookupJndiDestination {
  /**
   * Will never lookup for jndi destinations
   */
  NEVER,

  /**
   * Will always lookup the destinations through JNDI. It will fail if the destination does not exists.
   */
  ALWAYS,

  /**
   * Will always try to lookup the destinations through JNDI but if it does not exists it will create a new one.
   */
  TRY_ALWAYS;

  public org.mule.jms.commons.api.connection.LookupJndiDestination getJmsClientLookupJndiDestination() {
    switch (this) {
      case NEVER:
        return org.mule.jms.commons.api.connection.LookupJndiDestination.NEVER;
      case ALWAYS:
        return org.mule.jms.commons.api.connection.LookupJndiDestination.ALWAYS;
      case TRY_ALWAYS:
        return org.mule.jms.commons.api.connection.LookupJndiDestination.TRY_ALWAYS;
      default:
        throw new JmsExtensionException("Missing lookup configuration.");
    }
  }
}
