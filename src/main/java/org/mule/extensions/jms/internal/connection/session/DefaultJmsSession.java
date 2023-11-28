///*
// * Copyright 2023 Salesforce, Inc. All rights reserved.
// * The software in this package is published under the terms of the CPAL v1.0
// * license, a copy of which has been included with this distribution in the
// * LICENSE.txt file.
// */
//package org.mule.extensions.jms.internal.connection.session;
//
//import static java.util.Optional.ofNullable;
//import static org.mule.runtime.api.util.Preconditions.checkArgument;
//import static org.slf4j.LoggerFactory.getLogger;
//
//import javax.jms.JMSException;
//import javax.jms.Session;
//
//import java.util.Optional;
//
//import org.slf4j.Logger;
//
///**
// * Wrapper element for a JMS {@link Session} that relates the
// * session with its AckID
// *
// * @since 1.0
// */
//public final class DefaultJmsSession implements JmsSession {
//
//  private static final Logger LOGGER = getLogger(DefaultJmsSession.class);
//
//  private final Session session;
//  private String ackId;
//
//  public DefaultJmsSession(Session session) {
//    checkArgument(session != null, "A non null Session is required to use as delegate");
//    this.session = session;
//  }
//
//  public DefaultJmsSession(Session session, String ackId) {
//    checkArgument(session != null, "A non null Session is required to use as delegate");
//    this.session = session;
//    this.ackId = ackId;
//  }
//
//  /**
//   * @return the JMS {@link Session}
//   */
//  @Override
//  public Session get() {
//    return session;
//  }
//
//  /**
//   * @return the AckId of this {@link Session} or {@link Optional#empty} if no AckId is required
//   */
//  @Override
//  public Optional<String> getAckId() {
//    return ofNullable(ackId);
//  }
//
//  @Override
//  public void close() throws JMSException {
//    if (LOGGER.isDebugEnabled()) {
//      LOGGER.debug("Closing session " + session);
//    }
//    session.close();
//  }
//}
