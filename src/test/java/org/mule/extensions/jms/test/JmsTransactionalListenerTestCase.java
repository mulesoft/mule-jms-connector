/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;
import static org.mule.extensions.jms.api.exception.JmsError.TIMEOUT;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.TRANSACTION;
import static org.mule.functional.junit4.matchers.MessageMatchers.hasPayload;

import org.mule.functional.api.exception.ExpectedError;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.junit4.rule.SystemPropertyLambda;
import org.mule.test.runner.RunnerDelegateTo;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.Parameterized;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;

@Feature(JMS_EXTENSION)
@Story(TRANSACTION)
@RunnerDelegateTo(Parameterized.class)
public class JmsTransactionalListenerTestCase extends JmsAbstractTestCase {

  @Rule
  public ExpectedError expectedError = ExpectedError.none();

  @Parameter
  public Operations operation;

  @Rule
  public SystemProperty initialDestination =
      new SystemPropertyLambda("initialDestination", () -> newDestination("initialDestination"));

  @Rule
  public SystemProperty finalDestination =
      new SystemPropertyLambda("finalDestination", () -> newDestination("finalDestination"));

  @Rule
  public SystemProperty consumeDestination =
      new SystemPropertyLambda("consumeDestination", () -> newDestination("consumeDestination"));

  private String message;

  @Parameters(name = "operation:{0}")
  public static Collection<Object[]> data() {
    return asList(new Object[][] {
        {Operations.PUBLISH},
        //TODO: Review error: Cannot synchronously receive a message when a MessageListener is set.
        {Operations.CONSUME}
    });
  }

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"transactions/jms-transactional-listener.xml", "config/activemq/activemq-default.xml"};
  }

  @Test
  @Description("Verifies that transactions started by a listener work well when subsequent jms operation are set to " +
      "join if possible")
  public void txListenerWithDefaultTxActionOnNextOperation() throws Exception {
    message = buildMessage(operation, Actions.NOTHING);
    publishMessage(message, initialDestination.getValue());
    publishMessage(message, consumeDestination.getValue());

    ((Flow) getFlowConstruct("txListenerWithDefaultTxActionOnNextOperation")).start();

    assertSuccessfulExecution();
    checkForEmptyDestination(initialDestination.getValue());
  }

  @Test
  @Description("Verifies that rollbacks works as expected when transaction are started by a listener and subsequent " +
      "jms operations are set to join if possible")
  public void txListenerWithDefaultTxActionOnNextOperationRolledBack() throws Exception {
    message = buildMessage(operation, Actions.EXPLODE);
    publishMessage(message, initialDestination.getValue());
    publishMessage(message, consumeDestination.getValue());

    ((Flow) getFlowConstruct("txListenerWithDefaultTxActionOnNextOperation")).start();

    assertRollbackExecution();
    checkForMessageOnDestination(message, initialDestination.getValue());
  }

  @Test
  @Description("Verifies that transactions started by listener work well even though subsequent jms operation are set " +
      "to not join them")
  public void txListenerWithNotSupportedTxActionOnNextOperation() throws Exception {
    message = buildMessage(operation, Actions.NOTHING);
    publishMessage(message, initialDestination.getValue());
    publishMessage(message, consumeDestination.getValue());

    ((Flow) getFlowConstruct("txListenerWithNotSupportedTxActionOnNextOperation")).start();

    assertSuccessfulExecution();
    checkForEmptyDestination(initialDestination.getValue());
  }

  @Test
  @Description("Verifies that rollback of transactions started by a listener does not involve subsequent jms operation " +
      "set to not join them")
  @Ignore("MULE-13711")
  public void txListenerWithNotSupportedTxActionOnNextOperationRolledBack() throws Exception {
    message = buildMessage(operation, Actions.EXPLODE);
    publishMessage(message, initialDestination.getValue());
    publishMessage(message, consumeDestination.getValue());

    ((Flow) getFlowConstruct("txListenerWithNotSupportedTxActionOnNextOperation")).start();

    assertSuccessfulExecution();
    checkForMessageOnDestination(message, finalDestination.getValue());
  }

  @Test
  @Description("Verifies that transactions started by a listener work well when subsequent jms operation are set to " +
      "always join")
  public void txListenerAlwaysJoinTxActionOnNextOperation() throws Exception {
    message = buildMessage(operation, Actions.NOTHING);
    publishMessage(message, initialDestination.getValue());
    publishMessage(message, consumeDestination.getValue());

    ((Flow) getFlowConstruct("txListenerAlwaysJoinTxActionOnNextOperation")).start();

    assertSuccessfulExecution();
    checkForEmptyDestination(initialDestination.getValue());
  }

  @Test
  @Description("Verifies that rollbacks works as expected when transaction are started by a listener and subsequent " +
      "jms operations are set to always join")
  public void txListenerAlwaysJoinTxActionOnNextOperationRolledBack() throws Exception {
    message = buildMessage(operation, Actions.EXPLODE);
    publishMessage(message, initialDestination.getValue());
    publishMessage(message, consumeDestination.getValue());

    ((Flow) getFlowConstruct("txListenerAlwaysJoinTxActionOnNextOperation")).start();

    assertRollbackExecution();
    checkForMessageOnDestination(message, initialDestination.getValue());
  }


  @Step("Build actionable message")
  private String buildMessage(Operations operation, Actions action) {
    return "{\"message\" : \"" + TEST_MESSAGE + "\", \"operation\" : \"" + operation + "\", \"action\" : \"" + action
        + "\"}";
  }

  @Step("Publish message")
  private void publishMessage(String message, String destination) throws Exception {
    publish(message, destination, MediaType.APPLICATION_JSON);
  }

  @Step("Assert results on final message location based on operation")
  private void assertSuccessfulExecution() throws Exception {
    switch (operation) {
      case PUBLISH:
        checkForMessageOnDestination(message, finalDestination.getValue());
      case CONSUME:
        checkForEmptyDestination(consumeDestination.getValue());
    }
  }

  @Step("Assert rollback results on final message location based on operation")
  private void assertRollbackExecution() throws Exception {
    switch (operation) {
      case PUBLISH:
        checkForEmptyDestination(finalDestination.getValue());
      case CONSUME:
        checkForMessageOnDestination(message, consumeDestination.getValue());
    }
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

  public enum Actions {
    EXPLODE, NOTHING
  }

  public enum Operations {
    CONSUME, PUBLISH
  }
}
