package org.jkiss.dbeaver.ext.turbographpp.graph.chart;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swt.FXCanvas;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.jkiss.dbeaver.ext.turbographpp.graph.FXGraph;
import org.jkiss.dbeaver.ext.turbographpp.graph.MoveBox;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherNode;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.GraphDataModel;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.internal.GraphMessages;
import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPDataSource;
import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPTableBase;
import org.jkiss.dbeaver.ext.turbographpp.model.TurboGraphPPTableColumn;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCPreparedStatement;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCResultSet;
import org.jkiss.dbeaver.model.exec.jdbc.JDBCSession;
import org.jkiss.dbeaver.model.runtime.AbstractJob;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.runtime.DBWorkbench;

public class GraphChart extends MoveBox {

    private final FXGraph graph;

    private final TabFolder tabFolder;
    private final TabItem tab1;

    private Combo nodeLableList;
    private Combo propertyList;

    private FXCanvas ChartCanvas2d;

    private Object nodeSelectItem = null;
    private Object edgeSelectItem = null;

    private static final int OVERLAY_WIDTH = 200;
    private static final int OVERLAY_HEIGHT = 200;

    private String currentQuery = "";
    private int rowCount = 0;

    private StackPane chartPane = null;

    private static StackedBarChart<Number, String> barChart;

    private TurboGraphPPDataSource dataSource;

    private Button buttonGraph;
    private Button buttonAll;

    private static final Integer[] SUPPORT_MIN_MAX_TYPE_ID =
            new Integer[] {2, 4}; // NUMERIC(DECIMAL)(2), INTEGER(4), DATE(91)

