/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.connection;

import org.mule.extensions.jms.internal.JmsConnector;

/**
 * Versions of the JMS Spec supported by the {@link JmsConnector}
 *
 * @since 1.0
 */
public enum JmsSpecification {
  JMS_1_0_2b(org.mule.jms.commons.api.connection.JmsSpecification.JMS_1_0_2b),

  JMS_1_1(org.mule.jms.commons.api.connection.JmsSpecification.JMS_1_1),

  JMS_2_0(org.mule.jms.commons.api.connection.JmsSpecification.JMS_2_0);

  private final org.mule.jms.commons.api.connection.JmsSpecification spec;

  JmsSpecification(org.mule.jms.commons.api.connection.JmsSpecification spec) {
    this.spec = spec;
  }

  public org.mule.jms.commons.api.connection.JmsSpecification getJmsSpecification() {
    return spec;
  }
}
