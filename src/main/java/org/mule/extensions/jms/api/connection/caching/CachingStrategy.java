/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.connection.caching;

import javax.jms.Connection;
import javax.jms.Session;

/**
 * Defines the strategy to be used for caching of {@link Session}s and {@link Connection}s
 *
 * @since 1.0
 */
public interface CachingStrategy extends org.mule.jms.commons.api.connection.caching.CachingStrategy {

}
