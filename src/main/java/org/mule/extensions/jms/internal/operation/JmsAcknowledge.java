/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.operation;

import static org.slf4j.LoggerFactory.getLogger;

import org.mule.extensions.jms.api.exception.JmsAckErrorTypeProvider;
import org.mule.extensions.jms.api.exception.JmsAckException;
import org.mule.extensions.jms.api.exception.JmsSessionRecoverErrorTypeProvider;
import org.mule.extensions.jms.internal.connection.session.JmsSession;
import org.mule.extensions.jms.internal.connection.session.JmsSessionManager;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;

import javax.inject.Inject;
import javax.jms.Message;

import org.slf4j.Logger;


/**
 * Operation that allows the user to perform an ACK over a {@link Message} produced by the current {@link JmsSession}
 *
 * @since 1.0
 */
public final class JmsAcknowledge implements Initialisable {

  private static final Logger LOGGER = getLogger(JmsAcknowledge.class);

  @Inject
  private JmsSessionManager sessionManager;
  private org.mule.jms.commons.internal.operation.JmsAcknowledge jmsAck;

  /**
   * Allows the user to perform an ACK when the {@link AckMode#MANUAL} mode is elected while consuming the {@link Message}.
   * As per JMS Spec, performing an ACK over a single {@link Message} automatically works as an ACK for all the {@link Message}s
   * produced in the same {@link JmsSession}.
   *
   * @param ackId The AckId of the Message to ACK
   * @throws JmsAckException if the {@link JmsSession} or {@link JmsConnection} were closed, or if the ID doesn't belong
   * to a session of the current connection
   */
  @Throws(JmsAckErrorTypeProvider.class)
  public void ack(@Summary("The AckId of the Message to ACK") String ackId, CompletionCallback<Void, Void> completionCallback) {
    jmsAck.ack(ackId, completionCallback);
  }

  /**
   * Allows the user to perform a session recover when the {@link AckMode#MANUAL} mode is elected while consuming the
   * {@link Message}.
   * As per JMS Spec, performing a session recover automatically will redeliver all the consumed messages that had not being
   * acknowledged before this recover.
   *
   * @param ackId The AckId of the Message Session to recover
   */
  @Throws(JmsSessionRecoverErrorTypeProvider.class)
  public void recoverSession(String ackId, CompletionCallback<Void, Void> completionCallback) {
    jmsAck.recoverSession(ackId, completionCallback);
  }

  @Override
  public void initialise() throws InitialisationException {
    this.jmsAck = new org.mule.jms.commons.internal.operation.JmsAcknowledge(sessionManager);
  }
}
