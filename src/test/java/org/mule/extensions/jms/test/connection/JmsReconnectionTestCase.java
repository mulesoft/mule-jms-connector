/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.connection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.rules.ExpectedException.none;
import static org.junit.rules.RuleChain.outerRule;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.RECONNECTION;
import static org.mule.extensions.jms.test.JmsMessageStorage.pollMuleMessage;

import org.mule.extensions.jms.api.exception.JmsConsumeException;
import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.extensions.jms.test.JmsMessageStorage;
import org.mule.extensions.jms.test.util.ActiveMQBroker;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.runtime.core.api.exception.MessagingException;
import org.mule.tck.junit4.rule.SystemProperty;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;

@Feature(JMS_EXTENSION)
@Story(RECONNECTION)
public class JmsReconnectionTestCase extends JmsAbstractTestCase {

  public static final String RECONNECTED_LISTENER_FLOW = "reconnected-listener";

  public static final String RECONNECTION_FAIL_LISTENER_FLOW = "fail-reconnect-listener";

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

  private final String listenerDest = newDestination("listenerDest");
  private final String failedListenerDest = newDestination("failedListenerDest");
  private final String publishDest = newDestination("publishDest");

  @Rule
  public SystemProperty listenerDestProp = new SystemProperty("listenerDest", listenerDest);

  @Rule
  public SystemProperty failedListenerDestProp = new SystemProperty("failedListenerDest", failedListenerDest);

  @Rule
  public SystemProperty publishDestProp = new SystemProperty("publishDest", publishDest);

  @Rule
  public ExpectedException expected = none();

  @Before
  public void setUp() {
    JmsMessageStorage.cleanUpQueue();
  }

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"reconnect/jms-reconnection-config.xml", "config/activemq/activemq-reconnect.xml"};
  }

  @Test
  @Description("Verifies that all consumers get connected to the JMS broker after it has been reestablished.")
  public void testReconnection() throws Exception {
    destination = listenerDest;
    startFlow(RECONNECTED_LISTENER_FLOW);
    restartBroker();
    publish(TEST_MESSAGE);

    assertMessage(pollMuleMessage());
  }

  @Test
  @Description("Verifies that consumers does not connect to the JMS after the configured number of tries has been" +
      " surpassed.")
  public void testReconnectionFail() throws Exception {
    destination = failedListenerDest;
    expected.expect(MessagingException.class);
    expected.expectMessage("Failed to retrieve a Message, operation timed out");
    expected.expectCause(instanceOf(JmsConsumeException.class));

    startFlow(RECONNECTION_FAIL_LISTENER_FLOW);
    restartBroker();
    publish(TEST_MESSAGE);

    destination = publishDest;
    consume();
  }

  @Step("Start flow")
  private void startFlow(String flowName) throws Exception {
    ((Flow) getFlowConstruct(flowName)).start();
  }

  @Step("Restart JMS broker")
  private void restartBroker() throws InterruptedException {
    amqBroker.stop();
    Thread.sleep(500);
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
