/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.publish;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mule.extensions.jms.api.connection.JmsSpecification.JMS_2_0;
import static org.mule.runtime.api.util.Preconditions.checkArgument;
import static org.slf4j.LoggerFactory.getLogger;
import org.mule.extensions.jms.internal.support.JmsSupport;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

import org.slf4j.Logger;

/**
 * Wrapper implementation of a JMS {@link MessageProducer}
 *
 * @since 1.0
 */
public final class JmsMessageProducer implements AutoCloseable {

  private static final Logger LOGGER = getLogger(JmsMessageProducer.class);

  private final MessageProducer producer;
  private final JmsSupport jmsSupport;
  private final boolean isTopic;

  public JmsMessageProducer(JmsSupport jmsSupport, MessageProducer producer, boolean isTopic) {
    checkArgument(jmsSupport != null, "A non null JmsSupport implementation is required for publishing");
    checkArgument(producer != null, "A non null MessageProducer is required to use as delegate");

    this.producer = producer;
    this.jmsSupport = jmsSupport;
    this.isTopic = isTopic;
  }

  public void publish(Message message, PublisherParameters overrides)
      throws JMSException {

    java.util.Optional<Long> delay = resolveDeliveryDelay(overrides.getDeliveryDelay(), overrides.getDeliveryDelayUnit());
    long timeToLive = overrides.getTimeToLiveUnit().toMillis(overrides.getTimeToLive());

    configureProducer(delay, overrides.isDisableMessageId(), overrides.isDisableMessageTimestamp());

    jmsSupport.send(producer, message, overrides.isPersistentDelivery(), overrides.getPriority(), timeToLive, isTopic);
  }

  @Override
  public void close() throws JMSException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Closing producer: " + producer);
    }
    producer.close();
  }

  private java.util.Optional<Long> resolveDeliveryDelay(Long delay, TimeUnit delayUnit) {
    checkArgument(jmsSupport.getSpecification().equals(JMS_2_0) || delay == null,
                  format("[deliveryDelay] is only supported on [JMS 2.0] specification,"
                      + " but current configuration is set to [JMS %s]", jmsSupport.getSpecification().getName()));
    return delay != null ? of(delayUnit.toMillis(delay)) : empty();
  }

  private void configureProducer(Optional<Long> deliveryDelay, boolean dissableId, boolean dissableTimeStamp)
      throws JMSException {

    setDisableMessageID(dissableId);
    setDisableMessageTimestamp(dissableTimeStamp);
    deliveryDelay.ifPresent(this::setDeliveryDelay);
  }

  private void setDeliveryDelay(Long value) {
    try {
      producer.setDeliveryDelay(value);
    } catch (JMSException e) {
      LOGGER.error("Failed to configure [setDeliveryDelay] in MessageProducer: " + e.getMessage(), e);
    }
  }

  private void setDisableMessageID(boolean value) {
    try {
      producer.setDisableMessageID(value);
    } catch (JMSException e) {
      LOGGER.error("Failed to configure [setDisableMessageID] in MessageProducer: " + e.getMessage(), e);
    }
  }

  private void setDisableMessageTimestamp(boolean value) {
    try {
      producer.setDisableMessageTimestamp(value);
    } catch (JMSException e) {
      LOGGER.error("Failed to configure [setDisableMessageTimestamp] in MessageProducer: " + e.getMessage(), e);
    }
  }

}
