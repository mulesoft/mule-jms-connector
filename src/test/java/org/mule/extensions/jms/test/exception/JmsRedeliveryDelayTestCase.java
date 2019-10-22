/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.exception;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.REDELIVERY;

import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.functional.api.exception.FunctionalTestException;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.junit4.rule.SystemPropertyLambda;
import org.mule.test.runner.RunnerDelegateTo;

import java.util.Collection;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@Feature(JMS_EXTENSION)
@Story(REDELIVERY)
@RunnerDelegateTo(Parameterized.class)
public class JmsRedeliveryDelayTestCase extends JmsAbstractTestCase {

  private static final long REDELIVERY_TIMEOUT = 1000;
  private static final int MAX_REDELIVERY_VALUE = 2;

  private static int numberOfDeliveries;

  @Parameter()
  public int initialRedeliveryDelay;

  @Parameter(1)
  public int redeliveryDelay;

  @Parameters(name = "initialRedeliveryDelay: {0} - redeliveryDelay: {1}")
  public static Collection<Object[]> data() {
    return asList(new Object[][] {
        {0, 1500},
        {1500, 0},
        {500, 1500}
    });
  }

  @Rule
  public SystemProperty destination = new SystemProperty("destination", newDestination("destination"));

  @Rule
  public SystemProperty maxRedelivery = new SystemProperty(MAX_REDELIVERY, String.valueOf(MAX_REDELIVERY_VALUE));

  @Rule
  public SystemProperty initialRedeliveryDelayProp =
      new SystemPropertyLambda("initial.redelivery.delay", () -> String.valueOf(initialRedeliveryDelay));

  @Rule
  public SystemProperty redeliveryDelayProp =
      new SystemPropertyLambda("redelivery.delay", () -> String.valueOf(redeliveryDelay));

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"config/activemq/activemq-redelivery-delay.xml", "exception/jms-redelivery-delay-flow.xml"};
  }

  @Before
  public void setup() {
    numberOfDeliveries = 0;
  }

  @Test
  @Description("Verifies that message redelivery does not take place before the redelivery delay time, but it does after")
  public void testNoRedeliveryBeforeDelay() throws Exception {
    publish(TEST_MESSAGE);
    int totalDelay = initialRedeliveryDelay + redeliveryDelay;
    await().atLeast(totalDelay, MILLISECONDS).atMost(totalDelay + REDELIVERY_TIMEOUT, MILLISECONDS)
        .until(() -> numberOfDeliveries > MAX_REDELIVERY_VALUE);
  }

  public static class TestProcessor implements Processor {

    @Override
    public CoreEvent process(CoreEvent event) throws MuleException {
      numberOfDeliveries++;
      throw new FunctionalTestException();
    }
  }

}
