/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.connection.session;

import org.mule.extensions.jms.internal.config.InternalAckMode;

import javax.jms.Session;

/**
 * Manager that takes the responsibility of register the session information to be able to execute a manual
 * acknowledgement or a recover over a {@link Session}.
 * This is used when the {@link InternalAckMode} is configured in {@link InternalAckMode#MANUAL}
 *
 * @since 1.0
 */
final public class JmsSessionManager extends org.mule.jms.commons.internal.connection.session.JmsSessionManager {

}
