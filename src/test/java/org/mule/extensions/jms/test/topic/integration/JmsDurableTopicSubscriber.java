/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.topic.integration;

import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.DURABLE_SUBSCRIBER;

import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.junit4.rule.SystemPropertyLambda;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.junit.Rule;
import org.junit.Test;

@Feature(JMS_EXTENSION)
@Story(DURABLE_SUBSCRIBER)
public class JmsDurableTopicSubscriber extends JmsAbstractTestCase {

  private static final String TOPIC_SUBSCRIBER_1 = "topicSubscriber1";
  private static final String TOPIC_SUBSCRIBER_2 = "topicSubscriber2";

  @Rule
  public SystemProperty topicDest = new SystemPropertyLambda("topicDest", () -> newDestination("topicDest"));

  @Rule
  public SystemProperty subscriberFinalDest1 =
      new SystemPropertyLambda("subscriberFinalDest1", () -> newDestination("subscriberFinalDest1"));

  @Rule
  public SystemProperty subscriberFinalDest2 =
      new SystemPropertyLambda("subscriberFinalDest2", () -> newDestination("subscriberFinalDest2"));

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"integration/jms-durable-topic-subscriber.xml", "config/activemq/activemq-with-client-id.xml"};
  }

  @Test
  @Description("Verifies that durable subscribers do not receive messages published before their subscription")
  public void testMessagePublishedBeforeSubscription() throws Exception {
    publish(TEST_MESSAGE, topicDest.getValue());

    startSubscriberFlows();

    assertEmptyDestination(subscriberFinalDest1.getValue());
    assertEmptyDestination(subscriberFinalDest2.getValue());
  }

  @Test
  @Description("Verifies that durable subscribers receive messages published after their subscription")
  public void testMessagePublishedAfterSubscription() throws Exception {
    startSubscriberFlows();

    publish(TEST_MESSAGE, topicDest.getValue());

    assertMessageOnFlowsFinalDestinations();
  }

  @Test
  @Description("Verifies that durable subscribers receive messages even if they were published when they were disconnected")
  public void testMessagePublishedAfterSubscriptionWheSubscribersWereDisconnected() throws Exception {
    startSubscriberFlows();

    stopSubscriberFlows();

    publish(TEST_MESSAGE, topicDest.getValue());

    startSubscriberFlows();

    assertMessageOnFlowsFinalDestinations();
  }

  @Step("Start Subscriber flows")
  private void startSubscriberFlows() throws Exception {
    ((Flow) getFlowConstruct(TOPIC_SUBSCRIBER_1)).start();
    ((Flow) getFlowConstruct(TOPIC_SUBSCRIBER_2)).start();
  }

  @Step("Stop Subscriber flows")
  private void stopSubscriberFlows() throws Exception {
    ((Flow) getFlowConstruct(TOPIC_SUBSCRIBER_1)).stop();
    ((Flow) getFlowConstruct(TOPIC_SUBSCRIBER_2)).stop();
  }

  @Step("Assert message on flow's final destinations")
  private void assertMessageOnFlowsFinalDestinations() throws Exception {
    assertMessageOnDestination(TEST_MESSAGE, subscriberFinalDest1.getValue());
    assertMessageOnDestination(TEST_MESSAGE, subscriberFinalDest2.getValue());
  }
}
