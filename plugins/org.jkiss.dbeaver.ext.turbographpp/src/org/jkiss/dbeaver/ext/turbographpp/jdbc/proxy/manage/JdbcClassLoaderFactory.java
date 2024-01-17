/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package org.jkiss.dbeaver.ext.turbographpp.jdbc.proxy.manage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Driver;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jkiss.dbeaver.ext.turbographpp.jdbc.proxy.driver.CUBRIDDriverProxy;

/**
 * 
 * The <code>JdbcClassLoaderFactory</code> can register&get&clear the CUBRID
 * JDBC jar file class loader
 * 
 * @author pangqiren
 * @version 1.0 - 2009-12-28 created by pangqiren
 */
public final class JdbcClassLoaderFactory {

	/**
	 * the constructor
	 */
	private JdbcClassLoaderFactory() {
	}

	private static Map<String, ClassLoader> loaders = new HashMap<String, ClassLoader>();

	/**
	 * 
	 * get the class loader by the server version
	 * 
	 * @param jdbcVersion the JDBC version(format:CUBRID-JDBC-8.2.0.1147)
	 * @return the ClassLoader object
	 */
	public static ClassLoader getClassLoader(String jdbcVersion) {
		ClassLoader loader = null;
		if (loaders.containsKey(jdbcVersion)) {
			loader = loaders.get(jdbcVersion);
		}
		return loader;
	}

	/**
	 * 
	 * Validate the JDBC jar file;if it is valid,return the version;otherwise
	 * return null
	 * 
	 * note: the below case,return null (1)the JDBC URL path do not exist (2)the
	 * JDBC jar is not valid
	 * 
	 * @param jdbcURL the JDBC jar url
	 * @return the version (format:CUBRID-JDBC-8.2.0.1147)
	 * @throws MalformedURLException
	 */
	@SuppressWarnings("unchecked")
	public static String validateJdbcFile(String jdbcURL) {
		try {
			final URL[] us = {new URL("file:" + jdbcURL)};
			ClassLoader loader = AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
				public URLClassLoader run() {
					return new URLClassLoader(us);
				}
			});
			Class<Driver> driverClazz = (Class<Driver>) loader.loadClass("cubrid.jdbc.driver.CUBRIDDriver");
			new CUBRIDDriverProxy(driverClazz.newInstance());
			return getJdbcJarVersion(jdbcURL);
		} catch (MalformedURLException ex) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 
	 * Get JDBC jar version,if it is valid,return the version
	 * information(format:CUBRID-JDBC-8.2.0.1147);otherwise return null
	 * 
	 * @param jdbcURL the jdbc url
	 * @return the version (format:CUBRID-JDBC-8.2.0.1147)
	 * @throws IOException exception
	 */
	public static String getJdbcJarVersion(String jdbcURL) throws IOException {
		JarFile jarfile = new JarFile(new File(jdbcURL));
		Enumeration<JarEntry> entries = jarfile.entries();
		while (entries.hasMoreElements()) {
			JarEntry nextElement = entries.nextElement();
			if (nextElement.getName() != null
					&& nextElement.getName().startsWith("CUBRID-JDBC-")) {
				return nextElement.getName();
			}
		}
		return null;
	}

	/**
	 * Register the class loader in the factory.
	 * 
	 * note: the below case,return false (1)the JDBC URL path do not exist
	 * (2)the JDBC jar is not valid
	 * 
	 * @param jdbcUrl the JDBC URL
	 * @return <code>true</code> if registered successfully;<code>false</code>
	 *         otherwise;
	 */
	public static boolean registerClassLoader(String jdbcUrl) {
		try {
			String realVersion = validateJdbcFile(jdbcUrl);
			if (realVersion != null && realVersion.trim().length() > 0) {
				if (loaders.containsKey(realVersion)) {
					return true;
				}
				final URL[] us = {new URL("file:" + jdbcUrl)};
				ClassLoader loader = AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
					public URLClassLoader run() {
						return new URLClassLoader(us);
					}
				});
				loaders.put(realVersion, loader);
				return true;
			}
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	/**
	 * 
	 * Return whether this class loader already exist
	 * 
	 * @param jdbcUrl the JDBC URL
	 * @return <code>true</code> if exist;<code>false</code> otherwise
	 */
	public static boolean isContainedClassLoader(String jdbcUrl) {
		try {
			String version = getJdbcJarVersion(jdbcUrl);
			if (loaders.containsKey(version)) {
				return true;
			}
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	/**
	 * Clear all the class loader
	 */
	public static void clearClassLoader() {
		loaders.clear();
	}

	/**
	 * 
	 * Remove the class loader according to JDBC version
	 * 
	 * @param jdbcVersion the jdbc version(format:CUBRID-JDBC-8.2.0.1147)
	 */
	public static void removeClassLoader(String jdbcVersion) {
		loaders.remove(jdbcVersion);
	}

	/**
	 * Get all the class loader
	 * 
	 * @return the map(key is the JDBC jar
	 *         version(format:CUBRID-JDBC-8.2.0.1147),value is the url path)
	 */
	public static Map<String, ClassLoader> getClassLoaderMap() {
		return loaders;
	}
}
