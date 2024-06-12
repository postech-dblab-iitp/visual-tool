package org.jkiss.dbeaver.ext.turbographpp.model.plan;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPDataSource;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.DBCSession;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.exec.plan.*;
import org.jkiss.dbeaver.model.impl.plan.AbstractExecutionPlanSerializer;

public class TurboGraphPPPlanAnalyser extends AbstractExecutionPlanSerializer
        implements DBCQueryPlanner {

    private TurboGraphPPDataSource dataSource;

    public TurboGraphPPPlanAnalyser(TurboGraphPPDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public TurboGraphPPExecutionPlan explain(JDBCSession session, String query)
            throws DBCException {
        return new TurboGraphPPExecutionPlan(session, query);
    }

    @Override
    public DBPDataSource getDataSource() {
        return dataSource;
    }

    @NotNull
    @Override
    public DBCPlan planQueryExecution(
            @NotNull DBCSession session,
            @NotNull String query,
            @NotNull DBCQueryPlannerConfiguration configuration)
            throws DBCException {
        return explain((JDBCSession) session, query);
    }

    @NotNull
    @Override
    public DBCPlanStyle getPlanStyle() {
        return DBCPlanStyle.PLAN;
    }

    @Override
    public void serialize(Writer planData, DBCPlan plan)
            throws IOException, InvocationTargetException {}

    @Override
    public DBCPlan deserialize(Reader planData) throws IOException, InvocationTargetException {
        return null;
    }
}
