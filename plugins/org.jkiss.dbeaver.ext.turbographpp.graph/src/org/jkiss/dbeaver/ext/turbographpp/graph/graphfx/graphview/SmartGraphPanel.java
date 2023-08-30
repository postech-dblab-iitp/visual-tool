/* 
 * The MIT License
 *
 * Copyright 2019 brunomnsilva@gmail.com.
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
package org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview;


import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Graph;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherNode;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Digraph;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.layout.SmartPlacementStrategy;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.layout.SmartRandomPlacementStrategy;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import static org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.UtilitiesJavaFX.pick;
import static org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.UtilitiesPoint2D.attractiveForce;
import static org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.UtilitiesPoint2D.repellingForce;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * JavaFX {@link Pane} that is capable of plotting a {@link Graph} or {@link Digraph}.
 * <br>
 * Be sure to call {@link #init() } after the Stage is displayed.
 * <br>
 * Whenever changes to the underlying graph are made, you should call
 * {@link #update()} to force the rendering of any new elements and, also, the
 * removal of others, if applicable.
 * <br>
 * Vertices can be dragged by the user, if configured to do so. Consequently, 
 * any connected edges will also adjust automatically to the new vertex positioning.
 *
 * @param <V> Type of element stored at a vertex
 * @param <E> Type of element stored at an edge
 *
 * @author brunomnsilva
 */
public class SmartGraphPanel<V, E> extends Pane {

    /* 
    CONFIGURATION PROPERTIES
     */
    private final SmartGraphProperties graphProperties;

    /*
    INTERNAL DATA STRUCTURE
     */
    private final Graph<V, E> theGraph;
    private final SmartPlacementStrategy placementStrategy;
    private final Map<Vertex<V>, SmartGraphVertexNode<V>> vertexNodes;
    private final Map<FxEdge<E, V>, SmartGraphEdgeBase> edgeNodes;
    private final Map<String, SmartGraphEdgeBase> edgeNodesForID;
    private Map<FxEdge<E,V>, Tuple<Vertex<V>>> connections;
    private final Map<Tuple<SmartGraphVertexNode>, Integer> placedEdges = new HashMap<>();
    private boolean initialized = false;
    private final boolean edgesWithArrows;
    
    /*
    INTERACTION WITH VERTICES AND EDGES
     */
    private Consumer<SmartGraphVertex<V>> vertexClickConsumer = null;
    private Consumer<SmartGraphEdge<E, V>> edgeClickConsumer = null;

    private Consumer<SmartGraphVertex<V>> vertexOneClickConsumer = null;
    private Consumer<SmartGraphEdge<E, V>> edgeOneClickConsumer = null;
    
    private Consumer<SmartGraphVertex<V>> vertexMovedConsumer = null;
    
    /*
    AUTOMATIC LAYOUT RELATED ATTRIBUTES
     */
    public final BooleanProperty automaticLayoutProperty;
    private AnimationTimer timer;
    private final double repulsionForce;
    private final double attractionForce;
    private final double attractionScale;
    
    //This value was obtained experimentally
    private static final int AUTOMATIC_LAYOUT_ITERATIONS = 10;
    
    private Vertex<V> highlighNode = null;

    private double vertexPosionX = 0;
    private double vertexPosionY = 0;
    
    private Set<Vertex<V>> visitedVertexList = new HashSet<>(); 
    
