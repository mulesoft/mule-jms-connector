/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test;

import java.io.IOException;
import java.io.Serializable;

public class PojoThatDoesntDeserialize implements Serializable {

  private static int times = 0;

  public PojoThatDoesntDeserialize() {
    throw new RuntimeException();
  }

  public PojoThatDoesntDeserialize(String blaba) {

  }

  public static int getCount() {
    return times;
  }

  public static int reset() {
    return times = 0;
  }

  private void readObject(java.io.ObjectInputStream in)
      throws IOException, ClassNotFoundException {
    times = times + 1;
    if (times < 5) {
      System.out.println(times);
      throw new RuntimeException();
    }
    in.defaultReadObject();
  }
}
