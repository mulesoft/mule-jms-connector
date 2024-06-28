/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.fail;
import static org.mule.extensions.jms.api.exception.JmsError.TIMEOUT;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.JmsMessageStorage.cleanUpQueue;
import static org.mule.functional.junit4.matchers.MessageMatchers.hasPayload;
import static org.mule.runtime.api.metadata.MediaType.ANY;
import static org.mule.tck.junit4.matcher.ErrorTypeMatcher.errorType;

import org.mule.extensions.jms.test.util.ExpressionAssertion;
import org.mule.functional.api.exception.ExpectedError;
import org.mule.functional.api.flow.FlowRunner;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.runtime.api.el.BindingContext;
import org.mule.runtime.api.message.Error;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.el.ExpressionManager;
import org.mule.runtime.core.api.util.func.CheckedSupplier;
import org.mule.runtime.core.privileged.exception.EventProcessingException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.probe.JUnitLambdaProbe;
import org.mule.tck.probe.PollingProber;
import org.mule.test.runner.ArtifactClassLoaderRunnerConfig;

import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;

@Feature(JMS_EXTENSION)
@ArtifactClassLoaderRunnerConfig(
    testInclusions = {"org.apache.activemq:artemis-jms-client", "org.mule.connectors:mule-jms-client"},
    applicationSharedRuntimeLibs = {"org.apache.activemq:activemq-client",
        "org.apache.activemq:activemq-broker", "org.apache.activemq:activemq-kahadb-store", "org.fusesource.hawtbuf:hawtbuf",
        "org.apache.activemq.protobuf:activemq-protobuf", "org.mule.tests:mule-tests-model"})
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

  @Inject
  public ExpressionManager expressionManager;

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

  protected Message consume(String destination, long maximumWait) throws Exception {
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
  protected void assertHeaders(Object attributes, Object destination, Integer deliveryMode,
                               Integer priority, boolean hasMessageId, boolean hasTimestamp, String correlationId,
                               Object replyTo, String type, Boolean redelivered) {

    ExpressionAssertion attributesAsserter = from(attributes).as("attributes");
    attributesAsserter.assertThat("#[attributes.headers]", notNullValue());

    ExpressionAssertion headers = attributesAsserter.andFrom("#[attributes.headers]").as("headers");
    headers.assertThat("#[headers.JMSMessageID]", hasMessageId ? not(isEmptyOrNullString()) : nullValue());
    headers.assertThat("#[headers.JMSTimestamp]", hasTimestamp ? not(nullValue()) : nullValue());
    if (correlationId != null) {
      headers.assertThat("#[headers.JMSCorrelationID]", equalTo(correlationId));
    } else {
      headers.assertThat("#[headers.JMSCorrelationID]", is(notNullValue()));
    }
    headers.assertThat("#[headers.JMSDeliveryMode]", equalTo(deliveryMode));
    headers.assertThat("#[headers.JMSPriority]", equalTo(priority));
    headers.assertThat("#[headers.JMSRedelivered]", equalTo(redelivered));
    headers.assertThat("#[headers.JMSType]", equalTo(type));

    assertDestination(headers.andFrom("#[headers.JMSDestination]"), destination);

    if (replyTo == null) {
      headers.assertThat("#[headers.JMSReplyTo]", nullValue());
    } else {
      assertDestination(headers.andFrom("#[headers.getJMSReplyTo]"), destination);
    }
  }

  @Step("Assert destination")
  private void assertDestination(ExpressionAssertion actualDestination, Object expected) {
    actualDestination.as("actual");
    actualDestination.assertThat("#[actual.destination]")
        .comparedTo(expected)
        .is(Matchers::equalTo, "#[payload.destination]");
    actualDestination.assertThat("#[actual.destinationType as String]")
        .comparedTo(expected)
        .is(Matchers::equalTo, "#[payload.destinationType as String]");
  }

  @Step("Get reply destination")
  protected String getReplyDestination(Message firstMessage) {
    return (String) evaluate("#[payload.headers.JMSReplyTo.destination]", firstMessage.getAttributes());
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
  protected void assertJmsMessage(Result message, String jmsMessage, boolean isRedelivered) {
    Object value = ((TypedValue) message.getOutput()).getValue();
    assertThat(value, is(jmsMessage));

    from(message.getAttributes())
        .as("attributes")
        .assertThat("#[attributes.headers.JMSRedelivered]", is(isRedelivered));
  }

  @Step("Check for no messages on dest: {destination}")
  protected void assertEmptyDestination(String destination) throws Exception {
    boolean emptyDestination = false;
    try {
      consume(destination, 2000);
    } catch (EventProcessingException exception) {
      Optional<Error> error = exception.getEvent().getError();
      if (error.isPresent() && errorType(TIMEOUT).matches(error.get().getErrorType())) {
        emptyDestination = true;
      }
    } catch (Throwable throwable) {
      fail("Unexpected exception was caught when trying to find out if destination was empty");
    }

    if (!emptyDestination) {
      fail("Destination is not empty");
    }
  }

  @Step("Check for message on dest: {destination}")
  protected void assertMessageOnDestination(String message, String destination) throws Exception {
    assertThat(consume(destination), hasPayload(equalTo(message)));
  }

  public Object evaluate(String expression, Object object) {
    TypedValue typedValue;

    if (object instanceof TypedValue) {
      typedValue = (TypedValue) object;
    } else {
      typedValue = new TypedValue<>(object, DataType.OBJECT);
    }

    return expressionManager.evaluate(expression, BindingContext.builder().addBinding("payload", typedValue).build())
        .getValue();
  }

  public ExpressionAssertion from(Object object) {
    return new ExpressionAssertion(object, expressionManager);
  }
}
