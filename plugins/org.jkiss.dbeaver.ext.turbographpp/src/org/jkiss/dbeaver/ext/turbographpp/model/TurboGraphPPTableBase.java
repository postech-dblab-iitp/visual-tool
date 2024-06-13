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

import java.util.*;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.ext.generic.GenericConstants;
import org.jkiss.dbeaver.ext.turbographpp.model.meta.TurboGraphPPMetaModel;
import org.jkiss.dbeaver.model.*;
import org.jkiss.dbeaver.model.data.DBDDataFilter;
import org.jkiss.dbeaver.model.data.DBDDataReceiver;
import org.jkiss.dbeaver.model.data.DBDPseudoAttribute;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.DBCExecutionSource;
import org.jkiss.dbeaver.model.exec.DBCResultSet;
import org.jkiss.dbeaver.model.exec.DBCSession;
import org.jkiss.dbeaver.model.exec.DBCStatement;
import org.jkiss.dbeaver.model.exec.DBCStatementType;
import org.jkiss.dbeaver.model.exec.DBCStatistics;
import org.jkiss.dbeaver.model.exec.DBExecUtils;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCStatement;
import org.jkiss.dbeaver.model.impl.AbstractExecutionSource;
import org.jkiss.dbeaver.model.impl.jdbc.struct.JDBCTable;
import org.jkiss.dbeaver.model.messages.ModelMessages;
import org.jkiss.dbeaver.model.meta.Association;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.meta.PropertyLength;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sql.SQLUtils;
import org.jkiss.dbeaver.model.struct.DBSDataContainer;
import org.jkiss.dbeaver.model.struct.DBSObject;

