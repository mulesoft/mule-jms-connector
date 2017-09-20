/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.source;

import static java.lang.String.format;
import static org.mule.extensions.jms.api.connection.JmsSpecification.JMS_2_0;
import static org.mule.extensions.jms.internal.common.JmsCommons.EXAMPLE_CONTENT_TYPE;
import static org.mule.extensions.jms.internal.common.JmsCommons.EXAMPLE_ENCODING;
import static org.mule.extensions.jms.internal.common.JmsCommons.QUEUE;
import static org.mule.extensions.jms.internal.common.JmsCommons.TOPIC;
import static org.mule.extensions.jms.internal.common.JmsCommons.closeQuietly;
import static org.mule.extensions.jms.internal.common.JmsCommons.getDestinationType;
import static org.mule.extensions.jms.internal.common.JmsCommons.resolveOverride;
import static org.mule.extensions.jms.internal.common.JmsCommons.toInternalAckMode;
import static org.mule.extensions.jms.internal.config.InternalAckMode.AUTO;
import static org.mule.extensions.jms.internal.config.InternalAckMode.DUPS_OK;
import static org.mule.extensions.jms.internal.config.InternalAckMode.IMMEDIATE;
import static org.mule.extensions.jms.internal.config.InternalAckMode.TRANSACTED;
import static org.mule.runtime.core.api.util.ExceptionUtils.extractConnectionException;
import static org.mule.runtime.extension.api.tx.SourceTransactionalAction.ALWAYS_BEGIN;
import static org.slf4j.LoggerFactory.getLogger;
import org.mule.extensions.jms.api.config.AckMode;
import org.mule.extensions.jms.api.config.JmsConsumerConfig;
import org.mule.extensions.jms.api.destination.ConsumerType;
import org.mule.extensions.jms.api.destination.TopicConsumer;
import org.mule.extensions.jms.api.exception.JmsExtensionException;
import org.mule.extensions.jms.api.message.JmsAttributes;
import org.mule.extensions.jms.internal.config.InternalAckMode;
import org.mule.extensions.jms.internal.config.JmsConfig;
import org.mule.extensions.jms.internal.connection.JmsTransactionalConnection;
import org.mule.extensions.jms.internal.connection.session.JmsSession;
import org.mule.extensions.jms.internal.connection.session.JmsSessionManager;
import org.mule.extensions.jms.internal.consume.JmsMessageConsumer;
import org.mule.extensions.jms.internal.metadata.JmsOutputResolver;
import org.mule.extensions.jms.internal.support.Jms102bSupport;
import org.mule.extensions.jms.internal.support.JmsSupport;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.message.Error;
import org.mule.runtime.core.api.util.StringMessageUtils;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.execution.OnError;
import org.mule.runtime.extension.api.annotation.execution.OnSuccess;
import org.mule.runtime.extension.api.annotation.metadata.MetadataScope;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.ConfigOverride;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.mule.runtime.extension.api.tx.SourceTransactionalAction;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

/**
 * JMS Subscriber for {@link Destination}s, allows to listen
 * for incoming {@link Message}s
 *
 * @since 1.0
 */
@Alias("listener")
@EmitsResponse
@MetadataScope(outputResolver = JmsOutputResolver.class)
public class JmsListener extends Source<Object, JmsAttributes> {

  private static final Logger LOGGER = getLogger(JmsListener.class);
  static final String REPLY_TO_DESTINATION_VAR = "REPLY_TO_DESTINATION";
  static final String JMS_LOCK_VAR = "JMS_LOCK";
  static final String JMS_SESSION_VAR = "JMS_SESSION";

  static void notifyIfConnectionProblem(SourceCallbackContext callbackContext, Exception e) {
    notifyIfConnectionProblem(callbackContext.getSourceCallback(), e);
  }

  static void notifyIfConnectionProblem(SourceCallback callback, Exception e) {
    extractConnectionException(e).ifPresent(ce -> callback.onConnectionException(ce));
  }

  @Inject
  private JmsSessionManager sessionManager;

  @Config
  private JmsConfig config;

  @Connection
  private ConnectionProvider<JmsTransactionalConnection> connectionProvider;

  private JmsTransactionalConnection connection;

  private JmsSupport jmsSupport;

  private SourceTransactionalAction transactionalAction;

