/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.connection;

import static org.junit.rules.ExpectedException.none;
import static org.junit.rules.RuleChain.outerRule;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.RECONNECTION;

import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.extensions.jms.test.JmsMessageStorage;
import org.mule.extensions.jms.test.util.ActiveMQBroker;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.junit4.rule.SystemPropertyLambda;

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

  @Rule
  public SystemProperty listenerDestination =
      new SystemPropertyLambda("listenerDestination", () -> newDestination("listenerDestination"));

  @Rule
  public SystemProperty publishDestination =
      new SystemPropertyLambda("publishDestination", () -> newDestination("publishDestination"));

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
    publish(TEST_MESSAGE, listenerDestination.getValue());

    startFlow("reconnected-listener");
    assertMessageOnDestination(TEST_MESSAGE, publishDestination.getValue());

    restartBroker();
    publish(TEST_MESSAGE, listenerDestination.getValue());

    assertMessageOnDestination(TEST_MESSAGE, publishDestination.getValue());
  }

  @Test
  @Description("Verifies that consumers does not connect to the JMS after the configured number of tries has been" +
      " surpassed.")
  public void testReconnectionFail() throws Exception {
    publish(TEST_MESSAGE, listenerDestination.getValue());

    startFlow("fail-reconnect-listener");
    assertMessageOnDestination(TEST_MESSAGE, publishDestination.getValue());

    restartBroker();
    publish(TEST_MESSAGE, listenerDestination.getValue());

    assertEmptyDestination(publishDestination.getValue());
  }

  @Step("Start flow")
  private void startFlow(String flowName) throws Exception {
    ((Flow) getFlowConstruct(flowName)).start();
  }

  @Step("Restart JMS broker")
  private void restartBroker() throws InterruptedException {
    amqBroker.stop();
    Thread.sleep(3000);
    amqBroker.start();
  }

}
