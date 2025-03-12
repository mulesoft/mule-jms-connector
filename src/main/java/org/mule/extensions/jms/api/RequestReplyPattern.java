/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api;

/**
 * Enum that list the different Request Reply Pattern that the JMS Connector supports.
 *
 * @since 1.6.0
 */
public enum RequestReplyPattern {

  /**
   * Indicates that when waiting for the reply, the consumer will use a selector looking a message with correlation ID with the
   * value of the outgoing correlation ID.
   */
  CORRELATION_ID(org.mule.jms.commons.api.RequestReplyPattern.CORRELATION_ID),

  /**
   * Indicates that when waiting for the reply, the consumer will use a selector looking a message with correlation ID with the
   * value of the outgoing message ID
   */
  MESSAGE_ID(org.mule.jms.commons.api.RequestReplyPattern.MESSAGE_ID),

  /**
   * Indicates that when waiting for a reply, the consumer will consume a message without using any selector.
   */
  NONE(org.mule.jms.commons.api.RequestReplyPattern.NONE);

  private org.mule.jms.commons.api.RequestReplyPattern requestReplyPattern;

  RequestReplyPattern(org.mule.jms.commons.api.RequestReplyPattern requestReplyPattern) {
    this.requestReplyPattern = requestReplyPattern;
  }

  public org.mule.jms.commons.api.RequestReplyPattern get() {
    return requestReplyPattern;
  }
}
