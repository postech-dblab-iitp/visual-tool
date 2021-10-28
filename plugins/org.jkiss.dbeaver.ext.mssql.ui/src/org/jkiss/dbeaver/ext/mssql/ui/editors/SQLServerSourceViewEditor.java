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

package org.jkiss.dbeaver.ext.mssql.ui.editors;

import org.jkiss.dbeaver.ext.mssql.model.SQLServerProcedure;
import org.jkiss.dbeaver.ext.mssql.model.SQLServerTableTrigger;
import org.jkiss.dbeaver.ext.mssql.model.SQLServerView;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSObjectWithScript;
import org.jkiss.dbeaver.ui.editors.sql.SQLSourceViewer;

/**
 * SQLServerSourceViewEditor
 */
public class SQLServerSourceViewEditor extends SQLSourceViewer<DBSObjectWithScript> {

    public SQLServerSourceViewEditor()
    {
    }

    @Override
    protected boolean isReadOnly()
    {
        DBSObjectWithScript sourceObject = getSourceObject();
        if (sourceObject instanceof SQLServerView || sourceObject instanceof SQLServerTableTrigger || sourceObject instanceof SQLServerProcedure) {
            return false;
        }
        return true;
    }

    @Override
    protected void setSourceText(DBRProgressMonitor monitor, String sourceText)
    {
        getInputPropertySource().setPropertyValue(monitor, "objectDefinitionText", sourceText);
    }

}

