package org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph;

import java.util.*;

/**
 * An adjacency map structure for a directed graph. A double map structure is used to represent the
 * directed graph. The vertices are stored in the a map. The outgoing and incoming edges are stored
 * in two maps of the corresponding vertex. The double map structure can be used since the vertices
 * added are unique i.e. with unique <code>String</code> labels. This structure provides similar
 * performance to an adjacency matrix where the {@link #getEdge(Vertex u, Vertex v)} method can
 * achieve O(1) by performing lookup on the first and second map respectively.
 *
 * @param <V> Vertex type
 * @param <E> Edge type
 */
public class TurboGraphList<V, E> implements Graph<V, E> {

    private Map<V, Vertex<V>> vertices;
    private Map<E, FxEdge<E, V>> edges;

    /**
     * Concrete implementation of {@link Vertex}. A {@link DVertex} object stores a {@link V}
     * element and its edges. Edges are implemented as {@link LinkedHashMap} to provide O(1) lookup
     * and also maintain the insertion order.
     */
    private class DVertex implements Vertex<V> {
        private V element;
        private Map<Vertex<V>, FxEdge<E, V>> outgoingEdges, incomingEdges;

        public DVertex(V element) {
            this.element = element;
            outgoingEdges = new LinkedHashMap<>();
            incomingEdges = new LinkedHashMap<>();
        }

        @Override
        public V element() {
            return element;
        }

        public Map<Vertex<V>, FxEdge<E, V>> getOutgoingEdges() {
            return outgoingEdges;
        }

        public Map<Vertex<V>, FxEdge<E, V>> getIncomingEdges() {
            return incomingEdges;
        }

        @Override
        public String toString() {
            return "Vertex{" + element + "}";
        }

        /*
         * 2 DVertex objects are equals if their elements are equal. Override hashCode()
         * if override equals()
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DVertex vertex = (DVertex) o;
            return element.equals(vertex.element);
        }

        @Override
        public int hashCode() {
            return Objects.hash(element);
        }
    }

    /**
     * Concrete implementation of {@link Edge}. A {@link DEdge} object stores an {@link E} element
     * and its {@link V} end vertices.
     */
    private class DEdge implements FxEdge<E, V> {
        private E element;
        Vertex<V> vertexOutbound;
        Vertex<V> vertexInbound;
        int distance;

        public DEdge(Vertex<V> vertexOutbound, Vertex<V> vertexInbound, E element) {
            this.element = element;
            this.vertexOutbound = vertexOutbound;
            this.vertexInbound = vertexInbound;
            this.distance = 1;
        }

        @Override
        public E element() {
            return element;
        }

        @Override
        public Vertex<V>[] vertices() {
            Vertex[] vertices = new Vertex[2];
            vertices[0] = vertexOutbound;
            vertices[1] = vertexInbound;

            return vertices;
        }

        @Override
        public String toString() {
            return "Edge from "
                    + vertexOutbound
                    + " to "
                    + vertexInbound
                    + " with weight of "
                    + element;
        }

        /*
         * 2 DEdge objects are equals if their end vertices elements are equal. Override
         * hashCode() if override equals()
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DEdge edge = (DEdge) o;
            return Arrays.equals(vertices(), edge.vertices());
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(vertices());
        }

        public boolean contains(Vertex<V> v) {
            return (vertexOutbound == v || vertexInbound == v);
        }

        public Vertex<V> getOutbound() {
            return vertexOutbound;
        }

        public Vertex<V> getInbound() {
            return vertexInbound;
        }

        public int getDistance() {
            return distance;
        }

        public int setDistance(int distance) {
            return this.distance = distance;
        }
    }

    /*
     * LinkedHashMap is used to provide a map interface and maintain the insertion
     * order of vertices and elements. This also provides a faster iteration in the
     * Java for-each loop than HashMap.
     */
    public TurboGraphList() {
        this.vertices = new LinkedHashMap<>();
        this.edges = new LinkedHashMap<>();
    }

    public synchronized void clear() {
        vertices.clear();
        edges.clear();
    }

    @Override
    public int numVertices() {
        return vertices.size();
    }

    @Override
    public int numEdges() {
        return edges.size();
    }

    /*
     * synchronized methods are used to prevent thread interference since the graph
     * visualization is run using a non-javafx thread according to the author of
     * JavaFX SmartGraph library.
     */
    @Override
    public synchronized Collection<Vertex<V>> vertices() {
        return vertices.values();
    }

    @Override
    public synchronized Collection<FxEdge<E, V>> edges() {
        return edges.values();
    }

    @Override
    public synchronized Collection<FxEdge<E, V>> incidentEdges(Vertex<V> v)
            throws InvalidVertexException {
        DVertex vertex = validateVertex(v);
        return vertex.getIncomingEdges().values();
    }

