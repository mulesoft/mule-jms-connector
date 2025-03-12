/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.internal.lifecycle;

import static org.mule.extensions.jms.test.AllureConstants.ActiveMQFeature.ACTIVE_MQ_FEATURE;
import static org.mule.extensions.jms.test.AllureConstants.ActiveMQFeature.ActiveMQStories.ACTIVE_MQ_RESOURCE_RELEASING;
import static org.mule.extensions.jms.test.util.CollectableReference.collectedByGc;
import static org.mule.extensions.jms.test.util.DependencyResolver.getDependencyFromMaven;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.getAllStackTraces;
import static java.util.stream.Collectors.toList;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.fail;

import org.mule.extensions.jms.internal.lifecycle.ActiveMQArtifactLifecycleListener;
import org.mule.extensions.jms.test.util.CollectableReference;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.net.URL;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.hamcrest.Matcher;
import org.junit.Test;

/**
 * Test case for the {@link ActiveMQArtifactLifecycleListener}.
 * 
 * @see <a href="https://github.com/apache/activemq/pull/1119">https://github.com/apache/activemq/pull/1119</a>
 */
@Feature(ACTIVE_MQ_FEATURE)
@Story(ACTIVE_MQ_RESOURCE_RELEASING)
public class ActiveMQArtifactLifecycleListenerTestCase {

  private static final URL ACTIVEMQ_DRIVER_URL = getDependencyFromMaven("org.apache.activemq",
                                                                        "activemq-client",
                                                                        "5.16.4");
  private static final String ACTIVEMQ_PACKAGE_PREFIX = "org.apache.activemq";
  private static final String ACTIVEMQ_BAD_URL_CONFIG = "tcp://localhost:61616";
  private static final String ACTIVEMQ_READ_CHECK_TIMER_THREAD_NAME = "ActiveMQ InactivityMonitor ReadCheckTimer";

  @Test
  public void whenDriverIsInAppThenClassLoadersAreNotLeakedAfterDisposal() throws Exception {
    assertClassLoadersAreNotLeakedAfterDisposal(TestClassLoadersHierarchy.Builder::withUrlsInApp,
                                                TestClassLoadersHierarchy::getAppExtensionClassLoader);
  }

  @Test
  public void whenDriverIsInAppExtensionThenClassLoadersAreNotLeakedAfterDisposal() throws Exception {
    assertClassLoadersAreNotLeakedAfterDisposal(TestClassLoadersHierarchy.Builder::withUrlsInAppExtension,
                                                TestClassLoadersHierarchy::getAppExtensionClassLoader);
  }

  @Test
  public void whenDriverIsInAppThenThreadsAreNotLeakedAfterDisposal() throws Exception {
    assertThreadsNamesAfterAppDisposal(TestClassLoadersHierarchy.Builder::withUrlsInApp,
                                       TestClassLoadersHierarchy::getAppExtensionClassLoader,
                                       not(hasReadCheckTimer()));
  }

  @Test
  public void whenDriverIsInAppExtensionThenThreadsAreNotLeakedAfterDisposal() throws Exception {
    assertThreadsNamesAfterAppDisposal(TestClassLoadersHierarchy.Builder::withUrlsInAppExtension,
                                       TestClassLoadersHierarchy::getAppExtensionClassLoader,
                                       not(hasReadCheckTimer()));
  }

  @Test
  public void whenDriverIsInDomainThenClassLoadersAreNotLeakedAfterDisposal() throws Exception {
    assertClassLoadersAreNotLeakedAfterDisposal(TestClassLoadersHierarchy.Builder::withUrlsInDomain,
                                                TestClassLoadersHierarchy::getDomainExtensionClassLoader);
  }

  @Test
  public void whenDriverIsInDomainExtensionThenClassLoadersAreNotLeakedAfterDisposal() throws Exception {
    assertClassLoadersAreNotLeakedAfterDisposal(TestClassLoadersHierarchy.Builder::withUrlsInDomainExtension,
                                                TestClassLoadersHierarchy::getDomainExtensionClassLoader);
  }

  @Test
  public void whenDriverIsInDomainThenThreadsAreNotDisposedWhenAppIsDisposed() throws Exception {
    assertThreadsNamesAfterAppDisposal(TestClassLoadersHierarchy.Builder::withUrlsInDomain,
                                       TestClassLoadersHierarchy::getDomainExtensionClassLoader,
                                       hasReadCheckTimer());
  }

  @Test
  public void whenDriverIsInDomainExtensionThenThreadsAreNotDisposedWhenAppIsDisposed() throws Exception {
    assertThreadsNamesAfterAppDisposal(TestClassLoadersHierarchy.Builder::withUrlsInDomainExtension,
                                       TestClassLoadersHierarchy::getDomainExtensionClassLoader,
                                       hasReadCheckTimer());
  }

