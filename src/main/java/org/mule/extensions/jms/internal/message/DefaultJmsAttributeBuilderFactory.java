/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.message;

import org.mule.extensions.jms.api.message.JmsHeaders;
import org.mule.jms.commons.api.message.JmsAttributesBuilderFactory;
import org.mule.jms.commons.api.message.JmsHeadersBuilder;
import org.mule.jms.commons.internal.message.JmsAttributesBuilder;

/**
 * {@link JmsAttributesBuilderFactory} implementation for the JMS Connector
 *
 * @since 1.3.0
 */
public class DefaultJmsAttributeBuilderFactory implements JmsAttributesBuilderFactory {

  @Override
  public JmsAttributesBuilder builder() {
    return new JmsConnectorAttributeBuilder();
  }

  @Override
  public JmsHeadersBuilder headersBuilder() {
    return new JmsHeaders.Builder();
  }
}
