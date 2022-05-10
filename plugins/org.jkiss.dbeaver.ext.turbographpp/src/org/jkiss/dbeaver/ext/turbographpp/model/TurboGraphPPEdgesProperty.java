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
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSObject;

import java.sql.ResultSet;
import java.util.Objects;

/**
 * User privilege grant
 */
public class TurboGraphPPEdgesProperty implements DBSObject {

    private final TurboGraphPPEdge edges;
    private final String edgesPropery;

    public TurboGraphPPEdgesProperty(ResultSet resultSet, TurboGraphPPEdge edges, String property)
    {
        String edgesPropery;
        this.edges = edges;
        
        if (property == null) {
            if (resultSet != null) {
                edgesPropery = JDBCUtils.safeGetString(resultSet, "keys(r)");
                if (edgesPropery != null && !edgesPropery.equals("[]")) {
                    edgesPropery = edgesPropery.replace(String.valueOf('['), "");
                    edgesPropery = edgesPropery.replace(String.valueOf(']'), "");
                    this.edgesPropery = edgesPropery;
                } else {
                    this.edgesPropery = null;
                }
            } else {
                this.edgesPropery = null;
            }
        } else {
            this.edgesPropery = property;
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof TurboGraphPPEdgesProperty) {
            TurboGraphPPEdgesProperty temp = (TurboGraphPPEdgesProperty)obj;
            return this.edgesPropery.equals(temp.edgesPropery);
        }
        return false;
    }
    
    public int hashCode() {
        return Objects.hash(this.edgesPropery);
    }
    
    public static int countOfPair(String str, char search) {
        return (str.length() - str.replace(String.valueOf(search), "").length());
    }
    
    @Nullable
    @Override
    public TurboGraphPPEdge getParentObject() {
        return this.edges;
    }

    @NotNull
    @Override
    public DBPDataSource getDataSource() {
        return this.edges.getDataSource();
    }

    @Nullable
    @Override
    @Property(viewable = true, order = 1)
    public String getName() {
        return edgesPropery;
    }

    @Nullable
    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public boolean isPersisted() {
        return false;
    }

    public TurboGraphPPEdge getSubject(DBRProgressMonitor monitor) {
        return edges;
    }

}
