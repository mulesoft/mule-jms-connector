/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.ack;

import static org.mule.extensions.jms.api.ack.AckMode.MANUAL;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.JmsMessageStorage.pollMessage;
import static org.mule.extensions.jms.test.ack.JmsAbstractAckTestCase.Actions.ACK;
import static org.mule.extensions.jms.test.ack.JmsAbstractAckTestCase.Actions.NOTHING;
import static org.mule.extensions.jms.test.ack.JmsAbstractAckTestCase.Actions.RECOVER;

import org.mule.extensions.jms.api.ack.AckMode;
import org.mule.extensions.jms.test.JmsMessageStorage;
import org.mule.runtime.api.el.BindingContext;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.runtime.operation.Result;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.Test;

@Feature(JMS_EXTENSION)
@Story("Manual Acknowledgement over sessions")
public class JmsManualAckTestCase extends JmsAbstractAckTestCase {

  @Override
  public AckMode getAckMode() {
    return MANUAL;
  }

  @Override
  protected boolean isDisposeContextPerClass() {
    return false;
  }

  @Test
  @Description("Receives two messages which are manually acknowledged and a third one that doesn't. After a session " +
      "recover only the last message get's redelivered")
  public void ackSessionManually() throws Exception {
    publish(buildMessage("This is a message", ACK));
    publish(buildMessage("This is a message", ACK));
    String messageToReDeliver = buildMessage("Message to be re delivered", NOTHING);
    publish(messageToReDeliver);
    validate(() -> JmsMessageStorage.receivedMessages() == 3, TIMEOUT_MILLIS, POLL_DELAY_MILLIS);

    Result<TypedValue<Object>, Object> message = pollMessage();

    String ackId = (String) expressionManager.evaluate("#[attributes.ackId]", BindingContext.builder()
        .addBinding("attributes", TypedValue.of(message.getAttributes().get())).build())
        .getValue();
    JmsMessageStorage.cleanUpQueue();
    recoverSession(ackId);
    validate(() -> JmsMessageStorage.receivedMessages() == 1, TIMEOUT_MILLIS, POLL_DELAY_MILLIS);
    assertJmsMessage(pollMessage(), messageToReDeliver, true);
  }

  @Test
  @Description("A successfully processed message is not acknowledged and after a session recover it get's redelivered")
  public void recoveredSessionReDeliversNotAcknowledgedMessages() throws Exception {
    String message = buildMessage("Message to recover", RECOVER);
    publish(message);
    assertJmsMessage(pollMessage(), message, false);
    assertJmsMessage(pollMessage(), message, true);
  }
}
