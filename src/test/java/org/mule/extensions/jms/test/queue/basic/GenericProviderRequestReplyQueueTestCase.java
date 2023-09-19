/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.queue.basic;

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
@Story("Request-Reply pattern with publishConsume operation test with Queue and ActiveMQ Connections")
public class GenericProviderRequestReplyQueueTestCase extends JmsBaseQueueRequestReplyTestCase {

  @Parameter
  public String configName;

  @Parameter(1)
  public String configFileName;

  @Parameters(name = "{0}")
  public static Collection<Object[]> data() {
    return asList(new Object[][] {
        // TODO MULE-10962: migrate jndi-destinations-always.xml with custom JndiDestinationResolver
        {"jndi-destinations-never", "config/generic/jndi-destinations-never.xml"},
        {"jndi-destinations-try", "config/generic/jndi-destinations-try.xml"}
    });
  }

  @Override
  protected String[] getConfigFiles() {
    return new String[] {configFileName, DEFAULT_OPERATIONS_CONFIG, PUBLISH_CONSUME_OPERATIONS_CONFIG};
  }
}
