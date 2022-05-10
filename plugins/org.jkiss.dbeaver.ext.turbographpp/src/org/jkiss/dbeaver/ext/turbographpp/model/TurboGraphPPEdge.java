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
import org.jkiss.dbeaver.model.DBPRefreshableObject;
import org.jkiss.dbeaver.model.DBPSaveableObject;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

//public class TurboGraphPPEdge implements DBSEntity, DBPRefreshableObject, DBPSaveableObject {
public class TurboGraphPPEdge implements DBSObject, DBPRefreshableObject, DBPSaveableObject {

    private static final Log log = Log.getLog(TurboGraphPPEdge.class);

    private final TurboGraphPPDataSource dataSource;
    private String edgeType;
    private Set<TurboGraphPPEdgesProperty> properties;
    private boolean persisted;

    public TurboGraphPPEdge(TurboGraphPPDataSource dataSource, ResultSet resultSet) {
        this.dataSource = dataSource;
        if (resultSet != null) {
            this.edgeType = JDBCUtils.safeGetString(resultSet, "type(r)");
        } else {
            this.edgeType = "";
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof TurboGraphPPEdge) {
            TurboGraphPPEdge temp = (TurboGraphPPEdge)obj;
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

    public void setEdgeType(String edgeType) {
        this.edgeType = edgeType;
    }

    @Nullable
    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public DBSObject getParentObject() {
        return dataSource.getContainer();
    }

    @NotNull
    @Override
    public TurboGraphPPDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public boolean isPersisted() {
        return persisted;
    }

    @Override
    public DBSObject refreshObject(DBRProgressMonitor monitor) throws DBException {
        properties = null;
        return this;
    }
    
    public Set<TurboGraphPPEdgesProperty> getProperties(DBRProgressMonitor monitor) throws DBException
    {
        if (this.properties != null) {
            return this.properties;
        }
       
        try (JDBCSession session = DBUtils.openMetaSession(monitor, this, "Load Edges Propeties")) {
            String gql = "Match (n)-[r]->(m) where type(r) = '" + edgeType + "' Return keys(r)";
            try (JDBCPreparedStatement dbStat = session.prepareStatement(gql)) {
                try (JDBCResultSet dbResult = dbStat.executeQuery()) {
                    //List<TurboGraphPPEdge> edgeList = new ArrayList<>();
                    Set<TurboGraphPPEdgesProperty> propertyList = new HashSet<>();
                    while (dbResult.next()) {
                        TurboGraphPPEdgesProperty properties = new TurboGraphPPEdgesProperty(dbResult, this, null);
                        if (properties.getName() != null) {
                            String[] propertiesList = properties.getName().split(", ");
                            for (int i = 0 ; i < propertiesList.length ; i++) {
                                propertyList.add(new TurboGraphPPEdgesProperty(dbResult, this, propertiesList[i]));
                            }
                        }
                    }
                    return propertyList;
                }
            }
        } catch (SQLException ex) {
            //throw new DBException(ex, this);
            throw new DBException(ex.toString());
        }
    }
    
    @Override
    public void setPersisted(boolean persisted)
    {
        this.persisted = persisted;
        DBUtils.fireObjectUpdate(this);
    }
    
    public void clearPropertiesCache()
    {
        this.properties = null;
    }
}
