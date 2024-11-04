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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.ext.generic.model.GenericDataSource;
import org.jkiss.dbeaver.ext.generic.model.GenericView;
import org.jkiss.dbeaver.ext.turbographpp.model.meta.TurboGraphPPMetaModel;
import org.jkiss.dbeaver.model.DBPDataSourceContainer;
import org.jkiss.dbeaver.model.DBPDataSourceInfo;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCDatabaseMetaData;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSObject;

public class TurboGraphPPDataSource extends GenericDataSource {

    private boolean isTurboGraph = false;
    private DBPDataSourceInfo dataSourceInfo;
    private List<? extends TurboGraphPPView> edges;
    private List<? extends TurboGraphPPTable> nodes;

    public TurboGraphPPDataSource(
            DBRProgressMonitor monitor,
            DBPDataSourceContainer container,
            TurboGraphPPMetaModel metaModel,
            TurboPPSQLDialect dialect)
            throws DBException {
        super(monitor, container, metaModel, dialect);
        if (container.getDriver().getDriverClassName().contains("turbograph")) {
            isTurboGraph = true;
        }
    }

    public boolean isTurboGraph() {
        return isTurboGraph;
    }

    @Override
    protected DBPDataSourceInfo createDataSourceInfo(
            DBRProgressMonitor monitor, JDBCDatabaseMetaData metaData) {
        final TurboGraphPPDataSourceInfo info =
                new TurboGraphPPDataSourceInfo(getContainer().getDriver(), metaData);
        return info;
    }

    @Override
    public DBSObject refreshObject(DBRProgressMonitor monitor) throws DBException {
        this.edges = null;
        return super.refreshObject(monitor);
    }
    
    public List<? extends TurboGraphPPView> getEdges(DBRProgressMonitor monitor) throws DBException {
        if (edges == null) {
            edges = loadEdges(monitor);
        }
        return edges;
    }
    
    private TurboGraphPPView getEdge(DBRProgressMonitor monitor, String edgeName) throws DBException {
        if (edges != null) {
            Iterator itr = edges.iterator();
            while(itr.hasNext()) {
                TurboGraphPPView edge = (TurboGraphPPView) itr.next();
                if (edge.getName().equals(edgeName)) {
                    return edge;
                }
            }
        }
        return null;
    }
    
    private List<? extends TurboGraphPPView> loadEdges(DBRProgressMonitor monitor) throws DBException {
        if (edges != null) {
            return (List<? extends TurboGraphPPView>) edges;
        }
        
        try (JDBCSession session = DBUtils.openMetaSession(monitor, this, "Load Edges")) {
            try (JDBCPreparedStatement dbStat =
                    session.prepareStatement("CALL db.relationshipTypes()")) {
                try (JDBCResultSet dbResult = dbStat.executeQuery()) {
                    List<Neo4jEdge> edgeList = new ArrayList<Neo4jEdge>();
                    while (dbResult.next()) {
                        String edgeType = JDBCUtils.safeGetString(dbResult, "relationshipType");
                        Neo4jEdge neo4jEdge = new Neo4jEdge(this.getObject(), edgeType, dbResult);
                        edgeList.add(neo4jEdge);
                    }
                    return edgeList;
                }
            }
        } catch (SQLException ex) {
            throw new DBException(ex, this);
        }
    }
    
    @Override
    public List<? extends GenericView> getViews(DBRProgressMonitor monitor) throws DBException {
        if (!this.isTurboGraph) {
            return getEdges(monitor);
        }
        return super.getViews(monitor);
    }
    
    @Override
    public Collection<? extends DBSObject> getChildren(DBRProgressMonitor monitor)
            throws DBException {
        List<Object> ret = new ArrayList<Object>();
        ret.addAll(super.getChildren(monitor));
        if (!this.isTurboGraph) {
            ret.addAll(getEdges(monitor));
        }
        return (Collection<? extends DBSObject>) ret;
    }
    
    @Override
    public DBSObject getChild(DBRProgressMonitor monitor, String childName) throws DBException {
        if (!this.isTurboGraph) {
            DBSObject obj = this.getEdge(monitor, childName);
            if (obj == null) {
                obj = super.getChild(monitor, childName);
            }
            return obj;
        } 
        return super.getChild(monitor,childName);
    } 
}
