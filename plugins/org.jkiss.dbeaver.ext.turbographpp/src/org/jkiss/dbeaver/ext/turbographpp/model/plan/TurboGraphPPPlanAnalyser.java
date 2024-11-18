/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2024 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
