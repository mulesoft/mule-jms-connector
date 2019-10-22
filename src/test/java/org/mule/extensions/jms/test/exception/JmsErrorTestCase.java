/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.exception;

import static java.util.Collections.emptyMap;
import static org.mule.extensions.jms.api.exception.JmsError.ILLEGAL_BODY;
import static org.mule.extensions.jms.api.exception.JmsError.TIMEOUT;

import org.mule.extensions.jms.test.JmsAbstractTestCase;

import org.junit.Ignore;
import org.junit.Test;

public class JmsErrorTestCase extends JmsAbstractTestCase {

  public static final String TEST_DESTINATION = "test";
  public static final String AN_ERROR_OCCURRED_WHILE_SENDING_A_MESSAGE = "An error occurred while sending a message";
  public static final String AN_ERROR_OCCURRED_WHILE_CONSUMING_A_MESSAGE = "An error occurred while consuming a message";
  public static final String NULL_BODY = "Message body was 'null', which is not a value of a supported type";

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"config/activemq/activemq-default.xml", "exception/jms-error-flow.xml"};
  }

  @Test
  public void nullMessageBody() throws Exception {
    expectedError.expectError(NAMESPACE, ILLEGAL_BODY.getType(), Exception.class, NULL_BODY);
    destination = newDestination(TEST_DESTINATION);

    publish(null);
  }

  @Test
  @Ignore
  public void timeout() throws Exception {
    expectedError.expectError(NAMESPACE, TIMEOUT.getType(), Exception.class,
                              AN_ERROR_OCCURRED_WHILE_CONSUMING_A_MESSAGE);
    destination = newDestination(TEST_DESTINATION);
    consume(destination, emptyMap(), 5);
  }
}
