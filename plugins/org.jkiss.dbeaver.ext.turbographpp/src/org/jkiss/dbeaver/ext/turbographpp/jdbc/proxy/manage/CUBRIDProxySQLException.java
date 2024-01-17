/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search Solution. 
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met: 
 *
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer. 
 *
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution. 
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without 
 *   specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE. 
 *
 */

package org.jkiss.dbeaver.ext.turbographpp.jdbc.proxy.manage;

import java.sql.SQLException;

/**
 * 
 * <P>
 * An exception that provides information on a database access error or other
 * errors.
 * 
 * <P>
 * Each <code>SQLException</code> provides several kinds of information:
 * <UL>
 * <LI>a string describing the error. This is used as the Java Exception
 * message, available via the method <code>getMesage</code>.
 * <LI>a "SQLstate" string, which follows either the XOPEN SQLstate conventions
 * or the SQL 99 conventions. The values of the SQLState string are described in
 * the appropriate spec. The <code>DatabaseMetaData</code> method
 * <code>getSQLStateType</code> can be used to discover whether the driver
 * returns the XOPEN type or the SQL 99 type.
 * <LI>an integer error code that is specific to each vendor. Normally this will
 * be the actual error code returned by the underlying database.
 * <LI>a chain to a next Exception. This can be used to provide additional error
 * information.
 * </UL>
 * 
 * @author pangqiren
 * @version 1.0 - 2009-12-28 created by pangqiren
 */
public class CUBRIDProxySQLException extends
		SQLException {

	private static final long serialVersionUID = -2247520935409591719L;

	public CUBRIDProxySQLException(String msg, int errCode) {
		super(msg, null, errCode);
	}

	public CUBRIDProxySQLException(SQLException cause) {
		super(cause.getMessage(), cause.getSQLState(), cause.getErrorCode());
	}

	public CUBRIDProxySQLException(Throwable cause, int errCode) {
		super(cause.getMessage(), null, errCode);
	}

	public CUBRIDProxySQLException(Throwable cause, SQLException rootCause) {
		super(cause.getMessage() + "\r\n" + rootCause.getMessage(),
				rootCause.getSQLState(), rootCause.getErrorCode());
	}

	public CUBRIDProxySQLException(Throwable cause, Throwable rootCause,
			int errorCode) {
		super(cause.getMessage() + "\r\n" + rootCause.getMessage(), null,
				errorCode);
	}
}
