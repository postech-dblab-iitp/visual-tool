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
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
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
import org.eclipse.swt.widgets.Shell;
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
import org.jkiss.dbeaver.ui.editors.TextEditorUtils;
import org.jkiss.utils.CommonUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class VisualizationPresentation extends AbstractPresentation implements IAdaptable {

	static final int MOUSE_WHELL_UP = 5;
	static final int MOUSE_WHELL_DOWN = -5;
	
	static final int ZOOM_MIN = 50;
	static final int ZOOM_MAX = 500;
	
	static final int CTRL_KEYCODE = 0x40000;
	
	// Zest Graph Layout
	protected enum Layout {
		HORIZONTAL, HORIZONTAL_TREE, VERTICAL, VERTICAL_TREE, DIRECTED, GRID, HORIZONTAL_SHIFT, RADIAL, SPRING
	}

	// for Other ImageButton
	protected enum ImageButton {
		SHORTEST, CAPTURE, TO_CSV
	}
	
	Composite composite;
	static Shell captureShell;

	private DBDAttributeBinding curAttribute;
	private String curSelection;
	public boolean activated;
	private boolean showNulls;
	private Font monoFont;
	private GephiModel gephiModel = new GephiModel();
	private static Graph graph;
	Label nodeLabel;
	Combo nodePropertyListCombo;
	Label edgeLabel;
	Combo edgePropertyListCombo;
	static CoolBar coolBar;
	static Label resultLabel;

	ToolBarManager toolBarManager;

	Color[] colors;
	HashSet<String> propertyList = new HashSet<>();
	HashMap<String, DBDAttributeBinding> DBDAttributeNodeList = new HashMap<>();
	HashMap<String, DBDAttributeBinding> DBDAttributeEdgeList = new HashMap<>();
	HashMap<String, ResultSetRow> resultSetRowNodeList = new HashMap<>();
	HashMap<String, ResultSetRow> resultSetRowEdgeList = new HashMap<>();
	HashMap<String, String> displayStringNodeList = new HashMap<>();
	HashMap<String, String> displayStringEdgeList = new HashMap<>();
	
	static Layout defaultLayoutAlgorithm = Layout.SPRING;

	// ContextAction
	MenuManager manager;
	Action redoAction;
	Action undoAction;
	Action highlightAction;
	Action deleteAction;
	Action shortestPathAction;

	Object seletedItem = null;

	Composite menuBarComposite;
	int resultLabelwidth = 0;

	static ZoomManager zoomManager;
	static Combo zoomCombo;
	static int zoomCount = 100;
	static boolean zoomMode = false;
	
	@Override
	public void createPresentation(@NotNull final IResultSetController controller, @NotNull Composite parent) {
		super.createPresentation(controller, parent);

		colors = new Color[] { new Color(new RGB(158, 204, 255)), new Color(new RGB(204, 178, 255)),
				new Color(new RGB(204, 255, 255)), new Color(new RGB(102, 255, 178)), new Color(new RGB(192, 192, 192)),
				new Color(new RGB(204, 255, 102)), new Color(new RGB(255, 255, 153)), new Color(new RGB(255, 153, 153)),
				new Color(new RGB(204, 255, 103)), new Color(new RGB(255, 153, 255)), };
		composite = parent;
		captureShell = parent.getShell();

		Composite composite2 = new Composite(parent, SWT.NONE);
		menuBarComposite = composite2;
		composite2.setLayout(new GridLayout(8, false));

		nodeLabel = new Label(composite2, SWT.READ_ONLY);
		nodeLabel.setText("Nodes Property :");
		nodePropertyListCombo = new Combo(composite2, SWT.DROP_DOWN | SWT.READ_ONLY);
		nodePropertyListCombo.setEnabled(false);
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

		addMenuCoolbar(composite2, nodeLabel.getSize());

		graph = new Graph(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		graph.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_IBEAM));
		graph.setForeground(UIStyles.getDefaultTextForeground());
		graph.setBackground(UIStyles.getDefaultTextBackground());
		// graph.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		graph.setFont(UIUtils.getMonospaceFont());
		GridData data = new GridData(GridData.FILL_BOTH);
		// data.horizontalSpan = 8;
		graph.setLayoutData(data);

		zoomManager = new ZoomManager(graph.getRootLayer(), graph.getViewport());
		createHorizontalLine(parent, 1, 0);

		createZestListner();

		TextEditorUtils.enableHostEditorKeyBindingsSupport(controller.getSite(), graph);

		applyCurrentThemeSettings();

		if (graph != null) {
			activateTextKeyBindings(controller, graph);
		}
		trackPresentationControl();
		registerContextMenu();
		
		graph.addKeyListener(new KeyListener() {
			
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
		
		graph.addMouseWheelListener(new MouseWheelListener() {
			
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

		composite.addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				menuBarComposite.setSize(composite.getSize());
			}

			@Override
			public void controlMoved(ControlEvent e) {
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

			graph.setFont(newFont);

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
		if (refreshMetadata) {
			seletedItem = null;
			gephiModel.clear();
			gephiModel.clearGraph(graph);
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
	
	private static SelectionListener layoutChangeListener = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.widget != null) {
				setLayoutManager((Layout) e.widget.getData());
			}
		}
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};
	
	private static SelectionListener imageButtonListener = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.widget != null && e.widget.getData() != null) {
				switch((ImageButton) e.widget.getData()) {
					case CAPTURE :
						imageCapture();
						break;
					default :
						break;
				}
			}
		}
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};

	private static void addMenuCoolbar(Composite parent, Point size) {
		coolBar = new CoolBar(parent, SWT.NONE);
		coolBar.setBackground(parent.getBackground());

		CoolItem buttonItem1 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);

		Composite composite1 = new Composite(coolBar, SWT.NONE);
		composite1.setLayout(new GridLayout(7, true));

		Button button1 = new Button(composite1, SWT.PUSH);
		button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_LAYOUT_CIRCLE));
		button1.setToolTipText("Circle Layout");
		button1.setData(Layout.RADIAL);
		button1.addSelectionListener(layoutChangeListener);
		button1.pack();

		button1 = new Button(composite1, SWT.PUSH);
		button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_LAYOUT_FORCE_DIRECTED));
		button1.setToolTipText("Force-Directed Layout");
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
		button1.setToolTipText("Visualation Capture");
		button1.setData(ImageButton.CAPTURE);
		button1.addSelectionListener(imageButtonListener);
		button1.pack();

		button1 = new Button(composite3, SWT.PUSH);
		button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_CSV_FILE));
		button1.setToolTipText("To CSV File");
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

		if (graph != null) {
			resultLabel.setText("Node : " + graph.getNodes().size() + " Edge : " + graph.getConnections().size());
			graph.setPreferredSize(graph.getNodes().size() * 50, graph.getNodes().size() * 40);
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

			return gephiModel.addNode(graph, tempValue[0], tempValue[1], attrList, color);
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

		return gephiModel.addEdge(graph, tempValue[0], tempValue[1], tempValue[2], tempValue[3], attrList);
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

	private static void setLayoutManager(Layout layout) {
		defaultLayoutAlgorithm = layout;
		switch (layout) {
		case VERTICAL:
			graph.setLayoutAlgorithm(new VerticalLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;

		case HORIZONTAL_TREE:
			graph.setLayoutAlgorithm(new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case VERTICAL_TREE:
			graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case HORIZONTAL:
			graph.setLayoutAlgorithm(new HorizontalLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case DIRECTED:
			graph.setLayoutAlgorithm(new DirectedGraphLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case GRID:
			graph.setLayoutAlgorithm(new GridLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case SPRING:
			graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case HORIZONTAL_SHIFT:
			graph.setLayoutAlgorithm(new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			break;
		case RADIAL:
			graph.setLayoutAlgorithm(new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
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
						gephiModel.setHighlight(graph, (GraphNode) seletedItem);
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

	protected void contextMenuAboutToShow(IMenuManager m) {
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

	protected void createZestListner() {
		final ScrollBar verticalBar = graph.getVerticalBar();
		verticalBar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});

		setLayoutManager(defaultLayoutAlgorithm);

		graph.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object temp = null;
				gephiModel.unHighlight();

				if (graph.getSelection() != null && graph.getSelection().size() != 0) {
					temp = graph.getSelection().get(0);
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
	
	static protected void zoomIn() {
		if (zoomCount > 50) {
			zoomCount = zoomCount - 10;
			String zoomPercent = String.valueOf(zoomCount) + "%";
			zoomManager.setZoomAsText(zoomPercent);
		} 
	}
	
	protected void zoomOut() {
		if (zoomCount < 500) {
			zoomCount = zoomCount + 10;
			String zoomPercent = String.valueOf(zoomCount) + "%";
			zoomManager.setZoomAsText(zoomPercent);
		}	
	}
	
	static protected void imageCapture() {
		graph.getParent().setRedraw(false);
        final Point originalSize = graph.getSize();
        final Point size = graph.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        Image image = new Image(graph.getDisplay(), size.x, size.y);
        final GC gc = new GC(image);

        graph.setSize(size);
        graph.print(gc);
        graph.setSize(originalSize);
		
        graph.getParent().setRedraw(true);
        
		if(image != null) {
			FileDialog fileDialog = new FileDialog(captureShell, SWT.SAVE);
			fileDialog.setFilterExtensions(new String[] {"*.jpg"});
			fileDialog.setFilterNames(new String[] {"jpg Image File"});
			String filename = fileDialog.open();
			if (filename == null) {
				return;
			} else {
				filename = filename + ".jpg";
			}
				
			ImageData imageData = image.getImageData();
			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] { imageData };
			imageLoader.save(filename, SWT.IMAGE_JPEG);
			image.dispose();
			
		}
	}
}
