/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.source;

import org.mule.extensions.jms.api.destination.JmsDestination;
import org.mule.extensions.jms.api.message.JmsMessageBuilder;
import org.mule.jms.commons.api.RequestReplyPattern;
import org.mule.runtime.extension.api.annotation.param.ConfigOverride;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.parameter.OutboundCorrelationStrategy;

import java.util.concurrent.TimeUnit;

import javax.jms.Message;

/**
 * Enables the creation of an outgoing {@link Message} along with the response configuration.
 * Users must use this builder to create a message response instance.
 *
 * @since 1.0
 */
public class JmsResponseMessageBuilder extends JmsMessageBuilder
    implements org.mule.jms.commons.internal.source.JmsResponseMessageBuilder<JmsDestination> {

  /**
   * Whether or not the delivery should be done with a persistent configuration
   */
  @Parameter
  @ConfigOverride
  @Summary("Whether or not the delivery should be done with a persistent configuration")
  private boolean persistentDelivery;

  /**
   * The default JMSPriority value to be used when sending the message
   */
  @Parameter
  @ConfigOverride
  @Summary("The default JMSPriority value to be used when sending the message")
  private Integer priority;

  /**
   * Defines the default time the message will be in the broker before it expires and is discarded
   */
  @Parameter
  @ConfigOverride
  @Summary("Defines the default time the message will be in the broker before it expires and is discarded")
  private Long timeToLive;

  /**
   * Time unit to be used in the timeToLive configurations
   */
  @Parameter
  @ConfigOverride
  @Summary("Time unit to be used in the timeToLive configurations")
  private TimeUnit timeToLiveUnit;

  /**
   * If true; the Message will be flagged to avoid generating its MessageID
   */
  @Parameter
  @DisplayName("Disable Message ID")
  @ConfigOverride
  @Summary("If true; the Message will be flagged to avoid generating its MessageID")
  private boolean disableMessageId;

  /**
   * If true; the Message will be flagged to avoid generating its sent Timestamp
   */
  @Parameter
  @ConfigOverride
  @DisplayName("Disable Message Timestamp")
  @Summary("If true; the Message will be flagged to avoid generating its sent Timestamp")
  private boolean disableMessageTimestamp;

  // JMS 2.0
  /**
   * Only used by JMS 2.0. Sets the delivery delay to be applied in order to postpone the Message delivery
   */
  @Parameter
  @ConfigOverride
  @Summary("Only used by JMS 2.0. Sets the delivery delay to be applied in order to postpone the Message delivery")
  private Long deliveryDelay;

  /**
   * Time unit to be used in the deliveryDelay configurations
   */
  @Parameter
  @ConfigOverride
  @Summary("Time unit to be used in the deliveryDelay configurations")
  private TimeUnit deliveryDelayUnit;

  /**
   * Options on whether to include an outbound correlation id or not
   *
   * @since 1.3.0
   */
  @Parameter
  @DisplayName("Send Correlation ID")
  @ConfigOverride
  @Placement(order = 15)
  @Summary("Options on whether to include an outbound correlation id or not")
  private OutboundCorrelationStrategy sendCorrelationId;

  /**
   * Indicates which Request Reply Pattern to use.
   * By default uses the Correlation ID of the incoming message to do the Reply-To.
   * In case of configuring the Message ID pattern, the ReplyTo will be performed using the message ID of the incoming message.
   * If NONE is selected, not correlation ID will be configured automatically.
   *
   * @since 1.6.0
   */
  @Parameter
  @Optional(defaultValue = "CORRELATION_ID")
  @DisplayName("Request Reply Pattern")
  private org.mule.extensions.jms.api.RequestReplyPattern requestReplyPattern;


  @Parameter
  @DisplayName("Ignore Jms replyTo Header")
  @Placement(order = 1)
  @Summary("If true, no automatic response will be sent in case of Jms replyTo header presence.")
  private boolean ignoreReplyTo;

  public boolean isPersistentDelivery() {
    return persistentDelivery;
  }

  public Integer getPriority() {
    return priority;
  }

  public Long getTimeToLive() {
    return timeToLive;
  }

  public TimeUnit getTimeToLiveUnit() {
    return timeToLiveUnit;
  }

  public boolean isDisableMessageId() {
    return disableMessageId;
  }

  public boolean isDisableMessageTimestamp() {
    return disableMessageTimestamp;
  }

  public Long getDeliveryDelay() {
    return deliveryDelay;
  }

  public TimeUnit getDeliveryDelayUnit() {
    return deliveryDelayUnit;
  }

  public OutboundCorrelationStrategy getSendCorrelationId() {
    return sendCorrelationId;
  }

  public RequestReplyPattern getRequestReplyPattern() {
    return requestReplyPattern.get();
  }

  public boolean isReplyToIgnored() {
    return ignoreReplyTo;
  }

}
