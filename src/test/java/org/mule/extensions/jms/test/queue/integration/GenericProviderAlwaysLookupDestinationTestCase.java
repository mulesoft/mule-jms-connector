/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.queue.integration;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.extensions.jms.test.exception.JmsErrorTestCase;
import org.mule.runtime.api.message.Message;
import org.mule.test.runner.RunnerDelegateTo;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;


@Feature(JMS_EXTENSION)
@Story("Generic Connection Provider Always Lookup Destination")
public class GenericProviderAlwaysLookupDestinationTestCase extends JmsAbstractTestCase {

  private static final String FIRST_MESSAGE = "My First Message";
  private static final String SEND_PAYLOAD_FLOW = "send-payload";

  private static final String INITIAL_DESTINATION = "initialQueue";
  private static final String INITIAL_LOOKUP_DESTINATION = "jdni-queue-in";
  private static final String INITIAL_DESTINATION_VAR = "initialDestination";

  private static final String PROPERTY_KEY_VAR = "initialProperty";
  private static final String PROPERTY_KEY_VALUE = "INIT_PROPERTY";

  private static final String PROPERTY_VALUE_VAR = "propertyValue";
  private static final String PROPERTY_VALUE_VALUE = "Custom Value";

  protected final String CONFIG_XML = "operations/jms-queue-bridge.xml";

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"config/generic/jndi-destinations-always.xml", CONFIG_XML};
  }

  @Test
  public void failToFindDestination() throws Exception {
    expectedError.expectError(NAMESPACE, "DESTINATION_NOT_FOUND", Exception.class,
                              JmsErrorTestCase.AN_ERROR_OCCURRED_WHILE_SENDING_A_MESSAGE);

    flowRunner(SEND_PAYLOAD_FLOW)
        .withVariable(INITIAL_DESTINATION_VAR, INITIAL_DESTINATION)
        .withVariable(PROPERTY_KEY_VAR, PROPERTY_KEY_VALUE)
        .withVariable(PROPERTY_VALUE_VAR, PROPERTY_VALUE_VALUE)
        .withPayload(FIRST_MESSAGE)
        .run();
  }

  @Test
  public void findDestination() throws Exception {
    expectedError.expectError(NAMESPACE, "DESTINATION_NOT_FOUND", Exception.class,
                              JmsErrorTestCase.AN_ERROR_OCCURRED_WHILE_SENDING_A_MESSAGE);

    flowRunner(SEND_PAYLOAD_FLOW)
        .withVariable(INITIAL_DESTINATION_VAR, INITIAL_LOOKUP_DESTINATION)
        .withVariable(PROPERTY_KEY_VAR, PROPERTY_KEY_VALUE)
        .withVariable(PROPERTY_VALUE_VAR, PROPERTY_VALUE_VALUE)
        .withPayload(FIRST_MESSAGE)
        .run();
  }

}
