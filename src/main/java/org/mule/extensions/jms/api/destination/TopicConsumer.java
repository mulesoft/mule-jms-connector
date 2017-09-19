/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.destination;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.api.util.Preconditions.checkArgument;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import javax.jms.Destination;
import javax.jms.Topic;

/**
 * Implementation of {@link ConsumerType} that marks the consumed {@link Destination}
 * as a {@link Topic} and provides a way to configure topic-specific consumer parameters
 *
 * @since 1.0
 */
@Alias("topic-consumer")
public class TopicConsumer implements ConsumerType, Initialisable {

  /**
   * Allows an application to receive all the messages published on a topic,
   * including the ones published when there is no consumer associated with it.
   *
   * Requires a {@code subscriptionName} to be provided
   */
  @Parameter
  @Optional(defaultValue = "false")
  @Expression(NOT_SUPPORTED)
  @Summary("Allows an application to receive all the messages published on a topic, including the ones published when there is no consumer associated with it")
  @Alias("isDurable")
  private boolean durable;

  /**
   * Only for JMS 2.0: Allows the processing of messages from at topic
   * subscription by multiple threads, connections or JVMs.
   *
   * Requires a {@code subscriptionName} to be provided
   */
  @Parameter
  @Optional(defaultValue = "false")
  @Expression(NOT_SUPPORTED)
  @Summary("Only for JMS 2.0: Allows the processing of messages from a topic subscription by multiple threads, connections or JVMs")
  @Alias("isShared")
  private boolean shared;

  /**
   * Specifies that messages published to the topic by its own connection
   * must not be added to the subscription.
   */
  @Parameter
  @Optional(defaultValue = "false")
  @Expression(NOT_SUPPORTED)
  @Summary("Specifies that messages published to the topic by its own connection must not be added to the subscription")
  private boolean noLocal;

  /**
   * the name to be used for the subscription
   */
  @Parameter
  @Optional
  @ParameterDsl(allowReferences = false)
  @Expression(NOT_SUPPORTED)
  @Summary("The name to be used for the subscription")
  private String subscriptionName;

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean topic() {
    return true;
  }

  public boolean isDurable() {
    return durable;
  }

  public boolean isShared() {
    return shared;
  }

  public String getSubscriptionName() {
    return subscriptionName;
  }

  public boolean isNoLocal() {
    return noLocal;
  }

  @Override
  public void initialise() throws InitialisationException {
    if (!isBlank(subscriptionName)) {
      checkArgument(isShared() || isDurable(),
                    "A [subscriptionName] was provided, but the subscription is neither [durable] nor [shared]");
    } else {
      checkArgument(!isShared() && !isDurable(),
                    "No [subscriptionName] was provided, but one is required to create a [durable] or [shared] subscriber");
    }

    checkArgument(!(isShared() && isNoLocal()), "A [shared] topic consumer can't be [noLocal]");
  }
}
