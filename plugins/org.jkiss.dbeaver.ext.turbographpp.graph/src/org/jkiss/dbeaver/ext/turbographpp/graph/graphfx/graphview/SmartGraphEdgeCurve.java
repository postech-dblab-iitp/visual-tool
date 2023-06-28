/*
 * The MIT License
 *
 * Copyright 2019 brunomnsilva.
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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.QuadCurve;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;

/**
 * Concrete implementation of a curved edge.
 * <br>
 * The edge binds its start point to the <code>outbound</code>
 * {@link SmartGraphVertexNode} center and its end point to the
 * <code>inbound</code> {@link SmartGraphVertexNode} center. As such, the curve
 * is updated automatically as the vertices move.
 * <br>
 * Given there can be several curved edges connecting two vertices, when calling
 * the constructor {@link #SmartGraphEdgeCurve(com.brunomnsilva.smartgraph.graph.FxEdge, 
 * com.brunomnsilva.smartgraph.graphview.SmartGraphVertexNode, 
 * com.brunomnsilva.smartgraph.graphview.SmartGraphVertexNode, int) } the <code>edgeIndex</code>
 * can be specified as to create non-overlaping curves.
 *
 * @param <E> Type stored in the underlying edge
 * @param <V> Type of connecting vertex
 *
 * @author brunomnsilva
 */
public class SmartGraphEdgeCurve<E, V> extends QuadCurve implements SmartGraphEdgeBase<E, V> {

    private static final double FIRST_CURVE_ANGLE_COUNT = 10;
    private static final double MIDDLE_ANGLE = 140;
    private static final double MAX_ANGLE = 159;

    private final FxEdge<E, V> underlyingEdge;

    private final SmartGraphVertexNode<V> inbound;
    private final SmartGraphVertexNode<V> outbound;

    private SmartLabel attachedLabel = null;
    private SmartArrow attachedArrow = null;

    private int angleFactor = 0;
    
    private Point2D lastStartpoint;
    private Point2D lastEndpoint;
    
    /* Styling proxy */
    private final SmartStyleProxy styleProxy;

    public SmartGraphEdgeCurve(FxEdge<E, V> edge, SmartGraphVertexNode inbound, SmartGraphVertexNode outbound) {
        this(edge, inbound, outbound, 0);
    }

    public SmartGraphEdgeCurve(FxEdge<E, V> edge, SmartGraphVertexNode inbound, SmartGraphVertexNode outbound, int edgeIndex) {
        this.inbound = inbound;
        this.outbound = outbound;

        this.underlyingEdge = edge;

        styleProxy = new SmartStyleProxy(this);
        styleProxy.setStyle(SmartStyleProxy.DEFAULT_EDGE);

        //bind start and end positions to vertices centers through properties
        this.startXProperty().bind(outbound.centerXProperty());
        this.startYProperty().bind(outbound.centerYProperty());
        this.endXProperty().bind(inbound.centerXProperty());
        this.endYProperty().bind(inbound.centerYProperty());

        angleFactor = edgeIndex + 1;

        update();
        enableListeners();
    }

    @Override
    public void setStyleClass(String cssClass) {
        styleProxy.setStyleClass(cssClass);
    }

    @Override
    public void addStyleClass(String cssClass) {
        styleProxy.addStyleClass(cssClass);
    }

    @Override
    public boolean removeStyleClass(String cssClass) {
        return styleProxy.removeStyleClass(cssClass);
    }
    
    private void update() {
    	Point2D startpoint = new Point2D(inbound.getCenterX(), inbound.getCenterY());
        Point2D endpoint = new Point2D(outbound.getCenterX(), outbound.getCenterY());
    	
    	if (!checkStartEndNaN()) {
    		lastStartpoint = startpoint;
    		lastEndpoint = endpoint;
    	}
    	
        double angle = 0;
       	angle = getCurveAngle(angleFactor);
        if (angle > MIDDLE_ANGLE) {
        	angle = MIDDLE_ANGLE + (angleFactor - MIDDLE_ANGLE / FIRST_CURVE_ANGLE_COUNT) * 2;
        	if (angle > MAX_ANGLE) {
        		angle = MAX_ANGLE - angleFactor * 0.02;
        	}
        } 
        
        double x1 = lastStartpoint.getX();
        double y1 = lastStartpoint.getY();
        double x2 = lastEndpoint.getX();
        double y2 = lastEndpoint.getY();
        
        double mid_x = (x1 + x2) / 2;
        double mid_y = (y1 + y2) / 2;

        double vec_x = x1 - mid_x;
        double vec_y = y1 - mid_y;

        double rot_vec_x = -vec_y;
        double rot_vec_y = vec_x;

        double length = Math.sqrt(Math.pow(rot_vec_y, 2) + Math.pow(rot_vec_y, 2));
        double base_length = length * Math.tan(Math.toRadians(angle / 2));
        double scale_factor = base_length / length;
        double scaled_vec_x = rot_vec_x * scale_factor;
        double scaled_vec_y = rot_vec_y * scale_factor;

        double x3 = mid_x + scaled_vec_x;
        double y3 = mid_y + scaled_vec_y;
        
        
        if (!Double.isNaN(x3)) {
        	setControlX(x3);
        } 
        if (!Double.isNaN(y3)) {
        	setControlY(y3);
        }
        
        if (attachedLabel != null) {
        	double midX = (this.getStartX() + 2 * this.getControlX() + this.getEndX()) / 4;
        	double midY = (this.getStartY() + 2 * this.getControlY() + this.getEndY()) / 4;
        	
    		DoubleProperty xPropery = new SimpleDoubleProperty(midX);
    		attachedLabel.xProperty().bind(xPropery);
    		
        	DoubleProperty yPropery = new SimpleDoubleProperty(midY);
        	attachedLabel.yProperty().bind(yPropery);
        }
        
        if (attachedLabel != null) {
        	attachedLabel.setRotate(getLineAngle());
        }

    }

