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
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.themes.ITheme;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.ModelPreferences;
import org.jkiss.dbeaver.ext.turbographpp.graph.FXGraph;
import org.jkiss.dbeaver.ext.turbographpp.graph.GraphBase.LayoutStyle;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.DataRowID;
import org.jkiss.dbeaver.model.DBConstants;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.data.DBDAttributeBinding;
import org.jkiss.dbeaver.model.data.DBDDisplayFormat;
import org.jkiss.dbeaver.model.impl.data.DBDValueError;
import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.ui.UIIcon;
import org.jkiss.dbeaver.ui.UIStyles;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.controls.resultset.*;
import org.jkiss.dbeaver.ui.editors.TextEditorUtils;
import org.jkiss.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VisualizationPresentation extends AbstractPresentation implements IAdaptable {

    private IResultSetController controller;
	// for Other ImageButton
    private enum ImageButton {
        SHORTEST,
        VALUE,
        DESIGN,
        CHART,
        CAPTURE,
        TO_CSV
    }

	private Composite composite;
	private Composite mainComposite;
	private Composite menuBarComposite;
	private Composite graphTopComposite;
	
	private DBDAttributeBinding curAttribute;
	private String curSelection;
	public boolean activated;
	private boolean showNulls;
	private Font monoFont;

	private FXGraph visualGraph;
	
	private CoolBar coolBar;
	private Label resultLabel;
	private Label InfoLabel;

	private Button shortestButton;
	
    private HashSet<Object> propertyList = new HashSet<>();
	private HashMap<String, DBDAttributeBinding> DBDAttributeNodeList = new HashMap<>();
	private HashMap<String, DBDAttributeBinding> DBDAttributeEdgeList = new HashMap<>();
	private HashMap<String, ResultSetRow> resultSetRowNodeList = new HashMap<>();
	private HashMap<String, ResultSetRow> resultSetRowEdgeList = new HashMap<>();
	private HashMap<String, String> displayStringNodeList = new HashMap<>();
	private HashMap<String, String> displayStringEdgeList = new HashMap<>();
	
	private boolean init = false;
	
	public static final String NODE_EDGE_ID = DataRowID.NODE_EDGE_ID;
	public static final String NODE_LABEL = DataRowID.NODE_LABEL;
	public static final String EDGE_TYPE = DataRowID.EDGE_TYPE;
	public static final String NEO4J_EDGE_START_ID = DataRowID.NEO4J_EDGE_START_ID;
	public static final String NEO4J_EDGE_END_ID = DataRowID.NEO4J_EDGE_END_ID;
	public static final String TURBOGRAPH_EDGE_START_ID = DataRowID.TURBOGRAPH_EDGE_START_ID;
	public static final String TURBOGRAPH_EDGE_END_ID = DataRowID.TURBOGRAPH_EDGE_END_ID;
	
    private String currentQuery = "";
	@Override
    public void createPresentation(
            @NotNull final IResultSetController controller, @NotNull Composite parent) {
        super.createPresentation(controller, parent);

        this.controller = controller;

		composite = parent;
		GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 5;
        layout.marginWidth = 5;
        
        GridData gd_MainComposite = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
        
		mainComposite = new Composite(composite, SWT.NONE);
		mainComposite.setLayout(layout);
		mainComposite.setLayoutData(gd_MainComposite);
		
		menuBarComposite = new Composite(mainComposite, SWT.NONE);
		menuBarComposite.setLayout(new GridLayout(1, false));
		menuBarComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
        
		graphTopComposite = new Composite(mainComposite, SWT.NONE);
		graphTopComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        graphTopComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        
        addMenuCoolbar(menuBarComposite);

		visualGraph = new FXGraph(graphTopComposite, SWT.NONE, controller.getDataContainer().getDataSource());
		visualGraph.setCursor(graphTopComposite.getDisplay().getSystemCursor(SWT.CURSOR_IBEAM));
		visualGraph.setForeground(UIStyles.getDefaultTextForeground());
		visualGraph.setBackground(UIStyles.getDefaultTextBackground());
		visualGraph.setFont(UIUtils.getMonospaceFont());
		visualGraph.setLayout(new FillLayout(SWT.FILL));
		visualGraph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		createGraphListner();
		
		createHorizontalLine(parent, 1, 0); 

        TextEditorUtils.enableHostEditorKeyBindingsSupport(
                controller.getSite(), visualGraph.getControl());
		applyCurrentThemeSettings();

		if (visualGraph != null) {
			activateTextKeyBindings(controller, visualGraph.getControl());
		}
		
		trackPresentationControl();
		
	}

	@Override
	public void dispose() {
		if (monoFont != null) {
			UIUtils.dispose(monoFont);
			monoFont = null;
		}
		
		if (visualGraph != null) {
			visualGraph.finalize();
		}
		
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

			visualGraph.setFont(newFont);

			if (monoFont != null) {
				UIUtils.dispose(monoFont);
			}
			monoFont = newFont;

		}
	}

	@Override
	public Control getControl() {
	    if (visualGraph != null) {
	        return visualGraph.getControl();
	    } 
	    return mainComposite;
	}

	@Override
	public void refreshData(boolean refreshMetadata, boolean append, boolean keepState) {
		if (refreshMetadata) {
            if (visualGraph != null) {
                visualGraph.clearGraph();
            }
			setShortestMode(false);
			setDefaultLayoutManager();
			propertyList.clear();
			DBDAttributeNodeList.clear();
			DBDAttributeEdgeList.clear();
			resultSetRowNodeList.clear();
			resultSetRowEdgeList.clear();
			displayStringNodeList.clear();
			displayStringEdgeList.clear();

			ShowVisualizaion(append);
		}
	}
	
	private final SelectionListener layoutChangeListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.widget != null) {
				setLayoutManager((LayoutStyle) e.widget.getData());
			}
		}
	};
	
	private final SelectionListener imageButtonListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.widget != null && e.widget.getData() != null) {
				switch((ImageButton) e.widget.getData()) {
                    case VALUE :
				        visualGraph.valueShow();
				        break;
					case DESIGN :
					    visualGraph.designEditorShow();
					    break;
					case CHART:
					    visualGraph.chartShow();
					    break;
					case CAPTURE :
					    saveImage();
					    setShortestMode(false);
						break;
					case SHORTEST :
						if(visualGraph != null) {
							boolean status = visualGraph.getShortestMode();
							setShortestMode(!status);
						}
					    break;
					case TO_CSV :
						saveCSV();
						setShortestMode(false);
					    break;
					default :
						break;
				}
			}
		}
	};
	
	private void addMenuCoolbar(Composite parent) {
		coolBar = new CoolBar(parent, SWT.NONE);
		coolBar.setBackground(parent.getBackground());

		CoolItem buttonItem1 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);
		CoolItem buttonItem2 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);
		CoolItem buttonItem3 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);
		CoolItem buttonItem4 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);
		CoolItem buttonItem5 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);
		
		Composite composite1 = new Composite(coolBar, SWT.NONE);
		composite1.setLayout(new GridLayout(LayoutStyle.values().length, true));

		Button button1; 
		
		for (LayoutStyle style : LayoutStyle.values()) {
			button1 = new Button(composite1, SWT.PUSH);
			button1.setImage(style.getImage());
			button1.setToolTipText(style.getText());
			button1.setData(style);
			button1.addSelectionListener(layoutChangeListener);
			button1.pack();
		}
		
		composite1.pack();

		Point size = composite1.getSize();
		buttonItem1.setControl(composite1);
		buttonItem1.setSize(buttonItem1.computeSize(size.x, size.y));
		
		Composite composite2 = new Composite(coolBar, SWT.NONE);
		composite2.setLayout(new GridLayout(2, true));

		shortestButton = new Button(composite2, SWT.PUSH);
		shortestButton.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_SHORTEST_PATH));
		shortestButton.setToolTipText("Shortest Path");
		shortestButton.setData(ImageButton.SHORTEST);
		shortestButton.addSelectionListener(imageButtonListener);
		shortestButton.pack();
		
		button1 = new Button(composite2, SWT.PUSH);
		button1.setImage(DBeaverIcons.getImage(UIIcon.CHART_BAR));
		button1.setToolTipText("Chart");
		button1.setData(ImageButton.CHART);
		button1.addSelectionListener(imageButtonListener);
		button1.pack();
		
		composite2.pack();

		size = composite2.getSize();
		buttonItem2.setControl(composite2);
		buttonItem2.setSize(buttonItem2.computeSize(size.x, size.y));

		Composite composite3 = new Composite(coolBar, SWT.NONE);
        composite3.setLayout(new GridLayout(4, true));

        button1 = new Button(composite3, SWT.PUSH);
        button1.setImage(DBeaverIcons.getImage(UIIcon.PROPERTIES));
        button1.setToolTipText("Value");
        button1.setData(ImageButton.VALUE);
        button1.addSelectionListener(imageButtonListener);
        button1.pack();
        
		button1 = new Button(composite3, SWT.PUSH);
		button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_DESIGN));
		button1.setToolTipText("Design Editor");
		button1.setData(ImageButton.DESIGN);
		button1.addSelectionListener(imageButtonListener);
		button1.pack();
		
		button1 = new Button(composite3, SWT.PUSH);
		button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_CAPTURE));
		button1.setToolTipText("Visualization Capture");
		button1.setData(ImageButton.CAPTURE);
		button1.addSelectionListener(imageButtonListener);
		button1.pack();

		button1 = new Button(composite3, SWT.PUSH);
		button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_CSV_FILE));
		button1.setToolTipText("To CSV File");
		button1.setData(ImageButton.TO_CSV);
		button1.addSelectionListener(imageButtonListener);
		button1.pack();

		composite3.pack();

		size = composite3.getSize();
		buttonItem3.setControl(composite3);
		buttonItem3.setSize(buttonItem3.computeSize(size.x, size.y));
		
		Composite composite4 = new Composite(coolBar, SWT.NONE);
		composite4.setLayout(new GridLayout(4, false));
		composite4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		resultLabel = new Label(composite4, SWT.READ_ONLY | SWT.CENTER);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 4;
        resultLabel.setLayoutData(gd);
		resultLabel.setText("Edge : " + "00000" + " Node : " + "00000");

		composite4.pack();
		
		size = composite4.getSize();
		buttonItem4.setControl(composite4);
		buttonItem4.setSize(buttonItem4.computeSize(size.x, size.y));
		
		Composite composite5 = new Composite(coolBar, SWT.NONE);
		composite5.setLayout(new GridLayout(4, false));
		composite5.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		
		InfoLabel = new Label(composite5, SWT.READ_ONLY | SWT.CENTER);
		//InfoLabel.setText("Edge : " + "00000" + " Node : " + "00000");
		
		composite5.pack();
		
		size = composite5.getSize();
		buttonItem5.setControl(composite5);
		buttonItem5.setSize(buttonItem5.computeSize(size.x, size.y));
		
	}

	private static Label createHorizontalLine(Composite parent, int hSpan, int vIndent) {
		Label horizontalLine = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
		gd.horizontalSpan = hSpan;
		gd.verticalIndent = vIndent;
		horizontalLine.setLayoutData(gd);
		return horizontalLine;
	}

	private void ShowVisualizaion(boolean append) {
		dataSet();
		drawGraph();
	}

    private boolean addNeo4jNode(
            ResultSetModel model, DBDAttributeBinding attr, ResultSetRow row, String cellString) {
        Object cellValue = model.getCellValue(attr, row);

    	final String ID_KEY = NODE_EDGE_ID;
    	final String LABEL_KEY = NODE_LABEL;
        
        LinkedHashMap<String, Object> attrList = new LinkedHashMap<>();
        String id = "";
        List<String> labels;
        
        if (cellValue instanceof LinkedHashMap) {
        	attrList.putAll((LinkedHashMap)cellValue);
        }
        
        String regex = "[\\[\\]]";
        id = String.valueOf(attrList.get(ID_KEY)).replaceAll(regex, "");
        if (attrList.get(LABEL_KEY) instanceof String) {
        	List<String> tempLabels = new ArrayList<>();
        	tempLabels.add(String.valueOf(attrList.get(LABEL_KEY)));
        	labels = tempLabels;
        } else {
        	labels = (List<String>)attrList.get(LABEL_KEY);
        }
        
        attrList.remove(ID_KEY);
        attrList.remove(LABEL_KEY);

        DBDAttributeNodeList.put(id, attr);
        resultSetRowNodeList.put(id, row);
        displayStringNodeList.put(id, cellString);
        return visualGraph.addNode(id, labels, attrList) == null ? false : true;
    }

    private boolean addNeo4jEdge(ResultSetModel model,
    		DBDAttributeBinding attr, ResultSetRow row, String cellString) {
    	final String ID_KEY = NODE_EDGE_ID;
    	final String LABEL_KEY = EDGE_TYPE;
    	final String SID_KEY = NEO4J_EDGE_START_ID;
    	final String TID_KEY = NEO4J_EDGE_END_ID;
    	
    	LinkedHashMap<String, Object> attrList = new LinkedHashMap<>();
        Object cellValue = model.getCellValue(attr, row);

        String id = "", sId = "", tId = "";
        List<String> types;

        if (cellValue instanceof LinkedHashMap) {
        	attrList.putAll((LinkedHashMap)cellValue);
        } else {
        	return false;
        }
        
        String regex = "[\\[\\]]";
        id = String.valueOf(attrList.get(ID_KEY)).replaceAll(regex, "");
        if (attrList.get(LABEL_KEY) instanceof String) {
        	List<String> tempTypes = new ArrayList<>();
        	tempTypes.add(String.valueOf(attrList.get(LABEL_KEY)));
        	types = tempTypes;
        } else {
        	types = (List<String>)attrList.get(LABEL_KEY);
        }
        sId = String.valueOf(attrList.get(SID_KEY)).replaceAll(regex, "");
        tId = String.valueOf(attrList.get(TID_KEY)).replaceAll(regex, "");
        
        attrList.remove(ID_KEY);
        attrList.remove(LABEL_KEY);
        attrList.remove(SID_KEY);
        attrList.remove(TID_KEY);

        DBDAttributeEdgeList.put(id, attr);
        resultSetRowEdgeList.put(id, row);
        displayStringEdgeList.put(id, cellString);

        return visualGraph.addEdge(id, types, sId, tId, attrList)
                        == null
                ? false
                : true;
    }

	StringBuilder fixBuffer = new StringBuilder();

	private String getCellString(ResultSetModel model, DBDAttributeBinding attr, ResultSetRow row,
			DBDDisplayFormat displayFormat) {
		Object cellValue = model.getCellValue(attr, row);
		if (cellValue instanceof DBDValueError) {
			return ((DBDValueError) cellValue).getErrorTitle();
		}
		if (cellValue instanceof Number
				&& controller.getPreferenceStore().getBoolean(ModelPreferences.RESULT_NATIVE_NUMERIC_FORMAT)) {
			displayFormat = DBDDisplayFormat.NATIVE;
		}

		String displayString = attr.getValueHandler().getValueDisplayString(attr, cellValue, displayFormat);

		if (displayString.isEmpty() && showNulls && DBUtils.isNullValue(cellValue)) {
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
			if (c < ' '/* || (c > 127 && c < 255) */) {
				c = ' ';
			}
			fixBuffer.append(c);
		}

		return fixBuffer.toString();
	}

	@Override
	public void formatData(boolean refreshData) {
		// controller.refreshData(null);
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
		return Collections.singletonMap(TextTransfer.getInstance(), curSelection);
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
		public Object getFirstElement() {
			return curSelection;
		}

		@Override
		public Iterator<String> iterator() {
			return toList().iterator();
		}

		@Override
		public int size() {
			return curSelection == null ? 0 : 1;
		}

		@Override
		public Object[] toArray() {
			return curSelection == null ? new Object[0] : new Object[] { curSelection };
		}

		@Override
		public List<String> toList() {
			return curSelection == null ? Collections.emptyList() : Collections.singletonList(curSelection);
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@NotNull
		@Override
		public IResultSetController getController() {
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
		public List<ResultSetRow> getSelectedRows() {
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

	private void setLayoutManager(LayoutStyle layoutStyle) {
		visualGraph.setLayoutAlgorithm(layoutStyle);
	}
	
	private void setDefaultLayoutManager() {
		visualGraph.setDefaultLayoutAlgorithm();
	}

    private void createGraphListner() {
        visualGraph.setVertexSelectAction(
                (String id) -> {
                    curSelection = displayStringNodeList.get(id);
                    controller.setCurrentRow(resultSetRowNodeList.get(id));
                    curAttribute = DBDAttributeNodeList.get(id);
                    fireSelectionChanged(new VisualizationSelectionImpl());
                });

        visualGraph.setEdgeSelectAction(
                (String id) -> {
                    curSelection = displayStringEdgeList.get(id);
                    controller.setCurrentRow(resultSetRowEdgeList.get(id));
                    curAttribute = DBDAttributeEdgeList.get(id);
                    fireSelectionChanged(new VisualizationSelectionImpl());
                });
    }
	private void saveImage() {
	    
		if(visualGraph != null) {
			FileDialog fileDialog = new FileDialog(visualGraph.getGraphModel().getShell(), SWT.SAVE);
			fileDialog.setFilterExtensions(new String[] {"*.jpg"});
			fileDialog.setFilterNames(new String[] {"jpg Image File"});
			String filename = fileDialog.open();
			if (filename == null) {
				return;
			} else if (!filename.toLowerCase().contains(".jpg")){
				filename = filename + ".jpg";
			}
				
			ImageData imageData = visualGraph.getCaptureImage();
			
			if (imageData == null) {
				return;
			}
			
			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] { imageData };
			imageLoader.save(filename, SWT.IMAGE_JPEG);
		}
	}
	
	private void saveCSV() {
		visualGraph.exportCSV();
	}
	
//    private final SelectionListener imageButtonListener = new SelectionAdapter() {
//		@Override
//		public void widgetSelected(SelectionEvent e) {
//			if (e.widget != null && e.widget.getData() != null) {
//				switch((ImageButton) e.widget.getData()) {
//					case CAPTURE :
//					    saveImage();
//						break;
//					case SHORTEST :
//					    break;
//					case TO_CSV :
//					    break;
//					default :
//						break;
//				}
//			}
//		}
//	};
    
    public void setMiniMapVisible(boolean visible) {
        if (visualGraph != null) {
        	visualGraph.setMiniMapVisible(visible);
       }
	}
    
    private void setShortestMode(boolean status) {
        if (visualGraph != null) {
            visualGraph.setShortestMode(status);
            setColorShortestButton(status);
        }
    	
    }

    private void setColorShortestButton(boolean shortestStatus) {
        if (shortestButton != null && !shortestButton.isDisposed()) {
            if (shortestStatus) {
                shortestButton.setBackground(new Color(200, 200, 200));
            } else {
                shortestButton.setBackground(null);
            }
        }
    }
    
    class TurboRowData {
    	public boolean isEdge;
    	public String label;
    	public int startIdx;
    	public int endIdx;
    	
    	TurboRowData(String label, boolean isEdge, int startIdx, int endIdx) {
    		this.label = label;
			this.isEdge = isEdge; 
			this.startIdx = startIdx;
			this.endIdx = endIdx;
		}
    	
    	TurboRowData(String label, boolean isEdge, int startIdx) {
    		this.label = label;
			this.isEdge = isEdge; 
			this.startIdx = startIdx;
			this.endIdx = 0;
		}
    }
    
    class NEO4JRowData {
    	public int idx;
    	public boolean isEdge;
    	public DBDAttributeBinding attr;
    	
    	NEO4JRowData(int idx, boolean isEdge, DBDAttributeBinding attr) {
    		this.idx = idx;
			this.isEdge = isEdge; 
			this.attr = attr;
		}
    }
    
    private void dataSet() {
    	DBPPreferenceStore prefs = getController().getPreferenceStore();
		String graphType = "";

        DBDDisplayFormat displayFormat =
                DBDDisplayFormat.safeValueOf(
                        prefs.getString(ResultSetPreferences.RESULT_TEXT_VALUE_FORMAT));

		ResultSetModel model = controller.getModel();
		List<DBDAttributeBinding> attrs = model.getVisibleAttributes();

		List<ResultSetRow> allRows = model.getAllRows();

        if (visualGraph != null) {
            if (controller != null && controller.getDataContainer() != null) {
                currentQuery = controller.getDataContainer().getName();
                visualGraph.setCurrentQuery(currentQuery, allRows.size());
            }
        }
        
        List<Object> nodeRowData = new ArrayList<>();
        List<Object> edgeRowData = new ArrayList<>();
        TurboRowData temp = null;
        
        for (int i = 0; i < attrs.size(); i++) { // classify 
        	DBDAttributeBinding attr = attrs.get(i);
        	graphType = attrs.get(i).getTypeName();
        	
        	if (graphType == "NODE") { // Neo4j Node
        		nodeRowData.add(new NEO4JRowData(i, false, attr));
        	} else if (graphType == "RELATIONSHIP") { // Neo4j Edge
        		edgeRowData.add(new NEO4JRowData(i, true, attr));
        	} else { //TurboGraph++
        		String label = attrs.get(i).getMetaAttribute().getEntityName();
        		if (!label.isEmpty()) {
	            	if (attrs.get(i).getName().equals(NODE_EDGE_ID)
	            			&& attrs.get(i+1).getName().equals(TURBOGRAPH_EDGE_START_ID)) {
	            		temp = new TurboRowData(label, true, i);
	            		edgeRowData.add(temp);
	            	} else if (attrs.get(i).getName().equals(NODE_EDGE_ID)) {
	            		temp = new TurboRowData(label, false, i);
	            		nodeRowData.add(temp);
	            	} else {
	            		if (temp != null) {
	            			temp.endIdx = i;
	            		}
	            	}
        		}
        	}
        }
        
    	for (ResultSetRow row : allRows) { // Add Node
    		for (Object obj : nodeRowData) {
    			if (obj instanceof NEO4JRowData) {
    				NEO4JRowData data = (NEO4JRowData)obj;
    				String displayString = getCellString(model, data.attr, row, displayFormat);
                    addNeo4jNode(model, data.attr, row, displayString);
    			} else if (obj instanceof TurboRowData) {
    				TurboRowData data = (TurboRowData)obj;
    				List<String> multiLabel = new ArrayList<>(); //temp
    				multiLabel.add(data.label);
        			LinkedHashMap<String, Object> attrMap = new LinkedHashMap<>();
        			String id = "";
    				for (int j = data.startIdx ; j <= data.endIdx ; j++) {
    					if (j == data.startIdx) {
    						id = row.getValues()[j].toString();
    					} else {
    						attrMap.put(attrs.get(j).getLabel(), row.getValues()[j]);
    					}
    				}
    				visualGraph.addNode(id, multiLabel, attrMap);
    			}
    		}
        }
    	
    	for (ResultSetRow row : allRows) { // Add Edge
    		for (Object obj : edgeRowData) {
    			if (obj instanceof NEO4JRowData) {
    				NEO4JRowData data = (NEO4JRowData)obj;
    				String displayString = getCellString(model, data.attr, row, displayFormat);
                    addNeo4jEdge(model, data.attr, row, displayString);
    			} else if (obj instanceof TurboRowData) {
    				TurboRowData data = (TurboRowData)obj;
    				List<String> multiLabel = new ArrayList<>(); //temp
    				multiLabel.add(data.label);
    				LinkedHashMap<String, Object> attrMap = new LinkedHashMap<>();
        			String id = "" , sid = "", tid = "";
    				for (int j = data.startIdx ; j <= data.endIdx ; j++) {
    					if (j == data.startIdx) {
    						id = row.getValues()[j].toString();
    						j++;
    						sid = row.getValues()[j].toString();
    						j++;
    						tid = row.getValues()[j].toString();
    					} else {
    						attrMap.put(attrs.get(j).getLabel(), row.getValues()[j]);
    					}
    				}
    				visualGraph.addEdge(id, multiLabel, sid, tid, attrMap);
    			}
    		}
        }
    }
    
    private void drawGraph() {
    	int nodesNum = visualGraph.getNumNodes();
		int sqrt = (int) Math.sqrt(nodesNum);
		int compositeSizeX = graphTopComposite.getSize().x - 100;
		int compositeSizeY = graphTopComposite.getSize().y - 100;
		double drawSizeX = sqrt * 162;
		double drawSizeY = sqrt * 129;
        
		if (visualGraph != null) {
            resultLabel.setText(
                    "Node : " + visualGraph.getNumNodes() + " Edge : " + visualGraph.getNumEdges());
			
			if ( compositeSizeX > drawSizeX){
			    drawSizeX = compositeSizeX;
			}
			
			if ( compositeSizeY > drawSizeY){
			    drawSizeY = compositeSizeY;
            }
			
			//InfoLabel.setText(drawSizeX + " X " + drawSizeY); 
			
			if (!init) {
			    visualGraph.drawGraph(drawSizeX, drawSizeY);
			    init = true;
			} else {
			    visualGraph.drawGraph(drawSizeX, drawSizeY);
			    //visualGraph.redraw();
			}
			
		}
    }
}
