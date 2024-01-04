/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.connection.caching;

import org.mule.extensions.jms.internal.ExcludeFromGeneratedCoverage;
import org.mule.runtime.extension.api.annotation.Alias;

/**
 * Implementation of {@link CachingConfiguration} that <b>disables</b> session caching This {@link CachingConfiguration} is the
 * recommended only if an external {@code CachingConnectionFactory} is already being parameterized or if this extension is being
 * used in the context of a Java EE web or EJB application.
 *
 * @since 1.0
 */
@Alias("no-caching")
public class NoCachingConfiguration extends org.mule.jms.commons.api.connection.caching.NoCachingConfiguration
    implements CachingStrategy {

  @ExcludeFromGeneratedCoverage
  @Override
  public int hashCode() {
    return NoCachingConfiguration.class.hashCode();
  }

  @ExcludeFromGeneratedCoverage
  @Override
  public boolean equals(Object obj) {
    return obj != null && obj.getClass() == NoCachingConfiguration.class;
  }
}
