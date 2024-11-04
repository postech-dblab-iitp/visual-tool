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
package org.jkiss.dbeaver.ext.turbographpp.model;

import java.util.List;
import java.util.Map;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.ext.generic.model.GenericStructContainer;
import org.jkiss.dbeaver.ext.generic.model.GenericTable;
import org.jkiss.dbeaver.ext.generic.model.GenericView;
import org.jkiss.dbeaver.model.data.DBDDataFilter;
import org.jkiss.dbeaver.model.data.DBDDataReceiver;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.DBCExecutionSource;
import org.jkiss.dbeaver.model.exec.DBCSession;
import org.jkiss.dbeaver.model.exec.DBCStatistics;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;

public class TurboGraphPPView extends GenericView {

    public TurboGraphPPView(
            GenericStructContainer container,
            String tableName,
            String tableType,
            JDBCResultSet dbResult) {
        super(container,tableName,tableType,dbResult);
    }
    
    @Override
    public boolean isView() {
        return true;
    }
    
    @Override
    public List<TurboGraphPPTableColumn> getAttributes(DBRProgressMonitor monitor)
            throws DBException {
        return (List<TurboGraphPPTableColumn>) super.getAttributes(monitor);
    }
    
    @Override
    public DBCStatistics readData(
            DBCExecutionSource source,
            DBCSession session,
            DBDDataReceiver dataReceiver,
            DBDDataFilter dataFilter,
            long firstRow,
            long maxRows,
            long flags,
            int fetchSize)
            throws DBCException {
        throw new DBCException("Edge cannot be viewed independently.");
    }
    
    @Override
    @Property(viewable = true, order = 1)
    public String getName() {
        return super.getName();
    }
    
    @Override
    @Property(viewable = true, order = 2)
    public String getTableType() {
        return "Edge";
    }
    
    @Override
    @Property(viewable = false)
    public String getDescription() {
        return super.getDescription();
    }
    
    @Override
    public String getObjectDefinitionText(DBRProgressMonitor monitor, Map<String, Object> options)
            throws DBException {
        return "-- Edge Definition not available";
    }
    
    @Override
    public String getDDL() {
        return null;
    }
}
