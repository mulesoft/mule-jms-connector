/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.mule.extensions.jms.api.destination.DestinationType;
import org.mule.extensions.jms.api.destination.JmsDestination;
import org.mule.extensions.jms.api.message.JmsAttributes;
import org.mule.extensions.jms.api.message.JmsHeaders;
import org.mule.extensions.jms.internal.message.JmsConnectorAttributeBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.junit.Test;

public class JmsAttributesSerializationTestCase {

  @Test
  public void serializeJmsAttributes() throws IOException, ClassNotFoundException {
    JmsHeaders.Builder headersBuilder = new JmsHeaders.Builder();
    headersBuilder.setDestination(new JmsDestination("name", DestinationType.QUEUE));
    HashMap<String, Object> properties = new HashMap<>();
    properties.put("123", 123);

    JmsAttributes jmsAttributes = new JmsConnectorAttributeBuilder()
        .withAckId("123")
        .withHeaders(headersBuilder.build())
        .withProperties(properties)
        .build();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
    objectOutputStream.writeObject(jmsAttributes);

    Object object = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray())).readObject();
    assertThat(object, is(instanceOf(JmsAttributes.class)));
  }
}
