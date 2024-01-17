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
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import org.jkiss.dbeaver.ext.turbographpp.jdbc.proxy.manage.ReflectionUtil;

/**
 * The proxy of CUBRIDPreparedStatement
 *
 * @author robinhood
 *
 */
public class CUBRIDPreparedStatementProxy extends
		CUBRIDStatementProxy implements
		PreparedStatement {

	private final PreparedStatement realStatement;

	public CUBRIDPreparedStatementProxy(PreparedStatement statement) {
		super(statement);
		realStatement = statement;
	}

	/**
	 * @see PreparedStatement#clearParameters()
	 *
	 * @exception SQLException if a database access error occurs
	 */
	public void clearParameters() throws SQLException {
		realStatement.clearParameters();
	}

	/**
	 * @see PreparedStatement#execute()
	 *
	 * @return <code>true</code> if the first result is a <code>ResultSet</code>
	 *         object; <code>false</code> if the first result is an update count
	 *         or there is no result
	 * @exception SQLException if a database access error occurs or an argument
	 *            is supplied to this method
	 */
	public boolean execute() throws SQLException {
		return realStatement.execute();
	}

	/**
	 * @see PreparedStatement#executeQuery()
	 *
	 * @return <code>ResultSet</code> object that contains the data produced by
	 *         the query; never <code>null</code>
	 * @exception SQLException if a database access error occurs or the SQL
	 *            statement does not return a <code>ResultSet</code> object
	 */
	public ResultSet executeQuery() throws SQLException {
		CUBRIDResultSetProxy resultSetProxy = new CUBRIDResultSetProxy(
				realStatement.executeQuery());
		resultSetProxy.setJdbcVersion(jdbcVersion);
		return resultSetProxy;
	}

	/**
	 * @see PreparedStatement#executeUpdate()
	 *
	 * @return either (1) the row count for <code>INSERT</code>,
	 *         <code>UPDATE</code>, or <code>DELETE</code> statements or (2) 0
	 *         for SQL statements that return nothing
	 * @exception SQLException if a database access error occurs or the SQL
	 *            statement returns a <code>ResultSet</code> object
	 */
	public int executeUpdate() throws SQLException {
		return realStatement.executeUpdate();
	}

	/**
	 * @see PreparedStatement#getMetaData()
	 *
	 * @return the description of a <code>ResultSet</code> object's columns or
	 *         <code>null</code> if the driver cannot return a
	 *         <code>ResultSetMetaData</code> object
	 * @exception SQLException if a database access error occurs
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return realStatement.getMetaData();
	}

	/**
	 * @see PreparedStatement#getParameterMetaData()
	 *
	 * @return a <code>ParameterMetaData</code> object that contains information
	 *         about the number, types and properties of this
	 *         <code>PreparedStatement</code> object's parameters
	 * @exception SQLException if a database access error occurs
	 * @see ParameterMetaData
	 */
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return realStatement.getParameterMetaData();
	}

	/**
	 * @see PreparedStatement#setArray(int, Array)
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param array an <code>Array</code> object that maps an SQL
	 *        <code>ARRAY</code> value
	 * @exception SQLException if a database access error occurs
	 */
	public void setArray(int parameterIndex, Array array) throws SQLException {
		realStatement.setArray(parameterIndex, array);
	}

	/**
	 * @see PreparedStatement#setAsciiStream(int, InputStream)
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param array the Java input stream that contains the ASCII parameter
	 *        value
	 * @exception SQLException if a database access error occurs
	 */
	public void setAsciiStream(int parameterIndex, InputStream array) throws SQLException {
		//empty
	}

	/**
	 * @see PreparedStatement#setAsciiStream(int, InputStream, int)
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param stream the Java input stream that contains the ASCII parameter
	 *        value
	 * @param length the number of bytes in the stream
	 * @exception SQLException if a database access error occurs
	 */
	public void setAsciiStream(int parameterIndex, InputStream stream,
			int length) throws SQLException {
		realStatement.setAsciiStream(parameterIndex, stream, length);
	}

	/**
	 *
	 * Set ascii stream
	 *
	 * @param parameterIndex the index
	 * @param stream the stream
	 * @param length the length
	 * @throws SQLException the SQLException
	 */
	public void setAsciiStream(int parameterIndex, InputStream stream,
			long length) throws SQLException {
		//empty
	}

	/**
	 * @see PreparedStatement#setBigDecimal(int, BigDecimal)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param decimal the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setBigDecimal(int parameterIndex, BigDecimal decimal) throws SQLException {
		realStatement.setBigDecimal(parameterIndex, decimal);
	}

	/**
	 * Set binary stream(no implemented)
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param stream the stream
	 * @exception SQLException if a database access error occurs
	 */
	public void setBinaryStream(int parameterIndex, InputStream stream) throws SQLException {
		//empty
	}

	/**
	 * @see PreparedStatement#setBinaryStream(int, InputStream, int)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param stream the java input stream which contains the binary parameter
	 *        value
	 * @param length the number of bytes in the stream
	 * @exception SQLException if a database access error occurs
	 */
	public void setBinaryStream(int parameterIndex, InputStream stream,
			int length) throws SQLException {
		realStatement.setBinaryStream(parameterIndex, stream, length);
	}

	/*	public void setBinaryStream(int parameterIndex, InputStream x, long length)
				throws SQLException {
			subStatement.setBinaryStream(parameterIndex, x, length);

		}
	*/

	/**
	 * @see PreparedStatement#setBlob(int, Blob)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param blob <code>Blob</code> object that maps an SQL <code>BLOB</code>
	 *        value
	 * @exception SQLException if a database access error occurs
	 */
	public void setBlob(int parameterIndex, Blob blob) throws SQLException {
		realStatement.setBlob(parameterIndex, blob);

	}

	/*	public void setBlob(int parameterIndex, InputStream inputStream)
				throws SQLException {
			subStatement.setBlob(parameterIndex, inputStream);

		}

		public void setBlob(int parameterIndex, InputStream inputStream, long length)
				throws SQLException {
			subStatement.setBlob(parameterIndex, inputStream, length);

		}
	*/

	/**
	 * @see PreparedStatement#setBoolean(int, boolean)
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param value the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setBoolean(int parameterIndex, boolean value) throws SQLException {
		realStatement.setBoolean(parameterIndex, value);
	}

	/**
	 * @see PreparedStatement#setByte(int, byte)
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param value the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setByte(int parameterIndex, byte value) throws SQLException {
		realStatement.setByte(parameterIndex, value);
	}

	/**
	 * @see PreparedStatement#setBytes(int, byte[])
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param values the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setBytes(int parameterIndex, byte[] values) throws SQLException {
		realStatement.setBytes(parameterIndex, values);
	}

	/*	public void setCharacterStream(int parameterIndex, Reader reader)
				throws SQLException {
			subStatement.setCharacterStream(parameterIndex, reader);

		}
	*/

	/**
	 * @see PreparedStatement#setCharacterStream(int, Reader, int)
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param reader the <code>java.io.Reader</code> object that contains the
	 *        Unicode data
	 * @param length the number of characters in the stream
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		realStatement.setCharacterStream(parameterIndex, reader, length);
	}

	/*	public void setCharacterStream(int parameterIndex, Reader reader,
				long length) throws SQLException {
			subStatement.setCharacterStream(parameterIndex, reader, length);

		}
	*/
	/**
	 * @see PreparedStatement#setClob(int, Clob)
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param clob <code>Clob</code> object that maps an SQL <code>CLOB</code>
	 *        value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void setClob(int parameterIndex, Clob clob) throws SQLException {
		realStatement.setClob(parameterIndex, clob);
	}

	/*
		public void setClob(int parameterIndex, Reader reader) throws SQLException {
			subStatement.setClob(parameterIndex, reader);

		}

		public void setClob(int parameterIndex, Reader reader, long length)
				throws SQLException {
			subStatement.setClob(parameterIndex, reader, length);

		}
	*/

	/**
	 * @see PreparedStatement#setDate(int, Date)
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param date the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setDate(int parameterIndex, Date date) throws SQLException {
		realStatement.setDate(parameterIndex, date);
	}

	/**
	 * @see PreparedStatement#setDate(int, Date, Calendar)
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param date the parameter value
	 * @param cal the <code>Calendar</code> object the driver will use to
	 *        construct the date
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void setDate(int parameterIndex, Date date, Calendar cal) throws SQLException {
		realStatement.setDate(parameterIndex, date, cal);

	}

	/**
	 * @see PreparedStatement#setDouble(int, double)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param value the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setDouble(int parameterIndex, double value) throws SQLException {
		realStatement.setDouble(parameterIndex, value);
	}

	/**
	 * @see PreparedStatement#setFloat(int, float)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param value the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setFloat(int parameterIndex, float value) throws SQLException {
		realStatement.setFloat(parameterIndex, value);
	}

	/**
	 * @see PreparedStatement#setInt(int, int)
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param value the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setInt(int parameterIndex, int value) throws SQLException {
		realStatement.setInt(parameterIndex, value);
	}

	/**
	 * @see PreparedStatement#setLong(int, long)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param value the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setLong(int parameterIndex, long value) throws SQLException {
		realStatement.setLong(parameterIndex, value);
	}

	/**
	 * @see PreparedStatement#setNull(int, int)
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param sqlType the SQL type code defined in <code>java.sql.Types</code>
	 * @exception SQLException if a database access error occurs
	 */
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		realStatement.setNull(parameterIndex, sqlType);
	}

	/**
	 * @see PreparedStatement#setNull(int, int, String)
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param sqlType a value from <code>java.sql.Types</code>
	 * @param typeName the fully-qualified name of an SQL user-defined type;
	 *        ignored if the parameter is not a user-defined type or REF
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		realStatement.setNull(parameterIndex, sqlType, typeName);
	}

	/**
	 * @see PreparedStatement#setObject(int, Object)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param obj the object containing the input parameter value
	 * @exception SQLException if a database access error occurs or the type of
	 *            the given object is ambiguous
	 */
	public void setObject(int parameterIndex, Object obj) throws SQLException {
		realStatement.setObject(parameterIndex, obj);
	}

	/**
	 * @see PreparedStatement#setObject(int, Object, int)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param obj the object containing the input parameter value
	 * @param targetSqlType the SQL type (as defined in java.sql.Types) to be
	 *        sent to the database
	 * @exception SQLException if a database access error occurs
	 */
	public void setObject(int parameterIndex, Object obj, int targetSqlType) throws SQLException {
		realStatement.setObject(parameterIndex, obj, targetSqlType);
	}

	/**
	 * @see PreparedStatement#setObject(int, Object, int, int)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param obj the object containing the input parameter value
	 * @param targetSqlType the SQL type (as defined in java.sql.Types) to be
	 *        sent to the database. The scale argument may further qualify this
	 *        type.
	 * @param scaleOrLength for java.sql.Types.DECIMAL or java.sql.Types.NUMERIC
	 *        types, this is the number of digits after the decimal point. For
	 *        all other types, this value will be ignored.
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void setObject(int parameterIndex, Object obj, int targetSqlType,
			int scaleOrLength) throws SQLException {
		realStatement.setObject(parameterIndex, obj, targetSqlType,
				scaleOrLength);
	}

	/**
	 * @see PreparedStatement#setRef(int, Ref)
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param obj an SQL <code>REF</code> value
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void setRef(int parameterIndex, Ref obj) throws SQLException {
		realStatement.setRef(parameterIndex, obj);
	}

	/**
	 * @see PreparedStatement#setShort(int, short)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param value the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setShort(int parameterIndex, short value) throws SQLException {
		realStatement.setShort(parameterIndex, value);
	}

	/**
	 * @see PreparedStatement#setString(int, String)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param value the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setString(int parameterIndex, String value) throws SQLException {
		realStatement.setString(parameterIndex, value);
	}

	/**
	 * @see PreparedStatement#setTime(int, Time)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param value the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setTime(int parameterIndex, Time value) throws SQLException {
		realStatement.setTime(parameterIndex, value);
	}

	/**
	 * @see PreparedStatement#setTime(int, Time, Calendar)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param value the parameter value
	 * @param cal the <code>Calendar</code> object the driver will use to
	 *        construct the time
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void setTime(int parameterIndex, Time value, Calendar cal) throws SQLException {
		realStatement.setTime(parameterIndex, value, cal);
	}

	/**
	 * @see PreparedStatement#setTimestamp(int, Timestamp)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param value the parameter value
	 * @exception SQLException if a database access error occurs
	 */
	public void setTimestamp(int parameterIndex, Timestamp value) throws SQLException {
		realStatement.setTimestamp(parameterIndex, value);
	}

	/**
	 * @see PreparedStatement#setTimestamp(int, Timestamp, Calendar)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param value the parameter value
	 * @param cal the <code>Calendar</code> object the driver will use to
	 *        construct the timestamp
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void setTimestamp(int parameterIndex, Timestamp value, Calendar cal) throws SQLException {
		realStatement.setTimestamp(parameterIndex, value, cal);
	}

	/**
	 * @see PreparedStatement#setURL(int, URL)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param url the <code>java.net.URL</code> object to be set
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void setURL(int parameterIndex, URL url) throws SQLException {
		realStatement.setURL(parameterIndex, url);
	}

	/**
	 * @see PreparedStatement#setUnicodeStream(int, InputStream, int)
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param stream <code>java.io.InputStream</code> object that contains the
	 *        Unicode parameter value as two-byte Unicode characters
	 * @param length the number of bytes in the stream
	 * @exception SQLException if a database access error occurs
	 * @deprecated
	 */
	public void setUnicodeStream(int parameterIndex, InputStream stream,
			int length) throws SQLException {
		realStatement.setUnicodeStream(parameterIndex, stream, length);
	}

	/**
	 * @see PreparedStatement#addBatch()
	 * @exception SQLException if a database access error occurs
	 */
	public void addBatch() throws SQLException {
		realStatement.addBatch();
	}

	/**
	 * @see PreparedStatement#clearBatch()
	 * @exception SQLException if a database access error occurs
	 */
	public void clearBatch() throws SQLException {
		realStatement.clearBatch();
	}

	/**
	 * @see PreparedStatement#close()
	 * @exception SQLException if a database access error occurs
	 */
	public void close() throws SQLException {
		realStatement.close();
	}

	/**
	 * @see PreparedStatement#executeBatch()
	 * @return an array of update counts containing one element for each command
	 *         in the batch.
	 * @exception SQLException if a database access error occurs
	 */
	public int[] executeBatch() throws SQLException {
		return realStatement.executeBatch();
	}

	/**
	 *
	 * Invoke the executeInsert method in CUBRID PreparedStatement object
	 *
	 * @return the CUBRIDOIDProxy object
	 * @throws SQLException if a database access error occurs
	 */
	public CUBRIDOIDProxy executeInsert() throws SQLException {
		CUBRIDOIDProxy proxy = new CUBRIDOIDProxy(ReflectionUtil.invoke(
				realStatement, "executeInsert"));
		proxy.setJdbcVersion(jdbcVersion);
		return proxy;
	}

	/**
	 *
	 * Invoke the hasResultSet method in CUBRID PreparedStatement object
	 *
	 * @return <code>true</code> if has resultset;<code>false</code>otherwise
	 * @throws SQLException if a database access error occurs
	 */
	public boolean hasResultSet() throws SQLException {
		return (Boolean) ReflectionUtil.invoke(realStatement, "hasResultSet");
	}

	/**
	 *
	 * Invoke the setCollection method in CUBRID PreparedStatement object
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param objs the object array
	 * @throws SQLException if a database access error occurs
	 */
	public void setCollection(int parameterIndex, Object[] objs) throws SQLException {
		ReflectionUtil.invoke(realStatement, "setCollection", new Class<?>[]{
				int.class, Object[].class },
				new Object[]{parameterIndex, objs });
	}

	/**
	 *
	 * Invoke the setOID method in CUBRID PreparedStatement object
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param proxy the CUBRIDOIDProxy obj
	 * @throws SQLException if a database access error occurs
	 */
	public void setOID(int parameterIndex, CUBRIDOIDProxy proxy) throws SQLException {
		ReflectionUtil.invoke(realStatement, "setOID", new Class<?>[]{
				int.class, proxy.getCUBRIDOIDClass() }, new Object[]{
				parameterIndex, proxy.getProxyObject() });

	}

	/**
	 * Sets the designated parameter to the given input stream, which will have
	 * the specified number of bytes. When a very large binary value is input to
	 * a <code>LONGVARBINARY</code> parameter, it may be more practical to send
	 * it via a <code>java.io.InputStream</code> object. The data will be read
	 * from the stream as needed until end-of-file is reached.
	 *
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param is the java input stream which contains the binary parameter value
	 * @param length the number of bytes in the stream
	 * @exception SQLException if parameterIndex does not correspond to a
	 *            parameter marker in the SQL statement; if a database access
	 *            error occurs or this method is called on a closed
	 *            <code>PreparedStatement</code>
	 * @since 1.6
	 */
	public void setBinaryStream(int parameterIndex, InputStream is, long length) throws SQLException {
		realStatement.setBinaryStream(parameterIndex, is, length);

	}

	/**
	 * Sets the designated parameter to a <code>InputStream</code> object. This
	 * method differs from the <code>setBinaryStream (int, InputStream)</code>
	 * method because it informs the driver that the parameter value should be
	 * sent to the server as a <code>BLOB</code>. When the
	 * <code>setBinaryStream</code> method is used, the driver may have to do
	 * extra work to determine whether the parameter data should be sent to the
	 * server as a <code>LONGVARBINARY</code> or a <code>BLOB</code>
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of <code>setBlob</code> which
	 * takes a length parameter.
	 *
	 * @param parameterIndex index of the first parameter is 1, the second is 2,
	 *        ...
	 * @param inputStream An object that contains the data to set the parameter
	 *        value to.
	 * @throws SQLException if parameterIndex does not correspond to a parameter
	 *         marker in the SQL statement; if a database access error occurs;
	 *         this method is called on a closed <code>PreparedStatement</code>
	 *         or if parameterIndex does not correspond to a parameter marker in
	 *         the SQL statement,
	 *
	 * @since 1.6
	 */
	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		realStatement.setBlob(parameterIndex, inputStream);

	}

	/**
	 * Sets the designated parameter to a <code>InputStream</code> object. The
	 * inputstream must contain the number of characters specified by length
	 * otherwise a <code>SQLException</code> will be generated when the
	 * <code>PreparedStatement</code> is executed. This method differs from the
	 * <code>setBinaryStream (int, InputStream, int)</code> method because it
	 * informs the driver that the parameter value should be sent to the server
	 * as a <code>BLOB</code>. When the <code>setBinaryStream</code> method is
	 * used, the driver may have to do extra work to determine whether the
	 * parameter data should be sent to the server as a
	 * <code>LONGVARBINARY</code> or a <code>BLOB</code>
	 *
	 * @param parameterIndex index of the first parameter is 1, the second is 2,
	 *        ...
	 * @param inputStream An object that contains the data to set the parameter
	 *        value to.
	 * @param length the number of bytes in the parameter data.
	 * @throws SQLException if parameterIndex does not correspond to a parameter
	 *         marker in the SQL statement; if a database access error occurs;
	 *         this method is called on a closed <code>PreparedStatement</code>;
	 *         if the length specified is less than zero or if the number of
	 *         bytes in the inputstream does not match the specfied length.
	 *
	 * @since 1.6
	 */
	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		realStatement.setBlob(parameterIndex, inputStream, length);

	}

	/**
	 * Sets the designated parameter to the given <code>Reader</code> object.
	 * When a very large UNICODE value is input to a <code>LONGVARCHAR</code>
	 * parameter, it may be more practical to send it via a
	 * <code>java.io.Reader</code> object. The data will be read from the stream
	 * as needed until end-of-file is reached. The JDBC driver will do any
	 * necessary conversion from UNICODE to the database char format.
	 *
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of
	 * <code>setCharacterStream</code> which takes a length parameter.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param reader the <code>java.io.Reader</code> object that contains the
	 *        Unicode data
	 * @exception SQLException if parameterIndex does not correspond to a
	 *            parameter marker in the SQL statement; if a database access
	 *            error occurs or this method is called on a closed
	 *            <code>PreparedStatement</code>
	 * @since 1.6
	 */
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		realStatement.setCharacterStream(parameterIndex, reader);

	}

	/**
	 * Sets the designated parameter to the given <code>Reader</code> object,
	 * which is the given number of characters long. When a very large UNICODE
	 * value is input to a <code>LONGVARCHAR</code> parameter, it may be more
	 * practical to send it via a <code>java.io.Reader</code> object. The data
	 * will be read from the stream as needed until end-of-file is reached. The
	 * JDBC driver will do any necessary conversion from UNICODE to the database
	 * char format.
	 *
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param reader the <code>java.io.Reader</code> object that contains the
	 *        Unicode data
	 * @param length the number of characters in the stream
	 * @exception SQLException if parameterIndex does not correspond to a
	 *            parameter marker in the SQL statement; if a database access
	 *            error occurs or this method is called on a closed
	 *            <code>PreparedStatement</code>
	 * @since 1.6
	 */
	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException {
		realStatement.setCharacterStream(parameterIndex, reader, length);
	}

	/**
	 * Sets the designated parameter to a <code>Reader</code> object. This
	 * method differs from the <code>setCharacterStream (int, Reader)</code>
	 * method because it informs the driver that the parameter value should be
	 * sent to the server as a <code>CLOB</code>. When the
	 * <code>setCharacterStream</code> method is used, the driver may have to do
	 * extra work to determine whether the parameter data should be sent to the
	 * server as a <code>LONGVARCHAR</code> or a <code>CLOB</code>
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of <code>setClob</code> which
	 * takes a length parameter.
	 *
	 * @param parameterIndex index of the first parameter is 1, the second is 2,
	 *        ...
	 * @param reader An object that contains the data to set the parameter value
	 *        to.
	 * @throws SQLException if parameterIndex does not correspond to a parameter
	 *         marker in the SQL statement; if a database access error occurs;
	 *         this method is called on a closed <code>PreparedStatement</code>
	 *         or if parameterIndex does not correspond to a parameter marker in
	 *         the SQL statement
	 *
	 * @since 1.6
	 */
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		realStatement.setClob(parameterIndex, reader);
	}

	/**
	 * Sets the designated parameter to a <code>Reader</code> object. The reader
	 * must contain the number of characters specified by length otherwise a
	 * <code>SQLException</code> will be generated when the
	 * <code>PreparedStatement</code> is executed. This method differs from the
	 * <code>setCharacterStream (int, Reader, int)</code> method because it
	 * informs the driver that the parameter value should be sent to the server
	 * as a <code>CLOB</code>. When the <code>setCharacterStream</code> method
	 * is used, the driver may have to do extra work to determine whether the
	 * parameter data should be sent to the server as a <code>LONGVARCHAR</code>
	 * or a <code>CLOB</code>
	 *
	 * @param parameterIndex index of the first parameter is 1, the second is 2,
	 *        ...
	 * @param reader An object that contains the data to set the parameter value
	 *        to.
	 * @param length the number of characters in the parameter data.
	 * @throws SQLException if parameterIndex does not correspond to a parameter
	 *         marker in the SQL statement; if a database access error occurs;
	 *         this method is called on a closed <code>PreparedStatement</code>
	 *         or if the length specified is less than zero.
	 *
	 * @since 1.6
	 */
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		realStatement.setClob(parameterIndex, reader, length);
	}

	/**
	 * Sets the designated parameter to a <code>Reader</code> object. The
	 * <code>Reader</code> reads the data till end-of-file is reached. The
	 * driver does the necessary conversion from Java character format to the
	 * national character set in the database.
	 *
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of
	 * <code>setNCharacterStream</code> which takes a length parameter.
	 *
	 * @param parameterIndex of the first parameter is 1, the second is 2, ...
	 * @param value the parameter value
	 * @throws SQLException if parameterIndex does not correspond to a parameter
	 *         marker in the SQL statement; if the driver does not support
	 *         national character sets; if the driver can detect that a data
	 *         conversion error could occur; if a database access error occurs;
	 *         or this method is called on a closed
	 *         <code>PreparedStatement</code>
	 * @since 1.6
	 */
	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		realStatement.setNCharacterStream(parameterIndex, value);
	}

	/**
	 * Sets the designated parameter to a <code>Reader</code> object. The
	 * <code>Reader</code> reads the data till end-of-file is reached. The
	 * driver does the necessary conversion from Java character format to the
	 * national character set in the database.
	 *
	 * @param parameterIndex of the first parameter is 1, the second is 2, ...
	 * @param value the parameter value
	 * @param length the number of characters in the parameter data.
	 * @throws SQLException if parameterIndex does not correspond to a parameter
	 *         marker in the SQL statement; if the driver does not support
	 *         national character sets; if the driver can detect that a data
	 *         conversion error could occur; if a database access error occurs;
	 *         or this method is called on a closed
	 *         <code>PreparedStatement</code>
	 * @since 1.6
	 */
	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		realStatement.setNCharacterStream(parameterIndex, value, length);
	}

	/**
	 * Sets the designated parameter to a <code>java.sql.NClob</code> object.
	 * The driver converts this to a SQL <code>NCLOB</code> value when it sends
	 * it to the database.
	 *
	 * @param parameterIndex of the first parameter is 1, the second is 2, ...
	 * @param value the parameter value
	 * @throws SQLException if parameterIndex does not correspond to a parameter
	 *         marker in the SQL statement; if the driver does not support
	 *         national character sets; if the driver can detect that a data
	 *         conversion error could occur; if a database access error occurs;
	 *         or this method is called on a closed
	 *         <code>PreparedStatement</code>
	 * @since 1.6
	 */
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		realStatement.setNClob(parameterIndex, value);

	}

	/**
	 * Sets the designated parameter to a <code>Reader</code> object. This
	 * method differs from the <code>setCharacterStream (int, Reader)</code>
	 * method because it informs the driver that the parameter value should be
	 * sent to the server as a <code>NCLOB</code>. When the
	 * <code>setCharacterStream</code> method is used, the driver may have to do
	 * extra work to determine whether the parameter data should be sent to the
	 * server as a <code>LONGNVARCHAR</code> or a <code>NCLOB</code>
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of <code>setNClob</code> which
	 * takes a length parameter.
	 *
	 * @param parameterIndex index of the first parameter is 1, the second is 2,
	 *        ...
	 * @param reader An object that contains the data to set the parameter value
	 *        to.
	 * @throws SQLException if parameterIndex does not correspond to a parameter
	 *         marker in the SQL statement; if the driver does not support
	 *         national character sets; if the driver can detect that a data
	 *         conversion error could occur; if a database access error occurs
	 *         or this method is called on a closed
	 *         <code>PreparedStatement</code>
	 *
	 * @since 1.6
	 */
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		realStatement.setNClob(parameterIndex, reader);
	}

	/**
	 * Sets the designated parameter to a <code>Reader</code> object. The reader
	 * must contain the number of characters specified by length otherwise a
	 * <code>SQLException</code> will be generated when the
	 * <code>PreparedStatement</code> is executed. This method differs from the
	 * <code>setCharacterStream (int, Reader, int)</code> method because it
	 * informs the driver that the parameter value should be sent to the server
	 * as a <code>NCLOB</code>. When the <code>setCharacterStream</code> method
	 * is used, the driver may have to do extra work to determine whether the
	 * parameter data should be sent to the server as a
	 * <code>LONGNVARCHAR</code> or a <code>NCLOB</code>
	 *
	 * @param parameterIndex index of the first parameter is 1, the second is 2,
	 *        ...
	 * @param reader An object that contains the data to set the parameter value
	 *        to.
	 * @param length the number of characters in the parameter data.
	 * @throws SQLException if parameterIndex does not correspond to a parameter
	 *         marker in the SQL statement; if the length specified is less than
	 *         zero; if the driver does not support national character sets; if
	 *         the driver can detect that a data conversion error could occur;
	 *         if a database access error occurs or this method is called on a
	 *         closed <code>PreparedStatement</code>
	 *
	 * @since 1.6
	 */
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		realStatement.setNClob(parameterIndex, reader, length);
	}

	/**
	 * Sets the designated paramter to the given <code>String</code> object. The
	 * driver converts this to a SQL <code>NCHAR</code> or <code>NVARCHAR</code>
	 * or <code>LONGNVARCHAR</code> value (depending on the argument's size
	 * relative to the driver's limits on <code>NVARCHAR</code> values) when it
	 * sends it to the database.
	 *
	 * @param parameterIndex of the first parameter is 1, the second is 2, ...
	 * @param value the parameter value
	 * @throws SQLException if parameterIndex does not correspond to a parameter
	 *         marker in the SQL statement; if the driver does not support
	 *         national character sets; if the driver can detect that a data
	 *         conversion error could occur; if a database access error occurs;
	 *         or this method is called on a closed
	 *         <code>PreparedStatement</code>
	 * @since 1.6
	 */
	public void setNString(int parameterIndex, String value) throws SQLException {
		realStatement.setNString(parameterIndex, value);
	}

	/**
	 * Sets the designated parameter to the given <code>java.sql.RowId</code>
	 * object. The driver converts this to a SQL <code>ROWID</code> value when
	 * it sends it to the database
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param rowId the parameter value
	 * @throws SQLException if parameterIndex does not correspond to a parameter
	 *         marker in the SQL statement; if a database access error occurs or
	 *         this method is called on a closed <code>PreparedStatement</code>
	 *
	 * @since 1.6
	 */
	public void setRowId(int parameterIndex, RowId rowId) throws SQLException {
		realStatement.setRowId(parameterIndex, rowId);
	}

	/**
	 * Sets the designated parameter to the given <code>java.sql.SQLXML</code>
	 * object. The driver converts this to an SQL <code>XML</code> value when it
	 * sends it to the database.
	 * <p>
	 *
	 * @param parameterIndex index of the first parameter is 1, the second is 2,
	 *        ...
	 * @param xmlObject a <code>SQLXML</code> object that maps an SQL
	 *        <code>XML</code> value
	 * @throws SQLException if parameterIndex does not correspond to a parameter
	 *         marker in the SQL statement; if a database access error occurs;
	 *         this method is called on a closed <code>PreparedStatement</code>
	 *         or the <code>java.xml.transform.Result</code>,
	 *         <code>Writer</code> or <code>OutputStream</code> has not been
	 *         closed for the <code>SQLXML</code> object
	 *
	 * @since 1.6
	 */
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		realStatement.setSQLXML(parameterIndex, xmlObject);
	}

	/**
	 * Retrieves whether this <code>Statement</code> object has been closed. A
	 * <code>Statement</code> is closed if the method close has been called on
	 * it, or if it is automatically closed.
	 *
	 * @return true if this <code>Statement</code> object is closed; false if it
	 *         is still open
	 * @throws SQLException if a database access error occurs
	 * @since 1.6
	 */
	public boolean isClosed() throws SQLException {
		return realStatement.isClosed();
	}

	/**
	 * Returns a value indicating whether the <code>Statement</code> is poolable
	 * or not.
	 * <p>
	 *
	 * @return <code>true</code> if the <code>Statement</code> is poolable;
	 *         <code>false</code> otherwise
	 *         <p>
	 * @throws SQLException if this method is called on a closed
	 *         <code>Statement</code>
	 *         <p>
	 * @since 1.6
	 *        <p>
	 * @see java.sql.Statement#setPoolable(boolean) setPoolable(boolean)
	 */
	public boolean isPoolable() throws SQLException {
		return realStatement.isPoolable();
	}

	/**
	 * Requests that a <code>Statement</code> be pooled or not pooled. The value
	 * specified is a hint to the statement pool implementation indicating
	 * whether the applicaiton wants the statement to be pooled. It is up to the
	 * statement pool manager as to whether the hint is used.
	 * <p>
	 * The poolable value of a statement is applicable to both internal
	 * statement caches implemented by the driver and external statement caches
	 * implemented by application servers and other applications.
	 * <p>
	 * By default, a <code>Statement</code> is not poolable when created, and a
	 * <code>PreparedStatement</code> and <code>CallableStatement</code> are
	 * poolable when created.
	 * <p>
	 *
	 * @param poolable requests that the statement be pooled if true and that
	 *        the statement not be pooled if false
	 *        <p>
	 * @throws SQLException if this method is called on a closed
	 *         <code>Statement</code>
	 *         <p>
	 * @since 1.6
	 */
	public void setPoolable(boolean poolable) throws SQLException {
		realStatement.setPoolable(poolable);
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
		return realStatement.isWrapperFor(iface);
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
	public <T> T unwrap(java.lang.Class<T> iface) throws SQLException {
		return realStatement.unwrap(iface);
	}
}
