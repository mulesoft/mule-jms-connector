/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.config;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

import org.mule.extensions.jms.api.ack.AckMode;
import org.mule.extensions.jms.api.destination.ConsumerType;
import org.mule.extensions.jms.api.destination.QueueConsumer;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import java.util.Objects;

import javax.jms.Message;

/**
 * Configuration parameters for consuming messages from a JMS Queue or Topics
 *
 * @since 1.0
 */
public final class JmsConsumerConfig implements org.mule.jms.commons.api.config.JmsConsumerConfig {

  /**
   * The {@link ConsumerAckMode} to use when consuming a {@link Message}
   * Can be overridden at the message source level.
   * This attribute has to be IMMEDIATE if transactionType is LOCAL or MULTI
   */
  @Parameter
  @Optional(defaultValue = "AUTO")
  @Expression(NOT_SUPPORTED)
  @Summary("The Session ACK mode to use when consuming a message")
  private AckMode ackMode;

  /**
   * The {@link ConsumerType} to be used by default when consuming a {@link Message}
   * Can be overridden at the message source level.
   */
  @Parameter
  @Optional
  @Expression(NOT_SUPPORTED)
  @ParameterDsl(allowReferences = false)
  @NullSafe(defaultImplementingType = QueueConsumer.class)
  private ConsumerType consumerType;

  /**
   * Default selector to be used for filtering when consuming a {@link Message}
   * Can be overridden at the message source level.
   */
  @Parameter
  @Optional
  @Expression(NOT_SUPPORTED)
  @Summary("Default JMS selector to be used for filtering incoming messages")
  private String selector;

  /**
   * Used to configure the number of redelivers before discarding the message.
   * No redelivery is represented with 0, while -1 means infinite re deliveries accepted.
   */
  @Parameter
  @Optional(defaultValue = "0")
  @Expression(NOT_SUPPORTED)
  // TODO MULE-10958: duplicated in ActiveMQ for default factory creation
  private int maxRedelivery;

  public int getMaxRedelivery() {
    return maxRedelivery;
  }

  public String getSelector() {
    return selector;
  }

  public ConsumerType getConsumerType() {
    return consumerType;
  }

  public AckMode getAckMode() {
    return ackMode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    JmsConsumerConfig that = (JmsConsumerConfig) o;
    return maxRedelivery == that.maxRedelivery &&
        ackMode == that.ackMode &&
        Objects.equals(consumerType, that.consumerType) &&
        Objects.equals(selector, that.selector);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ackMode, consumerType, selector, maxRedelivery);
  }
}
