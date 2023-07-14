package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CyperEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CyperNode;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.DisplayType;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.GraphDataModel;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartStyleProxy;
import org.jkiss.dbeaver.ext.turbographpp.graph.internal.GraphMessages;

public class DesignBox extends MoveBox {

	private final FXGraph graph;
	
	private final TabFolder tabFolder;
	private final TabItem nodeTab;
	private final TabItem edgeTab;
	
	//Node widget
	private Combo nodeLableList;
    private Spinner radius;
    private Button colorButton;
    private Spinner textSize;
    private Combo displayType;
    private Combo displayProperty;
    
    //Edge widget
    private Combo edgeTypeList;
    private Spinner lineStrength;
    private Button lineColor;
    private Combo lineStyle;
    private Spinner edgeTextSize;

    private String[] typeList = {
        DisplayType.ID.name(), DisplayType.TYPE.name(), DisplayType.PROPERTY.name()
    };

    private Object nodeSelectItem = null;
    private Object edgeSelectItem = null;
    
    private static final int OVERLAY_WIDTH = 200;
    private static final int OVERLAY_NODE_HEIGHT = 200;
    private static final int OVERLAY_EDGE_HEIGHT = 175;
    
    public DesignBox(Control control, FXGraph graph) {
        super(control, GraphMessages.designbox_title, OVERLAY_WIDTH, OVERLAY_NODE_HEIGHT);
        this.graph = graph;
        
        tabFolder = new TabFolder(this.getShell(), SWT.BORDER);
        tabFolder.setEnabled(true);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 3;
        tabFolder.setLayoutData(gd);

        nodeTab = new TabItem(tabFolder, SWT.NULL);
        nodeTab.setText("Node");
        
        edgeTab = new TabItem(tabFolder, SWT.NULL);
        edgeTab.setText("Edge");
        
        Composite nodeComposite = new Composite(tabFolder, SWT.NONE);
        gd = new GridData();
        GridLayout layout1 = new GridLayout(2, false);
        layout1.marginHeight = 0;
        layout1.marginWidth = 0;
        layout1.horizontalSpacing = 5;
        layout1.verticalSpacing = 2;
        nodeComposite.setLayout(layout1);
        nodeComposite.setLayoutData(gd);
        nodeTab.setControl(nodeComposite);
        
        createNodeWidget(nodeComposite);
        
        Composite edgeComposite = new Composite(tabFolder, SWT.NONE);
        GridLayout layout2 = new GridLayout(2, false);
        layout2.marginHeight = 0;
        layout2.marginWidth = 0;
        layout2.horizontalSpacing = 5;
        layout2.verticalSpacing = 5;
        edgeComposite.setLayout(layout2);
        edgeComposite.setLayoutData(gd);
        edgeTab.setControl(edgeComposite);
        
        createEdgeWidget(edgeComposite);
        
    }

