/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mule.functional.api.exception.ExpectedError;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.junit4.rule.SystemPropertyLambda;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.mule.extensions.jms.api.exception.JmsError.TIMEOUT;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.TRANSACTION;

@Feature(JMS_EXTENSION)
@Story(TRANSACTION)
public class JmsTransactionalListenerTestCase extends JmsAbstractTestCase {

  @Rule
  public ExpectedError expectedError = ExpectedError.none();

  @Rule
  public SystemProperty listenerDestination = new SystemPropertyLambda("destination", () -> newDestination("destination"));

  @Rule
  public SystemProperty publishDestination =
      new SystemPropertyLambda("publishDestination", () -> newDestination("publishDestination"));

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"transactions/jms-transactional-listener.xml", "config/activemq/activemq-default.xml"};
  }

  @Test
  @Description("Verifies that transactions started by listener work well even though subsequent element do not join them")
  public void txListenerWithPublishNotJoinedToTransaction() throws Exception {
    String message = publishMessage(Actions.NOTHING);

    ((Flow) getFlowConstruct("txListenerWithPublishNotJoin")).start();
    checkForMessageOnDestination(message, publishDestination.getValue());
    checkForEmptyDestination(listenerDestination.getValue());
  }

  @Test
  @Description("Verifies that rollback of transactions started by a listener does not involve element that did not join" +
      " them")
  @Ignore("Failed to retrieve the message from the publishing destination. I think this means that the publishing got " +
      "into the transaction, which is not the expected behavior (according to my understanding)")
  public void txListenerWithPublishNotJoinedToTransactionRolledBack() throws Exception {
    String message = publishMessage(Actions.EXPLODE);

    ((Flow) getFlowConstruct("txListenerWithPublishNotJoin")).start();

    checkForMessageOnDestination(message, publishDestination.getValue());
    checkForMessageOnDestination(message, listenerDestination.getValue());
  }

  @Test
  @Description("Verifies that transactions started by a listener work well when subsequent elements joined them")
  public void txListenerWithPublishJoinedToTransaction() throws Exception {
    String message = publishMessage(Actions.NOTHING);

    ((Flow) getFlowConstruct("txListenerWithPublishJoin")).start();

    checkForMessageOnDestination(message, publishDestination.getValue());
    checkForEmptyDestination(listenerDestination.getValue());
  }

  @Test
  @Description("Verifies that rollbacks works as expected when transaction are started by a listener and subsequent " +
      "elements joined them")
  public void txListenerWithPublishJoinedToTransactionRolledBack() throws Exception {
    String message = publishMessage(Actions.EXPLODE);

    ((Flow) getFlowConstruct("txListenerWithPublishJoin")).start();

    checkForEmptyDestination(publishDestination.getValue());
    checkForMessageOnDestination(message, listenerDestination.getValue());
  }

  @Step("Publish actionable message")
  private String publishMessage(Actions action) throws Exception {
    String message = "{\"message\" : \"" + TEST_MESSAGE + "\", \"action\" : \"" + action + "\"}";
    publish(message, listenerDestination.getValue(), MediaType.APPLICATION_JSON);
    return message;
  }

  @Step("Check for no messages on dest: {destination}")
  private void checkForEmptyDestination(String destination) throws Exception {
    expectedError.expectErrorType(any(String.class), is(TIMEOUT.getType()));
    consume(destination);
  }

  @Step("check for messages on dest: {destination}")
  private void checkForMessageOnDestination(String message, String destination) throws Exception {
    Message event = consume(destination);
    assertThat(event.getPayload().getValue(), is(message));
  }

  public enum Actions {
    EXPLODE, NOTHING
  }
}
