/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.message;

import static org.mule.runtime.api.util.Preconditions.checkArgument;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.io.Serializable;

import javax.jms.Message;

/**
 * Contains all the metadata of a JMS {@link Message}, it carries information such as the Headers,
 * the Properties and the required ID for performing an ACK on the Message.
 * <p>
 *
 * @since 1.0
 */
public class JmsAttributes implements Serializable {

  private static final long serialVersionUID = -8148917084189760450L;

  /**
   * Container element for all the properties present in a JMS Message
   */
  @Parameter
  private final JmsMessageProperties properties;

  /**
   * All the possible headers of a JMS Message
   */
  @Parameter
  private final JmsHeaders headers;

  /**
   * The session ACK ID required to ACK a the current Message if one is available, or null otherwise.
   */
  @Parameter
  @Optional
  private final String ackId;

  public JmsAttributes(JmsMessageProperties properties, JmsHeaders headers, String ackId) {
    this.properties = properties;
    this.headers = headers;
    this.ackId = ackId;
  }

  /**
   * {@inheritDoc}
   */
  public JmsMessageProperties getProperties() {
    return properties;
  }

  /**
   * {@inheritDoc}
   */
  public JmsHeaders getHeaders() {
    return headers;
  }

  /**
   * {@inheritDoc}
   */
  public String getAckId() {
    return ackId;
  }

  /**
   * Builder implementation for creating a {@link JmsAttributes} instance
   * @since 1.0
   */
  public static class Builder {

    private JmsMessageProperties properties;
    private JmsHeaders headers;
    private String ackId;

    private Builder() {}

    public static JmsAttributes.Builder newInstance() {
      return new JmsAttributes.Builder();
    }

    public JmsAttributes.Builder withProperties(JmsMessageProperties properties) {
      this.properties = properties;
      return this;
    }

    public JmsAttributes.Builder withHeaders(JmsHeaders headers) {
      this.headers = headers;
      return this;
    }

    public JmsAttributes.Builder withAckId(String ackId) {
      this.ackId = ackId;
      return this;
    }

    public JmsAttributes build() {
      checkArgument(properties != null, "No JmsMessageProperties were provided, but they are required for the JmsAttributes");
      checkArgument(headers != null, "No JmsHeaders were provided, but they are required for the JmsAttributes");
      return new JmsAttributes(properties, headers, ackId);
    }
  }

}
