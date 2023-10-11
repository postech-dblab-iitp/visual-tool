package org.jkiss.dbeaver.ext.turbographpp.graph.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphProperties;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartStyleProxy;

public class CypherNode {

    public static final int DEFAULT_DISPLAY_TYPE_ID = 0;
    public static final int DEFAULT_DISPLAY_TYPE_LABLE = 1;
    public static final int DEFAULT_DISPLAY_TYPE_PROPERTY = 2;

    private String display;
    private String id;
    private String label;
    private String fillColor;
    private double radius = SmartGraphProperties.DEFAULT_VERTEX_RADIUS;
    private int textSize = SmartStyleProxy.DEFAULT_VERTEX_LABEL_SIZE;
    private HashMap<String, Object> property;
    private double lastPositionX, lastPositionY;

    private DisplayType displayType = DisplayType.PROPERTY;
    private String displayPropertyName = null;

    public CypherNode(String id, String label, HashMap<String, Object> property, String fillColor) {
        this.id = id;
        this.label = label;
        this.display = label;
        this.fillColor = fillColor;
        this.property = new HashMap<>();
        this.lastPositionX = -1;
        this.lastPositionY = -1;
        if (property != null) {
            this.property.putAll(property);
        }
        List<String> keyList = new ArrayList<>(property.keySet());
        Collections.sort(keyList);
        String[] typeList = {null, null, null};

        for (String key : keyList) {
            String type = key.toUpperCase();
            if (type.contains("NAME")) {
                typeList[0] = key;
            }

            if (type.contains("TITLE")) {
                typeList[1] = key;
            }

            if (type.contains("ID")) {
                typeList[2] = key;
            }
        }

        for (int i = 0; i < typeList.length; i++) {
            if (typeList[i] != null) {
            	displayPropertyName = typeList[i];
                break;
            }
        }
    }
    
    public String getID() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public String getFillColor() {
        return "-fx-fill: #" + fillColor;
    }

    public String getFillColorHexString() {
    	return fillColor;
    }
    
    public void setFillColor(String color) {
    	this.fillColor = color;
    }
    
    public String getDisplay() {
        if (displayType == DisplayType.PROPERTY) {
        	display = String.valueOf(this.property.get(displayPropertyName));
        	if (display == null || display.isEmpty() || display.contains("null")) {
            	displayType = DisplayType.TYPE;
                display = label;
        	}
        } else if (displayType == DisplayType.ID){
            display = id;
        } else {
        	display = label;
        }
        return display;
    }

    public HashMap<String, Object> getProperties() {
        return this.property;
    }

    public Object getProperty(String key) {
        return this.property.get(key);
    }
    
    public String getPropertyType(String key) {
        return this.property.get(key).getClass().getTypeName();
    }

    public String toString() {
        return getDisplay();
    }

    public void setDisplayProperty(String propertyName) {
        this.displayPropertyName = propertyName;
    }
    
    public String getDisplayProperty() {
        return this.displayPropertyName;
    }

    public void setDisplayType(DisplayType type) {
        this.displayType = type;
    }
    
    public DisplayType getDisplayType() {
        return this.displayType;
    }

    public void setLastPosition(double x, double y) {
        this.lastPositionX = x;
        this.lastPositionY = y;
    }

    public double getLastPositionX() {
        return this.lastPositionX;
    }

    public double getLastPositionY() {
        return this.lastPositionY;
    }
    
    public void setRadius(double radius) {
    	this.radius = radius;
    }
    
    public double getRadius() {
    	return this.radius;
    }
    
    public void setTextSize(int size) {
    	this.textSize = size;
    }
    
    public int getTextSize() {
    	return this.textSize;
    }
    
}
