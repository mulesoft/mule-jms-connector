/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.mule.extensions.jms.api.exception.JmsError.TIMEOUT;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.TRANSACTION;
import static org.mule.functional.junit4.matchers.MessageMatchers.hasPayload;

import org.junit.Ignore;
import org.mule.functional.api.exception.ExpectedError;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.junit4.rule.SystemPropertyLambda;

import org.junit.Rule;
import org.junit.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;

@Feature(JMS_EXTENSION)
@Story(TRANSACTION)
public class JmsTransactionalListenerPublishConsumeTestCase extends JmsAbstractTestCase {

  @Rule
  public ExpectedError expectedError = ExpectedError.none();

  @Rule
  public SystemProperty initialDestination =
      new SystemPropertyLambda("initialDestination", () -> newDestination("initialDestination"));

  @Rule
  public SystemProperty finalDestination =
      new SystemPropertyLambda("finalDestination", () -> newDestination("finalDestination"));

  private String message;

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"transactions/jms-transactional-listener-publish-consume.xml", "config/activemq/activemq-default.xml"};
  }

  @Test
  @Description("Verifies that transactions started by a listener work well when next element is a publish-consume" +
      " operation")
  public void txListenerWithDefaultTxActionOnNextOperation() throws Exception {
    message = buildMessage(Actions.NOTHING);
    publishMessage(message, initialDestination.getValue());

    ((Flow) getFlowConstruct("txListenerWithPublishConsume")).start();

    assertPublishedMessageAndReply();
    checkForEmptyDestination(initialDestination.getValue());
  }

  @Test
  @Description("Verifies that rollbacks works as expected when transaction are started by a listener and next " +
      "element is a publish-consume operation")
  @Ignore("TODO: Issue is still pending to be reported. According to @esteban.wasinger analysis a double rollback is" +
      " being performed, but we not sure yet if this is a connector or mule issue.")
  public void txListenerWithDefaultTxActionOnNextOperationRolledBack() throws Exception {
    message = buildMessage(Actions.EXPLODE);
    publishMessage(message, initialDestination.getValue());

    ((Flow) getFlowConstruct("txListenerWithPublishConsume")).start();

    assertPublishedMessageAndReply();
    checkForMessageOnDestination(message, initialDestination.getValue());
  }

  @Step("Build actionable message")
  private String buildMessage(Actions action) {
    return "{\"message\" : \"" + TEST_MESSAGE + "\", \"action\" : \"" + action + "\"}";
  }

  @Step("Publish message")
  private void publishMessage(String message, String destination) throws Exception {
    publish(message, destination, MediaType.APPLICATION_JSON);
  }

  @Step("Check for no messages on dest: {destination}")
  private void checkForEmptyDestination(String destination) throws Exception {
    expectedError.expectErrorType(any(String.class), is(TIMEOUT.getType()));
    consume(destination);
  }

  @Step("Check for messages on dest: {destination}")
  private void checkForMessageOnDestination(String message, String destination) throws Exception {
    assertThat(consume(destination), hasPayload(equalTo(message)));
  }

  @Step("Verifies that message was published on final destination and send the response back to replyTo dest")
  private void assertPublishedMessageAndReply() throws Exception {
    Message message = consume(finalDestination.getValue());
    assertThat(message, hasPayload(equalTo(this.message)));
    publish(TEST_MESSAGE, getReplyDestination(message));
  }

  public enum Actions {
    EXPLODE, NOTHING
  }
}
