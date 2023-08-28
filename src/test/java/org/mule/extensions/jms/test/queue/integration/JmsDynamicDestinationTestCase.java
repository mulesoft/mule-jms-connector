/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.queue.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.functional.junit4.matchers.MessageMatchers.hasPayload;

import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.runtime.api.message.Message;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.junit.Test;

@Feature(JMS_EXTENSION)
public class JmsDynamicDestinationTestCase extends JmsAbstractTestCase {

  private static final String BRIDGE_FLOW = "bridge";
  private static final String SEND_PAYLOAD_FLOW = "send-payload";
  private static final String BRIDGE_RECEIVER_FLOW = "bridge-receiver";

  private static final String INITIAL_DESTINATION = "initialQueue";
  private static final String INITIAL_DESTINATION_VAR = "initialDestination";

  private static final String FINAL_DESTINATION = "finalQueue";
  private static final String FINAL_DESTINATION_VAR = "finalDestination";

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"config/activemq/activemq-default.xml", "integration/jms-dynamic-destination.xml"};
  }

  @Test
  @Description("Verifies that a message can be dynamically routed to a destination based on its content or properties. This was" +
      " previously achieved through the usage of something called dynamic endpoints.")
  public void testDynamicDestination() throws Exception {
    sendMessage();
    executeBridge();

    assertExpectedMessage(receiveMessageFromBridgeTarget());
  }

  @Step("Send message")
  protected void sendMessage() throws Exception {
    flowRunner(SEND_PAYLOAD_FLOW)
        .withVariable(INITIAL_DESTINATION_VAR, INITIAL_DESTINATION)
        .withVariable(FINAL_DESTINATION_VAR, FINAL_DESTINATION)
        .withPayload(TEST_PAYLOAD)
        .run();
  }

  @Step("Execute bridge flow")
  protected void executeBridge() throws Exception {
    flowRunner(BRIDGE_FLOW)
        .withVariable(INITIAL_DESTINATION_VAR, INITIAL_DESTINATION)
        .run();
  }

  @Step("Receive message")
  protected Message receiveMessageFromBridgeTarget() throws Exception {
    return flowRunner(BRIDGE_RECEIVER_FLOW)
        .withVariable(FINAL_DESTINATION_VAR, FINAL_DESTINATION)
        .run()
        .getMessage();
  }

  @Step("Assert message")
  protected void assertExpectedMessage(Message message) {
    assertThat(message, hasPayload(equalTo(TEST_PAYLOAD)));
  }
}
