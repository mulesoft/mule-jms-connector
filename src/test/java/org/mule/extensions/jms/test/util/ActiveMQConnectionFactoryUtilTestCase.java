/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.util;

import org.junit.Test;
import org.mule.extensions.jms.internal.util.ActiveMQConnectionFactoryUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ActiveMQConnectionFactoryUtilTestCase {

  @Test
  public void testVersionValid() {
    assertFalse(ActiveMQConnectionFactoryUtil.isVerifyHostnameValidVersion("5.14.5"));
    assertFalse(ActiveMQConnectionFactoryUtil.isVerifyHostnameValidVersion("4.10.5"));
    assertTrue(ActiveMQConnectionFactoryUtil.isVerifyHostnameValidVersion("5.15.6"));
    assertTrue(ActiveMQConnectionFactoryUtil.isVerifyHostnameValidVersion("5.15.8"));
    assertTrue(ActiveMQConnectionFactoryUtil.isVerifyHostnameValidVersion("5.18.3"));
    assertTrue(ActiveMQConnectionFactoryUtil.isVerifyHostnameValidVersion("6.0.0"));
    assertFalse(ActiveMQConnectionFactoryUtil.isVerifyHostnameValidVersion(null));
    assertFalse(ActiveMQConnectionFactoryUtil.isVerifyHostnameValidVersion("5.14"));
  }

  @Test
  public void testBrokerUrlFormatWithQueryParameters() {
    assertEquals("failover:(ssl://localhost:61616?socket.verifyHostName=false,ssl://localhost:61616?socket.verifyHostName=false)?maxReconnectAttempts=5&useExponentialBackOff=false&randomize=false&jms.prefetchPolicy.all=5",
                 ActiveMQConnectionFactoryUtil.brokerUrlFormat(
                                                               "failover:(ssl://localhost:61616,ssl://localhost:61616)?maxReconnectAttempts=5&useExponentialBackOff=false&randomize=false&jms.prefetchPolicy.all=5",
                                                               false));
  }

  @Test
  public void testBrokerUrlFormatWithOutQueryParameters() {
    assertEquals("failover:(ssl://localhost:61616?socket.verifyHostName=false,ssl://localhost:61616?socket.verifyHostName=false)",
                 ActiveMQConnectionFactoryUtil.brokerUrlFormat("failover:(ssl://localhost:61616,ssl://localhost:61616)", false));
  }

  @Test
  public void testBrokerUrlFormatVerifyHostNameTrue() {
    assertEquals("failover:(ssl://localhost:61616?socket.verifyHostName=true,ssl://localhost:61616?socket.verifyHostName=true)",
                 ActiveMQConnectionFactoryUtil.brokerUrlFormat("failover:(ssl://localhost:61616,ssl://localhost:61616)", true));
  }

  @Test
  public void testBrokerUrlFormatWitSingleURL() {
    assertEquals("failover:(ssl://localhost:61616?socket.verifyHostName=false)?maxReconnectAttempts=5",
                 ActiveMQConnectionFactoryUtil.brokerUrlFormat("failover:(ssl://localhost:61616)?maxReconnectAttempts=5", false));
  }

  @Test
  public void testBrokerUrlFormatWithBlankSpace() {
    assertEquals("failover:(ssl://localhost:61616?socket.verifyHostName=false,ssl://localhost:61616?socket.verifyHostName=false)",
                 ActiveMQConnectionFactoryUtil.brokerUrlFormat("failover:( ssl://localhost:61616,ssl://localhost:61616)", false));
  }

  @Test
  public void testBrokerUrlFormatWithEmptyURL() {
    assertEquals("",
                 ActiveMQConnectionFactoryUtil.brokerUrlFormat("", false));
  }

  @Test
  public void testBrokerUrlFormatWithNullURL() {
    assertEquals("",
                 ActiveMQConnectionFactoryUtil.brokerUrlFormat(null, false));
  }

  @Test
  public void testBrokerUrlFormatWhenHostNameHasVerifyHostnameParameter() {
    assertEquals("failover:(ssl://localhost:61616?socket.verifyHostName=false,ssl://localhost:61616?socket.verifyHostName=false)",
                 ActiveMQConnectionFactoryUtil.brokerUrlFormat(
                                                               "failover:(ssl://localhost:61616?socket.verifyHostName=false,ssl://localhost:61616),false",
                                                               false));
  }

  @Test
  public void testBrokerUrlFormatWhenHostNameHasOthersParameter() {
    assertEquals("failover:(ssl://localhost:61616?maxReconnectAttempts=5&socket.verifyHostName=false,ssl://localhost:61616?socket.verifyHostName=false)",
                 ActiveMQConnectionFactoryUtil.brokerUrlFormat(
                                                               "failover:(ssl://localhost:61616?maxReconnectAttempts=5,ssl://localhost:61616?socket.verifyHostName=false)",
                                                               false));
  }

  @Test
  public void testBrokerUrlFormatWhenHostNameHasParameterAndFailoverQueryParameter() {
    assertEquals("failover:(ssl://localhost:61616?maxReconnectAttempts=5&socket.verifyHostName=false,ssl://localhost:61616?socket.verifyHostName=false)?maxReconnectAttempts=5",
                 ActiveMQConnectionFactoryUtil.brokerUrlFormat(
                                                               "failover:(ssl://localhost:61616?maxReconnectAttempts=5,ssl://localhost:61616?socket.verifyHostName=false)?maxReconnectAttempts=5",
                                                               false));
  }
}
