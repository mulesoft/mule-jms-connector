/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.integration;

import static com.google.common.collect.ImmutableMap.of;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mule.extensions.jms.api.destination.DestinationType.QUEUE;
import static org.mule.extensions.jms.api.destination.DestinationType.TOPIC;
import static org.mule.extensions.jms.api.exception.JmsError.TIMEOUT;
import static org.mule.functional.junit4.matchers.MessageMatchers.hasPayload;
import static org.mule.runtime.api.metadata.MediaType.ANY;

import org.mule.extensions.jms.api.destination.DestinationType;
import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.extensions.jms.test.JmsMessageStorage;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.junit4.rule.SystemPropertyLambda;
import org.mule.test.runner.RunnerDelegateTo;

import java.util.Collection;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunnerDelegateTo(Parameterized.class)
public abstract class JmsAbstractMessageFilteringTestCase extends JmsAbstractTestCase {

  @Parameter()
  public DestinationType destinationType;

  @Rule
  public SystemProperty destinationProperty = new SystemPropertyLambda("destination", () -> newDestination("destination"));

  private static final String MESSAGE_PRIORITY_PROP = "messagePriority";
  private static final String DESTINATION_TYPE_PROP = "destinationType";
  private static final String MESSAGE_BODY_1 = TEST_MESSAGE + "1";
  private static final String MESSAGE_BODY_2 = TEST_MESSAGE + "2";

  @Parameters(name = "destinationType: {0}")
  public static Collection<Object[]> data() {
    return asList(new Object[][] {
        {QUEUE}, {TOPIC}
    });
  }

  @Test
  @Description("Verifies the filtering of messages consumed by consume operation")
  public void testMessageFilteringOnConsume() throws Exception {
    expectedError.expectError(NAMESPACE, TIMEOUT, Exception.class,
                              "Failed to retrieve a Message. Operation timed out after 10000 milliseconds");

    destination = newDestination("destination");

    publishMessage(MESSAGE_BODY_2, destination, "2", destinationType);
    publishMessage(MESSAGE_BODY_1, destination, "1", destinationType);

    assertMuleMessage(consume(destination, of(DESTINATION_TYPE_PROP, destinationType)), MESSAGE_BODY_1, "1");
    consume(destination, of(DESTINATION_TYPE_PROP, destinationType));
  }

  @Test
  @Description("Verifies the filtering of messages consumed by listeners")
  public void testMessageFilteringOnListener() throws Exception {
    publishMessage(MESSAGE_BODY_2, destinationProperty.getValue(), "2", destinationType);
    publishMessage(MESSAGE_BODY_1, destinationProperty.getValue(), "1", destinationType);

    assertJmsMessageStorageMessage(JmsMessageStorage.pollMessage(), MESSAGE_BODY_1, "1");
    assertQueueIsEmpty();
  }

  @Step("Publish message")
  private void publishMessage(String message, String destination, String priority, DestinationType destinationType)
      throws Exception {
    publish(message, destination, of(MESSAGE_PRIORITY_PROP, priority, DESTINATION_TYPE_PROP, destinationType), ANY);
  }

  @Step("Assert Mule message")
  private void assertMuleMessage(Message message, String messagePayload, String priority) {
    assertThat(message, hasPayload(equalTo(messagePayload)));
    from(message.getAttributes())
        .as("attributes")
        .assertThat(format("#[attributes.properties.userProperties.%s]", MESSAGE_PRIORITY_PROP), is(priority));
  }

  @Step("Assert JmsMessageStorage message")
  private void assertJmsMessageStorageMessage(Result<TypedValue<Object>, Object> message, String messagePayload,
                                              String priority) {
    assertThat(message.getOutput().getValue(), is(messagePayload));
    from(message.getAttributes().get())
        .as("attributes")
        .assertThat(format("#[attributes.properties.userProperties.%s]", MESSAGE_PRIORITY_PROP), is(priority));
  }

}
