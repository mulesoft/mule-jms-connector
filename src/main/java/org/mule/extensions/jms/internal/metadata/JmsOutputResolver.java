/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.metadata;

import static org.mule.metadata.api.model.MetadataFormat.JAVA;

import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.metadata.resolving.OutputStaticTypeResolver;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;

/**
 * An {@link OutputTypeResolver} for JMS operations
 *
 * @since 1.0
 */
public class JmsOutputResolver extends OutputStaticTypeResolver {

  @Override
  public String getCategoryName() {
    return "JMSMetadata";
  }

  @Override
  public MetadataType getStaticMetadata() {
    return new BaseTypeBuilder(JAVA).anyType().build();
  }
}
