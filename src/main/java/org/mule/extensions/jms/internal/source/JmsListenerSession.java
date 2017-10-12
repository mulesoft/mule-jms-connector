/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.source;

import org.mule.extensions.jms.internal.connection.session.JmsSession;

import javax.jms.JMSException;
import javax.jms.Session;

import java.util.Optional;

/**
 * {@link JmsSession} specialization for the {@link JmsListener}
 *
 * @since 1.0
 */
final class JmsListenerSession implements JmsSession {

  private JmsSession session;

  JmsListenerSession(JmsSession session) {
    this.session = session;
  }

  @Override
  public void close() throws JMSException {
    //The session must not be closed
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Session get() {
    return session.get();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<String> getAckId() {
    return session.getAckId();
  }
}
