package org.jkiss.dbeaver.ext.turbographpp.model.plan;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Statement;

public class TurboGraphPPStatementProxy {

    protected Statement statement;

    public TurboGraphPPStatementProxy(Statement statement) {
        this.statement = statement;
    }

    public String getQueryplan(String sql) throws SQLException {
        return (String) invoke(statement, "getQueryplan", String.class, sql);
    }

    private static Object invoke(Object objSrc, String methodName, Class<?> clazz, Object obj) throws SQLException {
        try {
            Method m = objSrc.getClass().getMethod(methodName, new Class<?>[] { clazz });
            return m.invoke(objSrc, new Object[] { obj });
        } catch (SecurityException e) {
            throw e;
        } catch (NoSuchMethodException e) {
            throw new SQLException(e.getMessage(), null, -90000);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw new SQLException(e.getMessage(), null, -90001);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof SQLException) {
                throw new SQLException(e.getMessage(), e.getTargetException());
            } else {
                throw new SQLException(e.getMessage() + "\r\n" + e.getTargetException().getMessage(),
                        null, -90002);
            }
        }
    }
}
