/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.operation.profiling.tracing;

import static org.slf4j.LoggerFactory.getLogger;

import org.mule.jms.commons.internal.connection.JmsTransactionalConnection;
import org.mule.sdk.api.runtime.source.DistributedTraceContextManager;

import java.util.Locale;

import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;

import org.slf4j.Logger;

public abstract class JmsSpanCustomizer {

  private static final Logger LOGGER = getLogger(JmsSpanCustomizer.class);
  public static final String MESSAGING_SYSTEM = "messaging.system";
  public static final String MESSAGING_DESTINATION = "messaging.destination";

  protected void customizeSpan(DistributedTraceContextManager distributedTraceContextManager,
                               JmsTransactionalConnection connection, String destination) {
    ConnectionMetaData connectionMetaData;
    try {
      connectionMetaData = connection.get().getMetaData();
      distributedTraceContextManager
          .addCurrentSpanAttribute(MESSAGING_SYSTEM, connectionMetaData.getJMSProviderName());
    } catch (JMSException ignored) {
      LOGGER.info("Span connection metadata could not be fetched");
    }
    distributedTraceContextManager.setCurrentSpanName(destination + " " + getSpanOperation());
    distributedTraceContextManager.addCurrentSpanAttribute(MESSAGING_DESTINATION, destination);
  }

  protected abstract String getSpanOperation();
}
