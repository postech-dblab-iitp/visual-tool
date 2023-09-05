package org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.type;

public class DoublePoint {
	public double x;
	public double y;
	
	public DoublePoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public DoublePoint setPoint(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public DoublePoint add(double x, double y) {
		this.x = this.x + x;
		this.y = this.y + y;
		return this;
	}
	
	public DoublePoint setX(double x) {
		this.x = x;
		return this;
	}
	
	public DoublePoint setY(double y) {
		this.y = y;
		return this;
	}
}
