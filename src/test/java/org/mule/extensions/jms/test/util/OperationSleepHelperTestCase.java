/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.util;

import org.junit.Test;
import org.mule.extensions.jms.internal.util.OperationSleepHelper;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OperationSleepHelperTestCase {

  @Test
  public void testSleepWithoutException() {
    OperationSleepHelper helper = new OperationSleepHelper();
    try {
      helper.sleep(1000);
    } catch (Exception e) {
      fail("There is any exception");
    }
  }

  @Test
  public void testSleepWithException() {
    OperationSleepHelper helper = new OperationSleepHelper();
    Thread.currentThread().interrupt();
    helper.sleep(100);
    assertTrue(Thread.currentThread().isInterrupted());
  }
}