    /*
    With a curved edge we need to continuously update the control points.
    TODO: Maybe we can achieve this solely with bindings.
    */
    private void enableListeners() {
        this.startXProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            update();
        });
        this.startYProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            update();
        });
        this.endXProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            update();
        });
        this.endYProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            update();
        });
    }

    @Override
    public void attachLabel(SmartLabel label) {
        this.attachedLabel = label;
        double midX = (this.getStartX() + 2 * this.getControlX() + this.getEndX()) / 4;
    	double midY = (this.getStartY() + 2 * this.getControlY() + this.getEndY()) / 4;
    	DoubleProperty xPropery = new SimpleDoubleProperty(midX);
    	DoubleProperty yPropery = new SimpleDoubleProperty(midY);
    	label.xProperty().bind(xPropery);
    	label.yProperty().bind(yPropery);
    }

    @Override
    public SmartLabel getAttachedLabel() {
        return attachedLabel;
    }

    @Override
    public FxEdge<E, V> getUnderlyingEdge() {
        return underlyingEdge;
    }

    @Override
    public void attachArrow(SmartArrow arrow) {
        this.attachedArrow = arrow;

        /* attach arrow to line's endpoint */
        arrow.translateXProperty().bind(endXProperty());
        arrow.translateYProperty().bind(endYProperty());

        /* rotate arrow around itself based on this line's angle */
        Rotate rotation = new Rotate();
        rotation.pivotXProperty().bind(translateXProperty());
        rotation.pivotYProperty().bind(translateYProperty());
        rotation.angleProperty().bind(UtilitiesBindings.toDegrees(
                UtilitiesBindings.atan2(endYProperty().subtract(controlYProperty()),
                        endXProperty().subtract(controlXProperty()))
        ));

        arrow.getTransforms().add(rotation);

        /* add translation transform to put the arrow touching the circle's bounds */
        Translate t = new Translate(-outbound.getRadius(), 0);
        arrow.getTransforms().add(t);
        
        update();
    }

    @Override
    public SmartArrow getAttachedArrow() {
        return this.attachedArrow;
    }
    
    @Override
    public SmartStylableNode getStylableArrow() {
        return this.attachedArrow;
    }

    @Override
    public SmartStylableNode getStylableLabel() {
        return this.attachedLabel;
    }
    
    @Override
    public synchronized void setTextSize(int size) {
    	String labelStyle = "-fx-font: normal " 
        		+ size 
        		+ "pt \"sans-serif\";";
    	attachedLabel.setStyle(labelStyle);
    }
    
    @Override
    public synchronized void updateLabelText() {
    	attachedLabel.setText(underlyingEdge.element().toString());
    }
    
    @Override
    public synchronized void updateLabelPosition() {
    	DoubleProperty xPropery = new SimpleDoubleProperty(getMidPoint().getX());
    	DoubleProperty yPropery = new SimpleDoubleProperty(getMidPoint().getY());
    	attachedLabel.xProperty().bind(xPropery);
    	attachedLabel.yProperty().bind(yPropery);    
    }
    
    
    private double getLineAngle() {
        double y2y1 = endYProperty().intValue()-startYProperty().intValue();
        double x2x1 = endXProperty().intValue()-startXProperty().intValue();
        double angle = Math.atan(y2y1/x2x1) * (180.0/Math.PI);
        if(x2x1 < 0.0) {
            angle += 360.0;
        } else {
            if(y2y1 < 0.0) {
            	angle += 360.0;
            }
        }
        return angle;
    }
    
    
    private Point2D getMidPoint() {
		double midX = (this.getStartX() + 2 * this.getControlX() + this.getEndX()) / 4;
		double midY = (this.getStartY() + 2 * this.getControlY() + this.getEndY()) / 4;
		 
		Point2D midPoint = new Point2D(midX, midY);
		 
		return midPoint;
    }
    
    private double getCurveAngle(int angleFactor) {
    	int remain = angleFactor;
    	int factor = 0;
    	int minusCount = 0;
    	int n = 3;
    	double result = 0;
    	while (remain != 0) {
    		if (remain / n == 0) {
    			factor = remain % n;
    		} else {
    			factor = n;
    		}
    		remain = remain - factor;
    		result = result + (FIRST_CURVE_ANGLE_COUNT - minusCount) * factor;
    		minusCount++;
    		
    		if (result > MIDDLE_ANGLE) {
        		result = MIDDLE_ANGLE + (remain);
        		remain = 0;
            }
    		
    		if (result > MAX_ANGLE) {
        		result = MAX_ANGLE - remain * 0.02;
        		remain = 0;
        	}
    		
    	}
    	
    	return result;
    }
    
    private boolean checkStartEndNaN() {
    	if (Double.isNaN(inbound.getCenterX())) {
    		return true;
    	}
    	
    	if (Double.isNaN(inbound.getCenterY())) {
    		return true;
    	}
    	
    	if (Double.isNaN(outbound.getCenterX())) {
    		return true;
    	}
    	
    	if (Double.isNaN(outbound.getCenterY())) {
    		return true;
    	}
    	
    	return false;
    }
   
}
