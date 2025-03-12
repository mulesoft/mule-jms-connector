/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.connection;

import static org.mule.extensions.jms.test.TestUtils.setField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import org.apache.camel.util.jndi.JndiContext;
import org.junit.Test;
import org.mule.extensions.jms.api.connection.factory.jndi.CachedJndiNameResolver;
import org.mule.extensions.jms.api.connection.factory.jndi.SimpleJndiNameResolver;

import javax.naming.InitialContext;
import javax.naming.spi.InitialContextFactory;
import java.util.HashMap;

public class SimpleJndiNameResolverTestCase {

  private static String JNDI_PROVIDER_URL_FIELD = "jndiProviderUrl";
  private static String JNDI_INITIAL_FACTORY_FIELD = "jndiInitialFactory";
  private static String JNDI_PROVIDER_PROPERTIES_FIELD = "jndiProviderProperties";
  private static String JNDI_CONTEXT_FACTORY_FIELD = "contextFactory";
  private static String JNDI_CONTEXT_FIELD = "jndiContext";

  private InitialContextFactory contextFactory = InitialContext::new;

  @Test
  public void hashCodeReturnsSameHashForEqualNameResolver() {
    SimpleJndiNameResolver simpleJndiNameResolver = new SimpleJndiNameResolver();
    assertThat(simpleJndiNameResolver.hashCode(), equalTo(simpleJndiNameResolver.hashCode()));

    SimpleJndiNameResolver anotherSimpleJndiNameResolver = new SimpleJndiNameResolver();
    assertThat(simpleJndiNameResolver.hashCode(), equalTo(anotherSimpleJndiNameResolver.hashCode()));
  }


  @Test
  public void equalsMethodReturnsFalse() throws NoSuchFieldException, IllegalAccessException {
    SimpleJndiNameResolver simpleJndiNameResolver = new SimpleJndiNameResolver();
    CachedJndiNameResolver cachedJndiNameResolver = new CachedJndiNameResolver();

    assertThat(simpleJndiNameResolver, not(equalTo(null)));
    assertThat(simpleJndiNameResolver, not(equalTo(cachedJndiNameResolver)));

    SimpleJndiNameResolver anotherSimpleJndiNameResolver = new SimpleJndiNameResolver();

    setField(simpleJndiNameResolver, simpleJndiNameResolver.getClass().getSuperclass(), JNDI_PROVIDER_URL_FIELD,
             "some.jndi.provider.url");
    setField(anotherSimpleJndiNameResolver, anotherSimpleJndiNameResolver.getClass().getSuperclass(), JNDI_PROVIDER_URL_FIELD,
             "some.other.jndi.provider.url");
    assertThat(simpleJndiNameResolver, not(equalTo(anotherSimpleJndiNameResolver)));
  }

  @Test
  public void equalsMethodReturnsTrue() throws Exception {
    JndiContext jndiContext = new JndiContext();
    SimpleJndiNameResolver simpleJndiNameResolver = createSimpleJndiNameResolver(jndiContext);
    SimpleJndiNameResolver anotherSimpleJndiNameResolver = createSimpleJndiNameResolver(jndiContext);

    assertThat(simpleJndiNameResolver, equalTo(anotherSimpleJndiNameResolver));

    setField(simpleJndiNameResolver, simpleJndiNameResolver.getClass(), JNDI_CONTEXT_FIELD,
             jndiContext);
    setField(anotherSimpleJndiNameResolver, anotherSimpleJndiNameResolver.getClass(), JNDI_CONTEXT_FIELD,
             jndiContext);

    // object is equal to itself
    assertThat(simpleJndiNameResolver, equalTo(simpleJndiNameResolver));
  }

  private SimpleJndiNameResolver createSimpleJndiNameResolver(JndiContext jndiContext) throws Exception {
    SimpleJndiNameResolver simpleJndiNameResolver = new SimpleJndiNameResolver();

    setField(simpleJndiNameResolver, simpleJndiNameResolver.getClass().getSuperclass(), JNDI_PROVIDER_URL_FIELD,
             "some.jndi.provider.url");
    setField(simpleJndiNameResolver, simpleJndiNameResolver.getClass().getSuperclass(), JNDI_INITIAL_FACTORY_FIELD,
             "someJndiFactory");
    setField(simpleJndiNameResolver, simpleJndiNameResolver.getClass().getSuperclass(), JNDI_PROVIDER_PROPERTIES_FIELD,
             new HashMap());
    setField(simpleJndiNameResolver, simpleJndiNameResolver.getClass().getSuperclass(), JNDI_CONTEXT_FACTORY_FIELD,
             contextFactory);

    return simpleJndiNameResolver;
  }
}
