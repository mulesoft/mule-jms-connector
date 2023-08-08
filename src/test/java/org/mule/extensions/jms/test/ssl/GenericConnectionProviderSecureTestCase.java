package org.mule.extensions.jms.test.ssl;

import org.mule.extensions.jms.internal.connection.provider.GenericConnectionProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;


@RunWith(PowerMockRunner.class)
@PrepareForTest({SSLContext.class, FileInputStream.class, GenericConnectionProvider.class, TrustManagerFactory.class})
public class GenericConnectionProviderSecureTestCase {

    @Before
    public void setUp() {
        PowerMockito.mockStatic(SSLContext.class);
        PowerMockito.mockStatic(TrustManagerFactory.class);
    }

    @Test
    public void testGetConnectionFactory_withProperties() throws Exception{
        System.setProperty("mule.jms.generic.additionalCertificatePassword", "changeit");
        System.setProperty("mule.jms.generic.additionalCertificateFileName", "tls/customTrust.jks");

        SSLContext mockSSLContext = PowerMockito.mock(SSLContext.class);
        SSLParameters mockSSLParameters = PowerMockito.mock(SSLParameters.class);
        KeyStore mockKeyStore = PowerMockito.mock(KeyStore.class);
        File mockTruststoreFile = PowerMockito.mock(File.class);

        TrustManagerFactory mockTrustManagerFactory = PowerMockito.mock(TrustManagerFactory.class);

        PowerMockito.when(SSLContext.getDefault()).thenReturn(mockSSLContext);
        PowerMockito.when(mockSSLContext.getDefaultSSLParameters()).thenReturn(mockSSLParameters);
        PowerMockito.when(SSLContext.getInstance(anyString())).thenReturn(mockSSLContext);
        PowerMockito.when(SSLContext.getDefault().getDefaultSSLParameters().getProtocols()).thenReturn(new String[0]);
        PowerMockito.when(TrustManagerFactory.getDefaultAlgorithm()).thenReturn("TLS");
        PowerMockito.when(TrustManagerFactory.getInstance(anyString())).thenReturn(mockTrustManagerFactory);
        PowerMockito.when(mockTrustManagerFactory.getTrustManagers()).thenReturn(new TrustManager[0]);

        GenericConnectionProvider connectionProvider = PowerMockito.spy(new GenericConnectionProvider());
        PowerMockito.doReturn(mockKeyStore).
                when(connectionProvider,"getKeyStoreWithCustomCerts",any(Optional.class),anyString());

        PowerMockito.when(connectionProvider, "getTruststoreFile", "tls/customTrust.jks").thenReturn(Optional.of(mockTruststoreFile));

        FileInputStream mockFileInputStream = PowerMockito.mock(FileInputStream.class);
        PowerMockito.whenNew(FileInputStream.class).withArguments(mockTruststoreFile).thenReturn(mockFileInputStream);

        connectionProvider.getConnectionFactory();
        PowerMockito.verifyPrivate(connectionProvider).invoke("getKeyStoreWithCustomCerts", any(Optional.class), anyString());

    }

    @Test
    public void testGetConnectionFactory_withOutProperties() throws Exception {
        System.setProperty("mule.jms.generic.additionalCertificatePassword","");
        System.setProperty("mule.jms.generic.additionalCertificateFileName","");
        GenericConnectionProvider connectionProvider = PowerMockito.spy(new GenericConnectionProvider());
        GenericConnectionProvider genericConnectionProvider = new GenericConnectionProvider();
        genericConnectionProvider.getConnectionFactory();
        PowerMockito.verifyPrivate(connectionProvider, never()).invoke("getCustomTrustStoreWithDefaultCerts", any(Optional.class), anyString());


    }


}
