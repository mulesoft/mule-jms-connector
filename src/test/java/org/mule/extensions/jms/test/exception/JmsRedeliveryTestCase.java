/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.exception;

import static java.util.Arrays.asList;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;
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

@Feature(JMS_EXTENSION)
@Story(REDELIVERY)
@RunnerDelegateTo(Parameterized.class)
public class JmsRedeliveryTestCase extends JmsAbstractTestCase {

  private static final long REDELIVERY_TIMEOUT = 90000;
  private static final int POLL_DELAY_MILLIS = 100;
  public static final int REDELIVERY_IGNORE = -1;
  public static final int EXPECTED_REDELIVERS_WHEN_MAX_IGNORED = 5;

  private static int numberOfDeliveries;

  @Parameter
  public int maxRedeliveryAttempts;

  @Parameters(name = "maxRedelivery: {0}")
  public static Collection<Object[]> data() {
    return asList(new Object[][] {{0}, {3}, {REDELIVERY_IGNORE}});
  }

  @Rule
  public SystemProperty destination = new SystemProperty("destination", newDestination("destination"));

  @Rule
  public SystemProperty maxRedelivery = new SystemPropertyLambda(MAX_REDELIVERY, this::getMaxRedeliveryAttempts);

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"config/activemq/activemq-default.xml", "exception/jms-redelivery-flow.xml"};
  }

  @Before
  public void setup() {
    numberOfDeliveries = 0;
  }

  @Test
  @Description("verifies that message redelivery attempts correspond to the configured value")
  public void testMaxRedelivery() throws Exception {
    publish(TEST_MESSAGE);
    validate(this::hasReachedNumberOfExpectedRedelivers, REDELIVERY_TIMEOUT, POLL_DELAY_MILLIS);
  }

  private boolean hasReachedNumberOfExpectedRedelivers() {
    if (maxRedeliveryAttempts == REDELIVERY_IGNORE) {
      return numberOfDeliveries > EXPECTED_REDELIVERS_WHEN_MAX_IGNORED;
    } else {
      return numberOfDeliveries == maxRedeliveryAttempts + 1;
    }
  }

  private String getMaxRedeliveryAttempts() {
    return String.valueOf(maxRedeliveryAttempts);
  }

  public static class TestProcessor implements Processor {

    @Override
    public CoreEvent process(CoreEvent event) throws MuleException {
      numberOfDeliveries++;
      throw new FunctionalTestException();
    }
  }

}
