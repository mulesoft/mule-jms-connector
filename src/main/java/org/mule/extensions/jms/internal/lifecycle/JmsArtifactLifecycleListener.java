/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.lifecycle;

import org.mule.sdk.api.artifact.lifecycle.ArtifactDisposalContext;
import org.mule.sdk.api.artifact.lifecycle.ArtifactLifecycleListener;

/**
 * {@link ArtifactLifecycleListener} for releasing all the known references associated with a JMS driver that may lead to a
 * Thread/ClassLoader leak.
 *
 * @since 2.0.0
 */
public class JmsArtifactLifecycleListener implements ArtifactLifecycleListener {

  @Override
  public void onArtifactDisposal(ArtifactDisposalContext artifactDisposalContext) {
    new ActiveMQArtifactLifecycleListener().onArtifactDisposal(artifactDisposalContext);
  }
}
