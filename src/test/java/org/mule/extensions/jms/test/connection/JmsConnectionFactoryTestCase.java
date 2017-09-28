/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.connection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.CONNECTION_FACTORY;

import org.mule.extensions.jms.test.JmsAbstractTestCase;
import org.mule.extensions.jms.test.infra.JmsTestConnectionFactory;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.Test;

@Feature(JMS_EXTENSION)
@Story(CONNECTION_FACTORY)
public class JmsConnectionFactoryTestCase extends JmsAbstractTestCase {

  @Override
  protected String getConfigFile() {
    return "config/generic/custom-connection-factory-properties.xml";
  }

  @Test
  @Description("verify that connection factory properties are actually passed to the underlying ConnectionFactory.")
  public void testConnectionFactoryPropertiesPassed() throws Exception {
    assertThat(registry.lookupByType(JmsTestConnectionFactory.class).get().getConnectionFactoryProperty(), is("TEST_VALUE"));
  }
}
