/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.connection.provider.activemq;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.connectivity.NoConnectivityTest;


@DisplayName("ActiveMQ Connection - No Connectivity Test")
@Alias("active-mq-nct")
// TODO (nicomz) this should be removed.
@Deprecated
public class ActiveMQConnectionNCTProvider extends ActiveMQConnectionProvider implements NoConnectivityTest {

}
