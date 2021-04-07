/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.connection;

import static org.mockito.Mockito.mock;
import static org.mule.extensions.jms.api.connection.LookupJndiDestination.ALWAYS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mule.extensions.jms.test.TestUtils.setField;

import org.junit.Test;
import org.mule.extensions.jms.api.connection.factory.jndi.JndiConnectionFactory;
import org.mule.extensions.jms.api.connection.factory.jndi.JndiNameResolverProvider;
import org.mule.extensions.jms.api.connection.factory.jndi.SimpleJndiNameResolver;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;

public class JndiConnectionFactoryTestCase {

  private static String CONNECTION_FACTORY_NAME_FIELD = "connectionFactoryJndiName";
  private static String LOOKUP_DESTINATION_FIELD = "lookupDestination";
  private static String NAME_RESOLVER_PROVIDER_FIELD = "nameResolverProvider";
  private static String NAME_RESOLVER_FIELD = "nameResolver";
  private static String CONNECTION_FACTORY_FIELD = "connectionFactory";

  @Test
  public void equalJndiConnectionFactoriesGenerateSameHashCode() throws NoSuchFieldException, IllegalAccessException {
    MyCustomConnectionFactory connFactory = new MyCustomConnectionFactory();
    JndiConnectionFactory jndiConnectionFactory = createFullJndiConnectionFactory(connFactory);

    assertThat(jndiConnectionFactory.hashCode(), equalTo(jndiConnectionFactory.hashCode()));

    JndiConnectionFactory otherJndiConnectionFactory = createFullJndiConnectionFactory(connFactory);

    assertThat(jndiConnectionFactory.hashCode(), equalTo(otherJndiConnectionFactory.hashCode()));
  }

  @Test
  public void equalsMethodReturnsFalse() {
    JndiConnectionFactory jndiConnectionFactory = new JndiConnectionFactory();

    assertThat(jndiConnectionFactory, not(equalTo(null)));

    MyCustomConnectionFactory anObjectOfADifferentClass = new MyCustomConnectionFactory();
    assertThat(jndiConnectionFactory, not(equalTo(anObjectOfADifferentClass)));
  }

  @Test
  public void equalsMethodReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
    MyCustomConnectionFactory connFactory = new MyCustomConnectionFactory();
    JndiConnectionFactory jndiConnectionFactory = createSimpleJndiConnectionFactory();
    JndiConnectionFactory otherJndiConnectionFactory = createSimpleJndiConnectionFactory();

    assertThat(jndiConnectionFactory, equalTo(otherJndiConnectionFactory));

    jndiConnectionFactory = createFullJndiConnectionFactory(connFactory);
    otherJndiConnectionFactory = createFullJndiConnectionFactory(connFactory);

    assertThat(jndiConnectionFactory, equalTo(otherJndiConnectionFactory));

    //object is equal to itself
    assertThat(jndiConnectionFactory, equalTo(jndiConnectionFactory));
  }

  private JndiConnectionFactory createSimpleJndiConnectionFactory() throws NoSuchFieldException, IllegalAccessException {
    JndiConnectionFactory jndiConnectionFactory = new JndiConnectionFactory();

    setField(jndiConnectionFactory, jndiConnectionFactory.getClass(), CONNECTION_FACTORY_NAME_FIELD,
             "myJndiConnFactory");
    setField(jndiConnectionFactory, jndiConnectionFactory.getClass(), LOOKUP_DESTINATION_FIELD, ALWAYS);

    return jndiConnectionFactory;
  }

  private JndiConnectionFactory createFullJndiConnectionFactory(MyCustomConnectionFactory connFactory)
      throws NoSuchFieldException, IllegalAccessException {
    JndiConnectionFactory jndiConnectionFactory = createSimpleJndiConnectionFactory();
    setField(jndiConnectionFactory, jndiConnectionFactory.getClass(), NAME_RESOLVER_PROVIDER_FIELD,
             new JndiNameResolverProvider());

    setField(jndiConnectionFactory, jndiConnectionFactory.getClass(), NAME_RESOLVER_FIELD,
             new SimpleJndiNameResolver());

    setField(jndiConnectionFactory, jndiConnectionFactory.getClass(), CONNECTION_FACTORY_FIELD,
             connFactory);

    return jndiConnectionFactory;
  }

  private class MyCustomConnectionFactory implements ConnectionFactory {

    private JMSContext context = mock(JMSContext.class);
    private Connection conn = mock(Connection.class);

    @Override
    public Connection createConnection() throws JMSException {
      return conn;
    }

    @Override
    public Connection createConnection(String s, String s1) throws JMSException {
      return conn;
    }

    @Override
    public JMSContext createContext() {
      return context;
    }

    @Override
    public JMSContext createContext(String s, String s1) {
      return context;
    }

    @Override
    public JMSContext createContext(String s, String s1, int i) {
      return context;
    }

    @Override
    public JMSContext createContext(int i) {
      return context;
    }
  }

}
