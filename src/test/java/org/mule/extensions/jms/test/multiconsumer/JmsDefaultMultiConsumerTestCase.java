/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.multiconsumer;

import static java.lang.Runtime.getRuntime;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mule.extensions.jms.api.destination.DestinationType.QUEUE;

import org.junit.Test;
import org.mule.test.runner.RunnerDelegateTo;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@Feature("JMS Extension")
@Story("Multi Consumers - JMS 1.x")
@RunnerDelegateTo()
public class JmsDefaultMultiConsumerTestCase extends AbstractParalellMultiConsumerTestCase {

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"multiconsumer/jms-default-multi-consumers.xml", "config/activemq/activemq-default.xml"};
  }

  @Override
  long getExpectedNumberOfConsumers() {
    return (long) getRuntime().availableProcessors();
  }
}
