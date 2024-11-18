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
import java.util.HashMap;
import java.util.List;
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

    public HashMap<String, FxEdge<CypherEdge, CypherNode>> getEdges() {
        return edges;
    }

    public void putNode(String id, List<String> labels, Vertex<CypherNode> node) {
        nodes.put(id, node);
        for (String label : labels) {
            if (nodeLabelList.get(label) == null) {
                ArrayList<String> list = new ArrayList<>();
                list.add(id);
                nodeLabelList.put(label, list);
            } else {
                nodeLabelList.get(label).add(id);
            }
        }
    }

    public void putEdge(String id, List<String> types, FxEdge<CypherEdge, CypherNode> edge) {
        edges.put(id, edge);
        for (String type : types) {
            if (edgeTypeList.get(type) == null) {
                ArrayList<String> list = new ArrayList<>();
                list.add(id);
                edgeTypeList.put(type, list);
            } else {
                edgeTypeList.get(type).add(id);
            }
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

    public String[] getNodeLabelList() {
        return nodeLabelList.keySet().toArray(new String[nodeLabelList.size()]);
    }

    public String[] getEdgeTypeList() {
        return edgeTypeList.keySet().toArray(new String[edgeTypeList.size()]);
    }
}
