/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mule.extensions.jms.api.exception.JmsError.TIMEOUT;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.JmsMessageStorage.cleanUpQueue;
import static org.mule.functional.junit4.matchers.MessageMatchers.hasPayload;
import static org.mule.runtime.api.metadata.MediaType.ANY;

import org.mule.extensions.jms.api.destination.JmsDestination;
import org.mule.extensions.jms.api.message.JmsAttributes;
import org.mule.extensions.jms.api.message.JmsHeaders;
import org.mule.functional.api.exception.ExpectedError;
import org.mule.functional.api.flow.FlowRunner;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.util.func.CheckedSupplier;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.probe.JUnitLambdaProbe;
import org.mule.tck.probe.PollingProber;
import org.mule.test.runner.ArtifactClassLoaderRunnerConfig;

import java.util.Map;

import org.junit.Before;
import org.junit.Rule;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;

@Feature(JMS_EXTENSION)
@ArtifactClassLoaderRunnerConfig(
    testInclusions = {"org.apache.activemq:artemis-jms-client"}, sharedRuntimeLibs = {"org.apache.activemq:activemq-client",
        "org.apache.activemq:activemq-broker", "org.apache.activemq:activemq-kahadb-store"})
public abstract class JmsAbstractTestCase extends MuleArtifactFunctionalTestCase {

  protected static final String NAMESPACE = "JMS";
  protected static final String DESTINATION_VAR = "destination";
  protected static final String MAXIMUM_WAIT_VAR = "maximumWait";

  protected static final String PUBLISHER_FLOW = "publisher";
  protected static final String CONSUMER_FLOW = "consumer";
  protected static final String LISTENER_FLOW = "listener";

  protected static final int TIMEOUT_MILLIS = 10000;
  protected static final int POLL_DELAY_MILLIS = 100;
  protected static final String MAX_REDELIVERY = "max.redelivery";
  protected String destination;
  protected long maximumWait = 10000;

  @Rule
  public ExpectedError expectedError = ExpectedError.none();

  @Rule
  public SystemProperty maxRedelivery = new SystemProperty(MAX_REDELIVERY, "0");

  @Override
  protected void doSetUpBeforeMuleContextCreation() throws Exception {
    super.doSetUpBeforeMuleContextCreation();
  }

  @Before
  public void setUp() {
    cleanUpQueue();
  }

  @Override
  protected void doTearDown() throws Exception {
    cleanUpQueue();
  }

  protected String newDestination(String name) {
    return name + currentTimeMillis();
  }

  protected void publish(Object message) throws Exception {
    publish(message, ANY);
  }

  protected void publish(Object message, MediaType mediaType) throws Exception {
    publish(message, destination, mediaType);
  }

  protected void publish(Object message, String destination) throws Exception {
    publish(message, destination, ANY);
  }

  protected void publish(Object message, String destination, MediaType mediaType) throws Exception {
    publish(message, destination, emptyMap(), mediaType);
  }

  @Step("Run publish flow message to destination: {destination} with media type and flow vars")
  protected void publish(Object message, String destination, Map<String, Object> flowVars, MediaType mediaType) throws Exception {
    FlowRunner publisher = flowRunner(PUBLISHER_FLOW)
        .withPayload(message)
        .withMediaType(mediaType)
        .withVariable(DESTINATION_VAR, destination);
    flowVars.forEach(publisher::withVariable);
    publisher.run();
  }

  protected Message consume() throws Exception {
    return consume(destination, emptyMap(), maximumWait);
  }

  protected Message consume(String destination) throws Exception {
    return consume(destination, emptyMap(), maximumWait);
  }

  protected Message consume(String destination, Map<String, Object> flowVars) throws Exception {
    return consume(destination, flowVars, maximumWait);
  }

  @Step("Run consume message flow from dest: {destination} with flow vars and maximum wait")
  protected Message consume(String destination, Map<String, Object> flowVars, long maximumWait) throws Exception {
    FlowRunner consumer = flowRunner(CONSUMER_FLOW)
        .withVariable(DESTINATION_VAR, destination)
        .withVariable(MAXIMUM_WAIT_VAR, maximumWait);
    flowVars.forEach(consumer::withVariable);
    return consumer.run().getMessage();
  }

  @Step("Assert message headers")
  protected void assertHeaders(JmsAttributes attributes, JmsDestination destination, Integer deliveryMode,
                               Integer priority, boolean hasMessageId, boolean hasTimestamp, String correlationId,
                               JmsDestination replyTo, String type, Boolean redelivered) {

    JmsHeaders headers = attributes.getHeaders();
    assertThat(headers, notNullValue());
    assertThat(headers.getJMSMessageID(), hasMessageId ? not(isEmptyOrNullString()) : nullValue());
    assertThat(headers.getJMSTimestamp(), hasTimestamp ? not(nullValue()) : nullValue());
    assertThat(headers.getJMSCorrelationID(), equalTo(correlationId));
    assertThat(headers.getJMSDeliveryMode(), equalTo(deliveryMode));
    assertThat(headers.getJMSPriority(), equalTo(priority));
    assertThat(headers.getJMSRedelivered(), equalTo(redelivered));
    assertThat(headers.getJMSType(), equalTo(type));

    assertDestination(headers.getJMSDestination(), destination);

    if (replyTo == null) {
      assertThat(headers.getJMSReplyTo(), nullValue());
    } else {
      assertDestination(headers.getJMSReplyTo(), destination);
    }
  }

  @Step("Assert destination")
  private void assertDestination(JmsDestination actual, JmsDestination expected) {
    assertThat(actual.getDestination(), equalTo(expected.getDestination()));
    assertThat(actual.getDestinationType(), equalTo(expected.getDestinationType()));
  }

  @Step("Get reply destination")
  protected String getReplyDestination(Message firstMessage) {
    return ((JmsAttributes) firstMessage.getAttributes().getValue()).getHeaders().getJMSReplyTo().getDestination();
  }

  @Step("Polling probe validation")
  protected void validate(CheckedSupplier<Boolean> validation, long validationTimeout, long validationDelay) {
    new PollingProber(validationTimeout, validationDelay).check(new JUnitLambdaProbe(validation));
  }

  @Step("Assert queue is empty")
  protected void assertQueueIsEmpty() throws Exception {
    try {
      JmsMessageStorage.pollMuleMessage();
      throw new RuntimeException();
    } catch (AssertionError error) {
      //
    }
  }

  @Step("Assert JMS message")
  protected void assertJmsMessage(Result<TypedValue<Object>, JmsAttributes> message, String jmsMessage, boolean isRedelivered) {
    Object value = message.getOutput().getValue();
    assertThat(value, is(jmsMessage));

    JmsAttributes attributes = message.getAttributes().get();
    assertThat(attributes.getHeaders().getJMSRedelivered(), is(isRedelivered));
  }

  @Step("Check for no messages on dest: {destination}")
  protected void assertEmptyDestination(String destination) throws Exception {
    expectedError.expectErrorType(any(String.class), is(TIMEOUT.getType()));
    consume(destination);
  }

  @Step("Check for message on dest: {destination}")
  protected void assertMessageOnDestination(String message, String destination) throws Exception {
    assertThat(consume(destination), hasPayload(equalTo(message)));
  }
}
