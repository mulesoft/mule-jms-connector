/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.message;

import static com.google.common.collect.ImmutableMap.copyOf;
import static org.mule.extensions.jms.internal.message.JMSXDefinedPropertiesNames.JMSX_NAMES;
import static org.mule.runtime.api.util.Preconditions.checkArgument;
import org.mule.extensions.jms.internal.message.JmsxPropertiesBuilder;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;

/**
 * Container element for all the properties present in a JMS {@link Message}.
 * <p>
 * This container not only allows to fetch the all properties in a single Map representation,
 * but also provides accessors for the properties according to their origin.
 * Properties may be those predefined by JMS (the {@link JmsxProperties}),
 * those that are used by the JMS broker or provider (known as plain JMS properties),
 * and finally the ones provided by the User who created the {@link Message}.
 *
 * @since 1.0
 */
public class JmsMessageProperties {

  private static final String JMSX_PREFIX = "JMSX";
  private static final String JMS_PREFIX = "JMS";

  /**
   * All the properties of the JMS message as a flattened map
   */
  @Parameter
  private final Map<String, Object> all;

  /**
   * The user provided properties of the JMS Message
   */
  @Parameter
  private final Map<String, Object> userProperties = new HashMap<>();

  /**
   * The broker and provider specific of the JMS Message
   */
  @Parameter
  private final Map<String, Object> jmsProperties = new HashMap<>();

  /**
   * The JMSX properties of the JMS Message
   */
  @Parameter
  private JmsxProperties jmsxProperties;

  public JmsMessageProperties(Map<String, Object> messageProperties) {
    checkArgument(messageProperties != null, "Initializer properties Map expected, but it was null");

    all = copyOf(messageProperties);
    JmsxPropertiesBuilder jmsxPropertiesBuilder = JmsxPropertiesBuilder.create();

    all.entrySet().forEach(e -> {
      String key = e.getKey();
      if (key.startsWith(JMSX_PREFIX) && JMSX_NAMES.contains(key)) {
        jmsxPropertiesBuilder.add(key, e.getValue());

      } else if (key.startsWith(JMS_PREFIX)) {
        jmsProperties.put(key, e.getValue());

      } else {
        userProperties.put(key, e.getValue());
      }
    });

    jmsxProperties = jmsxPropertiesBuilder.build();
  }

  public Map<String, Object> asMap() {
    return copyOf(all);
  }

  public Map<String, Object> getUserProperties() {
    return copyOf(userProperties);
  }

  public Map<String, Object> getJmsProperties() {
    return copyOf(jmsProperties);
  }

  public JmsxProperties getJmsxProperties() {
    return jmsxProperties;
  }

  @Override
  public boolean equals(Object o) {
    return all.equals(o);
  }

  @Override
  public int hashCode() {
    return all.hashCode();
  }

}
