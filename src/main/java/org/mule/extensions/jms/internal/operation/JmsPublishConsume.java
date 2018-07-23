/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.operation;

import static org.slf4j.LoggerFactory.getLogger;

import org.mule.extensions.jms.api.config.JmsProducerConfig;
import org.mule.extensions.jms.api.exception.JmsExtensionException;
import org.mule.extensions.jms.api.exception.JmsPublishConsumeErrorTypeProvider;
import org.mule.extensions.jms.api.message.JmsMessageBuilder;
import org.mule.extensions.jms.internal.config.JmsConfig;
import org.mule.extensions.jms.internal.connection.session.JmsSessionManager;
import org.mule.extensions.jms.internal.consume.JmsConsumeParameters;
import org.mule.extensions.jms.internal.metadata.JmsOutputResolver;
import org.mule.jms.commons.api.AttributesOutputResolver;
import org.mule.jms.commons.internal.connection.JmsConnection;
import org.mule.jms.commons.internal.publish.JmsPublishParameters;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.ConfigOverride;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;
import org.mule.runtime.extension.api.runtime.parameter.OutboundCorrelationStrategy;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;

import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.Message;

import org.slf4j.Logger;

/**
 * Operation that allows the user to send a message to a JMS {@link Destination} and waits for a response
 * either to the provided {@code ReplyTo} destination or to a temporary {@link Destination} created dynamically
 *
 * @since 1.0
 */
public class JmsPublishConsume implements Initialisable, Disposable {

  private static final Logger LOGGER = getLogger(JmsPublishConsume.class);

  @Inject
  private JmsSessionManager sessionManager;

  @Inject
  private SchedulerService schedulerService;
  private org.mule.jms.commons.internal.operation.JmsPublishConsume jmsPublishConsume;

  /**
   * Operation that allows the user to send a message to a JMS {@link Destination} and waits for a response
   * either to the provided {@code ReplyTo} destination or to a temporary {@link Destination} created dynamically
   *
   * @param config            the current {@link JmsProducerConfig}
   * @param connection        the current {@link JmsConnection}
   * @param destination       the name of the {@link Destination} where the {@link Message} should be sent
   * @param messageBuilder    the {@link JmsMessageBuilder} used to create the {@link Message} to be sent
   * @param publishParameters Parameter group that lets override the publish configuration
   * @param consumeParameters Parameter group that lets override the consume configuration
   * @param sendCorrelationId options on whether to include an outbound correlation id or not
   * @param correlationInfo   the current message's correlation info
   * @return a {@link Result} with the reply {@link Message} content as {@link Result#getOutput} and its properties
   * and headers as {@link Result#getAttributes}
   * @throws JmsExtensionException if an error occurs
   */
  @OutputResolver(output = JmsOutputResolver.class, attributes = AttributesOutputResolver.class)
  @Throws(JmsPublishConsumeErrorTypeProvider.class)
  public void publishConsume(@Config JmsConfig config,
                             @Connection JmsConnection connection,
                             @Placement(
                                 order = 0) @Summary("The name of the Queue destination where the Message should be sent") String destination,
                             @Placement(
                                 order = 1) @Summary("A builder for the message that will be published") @ParameterGroup(
                                     name = "Message",
                                     showInDsl = true) JmsMessageBuilder messageBuilder,
                             @Placement(order = 2) @ParameterGroup(
                                 name = "Publish Configuration",
                                 showInDsl = true) JmsPublishParameters publishParameters,
                             @Placement(order = 3) @ParameterGroup(
                                 name = "Consume Configuration",
                                 showInDsl = true) JmsConsumeParameters consumeParameters,
                             @ConfigOverride OutboundCorrelationStrategy sendCorrelationId,
                             CorrelationInfo correlationInfo,
                             CompletionCallback<Object, Object> completionCallback)
      throws JmsExtensionException {
    jmsPublishConsume.publishConsume(config, connection, destination, messageBuilder, publishParameters, consumeParameters,
                                     sendCorrelationId, correlationInfo, (CompletionCallback) completionCallback);
  }

  @Override
  public void dispose() {
    if (jmsPublishConsume != null) {
      jmsPublishConsume.dispose();
    }
  }

  @Override
  public void initialise() {
    this.jmsPublishConsume = new org.mule.jms.commons.internal.operation.JmsPublishConsume(sessionManager, schedulerService);
  }
}
