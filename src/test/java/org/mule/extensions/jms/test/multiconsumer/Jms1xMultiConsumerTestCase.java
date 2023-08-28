/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.multiconsumer;

import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mule.extensions.jms.api.destination.DestinationType.QUEUE;
import static org.mule.extensions.jms.test.JmsMessageStorage.cleanUpQueue;
import static org.mule.extensions.jms.test.JmsMessageStorage.receivedMessages;

import org.mule.runtime.core.api.construct.Flow;
import org.mule.runtime.core.api.retry.policy.RetryPolicyExhaustedException;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.probe.JUnitLambdaProbe;
import org.mule.tck.probe.PollingProber;
import org.mule.test.runner.RunnerDelegateTo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.Rule;
import org.junit.Test;

@Feature("JMS Extension")
@Story("Multi Consumers - JMS 1.x")
@RunnerDelegateTo()
public class Jms1xMultiConsumerTestCase extends AbstractJmsMultiConsumerTestCase {

  @Rule
  public SystemProperty maxRedelivery = new SystemProperty(MAX_REDELIVERY, "1");

  @Inject
  @Named("topicListener")
  private Flow topicListenerFlow;

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"multiconsumer/jms-1.x-multi-consumers.xml", "config/activemq/activemq-default.xml"};
  }

  @Test
  public void multiConsumersConsumeMessagesInParallel() throws Exception {
    publishTo(NUMBER_OF_MESSAGES, destination.getValue(), QUEUE);

    long distinctAckIds = getMessages(NUMBER_OF_MESSAGES)
        .map(result -> evaluate("#[payload.ackId]", result.getAttributes()))
        .distinct()
        .count();

    assertThat(distinctAckIds, is(parseLong(NUMBER_OF_CONSUMERS)));
  }

  @Test
  public void ackFromOneConsumerDoesntAffectOtherConsumers() throws Exception {
    publishTo(NUMBER_OF_MESSAGES, destination.getValue(), QUEUE);

    Map<String, List<String>> collect = getMessages(NUMBER_OF_MESSAGES)
        .map(result -> (String) evaluate("#[payload.ackId]", result.getAttributes()))
        .collect(groupingBy(identity()));

    Iterator<Map.Entry<String, List<String>>> iterator = collect.entrySet().iterator();
    Map.Entry<String, List<String>> stringListEntry = iterator.next();
    String ackId = stringListEntry.getKey();
    int messagesToAck = stringListEntry.getValue().size();

    cleanUpQueue();

    while (iterator.hasNext()) {
      recoverSession(iterator.next().getKey());
    }
    ackMessage(ackId);

    new PollingProber(5000, 100).check(new JUnitLambdaProbe(() -> receivedMessages() == NUMBER_OF_MESSAGES - messagesToAck,
                                                            () -> format("The listener should have received %s messages, but received %s",
                                                                         NUMBER_OF_MESSAGES
                                                                             - messagesToAck,
                                                                         receivedMessages())));
  }

  @Test
  public void non2JMSTopicsCanOnlyUseOneConsumer() throws Exception {
    try {
      topicListenerFlow.start();
    } catch (RetryPolicyExhaustedException e) {
      assertThat(e.getCause().getCause().getCause(), is(instanceOf(IllegalArgumentException.class)));
    }
  }
}
