/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.publish;

import org.mule.jms.commons.internal.publish.PublisherParameters;
import org.mule.runtime.extension.api.annotation.param.ConfigOverride;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import java.util.concurrent.TimeUnit;

import jakarta.jms.Message;

/**
 * Contains the parameters that can override the default values for publishing a {@link Message}
 *
 * @since 1.0
 */
public class JmsPublishParameters implements PublisherParameters {

  /**
   * If true; the Message will be sent using the PERSISTENT JMSDeliveryMode
   */
  @Parameter
  @ConfigOverride
  @Summary("If true; the Message will be sent using the PERSISTENT JMSDeliveryMode")
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
  @ConfigOverride
  @Summary("If true; the Message will be flagged to avoid generating its MessageID")
  private boolean disableMessageId;

  /**
   * If true; the Message will be flagged to avoid generating its sent Timestamp
   */
  @Parameter
  @ConfigOverride
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
