/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.source;

import static org.mule.extensions.jms.internal.common.JmsCommons.EXAMPLE_CONTENT_TYPE;
import static org.mule.extensions.jms.internal.common.JmsCommons.EXAMPLE_ENCODING;
import static org.mule.extensions.jms.internal.common.JmsCommons.createWithJmsThreadGroup;
import static org.mule.runtime.extension.api.annotation.source.SourceClusterSupport.DEFAULT_PRIMARY_NODE_ONLY;

import org.mule.extensions.jms.api.ack.AckMode;
import org.mule.extensions.jms.api.destination.ConsumerType;
import org.mule.extensions.jms.internal.config.InternalAckMode;
import org.mule.extensions.jms.internal.config.JmsConfig;
import org.mule.extensions.jms.internal.connection.session.JmsSession;
import org.mule.extensions.jms.internal.connection.session.JmsSessionManager;
import org.mule.extensions.jms.internal.metadata.JmsOutputResolver;
import org.mule.jms.commons.api.AttributesOutputResolver;
import org.mule.jms.commons.api.connection.DefaultReconnectionManagerProvider;
import org.mule.jms.commons.api.lock.JmsListenerLockFactory;
import org.mule.jms.commons.internal.connection.JmsTransactionalConnection;
import org.mule.jms.commons.internal.source.DefaultJmsConnectionExceptionResolver;
import org.mule.jms.commons.internal.source.DefaultJmsResourceReleaser;
import org.mule.jms.commons.internal.source.SourceConfiguration;
import org.mule.jms.commons.internal.support.JmsSupport;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.api.message.Error;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.mule.runtime.api.tx.TransactionType;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.execution.OnError;
import org.mule.runtime.extension.api.annotation.execution.OnSuccess;
import org.mule.runtime.extension.api.annotation.metadata.MetadataScope;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.source.ClusterSupport;
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.mule.runtime.extension.api.tx.SourceTransactionalAction;

import javax.inject.Inject;
import jakarta.jms.Destination;
import jakarta.jms.Message;

/**
 * JMS Subscriber for {@link Destination}s, allows to listen for incoming {@link Message}s
 *
 * @since 1.0
 */
@Alias("listener")
@DisplayName("On New Message")
@EmitsResponse
@ClusterSupport(value = DEFAULT_PRIMARY_NODE_ONLY)
@MetadataScope(outputResolver = JmsOutputResolver.class, attributesResolver = AttributesOutputResolver.class)
@MediaType(MediaType.ANY)
public class JmsListener extends Source<Object, Object> {

  @Inject
  private JmsSessionManager sessionManager;

  @Config
  private JmsConfig config;

  @Connection
  private ConnectionProvider<JmsTransactionalConnection> connectionProvider;

  private JmsTransactionalConnection connection;

  private JmsSupport jmsSupport;

  private SourceTransactionalAction transactionalAction;

  private TransactionType transactionType;

  @RefName
  String configName;

  private InternalAckMode resolvedAckMode;

  private ComponentLocation componentLocation;

  /**
   * List to save all the created {@link JmsSession} and {@link JmsListenerLock} by this listener.
   */

  /**
   * The name of the Destination from where the Message should be consumed
   */
  @Parameter
  @ParameterDsl(allowReferences = false)
  private String destination;

  /**
   * The Type of the Consumer that should be used for the provided destination
   */
  @Parameter
  @ConfigOverride
  private ConsumerType consumerType;

  /**
   * The Session ACK mode to use when consuming a message
   */
  @Parameter
  @DisplayName("Acknowledge Mode")
  @Optional
  private AckMode ackMode;

  /**
   * JMS selector to be used for filtering incoming messages
   */
  @Parameter
  @ConfigOverride
  private String selector;

  /**
   * The content type of the message body
   */
  @Parameter
  @DisplayName("Inbound Content-Type")
  @Optional
  @Example(EXAMPLE_CONTENT_TYPE)
  private String inboundContentType;

  /**
   * The inboundEncoding of the message body
   */
  @Parameter
  @DisplayName("Inbound Encoding")
  @Optional
  @Example(EXAMPLE_ENCODING)
  private String inboundEncoding;

  /**
   * The number of concurrent consumers that will be used to receive JMS Messages
   */
  @Parameter
  @Optional(defaultValue = "4")
  private int numberOfConsumers;

  @Inject
  SchedulerService schedulerService;

  private org.mule.jms.commons.internal.source.JmsListener jmsListener;

  @Override
  public void onStart(SourceCallback<Object, Object> sourceCallback) throws MuleException {
    jmsListener = new org.mule.jms.commons.internal.source.JmsListener.Builder(
                                                                               sessionManager, config, connectionProvider,
                                                                               destination,
                                                                               consumerType, ackMode,
                                                                               selector, inboundContentType, inboundEncoding,
                                                                               numberOfConsumers,
                                                                               new SourceConfiguration(transactionalAction,
                                                                                                       transactionType,
                                                                                                       componentLocation,
                                                                                                       configName),
                                                                               schedulerService)
                                                                                   .setExceptionResolver(new DefaultJmsConnectionExceptionResolver())
                                                                                   .setResourceReleaser(new DefaultJmsResourceReleaser())
                                                                                   .setListenerLockFactory(JmsListenerLockFactory
                                                                                       .newDefault())
                                                                                   .setReconnectionManager(new DefaultReconnectionManagerProvider())
                                                                                   .build();

    try {
      createWithJmsThreadGroup(() -> {
        jmsListener.onStart(sourceCallback);
        return null;
      });
    } catch (Exception e) {
      if (e.getCause() instanceof MuleException) {
        throw (MuleException) e.getCause();
      } else {
        throw new MuleRuntimeException(e.getCause());
      }
    }
  }

  @Override
  public void onStop() {
    jmsListener.onStop();
  }

  @OnError
  public void onError(Error error, SourceCallbackContext callbackContext) {
    jmsListener.onError(error, callbackContext);
  }

  @OnSuccess
  public void onSuccess(@ParameterGroup(name = "Response",
      showInDsl = true) @DisplayName("Reply-To Response") JmsResponseMessageBuilder messageBuilder,
                        CorrelationInfo correlationInfo,
                        SourceCallbackContext callbackContext) {
    jmsListener.onSuccess(messageBuilder, correlationInfo, callbackContext);
  }
}