  private InternalAckMode resolvedAckMode;

  /**
   * List to save all the created {@link JmsSession} and {@link JmsListenerLock} by this listener.
   */
  private final List<MessageListenerInfo> createdListeners = new ArrayList<>();

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
  @Optional
  @Example(EXAMPLE_CONTENT_TYPE)
  private String inboundContentType;

  /**
   * The inboundEncoding of the message body
   */
  @Parameter
  @Optional
  @Example(EXAMPLE_ENCODING)
  private String inboundEncoding;

  /**
   * The number of concurrent consumers that will be used to receive JMS Messages
   */
  @Parameter
  @Optional(defaultValue = "4")
  private int numberOfConsumers;

  @Override
  public void onStart(SourceCallback<Object, JmsAttributes> sourceCallback) throws MuleException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Starting JMS Message Listener");
    }

    JmsConsumerConfig consumerConfig = config.getConsumerConfig();

    resolvedAckMode = transactionalAction.equals(ALWAYS_BEGIN)
        ? TRANSACTED
        : resolveOverride(toInternalAckMode(consumerConfig.getAckMode()), toInternalAckMode(ackMode));

    connection = connectionProvider.connect();
    jmsSupport = connection.getJmsSupport();

    connection.registerExceptionListener(e -> sourceCallback.onConnectionException(new ConnectionException(e)));

    JmsMessageListenerFactory messageListenerFactory =
        new JmsMessageListenerFactory(resolvedAckMode, inboundEncoding, inboundContentType, config, sessionManager, jmsSupport,
                                      sourceCallback);

    validateNumberOfConsumers(numberOfConsumers);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(format("Starting JMS Listener with [%s] consumers on destination [%s] of type [%s] with AckMode [%s]",
                          numberOfConsumers, destination, getDestinationType(consumerType), resolvedAckMode.name()));
    }

    try {
      for (int i = 0; i < numberOfConsumers; i++) {
        JmsSession session = connection.createSession(resolvedAckMode, consumerType.topic());

        final Destination jmsDestination = jmsSupport.createDestination(session.get(), destination, consumerType.topic());
        final JmsMessageConsumer consumer = connection.createConsumer(session, jmsDestination, selector, consumerType);

        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug(format("Creating Message Listener on Session [%s] for destination [%s]",
                              session.get(), destination));
        }

        JmsListenerLock jmsLock = createJmsLock();
        createdListeners.add(new MessageListenerInfo(session, jmsLock, consumer));
        consumer.listen(messageListenerFactory.createMessageListener(session, jmsLock));
      }
    } catch (Exception e) {
      String msg = format("An error occurred while creating the consumers for destination [%s:%s]: %s",
                          getDestinationType(consumerType), destination, e.getMessage());
      LOGGER.error(msg, e);
      releaseListeners();

      if (e.getCause() instanceof ConnectException) {
        throw new ConnectionException(e);
      }

      throw new JmsExtensionException(msg, e);
    }
  }

  @Override
  public void onStop() {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(format("Stopping JMS Listener on destination [%s:%s]", getDestinationType(consumerType), destination));
    }

    releaseListeners();

    if (connection != null) {
      connectionProvider.disconnect(connection);
    }
  }

  @OnSuccess
  public void onSuccess(@ParameterGroup(name = "Response", showInDsl = true) JmsResponseMessageBuilder messageBuilder,
                        SourceCallbackContext callbackContext) {
    callbackContext.<JmsListenerLock>getVariable(JMS_LOCK_VAR)
        .ifPresent(JmsListenerLock::unlock);

    callbackContext.<Destination>getVariable(REPLY_TO_DESTINATION_VAR)
        .ifPresent(replyTo -> callbackContext.<JmsSession>getVariable(JMS_SESSION_VAR)
            .ifPresent(session -> doReply(messageBuilder, callbackContext, replyTo,
                                          session)));
  }

  @OnError
  public void onError(Error error, SourceCallbackContext callbackContext) {
    callbackContext.<JmsListenerLock>getVariable(JMS_LOCK_VAR)
        .ifPresent(jmsLock -> {
          if (resolvedAckMode.equals(AUTO) || resolvedAckMode.equals(DUPS_OK)) {
            jmsLock.unlockWithFailure(error);
          } else {
            jmsLock.unlock();
          }
        });
  }

  private void doReply(JmsResponseMessageBuilder messageBuilder,
                       SourceCallbackContext callbackContext, Destination replyTo, JmsSession session) {
    final boolean replyToTopic = replyDestinationIsTopic(replyTo);
    String destinationName;
    try {
      destinationName = replyToTopic ? ((Topic) replyTo).getTopicName() : ((Queue) replyTo).getQueueName();
    } catch (JMSException e) {
      LOGGER.error(format("An error occurred during reply. Failed to obtain the destination name: %s", e.getMessage()));
      notifyIfConnectionProblem(callbackContext, e);
      return;
    }

    try {

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(format("Begin reply to destination [%s] of type [%s]", destinationName, replyToTopic ? TOPIC : QUEUE));
      }

      Message message = messageBuilder.build(connection.getJmsSupport(), session.get(), config);

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Message built, sending message to " + destinationName);
      }

      JmsSession replySession = connection.createSession(AUTO, replyToTopic);
      connection.createProducer(replySession, replyTo, replyToTopic)
          .publish(message, messageBuilder);

    } catch (Exception e) {
      LOGGER.error(format("An error occurred during reply to destination [%s] of type [%s]: %s",
                          destinationName, replyToTopic ? TOPIC : QUEUE, e.getMessage()),
                   e);
      notifyIfConnectionProblem(callbackContext, e);
    }
  }

  private boolean replyDestinationIsTopic(Destination destination) {
    // TODO: MULE-11156 - take into account the special logic in 3.x for handling Weblogic 8.x and 9.x
    // see 'org.mule.transport.jms.weblogic.WeblogicJmsTopicResolver#topic'

    if (destination instanceof Topic && destination instanceof Queue
        && jmsSupport instanceof Jms102bSupport) {
      LOGGER.error(StringMessageUtils.getBoilerPlate(
                                                     "Destination implements both Queue and Topic "
                                                         + "while complying with JMS 1.0.2b specification. "
                                                         + "Please report your application server or JMS vendor name and version "
                                                         + "to http://www.mulesoft.org/jira"));
    }

    return destination instanceof Topic;
  }

  private JmsListenerLock createJmsLock() {
    if (resolvedAckMode.equals(IMMEDIATE) || resolvedAckMode.equals(TRANSACTED)) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(format("Session lock skipped for ACK mode [%s].", resolvedAckMode.name()));
      }
      return new NullJmsListenerLock();
    }
    return new DefaultJmsListenerLock();
  }

  private void validateNumberOfConsumers(int numberOfConsumers) {
    if (numberOfConsumers < 1) {
      throw new IllegalArgumentException("Invalid number of consumers: [" + numberOfConsumers
          + "]. The number should be 1 or greater.");
    }

    if (numberOfConsumers > 1 && consumerType.topic()) {
      TopicConsumer topicConsumer = (TopicConsumer) consumerType;

      if (!isCapableOfMultiConsumersOnTopic(topicConsumer)) {
        throw new IllegalArgumentException("Destination [" + destination + "] is a topic, but [" + numberOfConsumers
            + "] receivers have been requested. This is only possible for 'shared' topic consumers, otherwise use 1.");
      }
    }
  }

  private boolean isCapableOfMultiConsumersOnTopic(TopicConsumer topicConsumer) {
    return jmsSupport.getSpecification().equals(JMS_2_0) && topicConsumer.isShared();
  }

  private void releaseListeners() {
    try {
      createdListeners.forEach(info -> {
        info.getLock().unlockWithFailure();
        closeQuietly(info.getConsumer());
        closeQuietly(info.getSession());
      });
    } finally {
      createdListeners.clear();
    }
  }

  private static class MessageListenerInfo {

    private JmsSession session;
    private JmsListenerLock jmsListenerLock;
    private JmsMessageConsumer messageConsumer;

    MessageListenerInfo(JmsSession session, JmsListenerLock jmsListenerLock, JmsMessageConsumer messageConsumer) {
      this.session = session;
      this.jmsListenerLock = jmsListenerLock;
      this.messageConsumer = messageConsumer;
    }

    public JmsSession getSession() {
      return session;
    }

    public JmsListenerLock getLock() {
      return jmsListenerLock;
    }

    public JmsMessageConsumer getConsumer() {
      return messageConsumer;
    }
  }
}
