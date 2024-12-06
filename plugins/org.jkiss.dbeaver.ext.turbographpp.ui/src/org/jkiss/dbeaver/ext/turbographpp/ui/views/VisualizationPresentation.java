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
package org.jkiss.dbeaver.ext.turbographpp.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.themes.ITheme;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.ModelPreferences;
import org.jkiss.dbeaver.ext.turbographpp.graph.FXGraph;
import org.jkiss.dbeaver.ext.turbographpp.graph.GraphBase.LayoutStyle;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.DataRowID;
import org.jkiss.dbeaver.ext.turbographpp.ui.internal.TurboGraphPPUIMessages;
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
import org.jkiss.dbeaver.ui.dialogs.BaseDialog;
import org.jkiss.dbeaver.ui.editors.TextEditorUtils;
import org.jkiss.utils.CommonUtils;

public class VisualizationPresentation extends AbstractPresentation implements IAdaptable {

    private IResultSetController controller;
    // for Other ImageButton
    private enum ImageButton {
        SHORTEST,
        VALUE,
        DESIGN,
        CHART,
        CAPTURE,
        TO_CSV,
        NEXT_DATA,
        ALL_DATA,
        DETACH_WINDOW,
        MINI_MAP
    }

    private Composite parentComposite;
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

    private Button shortestButton;
    private Button fetchNextButton;
    private Button fetchEndButton;

    private HashSet<Object> propertyList = new HashSet<>();
    private HashMap<String, DBDAttributeBinding> DBDAttributeNodeList = new HashMap<>();
    private HashMap<String, DBDAttributeBinding> DBDAttributeEdgeList = new HashMap<>();
    private HashMap<String, ResultSetRow> resultSetRowNodeList = new HashMap<>();
    private HashMap<String, ResultSetRow> resultSetRowEdgeList = new HashMap<>();
    private HashMap<String, String> displayStringNodeList = new HashMap<>();
    private HashMap<String, String> displayStringEdgeList = new HashMap<>();

    public static final String NODE_EDGE_ID = DataRowID.NODE_EDGE_ID;
    public static final String NODE_LABEL = DataRowID.NODE_LABEL;
    public static final String EDGE_TYPE = DataRowID.EDGE_TYPE;
    public static final String NEO4J_EDGE_START_ID = DataRowID.NEO4J_EDGE_START_ID;
    public static final String NEO4J_EDGE_END_ID = DataRowID.NEO4J_EDGE_END_ID;
    public static final String TURBOGRAPH_EDGE_START_ID = DataRowID.TURBOGRAPH_EDGE_START_ID;
    public static final String TURBOGRAPH_EDGE_END_ID = DataRowID.TURBOGRAPH_EDGE_END_ID;

    private String currentQuery = "";
    private int lastReadRowCount = 0;

    private DetachDialog detachDialog;
    private boolean detach = false;

    private List<Button> graphButtonList = new ArrayList<>();

    private int graphTapIndex = FXGraph.GRAPH_TAP;
    private boolean isAddMoreData = false;
    
    @Override
    public void createPresentation(
            @NotNull final IResultSetController controller, @NotNull Composite parent) {
        super.createPresentation(controller, parent);

        this.controller = controller;

        this.parentComposite = parent;
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 5;
        layout.marginWidth = 5;

        GridData gd_MainComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);

        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(layout);
        mainComposite.setLayoutData(gd_MainComposite);

