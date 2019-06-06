/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api;

/**
 * @since
 */
public enum RequestReplyPattern {

  CORRELATION_ID(org.mule.jms.commons.api.RequestReplyPattern.CORRELATION_ID),

  MESSAGE_ID(org.mule.jms.commons.api.RequestReplyPattern.MESSAGE_ID),

  NONE(org.mule.jms.commons.api.RequestReplyPattern.NONE);

  private org.mule.jms.commons.api.RequestReplyPattern requestReplyPattern;

  RequestReplyPattern(org.mule.jms.commons.api.RequestReplyPattern requestReplyPattern) {
    this.requestReplyPattern = requestReplyPattern;
  }

  public org.mule.jms.commons.api.RequestReplyPattern get() {
    return requestReplyPattern;
  }
}
