/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.config;

/**
 * Interface to be implemented in enums representing Acknowledgement modes and give their representation in
 * the {@link InternalAckMode}
 *
 * @since 1.0
 */
public interface JmsAckMode {

  /**
   * @return The {@link InternalAckMode} of the current enum value.
   */
  InternalAckMode getInternalAckMode();
}
