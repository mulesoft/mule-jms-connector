/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.ack;

import org.mule.extensions.jms.api.config.AckMode;
import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.extensions.jms.test.JmsMessageStorage;
import org.mule.tck.junit4.rule.SystemProperty;

import org.junit.After;
import org.junit.Rule;

public abstract class JmsAbstractAckTestCase extends JmsAbstractTestCase {

  @Rule
  public SystemProperty destination = new SystemProperty("destination", newDestination("destination"));

  @Rule
  public SystemProperty ackMode = new SystemProperty("ack.mode", getAckMode().toString());

  @Rule
  public SystemProperty maxRedelivery = new SystemProperty(MAX_REDELIVERY, "1");

  @After
  public void cleanUpQueues() {
    JmsMessageStorage.cleanUpQueue();
  }

  public abstract AckMode getAckMode();

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"ack/jms-ack.xml", "config/activemq/activemq-default.xml"};
  }

  void recoverSession(String ackId) throws Exception {
    flowRunner("recoverSession").withPayload(ackId).run();
  }

  void ackMessage(String ackId) throws Exception {
    flowRunner("doManualAck").withPayload(ackId).run();
  }

  String buildMessage(String message, Actions action) {
    return "{\"message\" : \"" + message + "\", \"action\" : \"" + action + "\"}";
  }

  public enum Actions {
    ACK, RECOVER, EXPLODE, NOTHING
  }

}
