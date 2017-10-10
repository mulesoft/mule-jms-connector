/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test;

import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;

import org.mule.functional.api.exception.ExpectedError;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.tck.junit4.rule.SystemProperty;

import org.junit.Rule;
import org.junit.Test;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import javax.inject.Inject;
import javax.inject.Named;

@Feature(JMS_EXTENSION)
@Story("Transaction Support")
public class JmsTransactionalAlwaysJoinErrorTestCase extends JmsAbstractTestCase {

  private static final String MESSAGE = "MESSAGE";

  @Rule
  public ExpectedError expectedError = ExpectedError.none();

  @Rule
  public SystemProperty listenerDestination = new SystemProperty("destination", newDestination("destination"));

  @Rule
  public SystemProperty publishDestination = new SystemProperty("publishDestination", newDestination("publishDestination"));

  @Inject
  @Named("txListenerWithPublishAlwaysJoin")
  private Flow txListenerWithPublishAlwaysJoinFlow;

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"transactions/jms-transactional-always-join-error.xml", "config/activemq/activemq-default.xml"};
  }

  @Test
  public void txPublishAlwaysJoinWhenNotTxAvailable() throws Exception {
    expectedError.expectErrorType(any(String.class), is("UNKNOWN"));
    expectedError.expectMessage(containsString("A transaction is not available for this session"));

    runFlow("txPublishAlwaysJoin", newDestination("txPublishAlwaysJoin"));
  }

  @Test
  public void txConsumeAlwaysJoinWhenNotTxAvailable() throws Exception {
    expectedError.expectErrorType(any(String.class), is("UNKNOWN"));
    expectedError.expectMessage(containsString("A transaction is not available for this session"));

    runFlow("txConsumeAlwaysJoin", newDestination("txPublishAlwaysJoin"));
  }

  @Test
  public void txListenerWithPublishAlwaysJoinWhenNotTxAvailable() throws Exception {
    expectedError.expectErrorType(any(String.class), is("UNKNOWN"));
    expectedError.expectMessage(containsString("A transaction is not available for this session"));

    publish(MESSAGE, listenerDestination.getValue());
    txListenerWithPublishAlwaysJoinFlow.start();
    //    Message event = consume(publishDestination.getValue(), emptyMap(), 5000L);
    //    assertThat(event.getPayload().getValue(), is(MESSAGE));
  }

  private CoreEvent runFlow(String flowName, String destination) throws Exception {
    return flowRunner(flowName)
        .withVariable("destination", destination)
        .withPayload(MESSAGE)
        .run();
  }
}
