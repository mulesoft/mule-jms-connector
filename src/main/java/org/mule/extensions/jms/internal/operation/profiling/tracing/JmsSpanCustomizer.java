/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.operation.profiling.tracing;

import static org.mule.extensions.jms.internal.operation.profiling.tracing.SpanCustomizerUtils.safeExecute;

import static org.slf4j.LoggerFactory.getLogger;

import org.mule.jms.commons.internal.connection.JmsTransactionalConnection;
import org.mule.sdk.api.runtime.source.DistributedTraceContextManager;

import java.util.Locale;

import javax.jms.JMSException;

import org.slf4j.Logger;

public abstract class JmsSpanCustomizer {

  private static final Logger LOGGER = getLogger(JmsSpanCustomizer.class);
  public static final String MESSAGING_SYSTEM = "messaging.system";
  public static final String MESSAGING_DESTINATION = "messaging.destination";

  protected void customizeSpan(DistributedTraceContextManager distributedTraceContextManager,
                               JmsTransactionalConnection connection, String destination) {
    safeExecute(() -> distributedTraceContextManager.setCurrentSpanName(destination + " " + getSpanOperation()),
                "Span name according to semantic conventions could not be added to span", LOGGER);
    safeExecute(() -> distributedTraceContextManager
        .addCurrentSpanAttribute(MESSAGING_SYSTEM, getMessagingSystem(connection)),
                "Messaging system data could not be added to span", LOGGER);
    safeExecute(() -> distributedTraceContextManager.addCurrentSpanAttribute(MESSAGING_DESTINATION, destination),
                "Messaging destination data could not be added to span", LOGGER);
  }

  protected abstract String getSpanOperation();

  private String getMessagingSystem(JmsTransactionalConnection connection) {
    try {
      if (connection != null && connection.get() != null) {
        return connection.get().getMetaData().getJMSProviderName().toLowerCase(Locale.ROOT);
      }
    } catch (JMSException e) {
      LOGGER.info("Span connection metadata could not be fetched");
    }
    return null;
  }
}
