/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.topic.basic;

import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static java.util.Arrays.asList;
import org.mule.test.runner.RunnerDelegateTo;

import java.util.Collection;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;


@RunnerDelegateTo(Parameterized.class)
@Feature(JMS_EXTENSION)
@Story("Basic Publish and Subscribe operations test with ActiveMQ Connections")
public class ActiveMQBasicTopicTestCase extends JmsBasicTopicPublishAndSubscribe {

  @Parameter
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
    return new String[] {configFileName, DEFAULT_OPERATIONS_CONFIG, SUBSCRIBER_CONFIG};
  }
}
