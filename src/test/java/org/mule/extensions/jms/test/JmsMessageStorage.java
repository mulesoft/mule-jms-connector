/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test;

import static org.mule.extensions.jms.test.JmsAbstractTestCase.POLL_DELAY_MILLIS;
import static org.mule.extensions.jms.test.JmsAbstractTestCase.TIMEOUT_MILLIS;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.tck.probe.JUnitLambdaProbe;
import org.mule.tck.probe.PollingProber;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JmsMessageStorage implements Processor {

  private static Queue<CoreEvent> EVENTS = new ConcurrentLinkedQueue<>();
  private static Queue<Message> MESSAGES = new ConcurrentLinkedQueue<>();

  @Override
  public CoreEvent process(CoreEvent event) {
    EVENTS.add(event);
    MESSAGES.add(event.getMessage());
    return event;
  }

  public static void cleanUpQueue() {
    MESSAGES = new ConcurrentLinkedQueue<>();
    EVENTS = new ConcurrentLinkedQueue<>();
  }

  public static Result<TypedValue<Object>, Object> pollMessage() {
    Message message = pollMuleMessage();
    return Result.<TypedValue<Object>, Object>builder()
        .output(message.getPayload())
        .attributes(message.getAttributes().getValue())
        .build();
  }

  public static Message pollMuleMessage() {
    return poll(MESSAGES);
  }

  public static CoreEvent pollEvent() {
    return poll(EVENTS);
  }

  private static <T> T poll(Queue<T> queue) {
    new PollingProber(TIMEOUT_MILLIS, POLL_DELAY_MILLIS).check(new JUnitLambdaProbe(() -> !queue.isEmpty()));
    return queue.poll();
  }

  public static int receivedMessages() {
    return MESSAGES.size();
  }
}
