/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.common;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.slf4j.LoggerFactory.getLogger;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.core.api.util.func.CheckedSupplier;
import org.slf4j.Logger;

import javax.jms.JMSException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Utility class to reuse logic for JMS Extension
 *
 * @since 1.0
 */
public final class JmsCommons {

  private static final Logger LOGGER = getLogger(JmsCommons.class);

  public static final String JMS_THREAD_GROUP_NAME = "JMS-CLIENT-LISTENER";
  private static final ThreadGroup JMS_THREAD_GROUP = new ThreadGroup(JMS_THREAD_GROUP_NAME);

  public static final String TOPIC = "TOPIC";
  public static final String QUEUE = "QUEUE";
  public static final String EXAMPLE_ENCODING = "UTF-8";
  public static final String EXAMPLE_CONTENT_TYPE = "application/json";


  public static <T> T createWithJmsThreadGroup(CheckedSupplier<T> function) throws ExecutionException, InterruptedException {
    ExecutorService executorService = newSingleThreadExecutor(runnable -> new Thread(JMS_THREAD_GROUP, runnable));
    try {
      Future<T> futureResult = executorService.submit(function::get);
      return futureResult.get();
    } finally {
      executorService.shutdown();
    }
  }

}
