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

import javafx.beans.value.ObservableValue;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;


/**
 * Implementation of a straight line edge.
 * 
 * @param <E> Type stored in the underlying edge
 * @param <V> Type of connecting vertex
 * 
 * @author brunomnsilva
 */
public class SmartGraphEdgeLine<E, V> extends Line implements SmartGraphEdgeBase<E, V> {
    
    private final FxEdge<E, V> underlyingEdge;
    
    private final SmartGraphVertexNode inbound;
    private final SmartGraphVertexNode outbound;
    
    private SmartLabel attachedLabel = null;
    private SmartArrow attachedArrow = null;
    
    /* Styling proxy */
    private final SmartStyleProxy styleProxy;
    
    public SmartGraphEdgeLine(FxEdge<E, V> edge, SmartGraphVertexNode inbound, SmartGraphVertexNode outbound) {
        if( inbound == null || outbound == null) {
            throw new IllegalArgumentException("Cannot connect null vertices.");
        }
        
        this.inbound = inbound;
        this.outbound = outbound;
        
        this.underlyingEdge = edge;
        
        styleProxy = new SmartStyleProxy(this);
        //styleProxy.addStyleClass("edge");
        styleProxy.setStyle(SmartStyleProxy.DEFAULT_EDGE);
        
        //bind start and end positions to vertices centers through properties
        this.startXProperty().bind(outbound.centerXProperty());
        this.startYProperty().bind(outbound.centerYProperty());
        this.endXProperty().bind(inbound.centerXProperty());
        this.endYProperty().bind(inbound.centerYProperty());
        
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
    

    @Override
    public void attachLabel(SmartLabel label) {
        this.attachedLabel = label;
        label.xProperty().bind(startXProperty().add(endXProperty()).divide(2).subtract(label.getLayoutBounds().getWidth() / 2));
        label.yProperty().bind(startYProperty().add(endYProperty()).divide(2).add(label.getLayoutBounds().getHeight() / 1.5));  
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
        rotation.angleProperty().bind( UtilitiesBindings.toDegrees( 
                UtilitiesBindings.atan2( endYProperty().subtract(startYProperty()), 
                endXProperty().subtract(startXProperty()))
        ));
        
        arrow.getTransforms().add(rotation);
        
        /* add translation transform to put the arrow touching the circle's bounds */
        Translate t = new Translate(- outbound.getRadius(), 0);
        arrow.getTransforms().add(t);
        
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
    	attachedLabel.xProperty().bind(startXProperty().add(endXProperty()).divide(2).subtract(attachedLabel.getLayoutBounds().getWidth() / 2));
    	attachedLabel.yProperty().bind(startYProperty().add(endYProperty()).divide(2).add(attachedLabel.getLayoutBounds().getHeight() / 1.5));
    }
    
    private void update() {
    	attachedLabel.setRotate(getAngle());
    }
    
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
 
    private double getAngle() {
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
}
