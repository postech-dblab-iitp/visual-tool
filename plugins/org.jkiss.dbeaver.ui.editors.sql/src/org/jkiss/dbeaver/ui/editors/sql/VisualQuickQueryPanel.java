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

package org.jkiss.dbeaver.ui.editors.sql;

import java.awt.Checkbox;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.model.DBIcon;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCDatabaseMetaData;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.runtime.AbstractJob;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.css.CSSUtils;
import org.jkiss.dbeaver.ui.css.DBStyles;
import org.jkiss.dbeaver.ui.editors.sql.internal.SQLEditorMessages;

import net.sf.jsqlparser.statement.select.First.Keyword;

class VisualQuickQueryPanel extends Composite {
    private static final Log log = Log.getLog(VisualQuickQueryPanel.class);

    private final ComboViewer vTypeCombo;
    private final ComboViewer vPropertyCombo;
    private final Text vPropertySearchText;
    private final ComboViewer eTypeCombo;
    private final ComboViewer ePropertyCombo;
    private final Text ePropertySearchText;
    private final Button edgeSelect;

    private Composite parentComposite;
    private Composite Composite;

    private final SQLEditor editor;

    private String activeDisplayName = "Quick Query";

    private LinkedHashMap<String, String> vertexList = new LinkedHashMap<>();
    private LinkedHashMap<String, String> edgeList = new LinkedHashMap<>();

    private static final String QUERY_GET_VERTEX_LABEL = "MATCH (n) RETURN distinct labels(n) AS label";
    private static final String QUERY_GET_EDGE_TYPE = "MATCH ()-[r]-() RETURN distinct type(r) AS type";
    private static final String QUERY_GET_EDGE_PROPERTIES =
            "Match ()-[r]->() where type(r) = $0 Return distinct keys(r)";

    private static final String DEFAULT_ALL = "ALL(Default)";
    
    private String vertexLabel;
    private String edgeType;

    private GetTypeInfoJob startUpdateJob;
    private ReentrantLock lock = new ReentrantLock();

