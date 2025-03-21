/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.ssl;

import java.net.URI;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

/**
 * Creates and starts a new ActiveMQ Server using SSL to secure connections
 */
public class ActiveMQSSLServer {

  private static final String ACTIVEMQ_PORT = "activemq.port";
  private static BrokerService brokerService;
  private static boolean started = false;
  private static final String FIPS140_2 = "fips140-2";
  private static final String MULE_SECURITY_MODEL_PROPERTY = "mule.security.model";

  public static void start(String port) throws Exception {
    if (!started) {
      System.setProperty(ACTIVEMQ_PORT, port);
      try {
        if (FIPS140_2.equals(System.getProperty(MULE_SECURITY_MODEL_PROPERTY))) {
          brokerService = BrokerFactory.createBroker(new URI("xbean:activemq-fips.xml"));
        } else {
          brokerService = BrokerFactory.createBroker(new URI("xbean:activemq.xml"));
        }
        brokerService.start();
      } finally {
        System.clearProperty(ACTIVEMQ_PORT);
      }
      started = true;
    }
  }

  public static void stop() throws Exception {
    if (started) {
      brokerService.stop();
      started = false;
    }
  }
}
