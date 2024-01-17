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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import org.jkiss.dbeaver.ext.turbographpp.jdbc.proxy.manage.ReflectionUtil;

/**
 * The proxy of CUBRIDStatement
 *
 * @author robinhood
 *
 */
public class CUBRIDStatementProxy implements
		Statement {
	protected Statement statement;
	protected String jdbcVersion;

	public CUBRIDStatementProxy(Statement statement) {
		this.statement = statement;
	}

	/**
	 * @see Statement#addBatch(String)
	 * @param sql typically this is a static SQL <code>INSERT</code> or
	 *        <code>UPDATE</code> statement
	 * @exception SQLException if a database access error occurs, or the driver
	 *            does not support batch updates
	 *
	 */
	public void addBatch(String sql) throws SQLException {
		statement.addBatch(sql);
	}

	/**
	 * @see Statement#cancel()
	 * @exception SQLException if a database access error occurs
	 */
	public void cancel() throws SQLException {
		statement.cancel();

	}

	/**
	 * @see Statement#clearBatch()
	 * @exception SQLException if a database access error occurs or the driver
	 *            does not support batch updates
	 */
	public void clearBatch() throws SQLException {
		statement.clearBatch();
	}

	/**
	 * @see Statement#clearWarnings()
	 * @exception SQLException if a database access error occurs
	 */
	public void clearWarnings() throws SQLException {
		statement.clearWarnings();
	}

	/**
	 * @see Statement#close()
	 * @exception SQLException if a database access error occurs
	 */
	public void close() throws SQLException {
		statement.close();
	}

	/**
	 * @see Statement#execute(String)
	 * @param sql any SQL statement
	 * @return <code>true</code> if the first result is a <code>ResultSet</code>
	 *         object; <code>false</code> if it is an update count or there are
	 *         no results
	 * @exception SQLException if a database access error occurs
	 */
	public boolean execute(String sql) throws SQLException {
		return statement.execute(sql);
	}

	/**
	 * @see Statement#execute(String,int)
	 * @param sql any SQL statement
	 * @param autoGeneratedKeys a constant indicating whether auto-generated
	 *        keys should be made available for retrieval using the method
	 *        <code>getGeneratedKeys</code>; one of the following constants:
	 *        <code>Statement.RETURN_GENERATED_KEYS</code> or
	 *        <code>Statement.NO_GENERATED_KEYS</code>
	 * @return <code>true</code> if the first result is a <code>ResultSet</code>
	 *         object; <code>false</code> if it is an update count or there are
	 *         no results
	 * @exception SQLException if a database access error occurs or the second
	 *            parameter supplied to this method is not
	 *            <code>Statement.RETURN_GENERATED_KEYS</code> or
	 *            <code>Statement.NO_GENERATED_KEYS</code>.
	 */
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		return statement.execute(sql, autoGeneratedKeys);
	}

	/**
	 * @see Statement#execute(String, int[])
	 * @param sql any SQL statement
	 * @param columnIndexes an array of the indexes of the columns in the
	 *        inserted row that should be made available for retrieval by a call
	 *        to the method <code>getGeneratedKeys</code>
	 * @return <code>true</code> if the first result is a <code>ResultSet</code>
	 *         object; <code>false</code> if it is an update count or there are
	 *         no results
	 * @exception SQLException if a database access error occurs or the elements
	 *            in the <code>int</code> array passed to this method are not
	 *            valid column indexes
	 */
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return statement.execute(sql, columnIndexes);
	}

	/**
	 * @see Statement#execute(String, String[])
	 * @param sql any SQL statement
	 * @param columnNames an array of the names of the columns in the inserted
	 *        row that should be made available for retrieval by a call to the
	 *        method <code>getGeneratedKeys</code>
	 * @return <code>true</code> if the next result is a <code>ResultSet</code>
	 *         object; <code>false</code> if it is an update count or there are
	 *         no more results
	 * @exception SQLException if a database access error occurs or the elements
	 *            of the <code>String</code> array passed to this method are not
	 *            valid column names
	 */
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		return statement.execute(sql, columnNames);
	}

	/**
	 * @see Statement#executeBatch()
	 * @return an array of update counts containing one element for each command
	 *         in the batch. The elements of the array are ordered according to
	 *         the order in which commands were added to the batch.
	 * @exception SQLException if a database access error occurs or the driver
	 *            does not support batch statements.
	 */
	public int[] executeBatch() throws SQLException {
		return statement.executeBatch();
	}

	/**
	 * @see Statement#executeQuery(String)
	 * @param sql an SQL statement to be sent to the database, typically a
	 *        static SQL <code>SELECT</code> statement
	 * @return a <code>ResultSet</code> object that contains the data produced
	 *         by the given query; never <code>null</code>
	 * @exception SQLException if a database access error occurs or the given
	 *            SQL statement produces anything other than a single
	 *            <code>ResultSet</code> object
	 */
	public ResultSet executeQuery(String sql) throws SQLException {
		CUBRIDResultSetProxy resultSetProxy = new CUBRIDResultSetProxy(
				statement.executeQuery(sql));
		resultSetProxy.setJdbcVersion(jdbcVersion);
		return resultSetProxy;
	}

	/**
	 * @see Statement#executeUpdate(String)
	 * @param sql an SQL <code>INSERT</code>, <code>UPDATE</code> or
	 *        <code>DELETE</code> statement or an SQL statement that returns
	 *        nothing
	 * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>
	 *         or <code>DELETE</code> statements, or <code>0</code> for SQL
	 *         statements that return nothing
	 * @exception SQLException if a database access error occurs or the given
	 *            SQL statement produces a <code>ResultSet</code> object
	 */
	public int executeUpdate(String sql) throws SQLException {
		return statement.executeUpdate(sql);
	}

	/**
	 * @see Statement#executeUpdate(String,int)
	 * @param sql must be an SQL <code>INSERT</code>, <code>UPDATE</code> or
	 *        <code>DELETE</code> statement or an SQL statement that returns
	 *        nothing
	 * @param autoGeneratedKeys a flag indicating whether auto-generated keys
	 *        should be made available for retrieval; one of the following
	 *        constants: <code>Statement.RETURN_GENERATED_KEYS</code>
	 *        <code>Statement.NO_GENERATED_KEYS</code>
	 * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>
	 *         or <code>DELETE</code> statements, or <code>0</code> for SQL
	 *         statements that return nothing
	 * @exception SQLException if a database access error occurs, the given SQL
	 *            statement returns a <code>ResultSet</code> object, or the
	 *            given constant is not one of those allowed
	 */
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return statement.executeUpdate(sql, autoGeneratedKeys);
	}

	/**
	 * @see Statement#executeUpdate(String, int[])
	 * @param sql an SQL <code>INSERT</code>, <code>UPDATE</code> or
	 *        <code>DELETE</code> statement or an SQL statement that returns
	 *        nothing, such as an SQL DDL statement
	 * @param columnIndexes an array of column indexes indicating the columns
	 *        that should be returned from the inserted row
	 * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>
	 *         , or <code>DELETE</code> statements, or 0 for SQL statements that
	 *         return nothing
	 * @exception SQLException if a database access error occurs, the SQL
	 *            statement returns a <code>ResultSet</code> object, or the
	 *            second argument supplied to this method is not an
	 *            <code>int</code> array whose elements are valid column indexes
	 */
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		return statement.executeUpdate(sql, columnIndexes);
	}

	/**
	 * @see Statement#executeUpdate(String, String[])
	 * @param sql an SQL <code>INSERT</code>, <code>UPDATE</code> or
	 *        <code>DELETE</code> statement or an SQL statement that returns
	 *        nothing
	 * @param columnNames an array of the names of the columns that should be
	 *        returned from the inserted row
	 * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>
	 *         , or <code>DELETE</code> statements, or 0 for SQL statements that
	 *         return nothing
	 * @exception SQLException if a database access error occurs, the SQL
	 *            statement returns a <code>ResultSet</code> object, or the
	 *            second argument supplied to this method is not a
	 *            <code>String</code> array whose elements are valid column
	 *            names
	 */
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		return statement.executeUpdate(sql, columnNames);
	}

	/**
	 * @see Statement#getConnection()
	 * @return the connection that produced this statement
	 * @exception SQLException if a database access error occurs
	 */
	public Connection getConnection() throws SQLException {
		return new CUBRIDConnectionProxy(statement.getConnection(), jdbcVersion);
	}

	/**
	 * @see Statement#getFetchDirection()
	 * @return the default fetch direction for result sets generated from this
	 *         <code>Statement</code> object
	 * @exception SQLException if a database access error occurs
	 */
	public int getFetchDirection() throws SQLException {
		return statement.getFetchDirection();
	}

	/**
	 * @see Statement#getFetchSize()
	 * @return the default fetch size for result sets generated from this
	 *         <code>Statement</code> object
	 * @exception SQLException if a database access error occurs
	 */
	public int getFetchSize() throws SQLException {
		return statement.getFetchSize();
	}

	/**
	 * @see Statement#getGeneratedKeys()
	 * @return a <code>ResultSet</code> object containing the auto-generated
	 *         key(s) generated by the execution of this <code>Statement</code>
	 *         object
	 * @exception SQLException if a database access error occurs
	 */
	public ResultSet getGeneratedKeys() throws SQLException {
		CUBRIDResultSetProxy resultSetProxy = new CUBRIDResultSetProxy(
				statement.getGeneratedKeys());
		resultSetProxy.setJdbcVersion(jdbcVersion);
		return resultSetProxy;
	}

	/**
	 * @see Statement#getMaxFieldSize()
	 * @return the current column size limit for columns storing character and
	 *         binary values; zero means there is no limit
	 * @exception SQLException if a database access error occurs
	 */
	public int getMaxFieldSize() throws SQLException {
		return statement.getMaxFieldSize();
	}

	/**
	 * @see Statement#getMaxRows()
	 * @return the current maximum number of rows for a <code>ResultSet</code>
	 *         object produced by this <code>Statement</code> object; zero means
	 *         there is no limit
	 * @exception SQLException if a database access error occurs
	 */
	public int getMaxRows() throws SQLException {
		return statement.getMaxRows();
	}

	/**
	 * @see Statement#getMoreResults()
	 * @return <code>true</code> if the next result is a <code>ResultSet</code>
	 *         object; <code>false</code> if it is an update count or there are
	 *         no more results
	 * @exception SQLException if a database access error occurs
	 */
	public boolean getMoreResults() throws SQLException {
		return statement.getMoreResults();
	}

	/**
	 * @see Statement#getMoreResults(int)
	 * @param current one of the following <code>Statement</code> constants
	 *        indicating what should happen to current <code>ResultSet</code>
	 *        objects obtained using the method <code>getResultSet</code>:
	 *        <code>Statement.CLOSE_CURRENT_RESULT</code>,
	 *        <code>Statement.KEEP_CURRENT_RESULT</code>, or
	 *        <code>Statement.CLOSE_ALL_RESULTS</code>
	 * @return <code>true</code> if the next result is a <code>ResultSet</code>
	 *         object; <code>false</code> if it is an update count or there are
	 *         no more results
	 * @exception SQLException if a database access error occurs or the argument
	 *            supplied is not one of the following:
	 *            <code>Statement.CLOSE_CURRENT_RESULT</code>,
	 *            <code>Statement.KEEP_CURRENT_RESULT</code>, or
	 *            <code>Statement.CLOSE_ALL_RESULTS</code>
	 */
	public boolean getMoreResults(int current) throws SQLException {
		return statement.getMoreResults(current);
	}

	/**
	 * @see Statement#getQueryTimeout()
	 * @return the current query timeout limit in seconds; zero means there is
	 *         no limit
	 * @exception SQLException if a database access error occurs
	 */
	public int getQueryTimeout() throws SQLException {
		return statement.getQueryTimeout();
	}

	/**
	 * @see Statement#getResultSet()
	 * @return the current result as a <code>ResultSet</code> object or
	 *         <code>null</code> if the result is an update count or there are
	 *         no more results
	 * @exception SQLException if a database access error occurs
	 */
	public ResultSet getResultSet() throws SQLException {
		CUBRIDResultSetProxy resultSetProxy = new CUBRIDResultSetProxy(
				statement.getResultSet());
		resultSetProxy.setJdbcVersion(jdbcVersion);
		return resultSetProxy;
	}

	/**
	 * @see Statement#getResultSetConcurrency()
	 * @return either <code>ResultSet.CONCUR_READ_ONLY</code> or
	 *         <code>ResultSet.CONCUR_UPDATABLE</code>
	 * @exception SQLException if a database access error occurs
	 */
	public int getResultSetConcurrency() throws SQLException {
		return statement.getResultSetConcurrency();
	}

	/**
	 * @see Statement#getResultSetHoldability()
	 * @return either <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
	 *         <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
	 * @exception SQLException if a database access error occurs
	 */
	public int getResultSetHoldability() throws SQLException {
		return statement.getResultSetHoldability();
	}

	/**
	 * @see Statement#getResultSetType()
	 * @return one of <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *         <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *         <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @exception SQLException if a database access error occurs
	 */
	public int getResultSetType() throws SQLException {
		return statement.getResultSetType();
	}

	/**
	 * @see Statement#getUpdateCount()
	 * @return the current result as an update count; -1 if the current result
	 *         is a <code>ResultSet</code> object or there are no more results
	 * @exception SQLException if a database access error occurs
	 */
	public int getUpdateCount() throws SQLException {
		return statement.getUpdateCount();
	}

	/**
	 * @see Statement#getWarnings()
	 * @return the first <code>SQLWarning</code> object or <code>null</code> if
	 *         there are no warnings
	 * @exception SQLException if a database access error occurs or this method
	 *            is called on a closed statement
	 */
	public SQLWarning getWarnings() throws SQLException {
		return statement.getWarnings();
	}

	/**
	 * @see Statement#setCursorName(String)
	 *
	 * @param name the new cursor name, which must be unique within a connection
	 * @exception SQLException if a database access error occurs
	 */
	public void setCursorName(String name) throws SQLException {
		statement.setCursorName(name);

	}

	/**
	 * @see Statement#setEscapeProcessing(boolean)
	 * @param enable <code>true</code> to enable escape processing;
	 *        <code>false</code> to disable it
	 * @exception SQLException if a database access error occurs
	 */
	public void setEscapeProcessing(boolean enable) throws SQLException {
		statement.setEscapeProcessing(enable);
	}

	/**
	 * @see Statement#setFetchDirection(int)
	 * @param direction the initial direction for processing rows
	 * @exception SQLException if a database access error occurs or the given
	 *            direction is not one of <code>ResultSet.FETCH_FORWARD</code>,
	 *            <code>ResultSet.FETCH_REVERSE</code>, or
	 *            <code>ResultSet.FETCH_UNKNOWN</code>
	 */
	public void setFetchDirection(int direction) throws SQLException {
		statement.setFetchDirection(direction);
	}

	/**
	 * @see Statement#setFetchSize(int)
	 * @param rows the number of rows to fetch
	 * @exception SQLException if a database access error occurs, or the
	 *            condition 0 <= <code>rows</code> <=
	 *            <code>this.getMaxRows()</code> is not satisfied.
	 */
	public void setFetchSize(int rows) throws SQLException {
		statement.setFetchSize(rows);
	}

	/**
	 * @see Statement#setMaxFieldSize(int)
	 * @param max the new column size limit in bytes; zero means there is no
	 *        limit
	 * @exception SQLException if a database access error occurs or the
	 *            condition max >= 0 is not satisfied
	 */
	public void setMaxFieldSize(int max) throws SQLException {
		statement.setMaxFieldSize(max);
	}

	/**
	 * @see Statement#setMaxRows(int)
	 * @param max the new max rows limit; zero means there is no limit
	 * @exception SQLException if a database access error occurs or the
	 *            condition max >= 0 is not satisfied
	 */
	public void setMaxRows(int max) throws SQLException {
		statement.setMaxRows(max);
	}

	/**
	 * @see Statement#setQueryTimeout(int)
	 * @param seconds the new query timeout limit in seconds; zero means there
	 *        is no limit
	 * @exception SQLException if a database access error occurs or the
	 *            condition seconds >= 0 is not satisfied
	 */
	public void setQueryTimeout(int seconds) throws SQLException {
		statement.setQueryTimeout(seconds);
	}

	/**
	 * Invoke the executeInsert method in CUBRID Statement object
	 *
	 * @param sql the sql
	 * @return the CUBRIDOIDProxy object
	 * @exception SQLException if a database access error occurs
	 */
	public CUBRIDOIDProxy executeInsert(String sql) throws SQLException {

		CUBRIDOIDProxy proxy = new CUBRIDOIDProxy(ReflectionUtil.invoke(
				statement, "executeInsert", new Class<?>[]{String.class },
				new Object[]{sql }));
		proxy.setJdbcVersion(jdbcVersion);
		return proxy;
	}

	/**
	 * Invoke the getQueryplan method in CUBRID Statement object
	 *
	 * @return the query plan string
	 * @exception SQLException if a database access error occurs
	 */
	public String getQueryplan() throws SQLException {
		return (String) ReflectionUtil.invoke(statement, "getQueryplan");
	}

	/**
	 * Invoke the getQueryplan method in CUBRID Statement object
	 *
	 * @param sql the sql string
	 * @return the query plan string
	 * @exception SQLException if a database access error occurs
	 */
	public String getQueryplan(String sql) throws SQLException {
		return (String) ReflectionUtil.invoke(statement, "getQueryplan",
				String.class, sql);
	}

	/**
	 * Invoke the getStatementType method in CUBRID Statement object
	 *
	 * @return the byte
	 * @exception SQLException if a database access error occurs
	 */
	public byte getStatementType() throws SQLException {
		return (Byte) ReflectionUtil.invoke(statement, "getStatementType");
	}

	/**
	 * Invoke the setOnlyQueryPlan method in CUBRID Statement object
	 *
	 * @param value the boolean value
	 * @exception SQLException if a database access error occurs
	 */
	public void setOnlyQueryPlan(boolean value) throws SQLException {
		ReflectionUtil.invoke(statement, "setOnlyQueryPlan",
				new Class<?>[]{boolean.class }, new Object[]{value });
	}

	/**
	 * Invoke the setQueryInfo method in CUBRID Statement object
	 *
	 * @param value the boolean value
	 * @exception SQLException if a database access error occurs
	 */
	public void setQueryInfo(boolean value) throws SQLException {
		ReflectionUtil.invoke(statement, "setQueryInfo",
				new Class<?>[]{boolean.class }, new Object[]{value });
	}

	public String getJdbcVersion() {
		return jdbcVersion;
	}

	public void setJdbcVersion(String jdbcVersion) {
		this.jdbcVersion = jdbcVersion;
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
		return statement.isClosed();
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
		return statement.isPoolable();
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
		statement.setPoolable(poolable);
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
		return statement.isWrapperFor(iface);
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
		return statement.unwrap(iface);
	}

	//--------------------------JDBC 4.1 -----------------------------

    /**
     * Specifies that this {@code Statement} will be closed when all its
     * dependent result sets are closed. If execution of the {@code Statement}
     * does not produce any result sets, this method has no effect.
     * <p>
     * <strong>Note:</strong> Multiple calls to {@code closeOnCompletion} do
     * not toggle the effect on this {@code Statement}. However, a call to
     * {@code closeOnCompletion} does effect both the subsequent execution of
     * statements, and statements that currently have open, dependent,
     * result sets.
     *
     * @throws SQLException if this method is called on a closed
     * {@code Statement}
     * @since 1.7
     */
	public void closeOnCompletion() throws SQLException {
		statement.closeOnCompletion();
	}

    /**
     * Returns a value indicating whether this {@code Statement} will be
     * closed when all its dependent result sets are closed.
     * @return {@code true} if the {@code Statement} will be closed when all
     * of its dependent result sets are closed; {@code false} otherwise
     * @throws SQLException if this method is called on a closed
     * {@code Statement}
     * @since 1.7
     */
	public boolean isCloseOnCompletion() throws SQLException {
		return statement.isCloseOnCompletion();
	}

}
