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
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.ext.generic.GenericConstants;
import org.jkiss.dbeaver.ext.generic.model.meta.GenericMetaObject;
import org.jkiss.dbeaver.ext.turbographpp.model.meta.TurboGraphPPMetaModel;
import org.jkiss.dbeaver.model.DBPDataSourceContainer;
import org.jkiss.dbeaver.model.DBPIdentifierCase;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.connection.DBPDriver;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCDatabaseMetaData;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCConstants;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCDataSource;
import org.jkiss.dbeaver.model.impl.jdbc.cache.JDBCBasicDataTypeCache;
import org.jkiss.dbeaver.model.impl.jdbc.struct.JDBCDataType;
import org.jkiss.dbeaver.model.meta.Association;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSDataType;
import org.jkiss.dbeaver.model.struct.DBSEntity;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.model.struct.DBSObjectFilter;
import org.jkiss.utils.CommonUtils;

public class TurboGraphPPDataSource extends JDBCDataSource implements TurboGraphPPStructContainer {

    private static final Log log = Log.getLog(TurboGraphPPDataSource.class);
    
    private final JDBCBasicDataTypeCache<TurboGraphPPDataSource, JDBCDataType> dataTypeCache;
    private TurboGraphPPObjectContainer structureContainer;
    private final TurboGraphPPMetaModel metaModel;
    private boolean omitSingleCatalog;
    private String allObjectsPattern;
    private Set<TurboGraphPPEdge> edges;

    public TurboGraphPPDataSource(DBRProgressMonitor monitor, DBPDataSourceContainer container, TurboGraphPPMetaModel metaModel)
            throws DBException {
        super(monitor, container, new TurboPPSQLDialect());
        this.metaModel = metaModel;
        dataTypeCache = new JDBCBasicDataTypeCache<>(this);
        final DBPDriver driver = container.getDriver();
        this.omitSingleCatalog = CommonUtils.getBoolean(driver.getDriverParameter(GenericConstants.PARAM_OMIT_SINGLE_CATALOG), false);
        this.allObjectsPattern = CommonUtils.toString(driver.getDriverParameter(GenericConstants.PARAM_ALL_OBJECTS_PATTERN));
        if (CommonUtils.isEmpty(this.allObjectsPattern)) {
            this.allObjectsPattern = "%";
        } else if ("null".equalsIgnoreCase(this.allObjectsPattern)) {
            this.allObjectsPattern = null;
        }
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
                        TurboGraphPPEdge user = new TurboGraphPPEdge(this, dbResult);
                        edgeList.add(user);
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
        this.structureContainer = new DataSourceObjectContainer();
        loadEdges(monitor);
    }

    @NotNull
    public TurboGraphPPMetaModel getMetaModel() {
        return metaModel;
    }

    @Nullable
    public GenericMetaObject getMetaObject(String id) {
        return metaModel.getMetaObject(id);
    }
    
    @Override
    public TurboGraphPPDataSource getDataSource() {
        // TODO Auto-generated method stub
        return this;
    }

    protected JDBCBasicDataTypeCache<TurboGraphPPDataSource, JDBCDataType> getDataTypeCache() {
        return dataTypeCache;
    }

    public Collection<? extends DBSDataType> getDataTypes(DBRProgressMonitor monitor) throws DBException {
        return dataTypeCache.getAllObjects(monitor, this);
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
        if (structureContainer != null) {
            return structureContainer.getTables(monitor);
        } else {
            return null;
        }    
    }

    @Override
    public DBSObject getChild(DBRProgressMonitor monitor, String childName) throws DBException {
        // TODO Auto-generated method stub
        if (structureContainer != null) {
            return structureContainer.getChild(monitor, childName);
        } else {
            return null;
        }
    }

    @Override
    public Class<? extends DBSObject> getPrimaryChildType(DBRProgressMonitor monitor) throws DBException {
        // TODO Auto-generated method stub
        return TurboGraphPPTable.class;
    }

    @Override
    public void cacheStructure(DBRProgressMonitor monitor, int scope) throws DBException {
        if (structureContainer != null) {
            structureContainer.cacheStructure(monitor, scope);
        }
    }

    @Override
    public TurboGraphPPStructContainer getObject() {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public TableCache getTableCache() {
        // TODO Auto-generated method stub
        return structureContainer == null ? null : structureContainer.getTableCache();
    }

    @Override
    public List<? extends TurboGraphPPTable> getPhysicalTables(DBRProgressMonitor monitor) throws DBException {
        // TODO Auto-generated method stub
        return structureContainer == null ? null : structureContainer.getPhysicalTables(monitor);
    }

    @Override
    public List<? extends TurboGraphPPTableBase> getTables(DBRProgressMonitor monitor) throws DBException {
        // TODO Auto-generated method stub
        return structureContainer == null ? null : structureContainer.getTables(monitor);
    }

    @Override
    public TurboGraphPPTableBase getTable(DBRProgressMonitor monitor, String name) throws DBException {
        // TODO Auto-generated method stub
        return structureContainer == null ? null : structureContainer.getTable(monitor, name);
    }

    public String getAllObjectsPattern() {
        // TODO Auto-generated method stub
        return allObjectsPattern;
    }
    
    public boolean isOmitSchema() {
        return CommonUtils.getBoolean(getContainer().getDriver().getDriverParameter(GenericConstants.PARAM_OMIT_SCHEMA), false);
    }
    
    public boolean isOmitCatalog() {
        return CommonUtils.getBoolean(getContainer().getDriver().getDriverParameter(GenericConstants.PARAM_OMIT_CATALOG), false);
    }
    
    public boolean isMergeEntities() {
        return getContainer().getNavigatorSettings().isMergeEntities();
    }
    
    private class DataSourceObjectContainer extends TurboGraphPPObjectContainer {
        private DataSourceObjectContainer() {
            super(TurboGraphPPDataSource.this);
        }

        @Override
        public TurboGraphPPStructContainer getObject() {
            return TurboGraphPPDataSource.this;
        }

        @NotNull
        @Override
        public Class<? extends DBSEntity> getPrimaryChildType(@Nullable DBRProgressMonitor monitor) throws DBException {
            return TurboGraphPPTable.class;
        }

        @NotNull
        @Override
        public String getName() {
            return TurboGraphPPDataSource.this.getName();
        }

        @Nullable
        @Override
        public String getDescription() {
            return TurboGraphPPDataSource.this.getDescription();
        }

        @Override
        public DBSObject getParentObject() {
            return TurboGraphPPDataSource.this.getParentObject();
        }

        @Override
        public void cacheStructure(DBRProgressMonitor monitor, int scope) throws DBException {
            // TODO Auto-generated method stub
            
        }
    }
    

}