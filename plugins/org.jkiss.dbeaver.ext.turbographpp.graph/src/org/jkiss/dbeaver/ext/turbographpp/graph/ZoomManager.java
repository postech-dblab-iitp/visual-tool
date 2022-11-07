package org.jkiss.dbeaver.ext.turbographpp.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

public class ZoomManager {
	
	private final DoubleProperty scaleFactorProperty = new ReadOnlyDoubleWrapper(1);
    private final Node content;
    private ScrollPane scrollPane; 

    private static final double MIN_SCALE = -1;
    private static final double MAX_SCALE = 5;
    private static final double SCROLL_DELTA = 0.25;
    private double currentZoomLevel = 1;
    private double computedZoomLevel = 1;
	
	public ZoomManager(Node content, ScrollPane parentPane) {
		this.content = content;
		this.scrollPane = parentPane;
	}
	
	public void setDefaultZoom() {
		currentZoomLevel = 1;
		computedZoomLevel = 1;
		setZoomLevel(computedZoomLevel);
	}
	
	public void zoomIn() {
		computedZoomLevel = currentZoomLevel; 
		if (computedZoomLevel >= MIN_SCALE && computedZoomLevel <= MAX_SCALE - SCROLL_DELTA) {
			computedZoomLevel = computedZoomLevel + SCROLL_DELTA;
			setZoomLevel(computedZoomLevel);
		}
		
	}
	
	public void zoomOut() {
		if (computedZoomLevel >= MIN_SCALE + SCROLL_DELTA && computedZoomLevel <= MAX_SCALE) {
			computedZoomLevel = computedZoomLevel - SCROLL_DELTA;
			setZoomLevel(computedZoomLevel);
		}
		
	}
	
	public void setZoomLevel(double level) {
		content.setScaleX(level);
		content.setScaleY(level);
	}
	
	public DoubleProperty scaleFactorProperty() {
        return scaleFactorProperty;
    }

	public static double boundValue(double value, double min, double max) {

        if (Double.compare(value, min) < 0) {
            return min;
        }

        if (Double.compare(value, max) > 0) {
            return max;
        }

        return value;
    }

	public void setContentPivot(double x, double y) {
        content.setTranslateX(content.getTranslateX() - x);
        content.setTranslateY(content.getTranslateY() - y);
    }
}
