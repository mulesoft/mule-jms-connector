/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.connection.session;

import static java.util.Optional.ofNullable;
import static org.mule.runtime.api.util.Preconditions.checkArgument;
import static org.slf4j.LoggerFactory.getLogger;

import org.mule.extensions.jms.internal.source.JmsListener;

import javax.jms.JMSException;
import javax.jms.Session;

import java.util.Optional;

import org.slf4j.Logger;

/**
 * Wrapper element for a JMS {@link Session} that relates the
 * session with its AckID
 *
 * @since 1.0
 */
public final class JmsSession implements AutoCloseable {

  private static final Logger LOGGER = getLogger(JmsSession.class);

  private final Session session;
  private final boolean closeImmediately;
  private String ackId;

  public JmsSession(Session session, boolean closeImmediately) {
    checkArgument(session != null, "A non null Session is required to use as delegate");
    this.closeImmediately = closeImmediately;
    this.session = session;
  }

  public JmsSession(Session session, String ackId, boolean closeImmediately) {
    checkArgument(session != null, "A non null Session is required to use as delegate");
    checkArgument(ackId != null, "The ackId cant be null");
    this.session = session;
    this.ackId = ackId;
    this.closeImmediately = closeImmediately;
  }

  /**
   * @return the JMS {@link Session}
   */
  public Session get() {
    return session;
  }

  /**
   * @return the AckId of this {@link Session} or {@link Optional#empty} if no AckId is required
   */
  public Optional<String> getAckId() {
    return ofNullable(ackId);
  }

  @Override
  public void close() throws JMSException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Closing session " + session);
    }
    session.close();
  }

  /**
   * Indicates if the Session must be closed immediately. This is required to distinguish in transactional scenarios
   * where a TX Session created by a {@link JmsListener} must remain open, but in a publish or consume scenario
   * this must be closed.
   *
   * @return a boolean indicating if the session must be closed immediately after the publish or consume of a message
   */
  public boolean isCloseImmediately() {
    return closeImmediately;
  }
}
