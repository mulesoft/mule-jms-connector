/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal;

import org.mule.extensions.jms.api.connection.caching.CachingStrategy;
import org.mule.extensions.jms.api.connection.caching.DefaultCachingStrategy;
import org.mule.extensions.jms.api.connection.caching.NoCachingConfiguration;
import org.mule.extensions.jms.api.connection.factory.jndi.CachedJndiNameResolver;
import org.mule.extensions.jms.api.connection.factory.jndi.JndiConnectionFactory;
import org.mule.extensions.jms.api.connection.factory.jndi.JndiNameResolver;
import org.mule.extensions.jms.api.connection.factory.jndi.SimpleJndiNameResolver;
import org.mule.extensions.jms.api.destination.ConsumerType;
import org.mule.extensions.jms.api.destination.QueueConsumer;
import org.mule.extensions.jms.api.destination.TopicConsumer;
import org.mule.extensions.jms.api.exception.JmsError;
import org.mule.extensions.jms.api.exception.JmsExceptionHandler;
import org.mule.extensions.jms.api.message.JmsAttributes;
import org.mule.extensions.jms.internal.config.JmsConfig;
import org.mule.extensions.jms.internal.connection.provider.GenericConnectionProvider;
import org.mule.extensions.jms.internal.connection.provider.activemq.ActiveMQConnectionNCTProvider;
import org.mule.extensions.jms.internal.connection.provider.activemq.ActiveMQConnectionProvider;
import org.mule.extensions.jms.internal.lifecycle.JmsArtifactLifecycleListener;
import org.mule.extensions.jms.internal.operation.JmsAcknowledge;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Export;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.OnException;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.SubTypeMapping;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.sdk.api.annotation.JavaVersionSupport;
import org.mule.sdk.api.annotation.OnArtifactLifecycle;
import org.mule.sdk.api.meta.JavaVersion;

import javax.jms.ConnectionFactory;


/**
 * JmsConnector is a JMS 1.0.2b, 1.1 and 2.0 compliant MuleSoft Extension, used to consume and produce JMS Messages.
 * The Extension supports all JMS functionality including topics and queues, durable subscribers, acknowledgement modes
 * and local transactions.
 *
 * @since 1.0
 */
@Extension(name = "JMS")
@Xml(prefix = "jms")
@Configurations({JmsConfig.class})
@ConnectionProviders({GenericConnectionProvider.class, ActiveMQConnectionProvider.class, ActiveMQConnectionNCTProvider.class})
@Operations(JmsAcknowledge.class)
@SubTypeMapping(
    baseType = ConsumerType.class, subTypes = {QueueConsumer.class, TopicConsumer.class})
@SubTypeMapping(
    baseType = CachingStrategy.class, subTypes = {DefaultCachingStrategy.class, NoCachingConfiguration.class})
@SubTypeMapping(
    baseType = ConnectionFactory.class, subTypes = {JndiConnectionFactory.class})
@SubTypeMapping(
    baseType = JndiNameResolver.class, subTypes = {SimpleJndiNameResolver.class, CachedJndiNameResolver.class})
@ErrorTypes(JmsError.class)
@OnException(JmsExceptionHandler.class)
@Export(classes = JmsAttributes.class)
@OnArtifactLifecycle(JmsArtifactLifecycleListener.class)
@JavaVersionSupport({JavaVersion.JAVA_8, JavaVersion.JAVA_11, JavaVersion.JAVA_17})
public class JmsConnector {

}
