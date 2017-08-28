package org.mule.extensions.jms.internal.connection.exception;

import org.mule.extensions.jms.internal.connection.JmsConnection;
import org.mule.extensions.jms.internal.source.JmsListener;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.List;

/**
 * Composite pattern implementation for {@link ExceptionListener}.
 * This gives the capability of register multiple {@link ExceptionListener} for the same exception notification,
 * this is useful to be able to notify all the {@link JmsListener} that has been created with the same {@link JmsConnection}.
 *
 * @since 1.0
 */
public class CompositeJmsExceptionListener implements ExceptionListener {

  List<ExceptionListener> exceptionListenerList = new ArrayList<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public void onException(JMSException exception) {
    exceptionListenerList.forEach(listener -> listener.onException(exception));
  }

  /**
   * Registers a {@link ExceptionListener} to be notified if an connection error occurs
   * @param exceptionListener The listener to register
   */
  public void registerExceptionListener(ExceptionListener exceptionListener) {
    exceptionListenerList.add(exceptionListener);
  }
}
