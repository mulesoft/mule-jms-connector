/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.exception;

import org.mule.jms.commons.api.exception.JmsTimeoutException;
import org.mule.runtime.extension.api.runtime.exception.ExceptionHandler;

/**
 * {@link ExceptionHandler} for the JMS extension.
 * 
 * @since 1.0
 */
public class JmsExceptionHandler extends ExceptionHandler {

  @Override
  public Exception enrichException(Exception e) {
    return getRootErrorException(e);
  }
}
