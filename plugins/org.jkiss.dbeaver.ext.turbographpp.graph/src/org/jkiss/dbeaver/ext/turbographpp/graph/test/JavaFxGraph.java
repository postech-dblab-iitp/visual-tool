package org.jkiss.dbeaver.ext.turbographpp.graph.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphPanel;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Graph;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.GraphEdgeList;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartPlacementStrategy;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.containers.SmartGraphDemoContainer;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Digraph;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.DigraphEdgeList;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartCircularSortedPlacementStrategy;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphVertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartStylableNode;

public class JavaFxGraph {
    
    private volatile boolean running;
    private FXCanvas canvas;
    
    public JavaFxGraph(Composite parent) {
        
        canvas = new FXCanvas(parent, SWT.NONE);
        Graph<String, String> g = build_sample_digraph();
        //Graph<String, String> g = build_flower_graph();
        System.out.println(g);
        
        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        //SmartPlacementStrategy strategy = new SmartRandomPlacementStrategy();
        SmartGraphPanel<String, String> graphView = new SmartGraphPanel<>(g, strategy);
        
        //Scene scene = new Scene(new SmartGraphDemoContainer(graphView), 1024, 768);
        Scene scene = new Scene(graphView, 1024, 768);

        graphView.init();
        
        graphView.setVertexDoubleClickAction((SmartGraphVertex<String> graphVertex) -> {
            System.out.println("Vertex contains element: " + graphVertex.getUnderlyingVertex().element());
            //toggle different styling
            if( !graphVertex.removeStyleClass("myVertex") ) {
                /* for the golden vertex, this is necessary to clear the inline
                css class. Otherwise, it has priority. Test and uncomment. */
                //graphVertex.setStyle(null);
                
                graphVertex.addStyleClass("myVertex");
            }
            
            //want fun? uncomment below with automatic layout
            //g.removeVertex(graphVertex.getUnderlyingVertex());
            //graphView.update();
        });

        graphView.setEdgeDoubleClickAction(graphEdge -> {
            System.out.println("Edge contains element: " + graphEdge.getUnderlyingEdge().element());
            //dynamically change the style when clicked
            //graphEdge.setStyle("-fx-stroke: black; -fx-stroke-width: 3;");
            graphEdge.setStyle("-fx-stroke-width: 2; -fx-stroke: #FF6D66; -fx-stroke-dash-array: 2 5 2 5; -fx-fill: transparent; -fx-stroke-line-cap: round; -fx-opacity: 0.8;");
            
            graphEdge.getStylableArrow().setStyle("-fx-stroke: black; -fx-stroke-width: 3;");
            
            //uncomment to see edges being removed after click
            //Edge<String, String> underlyingEdge = graphEdge.getUnderlyingEdge();
            //g.removeEdge(underlyingEdge);
            //graphView.update();
        });
        
        canvas.setScene(scene);
    }

    public void finalize() {
        
    }
    
    private Graph<String, String> build_sample_digraph() {

        Digraph<String, String> g = new DigraphEdgeList<>();

        g.insertVertex("A");
        g.insertVertex("B");
        g.insertVertex("C");
        g.insertVertex("D");
        g.insertVertex("E");
        g.insertVertex("F");

        g.insertEdge("A", "B", "AB");
        g.insertEdge("B", "A", "AB2");
        g.insertEdge("A", "C", "AC");
        g.insertEdge("A", "D", "AD");
        g.insertEdge("B", "C", "BC");
        g.insertEdge("C", "D", "CD");
        g.insertEdge("B", "E", "BE");
        g.insertEdge("F", "D", "DF");
        g.insertEdge("F", "D", "DF2");

        //yep, its a loop!
        g.insertEdge("A", "A", "Loop");

        return g;
    }

    private Graph<String, String> build_flower_graph() {

        Graph<String, String> g = new GraphEdgeList<>();

        g.insertVertex("A");
        g.insertVertex("B");
        g.insertVertex("C");
        g.insertVertex("D");
        g.insertVertex("E");
        g.insertVertex("F");
        g.insertVertex("G");

        g.insertEdge("A", "B", "1");
        g.insertEdge("A", "C", "2");
        g.insertEdge("A", "D", "3");
        g.insertEdge("A", "E", "4");
        g.insertEdge("A", "F", "5");
        g.insertEdge("A", "G", "6");

        g.insertVertex("H");
        g.insertVertex("I");
        g.insertVertex("J");
        g.insertVertex("K");
        g.insertVertex("L");
        g.insertVertex("M");
        g.insertVertex("N");

        g.insertEdge("H", "I", "7");
        g.insertEdge("H", "J", "8");
        g.insertEdge("H", "K", "9");
        g.insertEdge("H", "L", "10");
        g.insertEdge("H", "M", "11");
        g.insertEdge("H", "N", "12");

        g.insertEdge("A", "H", "0");

        //g.insertEdge("A", "B", "13");
        //g.insertVertex("ISOLATED");
        
        return g;
    }
    
    private static final Random random = new Random(/* seed to reproduce*/);

    private void continuously_test_adding_elements(Graph<String, String> g, SmartGraphPanel<String, String> graphView) {
        //update graph
        running = true;
        final long ITERATION_WAIT = 3000; //milliseconds

        Runnable r;
        r = () -> {
            int count = 0;
            
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                
            }
            
            while (running) {
                try {
                    Thread.sleep(ITERATION_WAIT);
                } catch (InterruptedException ex) {
 
                }
                
                //generate new vertex with 2/3 probability, else connect two
                //existing
                String id = String.format("%02d", ++count);
                if (random.nextInt(3) < 2) {
                    //add a new vertex connected to a random existing vertex
                    Vertex<String> existing = get_random_vertex(g);
                    Vertex<String> vertexId = g.insertVertex(("V" + id));
                    g.insertEdge(existing, vertexId, ("E" + id));
                    
                    //this variant must be called to ensure the view has reflected the
                    //underlying graph before styling a node immediately after.
                    graphView.updateAndWait();
                    
                    //color new vertices
                    SmartStylableNode stylableVertex = graphView.getStylableVertex(vertexId);
                    if(stylableVertex != null) {
                        stylableVertex.setStyle("-fx-fill: orange;");
                    }
                } else {
                    Vertex<String> existing1 = get_random_vertex(g);
                    Vertex<String> existing2 = get_random_vertex(g);
                    g.insertEdge(existing1, existing2, ("E" + id));
                    
                    graphView.update();
                }

                
            }
        };

        new Thread(r).start();
    }
    
    private static Vertex<String> get_random_vertex(Graph<String, String> g) {

        int size = g.numVertices();
        int rand = random.nextInt(size);
        Vertex<String> existing = null;
        int i = 0;
        for (Vertex<String> v : g.vertices()) {
            existing = v;
            if (i++ == rand) {
                break;
            }
        }
        return existing;
    }
}

