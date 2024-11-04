/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2024 DBeaver Corp and others
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.ext.generic.model.GenericStructContainer;
import org.jkiss.dbeaver.ext.generic.model.GenericTableColumn;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSObject;

public class Neo4jEdge extends TurboGraphPPView {

    private final TurboGraphPPDataSource dataSource;
    private String edgeType;
    private List<TurboGraphPPTableColumn> properties;

    public Neo4jEdge(GenericStructContainer container, String edgeType, JDBCResultSet resultSet) {
        super(container, edgeType, "Edge", resultSet);
        this.edgeType = edgeType;
        this.dataSource = (TurboGraphPPDataSource) container.getDataSource();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Neo4jEdge) {
            Neo4jEdge temp = (Neo4jEdge) obj;
            return dataSource.equals(temp.dataSource) && edgeType.equals(temp.edgeType);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(dataSource, edgeType);
    }

    @NotNull
    @Override
    @Property(viewable = true, order = 1)
    public String getName() {
        return edgeType;
    }
    
    @Override
    @Property(viewable = true, order = 2)
    public String getTableType() {
        return super.getTableType();
    }

    public void setEdgeType(String edgeType) {
        this.edgeType = edgeType;
    }

    @Nullable
    @Override
    public String getDescription() {
        return null;
    }

    @NotNull
    @Override
    public TurboGraphPPDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public DBSObject refreshObject(DBRProgressMonitor monitor) throws DBException {
        properties = null;
        return this;
    }

    @Nullable
    @Override
    public List<TurboGraphPPTableColumn> getAttributes(@NotNull DBRProgressMonitor monitor)
            throws DBException {
        return getProperties(monitor);
    }

    @Override
    public GenericTableColumn getAttribute(
            @NotNull DBRProgressMonitor monitor, @NotNull String attributeName) throws DBException {
        if (properties != null) {
            Iterator itr = properties.iterator();
            while (itr.hasNext()) {
                Neo4jProperty property = (Neo4jProperty) itr.next();
                if (property.getName() == attributeName) {
                    return property;
                }
            }
        }
        return null;
    }

    private List<TurboGraphPPTableColumn> getProperties(DBRProgressMonitor monitor) throws DBException {
        if (this.properties != null) {
            return this.properties;
        }

        try (JDBCSession session = DBUtils.openMetaSession(monitor, this, "Load Edges Propeties")) {
            String gql =
                    "MATCH ()-[r:" + edgeType + "]-() WITH r LIMIT 200 RETURN DISTINCT keys(r)";
            try (JDBCPreparedStatement dbStat = session.prepareStatement(gql)) {
                try (JDBCResultSet dbResult = dbStat.executeQuery()) {
                    // List<TurboGraphPPEdge> edgeList = new ArrayList<>();
                    String edgesPropery = null;
                    Set<Neo4jProperty> setList = new HashSet<>();
                    properties = null;
                    while (dbResult.next()) {
                        edgesPropery = JDBCUtils.safeGetString(dbResult, "keys(r)");
                        if (edgesPropery != null && !edgesPropery.equals("[]")) {
                            edgesPropery = edgesPropery.replace(String.valueOf('['), "");
                            edgesPropery = edgesPropery.replace(String.valueOf(']'), "");
                            String[] propertiesList = edgesPropery.split(", ");
                            for (int i = 0; i < propertiesList.length; i++) {
                                propertiesList[i] = propertiesList[i].replaceAll("\"", "");
                                setList.add(new Neo4jProperty(this, propertiesList[i]));
                            }
                        }
                    }
                    properties = new ArrayList<>(setList);
                    return properties;
                }
            }
        } catch (SQLException ex) {
            // throw new DBException(ex, this);
            throw new DBException(ex.toString());
        }
    }
}
