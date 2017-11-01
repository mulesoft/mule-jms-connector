/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.transaction;

import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.TRANSACTION;

import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.runtime.api.metadata.MediaType;
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
@Story(TRANSACTION)
public class JmsTransactionalListenerTestCase extends JmsAbstractTestCase {

  @Rule
  public SystemProperty initialDestination =
      new SystemPropertyLambda("initialDestination", () -> newDestination("initialDestination"));

  @Rule
  public SystemProperty finalDestination = new SystemPropertyLambda("finalDestination", () -> newDestination("finalDestination"));

  @Rule
  public SystemProperty maxRedelivery = new SystemProperty(MAX_REDELIVERY, "6");

  private String message;

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"transactions/jms-transactional-listener.xml", "config/activemq/activemq-default.xml"};
  }

  @Test
  @Description("Verifies that transactions started by a listener work well when next jms operation is set to join if" +
      " possible")
  public void txListenerWithDefaultTxActionOnNextOperation() throws Exception {
    message = buildMessage(Actions.NOTHING);
    publishMessage(message, initialDestination.getValue());

    ((Flow) getFlowConstruct("txListenerWithDefaultTxActionOnNextOperation")).start();

    assertMessageOnDestination(message, finalDestination.getValue());
    assertEmptyDestination(initialDestination.getValue());
  }

  @Test
  @Description("Verifies that rollbacks work as expected when transactions are started by a listener and next jms " +
      "operation is set to join if possible")
  public void txListenerWithDefaultTxActionOnNextOperationRolledBack() throws Exception {
    message = buildMessage(Actions.EXPLODE);
    publishMessage(message, initialDestination.getValue());

    ((Flow) getFlowConstruct("txListenerWithDefaultTxActionOnNextOperation")).start();

    assertEmptyDestination(finalDestination.getValue());
    assertMessageOnDestination(message, initialDestination.getValue());
  }

  @Test
  @Description("Verifies that transactions started by listener work well even though next jms operation is set to not" +
      " join them")
  public void txListenerWithNotSupportedTxActionOnNextOperation() throws Exception {
    message = buildMessage(Actions.NOTHING);
    publishMessage(message, initialDestination.getValue());

    ((Flow) getFlowConstruct("txListenerWithNotSupportedTxActionOnNextOperation")).start();

    assertMessageOnDestination(message, finalDestination.getValue());
    assertEmptyDestination(initialDestination.getValue());
  }

  @Test
  @Description("Verifies that rollback of transactions started by a listener does not involve next jms operation" +
      " if set not to join them")
  public void txListenerWithNotSupportedTxActionOnNextOperationRolledBack() throws Exception {
    message = buildMessage(Actions.EXPLODE);
    publishMessage(message, initialDestination.getValue());

    ((Flow) getFlowConstruct("txListenerWithNotSupportedTxActionOnNextOperation")).start();
    assertMessageOnDestination(message, finalDestination.getValue());
  }

  @Test
  @Description("Verifies that transactions started by a listener work well when next jms operation is set to always" +
      " join")
  public void txListenerAlwaysJoinTxActionOnNextOperation() throws Exception {
    message = buildMessage(Actions.NOTHING);
    publishMessage(message, initialDestination.getValue());

    ((Flow) getFlowConstruct("txListenerAlwaysJoinTxActionOnNextOperation")).start();

    assertMessageOnDestination(message, finalDestination.getValue());
    assertEmptyDestination(initialDestination.getValue());
  }

  @Test
  @Description("Verifies that rollbacks works as expected when transaction are started by a listener and next jms " +
      "operation is set to always join")
  public void txListenerAlwaysJoinTxActionOnNextOperationRolledBack() throws Exception {
    message = buildMessage(Actions.EXPLODE);
    publishMessage(message, initialDestination.getValue());

    ((Flow) getFlowConstruct("txListenerAlwaysJoinTxActionOnNextOperation")).start();

    assertEmptyDestination(finalDestination.getValue());
    assertMessageOnDestination(message, initialDestination.getValue());
  }

  @Step("Build actionable message")
  private String buildMessage(Actions action) {
    return "{\"message\" : \"" + TEST_MESSAGE + "\", \"action\" : \"" + action + "\"}";
  }

  @Step("Publish message")
  private void publishMessage(String message, String destination) throws Exception {
    publish(message, destination, MediaType.APPLICATION_JSON);
  }

  public enum Actions {
    EXPLODE, NOTHING
  }
}
