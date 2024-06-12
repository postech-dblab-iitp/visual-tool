package org.jkiss.dbeaver.ext.turbographpp.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

public class ZoomManager {

    private final DoubleProperty scaleFactorProperty = new ReadOnlyDoubleWrapper(1);
    private final Node content;
    private ScrollPane scrollPane;

    private static final double MIN_SCALE = 0.001;
    private static final double MAX_SCALE = 5;
    private static final double SCROLL_DELTA = 0.05;
    private double computedZoomLevel = 1;

    public ZoomManager(Node content, ScrollPane scrollPane) {
        this.content = content;
        this.scrollPane = scrollPane;
    }

    public void setDefaultZoom() {
        computedZoomLevel = 1;
        content.setScaleX(computedZoomLevel);
        content.setScaleY(computedZoomLevel);
    }

    public void zoomIn() {
        if (computedZoomLevel >= MIN_SCALE && computedZoomLevel <= MAX_SCALE - SCROLL_DELTA) {
            computedZoomLevel = computedZoomLevel + SCROLL_DELTA;
            setZoomLevel(computedZoomLevel);
        }
    }

    public boolean zoomOut() {

        if (!isScrollVisible()) {
            return false;
        }

        if (computedZoomLevel >= MIN_SCALE + SCROLL_DELTA && computedZoomLevel <= MAX_SCALE) {
            computedZoomLevel = computedZoomLevel - SCROLL_DELTA;
            setZoomLevel(computedZoomLevel);
        }

        return true;
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

    public boolean isScrollVisible() {
        Node vertical = scrollPane.lookup(".scroll-bar:vertical");
        Node horizontal = scrollPane.lookup(".scroll-bar:horizontal");

        if (vertical.isVisible() || horizontal.isVisible()) {
            return true;
        }

        return false;
    }

    public double getZoomLevel() {
        return computedZoomLevel;
    }
}
