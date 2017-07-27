/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
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

  /**
   * {@inheritDoc}
   */
  public JmsExtensionException(String message) {
    super(message, ANY);
  }

  /**
   * {@inheritDoc}
   */
  public JmsExtensionException(Exception exception, String message) {
    super(message, ANY, exception);
  }

  /**
   * Creates a new instance with the specified detail {@code message}
   *
   * @param message the detail message
   * @param errorType JMS error
   */
  protected JmsExtensionException(String message, JmsErrors errorType) {
    super(message, errorType);
  }

  /**
   * Creates a new instance with the specified detail {@code message}
   *
   * @param exception cause of this exception
   * @param message the detail message
   * @param errorType JMS error
   */
  protected JmsExtensionException(Exception exception, JmsErrors errorType, String message) {
    super(message, errorType, exception);
  }

}
