/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.exception;

import static java.util.Optional.ofNullable;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.Optional;

/**
 * Errors for the JMS extension
 * 
 * @since 1.0
 */
public enum JmsError implements ErrorTypeDefinition<JmsError> {

  PUBLISHING, ILLEGAL_BODY(PUBLISHING),

  CONSUMING, ACK(CONSUMING), TIMEOUT(CONSUMING), SESSION_RECOVER(CONSUMING),

  DESTINATION_NOT_FOUND,

  MISSING_LIBRARIES,

  SECURITY;

  private ErrorTypeDefinition<?> parentErrorType;

  JmsError(ErrorTypeDefinition parentErrorType) {
    this.parentErrorType = parentErrorType;
  }

  JmsError() {}

  @Override
  public Optional<ErrorTypeDefinition<? extends Enum<?>>> getParent() {
    return ofNullable(parentErrorType);
  }
}
