package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.jkiss.dbeaver.ext.turbographpp.graph.chart.jobs.GetChartInfoInGraphJob;
import org.jkiss.dbeaver.ext.turbographpp.graph.chart.jobs.GetChartInfoQueryJob;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherNode;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.GraphDataModel;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.internal.GraphMessages;
import org.jkiss.dbeaver.model.DBPDataSource;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;

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
    
    private DBPDataSource dataSource;
    
    private Button buttonGraph;
    private Button buttonQuery;
    private Button buttonAll;
    
    public GraphChart(Control control, FXGraph graph, DBPDataSource dataSource) {
        super(control, GraphMessages.graphbox_title, OVERLAY_WIDTH, OVERLAY_HEIGHT);
        this.graph = graph;
        this.dataSource = dataSource;
        Composite itemComposite = new Composite(this.getShell(), SWT.NONE);
        GridData gd = new GridData();
        gd.horizontalAlignment = GridData.CENTER; 
        GridLayout layout = new GridLayout(7, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.horizontalSpacing = 2;
        layout.verticalSpacing = 2;
        itemComposite.setLayout(layout);
        itemComposite.setLayoutData(gd);

        Label nodeLabel = new Label(itemComposite, SWT.NONE);
        nodeLabel.setText(" Label : ");

        nodeLableList = new Combo(itemComposite, SWT.READ_ONLY);
        nodeLableList.setEnabled(true);

        nodeLableList.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        updateProperyList();
                    }
                });

        Label propertyLabel = new Label(itemComposite, SWT.NONE);
        propertyLabel.setText(" Property : ");

        propertyList = new Combo(itemComposite, SWT.READ_ONLY);
        propertyList.setEnabled(false);
        propertyList.addSelectionListener(
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        String nodeLabel = nodeLableList.getText();
                        String propretyName = propertyList.getText();
                        String propertyType = getProperyType(propretyName);
                        dataAnalyze(nodeLabel, propretyName, propertyType);
                        edgeSelectItem = propertyList.getSelection();
                    }
                });

        
        buttonGraph = new Button(itemComposite, SWT.RADIO | SWT.CENTER);
        buttonGraph.setText(GraphMessages.graphbox_radio_by_visualGraph);
        buttonGraph.setSelection(true);
        
        buttonQuery = new Button(itemComposite, SWT.RADIO | SWT.CENTER);
        buttonQuery.setText(GraphMessages.graphbox_radio_by_query);
        buttonQuery.setSelection(false);
        buttonQuery.setVisible(false);
        
        buttonAll = new Button(itemComposite, SWT.RADIO | SWT.CENTER);
        buttonAll.setText(GraphMessages.graphbox_radio_by_all);
        buttonAll.setSelection(false);
        buttonAll.setVisible(false);
        
        tabFolder = new TabFolder(this.getShell(), SWT.BORDER);
        tabFolder.setEnabled(true);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 3;
        tabFolder.setLayoutData(gd);

        tab1 = new TabItem(tabFolder, SWT.NULL);
        tab1.setText("Chart");

        tabFolder.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        switchWidget(tabFolder.getSelectionIndex());
                    }
                });

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
        yAxis.setCategories(FXCollections.<String>observableArrayList(
                Arrays.asList("a", "b", "c", "d", "e")));
        barChart = new StackedBarChart<Number, String>(xAxis, yAxis);
        barChart.setTitle("Bar Chart");
        xAxis.setLabel("Count");
        xAxis.setTickLabelRotation(0);
        yAxis.setLabel("Summary");
        yAxis.setTickLabelRotation(90);
        yAxis.setTickLabelFont(new Font("System Regular", 10));

        return barChart;
    }

    public void setSelectNode(Object item) {
        nodeSelectItem = item;
        CypherNode node = (CypherNode) item;

        int labelIndex = nodeLableList.indexOf(node.getLabel());
        if (labelIndex != -1) {
            nodeLableList.select(labelIndex);
        }

        ArrayList<String> intProperyList = new ArrayList<>();
        for (String protype : node.getProperties().keySet()) {
        	if (node.getProperty(protype) instanceof Integer || node.getProperty(protype) instanceof Long) {
        		intProperyList.add(protype);
        	}
        }
        propertyList.setItems(intProperyList.toArray(new String[intProperyList.size()]));
        int idx = propertyList.indexOf(node.getDisplayProperty());
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
        nodeLableList.setItems(graph.getDataModel().getNodeLableList());
        show();
        setOverlaySize(positionX, positionY, tabFolder.getSize().x, tabFolder.getSize().y);
    }

    @Override
    public void remove() {
        super.remove();
    }

    private void updateProperyList() {
        GraphDataModel g = graph.getDataModel();
        if (nodeLableList.getItemCount() > 0) {
            String label = nodeLableList.getText();
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
        if (nodeSelectItem != null) {
        	setSelectNode(nodeSelectItem);
        }
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
        
        XYChart.Series series = new XYChart.Series();
        int val = 0;
        for (String key : data.keySet()) {
            val = Integer.valueOf(String.valueOf(data.get(key)));
            series.getData().add(new XYChart.Data<Number, String>(val, key));
        }
        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();
        yAxis.setCategories(FXCollections.<String>observableArrayList(
                data.keySet()));
        barChart = new StackedBarChart<Number, String>(xAxis, yAxis);
        barChart.getData().add(series);
        chartPane.getChildren().remove(0);
        chartPane.getChildren().add(barChart);
        
        for (final Series<Number, String> tempseries : barChart.getData()) {
            for (final XYChart.Data<Number, String> tempdata : tempseries.getData()) {
                Tooltip tooltip = new Tooltip();
                tooltip.setText(tempdata.getXValue().toString());
                Tooltip.install(tempdata.getNode(), tooltip);
            }
        }
    }
    
    public void dataAnalyze(String label, String property, String propertyType) {
    	
	    if (property == null && label == null) {
	    	return;
	    }
	    
	    if (buttonGraph.getSelection()) {
	    	GetChartInfoInGraphJob startUpdateJob =
	                new GetChartInfoInGraphJob("Get Graph ChartInfo",
	                		graph,
	                		this, label, property);
	
	        if (!startUpdateJob.isFinished()) {
	            startUpdateJob.schedule();
	        }
	    } else {
	        if (dataSource == null) {
	        	return;
	        }
	
	        String query = currentQuery;
	        if (buttonAll.getSelection()) {
	        	query = "MATCH (n) return n";
	        }
	        
	        GetChartInfoQueryJob startUpdateJob =
	                new GetChartInfoQueryJob("Get Graph ChartInfo",
	                		dataSource,
	                		this, query, label, property);
	
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
    
}
