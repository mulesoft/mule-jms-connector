/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ActiveMQConnectionFactoryUtil {

  private static final int MAJOR_VERSION_THRESHOLD = 5;
  private static final int MINOR_VERSION_THRESHOLD = 15;
  private static final int PATCH_VERSION_THRESHOLD = 6;
  private static final String REX_PATTER = "(\\d+)\\.(\\d+)\\.(\\d+)";
  private static final String HOSTS_MATCHER = "\\((.*?)\\)";
  private static final String VERIFY_HOSTNAME = "socket.verifyHostName";

  /**
   * Utility that helps us determine if the activeMQ client version is lower than the limit version to configure the VerifyHostName parameter
   * **/
  public static boolean isVerifyHostnameValidVersion(String activeMqClientVersion) {
    if (activeMqClientVersion == null) {
      return false;
    }
    Pattern pattern = Pattern.compile(REX_PATTER);
    Matcher matcher = pattern.matcher(activeMqClientVersion);
    if (!matcher.matches()) {
      return false;
    }
    int majorVersion = Integer.parseInt(matcher.group(1));
    int minorVersion = Integer.parseInt(matcher.group(2));
    int patchVersion = Integer.parseInt(matcher.group(3));
    if (majorVersion > MAJOR_VERSION_THRESHOLD) {
      return true;
    } else if (majorVersion < MAJOR_VERSION_THRESHOLD) {
      return false;
    }
    if (minorVersion > MINOR_VERSION_THRESHOLD) {
      return true;
    } else if (minorVersion < MINOR_VERSION_THRESHOLD) {
      return false;
    }
    return patchVersion >= PATCH_VERSION_THRESHOLD;
  }

  /**
   * Formats a broker URL by appending the 'VERIFY_HOSTNAME'
   * parameter to each URL within the `failover:()' block and then reconciles the query parameters
   * (if any) to the final URL.
   *
   * @param brokerURL The broker URL to format. May contain a 'failover:()' section,
   * with multiple URLs separated by commas.
   * @param verifyHostName The value to be assigned to the `VERIFY_HOSTNAME` parameter for
   * brokers within the 'failover:()' block. This value is added to each
   * broker within the 'failover:()' section.
   *
   * @return The URL formatted with the `VERIFY_HOSTNAME` parameter added to each URL
   * within 'failover:()', and with the original URL's query parameters (if any)
   * preserved at the end of the URL.
   * **/

  public static String brokerUrlFormat(String brokerURL, boolean verifyHostName) {
    if (brokerURL != null) {
      int parametersIndex = brokerURL.indexOf(")?");
      String queryParameters = "";
      if (parametersIndex != -1) {
        queryParameters = brokerURL.substring(parametersIndex + 1);
      }
      Pattern pattern = Pattern.compile(HOSTS_MATCHER);
      Matcher matcher = pattern.matcher(brokerURL);
      if (matcher.find()) {
        String failoverUrl = matcher.group(1);
        String addedVerifyHostName = Arrays.stream(failoverUrl.split(","))
            .map(element -> {
              element = element.trim();
              if (element.contains(VERIFY_HOSTNAME)) {
                return element;
              }
              if (element.contains("?")) {
                return element + "&" + VERIFY_HOSTNAME + "=" + verifyHostName;
              } else {
                return element + "?" + VERIFY_HOSTNAME + "=" + verifyHostName;
              }
            }).collect(Collectors.joining(","));
        brokerURL = "failover:(" + addedVerifyHostName + ")";
      }
      if (!queryParameters.isEmpty()) {
        brokerURL += queryParameters;
      }
      return brokerURL;
    }
    return "";
  }
}
