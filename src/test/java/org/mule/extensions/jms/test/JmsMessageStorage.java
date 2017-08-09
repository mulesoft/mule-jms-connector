/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test;

import static org.mule.extensions.jms.test.JmsAbstractTestCase.POLL_DELAY_MILLIS;
import static org.mule.extensions.jms.test.JmsAbstractTestCase.TIMEOUT_MILLIS;

import org.mule.extensions.jms.api.message.JmsAttributes;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.InternalEvent;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.tck.probe.JUnitLambdaProbe;
import org.mule.tck.probe.PollingProber;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JmsMessageStorage implements Processor {

  private static Queue<Message> messages = new ConcurrentLinkedQueue<>();

  @Override
  public InternalEvent process(InternalEvent event) throws MuleException {
    messages.add(event.getMessage());
    return event;
  }

  public static void cleanUpQueue() {
    messages = new ConcurrentLinkedQueue<>();
  }

  public static Result<TypedValue<Object>, JmsAttributes> pollMessage() {
    Message message = pollMuleMessage();
    return Result.<TypedValue<Object>, JmsAttributes>builder()
        .output(message.getPayload())
        .attributes((JmsAttributes) message.getAttributes().getValue())
        .build();
  }

  public static Message pollMuleMessage() {
    new PollingProber(TIMEOUT_MILLIS, POLL_DELAY_MILLIS).check(new JUnitLambdaProbe(() -> !messages.isEmpty()));
    return messages.poll();
  }

  public static int receivedMessages() {
    return messages.size();
  }
}
