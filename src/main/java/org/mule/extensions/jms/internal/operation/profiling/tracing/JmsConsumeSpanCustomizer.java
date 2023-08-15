/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.operation.profiling.tracing;

import static org.mule.jms.commons.internal.common.JmsCommons.getDestinationType;

import org.mule.jms.commons.api.destination.ConsumerType;
import org.mule.jms.commons.internal.connection.JmsTransactionalConnection;
import org.mule.sdk.api.runtime.source.DistributedTraceContextManager;

import java.util.Locale;

public class JmsConsumeSpanCustomizer extends JmsSpanCustomizer {

  private static final String SPAN_OPERATION_NAME = "receive";
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
    distributedTraceContextManager
        .addCurrentSpanAttribute(MESSAGING_DESTINATION_KIND, getDestinationType(consumerType).toLowerCase(Locale.ROOT));
  }

  @Override
  protected String getSpanOperation() {
    return SPAN_OPERATION_NAME;
  }
}
