/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.source;

import org.mule.extensions.jms.api.message.JmsMessageBuilder;
import org.mule.extensions.jms.internal.publish.PublisherParameters;
import org.mule.runtime.extension.api.annotation.param.ConfigOverride;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import java.util.concurrent.TimeUnit;

import javax.jms.Message;

/**
 * Enables the creation of an outgoing {@link Message} along with the response configuration.
 * Users must use this builder to create a message response instance.
 *
 * @since 1.0
 */
public class JmsResponseMessageBuilder extends JmsMessageBuilder implements PublisherParameters {

  @Parameter
  @ConfigOverride
  @Summary("Whether or not the delivery should be done with a persistent configuration")
  private boolean persistentDelivery;

  @Parameter
  @ConfigOverride
  @Summary("The default JMSPriority value to be used when sending the message")
  private Integer priority;

  @Parameter
  @ConfigOverride
  @Summary("Defines the default time the message will be in the broker before it expires and is discarded")
  private Long timeToLive;

  @Parameter
  @ConfigOverride
  @Summary("Time unit to be used in the timeToLive configurations")
  private TimeUnit timeToLiveUnit;

  @Parameter
  @ConfigOverride
  @Summary("If true; the Message will be flagged to avoid generating its MessageID")
  private boolean disableMessageId;

  @Parameter
  @ConfigOverride
  @Summary("If true; the Message will be flagged to avoid generating its sent Timestamp")
  private boolean disableMessageTimestamp;

  // JMS 2.0
  @Parameter
  @ConfigOverride
  @Summary("Only used by JMS 2.0. Sets the delivery delay to be applied in order to postpone the Message delivery")
  private Long deliveryDelay;

  @Parameter
  @ConfigOverride
  @Summary("Time unit to be used in the deliveryDelay configurations")
  private TimeUnit deliveryDelayUnit;

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
}
