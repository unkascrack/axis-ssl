# Introduction #
Use of the library axis-ssl-1.4

```
// create config
SSLClientAxisEngineConfig axisConfig = new SSLClientAxisEngineConfig();
axisConfig.setProtocol("TLS") 		//default SSL
axisConfig.setAlgorithm("SunX509")	//default KeyManagerFactory.getDefaultAlgorithm()
					//and TrustManagerFactory.getDefaultAlgorithm()

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
