/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.ack;

import org.mule.jms.commons.internal.config.InternalAckMode;
import org.mule.jms.commons.internal.config.JmsAckMode;
import org.mule.jms.commons.internal.operation.JmsConsume;
import org.mule.jms.commons.internal.source.JmsListener;

import javax.jms.Session;

/**
 * Declares the kind of Acknowledgement mode supported.
 * <ul>
 *     <li><b>AUTO</b>: Mule ACKs the message only if the flow is finished successfully. </li>
 *     <li><b>MANUAL</b>: This is JMS {@link Session#CLIENT_ACKNOWLEDGE} mode. The user must do the ack manually within the flow. </li>
 *     <li><b>DUPS_OK</b>: JMS message is acked automatically but in a lazy fashion which may lead to duplicates. </li>
 *     <li><b>IMMEDIATE</b>: Mule automatically ACKs the message upon reception. </li>
 * </ul>
 *
 *  @since 1.0
 */
public enum AckMode implements JmsAckMode {

  /**
   * Mule automatically ACKs the message upon reception
   */
  IMMEDIATE(InternalAckMode.IMMEDIATE),

  /**
   * This is JMS {@link Session#AUTO_ACKNOWLEDGE} mode.
   * The session automatically acknowledges the receipt when it successfully delivered the message
   * to a {@link JmsConsume#consume} or {@link JmsListener} handler.
   */
  AUTO(InternalAckMode.AUTO),

  /**
   * This is JMS {@link Session#CLIENT_ACKNOWLEDGE} mode. The user must do the ACK manually within the flow
   */
  MANUAL(InternalAckMode.MANUAL),

  /**
   * Similar to AUTO, the JMS message is acknowledged automatically but in a lazy fashion which may lead to duplicates.
   */
  DUPS_OK(InternalAckMode.DUPS_OK);

  private InternalAckMode ackMode;

  AckMode(InternalAckMode ackMode) {
    this.ackMode = ackMode;
  }

  @Override
  public InternalAckMode getInternalAckMode() {
    return ackMode;
  }
}
