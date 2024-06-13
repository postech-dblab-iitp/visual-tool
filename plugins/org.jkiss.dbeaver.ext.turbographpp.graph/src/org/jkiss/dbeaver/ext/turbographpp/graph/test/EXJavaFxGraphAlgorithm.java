package org.jkiss.dbeaver.ext.turbographpp.graph.test;

import graphvisualizer.graph.AdjacencyMapDigraph;
import graphvisualizer.graph.Graph;
import graphvisualizer.graph.Vertex;
import graphvisualizer.graphview.SmartCircularSortedPlacementStrategy;
import graphvisualizer.graphview.SmartGraphPanel;
import graphvisualizer.graphview.SmartPlacementStrategy;
import java.util.LinkedList;
import java.util.Queue;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class EXJavaFxGraphAlgorithm {

    private FXCanvas canvas;
    private static int selectedVerticesCount = 0;
    private static Vertex<String> startVertex;
    private static Vertex<String> endVertex;

    public EXJavaFxGraphAlgorithm(Composite parent) {

        canvas = new FXCanvas(parent, SWT.NONE);
        final AdjacencyMapDigraph<String, Integer> defaultDigraph = new AdjacencyMapDigraph<>();
        createDefaultDigraph(defaultDigraph);
        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        SmartGraphPanel<String, Integer> graphView =
                new SmartGraphPanel<>(defaultDigraph, strategy);

        //        SmartGraphDemoContainer smartGraphDemoContainer = new
        // SmartGraphDemoContainer(graphView);
        //        MenuPane menu = smartGraphDemoContainer.getMenu();
        final Queue<String> addedVertices = new LinkedList<>();
        createAddedVertices(addedVertices);

        Scene scene = new Scene(graphView, 1024, 768);

        graphView.init();
        graphView.setAutomaticLayout(true);

        //        menu.getStatusBox().setText("[Graph Algorithms Visualization]\n1. Click the
        // buttons above to start the graph algorithms.\n" +
        //                "2. Double click to select the vertices and the edges.\n3. Click \"ADD
        // VERTEX\" to add up to 5 additional vertices.\n" +
        //                "4. Click \"RESET\" to reset the default graph.\n\n" + defaultDigraph);

        graphView.setVertexDoubleClickAction(
                graphVertex -> {
                    switch (selectedVerticesCount) {
                        case 0:
                            for (Vertex<String> vertex : defaultDigraph.vertices()) {
                                graphView.getStylableVertex(vertex).setStyleClass("vertex");
                            }
                            startVertex = graphVertex.getUnderlyingVertex();
                            graphVertex.setStyleClass("selectedVertex");
                            ++selectedVerticesCount;
                            //
                            // menu.getStatusBox().appendText(graphVertex.getUnderlyingVertex() + "
                            // is selected as starting vertex.\n\n");
                            break;

                        case 1:
                            if (!graphVertex.getUnderlyingVertex().equals(startVertex)) {
                                endVertex = graphVertex.getUnderlyingVertex();
                                graphVertex.setStyleClass("selectedVertex");
                                ++selectedVerticesCount;
                                //
                                // menu.getStatusBox().appendText(graphVertex.getUnderlyingVertex()
                                // + " is selected as ending vertex.\n\n");
                            }
                            break;

                        case 2:
                            if (!graphVertex.getUnderlyingVertex().equals(startVertex)
                                    && !graphVertex.getUnderlyingVertex().equals(endVertex)) {
                                graphView.getStylableVertex(startVertex).setStyleClass("vertex");
                                startVertex = endVertex;
                                endVertex = graphVertex.getUnderlyingVertex();
                                graphVertex.setStyleClass("selectedVertex");
                                //                        menu.getStatusBox().appendText(startVertex
                                // + " is selected as new starting vertex.\n\n");
                                //                        menu.getStatusBox().appendText(endVertex +
                                // " is selected as new ending vertex.\n\n");
                            }
                        default:
                    }
                });

        //        graphView.setEdgeDoubleClickAction(graphEdge ->
        // menu.getStatusBox().appendText(graphEdge.getUnderlyingEdge() + " is selected.\n\n"));

        //        menu.setStrongConnectivityButtonAction(event -> {
        //            for (Vertex<String> vertex : defaultDigraph.vertices()) {
        //                graphView.getStylableVertex(vertex).setStyleClass("vertex");
        //            }
        //            menu.getStatusBox().appendText(StrongConnectivity.start(defaultDigraph,
        // graphView));
        //            selectedVerticesCount = 0;
        //            startVertex = endVertex = null;
        //        });

        //        menu.setCycleDetectionButtonAction(event -> {
        //            for (Vertex<String> vertex : defaultDigraph.vertices()) {
        //                graphView.getStylableVertex(vertex).setStyleClass("vertex");
        //            }
        //            menu.getStatusBox().appendText(CycleDetection.start(defaultDigraph,
        // graphView));
        //            selectedVerticesCount = 0;
        //            startVertex = endVertex = null;
        //        });
        //
        //        menu.setShortestPathButtonAction(event -> {
        //            if(startVertex != null && endVertex != null) {
        //                menu.getStatusBox().appendText(ShortestPath.start(defaultDigraph,
        // graphView, startVertex, endVertex));
        //                selectedVerticesCount = 0;
        //                startVertex = endVertex = null;
        //            }
        //            else{
        //                menu.getStatusBox().appendText("Please select two vertices to compute the
        // shortest path between them.\n\n");
        //            }
        //        });
        //
        //        menu.setAddVertexButtonAction(event -> {
        //            if (!addedVertices.isEmpty()) {
        //                String addedVertex = addedVertices.remove();
        //                defaultDigraph.insertVertex(addedVertex);
        //                graphView.update();
        //                menu.getStatusBox().appendText("Vertex{" + addedVertex + "} has been
        // added.\n\n");
        //            }
        //            else {
        //                menu.getStatusBox().appendText("No additional vertices will be
        // added.\n\n");
        //            }
        //        });
        //
        //        menu.setResetButtonAction(event -> {
        //            resetDefaultDigraph(defaultDigraph, graphView);
        //            createAddedVertices(addedVertices);
        //            selectedVerticesCount = 0;
        //            startVertex = endVertex = null;
        //            menu.getStatusBox().appendText("Graph has been reset.\n\n" + defaultDigraph);
        //        });

        canvas.setScene(scene);
    }

    public void finalize() {}

    private void createDefaultDigraph(Graph<String, Integer> defaultDigraph) {
        defaultDigraph.insertVertex("AU");
        defaultDigraph.insertVertex("BE");
        defaultDigraph.insertVertex("DK");
        defaultDigraph.insertVertex("EG");
        defaultDigraph.insertVertex("HK");
        defaultDigraph.insertEdge("AU", "HK", 6);
        defaultDigraph.insertEdge("AU", "EG", 12);
        defaultDigraph.insertEdge("DK", "BE", 1);
        defaultDigraph.insertEdge("DK", "EG", 4);
        defaultDigraph.insertEdge("HK", "BE", 9);

        defaultDigraph.insertVertex("CAU");
        defaultDigraph.insertVertex("CBE");
        defaultDigraph.insertVertex("CDK");
        defaultDigraph.insertVertex("CEG");
        defaultDigraph.insertVertex("CHK");
        defaultDigraph.insertEdge("CAU", "CHK", 6);
        defaultDigraph.insertEdge("CAU", "CEG", 12);
        defaultDigraph.insertEdge("CDK", "CBE", 1);
        defaultDigraph.insertEdge("CDK", "CEG", 4);
        defaultDigraph.insertEdge("CHK", "CBE", 9);
    }

    private void resetDefaultDigraph(
            Graph<String, Integer> defaultDigraph, SmartGraphPanel<String, Integer> graphView) {
        ((AdjacencyMapDigraph<String, Integer>) defaultDigraph).clear();
        graphView.getStylableVertex(defaultDigraph.insertVertex("AU")).setStyleClass("vertex");
        graphView.getStylableVertex(defaultDigraph.insertVertex("BE")).setStyleClass("vertex");
        graphView.getStylableVertex(defaultDigraph.insertVertex("DK")).setStyleClass("vertex");
        graphView.getStylableVertex(defaultDigraph.insertVertex("EG")).setStyleClass("vertex");
        graphView.getStylableVertex(defaultDigraph.insertVertex("HK")).setStyleClass("vertex");
        defaultDigraph.insertEdge("AU", "HK", 6);
        defaultDigraph.insertEdge("AU", "EG", 12);
        defaultDigraph.insertEdge("DK", "BE", 1);
        defaultDigraph.insertEdge("DK", "EG", 4);
        defaultDigraph.insertEdge("HK", "BE", 9);
        graphView.update();
    }

    private void createAddedVertices(Queue<String> addVertices) {
        addVertices.clear();
        addVertices.add("JS");
        addVertices.add("MN");
        addVertices.add("PQ");
        addVertices.add("WX");
        addVertices.add("YZ");
    }
}
