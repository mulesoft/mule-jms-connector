/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.event.CoreEvent;

import java.nio.charset.Charset;
import java.util.List;

import org.junit.Test;

public class JmsPayloadTypesSupportTestCase extends JmsAbstractTestCase {

  private String payloadValue = "<?xml version='1.0' encoding='UTF-8'?>\n" +
      "<employees>\n" +
      "  <name>foo</name>\n" +
      "</employees>";

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"integration/jms-listener-cursor-provider-support.xml", "config/activemq/activemq-default.xml"};
  }

  @Test
  public void supportIterator() throws Exception {
    CoreEvent run = flowRunner("publish-consume-flow").withVariable("destination", "iterator").run();
    TypedValue<Object> typedValue = run.getMessage().getPayload();
    List<Integer> payload = (List) typedValue.getValue();
    assertThat(payload, is(hasItems(3, 4, 5)));
  }

  @Test
  public void supportCursorProvider() throws Exception {
    CoreEvent run = flowRunner("publish-consume-flow").withVariable("destination", "cursor-provider").run();
    TypedValue<Object> typedValue = run.getMessage().getPayload();
    Object payload = typedValue.getValue();
    assertThat(typedValue.getDataType().getMediaType(), is(MediaType.create("application", "xml", Charset.forName("UTF-8"))));
    assertThat(payload, is(payloadValue));
  }

}
