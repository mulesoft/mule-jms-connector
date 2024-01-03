/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.queue.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.runtime.core.api.event.CoreEvent;

import io.qameta.allure.Feature;
import org.junit.Test;

@Feature(JMS_EXTENSION)
public class JmsXmlQueuePublish extends JmsAbstractTestCase {

  @Override
  protected String getConfigFile() {
    return "operations/jms-queue-publish-xml.xml";
  }

  @Test
  public void publishXml() throws Exception {
    destination = newDestination("xmlDestination");

    flowRunner(PUBLISHER_FLOW).withVariable(DESTINATION_VAR, destination).run();
    CoreEvent event = flowRunner(CONSUMER_FLOW).withVariable(DESTINATION_VAR, destination).run();

    assertThat(event, is(not(nullValue())));
  }
}
