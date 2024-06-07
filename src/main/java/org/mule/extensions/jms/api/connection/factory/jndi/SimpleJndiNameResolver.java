/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.api.connection.factory.jndi;

import static java.util.Objects.hash;
import static org.slf4j.LoggerFactory.getLogger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.mule.extensions.jms.internal.ExcludeFromGeneratedCoverage;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.slf4j.Logger;

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

  protected final Logger LOGGER = getLogger(SimpleJndiNameResolver.class);

  private Context jndiContext;

  @Override
  public synchronized Object lookup(String name) throws NamingException {
    try {
      LOGGER.debug("lookup: " + name);
      if (jndiContext == null) {
        //W-15812905 for the case of Weblogic is restarted the context is null,
        // for this reason it is necessary to create another one.
        LOGGER.debug("jndiContext is null, creating a new initial context for " + name);
        jndiContext = createInitialContext();
      }
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
