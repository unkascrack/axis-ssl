/**
 *
 */
package org.apache.axis.configuration;

import java.util.Hashtable;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisProperties;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.axis.transport.http.HTTPTransport;

/**
 * <p>
 * Axis Client Configuration
 * </p>
 *
 * @author Richard Unger
 */
public class SSLClientAxisEngineConfig extends SimpleProvider {

	/**
	 * Protocol
	 */
	private String protocol = null;

	/**
	 * Algorithm
	 */
	private String algorithm = null;

	/**
	 * Keystore filename
	 */
	private String keystore = null;

	/**
	 * Keystore type
	 */
	private String keystoreType = null;

	/**
	 * Keystore password
	 */
	private String keystorePassword = null;

	/**
	 * Truststore filename
	 */
	private String truststore = null;

	/**
	 * Truststore password
	 */
	private String truststorePassword = null;

	/**
	 * Truststore Type
	 */
	private String truststoreType = null;

	/**
	 * true to disable XML formatting
	 */
	private boolean disablePrettyXML = true;

	/**
	 * true to enable namespace prefix optimization (see Axis docs)
	 */
	private boolean enableNamespacePrefixOptimization = false;

	/**
	 * Constructor
	 */
	public SSLClientAxisEngineConfig() {
		super();
	}

	/**
	 * @param engineConfiguration
	 */
	public SSLClientAxisEngineConfig(EngineConfiguration engineConfiguration) {
		super(engineConfiguration);
	}

	/**
	 * @param typeMappingRegistry
	 */
	public SSLClientAxisEngineConfig(TypeMappingRegistry typeMappingRegistry) {
		super(typeMappingRegistry);
	}

	/**
	 * @see org.apache.axis.configuration.SimpleProvider#configureEngine(org.apache.axis.AxisEngine)
	 */
	public void configureEngine(AxisEngine engine)
			throws ConfigurationException {
		super.configureEngine(engine);
		engine.refreshGlobalOptions();
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
	 * <p>
	 * Initialize
	 * </p>
	 */
	public void initialize() {
		AxisProperties
				.setProperty("axis.socketSecureFactory",
						"org.apache.axis.components.net.CertificateSecureSocketFactory");
		AxisProperties.setProperty("axis.socketFactory",
				"org.apache.axis.components.net.DefaultSocketFactory");

		Hashtable opts = new Hashtable();
		opts.put(AxisEngine.PROP_DISABLE_PRETTY_XML, new Boolean(
				disablePrettyXML));
		opts.put(AxisEngine.PROP_ENABLE_NAMESPACE_PREFIX_OPTIMIZATION,
				new Boolean(enableNamespacePrefixOptimization));
		setGlobalOptions(opts);

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

		Handler transport = null;
		transport = new SimpleTargetedChain(pivot);
		deployTransport(HTTPTransport.DEFAULT_TRANSPORT_NAME, transport);
	}

	/**
	 * @return the disablePrettyXML
	 */
	public boolean isDisablePrettyXML() {
		return disablePrettyXML;
	}

	/**
	 * @param disablePrettyXML
	 *            the disablePrettyXML to set
	 */
	public void setDisablePrettyXML(boolean disablePrettyXML) {
		this.disablePrettyXML = disablePrettyXML;
	}

	/**
	 * @return the enableNamespacePrefixOptimization
	 */
	public boolean isEnableNamespacePrefixOptimization() {
		return enableNamespacePrefixOptimization;
	}

	/**
	 * @param enableNamespacePrefixOptimization
	 *            the enableNamespacePrefixOptimization to set
	 */
	public void setEnableNamespacePrefixOptimization(
			boolean enableNamespacePrefixOptimization) {
		this.enableNamespacePrefixOptimization = enableNamespacePrefixOptimization;
	}
}
