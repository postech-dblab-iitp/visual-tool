package org.jkiss.dbeaver.ext.turbographpp.graph.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;

public class GraphDataModel {
	
    private HashMap<String, Vertex<CypherNode>> nodes = new HashMap<>();
    private HashMap<String, FxEdge<CypherEdge, CypherNode>> edges = new HashMap<>();
    private HashMap<String, ArrayList<String>> nodeLabelList = new HashMap<>();
    private HashMap<String, ArrayList<String>> edgeTypeList = new HashMap<>();

    public HashMap<String, Vertex<CypherNode>> getNodes() {
        return nodes;
    }

    public void putNode(String id, String label, Vertex<CypherNode> node) {
    	nodes.put(id, node);
    	if (nodeLabelList.get(label) == null) {
    		ArrayList<String> list = new ArrayList<>();
    		list.add(id);
    		nodeLabelList.put(label, list);
    	} else {
    		nodeLabelList.get(label).add(id);
    	}
    }
    
    public void putEdge(String id, String type, FxEdge<CypherEdge, CypherNode> edge) {
    	edges.put(id, edge);
    	if (edgeTypeList.get(type) == null) {
    		ArrayList<String> list = new ArrayList<>();
    		list.add(id);
    		edgeTypeList.put(type, list);
    	} else {
    		edgeTypeList.get(type).add(id);
    	}
    }
    
    public Vertex<CypherNode> getNode(String id) {
    	return nodes.get(id);
    }
    
    public FxEdge<CypherEdge, CypherNode> getEdge(String id) {
    	return edges.get(id);
    }
    
    public void clear() {
    	nodes.clear();
    	edges.clear();
    	nodeLabelList.clear();
    	edgeTypeList.clear();
    }
    
    public ArrayList<String> getNodeLabelList(String label) {
    	return nodeLabelList.get(label);
    }
    
    public ArrayList<String> getEdgeTypeList(String type) {
    	return edgeTypeList.get(type);
    }
    
    public String[] getNodeLableList () {
    	return nodeLabelList.keySet().toArray(new String[nodeLabelList.size()]);
    }
    
    public String[] getEdgeTypeList () {
    	return edgeTypeList.keySet().toArray(new String[edgeTypeList.size()]);
    }
    
}