public abstract class TurboGraphPPTableBase
        extends JDBCTable<TurboGraphPPDataSource, TurboGraphPPStructContainer>
        implements DBPRefreshableObject, DBPSystemObject, DBPScriptObject {
    private static final Log log = Log.getLog(TurboGraphPPTableBase.class);

    private String tableType;
    private boolean isSystem;
    private Long rowCount;

    public TurboGraphPPTableBase(
            TurboGraphPPStructContainer container,
            @Nullable String tableName,
            @Nullable String tableType,
            @Nullable JDBCResultSet dbResult) {
        super(container, tableName, dbResult != null);
        this.tableType = tableType;
        if (this.tableType == null) {
            this.tableType = "";
        }

        final TurboGraphPPMetaModel metaModel = container.getDataSource().getMetaModel();
        this.isSystem = metaModel.isSystemTable(this);
    }

    @Override
    public TableCache getCache() {
        return getContainer().getTableCache();
    }

    @Override
    protected boolean isTruncateSupported() {
        return false;
    }

    @Override
    public TurboGraphPPStructContainer getParentObject() {
        return getContainer().getObject();
    }

    @NotNull
    @Override
    public String getFullyQualifiedName(DBPEvaluationContext context) {
        if (isView()
                && context == DBPEvaluationContext.DDL
                && !getDataSource().getMetaModel().useCatalogInObjectNames()) {
            // [SQL Server] workaround. You can't use catalog name in operations with views.
            return DBUtils.getFullQualifiedName(getDataSource(), this);
        }
        return DBUtils.getFullQualifiedName(getDataSource(), this);
    }

    @Override
    public boolean isSystem() {
        return this.isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    @Property(viewable = true, order = 2)
    public String getTableType() {
        return tableType;
    }

    @Nullable
    @Override
    public List<? extends TurboGraphPPTableColumn> getAttributes(
            @NotNull DBRProgressMonitor monitor) throws DBException {
        return this.getContainer().getTableCache().getChildren(monitor, getContainer(), this);
    }

    @Override
    public TurboGraphPPTableColumn getAttribute(
            @NotNull DBRProgressMonitor monitor, @NotNull String attributeName) throws DBException {
        return this.getContainer()
                .getTableCache()
                .getChild(monitor, getContainer(), this, attributeName);
    }

    public void addAttribute(TurboGraphPPTableColumn column) {
        this.getContainer().getTableCache().getChildrenCache(this).cacheObject(column);
    }

    public void removeAttribute(TurboGraphPPTableColumn column) {
        this.getContainer().getTableCache().getChildrenCache(this).removeObject(column, false);
    }

    @Association
    @Nullable
    public Collection<TurboGraphPPTableBase> getSubTables() {
        return null;
    }

    @Nullable
    @Override
    @Property(
            viewable = true,
            editableExpr = "object.dataSource.metaModel.tableCommentEditable",
            updatableExpr = "object.dataSource.metaModel.tableCommentEditable",
            length = PropertyLength.MULTILINE,
            order = 100)
    public String getDescription() {
        return "";
    }

    @Override
    public DBSObject refreshObject(@NotNull DBRProgressMonitor monitor) throws DBException {
        return this.getContainer().getTableCache().refreshObject(monitor, getContainer(), this);
    }

    // Comment row count calculation - it works too long and takes a lot of resources without
    // serious reason
    @Nullable
    @Property(viewable = false, expensive = true, order = 5, category = DBConstants.CAT_STATISTICS)
    public Long getRowCount(DBRProgressMonitor monitor) {
        if (rowCount != null) {
            return rowCount;
        }
        if (isView() || !isPersisted()) {
            // Do not count rows for views
            return null;
        }
        if (Boolean.FALSE.equals(
                getDataSource()
                        .getContainer()
                        .getDriver()
                        .getDriverParameter(GenericConstants.PARAM_SUPPORTS_SELECT_COUNT))) {
            // Select count not supported
            return null;
        }
        if (rowCount == null) {
            // Query row count
            try (DBCSession session = DBUtils.openUtilSession(monitor, this, "Read row count")) {
                rowCount =
                        countData(
                                new AbstractExecutionSource(
                                        this, session.getExecutionContext(), this),
                                session,
                                null,
                                DBSDataContainer.FLAG_NONE);
            } catch (DBException e) {
                // do not throw this error - row count is optional info and some providers may fail
                log.debug("Can't fetch row count: " + e.getMessage());
                //                if (indexes != null) {
                //                    rowCount = getRowCountFromIndexes(monitor);
                //                }
            }
        }
        if (rowCount == null) {
            rowCount = -1L;
        }

        return rowCount;
    }

    @NotNull
    @Override
    public DBCStatistics readData(
            @NotNull DBCExecutionSource source,
            @NotNull DBCSession session,
            @NotNull DBDDataReceiver dataReceiver,
            @Nullable DBDDataFilter dataFilter,
            long firstRow,
            long maxRows,
            long flags,
            int fetchSize)
            throws DBCException {
        DBCStatistics statistics = new DBCStatistics();
        boolean hasLimits = firstRow >= 0 && maxRows > 0;

        DBPDataSource dataSource = session.getDataSource();
        DBRProgressMonitor monitor = session.getProgressMonitor();
        try {
            getAttributes(monitor);
        } catch (DBException e) {
            log.warn(e);
        }

        DBDPseudoAttribute rowIdAttribute =
                (flags & FLAG_READ_PSEUDO) != 0 ? DBUtils.getRowIdAttribute(this) : null;

        String tableAlias = null;

        if (rowIdAttribute != null && tableAlias == null) {
            log.warn("Can't query ROWID - table alias not supported");
            rowIdAttribute = null;
        }

        StringBuilder query = new StringBuilder(100);
        query.append("MATCH (n: ").append(getFullyQualifiedName(DBPEvaluationContext.DML));
        query.append(") RETURN n");

        SQLUtils.appendQueryConditions(dataSource, query, tableAlias, dataFilter);
        SQLUtils.appendQueryOrder(dataSource, query, tableAlias, dataFilter);

        String sqlQuery = query.toString();
        statistics.setQueryText(sqlQuery);

        monitor.subTask(ModelMessages.model_jdbc_fetch_table_data);

        try (DBCStatement dbStat =
                DBUtils.makeStatement(
                        source, session, DBCStatementType.SCRIPT, sqlQuery, firstRow, maxRows)) {
            if (monitor.isCanceled()) {
                return statistics;
            }
            if (dbStat instanceof JDBCStatement && (fetchSize > 0 || maxRows > 0)) {
                DBExecUtils.setStatementFetchSize(dbStat, firstRow, maxRows, fetchSize);
            }

            long startTime = System.currentTimeMillis();
            boolean executeResult = dbStat.executeStatement();
            statistics.setExecuteTime(System.currentTimeMillis() - startTime);
            if (executeResult) {
                DBCResultSet dbResult = dbStat.openResultSet();
                if (dbResult != null && !monitor.isCanceled()) {
                    try {
                        dataReceiver.fetchStart(session, dbResult, firstRow, maxRows);

                        DBFetchProgress fetchProgress =
                                new DBFetchProgress(session.getProgressMonitor());
                        while (dbResult.nextRow()) {
                            if (fetchProgress.isCanceled()
                                    || (hasLimits && fetchProgress.isMaxRowsFetched(maxRows))) {
                                // Fetch not more than max rows
                                break;
                            }
                            dataReceiver.fetchRow(session, dbResult);
                            fetchProgress.monitorRowFetch();
                        }
                        fetchProgress.dumpStatistics(statistics);
                    } finally {
                        // First - close cursor
                        try {
                            dbResult.close();
                        } catch (Throwable e) {
                            log.error("Error closing result set", e); // $NON-NLS-1$
                        }
                        // Then - signal that fetch was ended
                        try {
                            dataReceiver.fetchEnd(session, dbResult);
                        } catch (Throwable e) {
                            log.error("Error while finishing result set fetch", e); // $NON-NLS-1$
                        }
                    }
                }
            }
            return statistics;
        } finally {
            dataReceiver.close();
        }
    }

    public boolean isEdge() {
        return isView() ? true : false;
    }
}
