/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.transaction;

import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.TRANSACTION;
import static org.mule.extensions.jms.test.JmsMessageStorage.receivedMessages;
import static org.mule.tck.probe.PollingProber.probe;

import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.tck.junit4.rule.SystemProperty;

import javax.inject.Inject;
import javax.inject.Named;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.Rule;
import org.junit.Test;

@Feature(JMS_EXTENSION)
@Story(TRANSACTION)
public class JmsTransactionalTestCase extends JmsAbstractTestCase {

  private static final String MESSAGE = "MESSAGE";

  @Rule
  public SystemProperty listenerDestination = new SystemProperty("destination", newDestination("destination"));

  @Rule
  public SystemProperty publishDestination = new SystemProperty("publishDestination", newDestination("publishDestination"));

  @Rule
  public SystemProperty maxRedelivery = new SystemProperty(MAX_REDELIVERY, "1");

  @Inject
  @Named("txSubscriberWithPublish")
  private Flow txSubscriberWithPublishFlow;

  @Inject
  @Named("txListener")
  private Flow txListener;

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"transactions/jms-transactional.xml", "config/activemq/activemq-default-no-caching.xml"};
  }

  @Test
  public void txPublish() throws Exception {
    String txPublishDestination = newDestination("txPublish");
    publishTx(txPublishDestination);
    Message consume = consume(txPublishDestination);
    assertThat(consume.getPayload().getValue(), is(MESSAGE));
  }

  @Test
  public void txPublishRollback() throws Exception {
    String txPublishDestination = newDestination("txPublish");
    publishTx(txPublishDestination, true);

    assertEmptyDestination(txPublishDestination);
  }

  @Test
  public void txConsumer() throws Exception {
    String txConsumeDestination = newDestination("txConsume");
    publish(MESSAGE, txConsumeDestination);

    consumeTx(txConsumeDestination, false);
    assertEmptyDestination(txConsumeDestination);
  }

  @Test
  public void txConsumerRollback() throws Exception {
    String txConsumeDestination = newDestination("txConsumerRollbacked");
    publish(MESSAGE, txConsumeDestination);

    consumeTx(txConsumeDestination, true);
    CoreEvent event = consumeTx(txConsumeDestination, false);
    assertThat(event.getMessage().getPayload().getValue(), is(MESSAGE));
  }

  @Test
  public void txSubscriberWithPublish() throws Exception {
    publish(MESSAGE, listenerDestination.getValue());
    txSubscriberWithPublishFlow.start();
    Message event = consume(publishDestination.getValue(), emptyMap(), 5000L);
    assertThat(event.getPayload().getValue(), is(MESSAGE));
  }

  @Test
  public void nonTxPublishMustNotJoinCurrentTx() throws Exception {
    String txDestinationName = "txDestination";
    String txDestination = newDestination(txDestinationName);
    String nonTxDestinationName = "nonTxDestination";
    String nonTxDestination = newDestination(nonTxDestinationName);

    flowRunner("nonTxPublishMustNotJoinCurrentTx")
        .withPayload(MESSAGE)
        .withVariable(txDestinationName, txDestination)
        .withVariable(nonTxDestinationName, nonTxDestination)
        .runExpectingException();

    consume(nonTxDestination);
    assertEmptyDestination(txDestination);
  }

  @Test
  public void txListenerPublishSeveralMessages() throws Exception {
    txListener.start();

    publish(MESSAGE, listenerDestination.getValue());
    publish(MESSAGE, listenerDestination.getValue());
    publish(MESSAGE, listenerDestination.getValue());
    publish(MESSAGE, listenerDestination.getValue());
    publish(MESSAGE, listenerDestination.getValue());

    probe(() -> receivedMessages() == 5);
  }

  @Test
  public void txConsumeAndPublish() throws Exception {
    String txPublishDestination = newDestination("txPublish");
    String txConsumeDestination = newDestination("txConsume");

    publish(MESSAGE, txConsumeDestination);
    consumeAndPublishTx(txConsumeDestination, txPublishDestination, false);

    Message consume = consume(txPublishDestination);
    assertThat(consume.getPayload().getValue(), is(MESSAGE));
  }

  @Test
  public void txConsumeAndPublishRollback() throws Exception {
    String txPublishDestination = newDestination("txPublish");
    String txConsumeDestination = newDestination("txConsume");

    publish(MESSAGE, txConsumeDestination);
    consumeAndPublishTx(txConsumeDestination, txPublishDestination, true);

    CoreEvent consume = consumeTx(txConsumeDestination, false);
    assertThat(consume.getMessage().getPayload().getValue(), is(MESSAGE));
    assertEmptyDestination(txPublishDestination);
  }

  private CoreEvent consumeAndPublishTx(String txConsumeDestination, String txPublishDestination, boolean rollback)
      throws Exception {
    return runFlowWithTxWrapper("txConsumeAndPublish", txPublishDestination, txConsumeDestination, rollback);
  }

  private CoreEvent consumeTx(String txConsumeDestination, boolean rollback) throws Exception {
    return runFlowWithTxWrapper("txConsume", txConsumeDestination, txConsumeDestination, rollback);
  }

  private CoreEvent publishTx(String txPublishDestination) throws Exception {
    return publishTx(txPublishDestination, false);
  }

  private CoreEvent publishTx(String txPublishDestination, boolean rollback) throws Exception {
    return runFlowWithTxWrapper("txPublish", txPublishDestination, null, rollback);
  }

  private CoreEvent runFlowWithTxWrapper(String flowName, String txPublishDestination, String txConsumeDestination,
                                         boolean shouldRollback)
      throws Exception {
    return flowRunner("executionWrapper")
        .withVariable("publishDestination", txPublishDestination)
        .withVariable("consumeDestination", txConsumeDestination)
        .withVariable("flowName", flowName)
        .withVariable("rollback", shouldRollback)
        .withPayload(MESSAGE)
        .run();
  }
}
