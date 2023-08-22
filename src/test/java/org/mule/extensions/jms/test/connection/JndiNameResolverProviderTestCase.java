/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.connection;

import org.junit.Test;
import org.mule.extensions.jms.api.connection.factory.jndi.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mule.extensions.jms.test.TestUtils.setField;

public class JndiNameResolverProviderTestCase {

  private static String NAME_RESOLVER_BUILDER_FIELD = "nameResolverBuilder";
  private static String CUSTOM_NAME_RESOLVER_FIELD = "customJndiNameResolver";

  private static String JNDI_PROVIDER_URL_FIELD = "jndiProviderUrl";

  @Test
  public void hashCodeReturnsSameHashForEqualJndiNameResolverProvider() throws NoSuchFieldException, IllegalAccessException {
    JndiNameResolverProvider jndiNameResolverProvider = createJndiNameResolverProvider();
    JndiNameResolverProvider anotherJndiNameResolverProvider = createJndiNameResolverProvider();

    assertThat(jndiNameResolverProvider.hashCode(), equalTo(jndiNameResolverProvider.hashCode()));

    CachedJndiNameResolver cachedJndiNameResolver = new CachedJndiNameResolver();
    cachedJndiNameResolver.setJndiProviderUrl("some.other.jndi.provider.url");
    setField(anotherJndiNameResolverProvider, anotherJndiNameResolverProvider.getClass(), CUSTOM_NAME_RESOLVER_FIELD,
             cachedJndiNameResolver);
    assertThat(jndiNameResolverProvider.hashCode(), not(equalTo(anotherJndiNameResolverProvider.hashCode())));
  }

  @Test
  public void equalsMethodReturnsFalse() {
    JndiNameResolverProvider jndiNameResolverProvider = new JndiNameResolverProvider();
    JndiNameResolverProperties anObjectOfAnotherClass = new JndiNameResolverProperties();

    assertThat(jndiNameResolverProvider, not(equalTo(null)));
    assertThat(jndiNameResolverProvider, not(equalTo(anObjectOfAnotherClass)));
  }

  @Test
  public void equalsMethodReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
    JndiNameResolverProvider jndiNameResolverProvider = createJndiNameResolverProvider();
    JndiNameResolverProvider anotherJndiNameResolverProvider = createJndiNameResolverProvider();

    assertThat(jndiNameResolverProvider, equalTo(anotherJndiNameResolverProvider));

    //object is equal to itself
    assertThat(jndiNameResolverProvider, equalTo(jndiNameResolverProvider));
  }

  private JndiNameResolverProvider createJndiNameResolverProvider() throws NoSuchFieldException, IllegalAccessException {
    JndiNameResolverProvider jndiNameResolverProvider = new JndiNameResolverProvider();

    JndiNameResolverProperties nameResolverProperties = new JndiNameResolverProperties();
    setField(nameResolverProperties, nameResolverProperties.getClass(), JNDI_PROVIDER_URL_FIELD, "some.jndi.provider.url");

    setField(jndiNameResolverProvider, jndiNameResolverProvider.getClass(), NAME_RESOLVER_BUILDER_FIELD, nameResolverProperties);
    setField(jndiNameResolverProvider, jndiNameResolverProvider.getClass(), CUSTOM_NAME_RESOLVER_FIELD,
             new SimpleJndiNameResolver());

    return jndiNameResolverProvider;
  }
}
