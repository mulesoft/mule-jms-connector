/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.message;


import static org.mule.extensions.jms.internal.common.JmsCommons.EXAMPLE_CONTENT_TYPE;
import static org.mule.extensions.jms.internal.common.JmsCommons.EXAMPLE_ENCODING;
import static org.slf4j.LoggerFactory.getLogger;

import org.mule.extensions.jms.api.config.JmsProducerConfig;
import org.mule.extensions.jms.api.destination.JmsDestination;
import org.mule.jms.commons.api.message.JmsMessageFactory;
import org.mule.jms.commons.internal.config.JmsConfig;
import org.mule.jms.commons.internal.support.JmsSupport;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.ConfigOverride;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;
import org.mule.runtime.extension.api.runtime.parameter.OutboundCorrelationStrategy;

import java.util.Map;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;

import org.slf4j.Logger;

/**
 * Enables the creation of an outgoing {@link Message}. Users must use this builder to create a message instance.
 *
 * @since 1.0
 */
public class JmsMessageBuilder implements org.mule.jms.commons.api.message.JmsMessageBuilder<JmsDestination> {

  private static final Logger LOGGER = getLogger(JmsMessageBuilder.class);
  public static final String BODY_CONTENT_TYPE_JMS_PROPERTY = "MM_MESSAGE_CONTENT_TYPE";
  public static final String BODY_ENCODING_JMS_PROPERTY = "MM_MESSAGE_ENCODING";

  /**
   * The body of the {@link Message}
   */
  @Parameter
  @ParameterDsl(allowReferences = false)
  @Content(primary = true)
  @Summary("The body of the Message")
  private TypedValue<Object> body;

  /**
   * The JMSType header of the {@link Message}
   */
  @Parameter
  @ConfigOverride
  @Summary("The JMSType identifier header of the Message")
  @DisplayName("JMS Type")
  private String jmsType;

  /**
   * The JMSCorrelationID header of the {@link Message}
   */
  @Parameter
  @Optional
  @Summary("The JMSCorrelationID header of the Message")
  @DisplayName("Correlation ID")
  private String correlationId;

  /**
   * {@code true} if the body type should be sent as a {@link Message} property
   */
  @Parameter
  @Optional(defaultValue = "true")
  @Summary("Whether or not the body content type should be sent as a property")
  @DisplayName("Send Content-Type")
  private boolean sendContentType;

  /**
   * The content type of the {@code body}
   */
  @Parameter
  @Optional
  @DisplayName("Content-Type")
  @Example(EXAMPLE_CONTENT_TYPE)
  @Summary("The content type of the message's body")
  private String outboundContentType;

  /**
   * {@code true} if the body outboundEncoding should be sent as a {@link Message} property
   */
  @Parameter
  @Optional(defaultValue = "true")
  @DisplayName("Send Encoding")
  @Summary("Whether or not the body outboundEncoding should be sent as a Message property")
  private boolean sendEncoding;

  /**
   * The outboundEncoding of the message's {@code body}
   */
  @Parameter
  @Optional
  @DisplayName("Encoding")
  @Example(EXAMPLE_ENCODING)
  @Summary("The encoding of the message's body")
  private String outboundEncoding;

  /**
   * The JMSReplyTo header information of the {@link Destination} where {@code this} {@link Message} should be replied to
   */
  @Parameter
  @Optional
  @DisplayName("Reply To")
  @Summary("The destination where a reply to this Message should be sent")
  private JmsDestination replyTo;

  /**
   * The custom user properties that should be set to this {@link Message}
   */
  @Content
  @Parameter
  @Optional
  @NullSafe
  @DisplayName("User Properties")
  @Summary("The custom user properties that should be set to this Message")
  private Map<String, Object> properties;

  /**
   * The JMSX properties that should be set to this {@link Message}
   */
  @Parameter
  @Optional
  @NullSafe
  @DisplayName("JMSX Properties")
  @Summary("The JMSX properties that should be set to this Message")
  private JmsxProperties jmsxProperties;

  /**
   * Creates a {@link Message} based on the provided configurations
   *
   * @param jmsSupport                  the {@link JmsSupport} used to create the JMSReplyTo {@link Destination}
   * @param outboundCorrelationStrategy the correlationId handling strategy
   * @param correlationInfo             the correlation information for the current message
   * @param session                     the current {@link Session}
   * @param config                      the current {@link JmsProducerConfig}
   * @return the {@link Message} created by the user
   * @throws JMSException if an error occurs
   */
  public Message build(JmsSupport jmsSupport, OutboundCorrelationStrategy outboundCorrelationStrategy,
                       CorrelationInfo correlationInfo,
                       Session session, JmsConfig config)
      throws JMSException {

    return JmsMessageFactory.build(jmsSupport, outboundCorrelationStrategy, correlationInfo, session, config, this);
  }

  public TypedValue<Object> getBody() {
    return body;
  }

  public boolean isSendContentType() {
    return sendContentType;
  }

  public String getOutboundContentType() {
    return outboundContentType;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public String getJmsType() {
    return jmsType;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public JmsxProperties getJmsxProperties() {
    return jmsxProperties;
  }

  public JmsDestination getReplyTo() {
    return replyTo;
  }

  public boolean isSendEncoding() {
    return sendEncoding;
  }

  public String getOutboundEncoding() {
    return outboundEncoding;
  }

  public void setBody(TypedValue<Object> body) {
    this.body = body;
  }

  public void setJmsType(String jmsType) {
    this.jmsType = jmsType;
  }

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  public void setSendContentType(boolean sendContentType) {
    this.sendContentType = sendContentType;
  }

  public void setOutboundContentType(String outboundContentType) {
    this.outboundContentType = outboundContentType;
  }

  public void setSendEncoding(boolean sendEncoding) {
    this.sendEncoding = sendEncoding;
  }

  public void setOutboundEncoding(String outboundEncoding) {
    this.outboundEncoding = outboundEncoding;
  }

  public void setReplyTo(JmsDestination replyTo) {
    this.replyTo = replyTo;
  }

  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }

  public void setJmsxProperties(JmsxProperties jmsxProperties) {
    this.jmsxProperties = jmsxProperties;
  }
}
