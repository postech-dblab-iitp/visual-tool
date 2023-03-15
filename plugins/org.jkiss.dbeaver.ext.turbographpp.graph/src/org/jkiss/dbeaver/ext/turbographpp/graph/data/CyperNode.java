package org.jkiss.dbeaver.ext.turbographpp.graph.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CyperNode {

    public static final int DEFAULT_DISPLAY_TYPE_ID = 0;
    public static final int DEFAULT_DISPLAY_TYPE_LABLE = 1;
    public static final int DEFAULT_DISPLAY_TYPE_PROPERTY = 2;

    private String display;
    private String id;
    private String label;
    private String fillColor;
    private HashMap<String, Object> property;
    private double lastPositionX, lastPositionY;

    private int defaultDisplayType = DEFAULT_DISPLAY_TYPE_LABLE;
    private String diplayPropertyName = null;

    public CyperNode(String id, String label, HashMap<String, Object> property, String fillColor) {
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
                diplayPropertyName = typeList[i];
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
        return this.fillColor;
    }

    public String getDisplay() {
        display = String.valueOf(this.property.get(diplayPropertyName));
        if (display == null || display.isEmpty() || display.contains("null")) {
            if (defaultDisplayType == DEFAULT_DISPLAY_TYPE_ID) {
                display = id;
            } else {
                display = label;
            }
        }
        return display;
    }

    public HashMap<String, Object> getProperties() {
        return this.property;
    }

    public Object getProperty(String key) {
        return this.property.get(key);
    }

    public String toString() {
        return getDisplay();
    }

    public void setDisplayName(String propertyName) {
        this.diplayPropertyName = propertyName;
    }

    public void setDisplayType(int defaultType) {
        this.defaultDisplayType = defaultType;
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
}
