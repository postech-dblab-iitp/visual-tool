package org.jkiss.dbeaver.ext.turbographpp.graph.data;

import java.util.HashMap;
import java.util.Set;

import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartLabelSource;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartStyleProxy;

public class CyperEdge {
    private String id;
    private String type;
    private HashMap<String, String> property;
    private String startNodeID;
    private String endNodeID;
    private DisplayType displayType;
    private String displayProperty;
    private String lineStrength = SmartStyleProxy.DEFAULT_EDGE_LINE_STRENGTH;
    private String lineColor = SmartStyleProxy.DEFAULT_EDGE_LINE_COLOR;
    private String lineStyle = SmartStyleProxy.DEFAULT_EDGE_LINE_STYLE;
    private int textSize = SmartStyleProxy.DEFAULT_EDGE_LABEL_SIZE;
    
    public CyperEdge(
            String id,
            String type,
            HashMap<String, String> property,
            String startNodeID,
            String endNodeID) {
        this.id = id;
        this.type = type;
        this.property = new HashMap<>();
        if (property != null) {
            this.property.putAll(property);
        }
        this.startNodeID = startNodeID;
        this.endNodeID = endNodeID;
        this.displayType = DisplayType.TYPE;
    }

    public String getID() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public HashMap<String, String> getProperties() {
        return this.property;
    }

    public String getProperty(String key) {
        return this.property.get(key);
    }

    public String getStartNodeID() {
        return this.startNodeID;
    }

    public String getEndNodeID() {
        return this.endNodeID;
    }

    public String toString() {
    	switch (displayType) {
            case ID:
            	return id;
            case PROPERTY:
            	return property.get(displayProperty);
            case TYPE:
            	return type;
            default:
                return type;
        }
    }
    
    public void setDisplayType(DisplayType type) {
    	displayType = type;
    }
    
    public void setDisplayProperty(String property) {
    	displayProperty = property;
    }
    
    public void setLineStrength(String strength) {
    	lineStrength = strength;
    }
    
    public String getLineStrength() {
    	return lineStrength;
    }
    
    public void setLineColor(String color) {
    	lineColor = color;
    }
    
    public String getLineColor() {
    	return lineColor;
    }
    
    public void setLineStyle(String style) {
    	lineStyle = style;
    }
    
    public String getLineStyle() {
    	return lineStyle;
    }
    
    public void setTextSize(int size) {
    	textSize = size;
    }
    
    public int getTextSize() {
    	return textSize;
    }
}
