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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.jkiss.dbeaver.ext.turbographpp.jdbc.proxy.manage.ReflectionUtil;

/**
 * The proxy of CUBRIDResultSetMetaData
 *
 * @author robinhood
 *
 */
public class CUBRIDResultSetMetaDataProxy implements
		ResultSetMetaData {
	private final ResultSetMetaData resultSetMetaData;

	public CUBRIDResultSetMetaDataProxy(ResultSetMetaData resultSetMetaData) {
		this.resultSetMetaData = resultSetMetaData;

	}

	/**
	 * @see ResultSetMetaData#getCatalogName(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return the name of the catalog for the table in which the given column
	 *         appears or "" if not applicable
	 * @exception SQLException if a database access error occurs
	 */
	public String getCatalogName(int column) throws SQLException {
		return resultSetMetaData.getCatalogName(column);
	}

	/**
	 * @see ResultSetMetaData#getColumnClassName(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return the fully-qualified name of the class in the Java programming
	 *         language that would be used by the method
	 *         <code>ResultSet.getObject</code> to retrieve the value in the
	 *         specified column. This is the class name used for custom mapping.
	 * @exception SQLException if a database access error occurs
	 */
	public String getColumnClassName(int column) throws SQLException {
		return resultSetMetaData.getColumnClassName(column);
	}

	/**
	 * @see ResultSetMetaData#getColumnCount()
	 * @return the number of columns
	 * @exception SQLException if a database access error occurs
	 */
	public int getColumnCount() throws SQLException {
		return resultSetMetaData.getColumnCount();
	}

	/**
	 * @see ResultSetMetaData#getColumnDisplaySize(int)
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return the normal maximum number of characters allowed as the width of
	 *         the designated column
	 * @exception SQLException if a database access error occurs
	 */
	public int getColumnDisplaySize(int column) throws SQLException {
		return resultSetMetaData.getColumnDisplaySize(column);
	}

	/**
	 * @see ResultSetMetaData#getColumnLabel(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return the suggested column title
	 * @exception SQLException if a database access error occurs
	 */
	public String getColumnLabel(int column) throws SQLException {
		return resultSetMetaData.getColumnLabel(column);
	}

	/**
	 * @see ResultSetMetaData#getColumnName(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return column name
	 * @exception SQLException if a database access error occurs
	 */
	public String getColumnName(int column) throws SQLException {
		return resultSetMetaData.getColumnName(column);
	}

	/**
	 * @see ResultSetMetaData#getColumnType(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return SQL type from java.sql.Types
	 * @exception SQLException if a database access error occurs
	 */
	public int getColumnType(int column) throws SQLException {
		return resultSetMetaData.getColumnType(column);
	}

	/**
	 * @see ResultSetMetaData#getColumnTypeName(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return type name used by the database. If the column type is a
	 *         user-defined type, then a fully-qualified type name is returned.
	 * @exception SQLException if a database access error occurs
	 */
	public String getColumnTypeName(int column) throws SQLException {
		return resultSetMetaData.getColumnTypeName(column);
	}

	/**
	 * @see ResultSetMetaData#getPrecision(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return precision
	 * @exception SQLException if a database access error occurs
	 */
	public int getPrecision(int column) throws SQLException {
		return resultSetMetaData.getPrecision(column);
	}

	/**
	 * @see ResultSetMetaData#getScale(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return scale
	 * @exception SQLException if a database access error occurs
	 */
	public int getScale(int column) throws SQLException {
		return resultSetMetaData.getScale(column);
	}

	/**
	 * @see ResultSetMetaData#getSchemaName(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return schema name or "" if not applicable
	 * @exception SQLException if a database access error occurs
	 */
	public String getSchemaName(int column) throws SQLException {
		return resultSetMetaData.getSchemaName(column);
	}

	/**
	 * @see ResultSetMetaData#getTableName(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return table name or "" if not applicable
	 * @exception SQLException if a database access error occurs
	 */
	public String getTableName(int column) throws SQLException {
		return resultSetMetaData.getTableName(column);
	}

	/**
	 * @see ResultSetMetaData#isAutoIncrement(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 */
	public boolean isAutoIncrement(int column) throws SQLException {
		return resultSetMetaData.isAutoIncrement(column);
	}

	/**
	 * @see ResultSetMetaData#isCaseSensitive(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 */
	public boolean isCaseSensitive(int column) throws SQLException {
		return resultSetMetaData.isCaseSensitive(column);
	}

	/**
	 * @see ResultSetMetaData#isCurrency(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 */
	public boolean isCurrency(int column) throws SQLException {
		return resultSetMetaData.isCurrency(column);
	}

	/**
	 * @see ResultSetMetaData#isDefinitelyWritable(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 */
	public boolean isDefinitelyWritable(int column) throws SQLException {
		return resultSetMetaData.isDefinitelyWritable(column);
	}

	/**
	 * @see ResultSetMetaData#isNullable(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return the nullability status of the given column; one of
	 *         <code>columnNoNulls</code>, <code>columnNullable</code> or
	 *         <code>columnNullableUnknown</code>
	 * @exception SQLException if a database access error occurs
	 */
	public int isNullable(int column) throws SQLException {
		return resultSetMetaData.isNullable(column);
	}

	/**
	 * @see ResultSetMetaData#isReadOnly(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 */
	public boolean isReadOnly(int column) throws SQLException {
		return resultSetMetaData.isReadOnly(column);
	}

	/**
	 * @see ResultSetMetaData#isSearchable(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 */
	public boolean isSearchable(int column) throws SQLException {
		return resultSetMetaData.isSearchable(column);
	}

	/**
	 * @see ResultSetMetaData#isSigned(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 */
	public boolean isSigned(int column) throws SQLException {
		return resultSetMetaData.isSigned(column);
	}

	/**
	 * @see ResultSetMetaData#isWritable(int)
	 * @param column the first column is 1, the second is 2, ...
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 */
	public boolean isWritable(int column) throws SQLException {
		return resultSetMetaData.isWritable(column);
	}

	/**
	 * Invoke the getElementType method in CUBRID ResultSetMetaData object
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return the element type
	 * @exception SQLException if a database access error occurs
	 */
	public int getElementType(int column) throws SQLException {
		String type = getColumnTypeName(column);
		if (!"SET".equals(type) && !"MULTISET".equals(type)
				&& !"SEQUENCE".equals(type)) {
			return -1;
		}
		return (Integer) ReflectionUtil.invoke(resultSetMetaData,
				"getElementType", new Class<?>[]{int.class },
				new Object[]{column });
	}

	/**
	 * Invoke the getElementTypeName method in CUBRID ResultSetMetaData object
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return the element type name
	 * @exception SQLException if a database access error occurs
	 */
	public String getElementTypeName(int column) throws SQLException {
		String type = getColumnTypeName(column);
		if (!"SET".equals(type) && !"MULTISET".equals(type)
				&& !"SEQUENCE".equals(type)) {
			return null;
		}
		return (String) ReflectionUtil.invoke(resultSetMetaData,
				"getElementTypeName", new Class<?>[]{int.class },
				new Object[]{column });

	}

	/**
	 * Returns true if this either implements the interface argument or is
	 * directly or indirectly a wrapper for an object that does. Returns false
	 * otherwise. If this implements the interface then return true, else if
	 * this is a wrapper then return the result of recursively calling
	 * <code>isWrapperFor</code> on the wrapped object. If this does not
	 * implement the interface and is not a wrapper, return false. This method
	 * should be implemented as a low-cost operation compared to
	 * <code>unwrap</code> so that callers can use this method to avoid
	 * expensive <code>unwrap</code> calls that may fail. If this method returns
	 * true then calling <code>unwrap</code> with the same argument should
	 * succeed.
	 *
	 * @param iface a Class defining an interface.
	 * @return true if this implements the interface or directly or indirectly
	 *         wraps an object that does.
	 * @throws java.sql.SQLException if an error occurs while determining
	 *         whether this is a wrapper for an object with the given interface.
	 * @since 1.6
	 */
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return resultSetMetaData.isWrapperFor(iface);
	}

	/**
	 * Returns an object that implements the given interface to allow access to
	 * non-standard methods, or standard methods not exposed by the proxy.
	 *
	 * If the receiver implements the interface then the result is the receiver
	 * or a proxy for the receiver. If the receiver is a wrapper and the wrapped
	 * object implements the interface then the result is the wrapped object or
	 * a proxy for the wrapped object. Otherwise return the the result of
	 * calling <code>unwrap</code> recursively on the wrapped object or a proxy
	 * for that result. If the receiver is not a wrapper and does not implement
	 * the interface, then an <code>SQLException</code> is thrown.
	 *
	 * @param iface A Class defining an interface that the result must
	 *        implement.
	 * @param <T> Class
	 * @return an object that implements the interface. May be a proxy for the
	 *         actual implementing object.
	 * @throws java.sql.SQLException If no object found that implements the
	 *         interface
	 * @since 1.6
	 */
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return resultSetMetaData.unwrap(iface);
	}
}
