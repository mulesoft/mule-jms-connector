/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.exception;

import org.mule.runtime.api.exception.MuleRuntimeException;

import jakarta.jms.Connection;
import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;

/**
 * Custom {@link MuleRuntimeException} to be used in a JMS {@link Connection} {@link ExceptionListener}
 *
 * @since 1.0
 */
public final class JmsCallbackConnectionException extends MuleRuntimeException {

  public JmsCallbackConnectionException(JMSException e) {
    super(e);
  }
}
