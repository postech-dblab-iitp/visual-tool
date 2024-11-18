/*
 * The MIT License
 *
 * Copyright 2019 brunomnsilva@gmail.com.
 * Copyright (C) 2024 CUBRID Corporation.
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
import java.util.HashSet;
import java.util.List;
import javafx.geometry.Point2D;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Graph;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphPanel;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphVertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.UtilitiesPoint2D;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.type.DoublePoint;

public class SmartCircularGroupPlacementStrategy implements SmartPlacementStrategy {

    private double paneWidth = 0;
    private double paneHeight = 0;

    double drawingWidth = 0;
    double drawingHeight = 0;
    DoublePoint drawMaxSize = new DoublePoint(0, 0);
    DoublePoint incrementSize = new DoublePoint(80, 0);

    @Override
    public <V, E> void place(
            double width,
            double height,
            SmartGraphPanel<V, E> smartGraphPanel,
            Collection<? extends SmartGraphVertex<V>> vertices) {
        Graph<V, E> graph = smartGraphPanel.getGraph();

        paneWidth = width;
        paneHeight = height;

        HashSet<SmartGraphVertex<V>> visitedVertexs = new HashSet<SmartGraphVertex<V>>();
        HashSet<SmartGraphVertex<V>> notVisitedVertexs = new HashSet<SmartGraphVertex<V>>();
        SmartGraphVertex<V> leaderVertex = null;
        HashSet<SmartGraphVertex<V>> groupVertexs = new HashSet<SmartGraphVertex<V>>();

        for (SmartGraphVertex<V> vertex : toList(vertices)) {
            if (visitedVertexs.contains(vertex)) {
                continue;
            }

            SmartGraphVertex<V> targetVertex =
                    searchGrandParent(smartGraphPanel, graph, vertex, vertex, null);

            if (targetVertex != null) {
                if (targetVertex.equals(vertex)) {
                    notVisitedVertexs.add(vertex);
                    continue;
                }
                vertex = targetVertex;

                Iterable<FxEdge<E, V>> outBoundEdges =
                        graph.outboundEdges(vertex.getUnderlyingVertex());

                if (((Collection<?>) outBoundEdges).size() > 0) {
                    if (!visitedVertexs.contains(vertex)) {
                        leaderVertex = vertex;
                        groupVertexs.clear();
                        outBoundPlace(
                                smartGraphPanel,
                                visitedVertexs,
                                groupVertexs,
                                vertex,
                                leaderVertex,
                                true);
                        drawGroup(smartGraphPanel, leaderVertex, groupVertexs);
                    }
                } else {
                    notVisitedVertexs.add(vertex);
                }
            } else { // inbound
                targetVertex = searchChildParent(smartGraphPanel, graph, vertex, vertex, null);

                if (targetVertex != null) {
                    if (targetVertex.equals(vertex)) {
                        notVisitedVertexs.add(vertex);
                        continue;
                    }
                    vertex = targetVertex;
                }

                Iterable<FxEdge<E, V>> inBoundEdges =
                        graph.incomingEdges(vertex.getUnderlyingVertex());

                if (((Collection<?>) inBoundEdges).size() > 0) {
                    if (!visitedVertexs.contains(vertex)) {
                        leaderVertex = vertex;
                        groupVertexs.clear();
                        inBoundPlace(
                                smartGraphPanel,
                                visitedVertexs,
                                groupVertexs,
                                vertex,
                                leaderVertex,
                                true);
                        drawGroup(smartGraphPanel, leaderVertex, groupVertexs);
                    }
                } else {
                    notVisitedVertexs.add(vertex);
                }
            }
        }

        drawGroup(smartGraphPanel, null, notVisitedVertexs);

        //        boolean first = true;
        //
        //        for (SmartGraphVertex<V> vertex : notVisitedVertexs) {
        //            if (!visitedVertexs.contains(vertex)) {
        //                visitedVertexs.add(vertex);
        //            } else {
        //                System.out.println("Debug CHECK visitedlist and not visited");
        //            }
        //        }
    }

    protected <V, E> void outBoundPlace(
            SmartGraphPanel<V, E> smartGraphPanel,
            HashSet<SmartGraphVertex<V>> visitedVertexs,
            HashSet<SmartGraphVertex<V>> groupVertexs,
            SmartGraphVertex<V> vertex,
            SmartGraphVertex<V> leaderVertex,
            boolean first) {
        Graph<V, E> graph = smartGraphPanel.getGraph();

        if (visitedVertexs.contains(vertex) || groupVertexs.contains(vertex)) {
            return;
        }

        if (!first) {
            if (leaderVertex != null && leaderVertex.equals(vertex)) {
                return;
            }
            groupVertexs.add(vertex);
        }

        Iterable<FxEdge<E, V>> outBoundEdges = graph.outboundEdges(vertex.getUnderlyingVertex());
        for (FxEdge<E, V> edge : outBoundEdges) {
            Vertex<V> childV = (Vertex<V>) edge.vertices()[1];
            SmartGraphVertex<V> smartChildV = smartGraphPanel.getGraphVertex(childV);
            if (!visitedVertexs.contains(smartChildV)) {
                outBoundPlace(
                        smartGraphPanel,
                        visitedVertexs,
                        groupVertexs,
                        smartChildV,
                        leaderVertex,
                        false);
            }
        }

        if (groupVertexs.size() > 1) {
            visitedVertexs.addAll(groupVertexs);
        }

        return;
    }

    protected <V, E> void inBoundPlace(
            SmartGraphPanel<V, E> smartGraphPanel,
            HashSet<SmartGraphVertex<V>> visitedVertexs,
            HashSet<SmartGraphVertex<V>> groupVertexs,
            SmartGraphVertex<V> vertex,
            SmartGraphVertex<V> leaderVertex,
            boolean first) {
        Graph<V, E> graph = smartGraphPanel.getGraph();

        if (visitedVertexs.contains(vertex) || groupVertexs.contains(vertex)) {
            return;
        }

        if (!first) {
            if (leaderVertex != null && leaderVertex.equals(vertex)) {
                return;
            }
            groupVertexs.add(vertex);
        }

        Iterable<FxEdge<E, V>> inBoundEdges = graph.incomingEdges(vertex.getUnderlyingVertex());
        for (FxEdge<E, V> edge : inBoundEdges) {
            Vertex<V> childV = (Vertex<V>) edge.vertices()[0];
            SmartGraphVertex<V> smartChildV = smartGraphPanel.getGraphVertex(childV);
            if (!visitedVertexs.contains(smartChildV)) {
                inBoundPlace(
                        smartGraphPanel,
                        visitedVertexs,
                        groupVertexs,
                        smartChildV,
                        leaderVertex,
                        false);
            }
        }

        if (groupVertexs.size() > 1) {
            visitedVertexs.addAll(groupVertexs);
        }

        return;
    }

    protected <V> Collection<SmartGraphVertex<V>> toList(
            Collection<? extends SmartGraphVertex<V>> vertices) {

        List<SmartGraphVertex<V>> list = new ArrayList<>();
        list.addAll(vertices);

        return list;
    }

    protected <V, E> SmartGraphVertex<V> searchChildParent(
            SmartGraphPanel<V, E> smartGraphPanel,
            Graph<V, E> graph,
            SmartGraphVertex<V> vertex,
            SmartGraphVertex<V> initVertex,
            SmartGraphVertex<V> childVertex) {

        Iterable<FxEdge<E, V>> outBoundEdges = graph.outboundEdges(vertex.getUnderlyingVertex());
        SmartGraphVertex<V> firstVertex = null;
        if (initVertex == null) {
            firstVertex = vertex;
        } else {
            firstVertex = initVertex;
        }

        if (((Collection<?>) outBoundEdges).size() < 1) {
            return null;
        }

        Vertex<V> parentV = null;
        SmartGraphVertex<V> smartparentV = null;
        SmartGraphVertex<V> smartGrandParentV = null;
        for (FxEdge<E, V> edge : outBoundEdges) {
            parentV = (Vertex<V>) edge.vertices()[1];
            smartparentV = smartGraphPanel.getGraphVertex(parentV);
            if (smartparentV.equals(childVertex)) {
                return firstVertex;
            }

            smartGrandParentV =
                    searchChildParent(smartGraphPanel, graph, smartparentV, firstVertex, vertex);
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

    protected <V, E> void changePaneArea(SmartGraphPanel<V, E> smartGraphPanel, DoublePoint size) {
        if (size.getX() > paneWidth) {
            paneWidth = size.getX();
        }

        if (size.getY() > paneHeight) {
            paneHeight = size.getY();
        }

        smartGraphPanel.setMinSize(paneWidth, paneHeight);
        smartGraphPanel.setMaxSize(paneWidth, paneHeight);
    }

    protected <V, E> void drawGroup(
            SmartGraphPanel<V, E> smartGraphPanel,
            SmartGraphVertex<V> leaderVertex,
            HashSet<SmartGraphVertex<V>> groupVertexs) {

        SmartGraphVertex<V> tempVertex = null;

        if (groupVertexs.size() > 0) {
            for (SmartGraphVertex<V> temp : groupVertexs) {
                tempVertex = temp;
                break;
            }
        } else {
            return;
        }

        int N = groupVertexs.size();
        double PI = 3.141592;
        double circumference = tempVertex.getRadius() * 2 * N;
        double diameter = circumference / PI;
        double circleWidth = diameter;
        double circleHeight = diameter;
        if (circleWidth < 300) {
            circleWidth = 300;
        }
        if (circleHeight < 300) {
            circleHeight = 300;
        }

        double areaWidth = circleWidth * 1.2;
        double areaHeight = circleHeight * 1.2;

        if (drawMaxSize.x == 0 && drawMaxSize.y == 0) {
            drawMaxSize.setPoint(areaWidth, areaHeight);
            changePaneArea(smartGraphPanel, drawMaxSize);
            drawingWidth = areaWidth;
            drawingHeight = areaHeight;
        } else {
            if (areaWidth + drawingWidth < paneWidth) {
                incrementSize.setX(drawingWidth);
                if (areaHeight > drawingHeight) {
                    drawingHeight = areaHeight;
                }
                drawingWidth = drawingWidth + areaWidth;

                drawMaxSize.setX(drawingWidth);
                drawMaxSize.setY(drawingHeight);
                changePaneArea(smartGraphPanel, drawMaxSize);

            } else {
                incrementSize.setX(80);
                incrementSize.setY(drawingHeight);

                drawingWidth = areaWidth;
                drawingHeight = drawingHeight + areaHeight;

                if (drawingWidth > drawMaxSize.getX()) {
                    drawMaxSize.setX(drawingWidth);
                }
                drawMaxSize.setY(drawingHeight);
                changePaneArea(smartGraphPanel, drawMaxSize);
            }
        }

        Point2D center =
                new Point2D(
                        circleWidth / 2 + incrementSize.getX(),
                        circleHeight / 2 + incrementSize.getY());
        double angleIncrement = -360f / N;

        boolean first = true;
        Point2D p = null;

        for (SmartGraphVertex<V> vertex : toList(groupVertexs)) {

            if (first) {
                if (leaderVertex != null) {
                    leaderVertex.setPosition(center.getX(), center.getY());
                }

                if (circleWidth > circleHeight) {
                    p =
                            new Point2D(
                                    center.getX(),
                                    center.getY() - circleHeight / 1.8 + vertex.getRadius() * 2);
                } else {
                    p =
                            new Point2D(
                                    center.getX(),
                                    center.getY() - circleWidth / 1.8 + vertex.getRadius() * 2);
                }
                first = false;
            } else {
                p = UtilitiesPoint2D.rotate(p, center, angleIncrement);
            }

            vertex.setPosition(p.getX(), p.getY());
        }

        return;
    }
}
