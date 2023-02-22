package org.jkiss.dbeaver.ext.turbographpp.graph.data;

import java.util.HashMap;
import java.util.Set;

import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartLabelSource;

public class CyperEdge {
	private String id;
	private String type;
    private HashMap<String, String> property;
    private String startNodeID;
    private String endNodeID;

    public CyperEdge(String id, String type, HashMap<String, String> property, String startNodeID, String endNodeID) {
    	this.id = id;
        this.type = type;
        this.property = new HashMap<>();
        if (property != null) {
        	this.property.putAll(property);
        } 
        this.startNodeID = startNodeID;
        this.endNodeID = endNodeID;
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
    	return type;
    }
}
