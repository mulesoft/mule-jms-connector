/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.operation;

import static org.mule.extensions.jms.internal.common.JmsCommons.EXAMPLE_CONTENT_TYPE;
import static org.mule.extensions.jms.internal.common.JmsCommons.EXAMPLE_ENCODING;
import static org.mule.extensions.jms.internal.operation.profiling.tracing.JmsConsumeSpanCustomizer.getJmsConsumeSpanCustomizer;
import static org.mule.jms.commons.internal.common.JmsCommons.getDestinationType;

import static org.slf4j.LoggerFactory.getLogger;

import org.mule.extensions.jms.api.config.ConsumerAckMode;
import org.mule.extensions.jms.api.config.JmsConsumerConfig;
import org.mule.extensions.jms.api.destination.ConsumerType;
import org.mule.extensions.jms.api.exception.JmsConsumeErrorTypeProvider;
import org.mule.extensions.jms.api.exception.JmsConsumeException;
import org.mule.extensions.jms.api.exception.JmsExtensionException;
import org.mule.extensions.jms.internal.config.JmsConfig;
import org.mule.extensions.jms.internal.connection.session.JmsSessionManager;
import org.mule.extensions.jms.internal.metadata.JmsOutputResolver;
import org.mule.jms.commons.api.AttributesOutputResolver;
import org.mule.jms.commons.internal.connection.JmsTransactionalConnection;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.ConfigOverride;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;
import org.mule.runtime.extension.api.tx.OperationTransactionalAction;
import org.mule.sdk.compatibility.api.utils.ForwardCompatibilityHelper;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;

import org.slf4j.Logger;

/**
 * Operation that allows the user to consume a single {@link Message} from a given {@link Destination}
 *
 * @since 1.0
 */
public final class JmsConsume implements Initialisable, Disposable {

  private static final Logger LOGGER = getLogger(JmsConsume.class);

  @Inject
  private JmsSessionManager sessionManager;

  @Inject
  private SchedulerService schedulerService;

  private org.mule.jms.commons.internal.operation.JmsConsume jmsConsume;

  @Inject
  private java.util.Optional<ForwardCompatibilityHelper> forwardCompatibilityHelper;

  /**
   * Operation that allows the user to consume a single {@link Message} from a given {@link Destination}.
   *
   * @param connection      the current {@link JmsConnection}
   * @param config          the current {@link JmsConsumerConfig}
   * @param destination     the name of the {@link Destination} from where the {@link Message} should be consumed
   * @param consumerType    the type of the {@link MessageConsumer} that is required for the given destination, along with any
   *                        extra configurations that are required based on the destination type.
   * @param ackMode         the {@link ConsumerAckMode} that will be configured over the Message and Session
   * @param selector        a custom JMS selector for filtering the messages
   * @param contentType     the {@link Message}'s content content type
   * @param encoding        the {@link Message}'s content encoding
   * @param maximumWait maximum time to wait for a message before timing out
   * @param maximumWaitUnit  Time unit to be used in the maximumWaitTime configurations
   * @return a {@link Result} with the {@link Message} content as {@link Result#getOutput} and its properties
   * and headers as {@link Result#getAttributes}
   * @throws JmsConsumeException if an error occurs
   */
  @OutputResolver(output = JmsOutputResolver.class, attributes = AttributesOutputResolver.class)
  @Throws(JmsConsumeErrorTypeProvider.class)
  public Result<Object, Object> consume(@Config JmsConfig config,
                                        @Connection JmsTransactionalConnection connection,
                                        @Summary("The name of the Destination from where the Message should be consumed") String destination,
                                        @ConfigOverride @Summary("The Type of the Consumer that should be used for the provided destination") ConsumerType consumerType,
                                        @Optional @Summary("The Session ACK mode to use when consuming a message") ConsumerAckMode ackMode,
                                        @ConfigOverride @Summary("The JMS selector to be used for filtering incoming messages") String selector,
                                        @Optional @Summary("The content type of the message body") @Example(EXAMPLE_CONTENT_TYPE) String contentType,
                                        @Optional @Summary("The encoding of the message body") @Example(EXAMPLE_ENCODING) String encoding,
                                        @Optional(
                                            defaultValue = "10000") @Summary("Maximum time to wait for a message to arrive before timeout") Long maximumWait,
                                        @Optional(
                                            defaultValue = "MILLISECONDS") @Example("MILLISECONDS") @Summary("Time unit to be used in the maximumWaitTime configuration") TimeUnit maximumWaitUnit,
                                        OperationTransactionalAction transactionalAction,
                                        CorrelationInfo correlationInfo)
      throws JmsExtensionException, ConnectionException {
    customizeCurrentSpan(connection, destination, consumerType, correlationInfo);
    return (Result) jmsConsume.consume(config, connection, destination, consumerType, ackMode,
                                       selector, contentType, encoding, maximumWait,
                                       maximumWaitUnit, transactionalAction);
  }


  @Override
  public void initialise() {
    jmsConsume =
        new org.mule.jms.commons.internal.operation.JmsConsume(sessionManager, schedulerService);
  }

  @Override
  public void dispose() {
    jmsConsume.dispose();
  }

  private void customizeCurrentSpan(JmsTransactionalConnection connection, String destination,
                                    org.mule.jms.commons.api.destination.ConsumerType consumerType,
                                    org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo correlationInfo) {
    forwardCompatibilityHelper
        .ifPresent(fch -> getJmsConsumeSpanCustomizer().customizeSpan(fch.getDistributedTraceContextManager(correlationInfo),
                                                                      connection, destination, consumerType));
  }
}
