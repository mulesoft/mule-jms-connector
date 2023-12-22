/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.connection.factory.activemq;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

import org.mule.extensions.jms.internal.ExcludeFromGeneratedCoverage;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.sdk.api.annotation.semantics.connectivity.ExcludeFromConnectivitySchema;
import org.mule.sdk.api.annotation.semantics.connectivity.Url;

import java.util.List;
import java.util.Objects;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;

/**
 * Contains the parameters required to configure an {@link ActiveMQConnectionFactory}
 *
 * @since 1.0
 */
public class ActiveMQConnectionFactoryConfiguration {

  private static final String DEFAULT_BROKER_URL = "vm://localhost?broker.persistent=false&broker.useJmx=false";

  /**
   * The address of the broker to connect
   */
  @Parameter
  @Optional(defaultValue = DEFAULT_BROKER_URL)
  @Expression(NOT_SUPPORTED)
  @Example("tcp://localhost:61616")
  @Url
  private String brokerUrl;

  /**
   * {@code true} if the {@link ConnectionFactory} should support XA
   */
  @Parameter
  @Alias("enable-xa")
  @Optional(defaultValue = "false")
  @Expression(NOT_SUPPORTED)
  private boolean enableXA;

  /**
   * Used to configure the {@link RedeliveryPolicy#getInitialRedeliveryDelay()}
   */
  @Parameter
  @Optional(defaultValue = "1000")
  @Expression(NOT_SUPPORTED)
  @Summary("Configures the ActiveMQ 'initialRedeliveryDelay' in the consumer's RedeliveryPolicy")
  @ExcludeFromConnectivitySchema
  private long initialRedeliveryDelay;

  /**
   * Used to configure the {@link RedeliveryPolicy#getRedeliveryDelay()}
   */
  @Parameter
  @Optional(defaultValue = "1000")
  @Expression(NOT_SUPPORTED)
  @Summary("Configures the ActiveMQ 'redeliveryDelay' in the consumer's RedeliveryPolicy")
  @ExcludeFromConnectivitySchema
  private long redeliveryDelay;

  /**
   * Used to configure the {@link RedeliveryPolicy#getMaximumRedeliveries()}
   * No redelivery is represented with 0, while -1 means infinite re deliveries accepted.
   */
  @Parameter
  @Optional(defaultValue = "0")
  @Expression(NOT_SUPPORTED)
  @Summary("Configures the ActiveMQ 'maxRedelivery' in the consumer's RedeliveryPolicy")
  @ExcludeFromConnectivitySchema
  private int maxRedelivery;

  /**
   * Allowed packages of classes to send and receive.
   * Starting with versions 5.12.2 and 5.13.0, ActiveMQ requires you to explicitly allow packages that can be exchanged using ObjectMessages.
   */
  @Parameter
  @Optional
  @Summary("List of packages of classes that are allowed sent and received.")
  @Expression(NOT_SUPPORTED)
  @ExcludeFromConnectivitySchema
  private List<String> trustedPackages;

  /**
   * Indicates if any class from any package can be sent and received. Enabling this parameter is unsafe, as a malicious payload can exploit the host system.
   * Starting with versions 5.12.2 and 5.13.0, ActiveMQ requires you to explicitly allow packages that can be exchanged using ObjectMessages.
   */
  @Parameter
  @Optional(defaultValue = "false")
  @Summary("Indicates whether any class from any package can be sent and received or not as a ObjectMessage." +
      "\nEnabling this is unsafe as malicious payload can exploit the host system.")
  @Expression(NOT_SUPPORTED)
  @ExcludeFromConnectivitySchema
  private boolean trustAllPackages;

  @ExcludeFromGeneratedCoverage
  public int getMaxRedelivery() {
    return maxRedelivery;
  }

  @ExcludeFromGeneratedCoverage
  public void setMaxRedelivery(int maxRedelivery) {
    this.maxRedelivery = maxRedelivery;
  }

  @ExcludeFromGeneratedCoverage
  public boolean isEnableXA() {
    return enableXA;
  }

  @ExcludeFromGeneratedCoverage
  public boolean getEnableXA() {
    return enableXA;
  }

  @ExcludeFromGeneratedCoverage
  public void setEnableXA(boolean enableXA) {
    this.enableXA = enableXA;
  }

  @ExcludeFromGeneratedCoverage
  public String getBrokerUrl() {
    return brokerUrl;
  }

  @ExcludeFromGeneratedCoverage
  public void setBrokerUrl(String brokerUrl) {
    this.brokerUrl = brokerUrl;
  }

  @ExcludeFromGeneratedCoverage
  public long getInitialRedeliveryDelay() {
    return initialRedeliveryDelay;
  }

  @ExcludeFromGeneratedCoverage
  public void setInitialRedeliveryDelay(long initialRedeliveryDelay) {
    this.initialRedeliveryDelay = initialRedeliveryDelay;
  }

  @ExcludeFromGeneratedCoverage
  public long getRedeliveryDelay() {
    return redeliveryDelay;
  }

  @ExcludeFromGeneratedCoverage
  public void setRedeliveryDelay(long redeliveryDelay) {
    this.redeliveryDelay = redeliveryDelay;
  }

  @ExcludeFromGeneratedCoverage
  public List<String> getTrustedPackages() {
    return trustedPackages;
  }

  @ExcludeFromGeneratedCoverage
  public void setTrustedPackages(List<String> trustedPackages) {
    this.trustedPackages = trustedPackages;
  }

  @ExcludeFromGeneratedCoverage
  public boolean isTrustAllPackages() {
    return trustAllPackages;
  }

  @ExcludeFromGeneratedCoverage
  public boolean getTrustAllPackages() {
    return trustAllPackages;
  }

  @ExcludeFromGeneratedCoverage
  public void setTrustAllPackages(boolean trustAllPackages) {
    this.trustAllPackages = trustAllPackages;
  }

  public boolean getVerifyHostName() {
    //TODO: change the default value to a parameter. This default is required to support a security fix and avoid
    // breaking backwards compatibility (GUS ticket: W-14487260)
    return false;
  }


  @Override
  @ExcludeFromGeneratedCoverage
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ActiveMQConnectionFactoryConfiguration that = (ActiveMQConnectionFactoryConfiguration) o;
    return enableXA == that.enableXA &&
        initialRedeliveryDelay == that.initialRedeliveryDelay &&
        redeliveryDelay == that.redeliveryDelay &&
        maxRedelivery == that.maxRedelivery &&
        trustAllPackages == that.trustAllPackages &&
        Objects.equals(brokerUrl, that.brokerUrl) &&
        Objects.equals(trustedPackages, that.trustedPackages);
  }

  @Override
  @ExcludeFromGeneratedCoverage
  public int hashCode() {
    return Objects.hash(brokerUrl, enableXA, initialRedeliveryDelay, redeliveryDelay, maxRedelivery, trustedPackages,
                        trustAllPackages);
  }
}
