/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActiveMQConnectionFactoryUtil {

  private static final int MAJOR_VERSION_THRESHOLD = 5;
  private static final int MINOR_VERSION_THRESHOLD = 15;
  private static final int PATCH_VERSION_THRESHOLD = 6;
  private static final String REX_PATTER = "(\\d+)\\.(\\d+)\\.(\\d+)";

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
}
