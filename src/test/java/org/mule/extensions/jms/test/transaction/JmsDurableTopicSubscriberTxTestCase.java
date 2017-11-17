/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.transaction;

import static java.lang.System.currentTimeMillis;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertThat;
import static org.mule.extensions.jms.api.exception.JmsError.TIMEOUT;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.DURABLE_SUBSCRIBER;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.TRANSACTION;
import static org.mule.functional.junit4.matchers.ThrowableMessageMatcher.hasMessage;
import static org.mule.tck.junit4.matcher.ErrorTypeMatcher.errorType;

import org.mule.extensions.jms.api.exception.JmsConsumeException;
import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.functional.api.flow.FlowRunner;
import org.mule.runtime.api.message.Error;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.runtime.core.privileged.exception.EventProcessingException;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.junit4.rule.SystemPropertyLambda;

import java.util.Optional;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Stories;
import io.qameta.allure.Story;
import org.junit.Rule;
import org.junit.Test;

@Feature(JMS_EXTENSION)
@Stories({@Story(TRANSACTION), @Story(DURABLE_SUBSCRIBER)})
public class JmsDurableTopicSubscriberTxTestCase extends JmsAbstractTestCase {

  @Rule
  public SystemProperty topicDest = new SystemPropertyLambda("topicDest", () -> newDestination("topicDest"));

  @Rule
  public SystemProperty finalDest = new SystemPropertyLambda("finalDest", () -> newDestination("finalDest"));

  @Rule
  public SystemProperty subscriptionName =
      new SystemPropertyLambda("subscriptionName", () -> newSubscription("subscriptionName"));

  private static final String TOPIC_CONSUMER_FLOW = "topicConsumer";

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"config/activemq/activemq-with-client-id.xml", "transactions/jms-durable-topic-subscriber-tx.xml"};
  }

  @Test
  @Description("Verifies that a message could be consumed from a topic when a tx is taking place")
  public void txListener() throws Exception {
    Flow flow = (Flow) getFlowConstruct("topicSubscriber");

    flow.start();
    publish(TEST_MESSAGE, topicDest.getValue());

    assertMessageOnDestination(TEST_MESSAGE, finalDest.getValue());
    flow.stop();

    assertThat(isTopicSubscriptionEmpty(topicDest.getValue()), is(true));
  }

  @Step("Run topic consumer flow from dest: {destination} and durable subscription: {subscriptionName} ")
  protected Message consumeFromTopic(String destination) throws Exception {
    FlowRunner consumer = flowRunner(TOPIC_CONSUMER_FLOW)
        .withVariable(DESTINATION_VAR, destination)
        .withVariable(MAXIMUM_WAIT_VAR, maximumWait);
    return consumer.run().getMessage();
  }

  @Step("Check if there are messages on dest: {destination}")
  protected boolean isTopicSubscriptionEmpty(String destination) throws Exception {
    boolean isEmpty = false;
    try {
      consumeFromTopic(destination);
    } catch (EventProcessingException exception) {
      Optional<Error> error = exception.getEvent().getError();
      isEmpty = error.isPresent()
          && errorType(TIMEOUT).matches(error.get().getErrorType())
          && both(isA(JmsConsumeException.class)).and(hasMessage(startsWith("An error occurred while consuming a message")))
              .matches(error.get().getCause());;
    }
    return isEmpty;
  }

  private String newSubscription(String subscriptionName) {
    return subscriptionName + currentTimeMillis();
  }

}
