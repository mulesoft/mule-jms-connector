/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.connection.factory.jndi;

import static java.util.Objects.hash;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.mule.extensions.jms.internal.ExcludeFromGeneratedCoverage;
import org.mule.runtime.api.lifecycle.InitialisationException;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Objects;

/**
 * Defines a simple {@link JndiNameResolver} that maintains a {@link Context}
 * instance opened all the time and always relies on the context to do the look
 * ups.
 *
 * @since 1.0
 */
public class SimpleJndiNameResolver extends AbstractJndiNameResolver {

  private Context jndiContext;

  @Override
  public synchronized Object lookup(String name) throws NamingException {
    try {
      return doLookUp(name);
    } catch (CommunicationException e) {
      jndiContext = this.createInitialContext();
      return doLookUp(name);
    }
  }

  @Override
  public void initialise() throws InitialisationException {
    if (jndiContext == null) {
      try {
        jndiContext = createInitialContext();
      } catch (Exception e) {
        throw new InitialisationException(e, this);
      }
    }
  }

  @Override
  public void dispose() {
    if (jndiContext != null) {
      try {
        jndiContext.close();
      } catch (NamingException e) {
        LOGGER.error("Jms connector failed to dispose properly: ", e);
      } finally {
        jndiContext = null;
      }
    }
  }

  private Object doLookUp(String name) throws NamingException {
    return jndiContext.lookup(name);
  }

  @Override
  @ExcludeFromGeneratedCoverage
  public int hashCode() {
    int result = super.hashCode();
    if (jndiContext != null) {
      result = 31 * result + hash(jndiContext);
    }
    return result;
  }

  @Override
  @ExcludeFromGeneratedCoverage
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }

    if (this == other) {
      return true;
    }

    if (!(other instanceof SimpleJndiNameResolver)) {
      return false;
    }

    SimpleJndiNameResolver otherJndiNameResolver = (SimpleJndiNameResolver) other;

    if (!super.equals(otherJndiNameResolver)) {
      return false;
    }

    if (jndiContext != null) {
      return Objects.equals(this.jndiContext, otherJndiNameResolver.jndiContext);
    }

    return true;
  }

  @ExcludeFromGeneratedCoverage
  public Context getJndiContext() {
    return jndiContext;
  }

  @ExcludeFromGeneratedCoverage
  public void setJndiContext(Context jndiContext) {
    this.jndiContext = jndiContext;
  }
}
