/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.util;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;

import java.io.IOException;

public class BadProxy {

  private final static ToxiproxyClient client = new ToxiproxyClient();
  private static Proxy brokerProxy;

  public static void generateConnectivityIssue(String realPort, String realHost, String proxyPort) throws IOException {
    // Creates a proxy to create the same effect as a server having connectivity issues
    System.out.println("Creating proxy from 0.0.0.0:" + proxyPort + " to " + realHost + ":" + realPort);
    brokerProxy = client.getProxyOrNull(proxyPort + "-" + realPort);
    if (brokerProxy == null) {
      brokerProxy = client.createProxy(proxyPort + "-" + realPort, "0.0.0.0:" + proxyPort, realHost + ":" + realPort);
    }
    brokerProxy.disable();
  }

  public static void resolveConnectivityIssue() throws IOException {
    brokerProxy.enable();
  }

}
