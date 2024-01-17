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
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.jkiss.dbeaver.ext.turbographpp.jdbc.proxy.manage.ReflectionUtil;

/**
 * The proxy of CUBRIDResultSet
 *
 * @author robinhood
 *
 */
public class CUBRIDResultSetProxy implements
		ResultSet {
	private final ResultSet rs;
	private String jdbcVersion;

	public String getJdbcVersion() {
		return jdbcVersion;
	}

	public void setJdbcVersion(String jdbcVersion) {
		this.jdbcVersion = jdbcVersion;
	}

	public CUBRIDResultSetProxy(ResultSet rs) {
		this.rs = rs;
	}

	/**
	 * @see ResultSet#absolute(int)
	 * @param row the number of the row to which the cursor should move. A
	 *        positive number indicates the row number counting from the
	 *        beginning of the result set; a negative number indicates the row
	 *        number counting from the end of the result set
	 * @return <code>true</code> if the cursor is on the result set;
	 *         <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs, or the result
	 *            set type is <code>TYPE_FORWARD_ONLY</code>
	 */
	public boolean absolute(int row) throws SQLException {
		return rs.absolute(row);
	}

	/**
	 * @see ResultSet#afterLast()
	 * @exception SQLException if a database access error occurs or the result
	 *            set type is <code>TYPE_FORWARD_ONLY</code>
	 */
	public void afterLast() throws SQLException {
		rs.afterLast();
	}

	/**
	 * @see ResultSet#beforeFirst()
	 * @exception SQLException if a database access error occurs or the result
	 *            set type is <code>TYPE_FORWARD_ONLY</code>
	 */
	public void beforeFirst() throws SQLException {
		rs.beforeFirst();
	}

	/**
	 * @see ResultSet#cancelRowUpdates()
	 * @exception SQLException if a database access error occurs
	 */
	public void cancelRowUpdates() throws SQLException {
		rs.cancelRowUpdates();
	}

	/**
	 * @see ResultSet#clearWarnings()
	 * @exception SQLException if a database access error occurs
	 */
	public void clearWarnings() throws SQLException {
		rs.clearWarnings();
	}

	/**
	 * @see ResultSet#close()
	 * @exception SQLException if a database access error occurs
	 */
	public void close() throws SQLException {
		rs.close();
	}

	/**
	 * @see ResultSet#deleteRow()
	 * @exception SQLException if a database access error occurs
	 */
	public void deleteRow() throws SQLException {
		rs.deleteRow();
	}

	/**
	 * @see ResultSet#findColumn(String)
	 * @param columnLabel the name of the column
	 * @return the column index of the given column name
	 * @exception SQLException if the <code>ResultSet</code> object does not
	 *            contain <code>columnName</code> or a database access error
	 *            occurs
	 */
	public int findColumn(String columnLabel) throws SQLException {
		return rs.findColumn(columnLabel);
	}

	/**
	 * @see ResultSet#first()
	 * @return <code>true</code> if the cursor is on a valid row;
	 *         <code>false</code> if there are no rows in the result set
	 * @exception SQLException if a database access error occurs or the result
	 *            set type is <code>TYPE_FORWARD_ONLY</code>
	 */
	public boolean first() throws SQLException {
		return rs.first();
	}

	/**
	 * @see ResultSet#getArray(int)
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an <code>Array</code> object representing the SQL
	 *         <code>ARRAY</code> value in the specified column
	 * @exception SQLException if a database access error occurs
	 */
	public Array getArray(int columnIndex) throws SQLException {
		return rs.getArray(columnIndex);
	}

	/**
	 * @see ResultSet#getArray(String)
	 * @param columnLabel the name of the column from which to retrieve the
	 *        value
	 * @return an <code>Array</code> object representing the SQL
	 *         <code>ARRAY</code> value in the specified column
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public Array getArray(String columnLabel) throws SQLException {
		return rs.getArray(columnLabel);
	}

	/**
	 * @see ResultSet#getAsciiStream(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return a Java input stream that delivers the database column value as a
	 *         stream of one-byte ASCII characters; if the value is SQL
	 *         <code>NULL</code>, the value returned is <code>null</code>
	 * @exception SQLException if a database access error occurs
	 */
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return rs.getAsciiStream(columnIndex);
	}

	/**
	 * @see ResultSet#getAsciiStream(String)
	 * @param columnLabel the SQL name of the column
	 * @return a Java input stream that delivers the database column value as a
	 *         stream of one-byte ASCII characters. If the value is SQL
	 *         <code>NULL</code>, the value returned is <code>null</code>.
	 * @exception SQLException if a database access error occurs
	 */
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		return rs.getAsciiStream(columnLabel);
	}

	/**
	 * @see ResultSet#getBigDecimal(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value (full precision); if the value is SQL
	 *         <code>NULL</code>, the value returned is <code>null</code> in the
	 *         Java programming language.
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return rs.getBigDecimal(columnIndex);
	}

	/**
	 * @see ResultSet#getBigDecimal(String)
	 * @param columnLabel the column name
	 * @return the column value (full precision); if the value is SQL
	 *         <code>NULL</code>, the value returned is <code>null</code> in the
	 *         Java programming language.
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		return rs.getBigDecimal(columnLabel);
	}

	/**
	 * @see ResultSet#getBigDecimal(int,int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param scale the number of digits to the right of the decimal point
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>null</code>
	 * @exception SQLException if a database access error occurs
	 * @deprecated
	 */
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return rs.getBigDecimal(columnIndex, scale);
	}

	/**
	 * @see ResultSet#getBigDecimal(String,int)
	 * @param columnLabel the SQL name of the column
	 * @param scale the number of digits to the right of the decimal point
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>null</code>
	 * @exception SQLException if a database access error occurs
	 * @deprecated
	 */
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		return rs.getBigDecimal(columnLabel, scale);
	}

	/**
	 * @see ResultSet#getBinaryStream(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return a Java input stream that delivers the database column value as a
	 *         stream of uninterpreted bytes; if the value is SQL
	 *         <code>NULL</code>, the value returned is <code>null</code>
	 * @exception SQLException if a database access error occurs
	 */
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return rs.getBinaryStream(columnIndex);
	}

	/**
	 * @see ResultSet#getBinaryStream(String)
	 * @param columnLabel the SQL name of the column
	 * @return a Java input stream that delivers the database column value as a
	 *         stream of uninterpreted bytes; if the value is SQL
	 *         <code>NULL</code>, the result is <code>null</code>
	 * @exception SQLException if a database access error occurs
	 */
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		return rs.getBinaryStream(columnLabel);
	}

	/**
	 * @see ResultSet#getBlob(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return a <code>Blob</code> object representing the SQL <code>BLOB</code>
	 *         value in the specified column
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 */
	public Blob getBlob(int columnIndex) throws SQLException {
		Blob blob = rs.getBlob(columnIndex);
		return blob == null ? null : new CUBRIDBlobProxy(blob);
	}

	/**
	 * @see ResultSet#getBlob(String)
	 * @param columnLabel the name of the column from which to retrieve the
	 *        value
	 * @return a <code>Blob</code> object representing the SQL <code>BLOB</code>
	 *         value in the specified column
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 */
	public Blob getBlob(String columnLabel) throws SQLException {
		return new CUBRIDBlobProxy(rs.getBlob(columnLabel));
	}

	/**
	 * @see ResultSet#getBoolean(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>false</code>
	 * @exception SQLException if a database access error occurs
	 */
	public boolean getBoolean(int columnIndex) throws SQLException {
		return rs.getBoolean(columnIndex);
	}

	/**
	 * @see ResultSet#getBoolean(String)
	 * @param columnLabel the SQL name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>false</code>
	 * @exception SQLException if a database access error occurs
	 */
	public boolean getBoolean(String columnLabel) throws SQLException {
		return rs.getBoolean(columnLabel);
	}

	/**
	 * @see ResultSet#getByte(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>0</code>
	 * @exception SQLException if a database access error occurs
	 */
	public byte getByte(int columnIndex) throws SQLException {
		return rs.getByte(columnIndex);
	}

	/**
	 * @see ResultSet#getByte(String)
	 * @param columnLabel the SQL name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>0</code>
	 * @exception SQLException if a database access error occurs
	 */
	public byte getByte(String columnLabel) throws SQLException {
		return rs.getByte(columnLabel);
	}

	/**
	 * @see ResultSet#getBytes(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>null</code>
	 * @exception SQLException if a database access error occurs
	 */
	public byte[] getBytes(int columnIndex) throws SQLException {
		return rs.getBytes(columnIndex);
	}

	/**
	 * @see ResultSet#getBytes(String)
	 * @param columnLabel the SQL name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>null</code>
	 * @exception SQLException if a database access error occurs
	 */
	public byte[] getBytes(String columnLabel) throws SQLException {
		return rs.getBytes(columnLabel);
	}

	/**
	 * @see ResultSet#getCharacterStream(int)
	 * @return a <code>java.io.Reader</code> object that contains the column
	 *         value; if the value is SQL <code>NULL</code>, the value returned
	 *         is <code>null</code> in the Java programming language.
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return rs.getCharacterStream(columnIndex);
	}

	/**
	 * @see ResultSet#getCharacterStream(String)
	 * @param columnLabel the name of the column
	 * @return a <code>java.io.Reader</code> object that contains the column
	 *         value; if the value is SQL <code>NULL</code>, the value returned
	 *         is <code>null</code> in the Java programming language
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		return rs.getCharacterStream(columnLabel);
	}

	/**
	 * @see ResultSet#getClob(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return a <code>Clob</code> object representing the SQL <code>CLOB</code>
	 *         value in the specified column
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public Clob getClob(int columnIndex) throws SQLException {
		return new CUBRIDClobProxy(rs.getClob(columnIndex));
	}

	/**
	 * @see ResultSet#getClob(String)
	 * @param columnLabel the name of the column from which to retrieve the
	 *        value
	 * @return a <code>Clob</code> object representing the SQL <code>CLOB</code>
	 *         value in the specified column
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public Clob getClob(String columnLabel) throws SQLException {
		return new CUBRIDClobProxy(rs.getClob(columnLabel));
	}

	/**
	 * @see ResultSet#getConcurrency()
	 * @return the concurrency type, either
	 *         <code>ResultSet.CONCUR_READ_ONLY</code> or
	 *         <code>ResultSet.CONCUR_UPDATABLE</code>
	 * @exception SQLException if a database access error occurs
	 */
	public int getConcurrency() throws SQLException {
		return rs.getConcurrency();
	}

	/**
	 * @see ResultSet#getCursorName()
	 * @return the SQL name for this <code>ResultSet</code> object's cursor
	 * @exception SQLException if a database access error occurs
	 */
	public String getCursorName() throws SQLException {
		return rs.getCursorName();
	}

	/**
	 * @see ResultSet#getDate(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>null</code>
	 * @exception SQLException if a database access error occurs
	 */
	public Date getDate(int columnIndex) throws SQLException {
		return rs.getDate(columnIndex);
	}

	/**
	 * @see ResultSet#getDate(String)
	 * @param columnLabel the SQL name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>null</code>
	 * @exception SQLException if a database access error occurs
	 */
	public Date getDate(String columnLabel) throws SQLException {
		return rs.getDate(columnLabel);
	}

	/**
	 * @see ResultSet#getDate(int,Calendar)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param cal the <code>java.util.Calendar</code> object to use in
	 *        constructing the date
	 * @return the column value as a <code>java.sql.Date</code> object; if the
	 *         value is SQL <code>NULL</code>, the value returned is
	 *         <code>null</code> in the Java programming language
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return rs.getDate(columnIndex, cal);
	}

	/**
	 * @see ResultSet#getDate(String,Calendar)
	 * @param columnLabel the SQL name of the column from which to retrieve the
	 *        value
	 * @param cal the <code>java.util.Calendar</code> object to use in
	 *        constructing the date
	 * @return the column value as a <code>java.sql.Date</code> object; if the
	 *         value is SQL <code>NULL</code>, the value returned is
	 *         <code>null</code> in the Java programming language
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		return rs.getDate(columnLabel, cal);
	}

	/**
	 * @see ResultSet#getDouble(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>0</code>
	 * @exception SQLException if a database access error occurs
	 */
	public double getDouble(int columnIndex) throws SQLException {
		return rs.getDouble(columnIndex);
	}

	/**
	 * @see ResultSet#getDouble(String)
	 * @param columnLabel the SQL name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>0</code>
	 * @exception SQLException if a database access error occurs
	 */
	public double getDouble(String columnLabel) throws SQLException {
		return rs.getDouble(columnLabel);
	}

	/**
	 * @see ResultSet#getFetchDirection()
	 * @return the current fetch direction for this <code>ResultSet</code>
	 *         object
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getFetchDirection() throws SQLException {
		return rs.getFetchDirection();
	}

	/**
	 * @see ResultSet#getFetchSize()
	 * @return the current fetch size for this <code>ResultSet</code> object
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getFetchSize() throws SQLException {
		return rs.getFetchSize();
	}

	/**
	 * @see ResultSet#getFloat(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>0</code>
	 * @exception SQLException if a database access error occurs
	 */
	public float getFloat(int columnIndex) throws SQLException {
		return rs.getFloat(columnIndex);
	}

	/**
	 * @see ResultSet#getFloat(String)
	 * @param columnLabel the SQL name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>0</code>
	 * @exception SQLException if a database access error occurs
	 */
	public float getFloat(String columnLabel) throws SQLException {
		return rs.getFloat(columnLabel);
	}

	/*	public int getHoldability() throws SQLException {
			return rs.getHoldability();
		}
	*/

	/**
	 * @see ResultSet#getInt(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>0</code>
	 * @exception SQLException if a database access error occurs
	 */
	public int getInt(int columnIndex) throws SQLException {
		return rs.getInt(columnIndex);
	}

	/**
	 * @see ResultSet#getInt(String)
	 * @param columnLabel the SQL name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>0</code>
	 * @exception SQLException if a database access error occurs
	 */
	public int getInt(String columnLabel) throws SQLException {
		return rs.getInt(columnLabel);
	}

	/**
	 * @see ResultSet#getLong(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>0</code>
	 * @exception SQLException if a database access error occurs
	 */
	public long getLong(int columnIndex) throws SQLException {
		return rs.getLong(columnIndex);
	}

	/**
	 * @see ResultSet#getLong(String)
	 * @param columnLabel the SQL name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>0</code>
	 * @exception SQLException if a database access error occurs
	 */
	public long getLong(String columnLabel) throws SQLException {
		return rs.getLong(columnLabel);
	}

	/**
	 * @see ResultSet#getMetaData()
	 * @return the description of this <code>ResultSet</code> object's columns
	 * @exception SQLException if a database access error occurs
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return new CUBRIDResultSetMetaDataProxy(rs.getMetaData());
	}

	/**
	 * @see ResultSet#getObject(int)
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return a <code>java.lang.Object</code> holding the column value
	 * @exception SQLException if a database access error occurs
	 */
	public Object getObject(int columnIndex) throws SQLException {
		return rs.getObject(columnIndex);
	}

	/**
	 * @see ResultSet#getObject(String)
	 * @param columnLabel the SQL name of the column
	 * @return a <code>java.lang.Object</code> holding the column value
	 * @exception SQLException if a database access error occurs
	 */
	public Object getObject(String columnLabel) throws SQLException {
		return rs.getObject(columnLabel);
	}

	/**
	 * @see ResultSet#getObject(int, Map<String,Class<?>>)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param map a <code>java.util.Map</code> object that contains the mapping
	 *        from SQL type names to classes in the Java programming language
	 * @return an <code>Object</code> in the Java programming language
	 *         representing the SQL value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		return rs.getObject(columnIndex, map);
	}

	/**
	 * @see ResultSet#getObject(String, Map<String,Class<?>>)
	 * @param columnLabel the name of the column from which to retrieve the
	 *        value
	 * @param map a <code>java.util.Map</code> object that contains the mapping
	 *        from SQL type names to classes in the Java programming language
	 * @return an <code>Object</code> representing the SQL value in the
	 *         specified column
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 */
	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		return rs.getObject(columnLabel, map);
	}

	/**
	 * @see ResultSet#getRef(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return a <code>Ref</code> object representing an SQL <code>REF</code>
	 *         value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public Ref getRef(int columnIndex) throws SQLException {
		return rs.getRef(columnIndex);
	}

	/**
	 * @see ResultSet#getRef(String)
	 * @param columnLabel the column name
	 * @return a <code>Ref</code> object representing the SQL <code>REF</code>
	 *         value in the specified column
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public Ref getRef(String columnLabel) throws SQLException {
		return rs.getRef(columnLabel);
	}

	/**
	 * @see ResultSet#getRow()
	 * @return the current row number; <code>0</code> if there is no current row
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getRow() throws SQLException {
		return rs.getRow();
	}

	/**
	 * @see ResultSet#getShort(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>0</code>
	 * @exception SQLException if a database access error occurs
	 */
	public short getShort(int columnIndex) throws SQLException {
		return rs.getShort(columnIndex);
	}

	/**
	 * @see ResultSet#getShort(String)
	 * @param columnLabel the SQL name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>0</code>
	 * @exception SQLException if a database access error occurs
	 */
	public short getShort(String columnLabel) throws SQLException {
		return rs.getShort(columnLabel);
	}

	/**
	 * @see ResultSet#getStatement()
	 * @return the <code>Statment</code> object that produced this
	 *         <code>ResultSet</code> object or <code>null</code> if the result
	 *         set was produced some other way
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public Statement getStatement() throws SQLException {
		return rs.getStatement();
	}

	/**
	 * @see ResultSet#getString(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>null</code>
	 * @exception SQLException if a database access error occurs
	 */
	public String getString(int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}

	/**
	 * @see ResultSet#getString(String)
	 * @param columnLabel the SQL name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>null</code>
	 * @exception SQLException if a database access error occurs
	 */
	public String getString(String columnLabel) throws SQLException {
		return rs.getString(columnLabel);
	}

	/**
	 * @see ResultSet#getTime(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>null</code>
	 * @exception SQLException if a database access error occurs
	 */
	public Time getTime(int columnIndex) throws SQLException {
		return rs.getTime(columnIndex);
	}

	/**
	 * @see ResultSet#getTime(String)
	 * @param columnLabel the SQL name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>null</code>
	 * @exception SQLException if a database access error occurs
	 */
	public Time getTime(String columnLabel) throws SQLException {
		return rs.getTime(columnLabel);
	}

	/**
	 * @see ResultSet#getTime(int,Calendar)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param cal the <code>java.util.Calendar</code> object to use in
	 *        constructing the time
	 * @return the column value as a <code>java.sql.Time</code> object; if the
	 *         value is SQL <code>NULL</code>, the value returned is
	 *         <code>null</code> in the Java programming language
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return rs.getTime(columnIndex, cal);
	}

	/**
	 * @see ResultSet#getTime(String,Calendar)
	 * @param columnLabel the SQL name of the column
	 * @param cal the <code>java.util.Calendar</code> object to use in
	 *        constructing the time
	 * @return the column value as a <code>java.sql.Time</code> object; if the
	 *         value is SQL <code>NULL</code>, the value returned is
	 *         <code>null</code> in the Java programming language
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		return rs.getTime(columnLabel, cal);
	}

	/**
	 * @see ResultSet#getTimestamp(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>null</code>
	 * @exception SQLException if a database access error occurs
	 */
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return rs.getTimestamp(columnIndex);
	}

	/**
	 * @see ResultSet#getTimestamp(String)
	 * @param columnLabel the SQL name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>null</code>
	 * @exception SQLException if a database access error occurs
	 */
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		return rs.getTimestamp(columnLabel);
	}

	/**
	 * @see ResultSet#getTimestamp(int,Calendar)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param cal the <code>java.util.Calendar</code> object to use in
	 *        constructing the timestamp
	 * @return the column value as a <code>java.sql.Timestamp</code> object; if
	 *         the value is SQL <code>NULL</code>, the value returned is
	 *         <code>null</code> in the Java programming language
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return rs.getTimestamp(columnIndex, cal);
	}

	/**
	 * @see ResultSet#getTimestamp(String,Calendar)
	 * @param columnLabel the SQL name of the column
	 * @param cal the <code>java.util.Calendar</code> object to use in
	 *        constructing the date
	 * @return the column value as a <code>java.sql.Timestamp</code> object; if
	 *         the value is SQL <code>NULL</code>, the value returned is
	 *         <code>null</code> in the Java programming language
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		return rs.getTimestamp(columnLabel, cal);
	}

	/**
	 * @see ResultSet#getType()
	 * @return <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *         <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *         <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getType() throws SQLException {
		return rs.getType();
	}

	/**
	 * @see ResultSet#getURL(int)
	 * @param columnIndex the index of the column 1 is the first, 2 is the
	 *        second,...
	 * @return the column value as a <code>java.net.URL</code> object; if the
	 *         value is SQL <code>NULL</code>, the value returned is
	 *         <code>null</code> in the Java programming language
	 * @exception SQLException if a database access error occurs, or if a URL is
	 *            malformed
	 *
	 */
	public URL getURL(int columnIndex) throws SQLException {
		return rs.getURL(columnIndex);
	}

	/**
	 * @see ResultSet#getURL(String)
	 * @param columnLabel the SQL name of the column
	 * @return the column value as a <code>java.net.URL</code> object; if the
	 *         value is SQL <code>NULL</code>, the value returned is
	 *         <code>null</code> in the Java programming language
	 * @exception SQLException if a database access error occurs or if a URL is
	 *            malformed
	 *
	 */
	public URL getURL(String columnLabel) throws SQLException {
		return rs.getURL(columnLabel);
	}

	/**
	 * @see ResultSet#getUnicodeStream(int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return a Java input stream that delivers the database column value as a
	 *         stream of two-byte Unicode characters; if the value is SQL
	 *         <code>NULL</code>, the value returned is <code>null</code>
	 *
	 * @exception SQLException if a database access error occurs
	 * @deprecated use <code>getCharacterStream</code> in place of
	 *             <code>getUnicodeStream</code>
	 */
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return rs.getUnicodeStream(columnIndex);
	}

	/**
	 * @see ResultSet#getUnicodeStream(String)
	 * @param columnLabel the SQL name of the column
	 * @return a Java input stream that delivers the database column value as a
	 *         stream of two-byte Unicode characters. If the value is SQL
	 *         <code>NULL</code>, the value returned is <code>null</code>.
	 * @exception SQLException if a database access error occurs
	 * @deprecated use <code>getCharacterStream</code> instead
	 */
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		return rs.getUnicodeStream(columnLabel);
	}

	/**
	 * @see ResultSet#getWarnings()
	 * @return the first <code>SQLWarning</code> object reported or
	 *         <code>null</code> if there are none
	 * @exception SQLException if a database access error occurs or this method
	 *            is called on a closed result set
	 */
	public SQLWarning getWarnings() throws SQLException {
		return rs.getWarnings();
	}

	/**
	 * @see ResultSet#insertRow()
	 * @exception SQLException if a database access error occurs, if this method
	 *            is called when the cursor is not on the insert row, or if not
	 *            all of non-nullable columns in the insert row have been given
	 *            a value
	 */
	public void insertRow() throws SQLException {
		rs.insertRow();
	}

	/**
	 * @see ResultSet#isAfterLast()
	 * @return <code>true</code> if the cursor is after the last row;
	 *         <code>false</code> if the cursor is at any other position or the
	 *         result set contains no rows
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean isAfterLast() throws SQLException {
		return rs.isAfterLast();
	}

	/**
	 * @see ResultSet#isBeforeFirst()
	 * @return <code>true</code> if the cursor is before the first row;
	 *         <code>false</code> if the cursor is at any other position or the
	 *         result set contains no rows
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean isBeforeFirst() throws SQLException {
		return rs.isBeforeFirst();
	}

	/**
	 * @see ResultSet#isFirst()
	 * @return <code>true</code> if the cursor is on the first row;
	 *         <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean isFirst() throws SQLException {
		return rs.isFirst();
	}

	/**
	 * @see ResultSet#isLast()
	 * @return <code>true</code> if the cursor is on the last row;
	 *         <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean isLast() throws SQLException {
		return rs.isLast();
	}

	/**
	 * @see ResultSet#last()
	 * @return <code>true</code> if the cursor is on a valid row;
	 *         <code>false</code> if there are no rows in the result set
	 * @exception SQLException if a database access error occurs or the result
	 *            set type is <code>TYPE_FORWARD_ONLY</code>
	 *
	 */
	public boolean last() throws SQLException {
		return rs.last();
	}

	/**
	 * @see ResultSet#moveToCurrentRow()
	 * @exception SQLException if a database access error occurs or the result
	 *            set is not updatable
	 */
	public void moveToCurrentRow() throws SQLException {
		rs.moveToCurrentRow();
	}

	/**
	 * @see ResultSet#moveToInsertRow()
	 * @exception SQLException if a database access error occurs or the result
	 *            set is not updatable
	 */
	public void moveToInsertRow() throws SQLException {
		rs.moveToInsertRow();
	}

	/**
	 * @see ResultSet#next()
	 * @return <code>true</code> if the new current row is valid;
	 *         <code>false</code> if there are no more rows
	 * @exception SQLException if a database access error occurs
	 */
	public boolean next() throws SQLException {
		return rs.next();
	}

	/**
	 * @see ResultSet#previous()
	 * @return <code>true</code> if the cursor is on a valid row;
	 *         <code>false</code> if it is off the result set
	 * @exception SQLException if a database access error occurs or the result
	 *            set type is <code>TYPE_FORWARD_ONLY</code>
	 */
	public boolean previous() throws SQLException {
		return rs.previous();
	}

	/**
	 * @see ResultSet#refreshRow()
	 * @exception SQLException if a database access error occurs or if this
	 *            method is called when the cursor is on the insert row
	 *
	 */
	public void refreshRow() throws SQLException {
		rs.refreshRow();
	}

	/**
	 * @see ResultSet#relative(int)
	 * @param rows an <code>int</code> specifying the number of rows to move
	 *        from the current row; a positive number moves the cursor forward;
	 *        a negative number moves the cursor backward
	 * @return <code>true</code> if the cursor is on a row; <code>false</code>
	 *         otherwise
	 * @exception SQLException if a database access error occurs, there is no
	 *            current row, or the result set type is
	 *            <code>TYPE_FORWARD_ONLY</code>
	 *
	 */
	public boolean relative(int rows) throws SQLException {
		return rs.relative(rows);
	}

	/**
	 * @see ResultSet#rowDeleted()
	 * @return <code>true</code> if a row was deleted and deletions are
	 *         detected; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean rowDeleted() throws SQLException {
		return rs.rowDeleted();
	}

	/**
	 * @see ResultSet#rowInserted()
	 * @return <code>true</code> if a row has had an insertion and insertions
	 *         are detected; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean rowInserted() throws SQLException {
		return rs.rowInserted();
	}

	/**
	 * @see ResultSet#rowUpdated()
	 * @return <code>true</code> if both (1) the row has been visibly updated by
	 *         the owner or another and (2) updates are detected
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean rowUpdated() throws SQLException {
		return rs.rowUpdated();
	}

	/**
	 * @see ResultSet#setFetchDirection(int)
	 * @param direction an <code>int</code> specifying the suggested fetch
	 *        direction; one of <code>ResultSet.FETCH_FORWARD</code>,
	 *        <code>ResultSet.FETCH_REVERSE</code>, or
	 *        <code>ResultSet.FETCH_UNKNOWN</code>
	 * @exception SQLException if a database access error occurs or the result
	 *            set type is <code>TYPE_FORWARD_ONLY</code> and the fetch
	 *            direction is not <code>FETCH_FORWARD</code>
	 */
	public void setFetchDirection(int direction) throws SQLException {
		rs.setFetchDirection(direction);
	}

	/**
	 * @see ResultSet#setFetchSize(int)
	 * @param rows the number of rows to fetch
	 * @exception SQLException if a database access error occurs or the
	 *            condition <code>0 <= rows <= Statement.getMaxRows()</code> is
	 *            not satisfied
	 */
	public void setFetchSize(int rows) throws SQLException {
		rs.setFetchSize(rows);
	}

	/**
	 * @see ResultSet#updateArray(int,Array)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param array the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateArray(int columnIndex, Array array) throws SQLException {
		rs.updateArray(columnIndex, array);
	}

	/**
	 * @see ResultSet#updateArray(String,Array)
	 * @param columnLabel the name of the column
	 * @param array the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateArray(String columnLabel, Array array) throws SQLException {
		rs.updateArray(columnLabel, array);
	}

	/*	public void updateAsciiStream(int columnIndex, InputStream x)
				throws SQLException {
			rs.updateAsciiStream(columnIndex, x);
		}

		public void updateAsciiStream(String columnLabel, InputStream x)
				throws SQLException {
			rs.updateAsciiStream(columnLabel, x);
		}
	*/
	/**
	 * @see ResultSet#updateAsciiStream(int,InputStream,int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param stream the new column value
	 * @param length the length of the stream
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateAsciiStream(int columnIndex, InputStream stream,
			int length) throws SQLException {
		rs.updateAsciiStream(columnIndex, stream, length);
	}

	/**
	 * @see ResultSet#updateAsciiStream(String,InputStream,int)
	 * @param columnLabel the name of the column
	 * @param stream the new column value
	 * @param length the length of the stream
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateAsciiStream(String columnLabel, InputStream stream,
			int length) throws SQLException {
		rs.updateAsciiStream(columnLabel, stream, length);
	}

	/**
	 * @see ResultSet#updateBigDecimal(int,BigDecimal)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param decimal the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateBigDecimal(int columnIndex, BigDecimal decimal) throws SQLException {
		rs.updateBigDecimal(columnIndex, decimal);
	}

	/**
	 * @see ResultSet#updateBigDecimal(String,BigDecimal)
	 * @param columnLabel the name of the column
	 * @param decimal the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateBigDecimal(String columnLabel, BigDecimal decimal) throws SQLException {
		rs.updateBigDecimal(columnLabel, decimal);
	}

	/**
	 * @see ResultSet#updateBinaryStream(int,InputStream,int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param stream the new column value
	 * @param length the length of the stream
	 * @exception SQLException if a database access error occurs
	 */
	public void updateBinaryStream(int columnIndex, InputStream stream,
			int length) throws SQLException {
		rs.updateBinaryStream(columnIndex, stream, length);
	}

	/**
	 * @see ResultSet#updateBinaryStream(String,InputStream,int)
	 * @param columnLabel the name of the column
	 * @param stream the new column value
	 * @param length the length of the stream
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateBinaryStream(String columnLabel, InputStream stream,
			int length) throws SQLException {
		rs.updateBinaryStream(columnLabel, stream, length);
	}

	/*	public void updateBinaryStream(int columnIndex, InputStream x, long length)
				throws SQLException {
			rs.updateBinaryStream(columnIndex, x, length);
		}

		public void updateBinaryStream(String columnLabel, InputStream x,
				long length) throws SQLException {
			rs.updateBinaryStream(columnLabel, x, length);
		}
	*/
	/**
	 * @see ResultSet#updateBlob(int,Blob)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param blob the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateBlob(int columnIndex, Blob blob) throws SQLException {
		rs.updateBlob(columnIndex, blob);
	}

	/**
	 * @see ResultSet#updateBlob(String,Blob)
	 *
	 * @param columnLabel the name of the column
	 * @param blob the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateBlob(String columnLabel, Blob blob) throws SQLException {
		rs.updateBlob(columnLabel, blob);
	}

	/**
	 * @see ResultSet#updateBoolean(int,boolean)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateBoolean(int columnIndex, boolean value) throws SQLException {
		rs.updateBoolean(columnIndex, value);
	}

	/**
	 * @see ResultSet#updateBoolean(String,boolean)
	 *
	 * @param columnLabel the name of the column
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateBoolean(String columnLabel, boolean value) throws SQLException {
		rs.updateBoolean(columnLabel, value);
	}

	/**
	 * @see ResultSet#updateByte(int,byte)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateByte(int columnIndex, byte value) throws SQLException {
		rs.updateByte(columnIndex, value);
	}

	/**
	 * @see ResultSet#updateByte(String,byte)
	 *
	 * @param columnLabel the name of the column
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateByte(String columnLabel, byte value) throws SQLException {
		rs.updateByte(columnLabel, value);
	}

	/**
	 * @see ResultSet#updateBytes(int,byte[])
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateBytes(int columnIndex, byte[] value) throws SQLException {
		rs.updateBytes(columnIndex, value);
	}

	/**
	 * @see ResultSet#updateBytes(String,byte[])
	 * @param columnLabel the name of the column
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateBytes(String columnLabel, byte[] value) throws SQLException {
		rs.updateBytes(columnLabel, value);
	}

	/**
	 * @see ResultSet#updateCharacterStream(int,Reader,int)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param reader the new column value
	 * @param length the length of the stream
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateCharacterStream(int columnIndex, Reader reader, int length) throws SQLException {
		rs.updateCharacterStream(columnIndex, reader, length);
	}

	/**
	 * @see ResultSet#updateCharacterStream(String,Reader,int)
	 * @param columnLabel the name of the column
	 * @param reader the <code>java.io.Reader</code> object containing the new
	 *        column value
	 * @param length the length of the stream
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateCharacterStream(String columnLabel, Reader reader,
			int length) throws SQLException {
		rs.updateCharacterStream(columnLabel, reader, length);
	}

	/*	public void updateCharacterStream(int columnIndex, Reader x, long length)
				throws SQLException {
			rs.updateCharacterStream(columnIndex, x, length);
		}

		public void updateCharacterStream(String columnLabel, Reader reader,
				long length) throws SQLException {
			rs.updateCharacterStream(columnLabel, reader, length);
		}
	*/
	/**
	 * @see ResultSet#updateClob(int,Clob)
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param clob the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateClob(int columnIndex, Clob clob) throws SQLException {
		rs.updateClob(columnIndex, clob);
	}

	/**
	 * @see ResultSet#updateClob(String,Clob)
	 *
	 * @param columnLabel the name of the column
	 * @param clob the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateClob(String columnLabel, Clob clob) throws SQLException {
		rs.updateClob(columnLabel, clob);
	}

	/**
	 * @see ResultSet#updateDate(int,Date)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param date the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateDate(int columnIndex, Date date) throws SQLException {
		rs.updateDate(columnIndex, date);
	}

	/**
	 * @see ResultSet#updateDate(String,Date)
	 *
	 * @param columnLabel the name of the column
	 * @param date the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateDate(String columnLabel, Date date) throws SQLException {
		rs.updateDate(columnLabel, date);
	}

	/**
	 * @see ResultSet#updateDouble(int,double)
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateDouble(int columnIndex, double value) throws SQLException {
		rs.updateDouble(columnIndex, value);
	}

	/**
	 * @see ResultSet#updateDouble(String,double)
	 *
	 * @param columnLabel the name of the column
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateDouble(String columnLabel, double value) throws SQLException {
		rs.updateDouble(columnLabel, value);
	}

	/**
	 * @see ResultSet#updateFloat(int,float)
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateFloat(int columnIndex, float value) throws SQLException {
		rs.updateFloat(columnIndex, value);
	}

	/**
	 * @see ResultSet#updateFloat(String,float)
	 *
	 * @param columnLabel the name of the column
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateFloat(String columnLabel, float value) throws SQLException {
		rs.updateFloat(columnLabel, value);
	}

	/**
	 * @see ResultSet#updateInt(int,int)
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateInt(int columnIndex, int value) throws SQLException {
		rs.updateInt(columnIndex, value);
	}

	/**
	 * @see ResultSet#updateInt(String,int)
	 *
	 * @param columnLabel the name of the column
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateInt(String columnLabel, int value) throws SQLException {
		rs.updateInt(columnLabel, value);
	}

	/**
	 * @see ResultSet#updateLong(int,long)
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateLong(int columnIndex, long value) throws SQLException {
		rs.updateLong(columnIndex, value);
	}

	/**
	 * @see ResultSet#updateLong(String,long)
	 *
	 * @param columnLabel the name of the column
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateLong(String columnLabel, long value) throws SQLException {
		rs.updateLong(columnLabel, value);
	}

	/**
	 * @see ResultSet#updateNull(int)
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateNull(int columnIndex) throws SQLException {
		rs.updateNull(columnIndex);
	}

	/**
	 * @see ResultSet#updateNull(String)
	 *
	 * @param columnLabel the name of the column
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateNull(String columnLabel) throws SQLException {
		rs.updateNull(columnLabel);
	}

	/**
	 * @see ResultSet#updateObject(int,Object)
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param obj the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateObject(int columnIndex, Object obj) throws SQLException {
		rs.updateObject(columnIndex, obj);
	}

	/**
	 * @see ResultSet#updateLong(String,Object)
	 *
	 * @param columnLabel the name of the column
	 * @param obj the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateObject(String columnLabel, Object obj) throws SQLException {
		rs.updateObject(columnLabel, obj);
	}

	/**
	 * @see ResultSet#updateObject(int,Object,int)
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param obj the new column value
	 * @param scaleOrLength for <code>java.sql.Types.DECIMA</code> or
	 *        <code>java.sql.Types.NUMERIC</code> types, this is the number of
	 *        digits after the decimal point. For all other types this value
	 *        will be ignored.
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateObject(int columnIndex, Object obj, int scaleOrLength) throws SQLException {
		rs.updateObject(columnIndex, obj, scaleOrLength);
	}

	/**
	 * @see ResultSet#updateObject(String,Object,int)
	 *
	 * @param columnLabel the name of the column
	 * @param obj the new column value
	 * @param scaleOrLength for <code>java.sql.Types.DECIMAL</code> or
	 *        <code>java.sql.Types.NUMERIC</code> types, this is the number of
	 *        digits after the decimal point. For all other types this value
	 *        will be ignored.
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateObject(String columnLabel, Object obj, int scaleOrLength) throws SQLException {
		rs.updateObject(columnLabel, obj, scaleOrLength);
	}

	/**
	 * @see ResultSet#updateRef(int,Ref)
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param ref the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateRef(int columnIndex, Ref ref) throws SQLException {
		rs.updateRef(columnIndex, ref);
	}

	/**
	 * @see ResultSet#updateRef(String,Ref)
	 *
	 * @param columnLabel the name of the column
	 * @param ref the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateRef(String columnLabel, Ref ref) throws SQLException {
		rs.updateRef(columnLabel, ref);
	}

	/**
	 * @see ResultSet#updateRow()
	 * @exception SQLException if a database access error occurs or if this
	 *            method is called when the cursor is on the insert row
	 */
	public void updateRow() throws SQLException {
		rs.updateRow();
	}

	/**
	 * @see ResultSet#updateShort(int,short)
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateShort(int columnIndex, short value) throws SQLException {
		rs.updateShort(columnIndex, value);
	}

	/**
	 * @see ResultSet#updateShort(String,short)
	 *
	 * @param columnLabel the name of the column
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateShort(String columnLabel, short value) throws SQLException {
		rs.updateShort(columnLabel, value);
	}

	/**
	 * @see ResultSet#updateString(int,String)
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateString(int columnIndex, String value) throws SQLException {
		rs.updateString(columnIndex, value);
	}

	/**
	 * @see ResultSet#updateString(String,String)
	 *
	 * @param columnLabel the name of the column
	 * @param value the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateString(String columnLabel, String value) throws SQLException {
		rs.updateString(columnLabel, value);
	}

	/**
	 * @see ResultSet#updateTime(int,Time)
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param time the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateTime(int columnIndex, Time time) throws SQLException {
		rs.updateTime(columnIndex, time);
	}

	/**
	 * @see ResultSet#updateTime(String,Time)
	 *
	 * @param columnLabel the name of the column
	 * @param time the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateTime(String columnLabel, Time time) throws SQLException {
		rs.updateTime(columnLabel, time);
	}

	/**
	 * @see ResultSet#updateTimestamp(int,Timestamp)
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param time the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateTimestamp(int columnIndex, Timestamp time) throws SQLException {
		rs.updateTimestamp(columnIndex, time);
	}

	/**
	 * @see ResultSet#updateTimestamp(String,Timestamp)
	 *
	 * @param columnLabel the name of the column
	 * @param time the new column value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void updateTimestamp(String columnLabel, Timestamp time) throws SQLException {
		rs.updateTimestamp(columnLabel, time);
	}

	/**
	 * @see ResultSet#wasNull()
	 * @return <code>true</code> if the last column value read was SQL
	 *         <code>NULL</code> and <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 */
	public boolean wasNull() throws SQLException {
		return rs.wasNull();
	}

	/**
	 * Invoke the getCollection method in CUBRID ResultSet object
	 *
	 * @param columnIndex the column index
	 * @return the object
	 * @exception SQLException if a database access error occurs
	 */
	public Object getCollection(int columnIndex) throws SQLException {
		return ReflectionUtil.invoke(rs, "getCollection",
				new Class<?>[]{int.class }, new Object[]{columnIndex });
	}

	/**
	 * Invoke the getCollection method in CUBRID ResultSet object
	 *
	 * @param columnName the column name
	 * @return the object
	 * @exception SQLException if a database access error occurs
	 */
	public Object getCollection(String columnName) throws SQLException {
		return ReflectionUtil.invoke(rs, "getCollection",
				new Class<?>[]{String.class }, new Object[]{columnName });
	}

	/**
	 * Invoke the getOID method in CUBRID ResultSet object
	 *
	 * @return the CUBRIDOIDProxy object
	 * @exception SQLException if a database access error occurs
	 */
	public CUBRIDOIDProxy getOID() throws SQLException {
		CUBRIDOIDProxy proxy = new CUBRIDOIDProxy(ReflectionUtil.invoke(rs,
				"getOID"));
		proxy.setJdbcVersion(jdbcVersion);
		return proxy;
	}

	/**
	 * Invoke the getOID method in CUBRID ResultSet object
	 *
	 * @param columnIndex the column index
	 * @return the CUBRIDOIDProxy object
	 * @exception SQLException if a database access error occurs
	 */
	public CUBRIDOIDProxy getOID(int columnIndex) throws SQLException {
		return new CUBRIDOIDProxy(ReflectionUtil.invoke(rs, "getOID",
				new Class<?>[]{int.class }, new Object[]{columnIndex }));
	}

	/**
	 * Invoke the getOID method in CUBRID ResultSet object
	 *
	 * @param columnName the column name
	 * @return the CUBRIDOIDProxy object
	 * @exception SQLException if a database access error occurs
	 */
	public CUBRIDOIDProxy getOID(String columnName) throws SQLException {
		return new CUBRIDOIDProxy(ReflectionUtil.invoke(rs, "getOID",
				String.class, columnName));
	}

	/**
	 * Invoke the getServerHandle method in CUBRID ResultSet object
	 *
	 * @return the handle
	 * @exception SQLException if a database access error occurs
	 */
	public int getServerHandle() throws SQLException {
		return (Integer) ReflectionUtil.invoke(rs, "getServerHandle");
	}

	/**
	 * Invoke the setReturnable method in CUBRID ResultSet object
	 *
	 * @exception SQLException if a database access error occurs
	 */
	public void setReturnable() throws SQLException {
		ReflectionUtil.invoke(rs, "setReturnable");
	}

	/**
	 * Retrieves the holdability of this <code>ResultSet</code> object
	 *
	 * @return either <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
	 *         <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
	 * @throws SQLException if a database access error occurs or this method is
	 *         called on a closed result set
	 * @since 1.6
	 */
	public int getHoldability() throws SQLException {
		return rs.getHoldability();
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>ResultSet</code> object as a <code>java.io.Reader</code> object. It
	 * is intended for use when accessing <code>NCHAR</code>,
	 * <code>NVARCHAR</code> and <code>LONGNVARCHAR</code> columns.
	 *
	 * @return a <code>java.io.Reader</code> object that contains the column
	 *         value; if the value is SQL <code>NULL</code>, the value returned
	 *         is <code>null</code> in the Java programming language.
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @exception SQLException if the columnIndex is not valid; if a database
	 *            access error occurs or this method is called on a closed
	 *            result set
	 * @since 1.6
	 */
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return rs.getNCharacterStream(columnIndex);
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>ResultSet</code> object as a <code>java.io.Reader</code> object. It
	 * is intended for use when accessing <code>NCHAR</code>,
	 * <code>NVARCHAR</code> and <code>LONGNVARCHAR</code> columns.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @return a <code>java.io.Reader</code> object that contains the column
	 *         value; if the value is SQL <code>NULL</code>, the value returned
	 *         is <code>null</code> in the Java programming language
	 * @exception SQLException if the columnLabel is not valid; if a database
	 *            access error occurs or this method is called on a closed
	 *            result set
	 * @since 1.6
	 */
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return rs.getNCharacterStream(columnLabel);
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>ResultSet</code> object as a <code>NClob</code> object in the Java
	 * programming language.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return a <code>NClob</code> object representing the SQL
	 *         <code>NCLOB</code> value in the specified column
	 * @exception SQLException if the columnIndex is not valid; if the driver
	 *            does not support national character sets; if the driver can
	 *            detect that a data conversion error could occur; this method
	 *            is called on a closed result set or if a database access error
	 *            occurs
	 * @since 1.6
	 */
	public NClob getNClob(int columnIndex) throws SQLException {
		return rs.getNClob(columnIndex);
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>ResultSet</code> object as a <code>NClob</code> object in the Java
	 * programming language.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @return a <code>NClob</code> object representing the SQL
	 *         <code>NCLOB</code> value in the specified column
	 * @exception SQLException if the columnLabel is not valid; if the driver
	 *            does not support national character sets; if the driver can
	 *            detect that a data conversion error could occur; this method
	 *            is called on a closed result set or if a database access error
	 *            occurs
	 * @since 1.6
	 */
	public NClob getNClob(String columnLabel) throws SQLException {
		return rs.getNClob(columnLabel);
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>ResultSet</code> object as a <code>String</code> in the Java
	 * programming language. It is intended for use when accessing
	 * <code>NCHAR</code>,<code>NVARCHAR</code> and <code>LONGNVARCHAR</code>
	 * columns.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>null</code>
	 * @exception SQLException if the columnIndex is not valid; if a database
	 *            access error occurs or this method is called on a closed
	 *            result set
	 * @since 1.6
	 */
	public String getNString(int columnIndex) throws SQLException {
		return rs.getNString(columnIndex);
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>ResultSet</code> object as a <code>String</code> in the Java
	 * programming language. It is intended for use when accessing
	 * <code>NCHAR</code>,<code>NVARCHAR</code> and <code>LONGNVARCHAR</code>
	 * columns.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the
	 *         value returned is <code>null</code>
	 * @exception SQLException if the columnLabel is not valid; if a database
	 *            access error occurs or this method is called on a closed
	 *            result set
	 * @since 1.6
	 */
	public String getNString(String columnLabel) throws SQLException {
		return rs.getNString(columnLabel);
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>ResultSet</code> object as a <code>java.sql.RowId</code> object in
	 * the Java programming language.
	 *
	 * @param columnIndex the first column is 1, the second 2, ...
	 * @return the column value; if the value is a SQL <code>NULL</code> the
	 *         value returned is <code>null</code>
	 * @throws SQLException if the columnIndex is not valid; if a database
	 *         access error occurs or this method is called on a closed result
	 *         set
	 * @since 1.6
	 */
	public RowId getRowId(int columnIndex) throws SQLException {
		return rs.getRowId(columnIndex);
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>ResultSet</code> object as a <code>java.sql.RowId</code> object in
	 * the Java programming language.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @return the column value ; if the value is a SQL <code>NULL</code> the
	 *         value returned is <code>null</code>
	 * @throws SQLException if the columnLabel is not valid; if a database
	 *         access error occurs or this method is called on a closed result
	 *         set
	 * @since 1.6
	 */
	public RowId getRowId(String columnLabel) throws SQLException {
		return rs.getRowId(columnLabel);
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>ResultSet</code> as a <code>java.sql.SQLXML</code> object in the
	 * Java programming language.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return a <code>SQLXML</code> object that maps an <code>SQL XML</code>
	 *         value
	 * @throws SQLException if the columnIndex is not valid; if a database
	 *         access error occurs or this method is called on a closed result
	 *         set
	 * @since 1.6
	 */
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		return rs.getSQLXML(columnIndex);
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>ResultSet</code> as a <code>java.sql.SQLXML</code> object in the
	 * Java programming language.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @return a <code>SQLXML</code> object that maps an <code>SQL XML</code>
	 *         value
	 * @throws SQLException if the columnLabel is not valid; if a database
	 *         access error occurs or this method is called on a closed result
	 *         set
	 * @since 1.6
	 */
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		return rs.getSQLXML(columnLabel);
	}

	/**
	 * Retrieves whether this <code>ResultSet</code> object has been closed. A
	 * <code>ResultSet</code> is closed if the method close has been called on
	 * it, or if it is automatically closed.
	 *
	 * @return true if this <code>ResultSet</code> object is closed; false if it
	 *         is still open
	 * @throws SQLException if a database access error occurs
	 * @since 1.6
	 */
	public boolean isClosed() throws SQLException {
		return rs.isClosed();
	}

	/**
	 * Updates the designated column with an ascii stream value. The data will
	 * be read from the stream as needed until end-of-stream is reached.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of
	 * <code>updateAsciiStream</code> which takes a length parameter.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param is the new column value
	 * @exception SQLException if the columnIndex is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateAsciiStream(int columnIndex, InputStream is) throws SQLException {
		rs.updateAsciiStream(columnIndex, is);
	}

	/**
	 * Updates the designated column with an ascii stream value. The data will
	 * be read from the stream as needed until end-of-stream is reached.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of
	 * <code>updateAsciiStream</code> which takes a length parameter.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param is the new column value
	 * @exception SQLException if the columnLabel is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateAsciiStream(String columnLabel, InputStream is) throws SQLException {
		rs.updateAsciiStream(columnLabel, is);
	}

	/**
	 * Updates the designated column with an ascii stream value, which will have
	 * the specified number of bytes.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param is the new column value
	 * @param length the length of the stream
	 * @exception SQLException if the columnIndex is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateAsciiStream(int columnIndex, InputStream is, long length) throws SQLException {
		rs.updateAsciiStream(columnIndex, is, length);

	}

	/**
	 * Updates the designated column with an ascii stream value, which will have
	 * the specified number of bytes.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param is the new column value
	 * @param length the length of the stream
	 * @exception SQLException if the columnLabel is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateAsciiStream(String columnLabel, InputStream is,
			long length) throws SQLException {
		rs.updateAsciiStream(columnLabel, is, length);

	}

	/**
	 * Updates the designated column with a binary stream value. The data will
	 * be read from the stream as needed until end-of-stream is reached.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of
	 * <code>updateBinaryStream</code> which takes a length parameter.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param is the new column value
	 * @exception SQLException if the columnIndex is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateBinaryStream(int columnIndex, InputStream is) throws SQLException {
		rs.updateBinaryStream(columnIndex, is);

	}

	/**
	 * Updates the designated column with a binary stream value. The data will
	 * be read from the stream as needed until end-of-stream is reached.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of
	 * <code>updateBinaryStream</code> which takes a length parameter.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param is the new column value
	 * @exception SQLException if the columnLabel is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateBinaryStream(String columnLabel, InputStream is) throws SQLException {
		rs.updateBinaryStream(columnLabel, is);

	}

	/**
	 * Updates the designated column with a binary stream value, which will have
	 * the specified number of bytes.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param is the new column value
	 * @param length the length of the stream
	 * @exception SQLException if the columnIndex is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateBinaryStream(int columnIndex, InputStream is, long length) throws SQLException {
		rs.updateBinaryStream(columnIndex, is, length);

	}

	/**
	 * Updates the designated column with a binary stream value, which will have
	 * the specified number of bytes.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param is the new column value
	 * @param length the length of the stream
	 * @exception SQLException if the columnLabel is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateBinaryStream(String columnLabel, InputStream is,
			long length) throws SQLException {
		rs.updateBinaryStream(columnLabel, is, length);

	}

	/**
	 * Updates the designated column using the given input stream. The data will
	 * be read from the stream as needed until end-of-stream is reached.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of <code>updateBlob</code> which
	 * takes a length parameter.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param inputStream An object that contains the data to set the parameter
	 *        value to.
	 * @exception SQLException if the columnIndex is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		rs.updateBlob(columnIndex, inputStream);

	}

	/**
	 * Updates the designated column using the given input stream. The data will
	 * be read from the stream as needed until end-of-stream is reached.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of <code>updateBlob</code> which
	 * takes a length parameter.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param inputStream An object that contains the data to set the parameter
	 *        value to.
	 * @exception SQLException if the columnLabel is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		rs.updateBlob(columnLabel, inputStream);
	}

	/**
	 * Updates the designated column using the given input stream, which will
	 * have the specified number of bytes.
	 *
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param inputStream An object that contains the data to set the parameter
	 *        value to.
	 * @param length the number of bytes in the parameter data.
	 * @exception SQLException if the columnIndex is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		rs.updateBlob(columnIndex, inputStream, length);

	}

	/**
	 * Updates the designated column using the given input stream, which will
	 * have the specified number of bytes.
	 *
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param inputStream An object that contains the data to set the parameter
	 *        value to.
	 * @param length the number of bytes in the parameter data.
	 * @exception SQLException if the columnLabel is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateBlob(String columnLabel, InputStream inputStream,
			long length) throws SQLException {
		rs.updateBlob(columnLabel, inputStream, length);

	}

	/**
	 * Updates the designated column with a character stream value. The data
	 * will be read from the stream as needed until end-of-stream is reached.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of
	 * <code>updateCharacterStream</code> which takes a length parameter.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param reader the new column value
	 * @exception SQLException if the columnIndex is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateCharacterStream(int columnIndex, Reader reader) throws SQLException {
		rs.updateCharacterStream(columnIndex, reader);

	}

	/**
	 * Updates the designated column with a character stream value. The data
	 * will be read from the stream as needed until end-of-stream is reached.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of
	 * <code>updateCharacterStream</code> which takes a length parameter.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param reader the <code>java.io.Reader</code> object containing the new
	 *        column value
	 * @exception SQLException if the columnLabel is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		rs.updateCharacterStream(columnLabel, reader);

	}

	/**
	 * Updates the designated column with a character stream value, which will
	 * have the specified number of bytes.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param reader the new column value
	 * @param length the length of the stream
	 * @exception SQLException if the columnIndex is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateCharacterStream(int columnIndex, Reader reader,
			long length) throws SQLException {
		rs.updateCharacterStream(columnIndex, reader, length);

	}

	/**
	 * Updates the designated column with a character stream value, which will
	 * have the specified number of bytes.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param reader the <code>java.io.Reader</code> object containing the new
	 *        column value
	 * @param length the length of the stream
	 * @exception SQLException if the columnLabel is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		rs.updateCharacterStream(columnLabel, reader, length);

	}

	/**
	 * Updates the designated column using the given <code>Reader</code> object.
	 * The data will be read from the stream as needed until end-of-stream is
	 * reached. The JDBC driver will do any necessary conversion from UNICODE to
	 * the database char format.
	 *
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of <code>updateClob</code> which
	 * takes a length parameter.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param reader An object that contains the data to set the parameter value
	 *        to.
	 * @exception SQLException if the columnIndex is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		rs.updateClob(columnIndex, reader);

	}

	/**
	 * Updates the designated column using the given <code>Reader</code> object.
	 * The data will be read from the stream as needed until end-of-stream is
	 * reached. The JDBC driver will do any necessary conversion from UNICODE to
	 * the database char format.
	 *
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of <code>updateClob</code> which
	 * takes a length parameter.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param reader An object that contains the data to set the parameter value
	 *        to.
	 * @exception SQLException if the columnLabel is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		rs.updateClob(columnLabel, reader);

	}

	/**
	 * Updates the designated column using the given <code>Reader</code> object,
	 * which is the given number of characters long. When a very large UNICODE
	 * value is input to a <code>LONGVARCHAR</code> parameter, it may be more
	 * practical to send it via a <code>java.io.Reader</code> object. The JDBC
	 * driver will do any necessary conversion from UNICODE to the database char
	 * format.
	 *
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param reader An object that contains the data to set the parameter value
	 *        to.
	 * @param length the number of characters in the parameter data.
	 * @exception SQLException if the columnIndex is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		rs.updateClob(columnIndex, reader, length);
	}

	/**
	 * Updates the designated column using the given <code>Reader</code> object,
	 * which is the given number of characters long. When a very large UNICODE
	 * value is input to a <code>LONGVARCHAR</code> parameter, it may be more
	 * practical to send it via a <code>java.io.Reader</code> object. The JDBC
	 * driver will do any necessary conversion from UNICODE to the database char
	 * format.
	 *
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param reader An object that contains the data to set the parameter value
	 *        to.
	 * @param length the number of characters in the parameter data.
	 * @exception SQLException if the columnLabel is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		rs.updateClob(columnLabel, reader, length);

	}

	/**
	 * Updates the designated column with a character stream value. The data
	 * will be read from the stream as needed until end-of-stream is reached.
	 * The driver does the necessary conversion from Java character format to
	 * the national character set in the database. It is intended for use when
	 * updating <code>NCHAR</code>,<code>NVARCHAR</code> and
	 * <code>LONGNVARCHAR</code> columns.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of
	 * <code>updateNCharacterStream</code> which takes a length parameter.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param reader the new column value
	 * @exception SQLException if the columnIndex is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateNCharacterStream(int columnIndex, Reader reader) throws SQLException {
		rs.updateNCharacterStream(columnIndex, reader);

	}

	/**
	 * Updates the designated column with a character stream value. The data
	 * will be read from the stream as needed until end-of-stream is reached.
	 * The driver does the necessary conversion from Java character format to
	 * the national character set in the database. It is intended for use when
	 * updating <code>NCHAR</code>,<code>NVARCHAR</code> and
	 * <code>LONGNVARCHAR</code> columns.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of
	 * <code>updateNCharacterStream</code> which takes a length parameter.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param reader the <code>java.io.Reader</code> object containing the new
	 *        column value
	 * @exception SQLException if the columnLabel is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		rs.updateNCharacterStream(columnLabel, reader);

	}

	/**
	 * Updates the designated column with a character stream value, which will
	 * have the specified number of bytes. The driver does the necessary
	 * conversion from Java character format to the national character set in
	 * the database. It is intended for use when updating <code>NCHAR</code>,
	 * <code>NVARCHAR</code> and <code>LONGNVARCHAR</code> columns.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @param reader the new column value
	 * @param length the length of the stream
	 * @exception SQLException if the columnIndex is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateNCharacterStream(int columnIndex, Reader reader,
			long length) throws SQLException {
		rs.updateNCharacterStream(columnIndex, reader, length);

	}

	/**
	 * Updates the designated column with a character stream value, which will
	 * have the specified number of bytes. The driver does the necessary
	 * conversion from Java character format to the national character set in
	 * the database. It is intended for use when updating <code>NCHAR</code>,
	 * <code>NVARCHAR</code> and <code>LONGNVARCHAR</code> columns.
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param reader the <code>java.io.Reader</code> object containing the new
	 *        column value
	 * @param length the length of the stream
	 * @exception SQLException if the columnLabel is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateNCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		rs.updateNCharacterStream(columnLabel, reader, length);

	}

	/**
	 * Updates the designated column with a <code>java.sql.NClob</code> value.
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnIndex the first column is 1, the second 2, ...
	 * @param nClob the value for the column to be updated
	 * @throws SQLException if the columnIndex is not valid; if the driver does
	 *         not support national character sets; if the driver can detect
	 *         that a data conversion error could occur; this method is called
	 *         on a closed result set; if a database access error occurs or the
	 *         result set concurrency is <code>CONCUR_READ_ONLY</code>
	 * @since 1.6
	 */
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		rs.updateNClob(columnIndex, nClob);

	}

	/**
	 * Updates the designated column with a <code>java.sql.NClob</code> value.
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param nClob the value for the column to be updated
	 * @throws SQLException if the columnLabel is not valid; if the driver does
	 *         not support national character sets; if the driver can detect
	 *         that a data conversion error could occur; this method is called
	 *         on a closed result set; if a database access error occurs or the
	 *         result set concurrency is <code>CONCUR_READ_ONLY</code>
	 * @since 1.6
	 */
	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		rs.updateNClob(columnLabel, nClob);

	}

	/**
	 * Updates the designated column using the given <code>Reader</code>
	 *
	 * The data will be read from the stream as needed until end-of-stream is
	 * reached. The JDBC driver will do any necessary conversion from UNICODE to
	 * the database char format.
	 *
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of <code>updateNClob</code>
	 * which takes a length parameter.
	 *
	 * @param columnIndex the first column is 1, the second 2, ...
	 * @param reader An object that contains the data to set the parameter value
	 *        to.
	 * @throws SQLException if the columnIndex is not valid; if the driver does
	 *         not support national character sets; if the driver can detect
	 *         that a data conversion error could occur; this method is called
	 *         on a closed result set, if a database access error occurs or the
	 *         result set concurrency is <code>CONCUR_READ_ONLY</code>
	 * @since 1.6
	 */
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		rs.updateNClob(columnIndex, reader);

	}

	/**
	 * Updates the designated column using the given <code>Reader</code> object.
	 * The data will be read from the stream as needed until end-of-stream is
	 * reached. The JDBC driver will do any necessary conversion from UNICODE to
	 * the database char format.
	 *
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of <code>updateNClob</code>
	 * which takes a length parameter.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param reader An object that contains the data to set the parameter value
	 *        to.
	 * @throws SQLException if the columnLabel is not valid; if the driver does
	 *         not support national character sets; if the driver can detect
	 *         that a data conversion error could occur; this method is called
	 *         on a closed result set; if a database access error occurs or the
	 *         result set concurrency is <code>CONCUR_READ_ONLY</code>
	 * @since 1.6
	 */
	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		rs.updateNClob(columnLabel, reader);

	}

	/**
	 * Updates the designated column using the given <code>Reader</code> object,
	 * which is the given number of characters long. When a very large UNICODE
	 * value is input to a <code>LONGVARCHAR</code> parameter, it may be more
	 * practical to send it via a <code>java.io.Reader</code> object. The JDBC
	 * driver will do any necessary conversion from UNICODE to the database char
	 * format.
	 *
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnIndex the first column is 1, the second 2, ...
	 * @param reader An object that contains the data to set the parameter value
	 *        to.
	 * @param length the number of characters in the parameter data.
	 * @throws SQLException if the columnIndex is not valid; if the driver does
	 *         not support national character sets; if the driver can detect
	 *         that a data conversion error could occur; this method is called
	 *         on a closed result set, if a database access error occurs or the
	 *         result set concurrency is <code>CONCUR_READ_ONLY</code>
	 * @since 1.6
	 */
	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		rs.updateNClob(columnIndex, reader, length);

	}

	/**
	 * Updates the designated column using the given <code>Reader</code> object,
	 * which is the given number of characters long. When a very large UNICODE
	 * value is input to a <code>LONGVARCHAR</code> parameter, it may be more
	 * practical to send it via a <code>java.io.Reader</code> object. The JDBC
	 * driver will do any necessary conversion from UNICODE to the database char
	 * format.
	 *
	 * <p>
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param reader An object that contains the data to set the parameter value
	 *        to.
	 * @param length the number of characters in the parameter data.
	 * @throws SQLException if the columnLabel is not valid; if the driver does
	 *         not support national character sets; if the driver can detect
	 *         that a data conversion error could occur; this method is called
	 *         on a closed result set; if a database access error occurs or the
	 *         result set concurrency is <code>CONCUR_READ_ONLY</code>
	 * @since 1.6
	 */
	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		rs.updateNClob(columnLabel, reader, length);

	}

	/**
	 * Updates the designated column with a <code>String</code> value. It is
	 * intended for use when updating <code>NCHAR</code>,<code>NVARCHAR</code>
	 * and <code>LONGNVARCHAR</code> columns. The updater methods are used to
	 * update column values in the current row or the insert row. The updater
	 * methods do not update the underlying database; instead the
	 * <code>updateRow</code> or <code>insertRow</code> methods are called to
	 * update the database.
	 *
	 * @param columnIndex the first column is 1, the second 2, ...
	 * @param nString the value for the column to be updated
	 * @throws SQLException if the columnIndex is not valid; if the driver does
	 *         not support national character sets; if the driver can detect
	 *         that a data conversion error could occur; this method is called
	 *         on a closed result set; the result set concurrency is
	 *         <code>CONCUR_READ_ONLY</code> or if a database access error
	 *         occurs
	 * @since 1.6
	 */
	public void updateNString(int columnIndex, String nString) throws SQLException {
		rs.updateNString(columnIndex, nString);

	}

	/**
	 * Updates the designated column with a <code>String</code> value. It is
	 * intended for use when updating <code>NCHAR</code>,<code>NVARCHAR</code>
	 * and <code>LONGNVARCHAR</code> columns. The updater methods are used to
	 * update column values in the current row or the insert row. The updater
	 * methods do not update the underlying database; instead the
	 * <code>updateRow</code> or <code>insertRow</code> methods are called to
	 * update the database.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param nString the value for the column to be updated
	 * @throws SQLException if the columnLabel is not valid; if the driver does
	 *         not support national character sets; if the driver can detect
	 *         that a data conversion error could occur; this method is called
	 *         on a closed result set; the result set concurrency is
	 *         <CODE>CONCUR_READ_ONLY</code> or if a database access error
	 *         occurs
	 * @since 1.6
	 */
	public void updateNString(String columnLabel, String nString) throws SQLException {
		rs.updateNString(columnLabel, nString);

	}

	/**
	 * Updates the designated column with a <code>RowId</code> value. The
	 * updater methods are used to update column values in the current row or
	 * the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnIndex the first column is 1, the second 2, ...
	 * @param rowId the column value
	 * @exception SQLException if the columnIndex is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateRowId(int columnIndex, RowId rowId) throws SQLException {
		rs.updateRowId(columnIndex, rowId);

	}

	/**
	 * Updates the designated column with a <code>RowId</code> value. The
	 * updater methods are used to update column values in the current row or
	 * the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param rowId the column value
	 * @exception SQLException if the columnLabel is not valid; if a database
	 *            access error occurs; the result set concurrency is
	 *            <code>CONCUR_READ_ONLY</code> or this method is called on a
	 *            closed result set
	 * @since 1.6
	 */
	public void updateRowId(String columnLabel, RowId rowId) throws SQLException {
		rs.updateRowId(columnLabel, rowId);

	}

	/**
	 * Updates the designated column with a <code>java.sql.SQLXML</code> value.
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 * <p>
	 *
	 * @param columnIndex the first column is 1, the second 2, ...
	 * @param xmlObject the value for the column to be updated
	 * @throws SQLException if the columnIndex is not valid; if a database
	 *         access error occurs; this method is called on a closed result
	 *         set; the <code>java.xml.transform.Result</code>,
	 *         <code>Writer</code> or <code>OutputStream</code> has not been
	 *         closed for the <code>SQLXML</code> object; if there is an error
	 *         processing the XML value or the result set concurrency is
	 *         <code>CONCUR_READ_ONLY</code>. The <code>getCause</code> method
	 *         of the exception may provide a more detailed exception, for
	 *         example, if the stream does not contain valid XML.
	 * @since 1.6
	 */
	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		rs.updateSQLXML(columnIndex, xmlObject);

	}

	/**
	 * Updates the designated column with a <code>java.sql.SQLXML</code> value.
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 * <p>
	 *
	 * @param columnLabel the label for the column specified with the SQL AS
	 *        clause. If the SQL AS clause was not specified, then the label is
	 *        the name of the column
	 * @param xmlObject the column value
	 * @throws SQLException if the columnLabel is not valid; if a database
	 *         access error occurs; this method is called on a closed result
	 *         set; the <code>java.xml.transform.Result</code>,
	 *         <code>Writer</code> or <code>OutputStream</code> has not been
	 *         closed for the <code>SQLXML</code> object; if there is an error
	 *         processing the XML value or the result set concurrency is
	 *         <code>CONCUR_READ_ONLY</code>. The <code>getCause</code> method
	 *         of the exception may provide a more detailed exception, for
	 *         example, if the stream does not contain valid XML.
	 * @since 1.6
	 */
	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		rs.updateSQLXML(columnLabel, xmlObject);

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
		return rs.isWrapperFor(iface);
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
	 * @param iface &lt;T&gt; A Class defining an interface that the result must
	 *        implement.
	 * @param <T> Class
	 * @return an object that implements the interface. May be a proxy for the
	 *         actual implementing object.
	 * @throws java.sql.SQLException If no object found that implements the
	 *         interface
	 * @since 1.6
	 */
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return rs.unwrap(iface);
	}

	//------------------------- JDBC 4.1 -----------------------------------

    /**
     *<p>Retrieves the value of the designated column in the current row
     * of this <code>ResultSet</code> object and will convert from the
     * SQL type of the column to the requested Java data type, if the
     * conversion is supported. If the conversion is not
     * supported  or null is specified for the type, a
     * <code>SQLException</code> is thrown.
     *<p>
     * At a minimum, an implementation must support the conversions defined in
     * Appendix B, Table B-3 and conversion of appropriate user defined SQL
     * types to a Java type which implements {@code SQLData}, or {@code Struct}.
     * Additional conversions may be supported and are vendor defined.
     *
     * @param columnIndex the first column is 1, the second is 2, ...
     * @param type Class representing the Java data type to convert the designated
     * column to.
     * @return an instance of {@code type} holding the column value
     * @throws SQLException if conversion is not supported, type is null or
     *         another error occurs. The getCause() method of the
     * exception may provide a more detailed exception, for example, if
     * a conversion error occurs
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
     * this method
     * @since 1.7
     */
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		return rs.getObject(columnIndex, type);
	}

    /**
     *<p>Retrieves the value of the designated column in the current row
     * of this <code>ResultSet</code> object and will convert from the
     * SQL type of the column to the requested Java data type, if the
     * conversion is supported. If the conversion is not
     * supported  or null is specified for the type, a
     * <code>SQLException</code> is thrown.
     *<p>
     * At a minimum, an implementation must support the conversions defined in
     * Appendix B, Table B-3 and conversion of appropriate user defined SQL
     * types to a Java type which implements {@code SQLData}, or {@code Struct}.
     * Additional conversions may be supported and are vendor defined.
     *
     * @param columnLabel the label for the column specified with the SQL AS clause.
     * If the SQL AS clause was not specified, then the label is the name
     * of the column
     * @param type Class representing the Java data type to convert the designated
     * column to.
     * @return an instance of {@code type} holding the column value
     * @throws SQLException if conversion is not supported, type is null or
     *         another error occurs. The getCause() method of the
     * exception may provide a more detailed exception, for example, if
     * a conversion error occurs
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
     * this method
     * @since 1.7
     */
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		return rs.getObject(columnLabel, type);
	}

}
