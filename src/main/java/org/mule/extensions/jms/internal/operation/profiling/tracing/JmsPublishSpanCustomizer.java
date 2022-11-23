/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.operation.profiling.tracing;

import static org.mule.extensions.jms.internal.operation.profiling.tracing.SpanCustomizerUtils.safeExecute;

import static org.slf4j.LoggerFactory.getLogger;

import org.mule.extensions.jms.api.destination.DestinationType;
import org.mule.jms.commons.internal.connection.JmsTransactionalConnection;
import org.mule.sdk.api.runtime.source.DistributedTraceContextManager;

import java.util.Locale;

import org.slf4j.Logger;

public class JmsPublishSpanCustomizer extends JmsSpanCustomizer {

  private static final Logger LOGGER = getLogger(JmsPublishSpanCustomizer.class);

  private static final String SPAN_OPERATION_NAME = "send";
  public static final String MESSAGING_DESTINATION_KIND = "messaging.destination_kind";

  /**
   * @return a new instance of a {@link JmsPublishSpanCustomizer}.
   */
  public static JmsPublishSpanCustomizer getJmsPublishSpanCustomizer() {
    return new JmsPublishSpanCustomizer();
  }

  public void customizeSpan(DistributedTraceContextManager distributedTraceContextManager, JmsTransactionalConnection connection,
                            String destination,
                            DestinationType destinationType) {
    super.customizeSpan(distributedTraceContextManager, connection, destination);
    safeExecute(() -> distributedTraceContextManager
        .addCurrentSpanAttribute(MESSAGING_DESTINATION_KIND, destinationType.toString().toLowerCase(Locale.ROOT)),
                "Messaging destination kind data could not be added to span", LOGGER);
  }

  @Override
  protected String getSpanOperation() {
    return SPAN_OPERATION_NAME;
  }
}
