package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.HashMap;

import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartLabelSource;

public class CyperNode {
	private String display;
	private String id;
	private String label;
    private HashMap<String, Object> property;

    public CyperNode(String id, String label, HashMap<String, Object> property) {
    	this.id = id;
        this.label = label;
        this.display = label;
        this.property = new HashMap<>();
        if (property != null) {
        	this.property.putAll(property);
        } 
    }

    public String getID() {
        return this.id;
    }
    
    public String getLabel() {
        return this.label;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public HashMap<String, Object> getProperties() {
        return this.property;
    }
    
    public Object getProperty(String key) {
        return this.property.get(key);
    }

    public String toString() {
        return display;
    }
}
