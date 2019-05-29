/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.connection.param;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class XaPoolParameters implements org.mule.jms.commons.internal.connection.param.XaPoolParameters {

  @Parameter
  @Optional(defaultValue = "4")
  private int minPoolSize = 4;

  @Parameter
  @Optional(defaultValue = "32")
  private int maxPoolSize = 32;

  @Parameter
  @Optional(defaultValue = "60")
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
    return 60;
  }
}
