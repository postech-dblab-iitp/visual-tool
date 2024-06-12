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

import java.sql.ResultSet;
import java.util.Objects;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSObject;

/** User privilege grant */
public class Neo4jEdgesProperty implements DBSObject {

    private final Neo4jEdge edges;
    private final String edgesPropery;

    public Neo4jEdgesProperty(ResultSet resultSet, Neo4jEdge edges, String property) {
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
        if (obj instanceof Neo4jEdgesProperty) {
            Neo4jEdgesProperty temp = (Neo4jEdgesProperty) obj;
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
    public Neo4jEdge getParentObject() {
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

    public Neo4jEdge getSubject(DBRProgressMonitor monitor) {
        return edges;
    }
}
