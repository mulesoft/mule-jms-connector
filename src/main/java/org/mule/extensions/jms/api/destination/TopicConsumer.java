/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.destination;

import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.extension.api.annotation.Alias;

import javax.jms.Destination;
import javax.jms.Topic;

/**
 * Implementation of {@link ConsumerType} that marks the consumed {@link Destination}
 * as a {@link Topic} and provides a way to configure topic-specific consumer parameters
 *
 * @since 1.0
 */
@Alias("topic-consumer")
public class TopicConsumer extends org.mule.jms.commons.api.destination.TopicConsumer implements Initialisable, ConsumerType {

  @Override
  public int hashCode() {
    return TopicConsumer.class.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null && obj.getClass() == TopicConsumer.class;
  }
}
