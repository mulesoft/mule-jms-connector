/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.message;

import org.mule.extensions.jms.api.message.JmsxProperties;

import java.util.Map;

import javax.jms.Message;

/**
 * Builder that provides a simple way of creating a {@link JmsxProperties} instance based on
 * the predefined properties {@link JMSXDefinedPropertiesNames names}.
 * <p>
 * This is useful for converting the properties {@link Map} found in the original {@link Message}
 * to their representation as {@link JmsxProperties}.
 * A default value is provided for the properties that are not set.
 *
 * @since 1.0
 */
public final class JmsxPropertiesBuilder extends org.mule.jms.commons.api.message.JmsxProperties {
}
