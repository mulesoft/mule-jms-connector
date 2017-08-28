/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.exception;

import static org.mule.extensions.jms.api.exception.JmsError.SESSION_RECOVER;
import org.mule.runtime.extension.api.exception.ModuleException;

/**
 * {@link ModuleException} to be thrown in the cases in which recovering a session fails
 *
 * @since 1.0
 */
public final class JmsSessionRecoverException extends JmsConsumeException {

  /**
   * Creates a new instance with the specified detail {@code message}
   *
   * @param message the detail message
   */
  public JmsSessionRecoverException(String message) {
    super(message, SESSION_RECOVER);
  }

  /**
   * Creates a new instance with the specified detail {@code message}
   *
   * @param message the detail message
   * @param exception cause of this exception
   */
  public JmsSessionRecoverException(String message, Exception exception) {
    super(message, SESSION_RECOVER, exception);
  }
}
