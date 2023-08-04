/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.internal.connection.provider;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.api.meta.ExternalLibraryType.DEPENDENCY;
import static org.slf4j.LoggerFactory.getLogger;

import org.mule.extensions.jms.api.connection.factory.jndi.JndiConnectionFactory;
import org.mule.extensions.jms.api.exception.JmsExtensionException;
import org.mule.jms.commons.api.connection.LookupJndiDestination;
import org.mule.jms.commons.internal.support.JmsSupport;
import org.mule.jms.commons.internal.support.Jms11Support;
import org.mule.jms.commons.internal.support.Jms20Support;
import org.mule.jms.commons.internal.support.JmsSupportFactory;
import org.mule.jms.commons.internal.support.Jms102bSupport;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.ExternalLib;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.jms.ConnectionFactory;
import javax.jms.XAConnectionFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Generic implementation of a JMS {@link ConnectionProvider}.
 * This provider uses any {@link ConnectionFactory} that the user configures in order to create a {@link JmsConnection}.
 *
 * @since 1.0
 */
@DisplayName("Generic Connection")
@Alias("generic")
@ExternalLib(name = "JMS Client", description = "Client which lets communicate with a JMS broker", type = DEPENDENCY)
public class GenericConnectionProvider extends BaseConnectionProvider {

  private static final Logger LOGGER = getLogger(GenericConnectionProvider.class);
  public static final String PROTOCOL = "SSL";
  private final String trustStorePassword = System.getProperty("mule.jms.generic.additionalCertificatePassword", "");
  private final String trustStoreName = System.getProperty("mule.jms.generic.additionalCertificateFileName", "");

  /**
   * a JMS {@link ConnectionFactory} implementation
   */
  @Parameter
  @Expression(NOT_SUPPORTED)
  private ConnectionFactory connectionFactory;

  @Override
  public ConnectionFactory getConnectionFactory() {
    this.configureSSLContextIfNeeded();
    return connectionFactory;
  }

  @Override
  protected boolean enableXa() {
    return connectionFactory instanceof XAConnectionFactory;
  }

  @Override
  protected Supplier<ConnectionFactory> getConnectionFactorySupplier() {
    return this::getConnectionFactory;
  }

  @Override
  protected void configureSSLContext() {}

  @Override
  protected JmsSupportFactory getJmsSupportFactory() {
    ConnectionFactory connectionFactory = this.getConnectionFactorySupplier().get();
    if (!(connectionFactory instanceof JndiConnectionFactory)) {
      return super.getJmsSupportFactory();
    }

    JndiConnectionFactory jndiConnectionFactory = (JndiConnectionFactory) connectionFactory;
    LookupJndiDestination lookupJndiDestination =
        jndiConnectionFactory.getLookupDestination().getJmsClientLookupJndiDestination();

    return new JmsSupportFactory() {

      @Override
      public JmsSupport create11Support() {
        return new Jms11Support(lookupJndiDestination, jndiConnectionFactory::getJndiDestination);
      }

      @Override
      public JmsSupport create20Support() {
        return new Jms20Support(lookupJndiDestination, jndiConnectionFactory::getJndiDestination);
      }

      @Override
      public JmsSupport create102bSupport() {
        return new Jms102bSupport(lookupJndiDestination, jndiConnectionFactory::getJndiDestination);
      }
    };
  }

  protected void configureSSLContextIfNeeded() {
    if (!trustStorePassword.isEmpty() && !trustStoreName.isEmpty()) {
      try {
        final SSLContext context = SSLContext.getInstance(PROTOCOL);
        context.init(new KeyManager[0],
                     getCustomTrustStoreWithDefaultCerts(getTruststoreFile(trustStoreName), trustStorePassword),
                     new SecureRandom());
        SSLContext.setDefault(context);
      } catch (Exception e) {
        throw new JmsExtensionException("Failed to set custom TrustStore", e);
      }
    }
  }

  private TrustManager[] getCustomTrustStoreWithDefaultCerts(java.util.Optional<File> truststoreFile, String trustStorePassword)
      throws Exception {
    final KeyStore keyStore = getKeyStoreWithCustomCerts(truststoreFile, trustStorePassword);

    final TrustManagerFactory jdkDefaultTrustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    jdkDefaultTrustManagerFactory.init((KeyStore) null);
    List<TrustManager> trustManagers = Arrays.asList(jdkDefaultTrustManagerFactory.getTrustManagers());

    List<X509Certificate> certificates = trustManagers.stream().filter(X509TrustManager.class::isInstance)
        .map(X509TrustManager.class::cast).map(trustManager -> Arrays.asList(trustManager.getAcceptedIssuers()))
        .flatMap(Collection::stream).collect(Collectors.toList());

    for (X509Certificate certificate : certificates) {
      keyStore.setCertificateEntry(String.valueOf(certificate.getSerialNumber()), certificate);
    }
    final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(keyStore);

    return trustManagerFactory.getTrustManagers();
  }

  private KeyStore getKeyStoreWithCustomCerts(java.util.Optional<File> truststoreFile, String trustStorePassword)
      throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {
    final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    keyStore.load(null, null);
    try (FileInputStream kos = new FileInputStream(truststoreFile.get())) {
      keyStore.load(kos, trustStorePassword.toCharArray());
    }
    return keyStore;
  }

  private java.util.Optional<File> getTruststoreFile(String trustStoreName) {
    URL resource = this.getClass().getClassLoader().getResource(trustStoreName);
    if (Objects.isNull(resource)) {
      LOGGER.error("Failed to found file {}", trustStoreName);
      return java.util.Optional.empty();
    }
    return java.util.Optional.of(new File(resource.getPath()));
  }
}
