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

import java.sql.ResultSetMetaData;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
import org.jkiss.dbeaver.model.exec.jdbc.JDBCStatement;
import org.jkiss.dbeaver.model.impl.jdbc.JDBCUtils;
import org.jkiss.dbeaver.model.runtime.AbstractJob;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.css.CSSUtils;
import org.jkiss.dbeaver.ui.css.DBStyles;
import org.jkiss.dbeaver.ui.editors.sql.internal.SQLEditorMessages;

public class VisualQuickQueryPanel extends Composite {
    private static final Log log = Log.getLog(VisualQuickQueryPanel.class);

    private final ComboViewer vTypeCombo;
    private final ComboViewer vPropertyCombo;
    private final ComboViewer vOperatorCombo;
    private final Text vPropertySearchText;
    private final ComboViewer eTypeCombo;
    private final ComboViewer ePropertyCombo;
    private final ComboViewer eOperatorCombo;
    private final Text ePropertySearchText;
    private final Button edgeSelect;

    private Composite Composite;

    private final SQLEditor editor;

    private String activeDisplayName = "Quick Query";

    private LinkedHashMap<String, LinkedHashMap<String, String>> vertexList = new LinkedHashMap<>();
    private LinkedHashMap<String, LinkedHashMap<String, String>> edgeList = new LinkedHashMap<>();

    private static final String DEFAULT_ALL_LABEL = "ALL(Label)";
    private static final String DEFAULT_ALL_TYPE = "ALL(Type)";
    private static final String DEFAULT_ALL_PROPERTIES = "ALL(Properties)";

    private SaveSelectItem saveItem = new SaveSelectItem();
    
    private ReentrantLock lock = new ReentrantLock();
    private GetTypeInfoJob startUpdateJob;

