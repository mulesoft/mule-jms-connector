/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.exception;

import static org.mule.extensions.jms.api.exception.JmsError.DESTINATION_NOT_FOUND;
import static org.mule.extensions.jms.api.exception.JmsError.ILLEGAL_BODY;
import static org.mule.extensions.jms.api.exception.JmsError.PUBLISHING;
import static org.mule.extensions.jms.api.exception.JmsError.SECURITY;
import org.mule.extensions.jms.api.destination.DestinationType;
import org.mule.extensions.jms.api.message.JmsMessageBuilder;
import org.mule.extensions.jms.internal.publish.JmsPublishParameters;
import org.mule.extensions.jms.internal.config.JmsConfig;
import org.mule.extensions.jms.internal.operation.JmsPublish;
import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Errors that can be thrown in the
 * {@link JmsPublish#publish(JmsConfig, JmsTransactionalConnection, String, DestinationType, JmsMessageBuilder, JmsPublishParameters)} operation
 * operation.
 *
 * @since 1.0
 */
public class JmsPublisherErrorTypeProvider implements ErrorTypeProvider {

  @Override
  public Set<ErrorTypeDefinition> getErrorTypes() {
    return ImmutableSet.<ErrorTypeDefinition>builder()
        .add(PUBLISHING)
        .add(ILLEGAL_BODY)
        .add(DESTINATION_NOT_FOUND)
        .add(SECURITY)
        .build();
  }
}

