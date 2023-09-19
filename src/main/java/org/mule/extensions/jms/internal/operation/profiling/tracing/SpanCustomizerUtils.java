/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.operation.profiling.tracing;

import org.slf4j.Logger;

public class SpanCustomizerUtils {

  /**
   * Safely executes a piece of logic.
   *
   * @param toExecute           the piece of logic to execute.
   * @param loggingMessage      the logging message if a throwable
   * @param logger              logger used for informing tracing errors.
   */
  public static void safeExecute(Runnable toExecute, String loggingMessage, Logger logger) {
    try {
      toExecute.run();
    } catch (Throwable e) {
      if (logger.isWarnEnabled()) {
        logger.warn(loggingMessage, e);
      }
    }
  }
}
