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

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jkiss.dbeaver.ext.turbographpp.jdbc.proxy.manage.CUBRIDProxySQLException;
import org.jkiss.dbeaver.ext.turbographpp.jdbc.proxy.manage.JdbcClassLoaderFactory;
import org.jkiss.dbeaver.ext.turbographpp.jdbc.proxy.manage.ReflectionUtil;

/**
 * The proxy of CUBRIDOID
 *
 * @author robinhood
 *
 */
public class CUBRIDOIDProxy {
	private final Object cubridOID;
	private String jdbcVersion;

	public CUBRIDOIDProxy(Object obj) {
		cubridOID = obj;
	}

	/**
	 *
	 * Invoke getValues method in CUBRIDOID object and return ResultSet object
	 *
	 * @param attrNames the attribute name array
	 * @return the ResultSet
	 * @throws SQLException the SQL exception
	 */
	public ResultSet getValues(String[] attrNames) throws SQLException {
		CUBRIDResultSetProxy resultSetProxy = new CUBRIDResultSetProxy(
				(ResultSet) ReflectionUtil.invoke(cubridOID, "getValues",
						String[].class, attrNames));
		resultSetProxy.setJdbcVersion(jdbcVersion);
		return resultSetProxy;
	}

	/**
	 *
	 * Invoke the setValues method in CUBRIDOID object
	 *
	 * @param attrNames the attribute name array
	 * @param values the value array
	 * @throws SQLException the SQL exception
	 */
	public void setValues(String[] attrNames, Object[] values) throws SQLException {
		ReflectionUtil.invoke(cubridOID, "setValues", new Class[]{
				String[].class, Object[].class }, new Object[]{attrNames,
				values });
	}

	/**
	 * Invoke the remove method in CUBRIDOID object
	 *
	 * @throws SQLException the SQL exception
	 */
	public void remove() throws SQLException {
		ReflectionUtil.invoke(cubridOID, "remove");
	}

	/**
	 *
	 * Invoke the isInstance method in CUBRIDOID object
	 *
	 * @return <code>true</code> if it is instance;<code>false</code>otherwise
	 * @throws SQLException the SQL exception
	 */
	public boolean isInstance() throws SQLException {
		return (Boolean) ReflectionUtil.invoke(cubridOID, "isInstance");
	}

	/**
	 *
	 * Invoke the setReadLock method in CUBRIDOID object
	 *
	 * @throws SQLException the exception
	 */
	public void setReadLock() throws SQLException {
		ReflectionUtil.invoke(cubridOID, "setReadLock");
	}

	/**
	 *
	 * Invoke the setWriteLock method in CUBRIDOID object
	 *
	 * @throws SQLException the exception
	 */
	public void setWriteLock() throws SQLException {
		ReflectionUtil.invoke(cubridOID, "setWriteLock");
	}

	/**
	 *
	 * Invoke the loadGLO method in CUBRIDOID object
	 *
	 * @param stream the stream
	 * @throws SQLException the exception
	 */
	public void loadGLO(OutputStream stream) throws SQLException {
		ReflectionUtil.invoke(cubridOID, "loadGLO", OutputStream.class, stream);
	}

	/**
	 *
	 * Invoke the saveGLO method in CUBRIDOID object
	 *
	 * @param stream the stream
	 * @throws SQLException the exception
	 */
	public void saveGLO(InputStream stream) throws SQLException {
		ReflectionUtil.invoke(cubridOID, "saveGLO", InputStream.class, stream);
	}

	/**
	 *
	 * Invoke the saveGLO method in CUBRIDOID object
	 *
	 * @param stream the stream
	 * @param length the length
	 * @throws SQLException the exception
	 */
	public void saveGLO(InputStream stream, int length) throws SQLException {
		ReflectionUtil.invoke(cubridOID, "saveGLO", new Class[]{
				InputStream.class, int.class }, new Object[]{stream, length });
	}

	/**
	 *
	 * Invoke the addToSet method in CUBRIDOID object
	 *
	 * @param attrName the attribute name
	 * @param value the value
	 * @throws SQLException the exception
	 */
	public void addToSet(String attrName, Object value) throws SQLException {
		ReflectionUtil.invoke(cubridOID, "addToSet", new Class<?>[]{
				String.class, Object.class }, new Object[]{attrName, value });
	}

	/**
	 *
	 * Invoke the removeFromSet method in CUBRIDOID object
	 *
	 * @param attrName the attribute name
	 * @param value the value
	 * @throws SQLException the exception
	 */
	public void removeFromSet(String attrName, Object value) throws SQLException {
		ReflectionUtil.invoke(cubridOID, "removeFromSet", new Class<?>[]{
				String.class, Object.class }, new Object[]{attrName, value });
	}

	/**
	 *
	 * Invoke the addToSequence method in CUBRIDOID object
	 *
	 * @param attrName the attribute name
	 * @param index the index
	 * @param value the value
	 * @throws SQLException the exception
	 */
	public void addToSequence(String attrName, int index, Object value) throws SQLException {
		ReflectionUtil.invoke(cubridOID, "addToSequence", new Class<?>[]{
				String.class, int.class, Object.class }, new Object[]{attrName,
				index, value });
	}

