/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.multiconsumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mule.extensions.jms.api.destination.DestinationType.QUEUE;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Rule;
import org.junit.Test;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.tck.junit4.rule.SystemProperty;

public abstract class AbstractParalellMultiConsumerTestCase extends AbstractJmsMultiConsumerTestCase {

  @Rule
  public SystemProperty maxRedelivery = new SystemProperty(MAX_REDELIVERY, "1");

  @Inject
  @Named("topicListener")
  protected Flow topicListenerFlow;

  @Test
  public void multiConsumersConsumeMessagesInParallel() throws Exception {
    publishTo(NUMBER_OF_MESSAGES, destination.getValue(), QUEUE);

    long distinctAckIds = getMessages(NUMBER_OF_MESSAGES)
        .map(result -> evaluate("#[payload.ackId]", result.getAttributes()))
        .distinct()
        .count();

    assertThat(distinctAckIds, is(getExpectedNumberOfConsumers()));
  }

  abstract long getExpectedNumberOfConsumers();
}
