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

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The mapping between server version and JDBC version,including the JDBC
 * version by the server version
 * 
 * @author robinhood
 * 
 */
public class ServerJdbcVersionMapping {

	public static final String JDBC_SELF_ADAPTING_VERSION = "Auto Detect";
	static Map<String, String[]> properties = new HashMap<String, String[]>();
	static {
		readProperties("server_jdbc_mapping.properties");
	}

	/**
	 * Return the JDBC versions that are supported by this server version
	 * 
	 * @param serverVersion the CUBRID server version
	 * @return the CUBRID JDBC version
	 * @deprecated 8.4.0
	 */
	public static String[] getSupportedJdbcVersions(String serverVersion) {
		if (serverVersion == null) {
			return null;
		}
		String version = null;
		String[] versions = serverVersion.split("\\.");
		if (versions.length == 2) {
			version = serverVersion;
		} else if (versions.length == 3) {
			version = serverVersion.substring(0, serverVersion.lastIndexOf("."));
		} else if (versions.length == 4) {
			version = versions[0] + "." + versions[1];
		} else {
			return null;
		}
		return properties.get(version);
	}

	/**
	 * get the Properties
	 * 
	 * @param path the path
	 * @return the properties object
	 */
	private Properties getProperties(String path) {
		Properties initProps = new Properties();
		InputStream in = null;
		try {
			in = this.getClass().getResourceAsStream(path);
			initProps.load(in);
		} catch (Exception e) {
			return initProps;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				in = null;
			}
		}
		return initProps;
	}

	/**
	 * Read the properties
	 * 
	 * @param filePath the file path
	 */
	private static void readProperties(String filePath) {
		Properties props = null;
		ServerJdbcVersionMapping initProperty = new ServerJdbcVersionMapping();
		try {
			props = initProperty.getProperties(filePath);
			Enumeration<?> en = props.propertyNames();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String jdbcVersions = props.getProperty(key);
				if (!properties.containsKey(key)) {
					properties.put(key, new String[]{jdbcVersions });
				}
			}
		} catch (Exception e) {
			properties.clear();
		}
	}
}
