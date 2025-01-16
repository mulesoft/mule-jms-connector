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
    assertTrue(ActiveMQConnectionFactoryUtil.isVerifyHostnameValidVersion("5.18.3"));
    assertTrue(ActiveMQConnectionFactoryUtil.isVerifyHostnameValidVersion("6.0.0"));
  }

  @Test
  public void testUrlFormatWithQueryParameters() {
    assertEquals(null,
                 "failover:(ssl://localhost:61616?socket.verifyHostName=false,ssl://localhost:61616?socket.verifyHostName=false)?maxReconnectAttempts=5&useExponentialBackOff=false&randomize=false&jms.prefetchPolicy.all=5",
                 ActiveMQConnectionFactoryUtil.brokerUrlFormat(
                                                         "failover:(ssl://localhost:61616,ssl://localhost:61616)?maxReconnectAttempts=5&useExponentialBackOff=false&randomize=false&jms.prefetchPolicy.all=5",
                                                         false));
  }

  @Test
  public void testUrlFormatWithoutQueryParameters() {
    assertEquals(null,
                 "failover:(ssl://localhost:61616?socket.verifyHostName=false,ssl://localhost:61616?socket.verifyHostName=false)",
                 ActiveMQConnectionFactoryUtil.brokerUrlFormat("failover:(ssl://localhost:61616,ssl://localhost:61616)", false));
  }
}
