/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.destination;

import org.mule.jms.commons.api.destination.DestinationTypeDescriptor;

import jakarta.jms.Destination;
import jakarta.jms.Queue;
import jakarta.jms.Topic;

/**
 * Type identifier for a {@link Destination}.
 *
 * @since 1.0
 */
public enum DestinationType implements DestinationTypeDescriptor {

  /**
   * {@link Destination} is a {@link Queue}
   */
  QUEUE(false),

  /**
   * {@link Destination} is a {@link Topic}
   */
  TOPIC(true);

  private final boolean isTopic;

  DestinationType(boolean isTopic) {
    this.isTopic = isTopic;
  }

  public boolean isTopic() {
    return isTopic;
  }
}
