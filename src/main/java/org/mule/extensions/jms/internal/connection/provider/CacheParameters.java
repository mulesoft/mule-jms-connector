/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.connection.provider;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import org.mule.extensions.jms.api.connection.caching.CachingStrategy;
import org.mule.extensions.jms.api.connection.caching.DefaultCachingStrategy;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import jakarta.jms.Connection;
import jakarta.jms.Session;

/**
 * Container group for connection factory cache parameters
 *
 * @since 1.0
 */
public class CacheParameters {

  /**
   * the strategy to be used for caching of {@link Session}s and {@link Connection}s
   */
  @Parameter
  @Optional
  @Expression(NOT_SUPPORTED)
  @NullSafe(defaultImplementingType = DefaultCachingStrategy.class)
  private CachingStrategy cachingStrategy;

  public CachingStrategy getCachingStrategy() {
    return cachingStrategy;
  }

}
