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

package org.jkiss.dbeaver.ui.controls.resultset.visual;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextPrintOptions;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.ModelPreferences;
import org.jkiss.dbeaver.ext.turbographpp.gephimodel.GephiModel;
import org.jkiss.dbeaver.model.DBConstants;
import org.jkiss.dbeaver.model.DBPDataKind;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.data.DBDAttributeBinding;
import org.jkiss.dbeaver.model.data.DBDDisplayFormat;
import org.jkiss.dbeaver.model.impl.data.DBDValueError;
import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.ui.UIIcon;
import org.jkiss.dbeaver.ui.UIStyles;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.controls.StyledTextFindReplaceTarget;
import org.jkiss.dbeaver.ui.controls.resultset.*;
import org.jkiss.dbeaver.ui.editors.TextEditorUtils;
import org.jkiss.utils.CommonUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class VisualizationPresentation extends AbstractPresentation implements IAdaptable {

    Composite composite;
    
    private DBDAttributeBinding curAttribute;
    private String curSelection;
    public boolean activated;
    private boolean showNulls;
    private Font monoFont;
    private GephiModel gephiModel = new GephiModel();
    private Graph graph;
    private int layoutAlgorithm = 2;
    Label nodeLabel;
    Combo nodePropertyListCombo;
    Label edgeLabel;
    Combo edgePropertyListCombo;
    
    ToolBarManager toolBarManager;
    
    Color[] colors;
    HashSet<String> propertyList = new HashSet<>();
    HashMap<String, DBDAttributeBinding> DBDAttributeNodeList = new HashMap<>();
    HashMap<String, DBDAttributeBinding> DBDAttributeEdgeList = new HashMap<>();
    HashMap<String, ResultSetRow> resultSetRowNodeList = new HashMap<>();
    HashMap<String, ResultSetRow> resultSetRowEdgeList = new HashMap<>();
    HashMap<String, String> displayStringNodeList = new HashMap<>();
    HashMap<String, String> displayStringEdgeList = new HashMap<>();
    
    @Override
    public void createPresentation(@NotNull final IResultSetController controller, @NotNull Composite parent) {
        super.createPresentation(controller, parent);
        colors = new Color[] { new Color( new RGB( 158, 204, 255 ) ),
                new Color(new RGB( 204, 178, 255 ) ),
                new Color(new RGB( 204, 255, 255 ) ),
                new Color(new RGB( 102, 255, 178 ) ),
                new Color(new RGB( 192, 192, 192 ) ),
                new Color(new RGB( 204, 255, 102 ) ),
                new Color(new RGB( 255, 255, 153 ) ),
                new Color(new RGB( 255, 153, 153 ) ),
                new Color(new RGB( 204, 255, 103 ) ),
                new Color(new RGB( 255, 153, 255 ) ),
               };
        composite = parent;
        GridLayout layout = new GridLayout(8, true);
        parent.setLayout(layout);
        
        nodeLabel = new Label(parent, SWT.READ_ONLY);
        nodeLabel.setText("Nodes Property");
        nodePropertyListCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        nodePropertyListCombo.setEnabled(true);
        nodePropertyListCombo.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
                if (gephiModel != null) {
                    String temp = nodePropertyListCombo.getText();
                    gephiModel.updateZestNode(graph, temp);
                }
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
                
            }
        });

        edgeLabel = new Label(parent, SWT.READ_ONLY);
        edgeLabel.setText("Edges Property");
        edgePropertyListCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        edgePropertyListCombo.setEnabled(true);
        edgePropertyListCombo.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
                
            }
        });
        
        createHorizontalLine(parent, 8, 0);
       
		graph = new Graph(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		graph.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_IBEAM));
		graph.setForeground(UIStyles.getDefaultTextForeground());
		graph.setBackground(UIStyles.getDefaultTextBackground());
		graph.setFont(UIUtils.getMonospaceFont());
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 8;
		graph.setLayoutData(data);
		//graph.setPreferredSize(2048, 1440);
		
		final ScrollBar verticalBar = graph.getVerticalBar();
		verticalBar.addSelectionListener(new SelectionAdapter() {
		  @Override
          public void widgetSelected(SelectionEvent e) {
          }
		});
        
        setLayoutManager(layoutAlgorithm);

        graph.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent e) {
	            Object temp = null;
	            
	            if (graph.getSelection() != null && graph.getSelection().size() != 0) { 
	                temp = graph.getSelection().get(0);
	            }
	            
	            if (temp != null) {
	                Object id = null;
    	            if (temp.getClass() == GraphNode.class) {
    	                GraphNode tNode = (GraphNode)temp;
    	                id = tNode.getData();
    	                curSelection = displayStringNodeList.get(id);
    	                controller.setCurrentRow(resultSetRowNodeList.get(id));
    	                curAttribute = DBDAttributeNodeList.get(id);
    	            } else if (temp.getClass() == GraphConnection.class) {
    	                GraphConnection tConnection =  (GraphConnection)temp;
    	                id = tConnection.getData();
    	                curSelection = displayStringEdgeList.get(id);
    	                controller.setCurrentRow(resultSetRowEdgeList.get(id));
                        curAttribute = DBDAttributeEdgeList.get(id);
    	            }
    	            
    	            fireSelectionChanged(new VisualizationSelectionImpl());
    	         
	            }
	            //System.out.println(e);
	        }
	    });
	    
        TextEditorUtils.enableHostEditorKeyBindingsSupport(controller.getSite(), graph);

        applyCurrentThemeSettings();
        
        if (graph != null) {
        	activateTextKeyBindings(controller, graph);
        }
        trackPresentationControl();
    }

    @Override
    public void dispose() {
        if (monoFont != null) {
            UIUtils.dispose(monoFont);
            monoFont = null;
        }
        GridLayout layout = new GridLayout(1, true);
        composite.setLayout(layout);
        super.dispose();
    }

    @Override
    protected void applyThemeSettings(ITheme currentTheme) {
        Font rsFont = currentTheme.getFontRegistry().get(ThemeConstants.FONT_SQL_RESULT_SET);
        if (rsFont != null) {
            int fontHeight = rsFont.getFontData()[0].getHeight();
            Font font = UIUtils.getMonospaceFont();

            FontData[] fontData = font.getFontData();
            fontData[0].setHeight(fontHeight);
            Font newFont = new Font(font.getDevice(), fontData[0]);

            this.graph.setFont(newFont);

            if (monoFont != null) {
                UIUtils.dispose(monoFont);
            }
            monoFont = newFont;

        }
    }

    @Override
    public Control getControl() {
        return graph;
    }

    @Override
    public void refreshData(boolean refreshMetadata, boolean append, boolean keepState) {
        System.out.println("==== refreshMetadata :" + refreshMetadata + " append : " + append + "keepState : " + keepState);
        if (refreshMetadata) {
            gephiModel.clear();
            graph.getNodes().clear();
            graph.getConnections().clear();
            setLayoutManager(2);
            propertyList.clear();
            DBDAttributeNodeList.clear();
            DBDAttributeEdgeList.clear();
            resultSetRowNodeList.clear();
            resultSetRowEdgeList.clear();
            displayStringNodeList.clear();
            displayStringEdgeList.clear();
            
            edgePropertyListCombo.removeAll();
            edgePropertyListCombo.add("label");
            edgePropertyListCombo.select(0);
            nodePropertyListCombo.removeAll();
            nodePropertyListCombo.add("label");
            nodePropertyListCombo.select(0);
            ShowVisualizaion(append);
        }
    }

    public static Label createHorizontalLine(Composite parent, int hSpan, int vIndent) {
        Label horizontalLine = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
        gd.horizontalSpan = hSpan;
        gd.verticalIndent = vIndent;
        horizontalLine.setLayoutData(gd);
        return horizontalLine;
    }
    
    private void ShowVisualizaion(boolean append) {
        DBPPreferenceStore prefs = getController().getPreferenceStore();
        String graphType = "";

        DBDDisplayFormat displayFormat = DBDDisplayFormat.safeValueOf(prefs.getString(ResultSetPreferences.RESULT_TEXT_VALUE_FORMAT));

        ResultSetModel model = controller.getModel();
        List<DBDAttributeBinding> attrs = model.getVisibleAttributes();
        
        List<ResultSetRow> allRows = model.getAllRows();


        for (int i = 0; i < attrs.size(); i++) {
            DBDAttributeBinding attr = attrs.get(i);
            graphType = attrs.get(i).getTypeName();
            
            if (graphType == "NODE") {
                for (ResultSetRow row : allRows) {
                    String displayString = getCellString(model, attr, row, displayFormat);
                    addNode(attr, row, displayString, colors[(i%10)]);
                }
            } 
        }

        for (int i = 0; i < attrs.size(); i++) {
            DBDAttributeBinding attr = attrs.get(i);
            graphType = attrs.get(i).getTypeName();
            
            if (graphType == "RELATIONSHIP") {
                for (ResultSetRow row : allRows) {
                    String displayString = getCellString(model, attr, row, displayFormat);
                    addEdge(attr, row, displayString);
                }
            }
        }
        
        for (String key : propertyList) {
            nodePropertyListCombo.add(key);
        }
    }
    
    private boolean addNode (DBDAttributeBinding attrs, ResultSetRow row, String cellString, Color color) {
    	int idx = 0;
    	HashMap<String, Object> attrList = new HashMap<>();
    	String regex = "[\\[\\]\\{\\}]";
    	String tempCellString = cellString.replaceAll(regex, "");
    	String[] tempValue = tempCellString.split(", ");
    	String prvKey = "";
    	
    	try {
            for (int i = 0; i < tempValue.length ; i++) {
            	idx = tempValue[i].indexOf("=");
            	if (i < 2) {
            		tempValue[i] = tempValue[i].substring(idx+1, tempValue[i].length());
            	} else {
            	    if (idx > 0) {
            	        attrList.put(tempValue[i].substring(0, idx), tempValue[i].substring(idx+1, tempValue[i].length()));
            	        prvKey = tempValue[i].substring(0, idx);
            	        propertyList.add(tempValue[i].substring(0, idx));
            	    } else {
            	        attrList.put(prvKey, attrList.get(prvKey) + ", " + tempValue[i]);
            	    }
            	}
            }
            DBDAttributeNodeList.put(tempValue[0], attrs);
            resultSetRowNodeList.put(tempValue[0], row);
            displayStringNodeList.put(tempValue[0], cellString);
            
            return gephiModel.addNode(graph, tempValue[0], tempValue[1], attrList, color);
    	} catch (Exception e) {
    	    e.printStackTrace();
    	    return false;
        }
    }
    
    private boolean addEdge (DBDAttributeBinding attrs, ResultSetRow row, String cellString) {
    	String[] tempValue;
    	int idx = 0;
    	int i = 0;
    	String regex = "[\\{\\}]";
        String tempCellString = cellString.replaceAll(regex, "");
    	HashMap<String, String> attrList = new HashMap<>();
    	String prvKey = "";

    	do {
    		tempValue = tempCellString.split(", ");	
    	}
    	while (tempCellString.length()==0); 
    		
    	try {
	        for (i = 0; i < tempValue.length ; i++) {
	        	idx = tempValue[i].indexOf("=");
	        	if (i < 4) {
	        		tempValue[i] = tempValue[i].substring(idx+1, tempValue[i].length());
	        	} else {
	        	    if (idx > 0) {
	        	        attrList.put(tempValue[i].substring(0, idx), tempValue[i].substring(idx+1, tempValue[i].length()));
	        	        prvKey = tempValue[i].substring(0, idx);
	        	    } else {
	        	        attrList.put(prvKey, attrList.get(prvKey) + ", " + tempValue[i]);
	        	    }
	        	}
	        }
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	DBDAttributeEdgeList.put(tempValue[0], attrs);
    	resultSetRowEdgeList.put(tempValue[0], row);
    	displayStringEdgeList.put(tempValue[0], cellString);
    	
        return gephiModel.addEdge(graph, tempValue[0], tempValue[1], tempValue[2], tempValue[3], attrList);
    }

    StringBuilder fixBuffer = new StringBuilder();

    private String getCellString(ResultSetModel model, DBDAttributeBinding attr, ResultSetRow row, DBDDisplayFormat displayFormat) {
        Object cellValue = model.getCellValue(attr, row);
        if (cellValue instanceof DBDValueError) {
            return ((DBDValueError) cellValue).getErrorTitle();
        }
        if (cellValue instanceof Number && controller.getPreferenceStore().getBoolean(ModelPreferences.RESULT_NATIVE_NUMERIC_FORMAT)) {
            displayFormat = DBDDisplayFormat.NATIVE;
        }

        String displayString = attr.getValueHandler().getValueDisplayString(attr, cellValue, displayFormat);

        if (displayString.isEmpty() &&
            showNulls &&
            DBUtils.isNullValue(cellValue))
        {
            displayString = DBConstants.NULL_VALUE_LABEL;
        }

        fixBuffer.setLength(0);
        for (int i = 0; i < displayString.length(); i++) {
            char c = displayString.charAt(i);
            switch (c) {
                case '\n':
                    c = CommonUtils.PARAGRAPH_CHAR;
                    break;
                case '\r':
                    continue;
                case 0:
                case 255:
                case '\t':
                    c = ' ';
                    break;
            }
            if (c < ' '/* || (c > 127 && c < 255)*/) {
                c = ' ';
            }
            fixBuffer.append(c);
        }

        return fixBuffer.toString();
    }

    @Override
    public void formatData(boolean refreshData) {
        //controller.refreshData(null);
    }

    @Override
    public void clearMetaData() {
    }

    @Override
    public void updateValueView() {
    }

    @Override
    public void fillMenu(@NotNull IMenuManager menu) {
    }

    @Override
    public void changeMode(boolean recordMode) {
    }

    @Override
    public void scrollToRow(@NotNull RowPosition position) {
    }

    @Nullable
    @Override
    public DBDAttributeBinding getCurrentAttribute() {
        return curAttribute;
    }

    @NotNull
    @Override
    public Map<Transfer, Object> copySelection(ResultSetCopySettings settings) {
        return Collections.singletonMap(
            TextTransfer.getInstance(),
            curSelection);
    }

    @Override
    public void printResultSet() {
    }

    @Override
    protected void performHorizontalScroll(int scrollCount) {
    }

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        return null;
    }

    @Override
    public ISelection getSelection() {
        return new VisualizationSelectionImpl();
    }

    private class VisualizationSelectionImpl implements IResultSetSelection {

        @Nullable
        @Override
        public Object getFirstElement()
        {
            return curSelection;
        }

        @Override
        public Iterator<String> iterator()
        {
            return toList().iterator();
        }

        @Override
        public int size()
        {
            return curSelection == null ? 0 : 1;
        }

        @Override
        public Object[] toArray()
        {
            return curSelection == null ?
                new Object[0] :
                new Object[] { curSelection };
        }

        @Override
        public List<String> toList()
        {
            return curSelection == null ?
                Collections.emptyList() :
                Collections.singletonList(curSelection);
        }

        @Override
        public boolean isEmpty()
        {
            return false;
        }

        @NotNull
        @Override
        public IResultSetController getController()
        {
            return controller;
        }

        @NotNull
        @Override
        public List<DBDAttributeBinding> getSelectedAttributes() {
            if (curAttribute == null) {
                return Collections.emptyList();
            }
            return Collections.singletonList(curAttribute);
        }

        @NotNull
        @Override
        public List<ResultSetRow> getSelectedRows()
        {
            ResultSetRow currentRow = controller.getCurrentRow();
            if (currentRow == null) {
                return Collections.emptyList();
            }
            return Collections.singletonList(currentRow);
        }

        @Override
        public DBDAttributeBinding getElementAttribute(Object element) {
            return curAttribute;
        }

        @Override
        public ResultSetRow getElementRow(Object element) {
            return getController().getCurrentRow();
        }
    }

    public void setLayoutManager(int layout) {
	    switch (layout) {
	    case 1:
	        graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(
	                LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	        layout++;
	        break;
	    case 2:
	        graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(
	                LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	        layout = 1;
	        break;
	    case 3:
            graph.setLayoutAlgorithm(new GridLayoutAlgorithm(
                    LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
            layout = 1;
            break;
	    }
    }
}
