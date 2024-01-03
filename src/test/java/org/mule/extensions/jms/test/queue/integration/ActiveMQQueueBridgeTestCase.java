/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.queue.integration;

import static java.util.Arrays.asList;
import static org.junit.runners.Parameterized.Parameter;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import org.mule.test.runner.RunnerDelegateTo;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import java.util.Collection;


@RunnerDelegateTo(Parameterized.class)
@Feature(JMS_EXTENSION)
@Story("ActiveMQ Connection Provider Queue Bridge")
public class ActiveMQQueueBridgeTestCase extends JmsAbstractQueueBridge {

  @Parameter(0)
  public String configName;

  @Parameter(1)
  public String configFileName;

  @Parameters(name = "{0}")
  public static Collection<Object[]> data() {
    return asList(new Object[][] {
        {"activemq-default", "config/activemq/activemq-default.xml"},
        {"activemq-default-no-caching", "config/activemq/activemq-default-no-caching.xml"},
        {"activemq-default-user-pass", "config/activemq/activemq-default-user-pass.xml"},
        {"activemq-with-overrides", "config/activemq/activemq-with-overrides.xml"}
    });
  }

  @Override
  protected String[] getConfigFiles() {
    return new String[] {configFileName, BRIDGE_CONFIG_XML};
  }

}
