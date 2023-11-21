/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.lifecycle;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import static org.slf4j.LoggerFactory.getLogger;

import org.mule.sdk.api.artifact.lifecycle.ArtifactDisposalContext;
import org.mule.sdk.api.artifact.lifecycle.ArtifactLifecycleListener;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Timer;

import org.slf4j.Logger;

/**
 * {@link ArtifactLifecycleListener} for releasing all the known references associated with the ActiveMQ driver that may lead to a
 * Thread/ClassLoader leak.
 *
 * @since 2.0.0
 */
public class ActiveMQArtifactLifecycleListener implements ArtifactLifecycleListener {

  private static final String ABSTRACT_INACTIVITY_MONITOR_CLASSNAME = "org.apache.activemq.transport.AbstractInactivityMonitor";
  private static final Logger LOGGER = getLogger(ActiveMQArtifactLifecycleListener.class);

  @Override
  public void onArtifactDisposal(ArtifactDisposalContext artifactDisposalContext) {
    disposeInactivityMonitorTimers(artifactDisposalContext, artifactDisposalContext.getArtifactClassLoader());
    disposeInactivityMonitorTimers(artifactDisposalContext, artifactDisposalContext.getExtensionClassLoader());
  }

  private void disposeInactivityMonitorTimers(ArtifactDisposalContext artifactDisposalContext, ClassLoader classLoader) {
    // See https://github.com/apache/activemq/pull/1119
    getReadCheckTimerField(artifactDisposalContext, classLoader).ifPresent(this::disposeReadCheckTimer);
  }

  private Optional<Field> getReadCheckTimerField(ArtifactDisposalContext artifactDisposalContext, ClassLoader classLoader) {
    try {
      Class<?> cls = classLoader.loadClass(ABSTRACT_INACTIVITY_MONITOR_CLASSNAME);
      if (!isArtifactOrExtensionOwnedClass(artifactDisposalContext, cls)) {
        // Don't do anything if the class does not belong to the artifact being disposed of.
        return empty();
      }
      return of(cls.getDeclaredField("READ_CHECK_TIMER"));
    } catch (ClassNotFoundException | NoSuchFieldException e) {
      // If the class or field is not found, there is nothing to dispose
      return empty();
    }
  }

  private void disposeReadCheckTimer(Field readCheckTimerField) {
    try {
      // This accessibility override will work as long as the driver is loaded in the unnamed module or as an automatic module
      readCheckTimerField.setAccessible(true);

      Timer readCheckTimer = (Timer) readCheckTimerField.get(null);
      if (readCheckTimer != null) {
        readCheckTimer.cancel();
        readCheckTimerField.set(null, null);
      }
    } catch (Exception e) {
      LOGGER.warn("Unable to cleanup ActiveMQ's InactivityMonitor timers", e);
    }
  }

  private boolean isArtifactOrExtensionOwnedClass(ArtifactDisposalContext artifactDisposalContext, Class<?> cls) {
    ClassLoader loaderOfClass = cls.getClassLoader();
    return artifactDisposalContext.isArtifactOwnedClassLoader(loaderOfClass) ||
        artifactDisposalContext.isExtensionOwnedClassLoader(loaderOfClass);
  }
}
