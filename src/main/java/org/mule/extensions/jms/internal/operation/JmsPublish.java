/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.operation;

import static org.mule.extensions.jms.internal.common.JmsCommons.QUEUE;
import static org.mule.extensions.jms.internal.operation.profiling.tracing.JmsPublishSpanCustomizer.getJmsPublishSpanCustomizer;

import static org.slf4j.LoggerFactory.getLogger;

import org.mule.extensions.jms.api.config.JmsProducerConfig;
import org.mule.extensions.jms.api.destination.DestinationType;
import org.mule.extensions.jms.api.exception.JmsExtensionException;
import org.mule.extensions.jms.api.exception.JmsPublisherErrorTypeProvider;
import org.mule.extensions.jms.api.message.JmsMessageBuilder;
import org.mule.extensions.jms.internal.config.JmsConfig;
import org.mule.extensions.jms.internal.connection.session.JmsSessionManager;
import org.mule.extensions.jms.internal.publish.JmsPublishParameters;
import org.mule.jms.commons.internal.connection.JmsConnection;
import org.mule.jms.commons.internal.connection.JmsTransactionalConnection;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.ConfigOverride;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;
import org.mule.runtime.extension.api.runtime.parameter.OutboundCorrelationStrategy;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.mule.runtime.extension.api.tx.OperationTransactionalAction;
import org.mule.sdk.compatibility.api.utils.ForwardCompatibilityHelper;

import java.util.Locale;

import javax.inject.Inject;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.slf4j.Logger;

/**
 * Operation that allows the user to send a message to a JMS {@link Destination}
 *
 * @since 1.0
 */
public final class JmsPublish implements Initialisable, Disposable {

  private static final Logger LOGGER = getLogger(JmsPublish.class);

  @Inject
  private JmsSessionManager jmsSessionManager;

  @Inject
  private SchedulerService schedulerService;

  @Inject
  private java.util.Optional<ForwardCompatibilityHelper> forwardCompatibilityHelper;

  private org.mule.jms.commons.internal.operation.JmsPublish jmsPublish;

  /**
   * Operation that allows the user to send a {@link Message} to a JMS {@link Destination}
   *
   * @param config              the current {@link JmsProducerConfig }
   * @param connection          the current {@link JmsConnection}
   * @param destination         the name of the {@link Destination} where the {@link Message} should be sent
   * @param destinationType     the {@link DestinationType} of the {@code destination}
   * @param messageBuilder      the {@link JmsMessageBuilder } used to create the {@link Message} to be sent
   * @param overrides           Parameter Group with overriding parameters from the configuration
   * @param transactionalAction Transactional Action for the operation. Indicates if the publish must be executed
   *                            or not in a transaction.
   * @param sendCorrelationId   options on whether to include an outbound correlation id or not
   * @param correlationInfo     the current message's correlation info
   * @throws JmsExtensionException if an error occurs trying to publish a message
   */
  @Throws(JmsPublisherErrorTypeProvider.class)
  public void publish(@Config JmsConfig config, @Connection JmsTransactionalConnection connection,
                      @Summary("The name of the Destination where the Message should be sent") String destination,
                      @Optional(defaultValue = QUEUE) @Summary("The type of the Destination") DestinationType destinationType,
                      @Summary("A builder for the message that will be published") @ParameterGroup(name = "Message",
                          showInDsl = true) JmsMessageBuilder messageBuilder,
                      @ParameterGroup(name = "Publish Configuration") JmsPublishParameters overrides,
                      OperationTransactionalAction transactionalAction,
                      @ConfigOverride OutboundCorrelationStrategy sendCorrelationId,
                      CorrelationInfo correlationInfo,
                      CompletionCallback<Void, Void> completionCallback)

      throws JmsExtensionException {
    customizeCurrentSpan(connection, destination, destinationType, messageBuilder, correlationInfo);
    jmsPublish.publish(config, connection, destination, destinationType, messageBuilder, overrides, transactionalAction,
                       sendCorrelationId, correlationInfo, completionCallback);
  }

  private void customizeCurrentSpan(JmsTransactionalConnection connection, String destination, DestinationType destinationType,
                                    JmsMessageBuilder messageBuilder, CorrelationInfo correlationInfo) {
    forwardCompatibilityHelper
        .ifPresent(fch -> getJmsPublishSpanCustomizer().customizeSpan(fch.getDistributedTraceContextManager(correlationInfo),
                                                                      connection, destination, destinationType, messageBuilder));
  }


  @Override
  public void dispose() {
    if (jmsPublish != null) {
      jmsPublish.dispose();
    }
  }

  @Override
  public void initialise() {
    this.jmsPublish = new org.mule.jms.commons.internal.operation.JmsPublish(jmsSessionManager, schedulerService);
  }
}
