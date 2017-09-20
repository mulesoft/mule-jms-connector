package org.mule.extensions.jms.api.exception;

import static org.mule.extensions.jms.api.exception.JmsError.MISSING_LIBRARIES;

/**
 * Exception which is thrown when a required library was not found.
 *
 * @since 1.0
 */
public class JmsMissingLibraryException extends JmsExtensionException {

  /**
   * Creates a new exception
   *
   * @param exception Exception cause
   * @param message   The error message
   */
  public JmsMissingLibraryException(Exception exception, String message) {
    super(message, MISSING_LIBRARIES, exception);
  }
}
