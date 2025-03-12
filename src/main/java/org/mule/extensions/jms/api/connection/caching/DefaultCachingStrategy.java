/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.connection.caching;

import org.mule.extensions.jms.internal.ExcludeFromGeneratedCoverage;
import org.mule.runtime.extension.api.annotation.Alias;

/**
 * Default implementation of {@link CachingConfiguration} that not only enables caching but also provides default values for all
 * the configurable parameters
 *
 * @since 1.0
 */
@Alias("default-caching")
public class DefaultCachingStrategy extends org.mule.jms.commons.api.connection.caching.DefaultCachingStrategy
    implements CachingStrategy {

  @ExcludeFromGeneratedCoverage
  @Override
  public int hashCode() {
    return DefaultCachingStrategy.class.hashCode();
  }

  @ExcludeFromGeneratedCoverage
  @Override
  public boolean equals(Object obj) {
    return obj != null && obj.getClass() == DefaultCachingStrategy.class;
  }
}
