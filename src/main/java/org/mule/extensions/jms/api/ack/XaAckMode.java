/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.ack;

import org.mule.jms.commons.internal.config.InternalAckMode;

import javax.jms.Session;

import static org.mule.runtime.extension.api.values.ValueBuilder.newValue;

import java.util.Set;

public enum XaAckMode {

  SESSION_TRANSACTED(Session.SESSION_TRANSACTED), AUTO_ACKNOWLEDGE(Session.AUTO_ACKNOWLEDGE), CLIENT_ACKNOWLEDGE(
      Session.CLIENT_ACKNOWLEDGE), DUPS_OK_ACKNOWLEDGE(Session.DUPS_OK_ACKNOWLEDGE);

  private int ackMode;

  XaAckMode(int ackMode) {
    this.ackMode = ackMode;
  }

  public int getAckMode() {
    return ackMode;
  }

  public void setAckMode(int ackMode) {
    this.ackMode = ackMode;
  }
}
