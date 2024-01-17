package org.jkiss.dbeaver.ext.turbographpp.model.plan;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPDataSource;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.DBCSession;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.exec.plan.*;
import org.jkiss.dbeaver.model.impl.plan.AbstractExecutionPlanSerializer;
import org.jkiss.dbeaver.model.sql.SQLDialect;
import org.jkiss.dbeaver.model.sql.SQLUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;

public class TurboGraphPPPlanAnalyser extends AbstractExecutionPlanSerializer implements DBCQueryPlanner {

    private TurboGraphPPDataSource dataSource;

    public TurboGraphPPPlanAnalyser(TurboGraphPPDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public TurboGraphPPExecutionPlan explain(JDBCSession session, String query) throws DBCException {
//        final SQLDialect dialect = SQLUtils.getDialectFromObject(dataSource);
//        final String plainQuery = SQLUtils.stripComments(dialect, query).toUpperCase();
//        final String firstKeyword = SQLUtils.getFirstKeyword(dialect, plainQuery);
//        if (!"SELECT".equalsIgnoreCase(firstKeyword) && !"WITH".equalsIgnoreCase(firstKeyword)) {
//            throw new DBCException("Only SELECT statements could produce execution plan");
//        }
        return new TurboGraphPPExecutionPlan(session, query);
    }


    @Override
    public DBPDataSource getDataSource() {
        return dataSource;
    }

    @NotNull
    @Override
    public DBCPlan planQueryExecution(@NotNull DBCSession session, @NotNull String query, @NotNull DBCQueryPlannerConfiguration configuration) throws DBCException {
    	return explain((JDBCSession) session, query);
    }

    @NotNull
    @Override
    public DBCPlanStyle getPlanStyle() {
        return DBCPlanStyle.PLAN;
    }

	@Override
	public void serialize(Writer planData, DBCPlan plan) throws IOException, InvocationTargetException {
		
	}

	@Override
	public DBCPlan deserialize(Reader planData) throws IOException, InvocationTargetException {
		return null;
	}

}

