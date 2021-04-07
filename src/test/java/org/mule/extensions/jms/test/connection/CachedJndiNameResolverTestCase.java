/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.connection;

import org.junit.Test;
import org.mule.extensions.jms.api.connection.factory.jndi.CachedJndiNameResolver;

import javax.naming.InitialContext;
import javax.naming.spi.InitialContextFactory;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mule.extensions.jms.test.TestUtils.setField;

public class CachedJndiNameResolverTestCase {

  private static String CACHE_FIELD = "cache";
  private static String JNDI_PROVIDER_URL_FIELD = "jndiProviderUrl";
  private static String JNDI_INITIAL_FACTORY_FIELD = "jndiInitialFactory";
  private static String JNDI_PROVIDER_PROPERTIES_FIELD = "jndiProviderProperties";
  private static String JNDI_CONTEXT_FACTORY_FIELD = "contextFactory";

  private InitialContextFactory contextFactory = InitialContext::new;

  @Test
  public void hashCodeReturnsSameHashForEqualNameResolver() {
    CachedJndiNameResolver cachedJndiNameResolver = new CachedJndiNameResolver();
    assertThat(cachedJndiNameResolver.hashCode(), equalTo(cachedJndiNameResolver.hashCode()));

    CachedJndiNameResolver anotherCachedJndiNameResolver = new CachedJndiNameResolver();
    assertThat(cachedJndiNameResolver.hashCode(), equalTo(anotherCachedJndiNameResolver.hashCode()));
  }


  @Test
  public void equalsMethodReturnsFalse() throws NoSuchFieldException, IllegalAccessException {
    CachedJndiNameResolver cachedJndiNameResolver = new CachedJndiNameResolver();
    CachedJndiNameResolver anotherCachedJndiNameResolver = new CachedJndiNameResolver();

    assertThat(cachedJndiNameResolver, not(equalTo(null)));

    Map<String, Object> cacheA = new HashMap<>();
    Map<String, Object> cacheB = new HashMap<>();

    cacheA.put("keyA", 1);
    cacheB.put("keyB", 2);

    setField(cachedJndiNameResolver, cachedJndiNameResolver.getClass(), CACHE_FIELD,
             cacheA);
    setField(anotherCachedJndiNameResolver, anotherCachedJndiNameResolver.getClass(), CACHE_FIELD,
             cacheB);

    assertThat(cachedJndiNameResolver, not(equalTo(anotherCachedJndiNameResolver)));
  }

  @Test
  public void equalsMethodReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
    CachedJndiNameResolver simpleJndiNameResolver = createCachedJndiNameResolver();
    CachedJndiNameResolver anotherSimpleJndiNameResolver = createCachedJndiNameResolver();

    assertThat(simpleJndiNameResolver, equalTo(anotherSimpleJndiNameResolver));

    //object is equal to itself
    assertThat(simpleJndiNameResolver, equalTo(simpleJndiNameResolver));
  }

  private CachedJndiNameResolver createCachedJndiNameResolver() throws NoSuchFieldException, IllegalAccessException {
    CachedJndiNameResolver cachedJndiNameResolver = new CachedJndiNameResolver();

    setField(cachedJndiNameResolver, cachedJndiNameResolver.getClass().getSuperclass(), JNDI_PROVIDER_URL_FIELD,
             "some.jndi.provider.url");
    setField(cachedJndiNameResolver, cachedJndiNameResolver.getClass().getSuperclass(), JNDI_INITIAL_FACTORY_FIELD,
             "someJndiFactory");
    setField(cachedJndiNameResolver, cachedJndiNameResolver.getClass().getSuperclass(), JNDI_PROVIDER_PROPERTIES_FIELD,
             new HashMap());
    setField(cachedJndiNameResolver, cachedJndiNameResolver.getClass().getSuperclass(), JNDI_CONTEXT_FACTORY_FIELD,
             contextFactory);

    setField(cachedJndiNameResolver, cachedJndiNameResolver.getClass(), CACHE_FIELD,
             new HashMap<>());

    return cachedJndiNameResolver;
  }
}
