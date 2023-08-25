/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test;

import java.lang.reflect.Field;

public class TestUtils {

  public static void setField(Object cc, Class objectClass, String field, Object value)
      throws NoSuchFieldException, IllegalAccessException {
    Field f1 = objectClass.getDeclaredField(field);
    f1.setAccessible(true);
    f1.set(cc, value);
  }

}
