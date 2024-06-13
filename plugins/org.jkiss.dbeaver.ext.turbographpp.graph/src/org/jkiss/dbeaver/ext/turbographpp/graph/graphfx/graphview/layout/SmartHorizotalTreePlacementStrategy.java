/*
 * The MIT License
 *
 * Copyright 2019 brunomnsilva.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Graph;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphPanel;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphVertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.type.DoublePoint;

public class SmartHorizotalTreePlacementStrategy implements SmartPlacementStrategy {

    private final int INCREMENT_VALUE = 100;
    private final int LINE_INCREMENT_VALUE = 200;

    private DoublePoint startPoint = new DoublePoint(100, 100);
    private DoublePoint endPoint = new DoublePoint(0, 0);

    private double maxWidth = 0;
    private double maxHeight = 0;

    @Override
    public <V, E> void place(
            double width,
            double height,
            SmartGraphPanel<V, E> smartGraphPanel,
            Collection<? extends SmartGraphVertex<V>> vertices) {
        Graph<V, E> graph = smartGraphPanel.getGraph();

        maxWidth = width;
        maxHeight = height;

        // place first vertice north position, others in clockwise manner
        boolean first = true;

        HashMap<SmartGraphVertex<V>, Boolean> visitedVertexs =
                new HashMap<SmartGraphVertex<V>, Boolean>();
        HashMap<SmartGraphVertex<V>, Boolean> notVisitedVertexs =
                new HashMap<SmartGraphVertex<V>, Boolean>();

        for (SmartGraphVertex<V> vertex : sort(vertices)) {
            // Search  Grand Parent

            if (visitedVertexs.containsKey(vertex)) {
                continue;
            }

            SmartGraphVertex<V> targetVertex =
                    searchGrandParent(smartGraphPanel, graph, vertex, vertex, null);

            if (targetVertex != null) {
                if (targetVertex.equals(vertex)) {
                    notVisitedVertexs.put(vertex, false);
                    continue;
                }
                vertex = targetVertex;
            }

            Iterable<FxEdge<E, V>> outBoundEdges =
                    graph.outboundEdges(vertex.getUnderlyingVertex());
            if (((Collection<?>) outBoundEdges).size() > 0) {
                if (!visitedVertexs.containsKey(vertex)) {
                    if (first) {
                        first = false;
                        vertex.setPosition(startPoint.getX(), startPoint.getY());
                        outBoundPlace(smartGraphPanel, visitedVertexs, vertex);
                    } else {
                        endPoint.add(0, INCREMENT_VALUE);
                        if (maxHeight < endPoint.getY()) {
                            endPoint.setPoint(
                                    endPoint.getX() + LINE_INCREMENT_VALUE, INCREMENT_VALUE);
                            startPoint.setPoint(endPoint.getX(), endPoint.getY());
                        } else {
                            startPoint.setPoint(startPoint.getX(), endPoint.getY());
                        }
                        vertex.setPosition(startPoint.getX(), startPoint.getY());
                        outBoundPlace(smartGraphPanel, visitedVertexs, vertex);
                    }
                }
            } else {
                notVisitedVertexs.put(vertex, false);
            }
        }

        endPoint.add(INCREMENT_VALUE, 0);

        for (SmartGraphVertex<V> vertex : notVisitedVertexs.keySet()) {
            if (!visitedVertexs.containsKey(vertex)) {
                visitedVertexs.put(vertex, true);
                //                endPoint.add(0, INCREMENT_VALUE);
                //                if (maxHeight < endPoint.getY()) {
                //                    endPoint.setPoint(endPoint.getX() + INCREMENT_VALUE,
                // INCREMENT_VALUE);
                //                    startPoint.setPoint(endPoint.getX(), endPoint.getY());
                //                } else {
                //                    startPoint.setPoint(endPoint.getX(), endPoint.getY());
                //                }
                //                vertex.setPosition(startPoint.getX(), startPoint.getY());
                Random rand = new Random();

                double x =
                        rand.nextDouble() * (maxWidth - endPoint.getX() + LINE_INCREMENT_VALUE)
                                + endPoint.getX()
                                + LINE_INCREMENT_VALUE;
                double y = rand.nextDouble() * maxHeight;

                vertex.setPosition(x, y);
            }
        }
    }

    protected <V, E> DoublePoint outBoundPlace(
            SmartGraphPanel<V, E> smartGraphPanel,
            HashMap<SmartGraphVertex<V>, Boolean> visitedVertexs,
            SmartGraphVertex<V> vertex) {
        boolean changedSize = false;
        Graph<V, E> graph = smartGraphPanel.getGraph();
        DoublePoint parentPoint =
                new DoublePoint(vertex.getPositionCenterX(), vertex.getPositionCenterY());

        if (visitedVertexs.containsKey(vertex)) {
            return endPoint;
        }
        visitedVertexs.put(vertex, true);

        Iterable<FxEdge<E, V>> outBoundEdges = graph.outboundEdges(vertex.getUnderlyingVertex());
        int numEdge = 0;
        DoublePoint incrementPoint = new DoublePoint(0, 0);
        for (FxEdge<E, V> edge : outBoundEdges) {

            Vertex<V> childV = (Vertex<V>) edge.vertices()[1];
            SmartGraphVertex<V> smartChildV = smartGraphPanel.getGraphVertex(childV);
            if (!visitedVertexs.containsKey(smartChildV)) {
                if (numEdge == 0) {
                    incrementPoint = incrementPoint.add(INCREMENT_VALUE, 0);
                } else {
                    if (endPoint.getY() > parentPoint.getY()) {
                        parentPoint.setY(endPoint.getY());
                        incrementPoint.setPoint(incrementPoint.getX(), INCREMENT_VALUE);
                    } else {
                        incrementPoint.add(0, INCREMENT_VALUE);
                    }
                }
                numEdge++;

                endPoint.setPoint(
                        parentPoint.getX() + incrementPoint.getX(),
                        parentPoint.getY() + incrementPoint.getY());

                if (maxWidth < endPoint.getX()) {
                    maxWidth = endPoint.getX() + INCREMENT_VALUE;
                    changedSize = true;
                }

                if (maxHeight < endPoint.getY()) {
                    maxHeight = endPoint.getY() + INCREMENT_VALUE;
                    changedSize = true;
                }

                if (changedSize) {
                    smartGraphPanel.setMinSize(maxWidth, maxHeight);
                    smartGraphPanel.setMaxSize(maxWidth, maxHeight);
                }
                smartChildV.setPosition(endPoint.getX(), endPoint.getY());
                outBoundPlace(smartGraphPanel, visitedVertexs, smartChildV);
            }
        }
        vertex.setPosition(
                vertex.getPositionCenterX(), (vertex.getPositionCenterY() + endPoint.getY()) / 2);
        return endPoint;
    }

    protected <V> Collection<SmartGraphVertex<V>> sort(
            Collection<? extends SmartGraphVertex<V>> vertices) {

        List<SmartGraphVertex<V>> list = new ArrayList<>();
        list.addAll(vertices);

        Collections.sort(
                list,
                new Comparator<SmartGraphVertex<V>>() {
                    @Override
                    public int compare(SmartGraphVertex<V> t, SmartGraphVertex<V> t1) {
                        return t.getUnderlyingVertex()
                                .element()
                                .toString()
                                .compareToIgnoreCase(t1.getUnderlyingVertex().element().toString());
                    }
                });

        return list;
    }

    protected <V, E> SmartGraphVertex<V> searchGrandParent(
            SmartGraphPanel<V, E> smartGraphPanel,
            Graph<V, E> graph,
            SmartGraphVertex<V> vertex,
            SmartGraphVertex<V> initVertex,
            SmartGraphVertex<V> childVertex) {

        Iterable<FxEdge<E, V>> inBoundEdges = graph.incomingEdges(vertex.getUnderlyingVertex());
        SmartGraphVertex<V> firstVertex = null;
        if (initVertex == null) {
            firstVertex = vertex;
        } else {
            firstVertex = initVertex;
        }

        if (((Collection<?>) inBoundEdges).size() < 1) {
            return null;
        }

        Vertex<V> parentV = null;
        SmartGraphVertex<V> smartparentV = null;
        SmartGraphVertex<V> smartGrandParentV = null;
        for (FxEdge<E, V> edge : inBoundEdges) {
            parentV = (Vertex<V>) edge.vertices()[0];
            smartparentV = smartGraphPanel.getGraphVertex(parentV);
            if (smartparentV.equals(childVertex)) {
                return firstVertex;
            }

            smartGrandParentV =
                    searchGrandParent(smartGraphPanel, graph, smartparentV, firstVertex, vertex);
            if (smartGrandParentV != null) {
                if (smartGrandParentV.equals(firstVertex)) {
                    return firstVertex;
                }
            }
        }

        if (smartGrandParentV == null) {
            return smartparentV;
        }

        if (smartparentV == null) {
            return vertex;
        }

        return smartGrandParentV;
    }
}
