/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.connection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.rules.RuleChain.outerRule;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.RECONNECTION;
import static org.mule.extensions.jms.test.JmsMessageStorage.pollMuleMessage;

import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.extensions.jms.test.util.ActiveMQBroker;
import org.mule.runtime.api.message.Message;
import org.mule.tck.junit4.rule.SystemProperty;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

@Feature(JMS_EXTENSION)
@Story(RECONNECTION)
public class JmsReconnectForeverTestCase extends JmsAbstractTestCase {

  private static ActiveMQBroker amqBroker = new ActiveMQBroker("amqPort") {

    @Override
    protected void before() throws Throwable {
      amqBroker.start();
    }

    @Override
    protected void after() {
      amqBroker.stop();
    }
  };

  private static SystemProperty amqUrl = new SystemProperty("amq.url", amqBroker.getConnectorUrl());

  @ClassRule
  public static TestRule chain = outerRule(amqBroker).around(amqUrl);

  private final String testDest = newDestination("reconnectionTest");

  @Rule
  public SystemProperty listenerDestination = new SystemProperty("destination", testDest);

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"reconnect/jms-reconnect-forever-config.xml", "config/activemq/activemq-reconnect.xml"};
  }

  @Test
  @Description("Verifies that all consumers get connected once the JMS broker access has been reestablished")
  public void testReconnection() throws Exception {
    destination = testDest;

    restartBroker();

    publish(TEST_MESSAGE);

    assertMessage(pollMuleMessage());
  }

  @Step("Restart JMS broker")
  private void restartBroker() throws InterruptedException {
    amqBroker.stop();
    Thread.sleep(1000);
    amqBroker.start();
  }

  @Step("Assert message")
  private void assertMessage(Message message) {
    assertThat(message, not(nullValue()));
    assertThat(message.getPayload(), not(nullValue()));
    assertThat(message.getPayload().getValue(), is(equalTo(TEST_MESSAGE)));
    assertThat(message.getAttributes(), not(nullValue()));
  }

}