  private TestClassLoadersHierarchy.Builder getBaseClassLoaderHierarchyBuilder() {
    return TestClassLoadersHierarchy.getBuilder()
        .withArtifactLifecycleListener(new ActiveMQArtifactLifecycleListener())
        .excludingClassNamesFromRoot(this::isClassFromDriver);
  }

  private void assertClassLoadersAreNotLeakedAfterDisposal(BiFunction<TestClassLoadersHierarchy.Builder, URL[], TestClassLoadersHierarchy.Builder> driverConfigurer,
                                                           Function<TestClassLoadersHierarchy, ClassLoader> connectionClassLoaderProvider)
      throws Exception {
    TestClassLoadersHierarchy.Builder builder = getBaseClassLoaderHierarchyBuilder();
    builder = driverConfigurer.apply(builder, new URL[] {ACTIVEMQ_DRIVER_URL});

    try (TestClassLoadersHierarchy classLoadersHierarchy = builder.build()) {
      tryStartFailingActiveMQConnection(connectionClassLoaderProvider.apply(classLoadersHierarchy));

      disposeAppAndAssertRelease(classLoadersHierarchy);
      disposeDomainAndAssertRelease(classLoadersHierarchy);
    }
  }

  private void assertThreadsNamesAfterAppDisposal(BiFunction<TestClassLoadersHierarchy.Builder, URL[], TestClassLoadersHierarchy.Builder> driverConfigurer,
                                                  Function<TestClassLoadersHierarchy, ClassLoader> connectionClassLoaderProvider,
                                                  Matcher<Iterable<? super String>> threadNamesMatcher)
      throws Exception {
    TestClassLoadersHierarchy.Builder builder = getBaseClassLoaderHierarchyBuilder();
    builder = driverConfigurer.apply(builder, new URL[] {ACTIVEMQ_DRIVER_URL});

    try (TestClassLoadersHierarchy classLoadersHierarchy = builder.build()) {
      tryStartFailingActiveMQConnection(connectionClassLoaderProvider.apply(classLoadersHierarchy));

      classLoadersHierarchy.disposeApp();
      assertThat(getCurrentThreadNames(), threadNamesMatcher);
    }
  }

  private void disposeAppAndAssertRelease(TestClassLoadersHierarchy classLoadersHierarchy) throws IOException {
    CollectableReference<ClassLoader> appClassLoader =
        new CollectableReference<>(classLoadersHierarchy.getAppClassLoader());
    CollectableReference<ClassLoader> extensionClassLoader =
        new CollectableReference<>(classLoadersHierarchy.getAppExtensionClassLoader());
    classLoadersHierarchy.disposeApp();
    await().until(() -> extensionClassLoader, is(collectedByGc()));
    await().until(() -> appClassLoader, is(collectedByGc()));
  }

  private void disposeDomainAndAssertRelease(TestClassLoadersHierarchy classLoadersHierarchy) throws IOException {
    CollectableReference<ClassLoader> domainClassLoader =
        new CollectableReference<>(classLoadersHierarchy.getDomainClassLoader());
    CollectableReference<ClassLoader> domainExtensionClassLoader =
        new CollectableReference<>(classLoadersHierarchy.getDomainExtensionClassLoader());
    classLoadersHierarchy.disposeDomain();
    await().until(() -> domainExtensionClassLoader, is(collectedByGc()));
    await().until(() -> domainClassLoader, is(collectedByGc()));
  }

  private void tryStartFailingActiveMQConnection(ClassLoader classLoader) throws ReflectiveOperationException {
    ClassLoader originalTCCL = currentThread().getContextClassLoader();
    currentThread().setContextClassLoader(classLoader);
    try {
      Class<?> activeMQFactoryClass = classLoader.loadClass(ActiveMQConnectionFactory.class.getName());

      Object activeMQFactory = activeMQFactoryClass.getDeclaredConstructor(String.class).newInstance(ACTIVEMQ_BAD_URL_CONFIG);

      try {
        activeMQFactoryClass.getMethod("createConnection").invoke(activeMQFactory);
        fail("A ConnectException was expected");
      } catch (InvocationTargetException ite) {
        assertThat(getRootCause(ite), is(instanceOf(SocketException.class)));
      }

      assertThat(getCurrentThreadNames(), hasReadCheckTimer());
    } finally {
      currentThread().setContextClassLoader(originalTCCL);
    }
  }

  private static Matcher<Iterable<? super String>> hasReadCheckTimer() {
    return hasItem(ACTIVEMQ_READ_CHECK_TIMER_THREAD_NAME);
  }

  private static List<String> getCurrentThreadNames() {
    return getAllStackTraces().keySet().stream().map(Thread::getName).collect(toList());
  }

  private static Throwable getRootCause(Throwable t) {
    while (t.getCause() != null) {
      t = t.getCause();
    }

    return t;
  }

  private boolean isClassFromDriver(String className) {
    return className.startsWith(ACTIVEMQ_PACKAGE_PREFIX);
  }
}
