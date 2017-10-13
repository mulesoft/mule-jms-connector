/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.transaction;

import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.TRANSACTION;
import static org.mule.extensions.jms.test.transaction.JmsNestedTransactionsTestCase.Actions.EXPLODE;
import static org.mule.extensions.jms.test.transaction.JmsNestedTransactionsTestCase.Actions.NOTHING;

import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.tck.junit4.rule.SystemProperty;

import javax.inject.Inject;
import javax.inject.Named;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.junit.Rule;
import org.junit.Test;

@Feature(JMS_EXTENSION)
@Story(TRANSACTION)
public class JmsNestedTransactionsTestCase extends JmsAbstractTestCase {

  @Rule
  public SystemProperty listenerDestination = new SystemProperty("listenerDestination", newDestination("listenerDestination"));

  @Rule
  public SystemProperty publishDestination = new SystemProperty("publishDestination", newDestination("publishDestination"));

  @Inject
  @Named("nestedTx")
  private Flow nestedTxFlow;

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"transactions/jms-nested-transactions.xml", "config/activemq/activemq-default.xml"};
  }

  @Test
  @Description("Verifies that there is no issues when beginning two transactions on the same flow")
  public void nestedTx() throws Exception {
    String message = buildMessage(NOTHING);
    publishMessage(message, listenerDestination.getValue());
    nestedTxFlow.start();

    assertEmptyDestination(listenerDestination.getValue());
    assertMessageOnDestination(message, publishDestination.getValue());
  }

  @Test
  @Description("Verifies that there is no issues when beginning two transactions on the same flow")
  public void nestedTxRollback() throws Exception {
    String message = buildMessage(EXPLODE);
    publishMessage(message, listenerDestination.getValue());
    nestedTxFlow.start();

    assertEmptyDestination(listenerDestination.getValue());
    assertEmptyDestination(publishDestination.getValue());
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
