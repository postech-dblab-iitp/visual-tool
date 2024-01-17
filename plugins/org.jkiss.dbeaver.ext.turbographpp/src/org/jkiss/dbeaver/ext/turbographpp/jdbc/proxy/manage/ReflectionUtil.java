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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * The reflection util
 *
 * @author robinhood
 *
 */
public final class ReflectionUtil {

	private ReflectionUtil() {
	}

	/**
	 *
	 * Invoke the method of this object by reflection
	 *
	 * @param objSrc the invoked object
	 * @param methodName the invoked method
	 * @return the object
	 * @throws CUBRIDProxySQLException exception
	 */
	public static Object invoke(Object objSrc, String methodName) throws CUBRIDProxySQLException {
		try {

			Method m = objSrc.getClass().getMethod(methodName);
			return m.invoke(objSrc);
		} catch (SecurityException e) {
			throw e;
		} catch (NoSuchMethodException e) {
			throw new CUBRIDProxySQLException(e, -90000);
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw new CUBRIDProxySQLException(e, -90001);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof SQLException) {
				throw new CUBRIDProxySQLException(e,
						(SQLException) e.getTargetException());
			} else {
				throw new CUBRIDProxySQLException(e, e.getTargetException(),
						-90002);
			}
		}
	}

	/**
	 *
	 * Invoke the method of this object by reflection
	 *
	 * @param objSrc the invoked object
	 * @param methodName the method name
	 * @param clazz method parameter type
	 * @param obj method parameter object
	 * @return the object
	 * @throws CUBRIDProxySQLException exception
	 */
	public static Object invoke(Object objSrc, String methodName,
			Class<?> clazz, Object obj) throws CUBRIDProxySQLException {
		try {
			Method m = objSrc.getClass().getMethod(methodName,
					new Class<?>[] {clazz });
			return m.invoke(objSrc, new Object[] {obj });
		} catch (SecurityException e) {
			throw e;
		} catch (NoSuchMethodException e) {
			throw new CUBRIDProxySQLException(e, -90000);
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw new CUBRIDProxySQLException(e, -90001);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof SQLException) {
				throw new CUBRIDProxySQLException(e,
						(SQLException) e.getTargetException());
			} else {
				throw new CUBRIDProxySQLException(e, e.getTargetException(),
						-90002);
			}
		}
	}

	/**
	 *
	 * Invoke the method of this object by reflection
	 *
	 * @param <T> the template type
	 * @param objSrc the invoked object
	 * @param methodName the invoked method
	 * @param parametersType the parameters type
	 * @param parameters the parameters object
	 * @return the object
	 * @throws CUBRIDProxySQLException the exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invoke(Object objSrc, String methodName,
			Class<?>[] parametersType, Object[] parameters) throws CUBRIDProxySQLException {
		try {
			Method m = objSrc.getClass().getMethod(methodName, parametersType);
			return (T) m.invoke(objSrc, parameters);
		} catch (SecurityException e) {
			throw e;
		} catch (NoSuchMethodException e) {
			throw new CUBRIDProxySQLException(e, -90000);
		} catch (IllegalArgumentException e) {

			throw e;
		} catch (IllegalAccessException e) {
			throw new CUBRIDProxySQLException(e, -90001);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof SQLException) {
				throw new CUBRIDProxySQLException(e,
						(SQLException) e.getTargetException());
			} else {
				throw new CUBRIDProxySQLException(e, e.getTargetException(),
						-90002);
			}
		}
	}

	/**
	 *
	 * Invoke the static method of this class by reflection
	 *
	 * @param clazzSrc the invoked class
	 * @param methodName the invoked method name
	 * @param parametersType the invoked parameters type
	 * @param parameters the invoked parameters object
	 * @return the object
	 * @throws CUBRIDProxySQLException the exception
	 */
	public static Object invokeStaticMethod(Class<?> clazzSrc,
			String methodName, Class<?>[] parametersType, Object[] parameters) throws CUBRIDProxySQLException {
		try {
			// clazzSrc.getMethods()
			Method m = clazzSrc.getMethod(methodName, parametersType);

			return m.invoke(null, parameters);

		} catch (SecurityException e) {
			throw e;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw new CUBRIDProxySQLException(e, -90001);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof SQLException) {
				throw new CUBRIDProxySQLException(e,
						(SQLException) e.getTargetException());
			} else {
				throw new CUBRIDProxySQLException(e, e.getTargetException(),
						-90002);
			}
		} catch (NoSuchMethodException e) {
			throw new CUBRIDProxySQLException(e, -90003);
		}
	}

	/**
	 *
	 * Get static field value
	 *
	 * @param fieldName the field name
	 * @param clazz the class
	 * @return the value
	 * @throws CUBRIDProxySQLException the exception
	 */
	public static int getStaticFieldValue(String fieldName, Class<?> clazz) throws CUBRIDProxySQLException {

		try {
			Field field = clazz.getField(fieldName);
			return field.getInt(clazz);
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw new CUBRIDProxySQLException(e, -90001);
		} catch (SecurityException e) {
			throw e;
		} catch (NoSuchFieldException e) {
			throw new CUBRIDProxySQLException(e, -90004);
		}
	}

}
