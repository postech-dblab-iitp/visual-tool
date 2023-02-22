package org.jkiss.dbeaver.ext.turbographpp.graph.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.FXGraph;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import org.openide.util.Lookup;

public class GephiModel {

    //private static GephiModel instance = null;
    
    private final ProjectController pc;
    private Workspace workspace;
    private GraphModel graphModel;
    private HashMap<String, Object> graphNodesMap;
    private HashMap<String, Object> graphEdgesMap;
    private Set<Object> highlightedList;
    
    public GephiModel() {
        pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        workspace = pc.getCurrentWorkspace();
        graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
        graphNodesMap = new HashMap<>();
        graphEdgesMap = new HashMap<>();
        highlightedList = new HashSet<>();
    }
    
    public void finalize() {
        pc.closeCurrentProject();
    }   
    
    public GraphModel getGraphModel() {
        return graphModel;
    }
    
    public boolean addNode(FXGraph graph, String id, String label, HashMap<String, Object> attr, Color color){
        
        highlightedList.clear();
        
        if (graph == null || id == null || label == null) {
            return false;
        }
        
        try {
            if (graphModel.getDirectedGraph().getNode(id) == null) {
                Node n = graphModel.factory().newNode(id);
                n.setLabel(label);
                for (String key : attr.keySet()) {
                	if (n.getTable().getColumn(key) == null) {
                		n.getTable().addColumn(key, String.class);
                	}
                	n.setAttribute(key, attr.get(key));
                }
                DirectedGraph directedGraph = graphModel.getDirectedGraph();
                directedGraph.addNode(n);
                addGraphNode(graph, id, label, attr, color);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    private boolean addGraphNode(FXGraph graph, String id, String label, HashMap<String, Object> attr, Color color) {
        try {
            Object node = graph.addNode(id, label, attr, color);
            graphNodesMap.put(id, node);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean updateGraphNode(FXGraph graph, String properyName){
        
//        if (graph == null || properyName == null) {
//            return false;
//        }
//        
//        GraphNode node;
//        String temp;
//        
//        try {
//            if (properyName == "label") {
//                for (String key : graphNodesMap.keySet()) {
//                    node = graphNodesMap.get(key);
//                    if (node != null) {
//                        node.setText(graphModel.getDirectedGraph().getNode(key).getLabel());
//                    }
//                }
//            } else {
//                for (String key : graphNodesMap.keySet()) {
//                    node = graphNodesMap.get(key);
//                    if (node != null) {
//                        if (graphModel.getDirectedGraph().getNode(key) != null 
//                                && graphModel.getDirectedGraph().getNode(key).getAttribute(properyName) != null ) {
//                            temp = graphModel.getDirectedGraph().getNode(key).getAttribute(properyName).toString();
//                            if (temp != null) {
//                                node.setText(temp);
//                            }
//                        }
//                        
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
        
        return true;
    }
    
    public boolean addEdge(FXGraph graph, String id, String label, String startNodeID, String endNodeID, HashMap<String, String> attr) {
        
        DirectedGraph directedGraph = graphModel.getDirectedGraph();
        
        try {
            Node startNode = directedGraph.getNode(startNodeID);
            Node endNode = directedGraph.getNode(endNodeID);
            if (startNode != null && endNode != null) {
                Edge e = graphModel.factory().newEdge(id, startNode, endNode, 0, 1.0, true);
            	e.setLabel(label);
                for (String key : attr.keySet()) {
                	if (e.getTable().getColumn(key) == null) {
                		e.getTable().addColumn(key, String.class);
                	}
                	e.setAttribute(key, attr.get(key));
                }
                directedGraph.addEdge(e);
                Object connection = graph.addEdge(id, label, startNodeID, endNodeID, attr); 
                graphEdgesMap.put(e.getId().toString(), connection);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean gephiDataToZestEdge(FXGraph graph) {
        
        try {
            DirectedGraph directedGraph = graphModel.getDirectedGraph();
            EdgeIterable edgeIterable = directedGraph.getEdges();
            Iterator<Edge> edges = edgeIterable.iterator();
            
            while (edges.hasNext()) {
                Edge edge =  edges.next();
                Object connection = graph.addEdge(edge.getId().toString(), null, 
                        graphNodesMap.get(edge.getSource().getId()).toString(), graphNodesMap.get(edge.getTarget().getId()).toString(), null);
                graphEdgesMap.put(edge.getId().toString(), connection);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

    public boolean setHighlight(FXGraph graph, Object node) {
//        try {
//            Object[] objects = graph.getConnections().toArray() ;           
//            for (int i = 0 ; i < objects.length; i++)
//            {
//                GraphConnection graphCon = (GraphConnection) objects[i];
//                if (graphCon.getSource().equals(node)) {
//                    graphCon.highlight();
//                    graphCon.getSource().highlight();
//                    graphCon.getDestination().highlight();
//                    highlightedList.add(graphCon);
//                    highlightedList.add(graphCon.getSource());
//                    highlightedList.add(graphCon.getDestination());
//                }
//            }            
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
        
        return true;
    }

    public boolean unHighlight() {
//        try {
//            if (highlightedList == null || highlightedList.isEmpty()) {
//                return false;
//            }
//            
//            GraphNode node;
//            GraphConnection edge;
//            for (Object obj : highlightedList) {
//                if (obj.getClass().equals(GraphNode.class)) {
//                    node = (GraphNode)obj;
//                    node.unhighlight();
//                } else if (obj.getClass().equals(GraphConnection.class)) {
//                    edge = (GraphConnection)obj;
//                    edge.unhighlight();
//                }
//            }
//            
//            highlightedList.clear();
//           
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
        
        return true;
    }

    public void clearGraph(FXGraph graph)
    {       
    	if (graph != null) {
    		graph.clearGraph();
    	}
    }
    
    public void clear() {
        if (pc != null) {
            pc.closeCurrentWorkspace();
            pc.newProject();
            workspace = pc.getCurrentWorkspace();
            graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
            graphNodesMap.clear();
            graphEdgesMap.clear();
        }
    }
    
}

