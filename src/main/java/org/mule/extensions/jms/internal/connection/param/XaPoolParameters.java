/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.connection.param;

import static org.mule.runtime.extension.api.annotation.param.display.Placement.ADVANCED_TAB;

import org.mule.extensions.jms.internal.ExcludeFromGeneratedCoverage;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.sdk.api.annotation.semantics.connectivity.ExcludeFromConnectivitySchema;

import java.util.Objects;

/**
 * Allows parametrization of the Bitronix connection pool that is created when XA is enabled.
 *
 * @since 1.5.0
 */
public class XaPoolParameters implements org.mule.jms.commons.internal.connection.param.XaPoolParameters {

  /**
   * The minimum size of the XA connection pool. This value is only considered when XA connections are used.
   */
  @Parameter
  @Optional(defaultValue = "4")
  @Summary("The minimum size of the XA connection pool.")
  @Placement(tab = ADVANCED_TAB)
  @ExcludeFromConnectivitySchema
  private int minPoolSize = 4;

  /**
   * The maximum size of the XA connection pool. This value is only considered when XA connections are used.
   */
  @Parameter
  @Optional(defaultValue = "32")
  @Summary("The maximum size of the XA connection pool.")
  @Placement(tab = ADVANCED_TAB)
  @ExcludeFromConnectivitySchema
  private int maxPoolSize = 32;

  /**
   * How many seconds can an XA transaction remain idle before its eligible for eviction. This value is only considered when XA
   * connections are used.
   */
  @Parameter
  @Optional(defaultValue = "60")
  @Summary("How many seconds can an XA transaction remain idle before its eligible for eviction.")
  @Placement(tab = ADVANCED_TAB)
  @ExcludeFromConnectivitySchema
  private int maxIdleSeconds = 60;

  @Override
  public int getMinPoolSize() {
    return minPoolSize;
  }

  @Override
  public int getMaxPoolSize() {
    return maxPoolSize;
  }

  @Override
  public int getMaxIdleTime() {
    return maxIdleSeconds;
  }

  @Override
  @ExcludeFromGeneratedCoverage
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    XaPoolParameters that = (XaPoolParameters) o;
    return minPoolSize == that.minPoolSize &&
        maxPoolSize == that.maxPoolSize &&
        maxIdleSeconds == that.maxIdleSeconds;
  }

  @Override
  @ExcludeFromGeneratedCoverage
  public int hashCode() {
    return Objects.hash(minPoolSize, maxPoolSize, maxIdleSeconds);
  }
}
