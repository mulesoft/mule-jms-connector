/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.exception;

import static org.mule.extensions.jms.api.exception.JmsError.DESTINATION_NOT_FOUND;

import javax.jms.Destination;

/**
 * Custom Exception thrown when the extension was not able to create a given {@link Destination}
 *
 * @since 1.0
 */
public final class DestinationNotFoundException extends JmsExtensionException {

  public DestinationNotFoundException(String message) {
    super(message, DESTINATION_NOT_FOUND);
  }

}
