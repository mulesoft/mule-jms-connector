/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extensions.jms.test.integration;

import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JMS_EXTENSION;
import static org.mule.extensions.jms.test.AllureConstants.JmsFeature.JmsStory.MESSAGE_FILTERING;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@Feature(JMS_EXTENSION)
@Story(MESSAGE_FILTERING)
public class JmsMessageFilteringTestCase extends JmsAbstractMessageFilteringTestCase {

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"config/activemq/activemq-default.xml", "integration/jms-message-filtering.xml"};
  }

}
