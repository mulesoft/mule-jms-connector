/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.message;

import org.mule.extensions.jms.api.destination.JmsDestination;
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

  public String getJMSMessageID() {
    return messageId;
  }

  public long getJMSTimestamp() {
    return timestamp;
  }

  public String getJMSCorrelationID() {
    return correlationId;
  }

  public JmsDestination getJMSReplyTo() {
    return replyTo;
  }

  public JmsDestination getJMSDestination() {
    return destination;
  }

  public int getJMSDeliveryMode() {
    return deliveryMode;
  }

  public boolean getJMSRedelivered() {
    return redelivered;
  }

  public String getJMSType() {
    return type;
  }

  public long getJMSExpiration() {
    return expiration;
  }

  public Long getJMSDeliveryTime() {
    return deliveryTime;
  }

  public int getJMSPriority() {
    return priority;
  }

  public static class Builder {

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

}
