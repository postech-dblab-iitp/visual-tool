package org.jkiss.dbeaver.ext.turbographpp.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.geometry.Bounds;
import javafx.scene.Node;

public class ZoomManager {
	
	private final DoubleProperty scaleFactorProperty = new ReadOnlyDoubleWrapper(1);
    private final Node content;

    private static final double MIN_SCALE = 1;
    private static final double MAX_SCALE = 5;
    private static final double SCROLL_DELTA = 0.25;
	
	public ZoomManager(Node content) {
		this.content = content;

	}
	
	public void setDefaultZoom() {
		
	}
	
	public void setZoomLevel(int level) {
		double direction = level;

        double currentScale = scaleFactorProperty.getValue();
        double computedScale = currentScale + direction * SCROLL_DELTA;

        computedScale = boundValue(computedScale, MIN_SCALE, MAX_SCALE);

        if (currentScale != computedScale) {

            content.setScaleX(computedScale);
            content.setScaleY(computedScale);

            if (computedScale == 1) {
                content.setTranslateX(-content.getTranslateX());
                content.setTranslateY(-content.getTranslateY());
            } else {
                scaleFactorProperty.setValue(computedScale);

                Bounds bounds = content.localToScene(content.getBoundsInLocal());
                double f = (computedScale / currentScale) - 1;
                //double dx = (event.getX() - (bounds.getWidth() / 2 + bounds.getMinX()));
                //double dy = (event.getY() - (bounds.getHeight() / 2 + bounds.getMinY()));

                //setContentPivot(f * dx, f * dy);
            }

        }
		
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
