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
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.zest.core.viewers.internal.ZoomManager;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.VerticalLayoutAlgorithm;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.ModelPreferences;
import org.jkiss.dbeaver.ext.turbographpp.graphmodel.GephiModel;
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
import org.jkiss.dbeaver.ui.controls.resultset.visual.RefreshThread.Refreshable;
import org.jkiss.dbeaver.ui.editors.TextEditorUtils;
import org.jkiss.utils.CommonUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class VisualizationPresentation extends AbstractPresentation implements IAdaptable, Refreshable {

	public static final int MOUSE_WHELL_UP = 5;
	public static final int MOUSE_WHELL_DOWN = -5;
	
	public static final int ZOOM_MIN = 50;
	public static final int ZOOM_MAX = 500;
	
	public static final int CTRL_KEYCODE = 0x40000;
	
	// Zest Graph Layout
	private enum Layout {
		HORIZONTAL, HORIZONTAL_TREE, VERTICAL, VERTICAL_TREE, DIRECTED, GRID, HORIZONTAL_SHIFT, RADIAL, SPRING
	}

	// for Other ImageButton
	private enum ImageButton {
		SHORTEST, CAPTURE, TO_CSV
	}
	
	private Composite composite;
	private Composite mainComposite;
	private Composite menuBarComposite;
	private Composite graphTopComposite;
	private MiniMap miniMap;
	
	private DBDAttributeBinding curAttribute;
	private String curSelection;
	public boolean activated;
	private boolean showNulls;
	private Font monoFont;
	private GephiModel gephiModel = new GephiModel();
	
	private Graph visualGraph;
	
	private Label nodeLabel;
	private Combo nodePropertyListCombo;
	private Label edgeLabel;
	private Combo edgePropertyListCombo;
	private CoolBar coolBar;
	private Label resultLabel;

	private Color[] colors;
	private HashSet<String> propertyList = new HashSet<>();
	private HashMap<String, DBDAttributeBinding> DBDAttributeNodeList = new HashMap<>();
	private HashMap<String, DBDAttributeBinding> DBDAttributeEdgeList = new HashMap<>();
	private HashMap<String, ResultSetRow> resultSetRowNodeList = new HashMap<>();
	private HashMap<String, ResultSetRow> resultSetRowEdgeList = new HashMap<>();
	private HashMap<String, String> displayStringNodeList = new HashMap<>();
	private HashMap<String, String> displayStringEdgeList = new HashMap<>();
	
	private Layout defaultLayoutAlgorithm = Layout.SPRING;

	// ContextAction
	private MenuManager manager;
	private Action redoAction;
	private Action undoAction;
	private Action highlightAction;
	private Action deleteAction;
	private Action shortestPathAction;

	private Object seletedItem = null;

	private ZoomManager zoomManager;
	private int zoomCount = 100;
	private boolean zoomMode = false;
	
	private boolean init = false;
	
	private RefreshThread miniMapThread = null;
	
	@Override
	public void createPresentation(@NotNull final IResultSetController controller, @NotNull Composite parent) {
		super.createPresentation(controller, parent);

		colors = new Color[] { new Color(new RGB(158, 204, 255)), new Color(new RGB(204, 178, 255)),
				new Color(new RGB(204, 255, 255)), new Color(new RGB(102, 255, 178)), new Color(new RGB(192, 192, 192)),
				new Color(new RGB(204, 255, 102)), new Color(new RGB(255, 255, 153)), new Color(new RGB(255, 153, 153)),
				new Color(new RGB(204, 255, 103)), new Color(new RGB(255, 153, 255)), };
		composite = parent;
		GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 5;
        layout.marginWidth = 5;
        
        GridData gd_MainComposite = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
        
		mainComposite = new Composite(composite, SWT.NONE);
		mainComposite.setLayout(layout);
		mainComposite.setLayoutData(gd_MainComposite);
		
		menuBarComposite = new Composite(mainComposite, SWT.NONE);
		menuBarComposite.setLayout(new GridLayout(8, false));
        
		graphTopComposite = new Composite(mainComposite, SWT.NONE);
		graphTopComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        graphTopComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        
		nodeLabel = new Label(menuBarComposite, SWT.READ_ONLY);
		nodeLabel.setText("Nodes Property :");
		nodePropertyListCombo = new Combo(menuBarComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		nodePropertyListCombo.setEnabled(false);
		nodePropertyListCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if (gephiModel != null) {
					String temp = nodePropertyListCombo.getText();
					gephiModel.updateZestNode(visualGraph, temp);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		addMenuCoolbar(menuBarComposite, nodeLabel.getSize());

		visualGraph = new Graph(graphTopComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.DOUBLE_BUFFERED | SWT.NO_REDRAW_RESIZE);
		visualGraph.setCursor(graphTopComposite.getDisplay().getSystemCursor(SWT.CURSOR_IBEAM));
		visualGraph.setForeground(UIStyles.getDefaultTextForeground());
		visualGraph.setBackground(UIStyles.getDefaultTextBackground());
		visualGraph.setFont(UIUtils.getMonospaceFont());
		visualGraph.setLayout(new FillLayout(SWT.FILL));
		visualGraph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		zoomManager = new ZoomManager(visualGraph.getRootLayer(), visualGraph.getViewport());
		
	    createMiniMap();
		
		createHorizontalLine(parent, 1, 0);

		createZestListner();

		TextEditorUtils.enableHostEditorKeyBindingsSupport(controller.getSite(), visualGraph);

		applyCurrentThemeSettings();

		if (visualGraph != null) {
			activateTextKeyBindings(controller, visualGraph);
		}
		trackPresentationControl();
		registerContextMenu();
		
		visualGraph.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == CTRL_KEYCODE) {
					zoomMode = false;
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == CTRL_KEYCODE) {
					zoomMode = true;
				}
			}
		});
		
		visualGraph.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseScrolled(MouseEvent e) {
				if (zoomMode) {
					if (e != null) {
						if (e.count == MOUSE_WHELL_UP) {
							zoomOut();
						} else if (e.count == MOUSE_WHELL_DOWN){
							zoomIn();
						}
						
					} 
				}
			}
		});
	}

	@Override
	public void dispose() {
		if (monoFont != null) {
			UIUtils.dispose(monoFont);
			monoFont = null;
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
	        return visualGraph;
	    } 
	    return graphTopComposite;
	}

	@Override
	public void refreshData(boolean refreshMetadata, boolean append, boolean keepState) {
		if (refreshMetadata) {
			seletedItem = null;
			if (gephiModel != null) {
    			gephiModel.clear();
    			gephiModel.clearGraph(visualGraph);
			}
			setLayoutManager(defaultLayoutAlgorithm);
			propertyList.clear();
			DBDAttributeNodeList.clear();
			DBDAttributeEdgeList.clear();
			resultSetRowNodeList.clear();
			resultSetRowEdgeList.clear();
			displayStringNodeList.clear();
			displayStringEdgeList.clear();

			if (edgePropertyListCombo != null) {
				edgePropertyListCombo.removeAll();
				edgePropertyListCombo.add("label");
				edgePropertyListCombo.select(0);
			}

			if (nodePropertyListCombo != null) {
				nodePropertyListCombo.removeAll();
				nodePropertyListCombo.add("label");
				nodePropertyListCombo.select(0);
			}
			ShowVisualizaion(append);
		}
	}
	
	private final SelectionListener layoutChangeListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.widget != null) {
				setLayoutManager((Layout) e.widget.getData());
			}
		}
	};
	
	private final SelectionListener imageButtonListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.widget != null && e.widget.getData() != null) {
				switch((ImageButton) e.widget.getData()) {
					case CAPTURE :
					    saveImage();
						break;
					case SHORTEST :
					    break;
					case TO_CSV :
					    break;
					default :
						break;
				}
			}
		}
	};
	
	private void addMenuCoolbar(Composite parent, Point size) {
		coolBar = new CoolBar(parent, SWT.NONE);
		coolBar.setBackground(parent.getBackground());

		CoolItem buttonItem1 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);

		Composite composite1 = new Composite(coolBar, SWT.NONE);
		composite1.setLayout(new GridLayout(7, true));

		Button button1 = new Button(composite1, SWT.PUSH);
		button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_LAYOUT_CIRCLE));
		button1.setToolTipText("Circle(Radial) Layout");
		button1.setData(Layout.RADIAL);
		button1.addSelectionListener(layoutChangeListener);
		button1.pack();

		button1 = new Button(composite1, SWT.PUSH);
		button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_LAYOUT_FORCE_DIRECTED));
		button1.setToolTipText("Spring Layout");
		button1.setData(Layout.SPRING);
		button1.addSelectionListener(layoutChangeListener);
		button1.pack();

		button1 = new Button(composite1, SWT.PUSH);
		button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_LAYOUT_GRID));
		button1.setToolTipText("Grid Layout");
		button1.setData(Layout.GRID);
		button1.addSelectionListener(layoutChangeListener);
		button1.pack();

		button1 = new Button(composite1, SWT.PUSH);
		button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_LAYOUT_HORIZONTAL));
		button1.setToolTipText("Horizontal Layout");
		button1.setData(Layout.HORIZONTAL);
		button1.addSelectionListener(layoutChangeListener);
		button1.pack();

		button1 = new Button(composite1, SWT.PUSH);
		button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_LAYOUT_TREE_HORIZONTAL));
		button1.setToolTipText("Horizontal-Tree Layout");
		button1.setData(Layout.HORIZONTAL_TREE);
		button1.addSelectionListener(layoutChangeListener);
		button1.pack();

		button1 = new Button(composite1, SWT.PUSH);
		button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_LAYOUT_VERTICAL));
		button1.setToolTipText("Vertical Layout");
		button1.setData(Layout.VERTICAL);
		button1.addSelectionListener(layoutChangeListener);
		button1.pack();

		button1 = new Button(composite1, SWT.PUSH);
		button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_LAYOUT_TREE_VERTICAL));
		button1.setToolTipText("Vertical-Tree Layout");
		button1.setData(Layout.VERTICAL_TREE);
		button1.addSelectionListener(layoutChangeListener);
		button1.pack();
		composite1.pack();

		size = composite1.getSize();
		buttonItem1.setControl(composite1);
		buttonItem1.setSize(buttonItem1.computeSize(size.x, size.y));

		Composite composite2 = new Composite(coolBar, SWT.NONE);
		composite2.setLayout(new GridLayout(2, true));

		button1 = new Button(composite2, SWT.PUSH);
		button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_SHORTEST_PATH));
		button1.setToolTipText("Shortest Path");
		button1.setData(ImageButton.SHORTEST);
		button1.addSelectionListener(imageButtonListener);
		button1.pack();
		composite2.pack();

		CoolItem buttonItem2 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);
		size = composite2.getSize();
		buttonItem2.setControl(composite2);
		buttonItem2.setSize(buttonItem2.computeSize(size.x, size.y));

		Composite composite3 = new Composite(coolBar, SWT.NONE);
		composite3.setLayout(new GridLayout(2, true));

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

		CoolItem buttonItem3 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);
		size = composite3.getSize();
		buttonItem3.setControl(composite3);
		buttonItem3.setSize(buttonItem3.computeSize(size.x, size.y));

		Composite composite4 = new Composite(coolBar, SWT.NONE);
		composite4.setLayout(new GridLayout(2, true));

		resultLabel = new Label(composite4, SWT.READ_ONLY | SWT.RIGHT);
		resultLabel.setText("Node : " + 0 + " Edge : " + 0);

		composite4.pack();

		CoolItem buttonItem4 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);
		size = composite4.getSize();
		buttonItem4.setControl(composite4);
		buttonItem4.setSize(composite4.computeSize(size.x, size.y));
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
		DBPPreferenceStore prefs = getController().getPreferenceStore();
		String graphType = "";

		DBDDisplayFormat displayFormat = DBDDisplayFormat
				.safeValueOf(prefs.getString(ResultSetPreferences.RESULT_TEXT_VALUE_FORMAT));

		ResultSetModel model = controller.getModel();
		List<DBDAttributeBinding> attrs = model.getVisibleAttributes();

		List<ResultSetRow> allRows = model.getAllRows();

		for (int i = 0; i < attrs.size(); i++) {
			DBDAttributeBinding attr = attrs.get(i);
			graphType = attrs.get(i).getTypeName();

			if (graphType == "NODE") {
				for (ResultSetRow row : allRows) {
					String displayString = getCellString(model, attr, row, displayFormat);
					addNode(attr, row, displayString, colors[(i % 10)]);
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

		if (!propertyList.isEmpty()) {
			nodePropertyListCombo.setEnabled(true);
		}

		int compositeSizeX = composite.getSize().x;
        int compositeSizeY = composite.getSize().y;
		int drawSizeX = visualGraph.getNodes().size() * 50;
		int drawSizeY =  visualGraph.getNodes().size() * 40;
        
        System.out.println("ShowVisualizaion composite x :" + compositeSizeX + " y : " + compositeSizeY);
		
		if (visualGraph != null) {
			resultLabel.setText("Node : " + visualGraph.getNodes().size() + " Edge : " + visualGraph.getConnections().size());
			
			if ( compositeSizeX > drawSizeX){
			    drawSizeX = compositeSizeX;
			}
			
			if ( compositeSizeY > drawSizeY){
			    drawSizeY = compositeSizeY;
            }
			
			//Set zoom default value
			zoomManager.setZoomAsText("100%");
			
			if (!init) {
			    visualGraph.setPreferredSize(drawSizeX, drawSizeY);
			    init = true;
			} else {
			    visualGraph.setPreferredSize(drawSizeX, drawSizeY);
			    visualGraph.redraw();
			}
		}
		
		if (miniMapThread == null) {
		    miniMapThread = new RefreshThread(this, 1500);
		    miniMapThread.start();
		}
		
	}

	private boolean addNode(DBDAttributeBinding attrs, ResultSetRow row, String cellString, Color color) {
		int idx = 0;
		HashMap<String, Object> attrList = new HashMap<>();
		String regex = "[\\[\\]\\{\\}]";
		String tempCellString = cellString.replaceAll(regex, "");
		String[] tempValue = tempCellString.split(", ");
		String prvKey = "";

		try {
			for (int i = 0; i < tempValue.length; i++) {
				idx = tempValue[i].indexOf("=");
				if (i < 2) {
					tempValue[i] = tempValue[i].substring(idx + 1, tempValue[i].length());
				} else {
					if (idx > 0) {
						attrList.put(tempValue[i].substring(0, idx),
								tempValue[i].substring(idx + 1, tempValue[i].length()));
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

			return gephiModel.addNode(visualGraph, tempValue[0], tempValue[1], attrList, color);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean addEdge(DBDAttributeBinding attrs, ResultSetRow row, String cellString) {
		String[] tempValue;
		int idx = 0;
		int i = 0;
		String regex = "[\\{\\}]";
		String tempCellString = cellString.replaceAll(regex, "");
		HashMap<String, String> attrList = new HashMap<>();
		String prvKey = "";

		do {
			tempValue = tempCellString.split(", ");
		} while (tempCellString.length() == 0);

		try {
			for (i = 0; i < tempValue.length; i++) {
				idx = tempValue[i].indexOf("=");
				if (i < 4) {
					tempValue[i] = tempValue[i].substring(idx + 1, tempValue[i].length());
				} else {
					if (idx > 0) {
						attrList.put(tempValue[i].substring(0, idx),
								tempValue[i].substring(idx + 1, tempValue[i].length()));
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

		return gephiModel.addEdge(visualGraph, tempValue[0], tempValue[1], tempValue[2], tempValue[3], attrList);
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

	private void setLayoutManager(Layout layout) {
		defaultLayoutAlgorithm = layout;
		switch (layout) {
		case VERTICAL:
			visualGraph.setLayoutAlgorithm(new VerticalLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;

		case HORIZONTAL_TREE:
			visualGraph.setLayoutAlgorithm(new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case VERTICAL_TREE:
			visualGraph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case HORIZONTAL:
			visualGraph.setLayoutAlgorithm(new HorizontalLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case DIRECTED:
			visualGraph.setLayoutAlgorithm(new DirectedGraphLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case GRID:
			visualGraph.setLayoutAlgorithm(new GridLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case SPRING:
			visualGraph.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case HORIZONTAL_SHIFT:
			visualGraph.setLayoutAlgorithm(new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case RADIAL:
			visualGraph.setLayoutAlgorithm(new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		}
	}

	protected void createContextMenuAction() {
		redoAction = new Action("&Redo", null) {
			public void run() {
			}
		};
		redoAction.setEnabled(false);

		undoAction = new Action("&Undo", null) {
			public void run() {
			}
		};
		undoAction.setEnabled(false);

		highlightAction = new Action("&Highlight", null) {
			public void run() {
				if (gephiModel != null) {
					if (seletedItem.getClass().equals(GraphNode.class)) {
						gephiModel.setHighlight(visualGraph, (GraphNode) seletedItem);
					}
				}
			}
		};
		highlightAction.setEnabled(true);

		deleteAction = new Action("&Delete", null) {
			public void run() {
			}
		};
		deleteAction.setEnabled(false);

		shortestPathAction = new Action("&Shortest Path", null) {
			public void run() {
			}
		};
		shortestPathAction.setEnabled(false);
	}

	protected void registerContextMenu() {
		createContextMenuAction();
		manager = new MenuManager();
		getControl().setMenu(manager.createContextMenu(getControl()));
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager m) {
				contextMenuAboutToShow(m);
			}
		});
	}

	private void contextMenuAboutToShow(IMenuManager m) {
		if (seletedItem != null && seletedItem.getClass().equals(GraphNode.class)) {
			highlightAction.setEnabled(true);
		} else {
			highlightAction.setEnabled(false);
		}

		undoAction.setEnabled(undoAction.isEnabled());
		redoAction.setEnabled(redoAction.isEnabled());
		deleteAction.setEnabled(deleteAction.isEnabled());
		shortestPathAction.setEnabled(shortestPathAction.isEnabled());
		manager.add(undoAction);
		manager.add(redoAction);
		manager.add(highlightAction);
		manager.add(deleteAction);
		manager.add(shortestPathAction);
	}

	private void createZestListner() {
		final ScrollBar verticalBar = visualGraph.getVerticalBar();
		verticalBar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});

		setLayoutManager(defaultLayoutAlgorithm);

		visualGraph.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object temp = null;
				gephiModel.unHighlight();

				if (visualGraph.getSelection() != null && visualGraph.getSelection().size() != 0) {
					temp = visualGraph.getSelection().get(0);
				}

				if (temp != null) {
					Object id = null;
					seletedItem = temp;
					if (temp.getClass() == GraphNode.class) {
						GraphNode tNode = (GraphNode) temp;
						id = tNode.getData();
						curSelection = displayStringNodeList.get(id);
						controller.setCurrentRow(resultSetRowNodeList.get(id));
						curAttribute = DBDAttributeNodeList.get(id);
					} else if (temp.getClass() == GraphConnection.class) {
						GraphConnection tConnection = (GraphConnection) temp;
						id = tConnection.getData();
						curSelection = displayStringEdgeList.get(id);
						controller.setCurrentRow(resultSetRowEdgeList.get(id));
						curAttribute = DBDAttributeEdgeList.get(id);
					}
					fireSelectionChanged(new VisualizationSelectionImpl());

				}
			}
		});
	}
	
	private void zoomIn() {
		if (zoomCount > 50) {
			zoomCount = zoomCount - 10;
			String zoomPercent = String.valueOf(zoomCount) + "%";
			zoomManager.setZoomAsText(zoomPercent);
		} 
	}
	
	private void zoomOut() {
		if (zoomCount < 500) {
			zoomCount = zoomCount + 10;
			String zoomPercent = String.valueOf(zoomCount) + "%";
			zoomManager.setZoomAsText(zoomPercent);
		}	
	}
	
	private Image graphImageCapture() {
	    Rectangle size = visualGraph.getContents().getBounds(); 
	    final Image image = new Image(null, size.width, size.height); 
	    GC gc = new GC(image); 
	    SWTGraphics swtGraphics = new SWTGraphics(gc); 
	    swtGraphics.translate(-1 * size.x, -1 * size.y); 
	    visualGraph.getContents().paint(swtGraphics); 
	    gc.dispose();
        
        return image;
    }
	
	private void saveImage() {
	    
	    Image captureImage = graphImageCapture();
	    
		if(captureImage != null) {
			FileDialog fileDialog = new FileDialog(visualGraph.getShell(), SWT.SAVE);
			fileDialog.setFilterExtensions(new String[] {"*.jpg"});
			fileDialog.setFilterNames(new String[] {"jpg Image File"});
			String filename = fileDialog.open();
			if (filename == null) {
				return;
			} else if (!filename.toLowerCase().contains(".jpg")){
				filename = filename + ".jpg";
			}
				
			ImageData imageData = captureImage.getImageData();
			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] { imageData };
			imageLoader.save(filename, SWT.IMAGE_JPEG);
			
			captureImage.dispose();
		}
	}
	
    @Override
    public void refreshWork() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (miniMap != null) {
                    miniMap.setImage(graphImageCapture());
                    miniMap.reDraw();
                }
            }
        });
    }
    
    private void createMiniMap() {
        miniMap = new MiniMap(graphTopComposite);
        miniMap.show();
        miniMap.addZoominListner(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                zoomIn();
                
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                
            }
        });
        
        miniMap.addZoomOutListner(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                zoomOut();
                
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                
            }
        });
    }
    
    public void setMiniMapVisible(boolean visible) {
        if (miniMap != null) {
            if (visible) {
                miniMap.show();
            } else {
                miniMap.remove();
            }
        }
    }
    

}
