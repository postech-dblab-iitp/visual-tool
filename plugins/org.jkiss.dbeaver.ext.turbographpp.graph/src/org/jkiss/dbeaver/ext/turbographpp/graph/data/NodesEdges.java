/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2024 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
