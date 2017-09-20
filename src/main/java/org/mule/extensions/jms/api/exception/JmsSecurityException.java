package org.mule.extensions.jms.api.exception;

import static org.mule.extensions.jms.api.exception.JmsError.SECURITY;

/**
 * {@link JmsExtensionException} to be thrown in the case that an Security related error occurs.
 *
 * @since 1.0
 */
public class JmsSecurityException extends JmsExtensionException {

  /**
   * Creates a new JMS Security Exception
   *
   * @param message   Message of the new exception
   * @param exception Cause of this exception
   */
  public JmsSecurityException(String message, Exception exception) {
    super(message, SECURITY, exception);
  }
}
