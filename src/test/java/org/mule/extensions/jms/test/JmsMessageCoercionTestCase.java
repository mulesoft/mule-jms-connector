/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test;

import static java.lang.System.lineSeparator;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.runtime.api.metadata.MediaType.ANY;
import static org.mule.runtime.api.metadata.MediaType.TEXT;

import org.mule.runtime.api.metadata.TypedValue;
import org.mule.tck.junit4.rule.SystemProperty;

import java.io.ByteArrayInputStream;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.Rule;
import org.junit.Test;

@Feature(JMS_EXTENSION)
@Story("Coercion of the input body to a JMS Message")
public class JmsMessageCoercionTestCase extends JmsAbstractTestCase {

  @Rule
  public SystemProperty destination = new SystemProperty("destination", newDestination("destination"));

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"mimeType/jms-mime-type-propagation.xml", "config/activemq/activemq-default.xml"};
  }

  @Test
  public void sendJson() throws Exception {
    TypedValue evaluate = muleContext.getExpressionManager().evaluate("output application/json --- { key: 'object'}");
    publish(evaluate.getValue(), evaluate.getDataType().getMediaType());
    TypedValue payload = consume().getPayload();
    assertThat(payload.getValue(), is("{\n  \"key\": \"object\"\n}"));
  }

  @Test
  public void sendXml() throws Exception {
    TypedValue evaluate =
        muleContext.getExpressionManager().evaluate("output application/xml encoding=\"UTF-8\" --- xml : { key: 'object'}");
    publish(evaluate.getValue(), evaluate.getDataType().getMediaType());
    TypedValue payload = consume().getPayload();
    assertThat(payload.getValue(), is("<?xml version='1.0' encoding='UTF-8'?>\n<xml>\n  <key>object</key>\n</xml>"));
  }

  @Test
  public void sendCsv() throws Exception {
    TypedValue evaluate = muleContext.getExpressionManager().evaluate("output application/csv --- [{col1 : 1}, {col1: 2}]");
    publish(evaluate.getValue(), evaluate.getDataType().getMediaType());
    TypedValue payload = consume().getPayload();
    assertThat(payload.getValue(), is("col1" + lineSeparator() + "1" + lineSeparator() + "2" + lineSeparator()));
  }

  @Test
  public void sendTextPlain() throws Exception {
    String textValue = "this is a text plain value";
    publish(new ByteArrayInputStream(textValue.getBytes()), TEXT);
    TypedValue payload = consume().getPayload();
    assertThat(payload.getValue(), is(textValue));
  }

  @Test
  public void nonTextStreamIsSendAsByteArray() throws Exception {
    byte[] byteArray = "{\n  \"key\": \"object\"\n}".getBytes();
    publish(new ByteArrayInputStream(byteArray), ANY);
    TypedValue payload = consume().getPayload();
    assertThat(payload.getValue(), is(byteArray));
  }
}
