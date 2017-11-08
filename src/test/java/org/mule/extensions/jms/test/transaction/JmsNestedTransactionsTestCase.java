/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.transaction;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.TRANSACTION;

import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.api.notification.ExceptionNotificationListener;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.tck.junit4.rule.SystemProperty;

import java.util.concurrent.CountDownLatch;

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
  @Description("Verifies that nested TXs are not supported")
  public void nestedTx() throws Exception {
    final CountDownLatch latch = new CountDownLatch(1);
    notificationListenerRegistry.registerListener((ExceptionNotificationListener) notification -> {
      if (notification.getException().getMessage().contains("Non-XA transactions can't be nested")) {
        latch.countDown();
      }
    });

    publishMessage(TEST_MESSAGE, listenerDestination.getValue());
    nestedTxFlow.start();

    assertThat(latch.await(2, SECONDS), is(true));
  }

  @Step("Publish message")
  private void publishMessage(String message, String destination) throws Exception {
    publish(message, destination, MediaType.APPLICATION_JSON);
  }

}
