package org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphalgorithms;

import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherNode;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.NodesEdges;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Entry;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.HeapAdaptablePriorityQueue;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.TurboGraphList;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphPanel;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartStyleProxy;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherEdge;

import java.util.*;

/**
 * A shortest path algorithm which implements Dijkstra’s algorithm that can be used on a directed
 * graph that is either strongly connected or not strongly connected with random generation of
 * directed edges
 */
public class ShortestPath {
    /**
     * Construct a visualization of shortest path algorithm in a directed graph which is either
     * strongly connected not strongly connected referenced by <code>digraph</code>. Random directed
     * edges will be generated until a path exists between the starting vertex and ending vertex
     *
     * @param digraph Directed graph
     * @param graphView Graph visualization object
     * @param startVertex Starting vertex of the shortest path algorithm
     * @param endVertex Ending vertex of the shortest path algorithm
     * @return The weight count from the starting vertex to ending vertex of the Dijkstra’s
     *     algorithm
     */
    public static NodesEdges start(
            TurboGraphList<CypherNode, CypherEdge> digraph,
            SmartGraphPanel<CypherNode, CypherEdge> graphView,
            Vertex<CypherNode> startVertex,
            Vertex<CypherNode> endVertex,
            String propertyName) {
        LinkedHashMap<Vertex<CypherNode>, Integer> d = new LinkedHashMap<>();

        int[] weight = {1};
        digraph.setlastWeight(0);
        digraph.clearLastPathString();

        if (!dijkstra(digraph, startVertex, endVertex, d, weight, propertyName)) {
            return null;
        }

        digraph.setlastWeight(weight[0]);

        return generatePath(digraph, startVertex, endVertex, d, graphView, propertyName);
    }

    /**
     * An implementation of Dijkstra’s algorithm to determine and pass the weight of shortest path
     * between the starting vertex and the ending vertex through array weight. This is also to
     * detect whether a path exists between the starting and ending vertices and return boolean
     * value. This should be called repeatedly by <code>
     * start(AdjacencyMapDigraph, SmartGraphPanel, Vertex<String>, Vertex<String>)</code>.
     *
     * @param digraph Directed graph
     * @param startVertex Starting vertex of the shortest path algorithm
     * @param endVertex Ending vertex of the shortest path algorithm
     * @param d Map to store the distance/weight of each vertex from the starting vertex
     * @param weight The weight count of the shortest path
     */
    private static boolean dijkstra(
            TurboGraphList<CypherNode, CypherEdge> digraph,
            Vertex<CypherNode> startVertex,
            Vertex<CypherNode> endVertex,
            LinkedHashMap<Vertex<CypherNode>, Integer> d,
            int[] weight,
            String propertyName) {

        Map<Vertex<CypherNode>, Integer> cloud = new LinkedHashMap<>();
        HeapAdaptablePriorityQueue<Integer, Vertex<CypherNode>> pq =
                new HeapAdaptablePriorityQueue<>();
        Map<Vertex<CypherNode>, Entry<Integer, Vertex<CypherNode>>> pqTokens = new LinkedHashMap<>();
        // for each vertex of the graph, add an entry to the priority queue with the
        // source having
        // distance 0 and all others having infinite distance
        for (Vertex<CypherNode> v : digraph.vertices()) {
            if (v.equals(startVertex)) {
                d.put(v, 0);
            } else {
                d.put(v, Integer.MAX_VALUE);
            }
            // save the entry for future updates
            pqTokens.put(v, pq.insert(d.get(v), v));
        }

        // Add all the reachable vertices to the Map cloud
        while (!pq.isEmpty()) {
            Entry<Integer, Vertex<CypherNode>> entry = pq.removeMin();
            int key = entry.getKey();
            Vertex<CypherNode> u = entry.getValue();
            cloud.put(u, key); // the actual distance to u
            pqTokens.remove(u); // remove u from pq

            for (FxEdge<CypherEdge, CypherNode> edge : digraph.outboundEdges(u)) {
                Vertex<CypherNode> v = digraph.opposite(u, edge);

                if (cloud.get(v) == null) {
                    // perform the relaxation step on edge (u,v)
                    int wgt = 1;
                    try {
                        wgt = Integer.valueOf(edge.element().getProperty(propertyName));
                    } catch (NumberFormatException ex) {
                        wgt = 1;
                    }

                    if ((d.get(u) + wgt)
                            < d.get(v)) { // check if there is any better/shorter path to v
                        d.put(v, (d.get(u) + wgt)); // update the distance in Map d
                        pq.replaceKey(pqTokens.get(v), d.get(v)); // update the pq entry
                    }
                }
            }
        }
        weight[0] = cloud.get(endVertex);

        // check if there is any path can be reach by starting vertex to ending vertex
        // and return a
        // boolean value
        if (cloud.get(endVertex) >= Integer.MAX_VALUE) {
        	weight[0] = 0;
            return false;
        } else return Integer.signum(cloud.get(endVertex)) != -1;
    }

