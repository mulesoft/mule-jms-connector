/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test;

import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.runtime.api.metadata.DataType.BYTE_ARRAY;
import static org.mule.runtime.api.metadata.DataType.JSON_STRING;
import static org.mule.runtime.api.metadata.DataType.OBJECT;
import static org.mule.runtime.api.metadata.DataType.STRING;

import org.mule.jms.commons.api.exception.JmsIllegalBodyException;
import org.mule.jms.commons.internal.message.JmsMessageUtils;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@Feature(JMS_EXTENSION)
@Story("JMS Message Utils")
@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class JmsMessageUtilsTestCase extends AbstractMuleTestCase {

  private static final String STRING_VALUE = "a string value";

  @Mock
  Session session;

  @Mock
  BytesMessage bytesMessage;

  @Mock
  StreamMessage streamMessage;

  @Mock
  MapMessage mapMessage;

  @Before
  public void setUp() throws JMSException {
    when(session.createBytesMessage()).thenReturn(bytesMessage);
    when(session.createStreamMessage()).thenReturn(streamMessage);
    when(session.createMapMessage()).thenReturn(mapMessage);
  }

  @Test(expected = JmsIllegalBodyException.class)
  public void canNotSendNullMessages() throws JMSException {
    JmsMessageUtils.toMessage(new TypedValue<>(null, STRING), session);
  }

  @Test
  public void stringMessagesIsSendAsTextMessage() throws JMSException {
    JmsMessageUtils.toMessage(new TypedValue<>(STRING_VALUE, STRING), session);
    verify(session).createTextMessage(STRING_VALUE);
  }

  @Test
  public void stringRepresentableInputStreamIsSendAsTextMessage() throws JMSException {
    ByteArrayInputStream stream = new ByteArrayInputStream(STRING_VALUE.getBytes());
    JmsMessageUtils.toMessage(new TypedValue<>(stream, JSON_STRING), session);
    verify(session).createTextMessage(STRING_VALUE);
  }

  @Test
  public void nonStringRepresentableInputStreamIsSendAsByteArrayMessage() throws JMSException {
    ByteArrayInputStream stream = new ByteArrayInputStream(STRING_VALUE.getBytes());
    JmsMessageUtils.toMessage(new TypedValue<>(stream, DataType.INPUT_STREAM), session);
    verify(session).createBytesMessage();
    verify(bytesMessage).writeBytes(STRING_VALUE.getBytes());
  }

  @Test
  public void mapIsSendAsMapMessage() throws JMSException {
    String key = "KEY";
    String value = "VALUE";
    Map<String, String> stringStringMap = Collections.singletonMap(key, value);
    JmsMessageUtils.toMessage(new TypedValue<>(stringStringMap, OBJECT), session);
    verify(session).createMapMessage();
    verify(mapMessage).setObject(key, value);
  }

  @Test
  public void listIsSendAsListMessage() throws JMSException {
    List<String> list = Collections.singletonList(STRING_VALUE);
    JmsMessageUtils.toMessage(new TypedValue<>(list, OBJECT), session);
    verify(session).createStreamMessage();
    verify(streamMessage).writeObject(STRING_VALUE);
  }

  @Test
  public void byteArrayIsSendAsByteArrayMessage() throws JMSException {
    byte[] bytes = STRING_VALUE.getBytes();
    JmsMessageUtils.toMessage(new TypedValue<>(bytes, BYTE_ARRAY), session);
    verify(session).createBytesMessage();
    verify(bytesMessage).writeBytes(aryEq(bytes));
  }
}
