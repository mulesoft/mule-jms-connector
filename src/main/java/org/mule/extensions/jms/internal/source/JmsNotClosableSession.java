/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.source;

import org.mule.extensions.jms.internal.connection.session.JmsSession;

import javax.jms.JMSException;

/**
 * {@link JmsSession} wrapper which ignores the {@link this#close()} action
 *
 * @since 1.0
 */
public final class JmsNotClosableSession extends JmsSession {

  JmsNotClosableSession(JmsSession session) {
    super(session.get(), session.getAckId().orElse(null));
  }

  @Override
  public void close() throws JMSException {
    //The session must not be closed
  }
}