    /**
     * Reconstruct a shortest-path tree rooted at starting vertex, the tree is represented as a map
     * from each reachable vertex other than the starting vertex to the edge that is used to reach a
     * vertex from its parent u in the tree. Then, this method return the String value of the vertex
     * or vertices on the path from starting vertex to ending vertex. This should be called
     * repeatedly by <code>
     * start(AdjacencyMapDigraph, SmartGraphPanel, Vertex<String>, Vertex<String>)</code>.
     *
     * @param digraph Directed graph
     * @param startVertex Starting vertex of the shortest path algorithm
     * @param endVertex Ending vertex of the shortest path algorithm
     * @param d Map to store the distance/weight of each vertex from the starting vertex
     * @param graphView Graph visualization object
     */
    private static NodesEdges generatePath(
            TurboGraphList<CypherNode, CypherEdge> digraph,
            Vertex<CypherNode> startVertex,
            Vertex<CypherNode> endVertex,
            LinkedHashMap<Vertex<CypherNode>, Integer> d,
            SmartGraphPanel<CypherNode, CypherEdge> graphView,
            String propertyName) {
        Map<Vertex<CypherNode>, FxEdge<CypherEdge, CypherNode>> tree = new LinkedHashMap<>();
        Map<Vertex<CypherNode>, Vertex<CypherNode>> parentsOfVertices = new HashMap<>();
        NodesEdges nodesEdges = new NodesEdges();

        for (Vertex<CypherNode> vertex : d.keySet()) {
            if (vertex != startVertex) {
                for (FxEdge<CypherEdge, CypherNode> edge :
                        digraph.incomingEdges(vertex)) { // consider the incoming edges
                    Vertex<CypherNode> u = digraph.opposite(vertex, edge);
                    int wgt = 1;
                    try {
                        wgt = Integer.valueOf(edge.element().getProperty(propertyName));
                    } catch (NumberFormatException ex) {
                        wgt = 1;
                    }
                    if (d.get(vertex) == d.get(u) + wgt) {
                        tree.put(vertex, edge); // The vertices and edges are stored
                        parentsOfVertices.put(vertex, u); // The parents of vertices are stored
                    }
                }
            }
        }
        Stack<Vertex<CypherNode>> path = new Stack<>(); // set of vertices on the path
        Vertex<CypherNode> vertexInPath;
        Vertex<CypherNode> oldVertexInPath;

        // push the vertex or vertices on path into the stack
        for (vertexInPath = endVertex;
                vertexInPath != null && !vertexInPath.equals(startVertex);
                vertexInPath = parentsOfVertices.get(vertexInPath)) {
            if (vertexInPath != null) {
                path.push(vertexInPath);
            }
        }

        // push the starting vertex into the stack
        path.push(startVertex);

        // Set the style class of the vertex or vertices on path and update the
        // graphview
        while (!path.isEmpty()) {
            oldVertexInPath = vertexInPath;
            vertexInPath = path.pop();
            nodesEdges.addNode(vertexInPath);
            CypherNode node = graphView.getGraphVertex(vertexInPath).getUnderlyingVertex().element();
            graphView
                    .getStylableVertex(vertexInPath)
                    .setStyle(SmartStyleProxy.HIGHLIGHT_VERTEX + node.getFillColor());
            graphView.update();

            for (FxEdge<CypherEdge, CypherNode> edge : digraph.incomingEdges(vertexInPath)) {
                Vertex<CypherNode> u = digraph.opposite(vertexInPath, edge);
                if (oldVertexInPath != null
                        && !oldVertexInPath.equals(vertexInPath)
                        && oldVertexInPath.equals(u)) {
                    if (graphView.getStylableEdge(edge) != null) {
                        graphView.getStylableEdge(edge).setStyle(SmartStyleProxy.HIGHLIGHT_EDGE);
                        nodesEdges.addEdge(edge);
                        int path_wet = 1;
                        if (propertyName == null) {
                            path_wet = 1;
                        } else {
                            path_wet = Integer.valueOf(edge.element().getProperty(propertyName));
                        }
                        digraph.addLastPathString(
                                edge.vertices()[0].element().getDisplay()
                                        + "->"
                                        + edge.vertices()[1].element().getDisplay()
                                        + " : "
                                        + path_wet
                                        + "\n");
                    }
                }
            }
        }

        digraph.setPathCount(nodesEdges.getEdges().size());
        return nodesEdges;
    }
}
