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

	private String keyStore;
	private String keyStoreType;
	private String keyStorePassword;

	private String trustStore;
	private String trustStorePassword;
	private String trustStoreType;

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
		log.debug("SSLClientAxisEngineConfig::AxisProperties::init");
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

		if (log.isDebugEnabled()) {
			log.debug("Global options: "+getGlobalOptions());
		}
	}

	private void initializeTransport() throws ConfigurationException {
		QName http = new QName(null, HTTPTransport.DEFAULT_TRANSPORT_NAME);
		Handler transport = getTransport(http);

		if (transport == null) {
			log.debug("SSLClientAxisEngineConfig::Transport::init");

			Handler pivot = (Handler) new HTTPSender();
			if (protocol != null) {
				pivot.setOption("protocol", protocol);
			}
			if (algorithm != null) {
				pivot.setOption("algorithm", algorithm);
			}

			if (keyStore != null) {
				pivot.setOption("clientauth", "true");
				pivot.setOption("keyStore", keyStore);
				if (keyStoreType != null)
					pivot.setOption("keyStoreType", keyStoreType);
				if (keyStorePassword != null) {
					pivot.setOption("keyStorePassword", keyStorePassword);
				}
			}
			if (trustStore != null) {
				pivot.setOption("trustStore", trustStore);
				if (trustStoreType != null)
					pivot.setOption("trustStoreType", trustStoreType);
				if (trustStorePassword != null)
					pivot.setOption("trustStorePassword", trustStorePassword);
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

		log.info("Enter::SSLClientAxisEngineConfig::init::"+configFile);

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
	 * @param keyStore
	 *            the keyStore to set
	 */
	public void setKeyStore(String keyStore) {
		this.keyStore = keyStore;
	}

	/**
	 * @param keyStorePassword
	 *            the keyStorePassword to set
	 */
	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	/**
	 * @param keyStoreType
	 *            the keyStoreType to set
	 */
	public void setKeyStoreType(String keyStoreType) {
		this.keyStoreType = keyStoreType;
	}

	/**
	 * @param trustStore
	 *            the trustStore to set
	 */
	public void setTrustStore(String trustStore) {
		this.trustStore = trustStore;
	}

	/**
	 * @param trustStorePassword
	 *            the trustStorePassword to set
	 */
	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}

	/**
	 * @param trustStoreType
	 *            the trustStoreType to set
	 */
	public void setTrustStoreType(String trustStoreType) {
		this.trustStoreType = trustStoreType;
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
