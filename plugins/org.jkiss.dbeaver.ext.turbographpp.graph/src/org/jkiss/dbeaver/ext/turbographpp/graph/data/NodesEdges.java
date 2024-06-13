package org.jkiss.dbeaver.ext.turbographpp.graph.data;

import java.util.ArrayList;
import java.util.List;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;

public class NodesEdges {
    List<Vertex<CypherNode>> nodes;
    List<FxEdge<CypherEdge, CypherNode>> edges;

    public NodesEdges() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void addNode(Vertex<CypherNode> node) {
        nodes.add(node);
    }

    public void addEdge(FxEdge<CypherEdge, CypherNode> edge) {
        edges.add(edge);
    }

    public List<FxEdge<CypherEdge, CypherNode>> getEdges() {
        return edges;
    }

    public List<Vertex<CypherNode>> getNodes() {
        return nodes;
    }
}
