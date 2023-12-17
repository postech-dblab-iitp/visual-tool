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
package org.jkiss.dbeaver.ext.turbographpp.model.meta;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.ext.generic.GenericConstants;
import org.jkiss.dbeaver.ext.generic.model.*;
import org.jkiss.dbeaver.ext.generic.model.meta.GenericMetaObject;
import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPDataSource;
import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPStructContainer;
import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPTable;
import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPTableBase;
import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPTableColumn;
import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPUtils;
import org.jkiss.dbeaver.model.*;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCStatement;
import org.jkiss.dbeaver.model.exec.plan.DBCQueryPlanner;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCConstants;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCDataSourceInfo;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.impl.jdbc.cache.JDBCBasicDataTypeCache;
import org.jkiss.dbeaver.model.impl.jdbc.struct.JDBCDataType;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sql.SQLConstants;
import org.jkiss.dbeaver.model.sql.SQLUtils;
import org.jkiss.dbeaver.model.struct.DBSEntityConstraintType;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.model.struct.DBSObjectFilter;
import org.jkiss.dbeaver.model.struct.DBStructUtils;
import org.jkiss.dbeaver.model.struct.rdb.DBSForeignKeyModifyRule;
import org.jkiss.dbeaver.model.struct.rdb.DBSIndexType;
import org.jkiss.dbeaver.model.struct.rdb.DBSProcedureType;
import org.jkiss.utils.CommonUtils;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.*;

/**
 * Generic meta model
 */
public class TurboGraphPPMetaModel {

    private static final Log log = Log.getLog(TurboGraphPPMetaModel.class);
    private static final String DEFAULT_NULL_SCHEMA_NAME = "DEFAULT";

    // Tables types which are not actually a table
    // This is needed for some strange JDBC drivers which returns not a table objects
    // in DatabaseMetaData.getTables method (PostgreSQL especially)
    private static final Set<String> INVALID_TABLE_TYPES = new HashSet<>();

    static {
        // [JDBC: PostgreSQL]
        INVALID_TABLE_TYPES.add("INDEX");
        INVALID_TABLE_TYPES.add("SEQUENCE");
        INVALID_TABLE_TYPES.add("TYPE");
        INVALID_TABLE_TYPES.add("SYSTEM INDEX");
        INVALID_TABLE_TYPES.add("SYSTEM SEQUENCE");
        // [JDBC: SQLite]
        INVALID_TABLE_TYPES.add("TRIGGER");
    }


    TurboGraphPPMetaModelDescriptor descriptor;

    public TurboGraphPPMetaModel()
    {
    }

    public GenericMetaObject getMetaObject(String id) {
        return descriptor == null ? null : descriptor.getObject(id);
    }

    //////////////////////////////////////////////////////
    // Datasource

    public TurboGraphPPDataSource createDataSourceImpl(DBRProgressMonitor monitor, DBPDataSourceContainer container) throws DBException {
        return new TurboGraphPPDataSource(monitor, container, this);
    }

    //////////////////////////////////////////////////////
    // Misc

    public JDBCBasicDataTypeCache<GenericStructContainer, ? extends JDBCDataType> createDataTypeCache(@NotNull GenericStructContainer container) {
        return new GenericDataTypeCache(container);
    }

    public DBCQueryPlanner getQueryPlanner(@NotNull GenericDataSource dataSource) {
        return null;
    }

    public DBPErrorAssistant.ErrorPosition getErrorPosition(@NotNull Throwable error) {
        return null;
    }

    public boolean supportsUpsertStatement() {
        return false;
    }

    /**
     * Returns SQL clause for table column auto-increment.
     * Null if auto-increment is not supported.
     */
    public String getAutoIncrementClause(GenericTableColumn column) {
        return null;
    }

    public boolean useCatalogInObjectNames() {
        return true;
    }

    //////////////////////////////////////////////////////
    // Schema load

    // True if schemas can be omitted.
    // App will suppress any error during schema read then
    public boolean isSchemasOptional() {
        return true;
    }

    public boolean isSystemSchema(GenericSchema schema) {
        return false;
    }

