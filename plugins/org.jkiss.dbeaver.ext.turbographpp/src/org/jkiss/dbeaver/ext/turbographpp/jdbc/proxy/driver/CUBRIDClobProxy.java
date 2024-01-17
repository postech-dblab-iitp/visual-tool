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
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.sql.Clob;
import java.sql.SQLException;

import org.jkiss.dbeaver.ext.turbographpp.jdbc.proxy.manage.CUBRIDProxyException;

/**
 * 
 * The proxy for cubrid.jdbc.driver.CUBRIDClob
 * 
 * @author pangqiren
 * @version 1.0 - 2010-8-6 created by pangqiren
 */
public class CUBRIDClobProxy implements
		Clob {

	private Clob proxyObj = null;

	/**
	 * The constructor
	 * 
	 * @param clob
	 */
	public CUBRIDClobProxy(Clob clob) {
		proxyObj = clob;
	}

	/**
	 * The constructor
	 * 
	 * @param conn
	 * @param charsetName
	 * @throws CUBRIDProxyException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public CUBRIDClobProxy(CUBRIDConnectionProxy conn, String charsetName) throws CUBRIDProxyException,
			SQLException {
		ClassLoader loader = conn.getProxyObject().getClass().getClassLoader();
		try {
			Class<Clob> blobClazz = (Class<Clob>) loader.loadClass("cubrid.jdbc.driver.CUBRIDClob");
			proxyObj = blobClazz.getConstructor(conn.getProxyClass(),
					String.class).newInstance(conn.getProxyObject(),
					charsetName);
		} catch (ClassNotFoundException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		} catch (SecurityException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		} catch (InstantiationException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		}
	}

	/**
	 * The constructor
	 * 
	 * @param conn
	 * @param packedLobHandle
	 * @param charsetNameO
	 * @throws CUBRIDProxyException
	 */
	@SuppressWarnings("unchecked")
	public CUBRIDClobProxy(CUBRIDConnectionProxy conn, byte[] packedLobHandle,
			String charsetName) throws CUBRIDProxyException {
		ClassLoader loader = conn.getProxyObject().getClass().getClassLoader();
		try {
			Class<Clob> blobClazz = (Class<Clob>) loader.loadClass("cubrid.jdbc.driver.CUBRIDClob");
			proxyObj = blobClazz.getConstructor(conn.getProxyClass(),
					byte[].class, String.class).newInstance(
					conn.getProxyObject(), packedLobHandle, charsetName);
		} catch (ClassNotFoundException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		} catch (SecurityException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		} catch (InstantiationException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new CUBRIDProxyException(e.getMessage(), e);
		}
	}

	/**
	 * Retrieves the <code>CLOB</code> value designated by this
	 * <code>Clob</code> object as an ascii stream.
	 * 
	 * @return a <code>java.io.InputStream</code> object containing the
	 *         <code>CLOB</code> data
	 * @exception SQLException if there is an error accessing the
	 *            <code>CLOB</code> value
	 * @see #setAsciiStream
	 */
	public InputStream getAsciiStream() throws SQLException {
		return proxyObj.getAsciiStream();
	}

	/**
	 * Retrieves the <code>CLOB</code> value designated by this
	 * <code>Clob</code> object as a <code>java.io.Reader</code> object (or as a
	 * stream of characters).
	 * 
	 * @return a <code>java.io.Reader</code> object containing the
	 *         <code>CLOB</code> data
	 * @exception SQLException if there is an error accessing the
	 *            <code>CLOB</code> value
	 * @see #setCharacterStream
	 */
	public Reader getCharacterStream() throws SQLException {
		return proxyObj.getCharacterStream();
	}

	/**
	 * Retrieves a copy of the specified substring in the <code>CLOB</code>
	 * value designated by this <code>Clob</code> object. The substring begins
	 * at position <code>pos</code> and has up to <code>length</code>
	 * consecutive characters.
	 * 
	 * @param pos the first character of the substring to be extracted. The
	 *        first character is at position 1.
	 * @param length the number of consecutive characters to be copied
	 * @return a <code>String</code> that is the specified substring in the
	 *         <code>CLOB</code> value designated by this <code>Clob</code>
	 *         object
	 * @exception SQLException if there is an error accessing the
	 *            <code>CLOB</code> value
	 */
	public String getSubString(long pos, int length) throws SQLException {
		return proxyObj.getSubString(pos, length);
	}

	/**
	 * Retrieves the number of characters in the <code>CLOB</code> value
	 * designated by this <code>Clob</code> object.
	 * 
	 * @return length of the <code>CLOB</code> in characters
	 * @exception SQLException if there is an error accessing the length of the
	 *            <code>CLOB</code> value
	 */
	public long length() throws SQLException {
		return proxyObj.length();
	}

	/**
	 * Retrieves the character position at which the specified substring
	 * <code>searchstr</code> appears in the SQL <code>CLOB</code> value
	 * represented by this <code>Clob</code> object. The search begins at
	 * position <code>start</code>.
	 * 
	 * @param searchstr the substring for which to search
	 * @param start the position at which to begin searching; the first position
	 *        is 1
	 * @return the position at which the substring appears or -1 if it is not
	 *         present; the first position is 1
	 * @exception SQLException if there is an error accessing the
	 *            <code>CLOB</code> value
	 */
	public long position(String searchstr, long start) throws SQLException {
		return proxyObj.position(searchstr, start);
	}

	/**
	 * Retrieves the character position at which the specified <code>Clob</code>
	 * object <code>searchstr</code> appears in this <code>Clob</code> object.
	 * The search begins at position <code>start</code>.
	 * 
	 * @param searchstr the <code>Clob</code> object for which to search
	 * @param start the position at which to begin searching; the first position
	 *        is 1
	 * @return the position at which the <code>Clob</code> object appears or -1
	 *         if it is not present; the first position is 1
	 * @exception SQLException if there is an error accessing the
	 *            <code>CLOB</code> value
	 */
	public long position(Clob searchstr, long start) throws SQLException {
		return proxyObj.position(searchstr, start);
	}

	/**
	 * Retrieves a stream to be used to write Ascii characters to the
	 * <code>CLOB</code> value that this <code>Clob</code> object represents,
	 * starting at position <code>pos</code>.
	 * 
	 * @param pos the position at which to start writing to this
	 *        <code>CLOB</code> object
	 * @return the stream to which ASCII encoded characters can be written
	 * @exception SQLException if there is an error accessing the
	 *            <code>CLOB</code> value
	 * @see #getAsciiStream
	 * 
	 */
	public OutputStream setAsciiStream(long pos) throws SQLException {
		return proxyObj.setAsciiStream(pos);
	}

	/**
	 * Retrieves a stream to be used to write a stream of Unicode characters to
	 * the <code>CLOB</code> value that this <code>Clob</code> object
	 * represents, at position <code>pos</code>.
	 * 
	 * @param pos the position at which to start writing to the
	 *        <code>CLOB</code> value
	 * 
	 * @return a stream to which Unicode encoded characters can be written
	 * @exception SQLException if there is an error accessing the
	 *            <code>CLOB</code> value
	 * @see #getCharacterStream
	 * 
	 */
	public Writer setCharacterStream(long pos) throws SQLException {
		return proxyObj.setCharacterStream(pos);
	}

	/**
	 * Writes the given Java <code>String</code> to the <code>CLOB</code> value
	 * that this <code>Clob</code> object designates at the position
	 * <code>pos</code>.
	 * 
	 * @param pos the position at which to start writing to the
	 *        <code>CLOB</code> value that this <code>Clob</code> object
	 *        represents
	 * @param str the string to be written to the <code>CLOB</code> value that
	 *        this <code>Clob</code> designates
	 * @return the number of characters written
	 * @exception SQLException if there is an error accessing the
	 *            <code>CLOB</code> value
	 * 
	 */
	public int setString(long pos, String str) throws SQLException {
		return proxyObj.setString(pos, str);
	}

	/**
	 * Writes <code>len</code> characters of <code>str</code>, starting at
	 * character <code>offset</code>, to the <code>CLOB</code> value that this
	 * <code>Clob</code> represents.
	 * 
	 * @param pos the position at which to start writing to this
	 *        <code>CLOB</code> object
	 * @param str the string to be written to the <code>CLOB</code> value that
	 *        this <code>Clob</code> object represents
	 * @param offset the offset into <code>str</code> to start reading the
	 *        characters to be written
	 * @param len the number of characters to be written
	 * @return the number of characters written
	 * @exception SQLException if there is an error accessing the
	 *            <code>CLOB</code> value
	 */
	public int setString(long pos, String str, int offset, int len) throws SQLException {
		return proxyObj.setString(pos, str, offset, len);
	}

	/**
	 * Truncates the <code>CLOB</code> value that this <code>Clob</code>
	 * designates to have a length of <code>len</code> characters.
	 * 
	 * @param len the length, in bytes, to which the <code>CLOB</code> value
	 *        should be truncated
	 * @exception SQLException if there is an error accessing the
	 *            <code>CLOB</code> value
	 */
	public void truncate(long len) throws SQLException {
		proxyObj.truncate(len);
	}

	public Clob getProxyObj() {
		return proxyObj;
	}

	/**
	 * This method frees the <code>Clob</code> object and releases the resources
	 * the resources that it holds. The object is invalid once the
	 * <code>free</code> method is called.
	 * <p>
	 * After <code>free</code> has been called, any attempt to invoke a method
	 * other than <code>free</code> will result in a <code>SQLException</code>
	 * being thrown. If <code>free</code> is called multiple times, the
	 * subsequent calls to <code>free</code> are treated as a no-op.
	 * <p>
	 * 
	 * @throws SQLException if an error occurs releasing the Clob's resources
	 * 
	 * @since 1.6
	 */
	public void free() throws SQLException {
		proxyObj.free();
	}

	/**
	 * Returns a <code>Reader</code> object that contains a partial
	 * <code>Clob</code> value, starting with the character specified by pos,
	 * which is length characters in length.
	 * 
	 * @param pos the offset to the first character of the partial value to be
	 *        retrieved. The first character in the Clob is at position 1.
	 * @param length the length in characters of the partial value to be
	 *        retrieved.
	 * @return <code>Reader</code> through which the partial <code>Clob</code>
	 *         value can be read.
	 * @throws SQLException if pos is less than 1 or if pos is greater than the
	 *         number of characters in the <code>Clob</code> or if pos + length
	 *         is greater than the number of characters in the <code>Clob</code>
	 * 
	 * @since 1.6
	 */
	public Reader getCharacterStream(long pos, long length) throws SQLException {
		return proxyObj.getCharacterStream(pos, length);
	}

}
