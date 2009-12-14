/**
 *
 */
package org.apache.axis.components.net;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Hashtable;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author carlos.alonso1
 *
 */
public class CertificateSecureSocketFactory extends JSSESocketFactory implements
		SecureSocketFactory {

	/**
	 * @param attributes
	 */
	public CertificateSecureSocketFactory(Hashtable attributes) {
		super(attributes);
	}

	/**
	 * Read the keystore, init the SSL socket factory
	 *
	 * This overrides the parent class to provide our SocketFactory
	 * implementation.
	 *
	 * @throws IOException
	 */
	protected void initFactory() throws IOException {
		try {
			SSLContext context = getContext();
			sslFactory = context.getSocketFactory();
		} catch (Exception e) {
			if (e instanceof IOException) {
				throw (IOException) e;
			}
			throw new IOException(e.getMessage());
		}
	}

	static final String DEFAULT_PROTOCOL = "TLS";

	/**
	 * Gets a custom SSL Context. This is the main working of this class. The
	 * following are the steps that make up our custom configuration:
	 *
	 * 1. Open our keystore file using the password provided
	 *
	 * 2. Create a KeyManagerFactory and TrustManagerFactory using this file
	 *
	 * 3. Initialise a SSLContext using these factories
	 *
	 * @return SSLContext
	 * @throws WebServiceClientConfigException
	 * @throws Exception
	 */
	protected SSLContext getContext() throws IOException {
		try {
			if (attributes == null || (attributes.get("keystore") == null && attributes.get("truststore") == null)) {
				SSLContext context = SSLContext.getInstance(DEFAULT_PROTOCOL);
				context.init(null, null, null);
				return context;
			}

			String protocol = getProtocol();

			KeyManager[] keyManagers = getKeyManagers();
			TrustManager trustManagers[] = getTrustManagers();

			SSLContext sslContext = SSLContext.getInstance(protocol);
			sslContext.init(keyManagers, trustManagers, new SecureRandom());
			return sslContext;
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("Exception trying to load sslContext "
					+ e.getMessage());
		} catch (KeyManagementException e) {
			throw new IOException("Exception trying to load sslContext "
					+ e.getMessage());
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private KeyManager[] getKeyManagers() throws IOException {
		String keystoreFile = (String) attributes.get("keystore");
		try {
			KeyManager[] keyManagers = null;
			if (keystoreFile != null) {
				String keystorePass = (String) attributes
						.get("keystorePassword");
				String keystoreType = (String) attributes.get("keystoreType");
				if (keystoreType == null) {
					keystoreType = KeyStore.getDefaultType();
				}
				String algorithm = getAlgorithm();
				if (algorithm == null) {
					algorithm = KeyManagerFactory.getDefaultAlgorithm();
				}

				KeyStore keyStore = initKeyStore(keystoreFile, keystorePass,
						keystoreType);
				KeyManagerFactory kmf = KeyManagerFactory
						.getInstance(algorithm);
				kmf.init(keyStore, keystorePass.toCharArray());
				keyManagers = kmf.getKeyManagers();
			}
			return keyManagers;
		} catch (Exception e) {
			throw new IOException("Exception trying to load keyStore "
					+ keystoreFile + ": " + e.getMessage());
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private TrustManager[] getTrustManagers() throws IOException {
		String truststoreFile = (String) attributes.get("truststore");
		try {
			TrustManager trustManagers[] = null;
			if (truststoreFile != null) {
				String truststorePass = (String) attributes
						.get("truststorePassword");
				String truststoreType = (String) attributes
						.get("truststoreType");
				if (truststoreType == null) {
					truststoreType = KeyStore.getDefaultType();
				}
				String algorithm = getAlgorithm();
				if (algorithm == null) {
					algorithm = TrustManagerFactory.getDefaultAlgorithm();
				}

				KeyStore trustStore = initKeyStore(truststoreFile,
						truststorePass, truststoreType);
				TrustManagerFactory tmf = TrustManagerFactory
						.getInstance(algorithm);
				tmf.init(trustStore);
				trustManagers = tmf.getTrustManagers();
			}
			return trustManagers;
		} catch (Exception e) {
			throw new IOException("Exception trying to load trustStore "
					+ truststoreFile + ": " + e.getMessage());
		}
	}

	/**
	 * @return
	 */
	private String getProtocol() {
		String protocol = (String) attributes.get("protocol");
		if (protocol == null) {
			protocol = DEFAULT_PROTOCOL;
		}
		return protocol;
	}

	/**
	 * @return
	 */
	private String getAlgorithm() {
		return (String) attributes.get("algorithm");
	}

	/**
	 * intializes a keystore.
	 *
	 * @param keyFile
	 * @param keyPassword
	 * @return keystore
	 * @throws IOException
	 */
	protected KeyStore initKeyStore(String keyFile, String keyPassword,
			String keyType) throws IOException {
		try {
			KeyStore kstore = KeyStore.getInstance(keyType);
			InputStream istream = new FileInputStream(keyFile);
			kstore.load(istream, keyPassword.toCharArray());
			return kstore;
		} catch (FileNotFoundException fnfe) {
			throw fnfe;
		} catch (IOException ioe) {
			throw ioe;
		} catch (Exception ex) {
			throw new IOException("Exception trying to load keystore "
					+ keyFile + ": " + ex.getMessage());
		}
	}

}
