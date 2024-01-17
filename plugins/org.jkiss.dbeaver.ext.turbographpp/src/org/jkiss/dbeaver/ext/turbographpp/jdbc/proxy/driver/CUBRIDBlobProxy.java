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
import java.lang.reflect.InvocationTargetException;
import java.sql.Blob;
import java.sql.SQLException;

import org.jkiss.dbeaver.ext.turbographpp.jdbc.proxy.manage.CUBRIDProxyException;

/**
 * 
 * The proxy for cubrid.jdbc.driver.CUBRIDBlob
 * 
 * @author pangqiren
 * @version 1.0 - 2010-8-6 created by pangqiren
 */
public class CUBRIDBlobProxy implements
		Blob {

	private Blob proxyObj = null;

	/**
	 * The constructor
	 * 
	 * @param blob
	 */
	public CUBRIDBlobProxy(Blob blob) {
		proxyObj = blob;
	}

	/**
	 * The constructor
	 * 
	 * @param conn
	 * @throws CUBRIDProxyException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public CUBRIDBlobProxy(CUBRIDConnectionProxy conn) throws CUBRIDProxyException,
			SQLException {
		ClassLoader loader = conn.getProxyObject().getClass().getClassLoader();
		try {
			Class<Blob> blobClazz = (Class<Blob>) loader.loadClass("cubrid.jdbc.driver.CUBRIDBlob");
			proxyObj = blobClazz.getConstructor(conn.getProxyClass()).newInstance(
					conn.getProxyObject());
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
	 * @throws CUBRIDProxyException
	 */
	@SuppressWarnings("unchecked")
	public CUBRIDBlobProxy(CUBRIDConnectionProxy conn, byte[] packedLobHandle) throws CUBRIDProxyException {
		ClassLoader loader = conn.getProxyObject().getClass().getClassLoader();
		try {
			Class<Blob> blobClazz = (Class<Blob>) loader.loadClass("cubrid.jdbc.driver.CUBRIDBlob");
			proxyObj = blobClazz.getConstructor(conn.getProxyClass(),
					byte[].class).newInstance(conn.getProxyObject(),
					packedLobHandle);
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
	 * Retrieves the <code>BLOB</code> value designated by this
	 * <code>Blob</code> instance as a stream.
	 * 
	 * @return a stream containing the <code>BLOB</code> data
	 * @exception SQLException if there is an error accessing the
	 *            <code>BLOB</code> value
	 * @see #setBinaryStream
	 */
	public InputStream getBinaryStream() throws SQLException {
		return proxyObj.getBinaryStream();
	}

	/**
	 * Retrieves all or part of the <code>BLOB</code> value that this
	 * <code>Blob</code> object represents, as an array of bytes. This
	 * <code>byte</code> array contains up to <code>length</code> consecutive
	 * bytes starting at position <code>pos</code>.
	 * 
	 * @param pos the ordinal position of the first byte in the
	 *        <code>BLOB</code> value to be extracted; the first byte is at
	 *        position 1
	 * @param length the number of consecutive bytes to be copied
	 * @return a byte array containing up to <code>length</code> consecutive
	 *         bytes from the <code>BLOB</code> value designated by this
	 *         <code>Blob</code> object, starting with the byte at position
	 *         <code>pos</code>
	 * @exception SQLException if there is an error accessing the
	 *            <code>BLOB</code> value
	 * @see #setBytes
	 */
	public byte[] getBytes(long pos, int length) throws SQLException {
		return proxyObj.getBytes(pos, length);
	}

	/**
	 * Returns the number of bytes in the <code>BLOB</code> value designated by
	 * this <code>Blob</code> object.
	 * 
	 * @return length of the <code>BLOB</code> in bytes
	 * @exception SQLException if there is an error accessing the length of the
	 *            <code>BLOB</code>
	 */
	public long length() throws SQLException {
		return proxyObj.length();
	}

	/**
	 * Retrieves the byte position at which the specified byte array
	 * <code>pattern</code> begins within the <code>BLOB</code> value that this
	 * <code>Blob</code> object represents. The search for <code>pattern</code>
	 * begins at position <code>start</code>.
	 * 
	 * @param pattern the byte array for which to search
	 * @param start the position at which to begin searching; the first position
	 *        is 1
	 * @return the position at which the pattern appears, else -1
	 * @exception SQLException if there is an error accessing the
	 *            <code>BLOB</code>
	 */
	public long position(byte[] pattern, long start) throws SQLException {
		return proxyObj.position(pattern, start);
	}

	/**
	 * Retrieves the byte position in the <code>BLOB</code> value designated by
	 * this <code>Blob</code> object at which <code>pattern</code> begins. The
	 * search begins at position <code>start</code>.
	 * 
	 * @param pattern the <code>Blob</code> object designating the
	 *        <code>BLOB</code> value for which to search
	 * @param start the position in the <code>BLOB</code> value at which to
	 *        begin searching; the first position is 1
	 * @return the position at which the pattern begins, else -1
	 * @exception SQLException if there is an error accessing the
	 *            <code>BLOB</code> value
	 */
	public long position(Blob pattern, long start) throws SQLException {
		return proxyObj.position(pattern, start);
	}

	/**
	 * Retrieves a stream that can be used to write to the <code>BLOB</code>
	 * value that this <code>Blob</code> object represents. The stream begins at
	 * position <code>pos</code>.
	 * 
	 * @param pos the position in the <code>BLOB</code> value at which to start
	 *        writing
	 * @return a <code>java.io.OutputStream</code> object to which data can be
	 *         written
	 * @exception SQLException if there is an error accessing the
	 *            <code>BLOB</code> value
	 * @see #getBinaryStream
	 */
	public OutputStream setBinaryStream(long pos) throws SQLException {
		return proxyObj.setBinaryStream(pos);
	}

	/**
	 * Writes the given array of bytes to the <code>BLOB</code> value that this
	 * <code>Blob</code> object represents, starting at position
	 * <code>pos</code>, and returns the number of bytes written.
	 * 
	 * @param pos the position in the <code>BLOB</code> object at which to start
	 *        writing
	 * @param bytes the array of bytes to be written to the <code>BLOB</code>
	 *        value that this <code>Blob</code> object represents
	 * @return the number of bytes written
	 * @exception SQLException if there is an error accessing the
	 *            <code>BLOB</code> value
	 * @see #getBytes
	 */
	public int setBytes(long pos, byte[] bytes) throws SQLException {
		return proxyObj.setBytes(pos, bytes);
	}

	/**
	 * Writes all or part of the given <code>byte</code> array to the
	 * <code>BLOB</code> value that this <code>Blob</code> object represents and
	 * returns the number of bytes written. Writing starts at position
	 * <code>pos</code> in the <code>BLOB</code> value; <code>len</code> bytes
	 * from the given byte array are written.
	 * 
	 * @param pos the position in the <code>BLOB</code> object at which to start
	 *        writing
	 * @param bytes the array of bytes to be written to this <code>BLOB</code>
	 *        object
	 * @param offset the offset into the array <code>bytes</code> at which to
	 *        start reading the bytes to be set
	 * @param len the number of bytes to be written to the <code>BLOB</code>
	 *        value from the array of bytes <code>bytes</code>
	 * @return the number of bytes written
	 * @exception SQLException if there is an error accessing the
	 *            <code>BLOB</code> value
	 * @see #getBytes
	 */
	public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
		return proxyObj.setBytes(pos, bytes, offset, len);
	}

	/**
	 * Truncates the <code>BLOB</code> value that this <code>Blob</code> object
	 * represents to be <code>len</code> bytes in length.
	 * 
	 * @param len the length, in bytes, to which the <code>BLOB</code> value
	 *        that this <code>Blob</code> object represents should be truncated
	 * @exception SQLException if there is an error accessing the
	 *            <code>BLOB</code> value
	 */
	public void truncate(long len) throws SQLException {
		proxyObj.truncate(len);
	}

	public Blob getProxyObj() {
		return proxyObj;
	}

	/**
	 * This method frees the <code>Blob</code> object and releases the resources
	 * that it holds. The object is invalid once the <code>free</code> method is
	 * called.
	 *<p>
	 * After <code>free</code> has been called, any attempt to invoke a method
	 * other than <code>free</code> will result in a <code>SQLException</code>
	 * being thrown. If <code>free</code> is called multiple times, the
	 * subsequent calls to <code>free</code> are treated as a no-op.
	 *<p>
	 * 
	 * @throws SQLException if an error occurs releasing the Blob's resources
	 * @since 1.6
	 */
	public void free() throws SQLException {
		proxyObj.free();
	}

	/**
	 * Returns an <code>InputStream</code> object that contains a partial
	 * <code>Blob</code> value, starting with the byte specified by pos, which
	 * is length bytes in length.
	 * 
	 * @param pos the offset to the first byte of the partial value to be
	 *        retrieved. The first byte in the <code>Blob</code> is at position
	 *        1
	 * @param length the length in bytes of the partial value to be retrieved
	 * @return <code>InputStream</code> through which the partial
	 *         <code>Blob</code> value can be read.
	 * @throws SQLException if pos is less than 1 or if pos is greater than the
	 *         number of bytes in the <code>Blob</code> or if pos + length is
	 *         greater than the number of bytes in the <code>Blob</code>
	 * @since 1.6
	 */
	public InputStream getBinaryStream(long pos, long length) throws SQLException {
		return proxyObj.getBinaryStream(pos, length);
	}

}
