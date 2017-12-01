/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.JmsMessageStorage.cleanUpQueue;
import static org.mule.extensions.jms.test.JmsMessageStorage.pollEvent;
import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.tck.junit4.rule.SystemProperty;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.Rule;
import org.junit.Test;

@Feature(JMS_EXTENSION)
@Story("Manual Acknowledgement over sessions")
public class JmsListenerCorrelationIdTestCase extends JmsAbstractTestCase {

  public static final String MY_PAYLOAD = "myPayload";
  public static final String MY_CORRELATION_ID = "myCorrelationId";

  @Rule
  public SystemProperty destination = new SystemProperty("destination", newDestination("destination"));

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"integration/jms-listener-correlation-id.xml", "config/activemq/activemq-default.xml"};
  }

  @Override
  protected void doTearDown() throws Exception {
    cleanUpQueue();
    super.doTearDown();
  }

  @Override
  protected boolean isDisposeContextPerClass() {
    return false;
  }

  @Test
  @Description("Verifies that messages are published by default with the correlationId as the current event and the listener "
      + "sets that correlationId")
  public void publishWithDefaultCorrelationId() throws Exception {
    flowRunner("publisher")
        .withSourceCorrelationId(MY_CORRELATION_ID)
        .withPayload(MY_PAYLOAD)
        .run();

    CoreEvent event = pollEvent();
    assertThat(event.getMessage().getPayload().getValue(), equalTo(MY_PAYLOAD));
    assertThat(event.getCorrelationId(), equalTo(MY_CORRELATION_ID));
  }

  @Test
  @Description("Verifies that messages are published with a custom correlationId and the listener sets that correlationId")
  public void publishWithCustomCorrelationId() throws Exception {
    flowRunner("publisherWithCustomCorrelation").withPayload(MY_PAYLOAD).run();

    CoreEvent event = pollEvent();
    assertThat(event.getMessage().getPayload().getValue(), equalTo(MY_PAYLOAD));
    assertThat(event.getCorrelationId(), equalTo(MY_CORRELATION_ID));
  }

  @Test
  @Description("Verifies that messages sending of correlationId can be disabled")
  public void neverSendCorrelationId() throws Exception {
    flowRunner("neverSendCorrelationId")
        .withSourceCorrelationId(MY_CORRELATION_ID)
        .withPayload(MY_PAYLOAD)
        .run();

    CoreEvent event = pollEvent();
    assertThat(event.getMessage().getPayload().getValue(), equalTo(MY_PAYLOAD));
    assertThat(event.getCorrelationId(), not(equalTo(MY_CORRELATION_ID)));
  }

}
