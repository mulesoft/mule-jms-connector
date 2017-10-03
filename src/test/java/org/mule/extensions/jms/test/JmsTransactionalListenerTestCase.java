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
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.TRANSACTION;

import org.mule.functional.api.exception.ExpectedError;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.junit4.rule.SystemPropertyLambda;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

@Feature(JMS_EXTENSION)
// TODO: Would it be better to call this story local transaction to differentiate it from XA transactions?
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
  //TODO: Allure desc
  public void txListenerWithPublishNotJoinedToTransaction() throws Exception {
    String message = buildMessage(TEST_MESSAGE, Actions.NOTHING);
    publish(message, listenerDestination.getValue(), MediaType.APPLICATION_JSON);

    ((Flow) getFlowConstruct("txListenerWithPublishNotJoin")).start();

    Message event = consume(publishDestination.getValue(), emptyMap(), 5000L);
    assertThat(event.getPayload().getValue(), is(message));
  }

  @Test
  //TODO: Allure desc
  @Ignore("A transaction is not available for this session, but transaction action is \"Always Join\"")
  public void txListenerWithPublishJoinedToTransaction() throws Exception {
    String message = buildMessage(TEST_MESSAGE, Actions.NOTHING);
    publish(message, listenerDestination.getValue(), MediaType.APPLICATION_JSON);

    ((Flow) getFlowConstruct("txListenerWithPublishJoin")).start();

    Message event = consume(publishDestination.getValue(), emptyMap(), 5000L);
    assertThat(event.getPayload().getValue(), is(message));
  }

  String buildMessage(String message, Actions action) {
    return "{\"message\" : \"" + message + "\", \"action\" : \"" + action + "\"}";
  }

  public enum Actions {
    EXPLODE, NOTHING
  }

}