    private void createEdgeWidget(Composite composite) {
    	
    	Label edgeType = new Label(composite, SWT.NONE);
    	edgeType.setText("Type");
    	
    	edgeTypeList = new Combo(composite, SWT.READ_ONLY);
    	edgeTypeList.setItems(graph.getDataModel().getEdgeTypeList());
    	
    	edgeTypeList.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e) {
            	updateItem();
            }
        });
    	
    	Label strengthLabel = new Label(composite, SWT.NONE);
    	strengthLabel.setText("Line Strength");

        lineStrength = new Spinner(composite, SWT.NONE);
        lineStrength.setDigits(1);
        lineStrength.setMinimum(2);
        lineStrength.setMaximum(10);
        lineStrength.setIncrement(1);

        Label colorLabel = new Label(composite, SWT.NONE);
        colorLabel.setText("Line Color");

        lineColor = new Button(composite, SWT.NONE);
        lineColor.setBackground(new Color(255, 0, 0));
        lineColor.addSelectionListener(
        		new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        ColorDialog cd = new ColorDialog(getShell());
                        cd.setRGB(lineColor.getBackground().getRGB());
                        RGB newbg = cd.open();
                        if (newbg != null) {
                            Color background = new Color(newbg);
                            lineColor.setBackground(background);
                            lineColor.redraw();
                        }
                    }
                });

        Label StyleLabel = new Label(composite, SWT.NONE);
        StyleLabel.setText("Line Style");

        lineStyle = new Combo(composite, SWT.READ_ONLY);
        String[] nameList = new String[LineStyle.values().length];
    	int i = 0;
    	for (LineStyle style : LineStyle.values()) {
			nameList[i] = style.name();
			i++;
		}
        lineStyle.setItems(nameList);

        lineStyle.addSelectionListener(new SelectionAdapter(){
        
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if (displayType.getSelectionIndex() == DisplayType.ID.ordinal()
            			|| displayType.getSelectionIndex() == DisplayType.TYPE.ordinal()) {
            		displayProperty.setEnabled(false);
            	}

            }
        });
        
        Label textSizeLable = new Label(composite, SWT.NONE);
        textSizeLable.setText("Edge Text Size");

        edgeTextSize = new Spinner(composite, SWT.NONE);
        edgeTextSize.setDigits(0);
        edgeTextSize.setMinimum(5);
        edgeTextSize.setMaximum(15);
        edgeTextSize.setIncrement(1);
        edgeTextSize.setSelection(5);

        Button applyButton = new Button(composite, SWT.NONE);
        applyButton.setText("Apply");
        applyButton.addSelectionListener(
        		new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        edgeApplyUpdate();
                    }
                });
        
        Label empty = new Label(composite, SWT.NONE);
    }
    
    private void createNodeWidget(Composite composite) {
    	
    	Label nodeLabel = new Label(composite, SWT.NONE);
    	nodeLabel.setText("Label");
    	
    	nodeLableList = new Combo(composite, SWT.READ_ONLY);

    	nodeLableList.addSelectionListener(new SelectionAdapter() {
        
            @Override
            public void widgetSelected(SelectionEvent e) {
            	updateItem();
            }
        });
    	
    	Label radiusLabel = new Label(composite, SWT.NONE);
        radiusLabel.setText("Radius");

        radius = new Spinner(composite, SWT.NONE);
        radius.setDigits(1);
        radius.setMinimum(150);
        radius.setMaximum(500);
        radius.setIncrement(5);
        radius.setSelection(105);

        Label colorLabel = new Label(composite, SWT.NONE);
        colorLabel.setText("Color");

        colorButton = new Button(composite, SWT.NONE);
        colorButton.setBackground(new Color(255, 0, 0));
        colorButton.addSelectionListener(
        		new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        ColorDialog cd = new ColorDialog(getShell());
                        cd.setRGB(colorButton.getBackground().getRGB());
                        RGB newbg = cd.open();
                        if (newbg != null) {
                            Color background = new Color(newbg);
                            colorButton.setBackground(background);
                            colorButton.redraw();
                        }
                    }
                });

        Label textSizeLable = new Label(composite, SWT.NONE);
        textSizeLable.setText("Text Size");

        textSize = new Spinner(composite, SWT.NONE);
        textSize.setDigits(0);
        textSize.setMinimum(5);
        textSize.setMaximum(15);
        textSize.setIncrement(1);
        textSize.setSelection(8);

        Label TypeLabel = new Label(composite, SWT.NONE);
        TypeLabel.setText("Display Type");

        displayType = new Combo(composite, SWT.READ_ONLY);
        displayType.setItems(typeList);

        displayType.addSelectionListener(new SelectionAdapter() {
        
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if (displayType.getSelectionIndex() == DisplayType.ID.ordinal()
            			|| displayType.getSelectionIndex() == DisplayType.TYPE.ordinal()) {
            		displayProperty.setEnabled(false);
            	} else {
            		displayProperty.setEnabled(true);
            	}

            }
        });
        
        Label PropertyLabel = new Label(composite, SWT.NONE);
        PropertyLabel.setText("Display Property");

        displayProperty = new Combo(composite, SWT.READ_ONLY);

        Button applyButton = new Button(composite, SWT.NONE);
        applyButton.setText("Apply");
        applyButton.addSelectionListener(
        		new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                    	nodeApplyUpdate();
                    }
                });
    }
    
    public void setSelectItem(Object item) {
        if (item instanceof CyperNode) {
        	nodeSelectItem = item;
            CyperNode node = (CyperNode) item;
            DisplayType type = node.getDisplayType();

            int labelIndex = nodeLableList.indexOf(node.getLabel());
            if (labelIndex != -1) {
            	nodeLableList.select(labelIndex);
            }
            
            radius.setSelection((int) (node.getRadius() * 10));
            colorButton.setBackground(stringToColor(node.getFillColorHexString()));
            textSize.setSelection(node.getTextSize());
            displayType.select(type.ordinal());
            if (type == DisplayType.PROPERTY) {
                displayProperty.setEnabled(true);
                displayProperty.setItems(
                        node.getProperties()
                                .keySet()
                                .toArray(new String[node.getProperties().size()]));
                int idx = displayProperty.indexOf(node.getDisplayProperty());
                if (idx != -1) {
                    displayProperty.select(idx);
                }
            } else {
                displayProperty.clearSelection();
                displayProperty.setEnabled(false);
            }
        } else {
        	edgeSelectItem = item;
        	CyperEdge edge = (CyperEdge) item;
        	
        	int index = edgeTypeList.indexOf(edge.getType());
            if (index != -1) {
            	edgeTypeList.select(index);
            }
            lineStrength.setSelection((int)(Double.valueOf(edge.getLineStrength()) * 10));
            lineColor.setBackground(stringToColor(edge.getLineColor()));
            for (LineStyle style : LineStyle.values()) {
            	if (style.getValue().equals(edge.getLineStyle())) {
            		lineStyle.select(style.ordinal());
            		break;
            	}
            }
            
            if (index != -1) {
            	lineStyle.select(index);
            }
            edgeTextSize.setSelection(edge.getTextSize());
        }
    }

    private void switchWidget(boolean isNode) {
    	if (isNode) {
    		tabFolder.setSelection(0);
    	} else {
    		tabFolder.setSelection(1);
    	}
    }
    
    private Color stringToColor(String colorString) {
        int r = Integer.parseInt(colorString.substring(0, 2), 16);
        int g = Integer.parseInt(colorString.substring(2, 4), 16);
        int b = Integer.parseInt(colorString.substring(4, 6), 16);
        Color color = new Color(r, g, b);
        return color;
    }

    private String ColorToString(Color color) {
        int intR, intG, intB;
        String r, g, b;
        intR = color.getRed();
        intG = color.getGreen();
        intB = color.getBlue();

        if (intR < 16) {
            r = "0" + Integer.toHexString(intR);
        } else {
            r = Integer.toHexString(intR);
        }

        if (intG < 16) {
            g = "0" + Integer.toHexString(intG);
        } else {
            g = Integer.toHexString(intG);
        }

        if (intB < 16) {
            b = "0" + Integer.toHexString(intB);
        } else {
            b = Integer.toHexString(intB);
        }

        return r + g + b;
    }

    private void nodeApplyUpdate() {
    	if (nodeSelectItem != null) {
            if (nodeSelectItem instanceof CyperNode) {
                CyperNode node = (CyperNode) nodeSelectItem;
                compareItem();
                if (ChangeItem.isChanged()) {
                    Iterator<String> iterator =
                            graph.getDataModel().getNodeLabelList(node.getLabel()).iterator();
                    final int selecetIndex = displayType.getSelectionIndex();
                    while (iterator.hasNext()) {
                        String id = iterator.next();
                        Vertex<CyperNode> vertex = graph.getDataModel().getNode(id);
                        CyperNode saveNode = vertex.element();
                        if (ChangeItem.changedRadius) {
                            saveNode.setRadius(radius.getSelection() / 10);
                            graph.getGraphView()
                                    .getGraphVertex(vertex)
                                    .setNodeRadius(saveNode.getRadius());
                            graph.getGraphView().updateEdgeArrowForRadius();
                        }
                        if (ChangeItem.changedColor) {
                            String color = ColorToString(colorButton.getBackground());
                            saveNode.setFillColor(color);
                            graph.getGraphView()
                                    .getStylableVertex(vertex)
                                    .setStyle(
                                            SmartStyleProxy.DEFAULT_VERTEX
                                                    + saveNode.getFillColor());
                        }

                        if (ChangeItem.changedTextSize) {
                            saveNode.setTextSize(textSize.getSelection());
                            graph.getGraphView().getGraphVertex(vertex).setTextSize(saveNode.getTextSize());
                        }

                        if (ChangeItem.changedType || ChangeItem.changedProperty) {
	                        if (selecetIndex == DisplayType.ID.ordinal()) {
	                        	saveNode.setDisplayType(DisplayType.ID);
	                        } else if (selecetIndex == DisplayType.TYPE.ordinal()) {
	                        	saveNode.setDisplayType(DisplayType.TYPE);
	                        } else if (selecetIndex == DisplayType.PROPERTY.ordinal()) {
	                        	saveNode.setDisplayType(DisplayType.PROPERTY);
	                            if (!displayProperty.getText().isEmpty()) {
	                            	saveNode.setDisplayProperty(displayProperty.getText());
	                            }
	                        } else {
	                        	saveNode.setDisplayType(DisplayType.PROPERTY);
	                        }
	                        graph.getGraphView().getGraphVertex(vertex).updateLabelText();
	                        
                        }
                        
                        if (ChangeItem.changedTextSize || ChangeItem.changedType || ChangeItem.changedProperty) {
                        	graph.getGraphView().getGraphVertex(vertex).updateLabelPosition();
                        }
                    }
                }
            }
    	}
    }
    private void edgeApplyUpdate() {
        if (edgeSelectItem != null) {
            	CyperEdge edge = (CyperEdge) edgeSelectItem;
            	Iterator<String> iterator =
                        graph.getDataModel().getEdgeTypeList(edge.getType()).iterator();
                final int selecetIndex = edgeTypeList.getSelectionIndex();
                while (iterator.hasNext()) {
                    String id = iterator.next();
                    FxEdge<CyperEdge, CyperNode> fxEdge = graph.getDataModel().getEdge(id);
                    CyperEdge saveEdge = fxEdge.element();
                    String color = ColorToString(lineColor.getBackground());
                    saveEdge.setLineColor(color);
                    
                    String styleValue = "";
                    for (LineStyle style : LineStyle.values()) {
                    	if (style.ordinal() == lineStyle.getSelectionIndex()) {
                    		styleValue = style.getValue();
                    		break;
                    	}
                    }
                    saveEdge.setLineStyle(styleValue);
                    
                    double tempStStrength = (double)lineStrength.getSelection() / 10;
                    String Strength = String.valueOf(tempStStrength);
                    saveEdge.setLineStrength(Strength);
                    
                    if (!color.isEmpty() | !styleValue.isEmpty() | !Strength.isEmpty()) {
                    	String lineStyle = SmartStyleProxy.getEdgeStyleInputValue(color, styleValue, Strength);
                    	graph.getGraphView().getGraphEdgeBase(fxEdge.element().getID()).setStyle(lineStyle);
                    }
                    
                    saveEdge.setTextSize(edgeTextSize.getSelection());
                    graph.getGraphView().getGraphEdgeBase(fxEdge.element().getID()).setTextSize(saveEdge.getTextSize());
                   	graph.getGraphView().getGraphEdgeBase(fxEdge.element().getID()).updateLabelPosition();
                }
            }
    }

    private void compareItem() {
        ChangeItem.resetStatus();
        if (nodeSelectItem instanceof CyperNode) {
            CyperNode node = (CyperNode) nodeSelectItem;
            if (node.getRadius() * 10 != radius.getSelection()) {
                ChangeItem.changedRadius = true;
            }

            if (!stringToColor(node.getFillColorHexString()).getRGB().toString().equals(
                    colorButton.getBackground().getRGB().toString())) {
                ChangeItem.changedColor = true;
            }

            if (node.getTextSize() * 10 != textSize.getSelection()) {
                ChangeItem.changedTextSize = true;
            }

            if (node.getDisplayType().ordinal() != displayType.getSelectionIndex()) {
                ChangeItem.changedType = true;
            }

            if (displayType.getText().equals(DisplayType.PROPERTY.name())) {
                if (!node.getDisplayProperty().equals(displayProperty.getText())) {
                    ChangeItem.changedProperty = true;
                }
            }

        }
    }

    private static class ChangeItem {
        public static boolean changedRadius = false;
        public static boolean changedColor = false;
        public static boolean changedTextSize = false;
        public static boolean changedType = false;
        public static boolean changedProperty = false;

        public static void resetStatus() {
            changedRadius = false;
            changedColor = false;
            changedTextSize = false;
            changedType = false;
            changedProperty = false;
        }

        public static boolean isChanged() {
            return changedRadius | changedColor | changedTextSize | changedType | changedProperty;
        }
    }
    
    
    private enum LineStyle {
	    SOLID("1 0 1 0"),
	    DOTTED("1 3 1 3"),
	    DASHED("4 4 4 4"),
	    LONG_DASHED("8 4 8 4"),
	    DASH_SINGGGLE_DOOTTED("8 4 2 4"),
	    DASH_DOUBLE_DOTTED("8 4 2 4 2 4");
    	
	    private final String lineValue;
	    
	    LineStyle(String value) { 
	    	this.lineValue = value; 
	    }
	    
	    public String getValue() 
	    { 
	    	return lineValue;
	    }
    }
    
    public void open(int positionX, int positionY) {
    	nodeLableList.setItems(graph.getDataModel().getNodeLableList());
    	edgeTypeList.setItems(graph.getDataModel().getEdgeTypeList());
    	show();
    	setOverlaySize(positionX, positionY, tabFolder.getSize().x, tabFolder.getSize().y);
    }
    
    private void updateItem() {
    	if (tabFolder.getSelectionIndex() == 0) { // NodeTab
    		GraphDataModel g = graph.getDataModel();
        	if (nodeLableList.getItemCount() > 0) {
        		String label = nodeLableList.getText();
        		if (label != null) {
        			CyperNode node = g.getNode(g.getNodeLabelList(label).get(0)).element();
        			if (node != null) {
                		setSelectItem(node);
                	}
        		}
        	}
    	} else { // EdgeTab
    		GraphDataModel g = graph.getDataModel();
        	if (edgeTypeList.getItemCount() > 0) {
        		String type = edgeTypeList.getText();
        		if (type != null) {
        			CyperEdge edge = g.getEdge(g.getEdgeTypeList(type).get(0)).element();
        			if (edge != null) {
                		setSelectItem(edge);
                	}
        		}
        	}
    	}
    }

    @Override
    public void remove() {
        super.remove();
    }
    
    @Override
    public void show() {
        super.show();
        if (nodeSelectItem != null) {
        	setSelectItem(nodeSelectItem);
        }
        
        if (edgeSelectItem != null) {
        	setSelectItem(edgeSelectItem);
        }
    }
    
    @Override
    public void show(int x, int y) {
    	 if (nodeSelectItem != null) {
         	setSelectItem(nodeSelectItem);
         }
         
         if (edgeSelectItem != null) {
         	setSelectItem(edgeSelectItem);
         }
    }
}
