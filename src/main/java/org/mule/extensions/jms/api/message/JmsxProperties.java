/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.message;

/**
 * JMS reserves the 'JMSX' property name prefix for JMS defined properties. Here we
 * define the set of 'well known' properties of JMS.
 *
 * JMSX properties 'set by provider on send' are available to both the producer and
 * the consumers of the message. If they are not present in a message, they are treated
 * like any other absent property.
 *
 *
 * @since 1.0
 */
public class JmsxProperties extends org.mule.jms.commons.api.message.JmsxProperties {

}
