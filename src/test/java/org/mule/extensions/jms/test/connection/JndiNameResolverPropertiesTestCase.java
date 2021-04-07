/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.connection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mule.extensions.jms.test.TestUtils.setField;

import org.junit.Test;
import org.mule.extensions.jms.api.connection.factory.jndi.JndiConnectionFactory;
import org.mule.extensions.jms.api.connection.factory.jndi.JndiNameResolverProperties;

import java.util.HashMap;
import java.util.Map;

public class JndiNameResolverPropertiesTestCase {

  private static String JNDI_CONTEXT_FIELD = "jndiInitialContextFactory";
  private static String JNDI_PROVIDER_URL_FIELD = "jndiProviderUrl";
  private static String JNDI_PROVIDER_PROPERTIES_FIELD = "providerProperties";

  @Test
  public void hashCodeReturnsSameHashForEqualNameResolverProperties() throws NoSuchFieldException, IllegalAccessException {
    JndiNameResolverProperties jndiNameResolverProperties = new JndiNameResolverProperties();
    assertThat(jndiNameResolverProperties.hashCode(), equalTo(jndiNameResolverProperties.hashCode()));

    JndiNameResolverProperties otherJndiNameResolverProperties = new JndiNameResolverProperties();
    assertThat(jndiNameResolverProperties.hashCode(), equalTo(otherJndiNameResolverProperties.hashCode()));

    JndiNameResolverProperties thirdJndiNameResolverProperties = createJndiNameResolverProperties();
    assertThat(jndiNameResolverProperties.hashCode(), not(equalTo(thirdJndiNameResolverProperties.hashCode())));
  }

  @Test
  public void equalsMethodReturnsFalse() throws NoSuchFieldException, IllegalAccessException {
    JndiNameResolverProperties jndiNameResolverProperties = createJndiNameResolverProperties();
    assertThat(jndiNameResolverProperties, not(equalTo(null)));

    JndiConnectionFactory anObjectOfDifferentClass = new JndiConnectionFactory();
    assertThat(jndiNameResolverProperties, not(equalTo(anObjectOfDifferentClass)));

    JndiNameResolverProperties anotherSetOfNameResolverProperties = createJndiNameResolverProperties();

    Map<String, Object> propertiesMapB = new HashMap();
    propertiesMapB.put("propB", 2);

    setField(anotherSetOfNameResolverProperties, anotherSetOfNameResolverProperties.getClass(), JNDI_PROVIDER_PROPERTIES_FIELD,
             propertiesMapB);
    assertThat(jndiNameResolverProperties, not(equalTo(anotherSetOfNameResolverProperties)));
  }

  @Test
  public void equalsMethodReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
    JndiNameResolverProperties jndiNameResolverProperties = createJndiNameResolverProperties();
    JndiNameResolverProperties anotherSetOfNameResolverProperties = createJndiNameResolverProperties();

    assertThat(jndiNameResolverProperties, equalTo(anotherSetOfNameResolverProperties));

    //object is equal to itself
    assertThat(jndiNameResolverProperties, equalTo(jndiNameResolverProperties));
  }

  private JndiNameResolverProperties createJndiNameResolverProperties() throws NoSuchFieldException, IllegalAccessException {
    JndiNameResolverProperties jndiNameResolverProperties = new JndiNameResolverProperties();
    Map<String, Object> propertiesMap = new HashMap();
    propertiesMap.put("propA", 1);

    setField(jndiNameResolverProperties, jndiNameResolverProperties.getClass(), JNDI_CONTEXT_FIELD, "some.jndi.context");
    setField(jndiNameResolverProperties, jndiNameResolverProperties.getClass(), JNDI_PROVIDER_URL_FIELD,
             "some.jndi.provider.url");
    setField(jndiNameResolverProperties, jndiNameResolverProperties.getClass(), JNDI_PROVIDER_PROPERTIES_FIELD, propertiesMap);
    return jndiNameResolverProperties;
  }
}