    @Override
    public synchronized Collection<FxEdge<E, V>> outboundEdges(Vertex<V> v)
            throws InvalidVertexException {
        DVertex vertex = validateVertex(v);
        return vertex.getOutgoingEdges().values();
    }

    @Override
    public synchronized Vertex<V> opposite(Vertex<V> v, FxEdge<E, V> e)
            throws InvalidVertexException, InvalidEdgeException {
        DVertex vertex = validateVertex(v);
        DEdge edge = validateEdge(e);
        Vertex<V>[] endVertices = edge.vertices();

        if (endVertices[0].equals(vertex)) {
            return endVertices[1];
        } else if (endVertices[1].equals(vertex)) {
            return endVertices[0];
        } else {
            throw new InvalidEdgeException("v is not incident to this edge.");
        }
    }

    @Override
    public synchronized Vertex<V> insertVertex(V element) throws InvalidVertexException {
        if (vertices.containsKey(element)) {
            throw new InvalidVertexException("A vertex with this element already exists.");
        } else {
            DVertex vertex = new DVertex(element);
            vertices.put(element, vertex);
            return vertex;
        }
    }

    @Override
    public synchronized FxEdge<E, V> getEdge(Vertex<V> u, Vertex<V> v)
            throws InvalidVertexException {
        DVertex startVertex = validateVertex(u);
        return startVertex.getOutgoingEdges().get(v);
    }

    @Override
    public synchronized FxEdge<E, V> insertEdge(Vertex<V> u, Vertex<V> v, E element)
            throws InvalidVertexException, InvalidEdgeException {
        if (getEdge(u, v) == null) {
            DVertex startVertex = validateVertex(u);
            DVertex endVertex = validateVertex(v);
            DEdge edge = new DEdge(startVertex, endVertex, element);
            edges.put(element, edge);
            startVertex.getOutgoingEdges().put(endVertex, edge);
            endVertex.getIncomingEdges().put(startVertex, edge);
            return edge;
        } else {
            throw new InvalidEdgeException("Edge from u to v exists.");
        }
    }

    @Override
    public synchronized FxEdge<E, V> insertEdge(V uElement, V vElement, E eElement)
            throws InvalidVertexException, InvalidEdgeException {
        DVertex startVertex = validateVertex(vertices.get(uElement));
        DVertex endVertex = validateVertex(vertices.get(vElement));

        if (getEdge(startVertex, endVertex) == null) {
            DEdge edge = new DEdge(startVertex, endVertex, eElement);
            edges.put(eElement, edge);
            startVertex.getOutgoingEdges().put(endVertex, edge);
            endVertex.getIncomingEdges().put(startVertex, edge);
            return edge;
        } else {
            throw new InvalidEdgeException("Edge from u to v exists.");
        }
    }

    @Override
    public synchronized V removeVertex(Vertex<V> v) throws InvalidVertexException {
        DVertex vertex = validateVertex(v);
        List<FxEdge<E, V>> removedEdges = new LinkedList<>();

        removedEdges.addAll(vertex.getIncomingEdges().values());
        removedEdges.addAll(vertex.getOutgoingEdges().values());

        for (FxEdge<E, V> edge : removedEdges) {
            removeEdge(edge);
        }

        V element = v.element();
        vertices.remove(v.element());
        return element;
    }

    @Override
    public synchronized E removeEdge(FxEdge<E, V> e) throws InvalidEdgeException {
        DEdge edge = validateEdge(e);
        Vertex<V>[] endVertices = edge.vertices();
        DVertex startVertex = validateVertex(endVertices[0]);
        DVertex endVertex = validateVertex(endVertices[1]);

        startVertex.getOutgoingEdges().remove(endVertex);
        endVertex.getIncomingEdges().remove(startVertex);
        E element = edge.element();
        edges.remove(edge);
        return element;
    }

    public synchronized String generateRandomEdge(E randomElement) {
        StringBuilder sb = new StringBuilder();
        if (numEdges()
                == numVertices()
                        * (numVertices() - 1)) // maximum no. of edges in digraph is n(n - 1)
        return sb.append("Graph has maximum number of edges.\n").toString();

        Random random;
        List<V> randomVertices = new ArrayList<>(vertices.keySet());
        V randomVertex;
        DVertex startVertex, endVertex;

        for (; ; ) { // continue to generate a new random edge between random vertices if invalid
            random = new Random();
            randomVertex = randomVertices.get(random.nextInt(randomVertices.size()));
            startVertex = validateVertex(vertices.get(randomVertex));

            random = new Random();
            randomVertex = randomVertices.get(random.nextInt(randomVertices.size()));
            endVertex = validateVertex(vertices.get(randomVertex));

            if (startVertex.equals(endVertex)) { // self-loop is not allowed, retry
                continue;
            } else if (getEdge(startVertex, endVertex)
                    == null) { // a random edge from u to v does not exist
                return sb.append(insertEdge(startVertex, endVertex, randomElement))
                        .append(" is generated.\n")
                        .toString();
            } else if (getEdge(endVertex, startVertex)
                    == null) { // a random edge from v to u does not exist
                return sb.append(insertEdge(endVertex, startVertex, randomElement))
                        .append(" is generated.\n")
                        .toString();
            } // random edges exist between u and v, retry
        }
    }

