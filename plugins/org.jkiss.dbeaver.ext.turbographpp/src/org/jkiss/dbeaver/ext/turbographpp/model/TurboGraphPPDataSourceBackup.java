/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2021 DBeaver Corp and others
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

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.DBPDataSourceContainer;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCDataSource;
import org.jkiss.dbeaver.model.impl.jdbc.cache.JDBCBasicDataTypeCache;
import org.jkiss.dbeaver.model.impl.jdbc.struct.JDBCDataType;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSDataType;
import org.jkiss.dbeaver.model.struct.DBSObject;

public class TurboGraphPPDataSourceBackup extends JDBCDataSource {

    private final JDBCBasicDataTypeCache<TurboGraphPPDataSourceBackup, JDBCDataType> dataTypeCache;
    private Set<TurboGraphPPEdge> edges;

    public TurboGraphPPDataSourceBackup(DBRProgressMonitor monitor, DBPDataSourceContainer container)
            throws DBException {
        super(monitor, container, new TurboPPSQLDialect());
        dataTypeCache = new JDBCBasicDataTypeCache<>(this);
    }

    @Override
    public DBSObject refreshObject(@NotNull DBRProgressMonitor monitor) throws DBException {
        super.refreshObject(monitor);
        this.edges = null;
        this.initialize(monitor);

        return this;
    }

    public Set<TurboGraphPPEdge> getEdges(DBRProgressMonitor monitor) throws DBException {
        if (edges == null) {
            edges = loadEdges(monitor);
        }
        return edges;
    }

    public TurboGraphPPEdge getEdges(DBRProgressMonitor monitor, String name) throws DBException {
        return DBUtils.findObject(getEdges(monitor), name);
    }

    private Set<TurboGraphPPEdge> loadEdges(DBRProgressMonitor monitor) throws DBException {
        try (JDBCSession session = DBUtils.openMetaSession(monitor, this, "Load Edges")) {
            try (JDBCPreparedStatement dbStat = session.prepareStatement("Match (n)-[r]->(m) Return type(r)")) {
                try (JDBCResultSet dbResult = dbStat.executeQuery()) {
                    //List<TurboGraphPPEdge> edgeList = new ArrayList<>();
                    Set<TurboGraphPPEdge> edgeList = new HashSet<>();
                    while (dbResult.next()) {
                        //TurboGraphPPEdge user = new TurboGraphPPEdge(this, dbResult);
                        //edgeList.add(user);
                    }
                    return edgeList;
                }
            }
        } catch (SQLException ex) {
            throw new DBException(ex, this);
        }
    }
    
    @Override
    public void initialize(@NotNull DBRProgressMonitor monitor)
        throws DBException
    {
        super.initialize(monitor);
        dataTypeCache.getAllObjects(monitor, this);
        dataTypeCache.cacheObject(new JDBCDataType<>(this, java.sql.Types.OTHER, "json", "json", false, false, 0, 0, 0));
        loadEdges(monitor);
    }

    @Override
    public DBPDataSource getDataSource() {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public Collection<? extends DBSDataType> getLocalDataTypes() {
        // TODO Auto-generated method stub
        return dataTypeCache.getCachedObjects();
    }

    @Override
    public DBSDataType getLocalDataType(String typeName) {
        // TODO Auto-generated method stub
        return dataTypeCache.getCachedObject(typeName);
    }

    @Override
    public Collection<? extends DBSObject> getChildren(DBRProgressMonitor monitor) throws DBException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DBSObject getChild(DBRProgressMonitor monitor, String childName) throws DBException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<? extends DBSObject> getPrimaryChildType(DBRProgressMonitor monitor) throws DBException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void cacheStructure(DBRProgressMonitor monitor, int scope) throws DBException {
        // TODO Auto-generated method stub
        
    }
}