    public List<GenericSchema> loadSchemas(JDBCSession session, GenericDataSource dataSource, GenericCatalog catalog)
        throws DBException
    {
        if (dataSource.isOmitSchema()) {
            return null;
        }
        try {
            final GenericMetaObject schemaObject = getMetaObject(GenericConstants.OBJECT_SCHEMA);
            final DBSObjectFilter schemaFilters = dataSource.getContainer().getObjectFilter(GenericSchema.class, catalog, false);

            final List<GenericSchema> tmpSchemas = new ArrayList<>();
            JDBCResultSet dbResult = null;
            boolean catalogSchemas = false, schemasFiltered = false;
            if (catalog != null) {
                try {
                    dbResult = session.getMetaData().getSchemas(
                        catalog.getName(),
                        schemaFilters != null && schemaFilters.hasSingleMask() ?
                            schemaFilters.getSingleMask() :
                            dataSource.getAllObjectsPattern());
                    catalogSchemas = true;
                } catch (Throwable e) {
                    if (isSchemasOptional()) {
                        // This method not supported (may be old driver version)
                        // Use general schema reading method
                        log.debug("Error reading schemas in catalog '" + catalog.getName() + "' - " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    } else {
                        throw e;
                    }
                }
            } else if (dataSource.isSchemaFiltersEnabled()) {
                // In some drivers (e.g. jt400) reading schemas with empty catalog leads to
                // incorrect results.
                try {
                    dbResult = session.getMetaData().getSchemas(
                        null,
                        schemaFilters != null && schemaFilters.hasSingleMask() ?
                            schemaFilters.getSingleMask() :
                            dataSource.getAllObjectsPattern());
                } catch (Throwable e) {
                    if (isSchemasOptional()) {
                        log.debug("Error reading global schemas " + " - " + e.getMessage());
                    } else {
                        throw e;
                    }
                }
            }
            if (dbResult == null) {
                dbResult = session.getMetaData().getSchemas();
            }

            try {
                while (dbResult.next()) {
                    if (session.getProgressMonitor().isCanceled()) {
                        break;
                    }
                    String schemaName = TurboGraphPPUtils.safeGetString(schemaObject, dbResult, JDBCConstants.TABLE_SCHEM);
                    if (CommonUtils.isEmpty(schemaName)) {
                        // some drivers uses TABLE_OWNER column instead of TABLE_SCHEM
                        schemaName = TurboGraphPPUtils.safeGetString(schemaObject, dbResult, JDBCConstants.TABLE_OWNER);
                    }
                    boolean nullSchema = false;
                    if (CommonUtils.isEmpty(schemaName)) {
                        if (supportsNullSchemas()) {
                            schemaName = DEFAULT_NULL_SCHEMA_NAME;
                            nullSchema = true;
                        } else {
                            continue;
                        }
                    }
                    if (schemaFilters != null && !schemaFilters.matches(schemaName)) {
                        // Doesn't match filter
                        schemasFiltered = true;
                        continue;
                    }
                    String catalogName = TurboGraphPPUtils.safeGetString(schemaObject, dbResult, JDBCConstants.TABLE_CATALOG);

                    if (!CommonUtils.isEmpty(catalogName)) {
                        if (catalog == null) {
                            if (!dataSource.isOmitCatalog()) {
                                // Invalid schema's catalog or schema without catalog (then do not use schemas as structure)
                                log.debug("Catalog name (" + catalogName + ") found for schema '" + schemaName + "' while schema doesn't have parent catalog");
                            }
                        } else if (!catalog.getName().equals(catalogName)) {
                            if (!catalogSchemas) {
                                // Just skip it - we have list of all existing schemas and this one belongs to another catalog
                                continue;
                            }
                            log.debug("Catalog name '" + catalogName + "' differs from schema's catalog '" + catalog.getName() + "'");
                        }
                    }

                    session.getProgressMonitor().subTask("Schema " + schemaName);

                    GenericSchema schema = createSchemaImpl(dataSource, catalog, schemaName);
                    if (nullSchema) {
                        schema.setVirtual(true);
                    }
                    tmpSchemas.add(schema);
                }
            } finally {
                dbResult.close();
            }
            if (tmpSchemas.isEmpty() && catalogSchemas && !schemasFiltered && dataSource.getCatalogs().size() == 1) {
                // There is just one catalog and empty schema list. Try to read global schemas
                return loadSchemas(session, dataSource, null);
            }
            if (dataSource.isOmitSingleSchema() && catalog == null && tmpSchemas.size() == 1 && (schemaFilters == null || schemaFilters.isNotApplicable())) {
                // Only one schema and no catalogs
                // Most likely it is a fake one, let's skip it
                // Anyway using "%" instead is ok
                tmpSchemas.clear();
            }
            return tmpSchemas;
        } catch (UnsupportedOperationException | SQLFeatureNotSupportedException e) {
            // Schemas are not supported
            log.debug("Can't read schema list: " + e.getMessage());
            return null;
        } catch (Throwable ex) {
            if (isSchemasOptional()) {
                // Schemas are not supported - just ignore this error
                log.warn("Can't read schema list", ex);
                return null;
            } else {
                log.error("Can't read schema list", ex);
                throw new DBException(ex, dataSource);
            }
        }
    }

    // Schema with NULL name is a valid schema [Phoenix]
    public boolean supportsNullSchemas() {
        return false;
    }

    public GenericSchema createSchemaImpl(@NotNull GenericDataSource dataSource, @Nullable GenericCatalog catalog, @NotNull String schemaName) throws DBException {
        return new GenericSchema(dataSource, catalog, schemaName);
    }

    //////////////////////////////////////////////////////
    // Catalog load

    // True if catalogs can be omitted.
    // App will suppress any error during catalog read then
    public boolean isCatalogsOptional() {
        return true;
    }

    public GenericCatalog createCatalogImpl(@NotNull GenericDataSource dataSource, @NotNull String catalogName) {
        return new GenericCatalog(dataSource, catalogName);
    }

    //////////////////////////////////////////////////////
    // Tables

    /**
     * Prepares statement which returns results with following columns (the same as in JDBC spec).
     * May also contain any other db-specific columns
     *  <OL>
     *  <LI><B>TABLE_CAT</B> String {@code =>} table catalog (may be <code>null</code>)
     *  <LI><B>TABLE_SCHEM</B> String {@code =>} table schema (may be <code>null</code>)
     *  <LI><B>TABLE_NAME</B> String {@code =>} table name
     *  <LI><B>TABLE_TYPE</B> String {@code =>} table type.  Typical types are "TABLE",
     *                  "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY",
     *                  "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
     *  <LI><B>REMARKS</B> String {@code =>} explanatory comment on the table
     *  <LI><B>TYPE_CAT</B> String {@code =>} the types catalog (may be <code>null</code>)
     *  <LI><B>TYPE_SCHEM</B> String {@code =>} the types schema (may be <code>null</code>)
     *  <LI><B>TYPE_NAME</B> String {@code =>} type name (may be <code>null</code>)
     *  <LI><B>SELF_REFERENCING_COL_NAME</B> String {@code =>} name of the designated
     *                  "identifier" column of a typed table (may be <code>null</code>)
     *  <LI><B>REF_GENERATION</B> String {@code =>} specifies how values in
     *                  SELF_REFERENCING_COL_NAME are created. Values are
     *                  "SYSTEM", "USER", "DERIVED". (may be <code>null</code>)
     *  </OL>
     */
    public JDBCStatement prepareTableLoadStatement(@NotNull JDBCSession session, @NotNull TurboGraphPPStructContainer owner, @Nullable TurboGraphPPTableBase object, @Nullable String objectName)
        throws SQLException
    {
        String tableNamePattern;
        if (object == null && objectName == null) {
            final DBSObjectFilter tableFilters = session.getDataSource().getContainer().getObjectFilter(TurboGraphPPTable.class, owner, false);

            if (tableFilters != null && tableFilters.hasSingleMask()) {
                tableNamePattern = tableFilters.getSingleMask();
                if (!CommonUtils.isEmpty(tableNamePattern)) {
                    tableNamePattern = SQLUtils.makeSQLLike(tableNamePattern);
                }
            } else {
                tableNamePattern = owner.getDataSource().getAllObjectsPattern();
            }
        } else {
            tableNamePattern = JDBCUtils.escapeWildCards(session, (object != null ? object.getName() : objectName));
        }

        return session.getMetaData().getTables(
            null,
            null,
            tableNamePattern,
            null).getSourceStatement();
    }

    public TurboGraphPPTableBase createTableImpl(@NotNull JDBCSession session, @NotNull TurboGraphPPStructContainer owner, @NotNull GenericMetaObject tableObject, @NotNull JDBCResultSet dbResult) {
        String tableName = TurboGraphPPUtils.safeGetStringTrimmed(tableObject, dbResult, JDBCConstants.TABLE_NAME);
        String tableType = TurboGraphPPUtils.safeGetStringTrimmed(tableObject, dbResult, JDBCConstants.TABLE_TYPE);

        String tableSchema = GenericUtils.safeGetStringTrimmed(tableObject, dbResult, JDBCConstants.TABLE_SCHEM);
        if (!CommonUtils.isEmpty(tableSchema) && owner.getDataSource().isOmitSchema()) {
            // Ignore tables with schema [Google Spanner]
            log.debug("Ignore table " + tableSchema + "." + tableName + " (schemas are omitted)");
            return null;
        }

        if (CommonUtils.isEmpty(tableName)) {
            log.debug("Empty table name " + (owner == null ? "" : " in container " + owner.getName()));
            return null;
        }

        if (tableType != null && INVALID_TABLE_TYPES.contains(tableType)) {
            // Bad table type. Just skip it
            return null;
        }
        if (DBUtils.isVirtualObject(owner) && !CommonUtils.isEmpty(tableSchema)) {
            // Wrong schema - this may happen with virtual schemas
            return null;
        }
        TurboGraphPPTableBase table = this.createTableImpl(
            owner,
            tableName,
            tableType,
            dbResult);
        if (table == null) {
            return null;
        }

        boolean isSystemTable = table.isSystem();
        if (isSystemTable && !owner.getDataSource().getContainer().getNavigatorSettings().isShowSystemObjects()) {
            return null;
        }
        return table;
    }

    public TurboGraphPPTableBase createTableImpl(
        TurboGraphPPStructContainer container,
        @Nullable String tableName,
        @Nullable String tableType,
        @Nullable JDBCResultSet dbResult)
    {
        return new TurboGraphPPTable(
            container,
            tableName,
            tableType,
            dbResult);
    }

    public String getViewDDL(DBRProgressMonitor monitor, GenericView sourceObject, Map<String, Object> options) throws DBException {
        return "-- View definition not available";
    }

    public String getTableDDL(DBRProgressMonitor monitor, TurboGraphPPTable sourceObject, Map<String, Object> options) throws DBException {
        return DBStructUtils.generateTableDDL(monitor, sourceObject, options, false);
    }

    public boolean supportsTableDDLSplit(GenericTableBase sourceObject) {
        return true;
    }

    // Some database (like Informix) do not support foreign key declaration as nested.
    // DDL for these tables must contain definition of FK outside main brackets (ALTER TABLE ... ADD CONSTRAINT FOREIGN KEY)
    public boolean supportNestedForeignKeys() {
        return true;
    }

    public boolean isSystemTable(TurboGraphPPTableBase table) {
        final String tableType = table.getTableType().toUpperCase(Locale.ENGLISH);
        return tableType.contains("SYSTEM");
    }

    public boolean isView(String tableType) {
        return tableType.toUpperCase(Locale.ENGLISH).contains(GenericConstants.TABLE_TYPE_VIEW);
    }

    //////////////////////////////////////////////////////
    // Table columns

    public JDBCStatement prepareTableColumnLoadStatement(@NotNull JDBCSession session, @NotNull TurboGraphPPStructContainer owner, @Nullable TurboGraphPPTableBase forTable) throws SQLException {
        return session.getMetaData().getColumns(
            null,
            null,
            forTable == null ?
            owner.getDataSource().getAllObjectsPattern() :
            JDBCUtils.escapeWildCards(session, forTable.getName()),
            getColumnNamePattern(forTable))
            .getSourceStatement();
    }

    public TurboGraphPPTableColumn createTableColumnImpl(@NotNull DBRProgressMonitor monitor, @Nullable JDBCResultSet dbResult, @NotNull TurboGraphPPTableBase table, String columnName, String typeName, int valueType, int sourceType, int ordinalPos, long columnSize, long charLength, Integer scale, Integer precision, int radix, boolean notNull, String remarks, String defaultValue, boolean autoIncrement, boolean autoGenerated) throws DBException {
        return new TurboGraphPPTableColumn(table,
            columnName,
            typeName, valueType, sourceType, ordinalPos,
            columnSize,
            charLength, scale, precision, radix, notNull,
            remarks, defaultValue, autoIncrement, autoGenerated
        );
    }

    //////////////////////////////////////////////////////
    // Constraints

    public JDBCStatement prepareUniqueConstraintsLoadStatement(@NotNull JDBCSession session, @NotNull GenericStructContainer owner, @Nullable GenericTableBase forParent)
            throws SQLException, DBException {
        return session.getMetaData().getPrimaryKeys(
            owner.getCatalog() == null ? null : owner.getCatalog().getName(),
            owner.getSchema() == null || DBUtils.isVirtualObject(owner.getSchema()) ? null : owner.getSchema().getName(),
            forParent == null ? owner.getDataSource().getAllObjectsPattern() : forParent.getName())
            .getSourceStatement();
    }

    public DBSEntityConstraintType getUniqueConstraintType(JDBCResultSet dbResult) throws DBException, SQLException {
        return DBSEntityConstraintType.PRIMARY_KEY;
    }

    public JDBCStatement prepareForeignKeysLoadStatement(@NotNull JDBCSession session, @NotNull GenericStructContainer owner, @Nullable GenericTableBase forParent) throws SQLException {
        return session.getMetaData().getImportedKeys(
                owner.getCatalog() == null ? null : owner.getCatalog().getName(),
                owner.getSchema() == null || DBUtils.isVirtualObject(owner.getSchema()) ? null : owner.getSchema().getName(),
                forParent == null ?
                        owner.getDataSource().getAllObjectsPattern() :
                        forParent.getName())
                .getSourceStatement();
    }

    public boolean isFKConstraintWordDuplicated() {
        return false;
    }

    public String generateOnDeleteFK(DBSForeignKeyModifyRule deleteRule) {
        String deleteClause = deleteRule.getClause();
        if (!CommonUtils.isEmpty(deleteClause)) {
            return "ON DELETE " + deleteClause;
        }
        return null;
    }

    public String generateOnUpdateFK(DBSForeignKeyModifyRule updateRule) {
        String updateClause = updateRule.getClause();
        if (!CommonUtils.isEmpty(updateClause)) {
            return "ON UPDATE " + updateClause;
        }
        return null;
    }
    
     //////////////////////////////////////////////////////
    // Sequences

    public boolean supportsSequences(@NotNull GenericDataSource dataSource) {
        return false;
    }

    public List<GenericSequence> loadSequences(@NotNull DBRProgressMonitor monitor, @NotNull GenericStructContainer container) throws DBException {
        return new ArrayList<>();
    }

    //////////////////////////////////////////////////////
    // Synonyms

    public boolean supportsSynonyms(@NotNull GenericDataSource dataSource) {
        return false;
    }

    public List<? extends GenericSynonym> loadSynonyms(@NotNull DBRProgressMonitor monitor, @NotNull GenericStructContainer container) throws DBException {
        return new ArrayList<>();
    }

    //////////////////////////////////////////////////////
    // Triggers

    public boolean supportsTriggers(@NotNull GenericDataSource dataSource) {
        return false;
    }

    public JDBCStatement prepareTableTriggersLoadStatement(@NotNull JDBCSession session, @NotNull GenericStructContainer genericStructContainer, @Nullable GenericTableBase forParent) throws SQLException {
        return null;
    }

    public GenericTrigger createTableTriggerImpl(@NotNull JDBCSession session, @NotNull GenericStructContainer genericStructContainer, @NotNull GenericTableBase genericTableBase, String triggerName, @NotNull JDBCResultSet resultSet) throws DBException {
        return null;
    }

    // Container triggers (not supported by default)

    public boolean supportsDatabaseTriggers(@NotNull GenericDataSource dataSource) {
        return false;
    }

    public JDBCStatement prepareContainerTriggersLoadStatement(@NotNull JDBCSession session, @Nullable GenericStructContainer forParent) throws SQLException {
        return null;
    }

    public GenericTrigger createContainerTriggerImpl(@NotNull GenericStructContainer container, @NotNull JDBCResultSet resultSet) throws DBException {
        return null;
    }

    public List<? extends GenericTrigger> loadTriggers(DBRProgressMonitor monitor, @NotNull GenericStructContainer container, @Nullable GenericTableBase table) throws DBException {
        return new ArrayList<>();
    }

    public String getTriggerDDL(@NotNull DBRProgressMonitor monitor, @NotNull GenericTrigger trigger) throws DBException {
        return "-- Source code not available";
    }

    // Comments

    public boolean isTableCommentEditable() {
        return false;
    }

    public boolean isTableColumnCommentEditable() {
        return false;
    }

    public boolean supportsNotNullColumnModifiers(DBSObject object) {
        return true;
    }

    public boolean isColumnNotNullByDefault() {
        return false;
    }

    public boolean hasProcedureSupport() {
        return false;
    }

    public boolean hasFunctionSupport() {
        return false;
    }

    public boolean supportsCheckConstraints() {
        return false;
    }

    public boolean supportsViews(@NotNull GenericDataSource dataSource) {
        DBPDataSourceInfo dataSourceInfo = dataSource.getInfo();
        return !(dataSourceInfo instanceof JDBCDataSourceInfo) ||
            ((JDBCDataSourceInfo) dataSourceInfo).supportsViews();
    }
    
    private String getColumnNamePattern(TurboGraphPPTableBase forTable) {
    if (forTable == null) {
        return "node";
    } else {
        if (isView(forTable.getTableType())) {
            return "edge";
        }
    }
    return "node";
}
}
