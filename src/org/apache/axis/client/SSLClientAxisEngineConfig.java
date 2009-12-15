/**
 *
 */
package org.apache.axis.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.xml.namespace.QName;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisProperties;
import org.apache.axis.ConfigurationException;
import org.apache.axis.Handler;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.axis.transport.http.HTTPTransport;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;

/**
 * <p>
 * Axis Client Configuration
 * </p>
 *
 * @author Richard Unger
 */
public class SSLClientAxisEngineConfig extends SimpleProvider {

	protected static Log log = LogFactory
			.getLog(SSLClientAxisEngineConfig.class.getName());

	protected static final String CLIENT_CONFIG_FILE = "client-config.wsdd";

	private String proxyHost;
	private String proxyPort;
	private String proxyUser;
	private String proxyPassword;

	private String protocol;
	private String algorithm;

	private String keystore;
	private String keystoreType;
	private String keystorePassword;

	private String truststore;
	private String truststorePassword;
	private String truststoreType;

	private Hashtable options;

	/**
	 *
	 */
	public SSLClientAxisEngineConfig() {
		super();
		loadOptions(CLIENT_CONFIG_FILE);
	}

	/**
	 * @param configFile
	 */
	public SSLClientAxisEngineConfig(String configFile) {
		super();
		loadOptions(configFile);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.axis.configuration.SimpleProvider#configureEngine(org.apache
	 * .axis.AxisEngine)
	 */
	public void configureEngine(AxisEngine engine)
			throws ConfigurationException {
		super.configureEngine(engine);
		initializeAxisProperties();
		initializeGlobalOptions(engine);
		initializeTransport();
	}

	private void initializeAxisProperties() {
		AxisProperties
				.setProperty("axis.socketSecureFactory",
						"org.apache.axis.components.net.CertificateSecureSocketFactory");
		AxisProperties.setProperty("https.proxyHost", proxyHost);
		AxisProperties.setProperty("https.proxyPort", proxyPort);
		AxisProperties.setProperty("https.proxyUser", proxyUser);
		AxisProperties.setProperty("https.proxyPassword", proxyPassword);
	}

	private void initializeGlobalOptions(AxisEngine engine)
			throws ConfigurationException {
		setGlobalOptions(getOptions());
		engine.refreshGlobalOptions();
	}

	private void initializeTransport() throws ConfigurationException {
		QName http = new QName(null, HTTPTransport.DEFAULT_TRANSPORT_NAME);
		Handler transport = getTransport(http);

		if (transport == null) {
			Handler pivot = (Handler) new HTTPSender();
			if (protocol != null) {
				pivot.setOption("protocol", protocol);
			}
			if (algorithm != null) {
				pivot.setOption("algorithm", algorithm);
			}

			if (keystore != null) {
				pivot.setOption("clientauth", "true");
				pivot.setOption("keystore", keystore);
				if (keystoreType != null)
					pivot.setOption("keystoreType", keystoreType);
				if (keystorePassword != null) {
					pivot.setOption("keystorePassword", keystorePassword);
				}
			}
			if (truststore != null) {
				pivot.setOption("truststore", truststore);
				if (truststoreType != null)
					pivot.setOption("truststoreType", truststoreType);
				if (truststorePassword != null)
					pivot.setOption("truststorePassword", truststorePassword);
			}

			transport = new SimpleTargetedChain(pivot);
			deployTransport(http, transport);
		}
	}

	/**
	 * @param configFile
	 * @throws ExceptionInInitializerError
	 */
	private void loadOptions(String configFile)
			throws ExceptionInInitializerError {
		InputStream input = null;
		try {
			try {
				input = new FileInputStream(configFile);
			} catch (Exception e) {
				input = ClassUtils.getResourceAsStream(getClass(), configFile,
						true);
			}

			if (input == null) {
				throw new ConfigurationException(Messages
						.getMessage("noConfigFile"));
			}

			WSDDDocument doc = new WSDDDocument(XMLUtils.newDocument(input));
			if (doc.getDeployment().getGlobalConfiguration() != null) {
				options = doc.getDeployment().getGlobalOptions();
			}
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					log.warn(e);
				}
			}
		}
	}

	/**
	 * @param protocol
	 *            the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * @param algorithm
	 *            the algorithm to set
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * @param keystore
	 *            the keystore to set
	 */
	public void setKeystore(String keystore) {
		this.keystore = keystore;
	}

	/**
	 * @param keystorePassword
	 *            the keystorePassword to set
	 */
	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	/**
	 * @param keystoreType
	 *            the keystoreType to set
	 */
	public void setKeystoreType(String keystoreType) {
		this.keystoreType = keystoreType;
	}

	/**
	 * @param truststore
	 *            the truststore to set
	 */
	public void setTruststore(String truststore) {
		this.truststore = truststore;
	}

	/**
	 * @param truststorePassword
	 *            the truststorePassword to set
	 */
	public void setTruststorePassword(String truststorePassword) {
		this.truststorePassword = truststorePassword;
	}

	/**
	 * @param truststoreType
	 *            the truststoreType to set
	 */
	public void setTruststoreType(String truststoreType) {
		this.truststoreType = truststoreType;
	}

	/**
	 * @param proxyHost
	 *            the proxyHost to set
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	/**
	 * @param proxyPort
	 *            the proxyPort to set
	 */
	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * @param proxyUser
	 *            the proxyUser to set
	 */
	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	/**
	 * @param proxyPassword
	 *            the proxyPassword to set
	 */
	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	/**
	 * @param disablePrettyXML
	 *            the disablePrettyXML to set
	 */
	public void setDisablePrettyXML(Boolean disablePrettyXML) {
		getOptions().put(AxisEngine.PROP_DISABLE_PRETTY_XML, disablePrettyXML);
	}

	/**
	 * @param enableNamespacePrefixOptimization
	 *            the enableNamespacePrefixOptimization to set
	 */
	public void setEnableNamespacePrefixOptimization(
			Boolean enableNamespacePrefixOptimization) {
		getOptions().put(AxisEngine.PROP_ENABLE_NAMESPACE_PREFIX_OPTIMIZATION,
				enableNamespacePrefixOptimization);
	}

	/**
	 * @param sendMultiRefs
	 *            the sendMultiRefs to set
	 */
	public void setSendMultiRefs(Boolean sendMultiRefs) {
		getOptions().put(AxisEngine.PROP_DOMULTIREFS, sendMultiRefs);
	}

	/**
	 * @param key
	 * @param value
	 */
	public void setOption(String key, Object value) {
		getOptions().put(key, value);
	}

	/**
	 * @return
	 */
	public Hashtable getOptions() {
		if (options == null) {
			options = new Hashtable();
		}
		return options;
	}
}
