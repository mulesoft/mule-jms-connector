/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extensions.jms.test.internal.operation.profiling.tracing;

import static org.mule.extensions.jms.api.destination.DestinationType.TOPIC;
import static org.mule.extensions.jms.internal.operation.profiling.tracing.JmsConsumeSpanCustomizer.getJmsConsumeSpanCustomizer;
import static org.mule.extensions.jms.internal.operation.profiling.tracing.JmsPublishSpanCustomizer.getJmsPublishSpanCustomizer;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.TRACING;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mule.extensions.jms.api.destination.DestinationType;
import org.mule.extensions.jms.api.message.JmsMessageBuilder;
import org.mule.extensions.jms.internal.operation.profiling.tracing.JmsConsumeSpanCustomizer;
import org.mule.extensions.jms.internal.operation.profiling.tracing.JmsPublishSpanCustomizer;
import org.mule.jms.commons.api.destination.ConsumerType;
import org.mule.jms.commons.internal.connection.JmsTransactionalConnection;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.sdk.api.runtime.source.DistributedTraceContextManager;

import java.util.OptionalLong;

import javax.jms.Connection;
import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;

import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;
import org.junit.Test;

@Story(TRACING)
@Issue("W-11859222")
public class JmsSpanCustomizerTestCase {

  public static final String MESSAGING_SYSTEM = "messaging.system";
  public static final String MESSAGING_DESTINATION = "messaging.destination";
  public static final String MESSAGING_DESTINATION_KIND = "messaging.destination_kind";
  public static final String MESSAGING_CONVERSATION_ID = "messaging.conversation_id";
  public static final String MESSAGING_MESSAGE_PAYLOAD_SIZE_BYTES = "messaging.message_payload_size_bytes";

  @Test
  @Description("The consume span customizer informs the distributed trace context manager the correct attributes/name")
  public void jmsConsumeSpanCustomizerShouldSetCorrespondingAttributes() throws JMSException {
    String messagingSystem = "testActiveMq";
    String destination = "queueName";
    String expectedSpanName = destination + " " + "receive";

    DistributedTraceContextManager distributedTraceContextManager = mock(DistributedTraceContextManager.class);
    JmsTransactionalConnection jmsTransactionalConnection = mock(JmsTransactionalConnection.class);
    Connection connection = mock(Connection.class);
    ConnectionMetaData connectionMetaData = mock(ConnectionMetaData.class);
    ConsumerType consumerType = mock(ConsumerType.class);

    when(jmsTransactionalConnection.get()).thenReturn(connection);
    when(connection.getMetaData()).thenReturn(connectionMetaData);
    when(connectionMetaData.getJMSProviderName()).thenReturn(messagingSystem);
    when(consumerType.topic()).thenReturn(false);

    JmsConsumeSpanCustomizer jmsConsumeSpanCustomizer = getJmsConsumeSpanCustomizer();
    jmsConsumeSpanCustomizer.customizeSpan(distributedTraceContextManager, jmsTransactionalConnection, destination, consumerType);

    verify(distributedTraceContextManager).setCurrentSpanName(expectedSpanName);
    verify(distributedTraceContextManager).addCurrentSpanAttribute(MESSAGING_SYSTEM, messagingSystem);
    verify(distributedTraceContextManager).addCurrentSpanAttribute(MESSAGING_DESTINATION, destination);
    verify(distributedTraceContextManager).addCurrentSpanAttribute(MESSAGING_DESTINATION_KIND, "queue");
  }

  @Test
  @Description("The publish span customizer informs the distributed trace context manager the correct attributes/name")
  public void jmsPublishSpanCustomizerShouldSetCorrespondingAttributes() throws Exception {
    String messagingSystem = "testActiveMq";
    String destination = "topicName";
    String expectedSpanName = destination + " " + "send";
    String correlationId = "correlationIdTest1";
    DestinationType destinationType = TOPIC;
    TypedValue<Object> typedValue = new TypedValue<Object>(Object.class, DataType.OBJECT, OptionalLong.of(39L));

    DistributedTraceContextManager distributedTraceContextManager = mock(DistributedTraceContextManager.class);
    JmsTransactionalConnection jmsTransactionalConnection = mock(JmsTransactionalConnection.class);
    Connection connection = mock(Connection.class);
    ConnectionMetaData connectionMetaData = mock(ConnectionMetaData.class);
    JmsMessageBuilder jmsMessageBuilder = mock(JmsMessageBuilder.class);

    when(jmsTransactionalConnection.get()).thenReturn(connection);
    when(connection.getMetaData()).thenReturn(connectionMetaData);
    when(connectionMetaData.getJMSProviderName()).thenReturn(messagingSystem);
    when(jmsMessageBuilder.getCorrelationId()).thenReturn(correlationId);
    when(jmsMessageBuilder.getBody()).thenReturn(typedValue);

    JmsPublishSpanCustomizer jmsPublishSpanCustomizer = getJmsPublishSpanCustomizer();
    jmsPublishSpanCustomizer.customizeSpan(distributedTraceContextManager, jmsTransactionalConnection, destination,
                                           destinationType, jmsMessageBuilder);

    verify(distributedTraceContextManager).setCurrentSpanName(expectedSpanName);
    verify(distributedTraceContextManager).addCurrentSpanAttribute(MESSAGING_SYSTEM, messagingSystem);
    verify(distributedTraceContextManager).addCurrentSpanAttribute(MESSAGING_DESTINATION, destination);
    verify(distributedTraceContextManager).addCurrentSpanAttribute(MESSAGING_DESTINATION_KIND, "topic");
    verify(distributedTraceContextManager).addCurrentSpanAttribute(MESSAGING_CONVERSATION_ID, correlationId);
    verify(distributedTraceContextManager).addCurrentSpanAttribute(MESSAGING_MESSAGE_PAYLOAD_SIZE_BYTES, "39");
  }
}
