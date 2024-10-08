/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.message;

import org.mule.extensions.jms.internal.ExcludeFromGeneratedCoverage;
import org.mule.jms.commons.api.destination.JmsDestination;
import org.mule.jms.commons.api.message.JmsHeadersBuilder;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import javax.jms.DeliveryMode;

/**
 * JMS header fields contain values used by both clients and providers to identify and route messages.
 * A message's complete header is transmitted to all JMS clients that receive the message.
 *
 * @since 1.0
 */
public class JmsHeaders extends org.mule.jms.commons.api.message.JmsHeaders {

  /**
   * The destination from which the JMS Message was received.
   */
  @Parameter
  private JmsDestination destination;

  /**
   * The delivery mode specified when the message was sent, which
   * can be either {@link DeliveryMode#NON_PERSISTENT} with value '1'
   * or {@link DeliveryMode#PERSISTENT} with value '2'
   */
  @Parameter
  private int deliveryMode;

  /**
   * The message's expiration time or {@code zero} if the message does not expire.
   *
   * JMS provider calculates its expiration time by adding the {@code timeToLive}
   * value specified on the send method to the time the message was sent (for transacted sends,
   * this is the time the client sends the message, not the time the transaction is committed)
   * <p>
   * If the {@code timeToLive} is specified as {@code zero}, the message's expiration time is
   * set to zero to indicate that the message does not expire.
   */
  @Parameter
  private long expiration;

  /**
   * The message priority level.
   *
   * JMS defines a ten level priority value with '0' as the lowest priority and '9' as the highest.
   * In addition, clients should consider priorities 0-4 as gradations of {@code normal} priority
   * and priorities 5-9 as gradations of {@code expedited} priority.
   * <p>
   * JMS does not require that a provider strictly implement priority ordering of messages;
   * however, it should do its best to deliver expedited messages ahead of normal messages.
   */
  @Parameter
  private int priority;

  /**
   * A value that uniquely identifies each message sent by a provider.
   *
   * If the Producer implementation supports optimizations and was configured to
   * avoid creating the Message ID using the {@code disableMessageID} parameter,
   * then this header will be {@code null}.
   */
  @Parameter
  @Optional
  private String messageId;

  /**
   * The time a message was handed off to a provider to be sent.
   * It is not the time the message was actually transmitted because the actual send
   * may occur later due to transactions or other client side queueing of messages.
   * <p>
   * If the Producer implementation supports optimizations and was configured to
   * avoid creating the Message timestamp using the {@code disableMessageTimestamp} parameter,
   * then {@code zero} is set.
   */
  @Parameter
  private long timestamp;

  /**
   * The message correlationId, used to link one message with another.
   *
   * A typical use is to link a response message with its request message, using its messageID
   */
  @Parameter
  @Optional
  private String correlationId;

  /**
   * The name of the Destination supplied by a client when a message is sent,
   * where a reply to the message should be sent.
   * If no {@code replyTo} destination was set, then {@code null} is set.
   */
  @Parameter
  @Optional
  private JmsDestination replyTo;

  /**
   * A message type identifier supplied by a client when a message is sent.
   */
  @Parameter
  private String type;

  /**
   * If {@code true}, it is likely, but not guaranteed,
   * that this message was delivered but not acknowledged in the past.
   * Relates to the {@code JMSXDeliveryCount} message property.
   *
   * {@code true} if the message may have been delivered in the past
   */
  @Parameter
  private boolean redelivered;

  /**
   * Present only in JMS 2.0 Messages.
   * It's the message's delivery time or {@code null} if not using JMS 2.0
   *
   * <p>
   * JMS provider calculates its delivery time by adding the {@code deliveryDelay}
   * value specified on the send method to the time the message was sent (for transacted sends,
   * this is the time the client sends the message, not the time the transaction is committed).
   * <p>
   * A message's delivery time is the earliest time when a provider may make the message visible
   * on the target destination and available for delivery to consumers.
   */
  @Parameter
  @Optional
  private Long deliveryTime;

  public JmsHeaders() {}

  public String getJMSMessageID() {
    return messageId;
  }

  @ExcludeFromGeneratedCoverage
  public void setJMSMessageID(String messageId) {
    this.messageId = messageId;
  }

  @ExcludeFromGeneratedCoverage
  public long getJMSTimestamp() {
    return timestamp;
  }

  @ExcludeFromGeneratedCoverage
  public void setJMSTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  @ExcludeFromGeneratedCoverage
  public String getJMSCorrelationID() {
    return correlationId;
  }

  @ExcludeFromGeneratedCoverage
  public void setJMSCorrelationID(String correlationId) {
    this.correlationId = correlationId;
  }

  @ExcludeFromGeneratedCoverage
  public JmsDestination getJMSReplyTo() {
    return replyTo;
  }

  @ExcludeFromGeneratedCoverage
  public void setJMSReplyTo(JmsDestination replyTo) {
    this.replyTo = replyTo;
  }