        menuBarComposite = new Composite(mainComposite, SWT.NONE);
        menuBarComposite.setLayout(new GridLayout(1, false));
        menuBarComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));

        graphTopComposite = new Composite(mainComposite, SWT.NONE);
        graphTopComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        graphTopComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        detachDialog = new DetachDialog(parent);

        addMenuCoolbar(menuBarComposite);

        visualGraph =
                new FXGraph(
                        graphTopComposite, SWT.NONE, controller.getDataContainer().getDataSource());
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

        if (detachDialog != null) {
            detachDialog.close();
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
        fetchNextButton.setEnabled(controller.isHasMoreData());
        fetchEndButton.setEnabled(controller.isHasMoreData());

        if (refreshMetadata) {
            lastReadRowCount = 0;
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

            ShowVisualizaion(refreshMetadata, append);
        } else {
            setShortestMode(false);
            ShowVisualizaion(refreshMetadata, append);
        }
    }

    private final SelectionListener layoutChangeListener =
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (e.widget != null) {
                        setLayoutManager((LayoutStyle) e.widget.getData());
                    }
                }
            };

    private final SelectionListener imageButtonListener =
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (e.widget != null && e.widget.getData() != null) {
                        switch ((ImageButton) e.widget.getData()) {
                            case VALUE:
                                visualGraph.valueShow();
                                break;
                            case DESIGN:
                                visualGraph.designEditorShow();
                                break;
                            case CHART:
                                visualGraph.chartShow();
                                break;
                            case CAPTURE:
                                saveImage();
                                setShortestMode(false);
                                break;
                            case SHORTEST:
                                if (visualGraph != null) {
                                    boolean status = visualGraph.getShortestMode();
                                    setShortestMode(!status);
                                }
                                break;
                            case TO_CSV:
                                saveCSV();
                                setShortestMode(false);
                                break;
                            case NEXT_DATA:
                                if (controller != null) {
                                    controller.readNextSegment();
                                }
                                break;
                            case ALL_DATA:
                                if (controller != null) {
                                    controller.readAllData();
                                }
                                break;
                            case DETACH_WINDOW:
                                if (!detach) {
                                    detach = true;
                                    detachDialog.create();
                                    visualGraph.miniMapUpdate(
                                            detachDialog.getmainComposite(),
                                            detachDialog.getmainComposite().getShell());
                                    visualGraph.subDispose();
                                    mainComposite.setParent(detachDialog.getmainComposite());
                                    parentComposite.layout(true, true);
                                    detachDialog.open();
                                } else {
                                    detach = false;
                                    mainComposite.setParent(parentComposite);
                                    visualGraph.miniMapUpdate(
                                            mainComposite, mainComposite.getShell());
                                    visualGraph.subDispose();
                                    parentComposite.layout(true, true);
                                    detachDialog.close();
                                }
                                break;
                            case MINI_MAP:
                                miniMapToggle();
                                break;
                            default:
                                break;
                        }
                    }
                }
            };

    private void addMenuCoolbar(Composite parent) {
        coolBar = new CoolBar(parent, SWT.NONE);
        coolBar.setBackground(parent.getBackground());

        CoolItem buttonItem0 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);
        CoolItem buttonItem1 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);
        CoolItem buttonItem2 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);
        CoolItem buttonItem3 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);
        CoolItem buttonItem4 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);
        CoolItem buttonItem5 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);
        CoolItem buttonItem6 = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);

        Composite menuComposite0 = new Composite(coolBar, SWT.NONE);
        menuComposite0.setLayout(new GridLayout(1, true));

        Button detachButton = new Button(menuComposite0, SWT.PUSH);
        detachButton.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_DETACH_WINDOW));
        detachButton.setToolTipText(
                TurboGraphPPUIMessages.visualization_detach_window_button_tool_tip);
        detachButton.setData(ImageButton.DETACH_WINDOW);
        detachButton.addSelectionListener(imageButtonListener);

        menuComposite0.pack();

        Point size = menuComposite0.getSize();
        buttonItem0.setControl(menuComposite0);
        buttonItem0.setSize(buttonItem0.computeSize(size.x, size.y));

        Composite menuComposite1 = new Composite(coolBar, SWT.NONE);
        menuComposite1.setLayout(new GridLayout(1, true));

        Button MiniMapButton = new Button(menuComposite1, SWT.PUSH);
        MiniMapButton.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_MINI_MAP));
        MiniMapButton.setToolTipText(
                TurboGraphPPUIMessages.visualization_minimap_window_button_tool_tip);
        MiniMapButton.setData(ImageButton.MINI_MAP);
        MiniMapButton.addSelectionListener(imageButtonListener);
        graphButtonList.add(MiniMapButton);

        menuComposite1.pack();

        size = menuComposite1.getSize();
        buttonItem1.setControl(menuComposite1);
        buttonItem1.setSize(buttonItem1.computeSize(size.x, size.y));

        Composite menuComposite2 = new Composite(coolBar, SWT.NONE);
        menuComposite2.setLayout(new GridLayout(LayoutStyle.values().length, true));

        Button button1;

        for (LayoutStyle style : LayoutStyle.values()) {
            button1 = new Button(menuComposite2, SWT.PUSH);
            button1.setImage(style.getImage());
            button1.setToolTipText(style.getText());
            button1.setData(style);
            button1.addSelectionListener(layoutChangeListener);
            button1.pack();
            graphButtonList.add(button1);
        }

        menuComposite2.pack();

        size = menuComposite2.getSize();
        buttonItem2.setControl(menuComposite2);
        buttonItem2.setSize(buttonItem1.computeSize(size.x, size.y));

        Composite menuComposite3 = new Composite(coolBar, SWT.NONE);
        menuComposite3.setLayout(new GridLayout(2, true));

        shortestButton = new Button(menuComposite3, SWT.PUSH);
        shortestButton.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_SHORTEST_PATH));
        shortestButton.setToolTipText(
                TurboGraphPPUIMessages.visualization_shortest_button_tool_tip);
        shortestButton.setData(ImageButton.SHORTEST);
        shortestButton.addSelectionListener(imageButtonListener);
        shortestButton.pack();
        graphButtonList.add(shortestButton);

        button1 = new Button(menuComposite3, SWT.PUSH);
        button1.setImage(DBeaverIcons.getImage(UIIcon.CHART_BAR));
        button1.setToolTipText(TurboGraphPPUIMessages.visualization_chart_button_tool_tip);
        button1.setData(ImageButton.CHART);
        button1.addSelectionListener(imageButtonListener);
        button1.pack();
        graphButtonList.add(button1);

        menuComposite3.pack();

        size = menuComposite3.getSize();
        buttonItem3.setControl(menuComposite3);
        buttonItem3.setSize(buttonItem3.computeSize(size.x, size.y));

        Composite menuComposite4 = new Composite(coolBar, SWT.NONE);
        menuComposite4.setLayout(new GridLayout(4, true));

        button1 = new Button(menuComposite4, SWT.PUSH);
        button1.setImage(DBeaverIcons.getImage(UIIcon.PROPERTIES));
        button1.setToolTipText(TurboGraphPPUIMessages.visualization_value_button_tool_tip);
        button1.setData(ImageButton.VALUE);
        button1.addSelectionListener(imageButtonListener);
        button1.pack();
        graphButtonList.add(button1);

        button1 = new Button(menuComposite4, SWT.PUSH);
        button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_DESIGN));
        button1.setToolTipText(TurboGraphPPUIMessages.visualization_open_design_edit_button_tool_tip);
        button1.setData(ImageButton.DESIGN);
        button1.addSelectionListener(imageButtonListener);
        button1.pack();
        graphButtonList.add(button1);

        button1 = new Button(menuComposite4, SWT.PUSH);
        button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_CAPTURE));
        button1.setToolTipText(TurboGraphPPUIMessages.visualization_capture_button_tool_tip);
        button1.setData(ImageButton.CAPTURE);
        button1.addSelectionListener(imageButtonListener);
        button1.pack();
        graphButtonList.add(button1);

        button1 = new Button(menuComposite4, SWT.PUSH);
        button1.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_CSV_FILE));
        button1.setToolTipText(TurboGraphPPUIMessages.visualization_to_csv_file_button_tool_tip);
        button1.setData(ImageButton.TO_CSV);
        button1.addSelectionListener(imageButtonListener);
        button1.pack();

        menuComposite4.pack();

        size = menuComposite4.getSize();
        buttonItem4.setControl(menuComposite4);
        buttonItem4.setSize(buttonItem3.computeSize(size.x, size.y));

        Composite menuComposite5 = new Composite(coolBar, SWT.NONE);
        menuComposite5.setLayout(new GridLayout(2, false));

        fetchNextButton = new Button(menuComposite5, SWT.PUSH);
        fetchNextButton.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_FETCH_NEXT));
        fetchNextButton.setToolTipText(TurboGraphPPUIMessages.visualization_next_data_button_tool_tip);
        fetchNextButton.setData(ImageButton.NEXT_DATA);
        fetchNextButton.addSelectionListener(imageButtonListener);
        fetchNextButton.setEnabled(false);
        fetchNextButton.pack();

        fetchEndButton = new Button(menuComposite5, SWT.PUSH);
        fetchEndButton.setImage(DBeaverIcons.getImage(UIIcon.BUTTON_FETCH_ALL));
        fetchEndButton.setToolTipText(TurboGraphPPUIMessages.visualization_all_data_button_tool_tip);
        fetchEndButton.setData(ImageButton.ALL_DATA);
        fetchEndButton.addSelectionListener(imageButtonListener);
        fetchEndButton.setEnabled(false);
        fetchEndButton.pack();

        menuComposite5.pack();

        size = menuComposite5.getSize();
        buttonItem5.setControl(menuComposite5);
        buttonItem5.setSize(buttonItem4.computeSize(size.x, size.y));

        Composite menuComposite6 = new Composite(coolBar, SWT.NONE);
        menuComposite6.setLayout(new GridLayout(4, false));
        menuComposite6.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        resultLabel = new Label(menuComposite6, SWT.READ_ONLY | SWT.CENTER);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 4;
        resultLabel.setLayoutData(gd);
        resultLabel.setText("Edge : " + "00000" + " Node : " + "00000");

        menuComposite6.pack();

        size = menuComposite6.getSize();
        buttonItem6.setControl(menuComposite6);
        buttonItem6.setSize(buttonItem6.computeSize(size.x, size.y));
    }

    private void graphMenuEnable(boolean enable) {
        Iterator<Button> iter = graphButtonList.iterator();

        while (iter.hasNext()) {
            Button data = iter.next();
            data.setEnabled(enable);
        }
    }

    private static Label createHorizontalLine(Composite parent, int hSpan, int vIndent) {
        Label horizontalLine = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
        gd.horizontalSpan = hSpan;
        gd.verticalIndent = vIndent;
        horizontalLine.setLayoutData(gd);
        return horizontalLine;
    }

    private void ShowVisualizaion(boolean refreshMetadata, boolean append) {
        dataSet(refreshMetadata, append);
        if (graphTapIndex == FXGraph.GRAPH_TAP) {
            drawGraph(refreshMetadata, append);
            isAddMoreData = false;
        } else {
            if (!refreshMetadata && append) {
                isAddMoreData = true;
            } else {
                isAddMoreData = refreshMetadata? true : false; 
            }
        }
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
            attrList.putAll((LinkedHashMap) cellValue);
        }

        String regex = "[\\[\\]]";
        id = String.valueOf(attrList.get(ID_KEY)).replaceAll(regex, "");
        if (attrList.get(LABEL_KEY) instanceof String) {
            List<String> tempLabels = new ArrayList<>();
            tempLabels.add(String.valueOf(attrList.get(LABEL_KEY)));
            labels = tempLabels;
        } else {
            labels = (List<String>) attrList.get(LABEL_KEY);
        }

        attrList.remove(ID_KEY);
        attrList.remove(LABEL_KEY);

        DBDAttributeNodeList.put(id, attr);
        resultSetRowNodeList.put(id, row);
        displayStringNodeList.put(id, cellString);
        return visualGraph.addNode(id, labels, attrList) == null ? false : true;
    }

    private boolean addNeo4jEdge(
            ResultSetModel model, DBDAttributeBinding attr, ResultSetRow row, String cellString) {
        final String ID_KEY = NODE_EDGE_ID;
        final String LABEL_KEY = EDGE_TYPE;
        final String SID_KEY = NEO4J_EDGE_START_ID;
        final String TID_KEY = NEO4J_EDGE_END_ID;

        LinkedHashMap<String, Object> attrList = new LinkedHashMap<>();
        Object cellValue = model.getCellValue(attr, row);

        String id = "", sId = "", tId = "";
        List<String> types;

        if (cellValue instanceof LinkedHashMap) {
            attrList.putAll((LinkedHashMap) cellValue);
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
            types = (List<String>) attrList.get(LABEL_KEY);
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

        return visualGraph.addEdge(id, types, sId, tId, attrList) == null ? false : true;
    }

    StringBuilder fixBuffer = new StringBuilder();

    private String getCellString(
            ResultSetModel model,
            DBDAttributeBinding attr,
            ResultSetRow row,
            DBDDisplayFormat displayFormat) {
        Object cellValue = model.getCellValue(attr, row);
        if (cellValue instanceof DBDValueError) {
            return ((DBDValueError) cellValue).getErrorTitle();
        }
        if (cellValue instanceof Number
                && controller
                        .getPreferenceStore()
                        .getBoolean(ModelPreferences.RESULT_NATIVE_NUMERIC_FORMAT)) {
            displayFormat = DBDDisplayFormat.NATIVE;
        }

        String displayString =
                attr.getValueHandler().getValueDisplayString(attr, cellValue, displayFormat);

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
            if (c < ' ' /* || (c > 127 && c < 255) */) {
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
    public void clearMetaData() {}

    @Override
    public void updateValueView() {}

    @Override
    public void fillMenu(@NotNull IMenuManager menu) {}

    @Override
    public void changeMode(boolean recordMode) {}

    @Override
    public void scrollToRow(@NotNull RowPosition position) {}

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
    public void printResultSet() {}

    @Override
    protected void performHorizontalScroll(int scrollCount) {}

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
            return curSelection == null ? new Object[0] : new Object[] {curSelection};
        }

        @Override
        public List<String> toList() {
            return curSelection == null
                    ? Collections.emptyList()
                    : Collections.singletonList(curSelection);
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
        visualGraph.setTabSelectAction(
                (Integer id) -> {
                    graphTapIndex = id;
                    if (id == FXGraph.BROWSER_TAP) {
                        graphMenuEnable(false);
                    } else {
                        if (isAddMoreData) {
                            drawGraph(false, true);
                        }
                        graphMenuEnable(true);
                    }
                });
    }

    private void saveImage() {

        if (visualGraph != null) {
            FileDialog fileDialog =
                    new FileDialog(visualGraph.getGraphModel().getShell(), SWT.SAVE);
            fileDialog.setFilterExtensions(new String[] {"*.jpg"});
            fileDialog.setFilterNames(new String[] {"jpg Image File"});
            String filename = fileDialog.open();
            if (filename == null) {
                return;
            } else if (!filename.toLowerCase().contains(".jpg")) {
                filename = filename + ".jpg";
            }

            ImageData imageData = visualGraph.getCaptureImage();

            if (imageData == null) {
                return;
            }

            ImageLoader imageLoader = new ImageLoader();
            imageLoader.data = new ImageData[] {imageData};
            imageLoader.save(filename, SWT.IMAGE_JPEG);
        }
    }

    private void saveCSV() {
        if (visualGraph != null) {
            visualGraph.exportCSV();
        }
    }

    public void miniMapToggle() {
        if (visualGraph != null) {
            visualGraph.miniMapToggle();
        }
    }

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

    private void dataSet(boolean refreshMetadata, boolean append) {

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
            } else { // TurboGraph++
                String label = attrs.get(i).getMetaAttribute().getEntityName();
                if (!label.isEmpty()) {
                    if (attrs.get(i).getName().equals(NODE_EDGE_ID)
                            && attrs.get(i + 1).getName().equals(TURBOGRAPH_EDGE_START_ID)) {
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

        for (int i = lastReadRowCount; i < allRows.size(); i++) { // Add Node
            ResultSetRow row = allRows.get(i);
            for (Object obj : nodeRowData) {
                if (obj instanceof NEO4JRowData) {
                    NEO4JRowData data = (NEO4JRowData) obj;
                    String displayString = getCellString(model, data.attr, row, displayFormat);
                    addNeo4jNode(model, data.attr, row, displayString);
                } else if (obj instanceof TurboRowData) {
                    TurboRowData data = (TurboRowData) obj;
                    List<String> multiLabel = new ArrayList<>(); // temp
                    multiLabel.add(data.label);
                    LinkedHashMap<String, Object> attrMap = new LinkedHashMap<>();
                    String id = "";
                    for (int j = data.startIdx; j <= data.endIdx; j++) {
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

        for (int i = lastReadRowCount; i < allRows.size(); i++) { // Add Edge
            ResultSetRow row = allRows.get(i);
            for (Object obj : edgeRowData) {
                if (obj instanceof NEO4JRowData) {
                    NEO4JRowData data = (NEO4JRowData) obj;
                    String displayString = getCellString(model, data.attr, row, displayFormat);
                    addNeo4jEdge(model, data.attr, row, displayString);
                } else if (obj instanceof TurboRowData) {
                    TurboRowData data = (TurboRowData) obj;
                    List<String> multiLabel = new ArrayList<>(); // temp
                    multiLabel.add(data.label);
                    LinkedHashMap<String, Object> attrMap = new LinkedHashMap<>();
                    String id = "", sid = "", tid = "";
                    for (int j = data.startIdx; j <= data.endIdx; j++) {
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

        lastReadRowCount = allRows.size();
        
        resultLabel.setText(
                "Node : " + visualGraph.getNumNodes() + " Edge : " + visualGraph.getNumEdges());
    }

    private void drawGraph(boolean refreshMetadata, boolean append) {
        int nodesNum = visualGraph.getNumNodes();
        int sqrt = (int) Math.sqrt(nodesNum);
        int compositeSizeX = graphTopComposite.getSize().x - 100;
        int compositeSizeY = graphTopComposite.getSize().y - 100;
        double drawSizeX = sqrt * 162;
        double drawSizeY = sqrt * 129;

        if (visualGraph != null) {
            if (compositeSizeX > drawSizeX) {
                drawSizeX = compositeSizeX;
            }

            if (compositeSizeY > drawSizeY) {
                drawSizeY = compositeSizeY;
            }

            visualGraph.drawGraph(refreshMetadata, drawSizeX, drawSizeY);
        }
    }

    private class DetachDialog extends BaseDialog {
        private Composite parentComposite;
        private Composite composite;

        public DetachDialog(Composite parent) {
            this(parent, SWT.DIALOG_TRIM | SWT.MODELESS | SWT.RESIZE | SWT.MAX | SWT.MIN);
            parentComposite = parent;
        }

        public DetachDialog(Composite parent, int style) {
            super(parent.getShell(), "Visual view", null);
            setShellStyle(style);
        }

        public Composite getmainComposite() {
            return composite;
        }

        public Shell getParentShell() {
            return this.getShell();
        }

        @Override
        protected Composite createDialogArea(Composite parent) {
            Composite area = (Composite) super.createDialogArea(parent);

            composite = new Composite(area, SWT.NONE);
            GridLayout layout = new GridLayout(1, false);
            layout.marginHeight = 5;
            layout.marginWidth = 5;
            composite.setLayout(layout);

            GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
            gridData.widthHint = mainComposite.getBounds().width;
            gridData.heightHint = mainComposite.getBounds().height;
            composite.setLayoutData(gridData);

            return parent;
        }

        @Override
        protected void createButtonsForButtonBar(Composite parent) {}

        @Override
        protected boolean isResizable() {
            return true;
        }

        @Override
        public int open() {
            composite.layout(true, true);
            return super.open();
        }

        @Override
        public boolean close() {
            detach = false;
            mainComposite.setParent(parentComposite);
            parentComposite.layout(true, true);
            return super.close();
        }
    }
}