    public VisualQuickQueryPanel(SQLEditor editor, Composite parent) {
        super(parent, SWT.NONE);
        this.Composite = this;
        this.editor = editor;
        final int numColumns = 10;

        CSSUtils.setCSSClass(this, DBStyles.COLORED_BY_CONNECTION_TYPE);

        GridLayout gl = new GridLayout(numColumns, false);
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

        vOperatorCombo = new ComboViewer(this, SWT.DROP_DOWN | SWT.READ_ONLY);
        vOperatorCombo.setLabelProvider(new LabelProvider());
        vOperatorCombo.setContentProvider(new ArrayContentProvider());
        vOperatorCombo
                .getCombo()
                .setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, true));
        vOperatorCombo.setInput(getOperator(null));
        vOperatorCombo.getCombo().select(0);

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
        eTypeCombo.setLabelProvider(
                new LabelProvider() {
                    public String getText(Object element) {
                        return element.toString();
                    }
                });
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

        eOperatorCombo = new ComboViewer(this, SWT.DROP_DOWN | SWT.READ_ONLY);
        eOperatorCombo.setLabelProvider(new LabelProvider());
        eOperatorCombo.setContentProvider(new ArrayContentProvider());
        eOperatorCombo
                .getCombo()
                .setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, true));
        eOperatorCombo.setInput(getOperator(null));
        eOperatorCombo.getCombo().select(0);

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
        vTypeCombo.addSelectionChangedListener(
                new ISelectionChangedListener() {

                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        vTypeComboSelected();
                    }
                });

        eTypeCombo.addSelectionChangedListener(
                new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        eTypeComboSelected();
                    }
                });

        vPropertyCombo.addSelectionChangedListener(
                new ISelectionChangedListener() {

                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        vPropertyComboSelected();
                    }
                });

        ePropertyCombo.addSelectionChangedListener(
                new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        ePropertyComboSelected();
                    }
                });
    }

    private SelectionListener executeListner =
            new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    StringBuilder query = new StringBuilder();
                    String vLabel = vTypeCombo.getCombo().getText();
                    String vProperties = vPropertyCombo.getCombo().getText();
                    String eType = eTypeCombo.getCombo().getText();
                    String eProperties = ePropertyCombo.getCombo().getText();
                    String vKeyword = vPropertySearchText.getText();
                    String eKeyword = ePropertySearchText.getText();
                    String vOperator = vOperatorCombo.getCombo().getText();
                    String eOperator = eOperatorCombo.getCombo().getText();

                    String vDataType = null;
                    String eDataType = null;

                    saveItem.saveItem();

                    if (vertexList != null
                            && vertexList.get(vLabel) != null
                            && vertexList.get(vLabel).get(vProperties) != null) {
                        vDataType = vertexList.get(vLabel).get(vProperties).toString();
                    }

                    if (edgeList != null
                            && edgeList.get(eType) != null
                            && edgeList.get(eType).get(eProperties) != null) {
                        eDataType = edgeList.get(eType).get(eProperties).toString();
                    }

                    if (vLabel.equals(DEFAULT_ALL_LABEL)) {
                        query.append("MATCH (n)");
                    } else {
                        query.append("MATCH (n:").append(vLabel).append(")");
                    }

                    if (edgeSelect.getSelection()) {

                        if (eType.equals(DEFAULT_ALL_TYPE)) {
                            query.append("-[r]-(n1)");
                        } else {
                            query.append("-[r:").append(eType).append("]-(n1)");
                        }
                    }

                    if (vProperties.equals(DEFAULT_ALL_PROPERTIES)) {
                        if (!vKeyword.isEmpty()) {
                            query.append(
                                            "\nWHERE ANY(prop in keys(n) where TOSTRINGORNULL(n[prop]) ")
                                    .append(vOperator)
                                    .append(" '")
                                    .append(vKeyword)
                                    .append("')");
                        }
                    } else {
                        if (!vKeyword.isEmpty()) {
                            if (PropertyType.getType(vDataType) == PropertyType.LIST) {
                                query.append("ANY ( x in ")
                                        .append(needProperetyConvert("r." + vProperties, vDataType))
                                        .append(" WHERE x ")
                                        .append(eOperator)
                                        .append(" ")
                                        .append(needSingleQuotation(vKeyword, vDataType))
                                        .append(")");
                            } else {
                                query.append("\nWHERE ")
                                        .append(needProperetyConvert("n." + vProperties, vDataType))
                                        .append(" ")
                                        .append(vOperator)
                                        .append(" ")
                                        .append(needSingleQuotation(vKeyword, vDataType));
                            }
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

                        if (eProperties.equals(DEFAULT_ALL_PROPERTIES)) {
                            if (!eKeyword.isEmpty()) {
                                query.append("ANY(prop in keys(r) where TOSTRINGORNULL(r[prop])")
                                        .append(" CONTAINS '")
                                        .append(eKeyword)
                                        .append("')");
                            }
                        } else {
                            if (!eKeyword.isEmpty()) {
                                if (PropertyType.getType(eDataType) == PropertyType.LIST) {
                                    query.append("ANY ( x in ")
                                            .append(
                                                    needProperetyConvert(
                                                            "r." + eProperties, eDataType))
                                            .append(" WHERE x ")
                                            .append(eOperator)
                                            .append(" ")
                                            .append(needSingleQuotation(eKeyword, eDataType))
                                            .append(")");
                                } else {
                                    query.append(
                                                    needProperetyConvert(
                                                            "r." + eProperties, eDataType))
                                            .append(" ")
                                            .append(eOperator)
                                            .append(" ")
                                            .append(needSingleQuotation(eKeyword, eDataType));
                                }
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
                public void widgetDefaultSelected(SelectionEvent e) {}
            };

    private String needSingleQuotation(String value, String type) {
        boolean isNeedAdd = false;
        switch (PropertyType.getType(type)) {
            case PropertyType.LIST:
            case PropertyType.STRING:
            case PropertyType.ALL:
                isNeedAdd = true;
                break;
            default:
                break;
        }

        if (isNeedAdd) {
            return quote(value);
        }

        return value;
    }

    private String needProperetyConvert(String value, String type) {
        String result = value;

        switch (PropertyType.getType(type)) {
            case PropertyType.LIST:
                // result = "TOSTRINGLIST(" + value + ")";
                break;
            default:
                break;
        }

        return result;
    }

    public static String quote(String s) {
        return new StringBuilder().append('\'').append(s).append('\'').toString();
    }

    private boolean isGraphDB() {
        String databaseName = "";

        if (this.editor != null && this.editor.getDataSourceContainer() != null) {
            databaseName = this.editor.getDataSourceContainer().getId();
//            if (databaseName.contains("turbograph_jdbc")) {
//                return true;
//            }
            
            if (databaseName.contains("cubrid_jdbc")) {
            	return true;
            }
        }

        return false;
    }

    private void setShowAndHide(boolean status) {
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
        if (isGraphDB) {
            startUpdateJob = new GetTypeInfoJob("VisualQuickQueryPanel editor Update");

            if (!isLocked()) {
                startUpdateJob.schedule();
            }
        }
        setShowAndHide(isGraphDB);
    }

    public ArrayList<String> propertiesToArray(String properties, boolean isEdge) {
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

            if (editor == null) {
                return Status.CANCEL_STATUS;
            }

            if (editor.getDataSourceContainer() == null) {
                return Status.CANCEL_STATUS;
            }

            if (editor.getDataSource() == null) {
                return Status.CANCEL_STATUS;
            }

            monitor.beginTask("Update GraphDB Type info", 1);
            try {
                lock.lock();

                dataInit();
                getProperties(monitor);
                updateComboView();
            } finally {
                lock.unlock();
                monitor.done();
            }
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

                                if (vTypeCombo == null) {
                                    return;
                                }

                                ArrayList<String> array1 = new ArrayList<>(vertexList.keySet());
                                vTypeCombo.setInput(array1);
                                ArrayList<String> array2 = new ArrayList<>(edgeList.keySet());
                                eTypeCombo.setInput(array2);

                                ArrayList<String> sampleArray = new ArrayList<>();
                                sampleArray.add(DEFAULT_ALL_PROPERTIES);
                                vPropertyCombo.setInput(sampleArray);
                                ePropertyCombo.setInput(sampleArray);

                                vTypeCombo.getCombo().select(0);
                                eTypeCombo.getCombo().select(0);
                                vPropertyCombo.getCombo().select(0);
                                ePropertyCombo.getCombo().select(0);

                                if (saveItem.isSaved()) {
                                    for (int i = 0; i < vTypeCombo.getCombo().getItemCount(); i++) {
                                        if (vTypeCombo
                                                .getCombo()
                                                .getItem(i)
                                                .equals(saveItem.vLabel)) {
                                            vTypeCombo.getCombo().select(i);
                                            vTypeComboSelected();
                                        }
                                    }
                                    for (int i = 0; i < eTypeCombo.getCombo().getItemCount(); i++) {
                                        if (eTypeCombo
                                                .getCombo()
                                                .getItem(i)
                                                .equals(saveItem.eType)) {
                                            eTypeCombo.getCombo().select(i);
                                            eTypeComboSelected();
                                        }
                                    }
                                    for (int i = 0;
                                            i < vPropertyCombo.getCombo().getItemCount();
                                            i++) {
                                        if (vPropertyCombo
                                                .getCombo()
                                                .getItem(i)
                                                .equals(saveItem.vProperties)) {
                                            vPropertyCombo.getCombo().select(i);
                                            vPropertyComboSelected();
                                        }
                                    }
                                    for (int i = 0;
                                            i < ePropertyCombo.getCombo().getItemCount();
                                            i++) {
                                        if (ePropertyCombo
                                                .getCombo()
                                                .getItem(i)
                                                .equals(saveItem.eProperties)) {
                                            ePropertyCombo.getCombo().select(i);
                                            ePropertyComboSelected();
                                        }
                                    }
                                }
                            }
                        });
            }
        }

        private void getProperties(DBRProgressMonitor monitor) {
            String label;
            String type;
            JDBCSession closeSession = null;
            
            try (JDBCSession session =
                    DBUtils.openMetaSession(
                            monitor, editor.getDataSourceContainer(), "Load Nodes properties")) {
                System.out.println(session.isClosed());
                System.out.println(session.isConnected());
            	JDBCDatabaseMetaData meta = session.getMetaData();
            	JDBCResultSet dbResult = meta.getTables(null, null, null, null);
            	
            	JDBCResultSet propertyResultSet;
            	LinkedHashMap<String, String> proprties;
                while (dbResult.next()) {
                    label = JDBCUtils.safeGetString(dbResult, "TABLE_NAME");
                    type = JDBCUtils.safeGetString(dbResult, "TABLE_TYPE");
                    
                    if (type.equals("TABLE")) {
                        propertyResultSet = meta.getColumns(null, null, label, "node");
                        proprties = vertexList.get(label);
                    
                        if (proprties == null) {
                        	proprties = new LinkedHashMap<>();
                            proprties.put(DEFAULT_ALL_PROPERTIES, null);
                            vertexList.put(label, proprties);
                        }
                    } else {
                        propertyResultSet = meta.getColumns(null, null, label, "edge");
                        proprties = edgeList.get(label);
                    
                        if (proprties == null) {
                            proprties = new LinkedHashMap<>();
                            proprties.put(DEFAULT_ALL_PROPERTIES, null);
                            edgeList.put(label, proprties);
                        }
                    }
                    
                    while (propertyResultSet.next()) {
                    	String propertyName = propertyResultSet.getString("COLUMN_NAME");
                    	String typeName = propertyResultSet.getString("TYPE_NAME");
//                        System.out.println("Property : " + propertyName);
//                        System.out.println("DataType : " + propertyResultSet.getInt("DATA_TYPE"));
//                        System.out.println("SQL DataType : " + propertyResultSet.getInt("SQL_DATA_TYPE"));
//                        System.out.println("DataType : " + propertyResultSet.getString("TYPE_NAME"));
                        proprties.put(
                        		propertyName, typeName);

                    }
                    
                    if (propertyResultSet.getStatement() != null) {
                    	propertyResultSet.getStatement().close(); 
                    }
            	}

                if (dbResult.getStatement() != null) dbResult.getStatement().close();
                closeSession = session;
            } catch (DBCException | SQLException e) {
                error = e;
                e.printStackTrace();
            } finally {
            	try {
					if (closeSession != null && !closeSession.isClosed()) {
						closeSession.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
        }

        private void getEdgeProperties(DBRProgressMonitor monitor) {
            String edgeType;
            JDBCSession closeSession = null;
            try (JDBCSession session =
                    DBUtils.openMetaSession(
                            monitor, editor.getDataSourceContainer(), "Load Edges Propreties")) {
            	JDBCDatabaseMetaData meta = session.getMetaData();
            	JDBCResultSet dbResult = meta.getTables(null, null, null, new String[] {"VIEW"});
                while (dbResult.next()) {
            		edgeType = JDBCUtils.safeGetString(dbResult, "TABLE_NAME");
                    JDBCResultSet propertyResultSet = meta.getColumns(null, null, edgeType, "edge");
                    LinkedHashMap<String, String> proprties = edgeList.get(edgeType);
                    if (proprties == null) {
                    	proprties = new LinkedHashMap<>();
                        proprties.put(DEFAULT_ALL_PROPERTIES, null);
                        edgeList.put(edgeType, proprties);
                    }
                    
                    while (propertyResultSet.next()) {
                    	String propertyName = propertyResultSet.getString("COLUMN_NAME");
                    	String typeName = propertyResultSet.getString("TYPE_NAME");
//                        System.out.println("Property : " + propertyName);
//                        System.out.println("DataType : " + propertyResultSet.getInt("DATA_TYPE"));
//                        System.out.println("SQL DataType : " + propertyResultSet.getInt("SQL_DATA_TYPE"));
//                        System.out.println("DataType : " + propertyResultSet.getString("TYPE_NAME"));
                        proprties.put(
                        		propertyName, typeName);

                    }
                    
                    if (propertyResultSet.getStatement() != null) {
                    	propertyResultSet.getStatement().close();
                    }
            	}                
            	
                if (dbResult.getStatement() != null) dbResult.getStatement().close();
                closeSession = session;
            } catch (DBCException | SQLException e) {
                error = e;
                e.printStackTrace();
            } finally {
            	try {
					if (closeSession != null && !closeSession.isClosed()) {
						closeSession.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
        }

        @SuppressWarnings("unused")
        private void testPrepareStatement(DBRProgressMonitor monitor) {
            String vertexLabel;
            String vertexProperty;

            try (JDBCSession session =
                    DBUtils.openMetaSession(
                            monitor, editor.getDataSourceContainer(), "Load Nodes And Edges")) {
            	String query = "MATCH (n:person) WHERE name = ? and age = ? RETURN n.born, n.name";
            	JDBCPreparedStatement dbStat = null;

            	dbStat = session.prepareStatement(query);
        		dbStat.setString(1, "Kim");
        		dbStat.setInt(2, 30);

        		JDBCResultSet dbResult = dbStat.executeQuery();
        		ResultSetMetaData meta = dbResult.getMetaData();
        		int count = meta.getColumnCount();

        		while (dbResult.next()) {
        			for (int j = 1; j < count + 1; j++) {
        					System.out.println("Label : " + meta.getTableName(j));
        					System.out.println("Property : " + meta.getColumnName(j));
        			       	System.out.println("Values : " + dbResult.getString(j));
        			    }
        		}

                if (dbResult != null) dbResult.close();
                if (dbStat != null) dbStat.close();
                
            } catch (DBCException | SQLException e) {
                error = e;
            }
        }
        
        @SuppressWarnings("unused")
        private void testCreateStatement(DBRProgressMonitor monitor) {
            String vertexLabel;
            String vertexProperty;

            try (JDBCSession session =
                    DBUtils.openMetaSession(
                            monitor, editor.getDataSourceContainer(), "Load Nodes And Edges")) {
            	//String query = "MATCH (n:) RETURN n.born, n.name";
            	String query = "Match (n:CUSTOMER)-[r]->(m) return n,r,m";
            	JDBCStatement dbStat = session.createStatement();
            	boolean ret = dbStat.execute(query);
            	JDBCResultSet dbResult;
            	if (ret) {
            		dbResult = dbStat.getResultSet();
            		ResultSetMetaData meta = dbResult.getMetaData();
            		int count = meta.getColumnCount();

            		while (dbResult.next()) {
            			for (int j = 1; j < count + 1; j++) {
            					System.out.println("Label : " + meta.getTableName(j));
            					System.out.println("Property : " + meta.getColumnName(j));
            			       	System.out.println("Values : " + dbResult.getString(j));
            			    }
            		}
            		if (dbResult != null) dbResult.close();
            	}

                if (dbStat != null) dbStat.close();
                
            } catch (DBCException | SQLException e) {
                error = e;
            }
        }
    }

    private String searchTypeInJson(String json, String search) {
        String temp = json;
        temp = temp.replace("{", "");
        temp = temp.replace("}", "");
        String[] bits = temp.split(", ");

        for (int i = 0; i < bits.length; i++) {
            String[] fieldBits = bits[i].split(":");
            String fieldName = fieldBits[0];
            fieldName = fieldName.replace("\"", "");
            if (fieldName.equals(search)) {
                String fieldValue = fieldBits[1];
                fieldValue = fieldValue.replace("\"", "");
                return fieldValue;
            }
        }

        return null;
    }

    private void dataInit() {
        vertexList.clear();
        edgeList.clear();
        LinkedHashMap<String, String> nodeProperties = new LinkedHashMap<>();
        nodeProperties.put(DEFAULT_ALL_PROPERTIES, null);
        LinkedHashMap<String, String> edgeProperties = new LinkedHashMap<>();
        edgeProperties.put(DEFAULT_ALL_PROPERTIES, null);
        vertexList.put(DEFAULT_ALL_LABEL, nodeProperties);
        edgeList.put(DEFAULT_ALL_TYPE, edgeProperties);
    }

    private ArrayList<String> getOperator(String type) {
        ArrayList<String> result = new ArrayList<>();

        switch (PropertyType.getType(type)) {
            case PropertyType.LIST:
            case PropertyType.STRING:
            case PropertyType.ALL:
                result.add("CONTAINS");
                result.add("START WITH");
                result.add("END WITH");
                result.add("=~");
                result.add("IS NULL");
                result.add("IS NOT NULL");
            case PropertyType.BOOLEAN:
            case PropertyType.NUMBER:
            case PropertyType.DATETIME:
            case PropertyType.DATE:
            case PropertyType.TIME:
                result.add("=");
                result.add("<>");
                result.add("<");
                result.add(">");
                result.add("<=");
                result.add(">=");
            default:
                break;
        }
        return result;
    }

    private String getDefaultTypeString(String type) {
        String result;
        switch (PropertyType.getType(type)) {
            case PropertyType.BOOLEAN:
                result = "true";
                break;
            case PropertyType.DATETIME:
            case PropertyType.DATE:
            case PropertyType.TIME:
                result = "datetime({year: ?, month: ?, day: ?})";
                break;
            default:
                result = "";
                break;
        }
        return result;
    }

    static class PropertyType {

        public static final int ALL = 0;
        public static final int STRING = 1;
        public static final int NUMBER = 2;
        public static final int DATETIME = 3;
        public static final int DATE = 4;
        public static final int TIME = 5;
        public static final int BOOLEAN = 6;
        public static final int LIST = 7;

        public PropertyType() {}

        public static int getType(String type) {

            if (type == null) {
                return ALL;
            }

            int result = 0;

            switch (type) {
                case "STRING":
                    result = STRING;
                    break;
                case "FLOAT":
                case "INTEGER":
                    result = NUMBER;
                    break;
                case "BOOLEAN":
                    result = BOOLEAN;
                    break;
                default:
                    if (type.contains("DateTime")) {
                        result = DATETIME;
                    } else if (type.contains("Date")) {
                        result = DATE;
                    } else if (type.contains("Time")) {
                        result = TIME;
                    } else if (type.contains("[]")) {
                        result = LIST;
                    } else {
                        result = ALL;
                    }
                    break;
            }
            return result;
        }
    }

    class SaveSelectItem {
        public String vLabel;
        public String vProperties;
        public String vOperator;
        public String eType;
        public String eProperties;
        public String eOperator;
        private boolean isSaved = false;

        public SaveSelectItem() {
            this.vLabel = "";
            this.vProperties = "";
            this.vOperator = "";
            this.eType = "";
            this.eProperties = "";
            this.eOperator = "";
        }

        public void saveItem() {
            Display.getDefault()
                    .asyncExec(
                            new Runnable() {
                                @Override
                                public void run() {
                                    vLabel = vTypeCombo.getCombo().getText();
                                    vProperties = vPropertyCombo.getCombo().getText();
                                    vOperator = vOperatorCombo.getCombo().getText();
                                    eType = eTypeCombo.getCombo().getText();
                                    eProperties = ePropertyCombo.getCombo().getText();
                                    eOperator = eOperatorCombo.getCombo().getText();
                                }
                            });
        }

        public boolean isSaved() {
            if (!vLabel.isEmpty()) return true;
            if (!vProperties.isEmpty()) return true;
            if (!vOperator.isEmpty()) return true;
            if (!eType.isEmpty()) return true;
            if (!eProperties.isEmpty()) return true;
            if (!eOperator.isEmpty()) return true;
            return false;
        }
    }

    private void vTypeComboSelected() {
        String selectItem = String.valueOf(vTypeCombo.getCombo().getText());
        if (vertexList != null && vertexList.get(selectItem) != null) {
            ArrayList<String> array1 = new ArrayList<>(vertexList.get(selectItem).keySet());
            vPropertyCombo.setInput(array1);
            vPropertyCombo.getCombo().select(0);
            vOperatorCombo.setInput(getOperator(null));
            vOperatorCombo.getCombo().select(0);
        }
    }

    private void vPropertyComboSelected() {
        String typeSelect = String.valueOf(vTypeCombo.getCombo().getText());
        String property = String.valueOf(vPropertyCombo.getCombo().getText());
        if (vertexList != null && vertexList.get(typeSelect) != null) {
            String dataType = String.valueOf(vertexList.get(typeSelect).get(property));
            if (!dataType.equals("null")) {
                vOperatorCombo.setInput(getOperator(dataType));
                vOperatorCombo.getCombo().select(0);
                vPropertySearchText.setText(getDefaultTypeString(dataType));
            }
        }
    }

    private void eTypeComboSelected() {
        String selectItem = String.valueOf(eTypeCombo.getCombo().getText());
        if (edgeList != null && edgeList.get(selectItem) != null) {
            ArrayList<String> array1 = new ArrayList<>(edgeList.get(selectItem).keySet());
            ePropertyCombo.setInput(array1);
            ePropertyCombo.getCombo().select(0);
            eOperatorCombo.setInput(getOperator(null));
            eOperatorCombo.getCombo().select(0);
        }
    }

    private void ePropertyComboSelected() {
        String typeSelect = String.valueOf(eTypeCombo.getCombo().getText());
        String property = String.valueOf(ePropertyCombo.getCombo().getText());
        if (edgeList != null && edgeList.get(typeSelect) != null) {
            String dataType = String.valueOf(edgeList.get(typeSelect).get(property));
            if (!dataType.equals("null")) {
                eOperatorCombo.setInput(getOperator(dataType));
                eOperatorCombo.getCombo().select(0);
                ePropertySearchText.setText(getDefaultTypeString(dataType));
            }
        }
    }
    
    public boolean isLocked() {
    	return lock.isLocked();
    }
}
