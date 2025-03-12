/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.destination;

import org.mule.runtime.extension.api.annotation.Alias;

import jakarta.jms.Destination;
import jakarta.jms.Queue;

/**
 * Implementation of {@link ConsumerType} that marks the consumed {@link Destination} as a {@link Queue}.
 *
 * @since 1.0
 */
@Alias("queue-consumer")
public class QueueConsumer extends org.mule.jms.commons.api.destination.QueueConsumer implements ConsumerType {

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean topic() {
    return false;
  }

  @Override
  public int hashCode() {
    return QueueConsumer.class.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null && obj.getClass() == QueueConsumer.class;
  }
}
