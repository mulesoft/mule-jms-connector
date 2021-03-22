/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.connection;

import static java.lang.Thread.currentThread;
import static org.hamcrest.Matchers.notNullValue;

import static org.mockito.Matchers.any;
import static org.mule.extensions.jms.internal.common.JmsCommons.JMS_THREAD_GROUP_NAME;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.extensions.jms.internal.connection.provider.activemq.ActiveMQConnectionProvider;
import org.mule.extensions.jms.internal.source.JmsListener;
import org.mule.jms.commons.internal.connection.provider.JmsConnectionProvider;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(PowerMockRunner.class)
@PrepareForTest({org.mule.jms.commons.internal.source.JmsListener.Builder.class, JmsListener.class})
@Feature("JMS Connector")
@Story("Create JMS connection with different thread group")
@Issue("JMSC-83")
public class JmsConnectionCreationTestCase {

  @Test
  public void activeMQConnectionCreation() throws Exception {
    AtomicReference<String> atomicReference = new AtomicReference<>();

    ActiveMQConnectionProvider activeMQConnectionProvider = new ActiveMQConnectionProvider();
    JmsConnectionProvider jmsConnectionProvider = mock(JmsConnectionProvider.class);
    setField(activeMQConnectionProvider, ActiveMQConnectionProvider.class.getSuperclass(), "jmsConnectionProvider",
             jmsConnectionProvider);

    doAnswer(invocationOnMock -> {
      atomicReference.set(currentThread().getThreadGroup().getName());
      return null;
    }).when(jmsConnectionProvider).connect();

    activeMQConnectionProvider.connect();
    verifyCreationInThread(atomicReference);
  }

  @Test
  public void jmsListenerCreation() throws Exception {
    org.mule.jms.commons.internal.source.JmsListener.Builder builder =
        mock(org.mule.jms.commons.internal.source.JmsListener.Builder.class);
    org.mule.jms.commons.internal.source.JmsListener jmsListener =
        mock(org.mule.jms.commons.internal.source.JmsListener.class);
    whenNew(org.mule.jms.commons.internal.source.JmsListener.Builder.class).withAnyArguments().thenReturn(builder);
    when(builder.setExceptionResolver(any())).thenReturn(builder);
    when(builder.setResourceReleaser(any())).thenReturn(builder);
    when(builder.setListenerLockFactory(any())).thenReturn(builder);
    when(builder.setReconnectionManager(any())).thenReturn(builder);
    when(builder.build()).thenReturn(jmsListener);

    JmsListener jmsListenerToTest = new JmsListener();
    SourceCallback sourceCallback = mock(SourceCallback.class);
    AtomicReference<String> atomicReference = new AtomicReference<>();

    doAnswer(invocationOnMock -> {
      atomicReference.set(currentThread().getThreadGroup().getName());
      return null;
    }).when(jmsListener).onStart(sourceCallback);

    jmsListenerToTest.onStart(sourceCallback);
    verifyCreationInThread(atomicReference);

  }

  private void verifyCreationInThread(AtomicReference<String> atomicReference) {
    assertThat(atomicReference.get(), is(notNullValue()));
    assertThat(atomicReference.get(), is(JMS_THREAD_GROUP_NAME));
  }

  private void setField(Object cc, Class objectClass, String field, Object value)
      throws NoSuchFieldException, IllegalAccessException {
    Field f1 = objectClass.getDeclaredField(field);
    f1.setAccessible(true);
    f1.set(cc, value);
  }
}
