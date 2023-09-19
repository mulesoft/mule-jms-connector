/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.destination;

/**
 * Implementations of this interface provide a way to configure custom properties for a consumer
 * based on the destination kind from which consumption will occurr
 *
 * @since 1.0
 */
public interface ConsumerType extends org.mule.jms.commons.api.destination.ConsumerType {

}
