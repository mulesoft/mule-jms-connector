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