    public GraphChart(Control control, FXGraph graph, DBPDataSource dataSource) {
        super(control, GraphMessages.graphbox_title, OVERLAY_WIDTH, OVERLAY_HEIGHT);
        this.graph = graph;
        this.dataSource = (TurboGraphPPDataSource) dataSource;
        Composite itemComposite = new Composite(this.getShell(), SWT.NONE);
        GridData gd = new GridData();
        gd.horizontalAlignment = GridData.CENTER;
        GridLayout layout = new GridLayout(6, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.horizontalSpacing = 2;
        layout.verticalSpacing = 2;
        itemComposite.setLayout(layout);
        itemComposite.setLayoutData(gd);

        Label nodeLabel = new Label(itemComposite, SWT.NONE);
        nodeLabel.setText(GraphMessages.fxgraph_all_label + " : ");

        nodeLableList = new Combo(itemComposite, SWT.READ_ONLY);
        nodeLableList.setEnabled(true);

        Label propertyLabel = new Label(itemComposite, SWT.NONE);
        propertyLabel.setText(GraphMessages.fxgraph_all_property + " : ");

        propertyList = new Combo(itemComposite, SWT.READ_ONLY);
        propertyList.setEnabled(false);

        buttonGraph = new Button(itemComposite, SWT.RADIO | SWT.CENTER);
        buttonGraph.setText(GraphMessages.graphbox_radio_by_visualGraph);
        buttonGraph.setSelection(true);

        buttonAll = new Button(itemComposite, SWT.RADIO | SWT.CENTER);
        buttonAll.setText(GraphMessages.graphbox_radio_by_all);
        buttonAll.setSelection(false);

        tabFolder = new TabFolder(this.getShell(), SWT.BORDER);
        tabFolder.setEnabled(true);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 3;
        tabFolder.setLayoutData(gd);

        tab1 = new TabItem(tabFolder, SWT.NULL);
        tab1.setText("Chart");

        Composite tab1Composite = new Composite(tabFolder, SWT.NONE);
        gd = new GridData();
        GridLayout layout1 = new GridLayout(2, false);
        layout1.marginHeight = 0;
        layout1.marginWidth = 0;
        layout1.horizontalSpacing = 5;
        layout1.verticalSpacing = 2;
        tab1Composite.setLayout(layout1);
        tab1Composite.setLayoutData(gd);
        tab1.setControl(tab1Composite);
        create2dChartCanva(tab1Composite);

        addListener();
    }

    private void addListener() {
        nodeLableList.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        String label = nodeLableList.getText();
                        if (buttonGraph.getSelection()) {
                            updateProperyList(label);
                        } else {
                            if (dataSource.getInfo().getDriverName().contains("Neo4j")) {
                                updatePropertiesInAllForNeo4j(label);
                            } else {
                                updatePropertiesInAll(label);
                            }
                        }
                    }
                });

        propertyList.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        String nodeLabel = nodeLableList.getText();
                        String propretyName = propertyList.getText();
                        dataAnalyze(nodeLabel, propretyName);
                        edgeSelectItem = propertyList.getSelection();
                    }
                });

        tabFolder.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        switchWidget(tabFolder.getSelectionIndex());
                    }
                });

        final SelectionListener radioSelectAdapter =
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        propertyList.setEnabled(false);
                        propertyList.clearSelection();
                        if (buttonGraph.getSelection()) {
                            nodeLableList.setItems(graph.getDataModel().getNodeLabelList());
                        } else {
                            updateLabelInSource();
                        }
                    }
                };

        buttonGraph.addSelectionListener(radioSelectAdapter);
        buttonAll.addSelectionListener(radioSelectAdapter);
    }

    private void create2dChartCanva(Composite composite) {
        ChartCanvas2d = new FXCanvas(composite, SWT.NONE);

        chartPane = new StackPane();
        chartPane.getChildren().add(create2dChartNode());
        Scene scene = new Scene(chartPane, 800, 512);

        ChartCanvas2d.setScene(scene);
    }

    public static Node create2dChartNode() {
        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();
        barChart = new StackedBarChart<Number, String>(xAxis, yAxis);
        barChart.setTitle("Bar Chart");

        return barChart;
    }

    public void setSelectNode(Object item) {
        int idx = -1;

        nodeSelectItem = item;
        CypherNode node = (CypherNode) item;

        for (String label : node.getLabels()) {
            int labelIndex = nodeLableList.indexOf(label);
            if (labelIndex != -1) {
                nodeLableList.select(labelIndex);
                break;
            }
        }

        List<String> intProperyList = new ArrayList<>();
        for (String protype : node.getProperties().keySet()) {
            if (node.getProperty(protype) instanceof Integer
                    || node.getProperty(protype) instanceof Long) {
                intProperyList.add(protype);
            }
        }
        propertyList.setItems(intProperyList.toArray(new String[intProperyList.size()]));
        if (node.getDisplayProperty() != null) {
            idx = propertyList.indexOf(node.getDisplayProperty());
        }

        if (idx != -1) {
            propertyList.select(idx);
        }
    }

    private void switchWidget(int selected) {
        if (selected == 0) {

        } else {

        }
    }

    public void open(int positionX, int positionY) {
        nodeLableList.setItems(graph.getDataModel().getNodeLabelList());
        show();
        setOverlaySize(positionX, positionY, tabFolder.getSize().x, tabFolder.getSize().y);
    }

    @Override
    public void remove() {
        super.remove();
        runUpdateChart(new HashMap<>());
    }

    private void updateProperyList(String label) {
        GraphDataModel g = graph.getDataModel();
        if (nodeLableList.getItemCount() > 0) {
            if (label != null) {
                CypherNode node = g.getNode(g.getNodeLabelList(label).get(0)).element();
                if (node != null) {
                    setSelectNode(node);
                    propertyList.setEnabled(true);
                }
            }
        }
    }

    private String getProperyType(String properyName) {
        GraphDataModel g = graph.getDataModel();
        if (nodeLableList.getItemCount() > 0) {
            String label = nodeLableList.getText();
            if (label != null) {
                CypherNode node = g.getNode(g.getNodeLabelList(label).get(0)).element();
                if (node != null) {
                    return node.getPropertyType(properyName);
                }
            }
        }

        return null;
    }

    @Override
    public void show() {
        super.show();
        buttonGraph.setSelection(true);
        buttonAll.setSelection(false);
    }

    @Override
    public void show(int x, int y) {
        show();
    }

    protected <V> Collection<Vertex<CypherNode>> sort(
            Collection<? extends Vertex<CypherNode>> nodes, String propertyName) {

        List<Vertex<CypherNode>> list = new ArrayList<>();
        list.addAll(nodes);

        Collections.sort(
                list,
                new Comparator<Vertex<CypherNode>>() {
                    @Override
                    public int compare(Vertex<CypherNode> t, Vertex<CypherNode> t1) {
                        return t.element().toString().compareToIgnoreCase(t1.element().toString());
                    }
                });

        return list;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void setCurrentQuery(String query, int count) {
        currentQuery = query;
        rowCount = count;
    }

    public void runUpdateChart(HashMap<String, Object> data) {
        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {
                        updateChart(data);
                    }
                });
    }

    public void updateChart(HashMap<String, Object> data) {
        barChart = null;

        Series series = new Series();
        int val = 0;
        for (String key : data.keySet()) {
            val = Integer.valueOf(String.valueOf(data.get(key)));
            Data<Number, String> nodeData = new Data<Number, String>(val, key);
            series.getData().add(nodeData);
            nodeData.nodeProperty()
                    .addListener(
                            new ChangeListener<Node>() {
                                @Override
                                public void changed(
                                        ObservableValue<? extends Node> ov,
                                        Node oldNode,
                                        final Node node) {
                                    if (node != null) {
                                        displayLabelForData(nodeData);
                                    }
                                }
                            });
        }
        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();
        yAxis.setCategories(FXCollections.<String>observableArrayList(data.keySet()));
        barChart = new StackedBarChart<Number, String>(xAxis, yAxis);
        barChart.getData().add(series);
        chartPane.getChildren().remove(0);
        chartPane.getChildren().add(barChart);

        setLabelProperty(xAxis, yAxis);
    }

    public void dataAnalyze(String label, String property) {
        dataAnalyze(label, property, "");
    }

    public void dataAnalyze(String label, String property, String propertyType) {

        if (property == null && label == null) {
            return;
        }

        if (buttonGraph.getSelection()) {
            GetChartInfoInGraphJob startUpdateJob =
                    new GetChartInfoInGraphJob("Get Graph ChartInfo", graph, this, label, property);

            if (!startUpdateJob.isFinished()) {
                startUpdateJob.schedule();
            }
        } else {
            if (dataSource == null) {
                return;
            }

            GetChartInfoAllJob startUpdateJob =
                    new GetChartInfoAllJob(
                            "Get Graph ChartInfo", dataSource, this, label, property);

            if (!startUpdateJob.isFinished()) {
                startUpdateJob.schedule();
            }
        }
    }

    public void UILock() {
        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {
                        propertyList.setEnabled(false);
                    }
                });
    }

    public void UIUnLock() {
        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {
                        propertyList.setEnabled(true);
                    }
                });
    }

    private void updateLabelInSource() {
        new AbstractJob("Get TurboGraph Info") {
            {
                setUser(true);
            }

            @Override
            protected IStatus run(DBRProgressMonitor monitor) {
                try {
                    List<? extends TurboGraphPPTableBase> tableList = dataSource.getTables(monitor);
                    List<String> labelList = new ArrayList<>();

                    for (TurboGraphPPTableBase table : tableList) {
                        if (!table.isEdge()) {
                            labelList.add(table.getName());
                        }
                    }

                    Display.getDefault()
                            .asyncExec(
                                    new Runnable() {
                                        public void run() {
                                            String[] result =
                                                    labelList.toArray(new String[labelList.size()]);
                                            nodeLableList.setItems(result);
                                        };
                                    });
                } catch (Exception e) {
                    DBWorkbench.getPlatformUI().showError("Table list", "Can't read table list", e);
                }
                return Status.OK_STATUS;
            }
        }.schedule();
    }

    private void updatePropertiesInAllForNeo4j(String label) {
        new AbstractJob("Get Neo4j Info") {
            {
                setUser(true);
            }

            @Override
            protected IStatus run(DBRProgressMonitor monitor) {
                try {
                    List<String> list = new ArrayList<>();
                    String vertexLabel;
                    String vertexProperty;

                    try (JDBCSession session =
                            DBUtils.openMetaSession(
                                    monitor,
                                    dataSource.getParentObject(),
                                    "Load Neo4j Properties")) {
                        StringBuilder queryBuilder = new StringBuilder();
                        queryBuilder.append("MATCH (n:" + label + ")");
                        queryBuilder.append(" UNWIND keys(n) as Key");
                        queryBuilder.append(" RETURN DISTINCT Key");
                        String query = queryBuilder.toString();

                        JDBCPreparedStatement dbStat = null;
                        dbStat = session.prepareStatement(query);

                        JDBCResultSet dbResult = dbStat.executeQuery();

                        while (dbResult.next()) {
                            String key = dbResult.getString(1);
                            list.add(key);
                        }

                        if (dbResult != null) dbResult.close();
                        if (dbStat != null) dbStat.close();

                    } catch (DBCException | SQLException e) {
                        DBWorkbench.getPlatformUI()
                                .showError("Neo4j Error", "Properties Load error", e);
                    }

                    Display.getDefault()
                            .asyncExec(
                                    new Runnable() {
                                        public void run() {
                                            String[] result = list.toArray(new String[list.size()]);
                                            propertyList.setItems(result);
                                            if (list.size() > 0) {
                                                propertyList.select(0);
                                                propertyList.setEnabled(true);
                                            }
                                        };
                                    });

                } catch (Exception e) {
                    DBWorkbench.getPlatformUI()
                            .showError("Neo4j Error", "Properties Load error", e);
                }
                return Status.OK_STATUS;
            }
        }.schedule();
    }

    private void updatePropertiesInAll(String label) {
        new AbstractJob("Get TurboGraph Info") {
            {
                setUser(true);
            }

            @Override
            protected IStatus run(DBRProgressMonitor monitor) {
                try {
                    TurboGraphPPTableBase table = dataSource.getTable(monitor, label);
                    List<? extends TurboGraphPPTableColumn> listProperties =
                            table.getAttributes(monitor);

                    List<String> list = new ArrayList<>();

                    for (TurboGraphPPTableColumn column : listProperties) {
                        for (int i = 0; i < SUPPORT_MIN_MAX_TYPE_ID.length; i++) {
                            if (SUPPORT_MIN_MAX_TYPE_ID[i] == column.getTypeID()) {
                                list.add(column.getName());
                            }
                        }
                    }

                    Display.getDefault()
                            .asyncExec(
                                    new Runnable() {
                                        public void run() {
                                            String[] result = list.toArray(new String[list.size()]);
                                            propertyList.setItems(result);
                                            if (list.size() > 0) {
                                                propertyList.select(0);
                                                propertyList.setEnabled(true);
                                            }
                                        };
                                    });
                } catch (Exception e) {
                    DBWorkbench.getPlatformUI().showError("Table list", "Can't read table list", e);
                }
                return Status.OK_STATUS;
            }
        }.schedule();
    }

    private static void setLabelProperty(NumberAxis xAxis, CategoryAxis yAxis) {
        xAxis.setLabel("Count");
        xAxis.setTickLabelRotation(0);
        xAxis.setTickLabelFont(new Font("Arial", 12));
        xAxis.setTickMarkVisible(true);
        yAxis.setLabel("Range");
        yAxis.setTickLabelRotation(0);
        yAxis.setTickLabelFont(new Font("Arial", 12));
        yAxis.setTickMarkVisible(true);
    }

    private void displayLabelForData(Data<Number, String> data) {
        final Node node = data.getNode();
        final Text dataText = new Text(data.getXValue() + "");
        node.setStyle("-fx-background-color: #99ccff");
        node.parentProperty()
                .addListener(
                        new ChangeListener<Parent>() {
                            @Override
                            public void changed(
                                    ObservableValue<? extends Parent> ov,
                                    Parent oldParent,
                                    Parent parent) {
                                Group parentGroup = (Group) parent;
                                parentGroup.getChildren().add(dataText);
                            }
                        });

        node.boundsInParentProperty()
                .addListener(
                        new ChangeListener<Bounds>() {
                            @Override
                            public void changed(
                                    ObservableValue<? extends Bounds> ov,
                                    Bounds oldBounds,
                                    Bounds bounds) {
                                dataText.setLayoutX(
                                        Math.round(
                                                bounds.getMinX()
                                                        + bounds.getWidth() / 2
                                                        - dataText.prefWidth(-1) / 2));
                                dataText.setLayoutY(
                                        Math.round(bounds.getMinY() - dataText.prefHeight(-1) * 0.5)
                                                + 25);
                            }
                        });
    }
}
