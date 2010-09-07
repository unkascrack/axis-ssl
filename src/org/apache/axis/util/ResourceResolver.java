/**
 * 
 */
package org.apache.axis.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:sebas@zaffarano.com.ar>Sebastián Zaffarano</a>
 * 
 *         Resolve file locations from many sources: "file" (default), "classpath" and "URL."
 * 
 *         Usage:
 * 
 *         <code>
 *         	ResourceResolver.getInputStream(LOCATION)
 *         </code>
 * 
 *         where LOCATION has the following format:
 * 
 *         <code> 
 *         	[PREFIX:]LOCATION (prefix ist optional and the default value is "file")
 *         </code>
 */
public class ResourceResolver {
	private static final Map<String, Resolver> resolvers;

	static {
		resolvers = new HashMap<String, Resolver>();
		resolvers.put("file", new FileResolver());
		resolvers.put("url", new URLResolver());
		resolvers.put("classpath", new ClasspathResolver());
	}

	static public InputStream getInputStream(final String location) {
		if (location == null) {
			throw new RuntimeException("Location must not be null!");
		}
		// default location is file:
		String prefix = "file";
		String effectiveLocation = location;
		if (location.indexOf(":") != -1) {
			prefix = location.substring(0, location.indexOf(":")).toLowerCase();
			effectiveLocation = location.substring(location.indexOf(":") + 1);
		}

		if (!resolvers.containsKey(prefix)) {
			throw new RuntimeException(String.format("Unknown protocol %s for location %s", prefix, location));
		}

		return resolvers.get(prefix).open(effectiveLocation);
	}
}

interface Resolver {
	InputStream open(String location);
}

class URLResolver implements Resolver {
	public InputStream open(final String location) {
		try {
			return new URL(location).openStream();
		} catch (MalformedURLException e) {
			throw new RuntimeException(String.format("Error opening url %s: %s", location, e.getMessage()), e);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error opening url %s: %s", location, e.getMessage()), e);
		}
	}
}

class FileResolver implements Resolver {
	public InputStream open(final String location) {
		try {
			return new FileInputStream(new File(location));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(String.format("Error opening file %s: %s", location, e.getMessage()), e);
		}
	}
}

class ClasspathResolver implements Resolver {
	public InputStream open(final String location) {
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(location);
		if (stream == null) {
			throw new RuntimeException(String.format("Resource %s not found in classpath", location));
		}
		return stream;
	}
}