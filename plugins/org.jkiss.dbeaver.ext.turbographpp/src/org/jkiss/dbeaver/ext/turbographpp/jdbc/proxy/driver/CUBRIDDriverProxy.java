/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
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
package org.jkiss.dbeaver.ext.turbographpp.jdbc.proxy.driver;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import org.jkiss.dbeaver.ext.turbographpp.jdbc.proxy.manage.CUBRIDProxyException;
import org.jkiss.dbeaver.ext.turbographpp.jdbc.proxy.manage.JdbcClassLoaderFactory;
import org.jkiss.dbeaver.ext.turbographpp.jdbc.proxy.manage.ReflectionUtil;

/**
 * The proxy of cubrid.jdbc.driver.CUBRIDDriver
 * 
 * @author robinhood
 * 
 */
public class CUBRIDDriverProxy implements
		Driver {

	private final Driver driver;

	private String jdbcVersion;

	@SuppressWarnings("unchecked")
	public CUBRIDDriverProxy(File jdbcJarFile) throws CUBRIDProxyException {
		if (jdbcJarFile == null) {
			throw new CUBRIDProxyException("The file can not be null.");
		}
		String filePath = jdbcJarFile.getAbsolutePath();
		if (JdbcClassLoaderFactory.registerClassLoader(filePath)) {
			try {
				String version = JdbcClassLoaderFactory.getJdbcJarVersion(filePath);
				this.jdbcVersion = version;
				ClassLoader loader = JdbcClassLoaderFactory.getClassLoader(jdbcVersion);
				Class<Driver> driverClazz = (Class<Driver>) loader.loadClass("cubrid.jdbc.driver.CUBRIDDriver");
				driver = driverClazz.newInstance();
			} catch (IOException e) {
				throw new CUBRIDProxyException(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				throw new CUBRIDProxyException(e.getMessage(), e);
			} catch (InstantiationException e) {
				throw new CUBRIDProxyException(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				throw new CUBRIDProxyException(e.getMessage(), e);
			}
		} else {
			driver = null;
			throw new CUBRIDProxyException("Invalid CUBRID jar driver.");
		}
	}

	@SuppressWarnings("unchecked")
	public CUBRIDDriverProxy(String jdbcVersion) throws CUBRIDProxyException {
		super();
		this.jdbcVersion = jdbcVersion;
		ClassLoader loader = JdbcClassLoaderFactory.getClassLoader(jdbcVersion);
		if (loader == null) {
			throw new CUBRIDProxyException(
					"Please use <code>JdbcClassLoaderFactory.registerClassLoader(String)</code> method to register the class loader of this version firstly.");
		}
		Class<Driver> driverClazz;
		try {
			driverClazz = (Class<Driver>) loader.loadClass("cubrid.jdbc.driver.CUBRIDDriver");
			driver = driverClazz.newInstance();
		} catch (ClassNotFoundException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		} catch (InstantiationException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		}

	}

	public CUBRIDDriverProxy(Driver driver) {
		this.driver = driver;
	}

	/**
	 * @see Driver#acceptsURL(String)
	 * 
	 * @param url the URL of the database
	 * @return <code>true</code> if this driver understands the given URL;
	 *         <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 */
	public boolean acceptsURL(String url) throws SQLException {
		return driver.acceptsURL(url);
	}

	/**
	 * @see Driver#connect(String, Properties)
	 * @param url the URL of the database to which to connect
	 * @param info a list of arbitrary string tag/value pairs as connection
	 *        arguments. Normally at least a "user" and "password" property
	 *        should be included.
	 * @return a <code>Connection</code> object that represents a connection to
	 *         the URL
	 * @exception SQLException if a database access error occurs
	 */
	public Connection connect(String url, Properties info) throws SQLException {
		Connection conn = driver.connect(url, info);
		if (conn == null) {
			throw new SQLException("Can't connect to the url:" + url);
		}
		return new CUBRIDConnectionProxy(conn, jdbcVersion);
	}

	/**
	 * @see Driver#connect(String, Properties)
	 * @param url the URL of the database to which to connect
	 * @param info a list of arbitrary string tag/value pairs as connection
	 *        arguments. Normally at least a "user" and "password" property
	 *        should be included.
	 * @param version the version
	 * @return a <code>Connection</code> object that represents a connection to
	 *         the URL
	 * @exception SQLException if a database access error occurs
	 */
	public Connection connect(String url, Properties info, String version) throws SQLException {
		Connection conn = driver.connect(url, info);
		if (conn == null) {
			throw new SQLException("Can't connect to the url:" + url);
		}
		return new CUBRIDConnectionProxy(conn, version);
	}

	public int getMajorVersion() {
		return driver.getMajorVersion();
	}

	public int getMinorVersion() {
		return driver.getMinorVersion();
	}

	/**
	 * @see Driver#getPropertyInfo(String, Properties)
	 * 
	 * @param url the URL of the database to which to connect
	 * @param info a proposed list of tag/value pairs that will be sent on
	 *        connect open
	 * @return an array of <code>DriverPropertyInfo</code> objects describing
	 *         possible properties. This array may be an empty array if no
	 *         properties are required.
	 * @exception SQLException if a database access error occurs
	 */
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return driver.getPropertyInfo(url, info);
	}

	/**
	 * @see Driver#jdbcCompliant()
	 * 
	 * @return <code>true</code> if this driver is JDBC Compliant;
	 *         <code>false</code> otherwise
	 */
	public boolean jdbcCompliant() {
		return driver.jdbcCompliant();
	}

	/**
	 * 
	 * Get default connection
	 * 
	 * @return the connection object
	 * @throws SQLException the sql exception
	 */
	public Connection defaultConnection() throws SQLException {
		return new CUBRIDConnectionProxy((Connection) ReflectionUtil.invoke(
				driver, "defaultConnection"), jdbcVersion);
	}

	/**
	 * 
	 * Get patch version
	 * 
	 * @return the version
	 * @throws SQLException the sql exception
	 */
	public int getPatchVersion() throws SQLException {
		return ReflectionUtil.getStaticFieldValue("patch_version",
				driver.getClass());
	}

	public String getJdbcVersion() {
		return jdbcVersion;
	}

	public void setJdbcVersion(String jdbcVersion) {
		this.jdbcVersion = jdbcVersion;
	}

	//------------------------- JDBC 4.1 -----------------------------------

    /**
     * Return the parent Logger of all the Loggers used by this driver. This
     * should be the Logger farthest from the root Logger that is
     * still an ancestor of all of the Loggers used by this driver. Configuring
     * this Logger will affect all of the log messages generated by the driver.
     * In the worst case, this may be the root Logger.
     *
     * @return the parent Logger for this driver
     * @throws SQLFeatureNotSupportedException if the driver does not use <code>java.util.logging<code>.
     * @since 1.7
     */
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return driver.getParentLogger();
	}

}
