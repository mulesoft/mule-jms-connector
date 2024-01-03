/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.integration;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.MESSAGE_TYPES;

import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.junit4.rule.SystemPropertyLambda;
import org.mule.tck.testmodels.fruit.Apple;

import java.awt.Color;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.Rule;
import org.junit.Test;

@Feature(JMS_EXTENSION)
@Story(MESSAGE_TYPES)
public class JmsMessageTypesTestCase extends JmsAbstractTestCase {

  @Rule
  public SystemProperty destination = new SystemPropertyLambda("destination", () -> newDestination("destination"));

  @Rule
  public SystemProperty finalDest = new SystemPropertyLambda("finalDest", () -> newDestination("finalDest"));

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"config/activemq/activemq-trust-all-packages.xml", "integration/jms-message-types.xml"};
  }

  @Test
  @Description("Verifies that text messages could be sent and received from JMS destinations")
  public void testTextMessage() throws Exception {
    publish(TEST_MESSAGE, destination.getValue());

    Object messagePayload = consume(finalDest.getValue()).getPayload().getValue();
    assertThat(messagePayload, instanceOf(String.class));
    assertThat(messagePayload, is(TEST_MESSAGE));
  }

  @Test
  @Description("Verifies that map messages could be sent and received from JMS destinations")
  public void testMapMessage() throws Exception {
    String value1 = "Value1";
    byte[] value2 = {1, 2, 3};
    Double value3 = new Double(99.999);

    Map testMessage = new HashMap();
    testMessage.put("Key1", value1);
    testMessage.put("Key2", value2);
    testMessage.put("Key3", value3);

    publish(testMessage, destination.getValue());
    Object messagePayload = consume(finalDest.getValue()).getPayload().getValue();
    assertThat(messagePayload, instanceOf(Map.class));

    Map message = (Map) messagePayload;
    assertThat(message.get("Key1"), is(value1));
    assertThat(message.get("Key2"), is(value2));
    assertThat(message.get("Key3"), is(value3));
  }

  @Test
  @Description("Verifies that list messages could be sent and received from JMS destinations")
  public void testListMessage() throws Exception {
    List<String> testMessage = Arrays.asList("value1", "value2");
    publish(testMessage, destination.getValue());

    Object messagePayload = consume(finalDest.getValue()).getPayload().getValue();
    assertThat(messagePayload, instanceOf(List.class));
    assertThat(messagePayload, is(testMessage));
  }

  @Test
  @Description("Verifies that binary messages could be sent and received from JMS destinations")
  public void testBinaryMessage() throws Exception {
    byte[] testMessage = new byte[] {'\u0000', '\u007F', '\u0033', '\u007F', '\u0055'};
    publish(testMessage, destination.getValue());

    Object messagePayload = consume(finalDest.getValue()).getPayload().getValue();
    assertThat(messagePayload, instanceOf(byte[].class));
    assertThat(messagePayload, is(testMessage));
  }

  @Test
  @Description("Verifies that serializable messages from JDK classes could be sent and received from JMS destinations")
  public void testJDKSerializableMessage() throws Exception {
    Serializable testMessage = new Color(0);
    publish(testMessage, destination.getValue());

    Object messagePayload = consume(finalDest.getValue()).getPayload().getValue();
    assertThat(messagePayload, instanceOf(Color.class));
    assertThat(messagePayload, is(testMessage));
  }

  @Test
  @Description("Verifies that serializable messages from custom classes could be sent and received from JMS destinations")
  public void testCustomSerializableMessage() throws Exception {
    Serializable testMessage = new Apple();
    publish(testMessage, destination.getValue());

    Object messagePayload = consume(finalDest.getValue()).getPayload().getValue();
    assertThat(messagePayload, instanceOf(Apple.class));
    assertThat(messagePayload, is(testMessage));
  }

}
