/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.operation.profiling.tracing;

import static org.mule.extensions.jms.internal.operation.profiling.tracing.SpanCustomizerUtils.safeExecute;
import static org.mule.jms.commons.internal.common.JmsCommons.getDestinationType;

import static org.slf4j.LoggerFactory.getLogger;

import org.mule.jms.commons.api.destination.ConsumerType;
import org.mule.jms.commons.internal.connection.JmsTransactionalConnection;
import org.mule.sdk.api.runtime.source.DistributedTraceContextManager;

import java.util.Locale;

import org.slf4j.Logger;

public class JmsConsumeSpanCustomizer extends JmsSpanCustomizer {

  private static final Logger LOGGER = getLogger(JmsConsumeSpanCustomizer.class);

  private static final String SPAN_OPERATION_NAME = "receive";
  private static final String SPAN_KIND_NAME = "CONSUMER";
  public static final String MESSAGING_DESTINATION_KIND = "messaging.destination_kind";

  /**
   * @return a new instance of a {@link JmsConsumeSpanCustomizer}.
   */
  public static JmsConsumeSpanCustomizer getJmsConsumeSpanCustomizer() {
    return new JmsConsumeSpanCustomizer();
  }

  public void customizeSpan(DistributedTraceContextManager distributedTraceContextManager, JmsTransactionalConnection connection,
                            String destination, ConsumerType consumerType) {
    super.customizeSpan(distributedTraceContextManager, connection, destination);
    safeExecute(() -> distributedTraceContextManager
        .addCurrentSpanAttribute(MESSAGING_DESTINATION_KIND, getDestinationType(consumerType).toLowerCase(Locale.ROOT)),
                "Messaging destination kind data could not be added to span", LOGGER);
  }

  @Override
  protected String getSpanOperation() {
    return SPAN_OPERATION_NAME;
  }

  @Override
  protected String getSpanKind() {
    return SPAN_KIND_NAME;
  }
}
