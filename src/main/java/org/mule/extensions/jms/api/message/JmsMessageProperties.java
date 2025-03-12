/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.message;

import java.util.Map;

import jakarta.jms.Message;

/**
 * Container element for all the properties present in a JMS {@link Message}.
 * <p>
 * This container not only allows to fetch the all properties in a single Map representation, but also provides accessors for the
 * properties according to their origin. Properties may be those predefined by JMS (the {@link JmsxProperties}), those that are
 * used by the JMS broker or provider (known as plain JMS properties), and finally the ones provided by the User who created the
 * {@link Message}.
 *
 * @since 1.0
 */
public class JmsMessageProperties extends org.mule.jms.commons.api.message.JmsMessageProperties {

  public JmsMessageProperties(Map<String, Object> messageProperties) {
    super(messageProperties);
  }
}
