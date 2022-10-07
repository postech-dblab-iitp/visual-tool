package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.graphics.Color;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

public class GephiModel {

    //private static GephiModel instance = null;
    
    private final ProjectController pc;
    private Workspace workspace;
    private GraphModel graphModel;
    private HashMap<String, GraphNode> graphNodesMap;
    private HashMap<String, GraphConnection> graphEdgesMap;
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
    
    public boolean addNode(Graph graph, String id, String label, HashMap<String, Object> attr, Color color){
        
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
                addZestNode(graph, id, label, color);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    private boolean addZestNode(Graph graph, String id, String label, Color color) {
        try {
            GraphNode node = new GraphNode(graph, ZestStyles.NODES_FISHEYE, label, id);
            node.setBackgroundColor(color);
            graphNodesMap.put(id, node);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean updateZestNode(Graph graph, String properyName){
        
        if (graph == null || properyName == null) {
            return false;
        }
        
        GraphNode node;
        String temp;
        
        try {
            if (properyName == "label") {
                for (String key : graphNodesMap.keySet()) {
                    node = graphNodesMap.get(key);
                    if (node != null) {
                        node.setText(graphModel.getDirectedGraph().getNode(key).getLabel());
                    }
                }
            } else {
                for (String key : graphNodesMap.keySet()) {
                    node = graphNodesMap.get(key);
                    if (node != null) {
                        if (graphModel.getDirectedGraph().getNode(key) != null 
                                && graphModel.getDirectedGraph().getNode(key).getAttribute(properyName) != null ) {
                            temp = graphModel.getDirectedGraph().getNode(key).getAttribute(properyName).toString();
                            if (temp != null) {
                                node.setText(temp);
                            }
                        }
                        
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    public boolean addEdge(Graph graph, String id, String label, String startNodeID, String endNodeID, HashMap<String, String> attr) {
        
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
                GraphConnection connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, 
                        graphNodesMap.get(e.getSource().getId()), graphNodesMap.get(e.getTarget().getId()));
                connection.setData(id);
                connection.setLineColor(new Color(0,0,0));
                connection.setLineWidth(2);
                connection.setText(label);
                graphEdgesMap.put(e.getId().toString(), connection);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean gephiDataToZestEdge(Graph graph) {
        
        try {
            DirectedGraph directedGraph = graphModel.getDirectedGraph();
            EdgeIterable edgeIterable = directedGraph.getEdges();
            Iterator<Edge> edges = edgeIterable.iterator();
            
            while (edges.hasNext()) {
                Edge edge =  edges.next();
                GraphConnection connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, 
                        graphNodesMap.get(edge.getSource().getId()), graphNodesMap.get(edge.getTarget().getId()));
                graphEdgesMap.put(edge.getId().toString(), connection);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

    public boolean setHighlight(Graph graph, GraphNode node) {
        try {
            Object[] objects = graph.getConnections().toArray() ;           
            for (int i = 0 ; i < objects.length; i++)
            {
                GraphConnection graphCon = (GraphConnection) objects[i];
                if (graphCon.getSource().equals(node)) {
                    graphCon.highlight();
                    graphCon.getSource().highlight();
                    graphCon.getDestination().highlight();
                    highlightedList.add(graphCon);
                    highlightedList.add(graphCon.getSource());
                    highlightedList.add(graphCon.getDestination());
                }
            }            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

    public boolean unHighlight() {
        try {
            if (highlightedList == null || highlightedList.isEmpty()) {
                return false;
            }
            
            GraphNode node;
            GraphConnection edge;
            for (Object obj : highlightedList) {
                if (obj.getClass().equals(GraphNode.class)) {
                    node = (GraphNode)obj;
                    node.unhighlight();
                } else if (obj.getClass().equals(GraphConnection.class)) {
                    edge = (GraphConnection)obj;
                    edge.unhighlight();
                }
            }
            
            highlightedList.clear();
           
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

    public void clearGraph(Graph graph)
    {       
        Object[] objects = graph.getConnections().toArray() ;           
        for (int i = 0 ; i < objects.length; i++)
        {
            GraphConnection graCon = (GraphConnection) objects[i];
            if(!graCon.isDisposed())
                graCon.dispose();
        }            

        objects = graph.getNodes().toArray();       
        for (int i = 0; i < objects.length; i++)
        {
            GraphNode graNode = (GraphNode) objects[i];
            if(!graNode.isDisposed())
                graNode.dispose();
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

