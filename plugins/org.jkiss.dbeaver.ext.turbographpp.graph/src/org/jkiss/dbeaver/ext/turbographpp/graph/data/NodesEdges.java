package org.jkiss.dbeaver.ext.turbographpp.graph.data;

import java.util.ArrayList;
import java.util.List;

import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;

public class NodesEdges {
    List<Vertex<CyperNode>> nodes;
    List<FxEdge<CyperEdge, CyperNode>> edges;

    public NodesEdges() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void addNode(Vertex<CyperNode> node) {
        nodes.add(node);
    }

    public void addEdge(FxEdge<CyperEdge, CyperNode> edge) {
        edges.add(edge);
    }

    public List<FxEdge<CyperEdge, CyperNode>> getEdges() {
        return edges;
    }

    public List<Vertex<CyperNode>> getNodes() {
        return nodes;
    }
}
