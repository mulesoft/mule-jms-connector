/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.ack;

import static org.mule.extensions.jms.api.ack.AckMode.IMMEDIATE;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.ack.JmsAbstractAckTestCase.Actions.EXPLODE;
import static org.mule.extensions.jms.test.ack.JmsAbstractAckTestCase.Actions.NOTHING;

import org.mule.extensions.jms.api.ack.AckMode;
import org.mule.extensions.jms.test.JmsMessageStorage;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.Test;

@Feature(JMS_EXTENSION)
@Story("Immediate Acknowledgement over sessions")
public class JmsImmediateAckTestCase extends JmsAbstractAckTestCase {

  @Override
  public AckMode getAckMode() {
    return IMMEDIATE;
  }

  @Test
  @Description("Messages get's acknowledged automatically by the JMS Client")
  public void sessionIsAutomaticallyAck() throws Exception {
    String message = "Message to ACK";
    publish(buildMessage(message, EXPLODE));
    publish(buildMessage(message, NOTHING));

    validate(() -> JmsMessageStorage.receivedMessages() == 2, 5000, 50);
    cleanUpQueues();
    assertQueueIsEmpty();
  }
}
