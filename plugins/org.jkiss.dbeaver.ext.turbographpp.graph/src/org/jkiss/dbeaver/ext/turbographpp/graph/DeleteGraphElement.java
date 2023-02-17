package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.ArrayList;
import java.util.Collection;

import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphVertex;

public class DeleteGraphElement {

	private SmartGraphVertex<CyperNode> node;
	private Collection<FxEdge<CyperEdge, CyperNode>> edges;
	private double positionX, positionY;
	private Vertex<CyperNode> vertex;
	
	public DeleteGraphElement(SmartGraphVertex<CyperNode> node, double positionX, double positionY) {
    	this.node = node;
        this.positionX = positionX;
        this.positionY = positionY;
        if (this.edges == null) {
        	this.edges = new ArrayList<>(); 
        } 
        this.vertex = null;
    }
	
	public void addEdges(Collection<FxEdge<CyperEdge, CyperNode>> edges) {
		this.edges.addAll(edges);
	}

	public SmartGraphVertex<CyperNode> getNode() {
		return this.node;
	}
	
	public double getPositionX() {
		return this.positionX;
	}
	
	public double getPositionY() {
		return this.positionY;
	}
	
	public Collection<FxEdge<CyperEdge, CyperNode>> getEdges() {
		return this.edges;
	}

	public void setVertex(Vertex<CyperNode> v) {
		this.vertex = v;
	}
	
	public Vertex<CyperNode> getVertex() {
		return this.vertex;
	}
}
