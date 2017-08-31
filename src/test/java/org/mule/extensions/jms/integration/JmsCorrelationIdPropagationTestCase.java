/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.CORRELATION_ID;

import org.mule.extensions.jms.api.message.JmsAttributes;
import org.mule.extensions.jms.api.message.JmsHeaders;
import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.junit.Test;

@Feature(JMS_EXTENSION)
@Story(CORRELATION_ID)
public class JmsCorrelationIdPropagationTestCase extends JmsAbstractTestCase {

  private static final String FIRST_MESSAGE = "My First Message";
  private static final String BRIDGE_FLOW = "bridge";
  private static final String SEND_PAYLOAD_FLOW = "send-payload";
  private static final String BRIDGE_RECEIVER_FLOW = "bridge-receiver";

  private static final String INITIAL_DESTINATION = "initialQueue";
  private static final String INITIAL_DESTINATION_VAR = "initialDestination";

  private static final String FINAL_DESTINATION = "finalQueue";
  private static final String FINAL_DESTINATION_VAR = "finalDestination";

  private static final String CUSTOM_CORRELATION_ID = "custom-correlation-id";
  private static final String CUSTOM_CORRELATION_ID_VAR = "customCorrelationId";

  @Override
  protected String[] getConfigFiles() {
    return new String[]{"config/activemq/activemq-default.xml", "integration/jms-correlation-id-propagation.xml"};
  }

  @Test
  @Description("Tests the correct propagation of the correlation id property within the JMS connector.")
  public void testMuleCorrelationIdPropagation() throws Exception {
    sendMessage();
    executeBridge();
    Message message = receiveMessageFromBridgeTarget();
    assertExpectedMessage(message);
  }

  @Step("Send message")
  protected void sendMessage() throws Exception {
    flowRunner(SEND_PAYLOAD_FLOW)
        .withVariable(INITIAL_DESTINATION_VAR, INITIAL_DESTINATION)
        .withVariable(CUSTOM_CORRELATION_ID_VAR, CUSTOM_CORRELATION_ID)
        .withPayload(FIRST_MESSAGE)
        .run();
  }

  @Step("Execute bridge")
  protected void executeBridge() throws Exception {
    flowRunner(BRIDGE_FLOW)
        .withVariable(INITIAL_DESTINATION_VAR, INITIAL_DESTINATION)
        .withVariable(FINAL_DESTINATION_VAR, FINAL_DESTINATION)
        .run();
  }

  @Step("Receive message from bridge target")
  protected Message receiveMessageFromBridgeTarget() throws Exception {
    return flowRunner(BRIDGE_RECEIVER_FLOW)
        .withVariable(FINAL_DESTINATION_VAR, FINAL_DESTINATION)
        .run()
        .getMessage();
  }

  @Step("Verify correlation id properly was propagated")
  protected void assertExpectedMessage(Message message) {
    TypedValue<Object> attributes = message.getAttributes();
    assertThat(attributes, not(nullValue()));

    JmsHeaders headers = ((JmsAttributes) attributes.getValue()).getHeaders();
    assertThat(headers, not(nullValue()));
    assertThat(headers.getJMSCorrelationID(), is(CUSTOM_CORRELATION_ID));
  }

}
