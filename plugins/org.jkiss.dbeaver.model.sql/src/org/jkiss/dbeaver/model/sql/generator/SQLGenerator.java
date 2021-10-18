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
package org.jkiss.dbeaver.model.sql.generator;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPEvaluationContext;
import org.jkiss.dbeaver.model.DBPObject;
import org.jkiss.dbeaver.model.DBPScriptObject;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.runtime.DBRRunnableWithResult;
import org.jkiss.dbeaver.model.sql.registry.SQLGeneratorConfigurationRegistry;
import org.jkiss.dbeaver.model.sql.registry.SQLGeneratorDescriptor;
import org.jkiss.dbeaver.model.struct.DBSEntity;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class SQLGenerator<OBJECT> extends DBRRunnableWithResult<String> {
    protected List<OBJECT> objects;
    private boolean fullyQualifiedNames = true;
    private boolean compactSQL = false;
    private boolean showComments = true;
    private boolean showPermissions = false;
    private boolean showFullDdl = false;
    private boolean excludeAutoGeneratedColumn = false;
    private boolean useCustomDataFormat = false;

    private final Map<String, Object> generatorOptions = new LinkedHashMap<>();

    public void initGenerator(List<OBJECT> objects) {
        this.objects = objects;
    }

    public List<OBJECT> getObjects() {
        return objects;
    }

    public boolean isFullyQualifiedNames() {
        return fullyQualifiedNames;
    }

    public void setFullyQualifiedNames(boolean fullyQualifiedNames) {
        this.fullyQualifiedNames = fullyQualifiedNames;
    }

    public boolean isCompactSQL() {
        return compactSQL;
    }

    public void setCompactSQL(boolean compactSQL) {
        this.compactSQL = compactSQL;
    }

    public boolean isShowComments() {
        return showComments;
    }

    public void setShowComments(boolean showComments) {
        this.showComments = showComments;
    }

    public boolean isIncludePermissions() {
        return showPermissions;
    }

    public void setShowPermissions(boolean showPermissions) {
        this.showPermissions = showPermissions;
    }

    public boolean isShowFullDdl() {
        return showFullDdl;
    }

    public void setShowFullDdl(boolean showFullDdl) {
        this.showFullDdl = showFullDdl;
    }

    public boolean isExcludeAutoGeneratedColumn() {
        return excludeAutoGeneratedColumn;
    }

    public void setExcludeAutoGeneratedColumn(boolean excludeAutoGeneratedColumn) {
        this.excludeAutoGeneratedColumn = excludeAutoGeneratedColumn;
    }

    public boolean isUseCustomDataFormat() {
        return useCustomDataFormat;
    }

    public void setUseCustomDataFormat(boolean useCustomDataFormat) {
        this.useCustomDataFormat = useCustomDataFormat;
    }

    public boolean isDDLOption() {
        return false;
    }

    public boolean isDMLOption() {
        return false;
    }

    public boolean isInsertOption() {
        return false;
    }

    public Object getGeneratorOption(String name) {
        return generatorOptions.get(name);
    }

    public void setGeneratorOption(String name, Object value) {
        if (value == null) {
            generatorOptions.remove(name);
        } else {
            generatorOptions.put(name, value);
        }
    }

    protected String getLineSeparator() {
        return compactSQL ? " " : "\n";
    }

    protected String getEntityName(DBSEntity entity) {
        if (fullyQualifiedNames) {
            return DBUtils.getObjectFullName(entity, DBPEvaluationContext.DML);
        } else {
            return DBUtils.getQuotedIdentifier(entity);
        }
    }

    protected void addOptions(Map<String, Object> options) {
        options.put(DBPScriptObject.OPTION_DDL_SOURCE, true);
        options.put(DBPScriptObject.OPTION_FULLY_QUALIFIED_NAMES, isFullyQualifiedNames());
        options.put(DBPScriptObject.OPTION_SCRIPT_FORMAT_COMPACT, isCompactSQL());
        options.put(DBPScriptObject.OPTION_INCLUDE_PERMISSIONS, isIncludePermissions());
        options.put(DBPScriptObject.OPTION_INCLUDE_COMMENTS, isShowComments());
        options.put(DBPScriptObject.OPTION_INCLUDE_NESTED_OBJECTS, isShowFullDdl());
        options.put(DBPScriptObject.OPTION_SCRIPT_EXCLUDE_AUTO_GENERATED_COLUMN, isExcludeAutoGeneratedColumn());
        options.put(DBPScriptObject.OPTION_SCRIPT_USE_CUSTOM_DATA_FORMAT, isUseCustomDataFormat());
        options.putAll(generatorOptions);
    }

    @Override
    public void run(DBRProgressMonitor monitor) throws InvocationTargetException, InterruptedException
    {
        SQLGeneratorDescriptor descriptor = SQLGeneratorConfigurationRegistry.getInstance().getGeneratorDescriptor(this);
        StringBuilder sql = new StringBuilder(100);
        try {
            for (OBJECT object : objects) {
                if (!(object instanceof DBPObject) || descriptor.appliesTo((DBPObject) object)) {
                    generateSQL(monitor, sql, object);
                }
            }
        } catch (DBException e) {
            throw new InvocationTargetException(e);
        }
        result = sql.toString();
    }

    protected abstract void generateSQL(DBRProgressMonitor monitor, StringBuilder sql, OBJECT object)
        throws DBException;

}