	/**
	 *
	 * Invoke the putIntoSequence method in CUBRIDOID object
	 *
	 * @param attrName the attribute name
	 * @param index the index
	 * @param value the value
	 * @throws SQLException the exception
	 */
	public void putIntoSequence(String attrName, int index, Object value) throws SQLException {
		ReflectionUtil.invoke(cubridOID, "putIntoSequence", new Class<?>[]{
				String.class, int.class, Object.class }, new Object[]{attrName,
				index, value });
	}

	/**
	 *
	 * Invoke the removeFromSequence method in CUBRIDOID object
	 *
	 * @param attrName the attribute name
	 * @param index the index
	 * @throws SQLException the exception
	 */
	public void removeFromSequence(String attrName, int index) throws SQLException {
		ReflectionUtil.invoke(cubridOID, "removeFromSequence", new Class[]{
				String.class, int.class }, new Object[]{attrName, index });
	}

	/**
	 *
	 * Invoke the getOidString method in CUBRIDOID object
	 *
	 * @return the oid string
	 * @throws SQLException the exception
	 */
	public String getOidString() throws SQLException {
		return (String) ReflectionUtil.invoke(cubridOID, "getOidString");
	}

	/**
	 *
	 * Invoke the getOID method in CUBRIDOID object
	 *
	 * @return the byte array
	 * @throws SQLException the exception
	 */
	public byte[] getOID() throws SQLException {
		return (byte[]) ReflectionUtil.invoke(cubridOID, "getOID");
	}

	/**
	 *
	 * Invoke the getTableName method in CUBRIDOID object
	 *
	 * @return the string
	 * @throws SQLException the exception
	 */
	public String getTableName() throws SQLException {
		return (String) ReflectionUtil.invoke(cubridOID, "getTableName");
	}

	/**
	 * Load the CUBRIDOID class
	 *
	 * @param version the version
	 * @return the CUBRIDOID class
	 */
	public static Class<?> getCUBRIDOIDClass(String version) {

		ClassLoader loader = JdbcClassLoaderFactory.getClassLoader(version);
		if (isAfterJdbc111(version)) {
			try {
				return loader.loadClass("cubrid.sql.CUBRIDOIDImpl");
			} catch (ClassNotFoundException e) {
				return null;
			}
		} else {
			try {
				return loader.loadClass("cubrid.sql.CUBRIDOID");
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
	}

	public Class<?> getCUBRIDOIDClass() {
		return cubridOID.getClass();
	}

	/**
	 *
	 * Get CUBRIDOIDProxy instance
	 *
	 * @param conn the connection proxy
	 * @param oidStr the oid str
	 * @return the instance
	 * @throws SQLException the SQL exception
	 */
	public static CUBRIDOIDProxy getNewInstance(CUBRIDConnectionProxy conn,
			String oidStr) throws SQLException {
		if (conn == null) {
			return null;
		}
		Class<?> clazz = null;

		try {
			String jdbcVersion = conn.getJdbcVersion();
			if (isAfterJdbc111(jdbcVersion)) {
				clazz = conn.getProxyClass().getClassLoader().loadClass(
						"cubrid.sql.CUBRIDOIDImpl");
			} else {
				clazz = conn.getProxyClass().getClassLoader().loadClass(
						"cubrid.sql.CUBRIDOID");
			}
		} catch (ClassNotFoundException e) {
			throw new CUBRIDProxySQLException(e, -90007);
		}
		return new CUBRIDOIDProxy(ReflectionUtil.invokeStaticMethod(clazz,
				"getNewInstance", new Class<?>[]{
						((CUBRIDConnectionProxy) conn).getProxyClass(),
						String.class },
				new Object[]{((CUBRIDConnectionProxy) conn).getProxyObject(),
						oidStr }));
	}

	/**
	 * Invoke the gloRead method in CUBRIDOID object
	 *
	 * @param pos the position
	 * @param length the length
	 * @param dataBuffer the databuffer array
	 * @param bufOffset the buffer offset
	 * @return the position
	 * @throws SQLException the SQL exception
	 *
	 */
	public int gloRead(long pos, int length, byte[] dataBuffer, int bufOffset) throws SQLException {
		return (Integer) ReflectionUtil.invoke(cubridOID, "gloRead",
				new Class[]{long.class, int.class, byte[].class, int.class },
				new Object[]{pos, length, dataBuffer, bufOffset });
	}

	public String getJdbcVersion() {
		return jdbcVersion;
	}

	public void setJdbcVersion(String jdbcVersion) {
		this.jdbcVersion = jdbcVersion;
	}

	public Object getProxyObject() {
		return cubridOID;
	}
	
	private static boolean isAfterJdbc111(String jdbcVersion) {
		String[] tempString;
		int tempIntVersion = 0;

		tempString = jdbcVersion.replaceAll("[^0-9.]","").split("\\.");

		tempIntVersion += Integer.valueOf(tempString[0]).intValue() * 10;
		tempIntVersion += Integer.valueOf(tempString[1]).intValue();

		if (tempIntVersion >= 111) {
			return true;
		}
		
		return false;
	}

}
