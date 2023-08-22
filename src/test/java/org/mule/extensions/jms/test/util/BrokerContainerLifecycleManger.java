/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.util;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.lang.String.format;

public class BrokerContainerLifecycleManger {

  private static Logger LOGGER = LoggerFactory.getLogger(BrokerContainerLifecycleManger.class);

  public static Container getContainerByName(String containerName) throws Exception {
    final DockerClient docker = DefaultDockerClient.fromEnv().build();
    final List<Container> containers = docker.listContainers();

    for (Container container : containers) {
      if (container.image().contains(containerName)) {
        return container;
      }
    }

    throw new Exception(format("No container found for name %s", containerName));
  }


  public static String stopContainerWithName(String containerName, int delay) throws Exception {
    final DockerClient docker = DefaultDockerClient.fromEnv().build();
    Container sftpServerContainer = getContainerByName(containerName);
    docker.stopContainer(sftpServerContainer.id(), delay);
    LOGGER.info(String.format("STOPPING DOCKER CONTAINER %s", sftpServerContainer.id()));
    return sftpServerContainer.id();
  }

  public static void startContainerWithId(String containerId) throws Exception {
    LOGGER.info(String.format("STARTING DOCKER CONTAINER %s", containerId));
    final DockerClient docker = DefaultDockerClient.fromEnv().build();
    docker.startContainer(containerId);
  }

}