  @ExcludeFromGeneratedCoverage
  public JmsDestination getJMSDestination() {
    return destination;
  }

  @ExcludeFromGeneratedCoverage
  public void setJMSDestination(JmsDestination destination) {
    this.destination = destination;
  }

  @ExcludeFromGeneratedCoverage
  public int getJMSDeliveryMode() {
    return deliveryMode;
  }

  @ExcludeFromGeneratedCoverage
  public void setJMSDeliveryMode(int deliveryMode) {
    this.deliveryMode = deliveryMode;
  }

  @ExcludeFromGeneratedCoverage
  public boolean getJMSRedelivered() {
    return redelivered;
  }

  @ExcludeFromGeneratedCoverage
  public void setJMSRedelivered(boolean redelivered) {
    this.redelivered = redelivered;
  }

  @ExcludeFromGeneratedCoverage
  public String getJMSType() {
    return type;
  }

  @ExcludeFromGeneratedCoverage
  public void setJMSType(String type) {
    this.type = type;
  }

  @ExcludeFromGeneratedCoverage
  public long getJMSExpiration() {
    return expiration;
  }

  @ExcludeFromGeneratedCoverage
  public void setJMSExpiration(long expiration) {
    this.expiration = expiration;
  }

  @ExcludeFromGeneratedCoverage
  public Long getJMSDeliveryTime() {
    return deliveryTime;
  }

  @ExcludeFromGeneratedCoverage
  public void setJMSDeliveryTime(Long deliveryTime) {
    this.deliveryTime = deliveryTime;
  }

  @ExcludeFromGeneratedCoverage
  public int getJMSPriority() {
    return priority;
  }

  @ExcludeFromGeneratedCoverage
  public void setJMSPriority(int priority) {
    this.priority = priority;
  }

  public static class Builder implements JmsHeadersBuilder {

    private JmsHeaders jmsHeaders = new JmsHeaders();

    public JmsHeaders.Builder setMessageId(String messageId) {
      jmsHeaders.messageId = messageId;
      return this;
    }

    public JmsHeaders.Builder setTimestamp(long timestamp) {
      jmsHeaders.timestamp = timestamp;
      return this;
    }

    public JmsHeaders.Builder setCorrelationId(String correlationId) {
      jmsHeaders.correlationId = correlationId;
      return this;
    }

    public JmsHeaders.Builder setReplyTo(JmsDestination replyTo) {
      jmsHeaders.replyTo = replyTo;
      return this;
    }

    public JmsHeaders.Builder setDestination(JmsDestination destination) {
      jmsHeaders.destination = destination;
      return this;
    }

    public JmsHeaders.Builder setDeliveryMode(int deliveryMode) {
      jmsHeaders.deliveryMode = deliveryMode;
      return this;
    }

    public JmsHeaders.Builder setRedelivered(boolean redelivered) {
      jmsHeaders.redelivered = redelivered;
      return this;
    }

    public JmsHeaders.Builder setType(String type) {
      jmsHeaders.type = type;
      return this;
    }

    public JmsHeaders.Builder setExpiration(long expiration) {
      jmsHeaders.expiration = expiration;
      return this;
    }

    public JmsHeaders.Builder setPriority(int priority) {
      jmsHeaders.priority = priority;
      return this;
    }

    public JmsHeaders.Builder setDeliveryTime(long deliveryTime) {
      jmsHeaders.deliveryTime = deliveryTime;
      return this;
    }

    public JmsHeaders build() {
      return jmsHeaders;
    }
  }

  @Override
  public JmsDestination getDestination() {
    return destination;
  }

  @Override
  public void setDestination(JmsDestination destination) {
    this.destination = destination;
  }

  @Override
  public int getDeliveryMode() {
    return deliveryMode;
  }

  @Override
  public void setDeliveryMode(int deliveryMode) {
    this.deliveryMode = deliveryMode;
  }

  @Override
  public long getExpiration() {
    return expiration;
  }

  @Override
  public void setExpiration(long expiration) {
    this.expiration = expiration;
  }

  @Override
  public int getPriority() {
    return priority;
  }

  @Override
  public void setPriority(int priority) {
    this.priority = priority;
  }

  @Override
  public String getMessageId() {
    return messageId;
  }

  @Override
  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  @Override
  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public String getCorrelationId() {
    return correlationId;
  }

  @Override
  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  @Override
  public JmsDestination getReplyTo() {
    return replyTo;
  }

  @Override
  public void setReplyTo(JmsDestination replyTo) {
    this.replyTo = replyTo;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean isRedelivered() {
    return redelivered;
  }

  @Override
  public void setRedelivered(boolean redelivered) {
    this.redelivered = redelivered;
  }

  @Override
  public Long getDeliveryTime() {
    return deliveryTime;
  }

  @Override
  public void setDeliveryTime(Long deliveryTime) {
    this.deliveryTime = deliveryTime;
  }
}
