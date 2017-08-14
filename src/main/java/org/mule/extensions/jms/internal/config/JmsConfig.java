/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.config;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

import org.mule.extensions.jms.api.config.JmsConsumerConfig;
import org.mule.extensions.jms.api.config.JmsProducerConfig;
import org.mule.extensions.jms.internal.JmsConnector;
import org.mule.extensions.jms.internal.operation.JmsConsume;
import org.mule.extensions.jms.internal.operation.JmsPublish;
import org.mule.extensions.jms.internal.operation.JmsPublishConsume;
import org.mule.extensions.jms.internal.source.JmsListener;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.param.DefaultEncoding;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * Base configuration for {@link JmsConnector}
 *
 * @since 1.0
 */
@Configuration(name = "config")
@Operations({JmsConsume.class, JmsPublish.class, JmsPublishConsume.class})
@Sources({JmsListener.class})
public class JmsConfig {

  /**
   * The default {@code encoding} of the {@link Message} {@code body} to be used if the message doesn't communicate it
   */
  @Parameter
  @DefaultEncoding
  @Expression(NOT_SUPPORTED)
  private String encoding;

  /**
   * The default {@code contentType} of the {@link Message} {@code body} to be used if the message doesn't communicate it
   */
  @Parameter
  @Expression(NOT_SUPPORTED)
  @Optional(defaultValue = "*/*")
  private String contentType;

  /**
   * Configuration parameters for consuming {@link Message}s from a JMS {@link Queue} or {@link Topic}
   */
  @Expression(NOT_SUPPORTED)
  @Placement(tab = "Consumer")
  @ParameterGroup(name = "Consumer Config", showInDsl = true)
  private JmsConsumerConfig consumerConfig;

  /**
   * Configuration parameters for sending {@link Message}s to a JMS {@link Queue} or {@link Topic}
   */
  @Expression(NOT_SUPPORTED)
  @Placement(tab = "Producer")
  @ParameterGroup(name = "Producer Config", showInDsl = true)
  private JmsProducerConfig producerConfig;

  public String getContentType() {
    return contentType;
  }

  public String getEncoding() {
    return encoding;
  }

  public JmsConsumerConfig getConsumerConfig() {
    return consumerConfig;
  }

  public JmsProducerConfig getProducerConfig() {
    return producerConfig;
  }

}
