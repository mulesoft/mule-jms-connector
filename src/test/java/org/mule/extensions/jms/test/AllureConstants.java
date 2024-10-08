/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test;

public class AllureConstants {

  public interface JmsFeature {

    String JMS_EXTENSION = "JMS Extension";

    interface JmsStory {

      String CORRELATION_ID = "Correlation Id";

      String RECONNECTION = "Reconnection";

      String REDELIVERY = "Redelivery";

      String CONNECTION_FACTORY = "Connection Factory";

      String TRANSACTION = "Transaction";

      String DURABLE_SUBSCRIBER = "Durable Subscriber";

      String MESSAGE_TYPES = "Durable Subscriber";

      String MESSAGE_FILTERING = "Message Filtering";
    }

  }

  public interface ActiveMQFeature {

    String ACTIVE_MQ_FEATURE = "ActiveMQ";

    interface ActiveMQStories {

      String ACTIVE_MQ_RESOURCE_RELEASING = "ActiveMQ Resource Releasing";
    }
  }
}
