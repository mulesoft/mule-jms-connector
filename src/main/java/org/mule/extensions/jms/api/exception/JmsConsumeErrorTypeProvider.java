/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.exception;

import static org.mule.extensions.jms.api.exception.JmsError.ACK;
import static org.mule.extensions.jms.api.exception.JmsError.CONSUMING;
import static org.mule.extensions.jms.api.exception.JmsError.DESTINATION_NOT_FOUND;
import static org.mule.extensions.jms.api.exception.JmsError.SECURITY;
import static org.mule.extensions.jms.api.exception.JmsError.TIMEOUT;

import org.mule.extensions.jms.api.config.ConsumerAckMode;
import org.mule.extensions.jms.api.destination.ConsumerType;
import org.mule.extensions.jms.internal.config.JmsConfig;
import org.mule.extensions.jms.internal.operation.JmsConsume;
import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ImmutableSet;

/**
 * Errors that can be thrown in the
 * {@link JmsConsume#consume(JmsConfig, JmsTransactionalConnection, String, ConsumerType, ConsumerAckMode, String, String, String, Long, TimeUnit)}
 * operation operation.
 *
 * @since 1.0
 */
public class JmsConsumeErrorTypeProvider implements ErrorTypeProvider {

  @Override
  public Set<ErrorTypeDefinition> getErrorTypes() {
    return ImmutableSet.<ErrorTypeDefinition>builder()
        .add(CONSUMING)
        .add(TIMEOUT)
        .add(DESTINATION_NOT_FOUND)
        .add(ACK)
        .add(SECURITY)
        .build();
  }
}