    private double initWidth = 0;
    private double initHeight = 0;
    
    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using default properties and default random
     * placement of vertices.
     *
     * @param theGraph underlying graph
     *
     * @see Graph
     */
    public SmartGraphPanel(Graph<V, E> theGraph) {
        this(theGraph, new SmartGraphProperties(), null);
    }

    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using custom properties and default random
     * placement of vertices.
     *
     * @param theGraph underlying graph
     * @param properties custom properties
     */
    public SmartGraphPanel(Graph<V, E> theGraph, SmartGraphProperties properties) {
        this(theGraph, properties, null);
    }

    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using default properties and custom placement of
     * vertices.
     *
     * @param theGraph underlying graph
     * @param placementStrategy placement strategy, null for default
     */
    public SmartGraphPanel(Graph<V, E> theGraph, SmartPlacementStrategy placementStrategy) {
        this(theGraph, null, placementStrategy);
    }

    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using custom properties and custom placement of
     * vertices.
     *
     * @param theGraph underlying graph
     * @param properties custom properties, null for default
     * @param placementStrategy placement strategy, null for default
     */
    public SmartGraphPanel(Graph<V, E> theGraph, SmartGraphProperties properties,
            SmartPlacementStrategy placementStrategy) {
        
        this(theGraph, properties, placementStrategy, null);
    }
    
    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using custom properties and custom placement of
     * vertices.
     *
     * @param theGraph underlying graph
     * @param properties custom properties, null for default
     * @param placementStrategy placement strategy, null for default
     * @param cssFile alternative css file, instead of default 'smartgraph.css'
     */
    public SmartGraphPanel(Graph<V, E> theGraph, SmartGraphProperties properties,
            SmartPlacementStrategy placementStrategy, URI cssFile) {

        if (theGraph == null) {
            throw new IllegalArgumentException("The graph cannot be null.");
        }
        this.theGraph = theGraph;
        this.graphProperties = properties != null ? properties : new SmartGraphProperties();
        
        this.placementStrategy = placementStrategy != null ? placementStrategy : new SmartRandomPlacementStrategy();

        this.edgesWithArrows = this.graphProperties.getUseEdgeArrow();

        this.repulsionForce = this.graphProperties.getRepulsionForce();
        this.attractionForce = this.graphProperties.getAttractionForce();
        this.attractionScale = this.graphProperties.getAttractionScale();

        vertexNodes = new HashMap<>();
        edgeNodes = new HashMap<>();
        edgeNodesForID = new HashMap<>();
        connections = new HashMap<>();

        //set stylesheet and class
        //loadStylesheet(cssFile);

        //initNodes();

        enableMouseListener();

        //automatic layout initializations        
        timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                runLayoutIteration();
            }
        };
        
        this.automaticLayoutProperty = new SimpleBooleanProperty(false);
        this.automaticLayoutProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                timer.start();
            } else {
                timer.stop();
            }
        });
                
    }

    private synchronized void runLayoutIteration() {
        for (int i = 0; i < AUTOMATIC_LAYOUT_ITERATIONS; i++) {
            resetForces();
            computeForces();
            updateForces();
        }
        applyForces();
    }    

    /**
     * Runs the initial current vertex placement strategy.
     * <p>
     * This method should only be called once during the lifetime of the object
     * and only after the underlying {@link Scene} is displayed.
     *
     * Further required updates should be performed through the {@link #update()
     * } method.
     *
     * @throws IllegalStateException The exception is thrown if: (1) the Scene
     * is not yet displayed; (2) It has zero width and/or height, and; (3) If
     * this method was already called.
     */
    public void init() throws IllegalStateException {
//        if (this.getScene() == null) {
//            throw new IllegalStateException("You must call this method after the instance was added to a scene.");
//        } 
//        else if (this.getWidth() == 0 || this.getHeight() == 0) {
//            throw new IllegalStateException("The layout for this panel has zero width and/or height");
//        } 
        if (this.initialized) {
        	initialized = false;
        	this.getChildren().clear();
        	removeNodes();
            //throw new IllegalStateException("Already initialized. Use update() method instead.");
        }

        initNodes();
        
        if (placementStrategy != null) {
			placementStrategy.place(this.getMinWidth(),
									this.getMaxHeight(),
									this,
									this.vertexNodes.values());
        } else {
            //apply random placement
            new SmartRandomPlacementStrategy().place(this.widthProperty().doubleValue(),
                    this.heightProperty().doubleValue(),
                    this,
                    this.vertexNodes.values());

            //start automatic layout
            timer.start();
        }

        this.initialized = true;
    }

    /**
     * Returns the property used to toggle the automatic layout of vertices.
     * 
     * @return  automatic layout property
     */
    public BooleanProperty automaticLayoutProperty() {
        return this.automaticLayoutProperty;
    }
    
    /**
     * Toggle the automatic layout of vertices.
     * 
     * @param value     true if enabling; false, otherwise
     */
    public void setAutomaticLayout(boolean value) {
        automaticLayoutProperty.set(value);
    }

    /**
     * Forces a refresh of the visualization based on current state of the
     * underlying graph, immediately returning to the caller.
     * 
     * This method invokes the refresh in the graphical
     * thread through Platform.runLater(), so its not guaranteed that the visualization is in sync
     * immediately after this method finishes. That is, this method
     * immediately returns to the caller without waiting for the update to the
     * visualization.
     * <p>
     * New vertices will be added close to adjacent ones or randomly for
     * isolated vertices.
     */
    public void update() {
        if (this.getScene() == null) {
            throw new IllegalStateException("You must call this method after the instance was added to a scene.");
        }

        if (!this.initialized) {
        	return;
            //throw new IllegalStateException("You must call init() method before any updates.");
        }

        //this will be called from a non-javafx thread, so this must be guaranteed to run of the graphics thread
        Platform.runLater(() -> {
            updateNodes();
        });

    }
    
    /**
     * Forces a refresh of the visualization based on current state of the
     * underlying graph and waits for completion of the update.
     * 
     * Use this variant only when necessary, e.g., need to style an element
     * immediately after adding it to the underlying graph. Otherwise, use
     * {@link #update() } instead for performance sake.
     * <p>
     * New vertices will be added close to adjacent ones or randomly for
     * isolated vertices.
     */
    public void updateAndWait() {
        if (this.getScene() == null) {
            throw new IllegalStateException("You must call this method after the instance was added to a scene.");
        }

        if (!this.initialized) {
            throw new IllegalStateException("You must call init() method before any updates.");
        }
        
        final FutureTask update = new FutureTask(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                updateNodes();
                return true;
            }
        });
        
        //
        if(!Platform.isFxApplicationThread()) {            
            //this will be called from a non-javafx thread, so this must be guaranteed to run of the graphics thread
            Platform.runLater(update);
        
            //wait for completion, only outside javafx thread; otherwise -> deadlock
            try {            
                update.get(1, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                Logger.getLogger(SmartGraphPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            updateNodes();
        }
        
    }

    private synchronized void updateNodes() {
        removeNodes();
        insertNodes();
        updateLabels();
    }

    public synchronized void updateLabel() {
        updateLabels();
    }
    
    /*
    INTERACTION WITH VERTICES AND EDGES
     */
    /**
     * Sets the action that should be performed when a vertex is double clicked.
     *
     * @param action action to be performed
     */
    public void setVertexDoubleClickAction(Consumer<SmartGraphVertex<V>> action) {
        this.vertexClickConsumer = action;
    }

    /**
     * Sets the action that should be performed when an edge is double clicked.
     *
     * @param action action to be performed
     */
    public void setEdgeDoubleClickAction(Consumer<SmartGraphEdge<E, V>> action) {
        this.edgeClickConsumer = action;
    }

    
    public void setVertexSelectAction(Consumer<SmartGraphVertex<V>> action) {
        this.vertexOneClickConsumer = action;
    }
    
    public void setEdgeSelectAction(Consumer<SmartGraphEdge<E, V>> action) {
        this.edgeOneClickConsumer = action;
    }
    
    public void setVertexMovedAction(Consumer<SmartGraphVertex<V>> action) {
        this.vertexMovedConsumer = action;
    }
    
    /*
    NODES CREATION/UPDATES
     */
    private void initNodes() {

        /* create vertex graphical representations */
        for (Vertex<V> vertex : listOfVertices()) {
            SmartGraphVertexNode<V> vertexAnchor = new SmartGraphVertexNode<V>(vertex, 0, 0,
                    graphProperties.getVertexRadius(), graphProperties.getVertexAllowUserMove());
            vertexNodes.put(vertex, vertexAnchor);
        }

        /* create edges graphical representations between existing vertices */
        //this is used to guarantee that no duplicate edges are ever inserted
        List<FxEdge<E, V>> edgesToPlace = listOfEdges();

        for (Vertex<V> vertex : vertexNodes.keySet()) {

            Iterable<FxEdge<E, V>> incidentEdges = theGraph.incomingEdges(vertex);

            for (FxEdge<E, V> edge : incidentEdges) {

                //if already plotted, ignore edge.
                if (!edgesToPlace.contains(edge)) {
                    continue;
                }

                Vertex<V> oppositeVertex = theGraph.opposite(vertex, edge);

                SmartGraphVertexNode<V> graphVertexIn = vertexNodes.get(vertex);
                SmartGraphVertexNode<V> graphVertexOppositeOut = vertexNodes.get(oppositeVertex);

                graphVertexIn.addAdjacentVertex(graphVertexOppositeOut);
                graphVertexOppositeOut.addAdjacentVertex(graphVertexIn);

                SmartGraphEdgeBase graphEdge = createEdge(edge, graphVertexIn, graphVertexOppositeOut);

                /* Track Edges already placed */
                connections.put(edge, new Tuple<>(vertex, oppositeVertex));
                addEdge(graphEdge, edge);

                if (this.edgesWithArrows) {
                    SmartArrow arrow = new SmartArrow(this.graphProperties.getEdgeArrowSize());
                    graphEdge.attachArrow(arrow);
                    this.getChildren().add(arrow);
                }

                edgesToPlace.remove(edge);
            }

        }

        /* place vertices above lines */
        for (Vertex<V> vertex : vertexNodes.keySet()) {
            SmartGraphVertexNode<V> v = vertexNodes.get(vertex);

            addVertex(v);
        }
    }

    private SmartGraphEdgeBase createEdge(FxEdge<E, V> edge, SmartGraphVertexNode<V> graphVertexInbound, SmartGraphVertexNode<V> graphVertexOutbound) {
        /*
        Even if edges are later removed, the corresponding index remains the same. Otherwise, we would have to
        regenerate the appropriate edges.
         */
        int edgeIndex = 0;
        Integer counter = placedEdges.get(new Tuple(graphVertexInbound, graphVertexOutbound));
        if (counter != null) {
            edgeIndex = counter;
        }
        SmartGraphEdgeBase graphEdge;

        if (getTotalEdgesBetween(graphVertexInbound.getUnderlyingVertex(), graphVertexOutbound.getUnderlyingVertex()) > 1) {
        	if (graphVertexInbound == graphVertexOutbound) {
        		graphEdge = new SmartGraphEdgeCurveSelf(edge, graphVertexInbound, graphVertexOutbound, edgeIndex);
        	} else {
        		graphEdge = new SmartGraphEdgeCurve(edge, graphVertexInbound, graphVertexOutbound, edgeIndex);
        	}
        } else {
            graphEdge = new SmartGraphEdgeLine<>(edge, graphVertexInbound, graphVertexOutbound);
        }

        placedEdges.put(new Tuple(graphVertexInbound, graphVertexOutbound), ++edgeIndex);

        return graphEdge;
    }

    private void addVertex(SmartGraphVertexNode<V> v) {
        this.getChildren().add(v);

        String labelText = generateVertexLabel(v.getUnderlyingVertex().element());
        
        if (graphProperties.getUseVertexTooltip()) {            
            Tooltip t = new Tooltip(labelText);
            Tooltip.install(v, t);
        }

        if (graphProperties.getUseVertexLabel()) {
            SmartLabel label = new SmartLabel(labelText);

            //label.addStyleClass("vertex-label");
            label.setStyle(SmartStyleProxy.DEFAULT_VERTEX_LABEL);
            this.getChildren().add(label);
            v.attachLabel(label);
        }
        
        if (v.getUnderlyingVertex().element() instanceof CypherNode) {
        	CypherNode node = (CypherNode) v.getUnderlyingVertex().element();
        	String fillColor = node.getFillColor();
        	v.setStyle(fillColor);
        }
    }

    private void addEdge(SmartGraphEdgeBase e, FxEdge<E, V> edge) {
        //edges to the back
        this.getChildren().add(0, (Node) e);
        edgeNodes.put(edge, e);
        if (e.getUnderlyingEdge().element() instanceof CypherEdge) {
        	CypherEdge tempElement = (CypherEdge)e.getUnderlyingEdge().element();
        	edgeNodesForID.put(tempElement.getID(), e);
        }

        String labelText = generateEdgeLabel(edge.element());
        
        if (graphProperties.getUseEdgeTooltip()) {
            Tooltip t = new Tooltip(labelText);
            Tooltip.install((Node) e, t);
        }

        if (graphProperties.getUseEdgeLabel()) {
            SmartLabel label = new SmartLabel(labelText);

            //label.addStyleClass("edge-label");
            label.setStyle(SmartStyleProxy.DEFAULT_EDGE_LABEL);
            this.getChildren().add(label);
            e.attachLabel(label);
        }
    }

    private void insertNodes() {
        Collection<Vertex<V>> unplottedVertices = unplottedVertices();

        List<SmartGraphVertexNode<V>> newVertices = null;

        Bounds bounds = getPlotBounds();
        double mx = bounds.getMinX() + bounds.getWidth() / 2.0;
        double my = bounds.getMinY() + bounds.getHeight() / 2.0;

        if (!unplottedVertices.isEmpty()) {

            newVertices = new LinkedList<>();

            for (Vertex<V> vertex : unplottedVertices) {
                //create node
                //Place new nodes in the vicinity of existing adjacent ones;
                //Place them in the middle of the plot, otherwise.
                double x, y;
                Collection<FxEdge<E, V>> incidentEdges = theGraph.incomingEdges(vertex);
                if (incidentEdges.isEmpty()) {
                    /* not (yet) connected, put in the middle of the plot */
                    x = mx;
                    y = my;
                } else {
                    FxEdge<E, V> firstEdge = incidentEdges.iterator().next();
                    Vertex<V> opposite = theGraph.opposite(vertex, firstEdge);
                    SmartGraphVertexNode<V> existing = vertexNodes.get(opposite);
                    
                    if(existing == null) {
                        /* 
                        Updates may be coming too fast and we can get out of sync.
                        The opposite vertex exists in the (di)graph, but we have not yet
                        created it for the panel. Therefore, its position is unknown,
                        so place the vertex representation in the middle.
                        */                        
                        x = mx;
                        y = my;
                    } else {
                        /* TODO: fix -- the placing point can be set out of bounds*/
                        Point2D p = UtilitiesPoint2D.rotate(existing.getPosition().add(50.0, 50.0),
                                existing.getPosition(), Math.random() * 360);

                        x = p.getX();
                        y = p.getY();
                    }
                }

                if (vertex.element() instanceof CypherNode) {
                	double lastPositionX = ((CypherNode) vertex.element()).getLastPositionX();
                	double lastPositionY = ((CypherNode) vertex.element()).getLastPositionY();
                	if (lastPositionX != -1) {
                		x = lastPositionX;
                	}
                	if (lastPositionY != -1) {
                		y = lastPositionY;
                	}
                }

                SmartGraphVertexNode newVertex = new SmartGraphVertexNode<>(vertex,
                        x, y, graphProperties.getVertexRadius(), graphProperties.getVertexAllowUserMove());

                //track new nodes
                newVertices.add(newVertex);
                //add to global mapping
                vertexNodes.put(vertex, newVertex);
            }

        }

        Collection<FxEdge<E, V>> unplottedEdges = unplottedEdges();
        if (!unplottedEdges.isEmpty()) {
            for (FxEdge<E, V> edge : unplottedEdges) {

                Vertex<V>[] vertices = edge.vertices();
                Vertex<V> u = vertices[0]; //oubound if digraph, by javadoc requirement
                Vertex<V> v = vertices[1]; //inbound if digraph, by javadoc requirement

                SmartGraphVertexNode<V> graphVertexOut = vertexNodes.get(u);
                SmartGraphVertexNode<V> graphVertexIn = vertexNodes.get(v);

                /* 
                Updates may be coming too fast and we can get out of sync.
                Skip and wait for another update call, since they will surely
                be coming at this pace.
                */
                if(graphVertexIn == null || graphVertexOut == null) {
                    continue;
                }
                
                graphVertexOut.addAdjacentVertex(graphVertexIn);
                graphVertexIn.addAdjacentVertex(graphVertexOut);

                SmartGraphEdgeBase graphEdge = createEdge(edge, graphVertexIn, graphVertexOut);

                if (this.edgesWithArrows) {
                    SmartArrow arrow = new SmartArrow(this.graphProperties.getEdgeArrowSize());
                    graphEdge.attachArrow(arrow);
                    this.getChildren().add(arrow);
                }

                 /* Track edges */
                connections.put(edge, new Tuple<>(u, v));
                addEdge(graphEdge, edge);

            }
        }

        if (newVertices != null) {
            for (SmartGraphVertexNode<V> v : newVertices) {
                addVertex(v);
            }
        }

    }

    private void removeNodes() {
         //remove edges (graphical elements) that were removed from the underlying graph
        Collection<FxEdge<E, V>> removedEdges = removedEdges();
        for (FxEdge<E, V> e : removedEdges) {
            SmartGraphEdgeBase edgeToRemove = edgeNodes.get(e);
            edgeNodes.remove(e);
            removeEdge(edgeToRemove);   //remove from panel

            //when edges are removed, the adjacency between vertices changes
            //the adjacency is kept in parallel in an internal data structure
            Tuple<Vertex<V>> vertexTuple = connections.get(e);

            if( getTotalEdgesBetween(vertexTuple.first, vertexTuple.second) == 0 ) {
                SmartGraphVertexNode<V> v0 = vertexNodes.get(vertexTuple.first);
                SmartGraphVertexNode<V> v1 = vertexNodes.get(vertexTuple.second);

                v0.removeAdjacentVertex(v1);
                v1.removeAdjacentVertex(v0);
            }

            connections.remove(e);
        }

        //remove vertices (graphical elements) that were removed from the underlying graph
        Collection<Vertex<V>> removedVertices = removedVertices();
        for (Vertex<V> removedVertex : removedVertices) {
            SmartGraphVertexNode<V> removed = vertexNodes.remove(removedVertex);
            removeVertex(removed);
        }
                
    }

    private void removeEdge(SmartGraphEdgeBase e) {
        getChildren().remove((Node) e);

        SmartArrow attachedArrow = e.getAttachedArrow();
        if (attachedArrow != null) {
            getChildren().remove(attachedArrow);
        }

        Text attachedLabel = e.getAttachedLabel();
        if (attachedLabel != null) {
            getChildren().remove(attachedLabel);
        }
    }

    private void removeVertex(SmartGraphVertexNode v) {
        getChildren().remove(v);

        Text attachedLabel = v.getAttachedLabel();
        if (attachedLabel != null) {
            getChildren().remove(attachedLabel);
        }
    }

    /**
     * Updates node's labels
     */
    private void updateLabels() {
        theGraph.vertices().forEach((v) -> {
            SmartGraphVertexNode<V> vertexNode = vertexNodes.get(v);
            if (vertexNode != null) {
                SmartLabel label = vertexNode.getAttachedLabel();
                if(label != null) {
                    String text = generateVertexLabel(v.element());
                    label.setText( text );
                }
                
            }
        });
        
        theGraph.edges().forEach((e) -> {
            SmartGraphEdgeBase edgeNode = edgeNodes.get(e);
            if (edgeNode != null) {
                SmartLabel label = edgeNode.getAttachedLabel();
                if (label != null) {
                    String text = generateEdgeLabel(e.element());
                    label.setText( text );
                }
            }
        });
    }
    
    
    /**
     * Updates All Edge Arrow For changed radius.
     */
    public void updateEdgeArrowForRadius() {
        theGraph.edges().forEach((e) -> {
            SmartGraphEdgeBase edgeNode = edgeNodes.get(e);
            if (edgeNode != null) {
                edgeNode.updateArrowPosition();
            }
        });
    }
    
    private String generateVertexLabel(V vertex) {
        
    	if (vertex == null) {
            return "null";
        }
    	
        try {
            Class<?> clazz = vertex.getClass();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(SmartLabelSource.class)) {
                    method.setAccessible(true);
                    Object value = method.invoke(vertex);
                    return value.toString();
                }
            }
        } catch (SecurityException | IllegalAccessException  | IllegalArgumentException |InvocationTargetException ex) {
            Logger.getLogger(SmartGraphPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return vertex != null ? vertex.toString() : "<NULL>";
    }
    
    private String generateEdgeLabel(E edge) {
        
        if (edge == null) {
            return "null";
        }
	
        try {
            Class<?> clazz = edge.getClass();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(SmartLabelSource.class)) {
                    method.setAccessible(true);
                    Object value = method.invoke(edge);
                    return value.toString();
                }
            }
        } catch (SecurityException | IllegalAccessException  | IllegalArgumentException |InvocationTargetException ex) {
            Logger.getLogger(SmartGraphPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return edge != null ? edge.toString() : "<NULL>";
    }
    
    /**
     * Computes the bounding box from all displayed vertices.
     *
     * @return bounding box
     */
    private Bounds getPlotBounds() {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE,
                maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        
        if(vertexNodes.size() == 0) return new BoundingBox(0, 0, getWidth(), getHeight());
        
        for (SmartGraphVertexNode<V> v : vertexNodes.values()) {
            minX = Math.min(minX, v.getCenterX());
            minY = Math.min(minY, v.getCenterY());
            maxX = Math.max(maxX, v.getCenterX());
            maxY = Math.max(maxY, v.getCenterY());
        }

        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }


    /*
    * AUTOMATIC LAYOUT 
     */
    private void computeForces() {
        for (SmartGraphVertexNode<V> v : vertexNodes.values()) {
            for (SmartGraphVertexNode<V> other : vertexNodes.values()) {
                if (v == other) {
                    continue; //NOP
                }

                //double k = Math.sqrt(getWidth() * getHeight() / graphVertexMap.size());
                Point2D repellingForce = repellingForce(v.getUpdatedPosition(), other.getUpdatedPosition(), this.repulsionForce);

                double deltaForceX = 0, deltaForceY = 0;

                //compute attractive and reppeling forces
                //opt to use internal areAdjacent check, because a vertex can be removed from
                //the underlying graph before we have the chance to remove it from our
                //internal data structure
                if (areAdjacent(v, other)) {

                    Point2D attractiveForce = attractiveForce(v.getUpdatedPosition(), other.getUpdatedPosition(),
                            vertexNodes.size(), this.attractionForce, this.attractionScale);

                    deltaForceX = attractiveForce.getX() + repellingForce.getX();
                    deltaForceY = attractiveForce.getY() + repellingForce.getY();
                } else {
                    deltaForceX = repellingForce.getX();
                    deltaForceY = repellingForce.getY();
                }

                v.addForceVector(deltaForceX, deltaForceY);
            }
        }
    }

    private boolean areAdjacent(SmartGraphVertexNode<V> v, SmartGraphVertexNode<V> u) {
        return v.isAdjacentTo(u);
    }

    private void updateForces() {
        vertexNodes.values().forEach((v) -> {
            v.updateDelta();
        });
    }

    private void applyForces() {
        vertexNodes.values().forEach((v) -> {
            v.moveFromForces();
        });
    }

    private void resetForces() {
        vertexNodes.values().forEach((v) -> {
            v.resetForces();
        });
    }

    private int getTotalEdgesBetween(Vertex<V> v, Vertex<V> u) {
        //TODO: It may be necessary to adjust this method if you use another Graph
        //variant, e.g., Digraph (directed graph)
        int count = 0;
        for (FxEdge<E, V> edge : theGraph.edges()) {
            if (edge.vertices()[0] == v && edge.vertices()[1] == u
                    || edge.vertices()[0] == u && edge.vertices()[1] == v) {
                count++;
            }
        }
        return count;
    }

    private List<FxEdge<E, V>> listOfEdges() {
        List<FxEdge<E, V>> list = new LinkedList<>();
        for (FxEdge<E, V> edge : theGraph.edges()) {
            list.add(edge);
        }
        return list;
    }

    private List<Vertex<V>> listOfVertices() {
        List<Vertex<V>> list = new LinkedList<>();
        for (Vertex<V> vertex : theGraph.vertices()) {
            list.add(vertex);
        }
        return list;
    }

    /**
     * Computes the vertex collection of the underlying graph that are not
     * currently being displayed.
     *
     * @return collection of vertices
     */
    private Collection<Vertex<V>> unplottedVertices() {
        List<Vertex<V>> unplotted = new LinkedList<>();

        for (Vertex<V> v : theGraph.vertices()) {
            if (!vertexNodes.containsKey(v)) {
                unplotted.add(v);
            }
        }

        return unplotted;
    }

    /**
     * Computes the collection for vertices that are currently being displayed but do
     * not longer exist in the underlying graph.
     *
     * @return collection of vertices
     */
    private Collection<Vertex<V>> removedVertices() {
        List<Vertex<V>> removed = new LinkedList<>();

        Collection<Vertex<V>> graphVertices = theGraph.vertices();
        Collection<SmartGraphVertexNode<V>> plotted = vertexNodes.values();

        for (SmartGraphVertexNode<V> v : plotted) {
            if (!graphVertices.contains(v.getUnderlyingVertex())) {
                removed.add(v.getUnderlyingVertex());
            }
        }

        return removed;
    }
    
    /**
     * Computes the collection for edges that are currently being displayed but do
     * not longer exist in the underlying graph.
     *
     * @return collection of edges
     */
    private Collection<FxEdge<E, V>> removedEdges() {
        List<FxEdge<E, V>> removed = new LinkedList<>();

        Collection<FxEdge<E, V>> graphEdges = theGraph.edges();
        Collection<SmartGraphEdgeBase> plotted = edgeNodes.values();

        for (SmartGraphEdgeBase e : plotted) {
            if (!graphEdges.contains(e.getUnderlyingEdge())) {
                removed.add(e.getUnderlyingEdge());
            }
        }

        return removed;
    }

    /**
     * Computes the edge collection of the underlying graph that are not
     * currently being displayed.
     *
     * @return collection of edges
     */
    private Collection<FxEdge<E, V>> unplottedEdges() {
        List<FxEdge<E, V>> unplotted = new LinkedList<>();

        for (FxEdge<E, V> e : theGraph.edges()) {
            if (!edgeNodes.containsKey(e)) {
                unplotted.add(e);
            }
        }

        return unplotted;
    }
    
    /**
     * Sets a vertex position (its center) manually.
     * 
     * The positioning should be inside the boundaries of the panel, but
     * no restrictions are enforced by this method, so be aware. 
     * 
     * @param v underlying vertex
     * @param x x-coordinate on panel
     * @param y y-coordinate on panel
     */
    public void setVertexPosition(Vertex<V> v, double x, double y) {
        SmartGraphVertexNode<V> node = vertexNodes.get(v);
        if(node != null) {
            node.setPosition(x, y);
        }
    }
    
    /**
     * Return the current x-coordinate (relative to the panel) of a vertex.
     * 
     * @param v underlying vertex
     * @return the x-coordinate or NaN if the vertex does not exist 
     */
    public double getVertexPositionX(Vertex<V> v) {
        SmartGraphVertexNode<V> node = vertexNodes.get(v);
        if(node != null) {
            return node.getPositionCenterX();
        }
        return Double.NaN;
    }
    
    /**
     * Return the current y-coordinate (relative to the panel) of a vertex.
     * 
     * @param v underlying vertex
     * @return the y-coordinate or NaN if the vertex does not exist 
     */
    public double getVertexPositionY(Vertex<V> v) {
        SmartGraphVertexNode<V> node = vertexNodes.get(v);
        if(node != null) {
            return node.getPositionCenterY();
        }
        return Double.NaN;
    }

    /**
     * Returns the associated stylable element with a graph vertex.
     *
     * @param v underlying vertex
     * @return stylable element
     */
    public SmartStylableNode getStylableVertex(Vertex<V> v) {
        return vertexNodes.get(v);
    }
    
    public SmartGraphVertexNode<V> getGraphVertex(Vertex<V> v) {
        return vertexNodes.get(v);
    }
    /**
     * Returns the associated stylable element with a graph vertex.
     *
     * @param vertexElement underlying vertex's element
     * @return stylable element
     */
    public SmartStylableNode getStylableVertex(V vertexElement) {
        for (Vertex<V> v : vertexNodes.keySet()) {
            if (v.element().equals(vertexElement)) {
                return vertexNodes.get(v);
            }
        }
        return null;
    }

    /**
     * Returns the associated stylable element with a graph edge.
     *
     * @param edge underlying graph edge
     * @return stylable element
     */
    public SmartStylableNode getStylableEdge(FxEdge<E, V> edge) {
        return edgeNodes.get(edge);
    }

    /**
     * Returns the associated stylable element with a graph edge.
     *
     * @param edgeElement underlying graph edge's element
     * @return stylable element
     */
    public SmartStylableNode getStylableEdge(E edgeElement) {
        for (FxEdge<E, V> e : edgeNodes.keySet()) {
            if (e.element().equals(edgeElement)) {
                return edgeNodes.get(e);
            }
        }
        return null;
    }
    
    /**
     * Returns the associated stylable element with a graph vertex.
     *
     * @param v underlying vertex
     * @return stylable element (label)
     */
    public SmartStylableNode getStylableLabel(Vertex<V> v) {
        SmartGraphVertexNode<V> vertex = vertexNodes.get(v);
        
        return vertex != null ? vertex.getStylableLabel() : null;
    }
    
    /**
     * Returns the associated stylable element with a graph edge.
     *
     * @param e underlying graph edge
     * @return stylable element (label)
     */
    public SmartStylableNode getStylableLabel(FxEdge<E,V> e) {
        SmartGraphEdgeBase edge = edgeNodes.get(e);
        
        return edge != null ? edge.getStylableLabel() : null;
    }
   
    public SmartGraphEdgeBase getGraphEdgeBase(String eID) {
    	return edgeNodesForID.get(eID);
    }

    /**
     * Loads the stylesheet and applies the .graph class to this panel.
     */
    private void loadStylesheet(URI cssFile) {
        try {
            String css;
            if( cssFile != null ) {
                css = cssFile.toURL().toExternalForm();
            } else {
                File f = new File("smartgraph.css");
                css = f.toURI().toURL().toExternalForm();
            }

            getStylesheets().add(css);
            this.getStyleClass().add("graph");
        } catch (MalformedURLException ex) {
            Logger.getLogger(SmartGraphPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void enableMouseListener() {
        setOnMouseClicked((MouseEvent mouseEvent) -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

            	Node node = pick(SmartGraphPanel.this, mouseEvent.getSceneX(), mouseEvent.getSceneY());
            	
                if (node == null) {
                    return;
                }
                
                if (node instanceof SmartGraphVertex) {
                	
                	SmartGraphVertex v = (SmartGraphVertex) node;
                	
                	if (vertexMovedConsumer != null) {
                		if (v.getPositionCenterX() != vertexPosionX 
                        		|| v.getPositionCenterY() != vertexPosionY) {
                        	vertexMovedConsumer.accept(v);
                        }
                	}
                	
                	if (mouseEvent.getClickCount() == 2) {
                        if (vertexClickConsumer != null) {
                            vertexClickConsumer.accept(v);
                        }
                    }
                	
                	if (mouseEvent.getClickCount() == 1) {
                		if (vertexOneClickConsumer != null) {
                			vertexOneClickConsumer.accept(v);
                		}
                	}
                	
                } else if (node instanceof SmartGraphEdge) {
                	
                	SmartGraphEdge e = (SmartGraphEdge) node;
                	
                	if (mouseEvent.getClickCount() == 2) {
                        if (vertexClickConsumer != null) {
                        	edgeClickConsumer.accept(e);
                        }
                    }
                	
                	if (mouseEvent.getClickCount() == 1) {
                		if (vertexOneClickConsumer != null) {
                			edgeOneClickConsumer.accept(e);
                		}
                	}
                }
       
            }
        });
        
        setOnMouseMoved((MouseEvent mouseEvent) -> {
        	Node node = mouseEvent.getPickResult().getIntersectedNode();
        	if (node instanceof SmartGraphVertex) {
        		vertexPosionX = ((SmartGraphVertex) node).getPositionCenterX();
        		vertexPosionY = ((SmartGraphVertex) node).getPositionCenterY();
        	}
        });
    }

    /**
     * Represents a tuple in Java.
     *
     * @param <T> the type of the tuple
     */
    private class Tuple<T> {

        private final T first;
        private final T second;

        public Tuple(T first, T second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + Objects.hashCode(this.first);
            hash = 29 * hash + Objects.hashCode(this.second);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Tuple<?> other = (Tuple<?>) obj;
            if (!Objects.equals(this.first, other.first)) {
                return false;
            }
            if (!Objects.equals(this.second, other.second)) {
                return false;
            }
            return true;
        }
    }

    public void doDefaultVertexStyle(SmartGraphVertex<CypherNode> Node) {
		Node.setStyle(SmartStyleProxy.DEFAULT_VERTEX + Node.getUnderlyingVertex().element().getFillColor());
	}
	
    public void doHighlightVertexStyle(SmartGraphVertex<CypherNode> Node) {
		Node.setStyle(SmartStyleProxy.HIGHLIGHT_VERTEX + Node.getUnderlyingVertex().element().getFillColor());
	}
    
    public void doDefaultEdgeStyle(SmartGraphEdgeBase edge) {
		edge.setStyle(SmartStyleProxy.DEFAULT_EDGE);
	}
	
    public void doHighlightEdgeStyle(SmartGraphEdgeBase edge) {
    	edge.setStyle(SmartStyleProxy.HIGHLIGHT_EDGE);
	}
    
    public void setHighlight(Vertex<V> vertex) {
    	highlighNode = vertex;
    	doHighlight(highlighNode, true);
    	visitedVertexList.clear();
    }
    
    public void setUnHighlight() {
    	if (highlighNode != null) {
    		doHighlight(highlighNode, false);
    		highlighNode = null;
    		visitedVertexList.clear();
    	}
    }
    
    public boolean isHighlighted() {
    	if (highlighNode == null) {
    		return false;
    	}
    	return true;
    }
    
    private void doHighlight(Vertex<V> vertex, boolean highlight) {
    	String vertexStyle, edgeStyle;
    	if (highlight) {
    		vertexStyle = SmartStyleProxy.HIGHLIGHT_VERTEX;
    	} else {
    		vertexStyle = SmartStyleProxy.DEFAULT_VERTEX;
    	}
    	
    	SmartGraphVertexNode<V> graphVertexIn = vertexNodes.get(vertex);
    	
    	if (graphVertexIn.getUnderlyingVertex().element() instanceof CypherNode) {
    		CypherNode node = (CypherNode) graphVertexIn.getUnderlyingVertex().element();
    		//Set Vertex Style
    		graphVertexIn.setStyle(vertexStyle + node.getFillColor());
        }
    	
    	Iterable<FxEdge<E, V>> incidentEdges = theGraph.outboundEdges(vertex);
    	for (FxEdge<E, V> edge : incidentEdges) {
    		//Set Edge Style
    		if (highlight) {
    			edgeStyle = SmartStyleProxy.HIGHLIGHT_EDGE;
    		} else {
    			if (edge.element() instanceof CypherEdge) {
    				CypherEdge cyperEdge = (CypherEdge)edge.element();
	    			edgeStyle = SmartStyleProxy.getEdgeStyleInputValue(
	    					cyperEdge.getLineColor(),
	    					cyperEdge.getLineStyle(),
	    					cyperEdge.getLineStrength());
    			} else {
    				edgeStyle = SmartStyleProxy.DEFAULT_EDGE;
    			}
    		}
    		getStylableEdge(edge).setStyle(edgeStyle);

    		Vertex<V> oppositeVertex = theGraph.opposite(vertex, edge);

    		SmartGraphVertexNode<V> graphVertexOppositeOut = vertexNodes.get(oppositeVertex);
    		if (graphVertexOppositeOut.getUnderlyingVertex().element() instanceof CypherNode) {
    			CypherNode node = (CypherNode) graphVertexOppositeOut.getUnderlyingVertex().element();
    			//Set Vertex Style
    			graphVertexOppositeOut.setStyle(vertexStyle + node.getFillColor());
    		}
    		
    		if (!visitedVertexList.contains(graphVertexOppositeOut.getUnderlyingVertex())) {
    			visitedVertexList.add(graphVertexOppositeOut.getUnderlyingVertex());
    			doHighlight(graphVertexOppositeOut.getUnderlyingVertex(), highlight);
    		}
         }
	}
    
    
    public void clear() {
    	highlighNode = null;
    }
    
    public void setSmartPlacementStrategy (SmartPlacementStrategy placementStrategy) {
    	placementStrategy.place(initWidth,
				initHeight,
				this,
				this.vertexNodes.values());
    }
    
    public SmartGraphProperties getSmartGraphProperties() {
    	return graphProperties;
    }
    
    public Graph<V, E> getGraph() {
    	return this.theGraph;
    }
    
    public void setInitSize(double width, double height) {
    	initWidth = width;
    	initHeight = height;
    	System.out.println("Init Size width : " + width);
    	System.out.println("Init Size height : " + height);
    }
    
    public double getInitWidth() {
    	return initWidth;
    }
    
    public double getInitHeight() {
    	return initHeight;
    }
    
}
