/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.message;

import static org.mule.runtime.api.util.Preconditions.checkArgument;

import org.mule.extensions.jms.api.message.JmsAttributes;
import org.mule.extensions.jms.api.message.JmsHeaders;
import org.mule.extensions.jms.api.message.JmsMessageProperties;
import org.mule.jms.commons.internal.message.JmsAttributesBuilder;

import java.util.Map;

/**
 * {@link JmsAttributesBuilder} implementation
 *
 * @since 1.3.0
 */
public class JmsConnectorAttributeBuilder implements JmsAttributesBuilder {

  private JmsMessageProperties properties;
  private String ackId;
  private org.mule.jms.commons.api.message.JmsHeaders headers;

  public JmsConnectorAttributeBuilder withProperties(Map<String, Object> properties) {
    this.properties = new JmsMessageProperties(properties);
    return this;
  }

  public JmsConnectorAttributeBuilder withAckId(String ackId) {
    this.ackId = ackId;
    return this;
  }

  @Override
  public JmsConnectorAttributeBuilder withHeaders(org.mule.jms.commons.api.message.JmsHeaders headers) {
    this.headers = headers;
    return this;
  }

  public JmsAttributes build() {
    checkArgument(properties != null, "No JmsMessageProperties were provided, but they are required for the JmsAttributes");
    checkArgument(headers != null, "No JmsHeaders were provided, but they are required for the JmsAttributes");
    return new JmsAttributes(properties, (JmsHeaders) headers, ackId);
  }
}
