package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.HashMap;

import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartLabelSource;

public class CyperEdge {
	private String id;
	private String label;
    private HashMap<String, String> property;
    private String startNodeID;
    private String endNodeID;

    private String display;
    
    public CyperEdge(String id, String label, HashMap<String, String> property, String startNodeID, String endNodeID) {
    	this.id = id;
        this.label = label;
        this.property = new HashMap<>();
        if (property != null) {
        	this.property.putAll(property);
        } 
        this.startNodeID = startNodeID;
        this.endNodeID = endNodeID;
        this.display = label;
    }

    public String getID() {
        return this.id;
    }
    
    public String label() {
    	return this.label;
    }
    
    public void setDisplay(String display) {
        this.display = display;
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
    	return this.display;
    }
}
