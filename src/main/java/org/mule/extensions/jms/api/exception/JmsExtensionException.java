/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.exception;

import static org.mule.runtime.extension.api.error.MuleErrors.ANY;

import org.mule.runtime.extension.api.exception.ModuleException;

/**
 * Custom generic exception for JmsConnector thrown errors
 *
 * @since 1.0
 */
public class JmsExtensionException extends ModuleException {

  private static String buildMessage(String message, Exception exception) {
    return message + ". " + exception.getMessage();
  }

  /**
   * {@inheritDoc}
   */
  public JmsExtensionException(String message) {
    super(message, ANY);
  }

  /**
   * {@inheritDoc}
   */
  public JmsExtensionException(String message, Exception exception) {
    super(buildMessage(message, exception), ANY, exception);
  }

  /**
   * Creates a new instance with the specified detail {@code message}
   *
   * @param message   the detail message
   * @param errorType JMS error
   */
  protected JmsExtensionException(String message, JmsError errorType) {
    super(message, errorType);
  }

  /**
   * Creates a new instance with the specified detail {@code message}
   *
   * @param message   the detail message
   * @param errorType JMS error
   * @param exception cause of this exception
   */
  protected JmsExtensionException(String message, JmsError errorType, Exception exception) {
    super(buildMessage(message, exception), errorType, exception);
  }
}
