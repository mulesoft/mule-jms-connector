/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.connection.caching;

/**
 * Provides the configuration elements required to configure a {@link JmsCachingConnectionFactory}
 *
 * @since 1.0
 */
public interface CachingConfiguration {

  /**
   * Gets the size of the session cache
   */
  int getSessionCacheSize();

  /**
   * Indicates if producers are cached
   */
  boolean isProducersCache();

  /**
   * Indicates if consumers are cached
   */
  boolean isConsumersCache();
}
