/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.exception;

import static org.mule.extensions.jms.api.exception.JmsError.PUBLISHING;

/**
 * {@link JmsExtensionException} to be thrown in the cases where an error occurs when trying to publish a message
 *
 * @since 1.0
 */
public class JmsPublishException extends JmsExtensionException {

  /**
   * Creates a new instance with the specified detail {@code message}
   *
   * @param message the detail message
   */
  public JmsPublishException(String message) {
    super(message, PUBLISHING);
  }

  /**
   * Creates a new instance with the specified detail {@code message}
   *
   * @param message the detail message
   * @param exception cause of this exception
   */
  public JmsPublishException(String message, Exception exception) {
    super(message, PUBLISHING, exception);
  }


  /**
   * Creates a new instance with the specified detail {@code message}
   *
   * @param message the detail message
   * @param errorType JMS error
   */
  protected JmsPublishException(String message, JmsError errorType) {
    super(message, errorType);
  }

  /**
   * Creates a new instance with the specified detail {@code message}
   *
   * @param message the detail message
   * @param errorType JMS error
   * @param exception cause of this exception
   */
  protected JmsPublishException(String message, JmsError errorType, Exception exception) {
    super(message, errorType, exception);
  }
}
