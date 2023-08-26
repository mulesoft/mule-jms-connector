/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.exception;

import static org.mule.extensions.jms.api.exception.JmsError.ILLEGAL_BODY;
import org.mule.runtime.extension.api.exception.ModuleException;

/**
 * {@link ModuleException} to be thrown in the cases in which a message body invalid or cannot be converted to a supported type
 *
 * @since 1.0
 */
public final class JmsIllegalBodyException extends JmsPublishException {

  /**
   * Creates a new instance with the specified detail {@code message}
   *
   * @param message the detail message
   */
  public JmsIllegalBodyException(String message) {
    super(message, ILLEGAL_BODY);
  }

  /**
   * Creates a new instance with the specified detail {@code message}
   *
   * @param message the detail message
   * @param exception cause of this exception
   */
  public JmsIllegalBodyException(String message, Exception exception) {
    super(message, ILLEGAL_BODY, exception);
  }
}
