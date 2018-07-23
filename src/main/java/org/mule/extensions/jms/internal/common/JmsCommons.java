/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.common;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

/**
 * Utility class to reuse logic for JMS Extension
 *
 * @since 1.0
 */
public final class JmsCommons {

  private static final Logger LOGGER = getLogger(JmsCommons.class);

  public static final String TOPIC = "TOPIC";
  public static final String QUEUE = "QUEUE";
  public static final String EXAMPLE_ENCODING = "UTF-8";
  public static final String EXAMPLE_CONTENT_TYPE = "application/json";

}
