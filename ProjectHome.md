**Axis 1.4 Dynamic SSL Config, permite el uso de la librería Axis 1.4 con varias conexiones seguras (SSL)**


Uso de la libreria axis-ssl-1.4:

```
// create config
SSLClientAxisEngineConfig axisConfig = new SSLClientAxisEngineConfig();
axisConfig.setProtocol("TLS") 						//default SSL
axisConfig.setAlgorithm("SunX509")					//default KeyManagerFactory.getDefaultAlgorithm()
									//y TrustManagerFactory.getDefaultAlgorithm()

//define config keystore and truststore (optional)
axisConfig.setKeystore("/path/to/clientkey.p12");
axisConfig.setKeystoreType("PKCS12");
axisConfig.setKeystorePassword("changeit");
axisConfig.setTruststore("/path/to/truststore.jks");
axisConfig.setTruststoreType("JKS");
axisConfig.setTruststorePassword("changeit");
axisConfig.initialize();

// initialize service
URL soapURL = new URL("https://myserver.com/myapp/services/mywebserviceport");
MyWebServiceServiceLocator locator = new MyServiceLocator(axisConfig);
MyWebServicePort port = locator.getMyWebServicePort(soapURL);
MyWebServiceBindingStub stub = (MyWebServiceBindingStub) port;
MyResultType result = stub.myoperation1(...);
```