/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test;

import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.mule.extensions.jms.api.exception.JmsError.TIMEOUT;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;

import org.mule.functional.api.exception.ExpectedError;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.tck.junit4.rule.SystemProperty;

import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Named;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@Feature(JMS_EXTENSION)
@Story("Transaction Support")
public class JmsTransactionalTestCase extends JmsAbstractTestCase {

  private static final String MESSAGE = "MESSAGE";

  @Rule
  public ExpectedError expectedError = ExpectedError.none();

  @Rule
  public SystemProperty listenerDestination = new SystemProperty("destination", newDestination("destination"));

  @Rule
  public SystemProperty publishDestination = new SystemProperty("publishDestination", newDestination("publishDestination"));

  @Inject
  @Named("txSubscriberWithPublish")
  private Flow txSubscriberWithPublishFlow;

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"transactions/jms-transactional.xml", "config/activemq/activemq-default.xml"};
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

    checkForEmptyDestination(txPublishDestination);
  }

  @Test
  public void txConsumer() throws Exception {
    String txConsumeDestination = newDestination("txConsume");
    publish(MESSAGE, txConsumeDestination);

    consumeTx(txConsumeDestination, false);
    checkForEmptyDestination(txConsumeDestination);
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
    checkForEmptyDestination(txPublishDestination);
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

  private void checkForEmptyDestination(String txPublishDestination) throws Exception {
    expectedError.expectErrorType(any(String.class), is(TIMEOUT.getType()));
    consumeTx(txPublishDestination, false);
  }
}
