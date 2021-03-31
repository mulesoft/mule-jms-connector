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

import org.junit.Test;
import org.mule.extensions.jms.api.connection.factory.jndi.JndiConnectionFactory;
import org.mule.extensions.jms.api.connection.factory.jndi.JndiNameResolverProvider;
import org.mule.extensions.jms.api.connection.factory.jndi.SimpleJndiNameResolver;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import java.lang.reflect.Field;

public class JndiConnectionFactoryTestCase {

  //@Test
  //public void equalJndiConnectionFactoryGenerateSameHashCode() {

  //}

  @Test
  public void equalJndiConnectionFactoryGenerateEqualMethodReturnsTrue() throws NoSuchFieldException, IllegalAccessException {
    JndiConnectionFactory jndiConnectionFactory = new JndiConnectionFactory();
    JndiNameResolverProvider nameResolverProvider = new JndiNameResolverProvider();
    MyCustomConnectionFactory connFactory = new MyCustomConnectionFactory();
    SimpleJndiNameResolver nameResolver = new SimpleJndiNameResolver();

    setField(jndiConnectionFactory, jndiConnectionFactory.getClass(), "connectionFactoryJndiName",
            "myJndiConnFactory");
    setField(jndiConnectionFactory, jndiConnectionFactory.getClass(), "lookupDestination", ALWAYS);
    setField(jndiConnectionFactory, jndiConnectionFactory.getClass(), "nameResolver",
            nameResolver);
    setField(jndiConnectionFactory, jndiConnectionFactory.getClass(), "nameResolverProvider",
            nameResolverProvider);
    setField(jndiConnectionFactory, jndiConnectionFactory.getClass(), "connectionFactory",
            connFactory);

    JndiConnectionFactory otherJndiConnectionFactory = new JndiConnectionFactory();

    setField(otherJndiConnectionFactory, otherJndiConnectionFactory.getClass(), "connectionFactoryJndiName",
            "myJndiConnFactory");
    setField(otherJndiConnectionFactory, otherJndiConnectionFactory.getClass(), "lookupDestination", ALWAYS);
    setField(otherJndiConnectionFactory, otherJndiConnectionFactory.getClass(), "nameResolver",
            nameResolver);
    setField(otherJndiConnectionFactory, otherJndiConnectionFactory.getClass(), "nameResolverProvider",
            nameResolverProvider);
    setField(otherJndiConnectionFactory, otherJndiConnectionFactory.getClass(), "connectionFactory",
            connFactory);

    assertThat(jndiConnectionFactory, equalTo(jndiConnectionFactory));

    assertThat(jndiConnectionFactory, equalTo(otherJndiConnectionFactory));
  }

  private void setField(Object cc, Class objectClass, String field, Object value)
      throws NoSuchFieldException, IllegalAccessException {
    Field f1 = objectClass.getDeclaredField(field);
    f1.setAccessible(true);
    f1.set(cc, value);
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
