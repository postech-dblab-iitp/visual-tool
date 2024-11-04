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
import java.util.Collection;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphVertex;

public class DeleteGraphElement {

    private SmartGraphVertex<CypherNode> node;
    private Collection<FxEdge<CypherEdge, CypherNode>> edges;
    private double positionX, positionY;
    private Vertex<CypherNode> vertex;

    public DeleteGraphElement(
            SmartGraphVertex<CypherNode> node, double positionX, double positionY) {
        this.node = node;
        this.positionX = positionX;
        this.positionY = positionY;
        if (this.edges == null) {
            this.edges = new ArrayList<>();
        }
        this.vertex = null;
    }

    public void addEdges(Collection<FxEdge<CypherEdge, CypherNode>> edges) {
        this.edges.addAll(edges);
    }

    public SmartGraphVertex<CypherNode> getNode() {
        return this.node;
    }

    public double getPositionX() {
        return this.positionX;
    }

    public double getPositionY() {
        return this.positionY;
    }

    public Collection<FxEdge<CypherEdge, CypherNode>> getEdges() {
        return this.edges;
    }

    public void setVertex(Vertex<CypherNode> v) {
        this.vertex = v;
    }

    public Vertex<CypherNode> getVertex() {
        return this.vertex;
    }
}
