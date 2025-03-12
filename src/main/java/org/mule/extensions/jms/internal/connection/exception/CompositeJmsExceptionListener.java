/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.connection.exception;

import org.mule.extensions.jms.internal.source.JmsListener;

import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Composite pattern implementation for {@link ExceptionListener}. This gives the capability of register multiple
 * {@link ExceptionListener} for the same exception notification, this is useful to be able to notify all the {@link JmsListener}
 * that has been created with the same {@link JmsConnection}.
 *
 * @since 1.0
 */
public class CompositeJmsExceptionListener implements ExceptionListener {

  private List<ExceptionListener> exceptionListenerList = new CopyOnWriteArrayList<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public void onException(JMSException exception) {
    exceptionListenerList.forEach(listener -> listener.onException(exception));
  }

  /**
   * Registers a {@link ExceptionListener} to be notified if an connection error occurs
   * 
   * @param exceptionListener The listener to register
   */
  public void registerExceptionListener(ExceptionListener exceptionListener) {
    exceptionListenerList.add(exceptionListener);
  }
}