    /* validate that this vertex belongs to the graph */
    private DVertex validateVertex(Vertex<V> v) throws InvalidVertexException {
        if (v == null) throw new InvalidVertexException("Null vertex.");

        DVertex vertex;

        try {
            vertex = (DVertex) v;
        } catch (ClassCastException e) {
            throw new InvalidVertexException("Not a vertex.");
        }

        if (!vertices.containsKey(vertex.element)) {
            throw new InvalidVertexException("Vertex does not belong to this graph.");
        }
        return vertex;
    }

    /* validate that this edge belongs to the graph */
    private DEdge validateEdge(FxEdge<E, V> e) throws InvalidEdgeException {
        if (e == null) throw new InvalidEdgeException("Null edge.");

        DEdge edge;

        try {
            edge = (DEdge) e;
        } catch (ClassCastException ex) {
            throw new InvalidVertexException("Not an adge.");
        }

        if (!edges.containsKey(edge.element)) {
            throw new InvalidEdgeException("Edge does not belong to this graph.");
        }
        return edge;
    }

    @Override
    public String toString() {
        StringBuilder sb =
                new StringBuilder(
                        String.format(
                                "[Graph with %d vertices and %d edges]\n",
                                numVertices(), numEdges()));

        sb.append("--- Vertices: \n");
        for (Vertex<V> v : vertices.values()) {
            sb.append("\t").append(v.toString()).append("\n");
        }
        sb.append("\n--- Edges: \n");
        for (FxEdge<E, V> e : edges.values()) {
            sb.append("\t").append(e.toString()).append("\n");
        }

        return sb.append("\n").toString();
    }

    @Override
    public boolean areAdjacent(Vertex<V> outbound, Vertex<V> inbound)
            throws InvalidVertexException {
        checkVertex(outbound);
        checkVertex(inbound);

        /* find and edge that goes outbound ---> inbound */
        for (FxEdge<E, V> edge : edges.values()) {
            if (((DEdge) edge).getOutbound() == outbound
                    && ((DEdge) edge).getInbound() == inbound) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V replace(Vertex<V> v, V newElement) throws InvalidVertexException {
        if (existsVertexWith(newElement)) {
            throw new InvalidVertexException("There's already a vertex with this element.");
        }

        DVertex vertex = checkVertex(v);

        V oldElement = vertex.element;
        vertex.element = newElement;

        return oldElement;
    }

    @Override
    public E replace(FxEdge<E, V> e, E newElement) throws InvalidEdgeException {
        if (existsEdgeWith(newElement)) {
            throw new InvalidEdgeException("There's already an edge with this element.");
        }

        DEdge edge = checkEdge(e);

        E oldElement = edge.element;
        edge.element = newElement;

        return oldElement;
    }

    @Override
    public void clearElement() {
        this.vertices.clear();
        this.edges.clear();
    }

    /**
     * Checks whether a given vertex is valid and belongs to this graph
     *
     * @param v
     * @return
     * @throws InvalidVertexException
     */
    private DVertex checkVertex(Vertex<V> v) throws InvalidVertexException {
        if (v == null) throw new InvalidVertexException("Null vertex.");

        DVertex vertex;
        try {
            vertex = (DVertex) v;
        } catch (ClassCastException e) {
            throw new InvalidVertexException("Not a vertex.");
        }

        if (!vertices.containsKey(vertex.element)) {
            throw new InvalidVertexException("Vertex does not belong to this graph.");
        }

        return vertex;
    }

    private DEdge checkEdge(FxEdge<E, V> e) throws InvalidEdgeException {
        if (e == null) throw new InvalidEdgeException("Null edge.");

        DEdge edge;
        try {
            edge = (DEdge) e;
        } catch (ClassCastException ex) {
            throw new InvalidVertexException("Not an adge.");
        }

        if (!edges.containsKey(edge.element)) {
            throw new InvalidEdgeException("Edge does not belong to this graph.");
        }

        return edge;
    }

    private boolean existsVertexWith(V vElement) {
        return vertices.containsKey(vElement);
    }

    private boolean existsEdgeWith(E edgeElement) {
        return edges.containsKey(edgeElement);
    }
}