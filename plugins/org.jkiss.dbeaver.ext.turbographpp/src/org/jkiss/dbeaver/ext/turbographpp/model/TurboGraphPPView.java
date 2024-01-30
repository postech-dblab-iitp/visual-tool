package org.jkiss.dbeaver.ext.turbographpp.model;

import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.ext.generic.model.GenericTable;
import org.jkiss.dbeaver.model.DBPScriptObject;
import org.jkiss.dbeaver.model.DBPScriptObjectExt2;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSEntityAssociation;
import org.jkiss.dbeaver.model.struct.DBStructUtils;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableConstraint;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableIndex;
import org.jkiss.utils.CommonUtils;

import java.util.Collection;
import java.util.Map;

public class TurboGraphPPView extends TurboGraphPPTableBase implements DBPScriptObjectExt2
{
    private static final Log log = Log.getLog(GenericTable.class);

    private String ddl;

    public TurboGraphPPView(
        TurboGraphPPStructContainer container,
        @Nullable String tableName,
        @Nullable String tableType,
        @Nullable JDBCResultSet dbResult)
    {
        super(container, tableName, tableType, dbResult);
    }

    @Override
    public boolean isView() {
        return true;
    }

    public String getDDL() {
        return ddl;
    }

    @Override
    @Property(hidden = true, editable = true, updatable = true, order = -1)
    public String getObjectDefinitionText(DBRProgressMonitor monitor, Map<String, Object> options) throws DBException {
        if (CommonUtils.getOption(options, DBPScriptObject.OPTION_REFRESH)) {
            ddl = null;
        }
        if (!isPersisted()) {
            return DBStructUtils.generateTableDDL(monitor, this, options, false);
        }

        if (ddl == null || !isCacheDDL()) {
            ddl = getDataSource().getMetaModel().getViewDDL(monitor, this, options);
        }
        return ddl;
    }

    protected boolean isCacheDDL() {
        return true;
    }

    @Override
    public boolean supportsObjectDefinitionOption(String option) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Collection<? extends DBSTableIndex> getIndexes(DBRProgressMonitor monitor) throws DBException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<? extends DBSTableConstraint> getConstraints(DBRProgressMonitor monitor) throws DBException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<? extends DBSEntityAssociation> getAssociations(DBRProgressMonitor monitor) throws DBException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<? extends DBSEntityAssociation> getReferences(DBRProgressMonitor monitor) throws DBException {
        // TODO Auto-generated method stub
        return null;
    }


}
