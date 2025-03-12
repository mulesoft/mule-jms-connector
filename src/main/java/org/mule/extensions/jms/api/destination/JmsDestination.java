/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.destination;

import static org.mule.extensions.jms.internal.common.JmsCommons.QUEUE;

import org.mule.extensions.jms.internal.ExcludeFromGeneratedCoverage;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import jakarta.jms.Destination;

/**
 * Representation of a {@link Destination} that contains it's identifier name and kind of Destination
 *
 * @since 1.0
 */
public class JmsDestination implements org.mule.jms.commons.api.destination.JmsDestination {

  /**
   * The name that identifies the destination where a reply to a message should be sent
   */
  @Parameter
  @ParameterDsl(allowReferences = false)
  @DisplayName("Destination Name")
  @Summary("It is the destination where a reply to the message should be sent")
  private String destination;

  /**
   * the type of this destination
   */
  @Parameter
  @Optional(defaultValue = QUEUE)
  private DestinationType destinationType;


  public JmsDestination() {}

  public JmsDestination(String name, DestinationType type) {
    this.destination = name;
    this.destinationType = type;
  }

  @ExcludeFromGeneratedCoverage
  public String getDestination() {
    return destination;
  }

  @ExcludeFromGeneratedCoverage
  public void setDestination(String destination) {
    this.destination = destination;
  }

  public DestinationType getDestinationType() {
    return destinationType;
  }

  @ExcludeFromGeneratedCoverage
  public void setDestinationType(DestinationType destinationType) {
    this.destinationType = destinationType;
  }
}
