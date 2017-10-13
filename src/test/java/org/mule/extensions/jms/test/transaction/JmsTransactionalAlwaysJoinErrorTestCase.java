/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.transaction;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.TRANSACTION;

import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.runtime.api.notification.ExceptionNotificationListener;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.tck.junit4.rule.SystemProperty;

import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;
import javax.inject.Named;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.Rule;
import org.junit.Test;

@Feature(JMS_EXTENSION)
@Story(TRANSACTION)
public class JmsTransactionalAlwaysJoinErrorTestCase extends JmsAbstractTestCase {

  private static final String MESSAGE = "MESSAGE";

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
    final CountDownLatch latch = new CountDownLatch(1);
    notificationListenerRegistry.registerListener((ExceptionNotificationListener) notification -> {
      if (notification.getException().getMessage().contains("A transaction is not available for this session")) {
        latch.countDown();
      }
    });

    publish(MESSAGE, listenerDestination.getValue());
    txListenerWithPublishAlwaysJoinFlow.start();

    assertThat(latch.await(2, SECONDS), is(true));
  }

  private CoreEvent runFlow(String flowName, String destination) throws Exception {
    return flowRunner(flowName)
        .withVariable("destination", destination)
        .withPayload(MESSAGE)
        .run();
  }
}