    VisualQuickQueryPanel(SQLEditor editor, Composite parent) {
        super(parent, SWT.NONE);
        this.parentComposite = parent;
        this.Composite = this;
        this.editor = editor;
        final int numColumns = 9;
        
        CSSUtils.setCSSClass(this, DBStyles.COLORED_BY_CONNECTION_TYPE);

        GridLayout gl = new GridLayout(numColumns, false);
//        gl.marginHeight = 3;
//        gl.marginWidth = 3;
        this.setLayout(gl);
        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData.horizontalSpan = 3;
        gridData.exclude = true;
        this.setLayoutData(gridData);

        ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.DROP_DOWN);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 2));
        ToolItem item = new ToolItem(toolBar, SWT.NONE);
        item.setText(SQLEditorMessages.query_translation_title);
        item.setImage(DBeaverIcons.getImage(DBIcon.QUERY_TRANSFORMING_BIG));
        item.addSelectionListener(executeListner);

        Label separator = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
        separator.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 2));

        Button nodeLabel = new Button(this, SWT.CHECK);
        nodeLabel.setText(SQLEditorMessages.query_translation_node_label);
        nodeLabel.setEnabled(false);
        nodeLabel.setSelection(true);
        
        vTypeCombo = new ComboViewer(this, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SHADOW_NONE);
        vTypeCombo.setLabelProvider(new LabelProvider());
        vTypeCombo.setContentProvider(new ArrayContentProvider());
        vTypeCombo
                .getCombo()
                .setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, true));
        vTypeCombo.getCombo().add("Need to load");

        vPropertyCombo = new ComboViewer(this, SWT.DROP_DOWN | SWT.READ_ONLY);
        vPropertyCombo.setLabelProvider(new LabelProvider());
        vPropertyCombo.setContentProvider(new ArrayContentProvider());
        vPropertyCombo
                .getCombo()
                .setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, true));
        vPropertyCombo.getCombo().add("Need to load");
        vPropertySearchText = new Text(this, SWT.LEFT | SWT.BORDER);
        vPropertySearchText.setLayoutData(
                new GridData(GridData.FILL, GridData.CENTER, true, true, 2, 1));

        Button addBtn = new Button(this, SWT.CENTER);
        addBtn.setText(SQLEditorMessages.query_translation_execute);
        addBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true, 1, 2));
        addBtn.addSelectionListener(executeListner);

        Label space = new Label(this, SWT.CENTER);
        space.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
        
        edgeSelect = new Button(this, SWT.CHECK);
        edgeSelect.setText(SQLEditorMessages.query_translation_edge_title);
        edgeSelect.setSelection(true);
        
        eTypeCombo = new ComboViewer(this, SWT.DROP_DOWN | SWT.READ_ONLY);
        eTypeCombo
                .getCombo()
                .setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, true));
        eTypeCombo.setLabelProvider(new LabelProvider());
        eTypeCombo.setContentProvider(
                new IStructuredContentProvider() {
                    @Override
                    public Object[] getElements(Object inputElement) {
                        ArrayList list = (ArrayList) inputElement;
                        return list.toArray();
                    }
                });
        eTypeCombo.getCombo().add("Need to load");

        ePropertyCombo = new ComboViewer(this, SWT.DROP_DOWN | SWT.READ_ONLY);
        ePropertyCombo.setLabelProvider(new LabelProvider());
        ePropertyCombo.setContentProvider(new ArrayContentProvider());
        ePropertyCombo
                .getCombo()
                .setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
        ePropertyCombo.getCombo().add("Need to load");

        ePropertySearchText = new Text(this, SWT.LEFT | SWT.BORDER);
        ePropertySearchText.setLayoutData(
        		new GridData(GridData.FILL, GridData.CENTER, true, true, 2, 1));
        
        Label horizontalLine = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd1 = new GridData(GridData.FILL, GridData.BEGINNING, true, false, numColumns, 1);
		gd1.horizontalSpan = numColumns;
		gd1.verticalIndent = 0;
		horizontalLine.setLayoutData(gd1);

        createListener();
        setShowAndHide(isGraphDB());
    }

    private void createListener() {
        vTypeCombo
                .getCombo()
                .addSelectionListener(
                        new SelectionListener() {

                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                Combo combo = vTypeCombo.getCombo();
                                String selectItem =
                                        String.valueOf(combo.getItem(combo.getSelectionIndex()));
                                System.out.println("selectItem : " + selectItem);
                                vPropertyCombo.setInput(
                                        propertiesToArray(vertexList.get(selectItem), false));
                                vPropertyCombo.getCombo().select(0);
                            }

                            @Override
                            public void widgetDefaultSelected(SelectionEvent e) {}
                        });

        eTypeCombo
                .getCombo()
                .addSelectionListener(
                        new SelectionListener() {

                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                Combo combo = eTypeCombo.getCombo();
                                String selectItem =
                                        String.valueOf(combo.getItem(combo.getSelectionIndex()));
                                System.out.println("selectItem : " + selectItem);
                                ePropertyCombo.setInput(
                                        propertiesToArray(edgeList.get(selectItem), true));
                                ePropertyCombo.getCombo().select(0);
                            }

                            @Override
                            public void widgetDefaultSelected(SelectionEvent e) {}
                        });

        eTypeCombo.addSelectionChangedListener(
                new ISelectionChangedListener() {

                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {}
                });
    }

    private SelectionListener executeListner = new SelectionListener(){
    
        @Override
        public void widgetSelected(SelectionEvent e) {
        	StringBuilder query = new StringBuilder();
            String vLabel = vTypeCombo.getCombo().getText();
            String vProperties = vPropertyCombo.getCombo().getText();
            String eType = eTypeCombo.getCombo().getText();
            String eProperties = ePropertyCombo.getCombo().getText();
            String vKeyword = vPropertySearchText.getText();
            String eKeyword = ePropertySearchText.getText();

            if (vLabel.equals(DEFAULT_ALL)) {
                query.append("MATCH (n)");
            } else {
                query.append("MATCH (n:").append(vLabel).append(")");
            }

            if (edgeSelect.getSelection()) {
            
	            if (eType.equals(DEFAULT_ALL)) {
	                query.append("-[r]-(n1)");
	            } else {
	                query.append("-[r:").append(eType).append("]-(n1)");
	                
	            }
            }
            
            if (vProperties.equals(DEFAULT_ALL)) {
                if (!vKeyword.isEmpty()) {
                	query.append("\nWHERE ANY(prop in keys(n) where TOSTRING(n[prop])")
                    .append(" CONTAINS '")
                    .append(vKeyword)
                    .append("')");
                }
            } else {
                if (!vKeyword.isEmpty()) {
                    query.append("\nWHERE TOSTRING(n.")
                            .append(vProperties)
                            .append(") CONTAINS '")
                            .append(vKeyword)
                            .append("'");
                }
            }
            
            if (edgeSelect.getSelection()) {
                
            	if (vKeyword.isEmpty()) {
            		if (!eKeyword.isEmpty()) {
            			query.append("\nWHERE ");
            		}
            	} else {
            		if (!eKeyword.isEmpty()) {
            			query.append("\nAND ");
            		}
            	}
            	
	            if (eProperties.equals(DEFAULT_ALL)) {
	            	if (!eKeyword.isEmpty()) {
	                	query.append("ANY(prop in keys(r) where TOSTRING(r[prop])")
	                    .append(" CONTAINS '")
	                    .append(eKeyword)
	                    .append("')");
	                }
	            } else {
	            	if (!eKeyword.isEmpty()) {
	                    query.append("TOSTRING(r.")
	                            .append(eProperties)
	                            .append(") CONTAINS '")
	                            .append(eKeyword).append("'");
	                }
	            }
            }
            
            if (edgeSelect.getSelection()) {
            	query.append("\nRETURN n,r,n1");
            } else {
            	query.append("\nRETURN n");
            }
            
            if (editor.getEditorControl().getText().isEmpty()) {
                editor.getEditorControl().setText(query.toString());
            } else {
                editor.getEditorControl()
                        .setText(
                                editor.getEditorControl().getText()
                                        + "\n"
                                        + query.toString());
            }
        }
    
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }
    };
    
    private boolean isGraphDB() {
        String databaseName = "";

        if (this.editor != null && this.editor.getDataSourceContainer() != null) {
            System.out.println(
                    "isGraphDB dataSourceContainer.getDataSource().getName() "
                            + this.editor.getDataSourceContainer().getId());
            databaseName = this.editor.getDataSourceContainer().getId();
            if (databaseName.contains("turbograph_jdbc")) {
                System.out.println("database is neo4j");
                return true;
            }
        }

        return false;
    }

    private void setShowAndHide(boolean status) {
        System.out.println("setShowAndHide status : " + status);
        UIUtils.asyncExec(
                new Runnable() {

                    @Override
                    public void run() {
                    	GridData gd = (GridData) Composite.getLayoutData();
                        if (status) {
                            gd.horizontalSpan = 3;
                            gd.exclude = false;
                            Composite.getParent().layout(false);
                        } else {
                            gd.horizontalSpan = 3;
                            gd.exclude = true;
                            Composite.getParent().layout(false);
                        }
                    }
                });
    }

    public void ControlQuickQueryView() {
        boolean isGraphDB = isGraphDB();
        System.out.println("ControlQuickQueryView isGraphDB : " + isGraphDB);
        if (isGraphDB) {
            startUpdateJob = new GetTypeInfoJob("VisualQuickQueryPanel editor Update");

            if (!startUpdateJob.isFinished()) {
                System.out.println("startUpdateJob let's go");
                startUpdateJob.schedule();
            } else {
                System.out.println("startUpdateJob is null");
            }
        }
        setShowAndHide(isGraphDB);
    }

    public ArrayList<String> propertiesToArray(String properties, boolean isEdge) {
        System.out.println("propertiesToArray properties :" + properties);
        ArrayList<String> list = new ArrayList<>();

        if (properties != null && !properties.equals("[]")) {
            properties = properties.replace(String.valueOf('['), "");
            properties = properties.replace(String.valueOf(']'), "");
        } else {
            properties = null;
        }

        if (properties != null) {
            String[] propertiesList = properties.split(", ");
            for (int i = 0; i < propertiesList.length; i++) {
                list.add(propertiesList[i].replaceAll("\"", ""));
                System.out.println("EdgePropertiesToArray edgePropertyName :" + propertiesList[i]);
            }
        }

        return list;
    }

    private class GetTypeInfoJob extends AbstractJob {
        private Throwable error;

        GetTypeInfoJob(String name) {
            super(name);
            setUser(true);
        }

        @Override
        protected IStatus run(DBRProgressMonitor monitor) {
            System.out.println("GetTypeInfoJob start");

            if (editor == null) {
                System.out.println("GetTypeInfoJob editor null");
                return Status.CANCEL_STATUS;
            }

            if (editor.getDataSourceContainer() == null) {
                System.out.println("GetTypeInfoJob getDataSourceContainer is null");
                return Status.CANCEL_STATUS;
            }

            if (editor.getDataSource() == null) {
                System.out.println("GetTypeInfoJob getdatasource is null");
                return Status.CANCEL_STATUS;
            }

            monitor.beginTask("Update GraphDB Type info", 1);
            try {
                lock.lock();

                vertexList.clear();
                vertexList.put(DEFAULT_ALL, DEFAULT_ALL);
                edgeList.clear();
                edgeList.put(DEFAULT_ALL, DEFAULT_ALL);

                // getVertexInfo(monitor);
                getNodeLabel(monitor);
                getNodeProperties(monitor);
                getEdgeType(monitor);
                getEdgeProperties(monitor);
                updateComboView();
            } finally {
                lock.unlock();
                monitor.done();
            }
            System.out.println("GetTypeInfoJob end");
            return Status.OK_STATUS;
        }

        private void updateComboView() {

            if (error != null) {
                // DBWorkbench.getPlatformUI().showError("updateComboView", "Can't update
                // ComboView", error);
            } else {
                UIUtils.asyncExec(
                        new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<String> array1 = new ArrayList<>(vertexList.keySet());
                                vTypeCombo.setInput(array1);
                                vTypeCombo.getCombo().select(0);

                                ArrayList<String> array2 = new ArrayList<>(edgeList.keySet());
                                eTypeCombo.setInput(array2);
                                eTypeCombo.getCombo().select(0);

                                ArrayList<String> sampleArray = new ArrayList<>();
                                sampleArray.add(DEFAULT_ALL);

                                vPropertyCombo.setInput(sampleArray);
                                vPropertyCombo.getCombo().select(0);
                                ePropertyCombo.setInput(sampleArray);
                                ePropertyCombo.getCombo().select(0);
                            }
                        });
            }
        }

        private void getNodeLabel(DBRProgressMonitor monitor) {
            System.out.println("getVertexLabel start");
            try (JDBCSession session =
                    DBUtils.openMetaSession(
                            monitor, editor.getDataSourceContainer(), "Load Vertex Label")) {
                try (JDBCPreparedStatement dbStat =
                        session.prepareStatement(QUERY_GET_VERTEX_LABEL)) {
                    try (JDBCResultSet dbResult = dbStat.executeQuery()) {
                        while (dbResult.next()) {
                            vertexLabel = JDBCUtils.safeGetString(dbResult, "label");
                            vertexLabel = vertexLabel.replaceAll("[\\[\\]\"]", "");
                            vertexList.put(vertexLabel, DEFAULT_ALL);
                            System.out.println("vertexLabel :" + vertexLabel);
                        }
                    }
                }
            } catch (DBCException | SQLException e) {
                System.out.println("getVertexLabel excpetion");
                error = e;
            }
            System.out.println("getVertexLabel end");
        }

        private void getNodeProperties(DBRProgressMonitor monitor) {
            String vertexLabel;
            String vertexPropertyName;
            System.out.println("getVertexProperties start");
            String query = makeVertexPropertiesQuery();

            try (JDBCSession session =
                    DBUtils.openMetaSession(
                            monitor, editor.getDataSourceContainer(), "Load Nodes properties")) {
                JDBCPreparedStatement dbStat = session.prepareStatement(query);
                try (JDBCResultSet dbResult = dbStat.executeQuery()) {
                    while (dbResult.next()) {
                        vertexLabel = JDBCUtils.safeGetString(dbResult, "label");
                        vertexPropertyName = JDBCUtils.safeGetString(dbResult, "key");
                        vertexList.put(
                                vertexLabel,
                                vertexList.get(vertexLabel).concat(", " + vertexPropertyName));
                        System.out.println("vertexPropertyName :" + vertexLabel);
                        System.out.println("vertexPropertyName :" + vertexPropertyName);
                    }
                }
            } catch (DBCException | SQLException e) {
                error = e;
                System.out.println("getVertexProperties e : " + e.toString());
            }

            System.out.println("getVertexProperties end");
        }

        private void getEdgeType(DBRProgressMonitor monitor) {
            System.out.println("getEdgeType start");
            try (JDBCSession session =
                    DBUtils.openMetaSession(
                            monitor, editor.getDataSourceContainer(), "Load Edges Type")) {
                try (JDBCPreparedStatement dbStat = session.prepareStatement(QUERY_GET_EDGE_TYPE)) {
                    try (JDBCResultSet dbResult = dbStat.executeQuery()) {
                        while (dbResult.next()) {
                            edgeType = JDBCUtils.safeGetString(dbResult, "type");
                            edgeList.put(edgeType, DEFAULT_ALL);
                            System.out.println("edge typeName :" + edgeType);
                        }
                    }
                }
            } catch (DBCException | SQLException e) {
                error = e;
                System.out.println("getEdgeType e : " + e.toString());
            }
            System.out.println("getEdgeType end");
        }

        private void getEdgeProperties(DBRProgressMonitor monitor) {
            if (edgeList.size() < 1) {
                return;
            }
            System.out.println("getEdgeProperties start");
            
            String edgeType;
            String edgePropertyName;
            String query = makeEdgePropertiesQuery();

            try (JDBCSession session =
                    DBUtils.openMetaSession(
                            monitor, editor.getDataSourceContainer(), "Load Edges Propreties")) {
                try (JDBCPreparedStatement dbStat =
                        session.prepareStatement(query)) {
                    try (JDBCResultSet dbResult = dbStat.executeQuery()) {
                        while (dbResult.next()) {
                        	edgeType = JDBCUtils.safeGetString(dbResult, "type");
                            edgePropertyName = JDBCUtils.safeGetString(dbResult, "key");
                            edgeList.put(edgeType, 
                            		vertexList.get(vertexLabel).concat(", " + edgePropertyName));
                            System.out.println("edge edgeType : " + edgeType);
                            System.out.println("edge edgePropertyName :" + edgePropertyName);
                        }
                    }
                    
                }
            } catch (DBCException | SQLException e) {
                error = e;
                System.out.println("getEdgeProperties e : " + e.toString());
            }
            System.out.println("getEdgeProperties end");
        }

        @SuppressWarnings("unused")
        private void getVertexInfo(DBRProgressMonitor monitor) {
            String vertexLabel;
            String vertexProperty;

            try (JDBCSession session =
                    DBUtils.openMetaSession(
                            monitor, editor.getDataSourceContainer(), "Load Nodes And Edges")) {
                session.setNetworkTimeout(null, 300);
                JDBCDatabaseMetaData meta = session.getMetaData();
                JDBCResultSet tableResultSet =
                        meta.getTables(null, null, null, new String[] {"TABLE"});
                while (tableResultSet.next()) {
                    vertexLabel = JDBCUtils.safeGetString(tableResultSet, "TABLE_NAME");
                    vertexList.put(vertexLabel, DEFAULT_ALL);
                    System.out.println("tableName name :" + vertexLabel);
                    JDBCResultSet columsResultSet = meta.getColumns(null, null, vertexLabel, null);
                    while (columsResultSet.next()) {
                        vertexProperty = JDBCUtils.safeGetString(columsResultSet, "COLUMN_NAME");
                        System.out.println("columnName name :" + vertexProperty);
                        vertexList.put(
                                vertexLabel,
                                vertexList.get(vertexLabel).concat(", " + vertexProperty));
                    }
                }
            } catch (DBCException | SQLException e) {
                error = e;
                System.out.println("e : " + e.toString());
            }
        }

        private String makeVertexPropertiesQuery() {
            StringBuilder query = new StringBuilder();
            boolean first = true;
            for (String vertexLabel : vertexList.keySet()) {
            	if (!vertexLabel.equals(DEFAULT_ALL)) {
	                if (!first) {
	                    query.append(" UNION ");
	                }
	                first = false;
	                query.append("MATCH (n:")
	                        .append(vertexLabel)
	                        .append(") UNWIND keys(n) AS key UNWIND labels(n) AS label RETURN DISTINCT key, label");
            	}
            }
            return query.toString();
        }

        private String makeEdgePropertiesQuery() {
            StringBuilder query = new StringBuilder();
            boolean first = true;
            for (String edgeType : edgeList.keySet()) {
            	if (!edgeType.equals(DEFAULT_ALL)) {
	                if (!first) {
	                    query.append(" UNION ");
	                }
	                first = false;
	                query.append("MATCH ()-[r:")
	                        .append(edgeType)
	                        .append("]-() UNWIND keys(r) AS key RETURN DISTINCT key, type(r) AS type");
            	}
            }
            return query.toString();
        }
    }
}
