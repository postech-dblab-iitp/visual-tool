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

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.model.DBPEvaluationContext;
import org.jkiss.dbeaver.model.DBPRefreshableObject;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCStatement;
import org.jkiss.dbeaver.model.impl.jdbc.cache.JDBCObjectCache;
import org.jkiss.dbeaver.model.meta.Association;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSDataType;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.model.struct.rdb.DBSProcedureType;
import org.jkiss.utils.CommonUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * GenericEntityContainer
 */
public abstract class TurboGraphPPObjectContainer implements TurboGraphPPStructContainer, DBPRefreshableObject {
    private static final Log log = Log.getLog(TurboGraphPPObjectContainer.class);

    @NotNull
    private final TurboGraphPPDataSource dataSource;
    private final TableCache tableCache;

    protected TurboGraphPPObjectContainer(@NotNull TurboGraphPPDataSource dataSource) {
        this.dataSource = dataSource;
        this.tableCache = createTableCache(dataSource);
    }

    public TableCache createTableCache(TurboGraphPPDataSource datasource) {
        return new TableCache(datasource);
    }

    @Override
    public final TableCache getTableCache() {
        return tableCache;
    }

    @NotNull
    @Override
    public TurboGraphPPDataSource getDataSource() {
        return dataSource;
    }


    @Override
    public boolean isPersisted() {
        return true;
    }

    @Override
    public List<? extends TurboGraphPPTable> getPhysicalNode(DBRProgressMonitor monitor) throws DBException {
        List<? extends TurboGraphPPTableBase> tables = getTables(monitor);
        if (tables != null) {
            List<TurboGraphPPTable> filtered = new ArrayList<>();
            for (TurboGraphPPTableBase table : tables) {
                if (table.isPhysicalTable() && table.isNode()) {
                    filtered.add((TurboGraphPPTable) table);
                }
            }
            return filtered;
        }
        return null;
    }

    @Override
    public List<? extends TurboGraphPPTable> getPhysicalEdge(DBRProgressMonitor monitor) throws DBException {
        List<? extends TurboGraphPPTableBase> tables = getTables(monitor);
        if (tables != null) {
            List<TurboGraphPPTable> filtered = new ArrayList<>();
            for (TurboGraphPPTableBase table : tables) {
                if (table.isPhysicalTable() && table.isEdge()) {
                    filtered.add((TurboGraphPPTable) table);
                }
            }
            return filtered;
        }
        return null;
    }
    
    @Override
    public List<? extends TurboGraphPPTableBase> getTables(DBRProgressMonitor monitor)
        throws DBException {
        return tableCache.getAllObjects(monitor, this);
    }

    @Override
    public TurboGraphPPTableBase getTable(DBRProgressMonitor monitor, String name)
        throws DBException {
        return tableCache.getObject(monitor, this, name);
    }

    @Association
    public Collection<? extends DBSDataType> getDataTypes(DBRProgressMonitor monitor) throws DBException {
        return getDataSource().getDataTypes(monitor);
    }

    @Override
    public Collection<? extends DBSObject> getChildren(@NotNull DBRProgressMonitor monitor)
        throws DBException {
        List<DBSObject> childrenList = new ArrayList<>(getTables(monitor));
        return childrenList;
    }

    @Override
    public DBSObject getChild(@NotNull DBRProgressMonitor monitor, @NotNull String childName)
        throws DBException {
        return getTable(monitor, childName);
    }

    @Override
    public synchronized DBSObject refreshObject(@NotNull DBRProgressMonitor monitor)
        throws DBException {
        this.tableCache.clearCache();
        return this;
    }

    public String toString() {
        return getName() == null ? "<NONE>" : getName();
    }

}
