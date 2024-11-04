/*
 * The MIT License
 *
 * Copyright 2019 brunomnsilva@gmail.com.
 * Copyright (C) 2024 CUBRID Corporation.
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
import javafx.scene.CacheHint;
import javafx.scene.shape.CubicCurve;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;

/**
 * Concrete implementation of a curved edge. <br>
 * The edge binds its start point to the <code>outbound</code> {@link SmartGraphVertexNode} center
 * and its end point to the <code>inbound</code> {@link SmartGraphVertexNode} center. As such, the
 * curve is updated automatically as the vertices move. <br>
 * Given there can be several curved edges connecting two vertices, when calling the constructor
 * {@link #SmartGraphEdgeCurve(com.brunomnsilva.smartgraph.graph.FxEdge,
 * com.brunomnsilva.smartgraph.graphview.SmartGraphVertexNode,
 * com.brunomnsilva.smartgraph.graphview.SmartGraphVertexNode, int) } the <code>edgeIndex</code> can
 * be specified as to create non-overlaping curves.
 *
 * @param <E> Type stored in the underlying edge
 * @param <V> Type of connecting vertex
 * @author brunomnsilva
 */
public class SmartGraphEdgeCurveSelf<E, V> extends CubicCurve implements SmartGraphEdgeBase<E, V> {

    private final FxEdge<E, V> underlyingEdge;

    private final SmartGraphVertexNode<V> inbound;
    private final SmartGraphVertexNode<V> outbound;

    private SmartLabel attachedLabel = null;
    private SmartArrow attachedArrow = null;

    private double angleFactor = 0;

    /* Styling proxy */
    private final SmartStyleProxy styleProxy;

    public SmartGraphEdgeCurveSelf(
            FxEdge<E, V> edge, SmartGraphVertexNode inbound, SmartGraphVertexNode outbound) {
        this(edge, inbound, outbound, 0);
    }

    public SmartGraphEdgeCurveSelf(
            FxEdge<E, V> edge,
            SmartGraphVertexNode inbound,
            SmartGraphVertexNode outbound,
            int edgeIndex) {
        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);
        this.inbound = inbound;
        this.outbound = outbound;

        this.underlyingEdge = edge;

        styleProxy = new SmartStyleProxy(this);
        // styleProxy.addStyleClass("edge");
        styleProxy.setStyle(SmartStyleProxy.DEFAULT_EDGE);

        // bind start and end positions to vertices centers through properties
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
        double midpointX1 = outbound.getCenterX();
        double midpointY1 = outbound.getCenterY();
        double midpointX2 = outbound.getCenterX();
        double midpointY2 = outbound.getCenterY();

        if (angleFactor % 4 == 0) {
            midpointX1 = midpointX1 - inbound.getRadius() * (angleFactor / 4 + 4);
            midpointY1 = midpointY1 - inbound.getRadius() * (angleFactor / 4 + 1);

            midpointX2 = midpointX2 + inbound.getRadius() * (angleFactor / 4 + 1);
            midpointY2 = midpointY2 - inbound.getRadius() * (angleFactor / 4 + 4);
        } else if (angleFactor % 4 == 1) {
            midpointX1 = midpointX1 + inbound.getRadius() * (angleFactor / 4 + 4);
            midpointY1 = midpointY1 + inbound.getRadius() * (angleFactor / 4 + 1);

            midpointX2 = midpointX2 - inbound.getRadius() * (angleFactor / 4 + 1);
            midpointY2 = midpointY2 + inbound.getRadius() * (angleFactor / 4 + 4);
        } else if (angleFactor % 4 == 2) {
            midpointX1 = midpointX1 + inbound.getRadius() * (angleFactor / 4 + 4);
            midpointY1 = midpointY1 + inbound.getRadius() * (angleFactor / 4 + 1);
            midpointX2 = midpointX2 + inbound.getRadius() * (angleFactor / 4 + 1);
            midpointY2 = midpointY2 - inbound.getRadius() * (angleFactor / 4 + 4);
        } else {
            midpointX1 = midpointX1 - inbound.getRadius() * (angleFactor / 4 + 4);
            midpointY1 = midpointY1 - inbound.getRadius() * (angleFactor / 4 + 1);

            midpointX2 = midpointX2 - inbound.getRadius() * (angleFactor / 4 + 1);
            midpointY2 = midpointY2 + inbound.getRadius() * (angleFactor / 4 + 4);
        }

        setControlX1(midpointX1);
        setControlY1(midpointY1);
        setControlX2(midpointX2);
        setControlY2(midpointY2);
    }

    /*
    With a curved edge we need to continuously update the control points.
    TODO: Maybe we can achieve this solely with bindings.
    */
    private void enableListeners() {
        this.startXProperty()
                .addListener(
                        (ObservableValue<? extends Number> ov, Number t, Number t1) -> {
                            update();
                        });
        this.startYProperty()
                .addListener(
                        (ObservableValue<? extends Number> ov, Number t, Number t1) -> {
                            update();
                        });
        this.endXProperty()
                .addListener(
                        (ObservableValue<? extends Number> ov, Number t, Number t1) -> {
                            update();
                        });
        this.endYProperty()
                .addListener(
                        (ObservableValue<? extends Number> ov, Number t, Number t1) -> {
                            update();
                        });
    }

    @Override
    public void attachLabel(SmartLabel label) {
        this.attachedLabel = label;
        attachedLabel
                .xProperty()
                .bind(
                        startXProperty()
                                .add(controlX1Property().multiply(3))
                                .add(controlX2Property().multiply(3))
                                .add(endXProperty())
                                .divide(8)
                                .subtract(16));
        attachedLabel
                .yProperty()
                .bind(
                        startYProperty()
                                .add(controlY1Property().multiply(3))
                                .add(controlY2Property().multiply(3))
                                .add(endYProperty())
                                .divide(8));

        if (angleFactor % 4 == 2) {
            attachedLabel.setRotate(90);
        } else if (angleFactor % 4 == 3) {
            attachedLabel.setRotate(90);
        }
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
        rotation.angleProperty()
                .bind(
                        UtilitiesBindings.toDegrees(
                                UtilitiesBindings.atan2(
                                        endYProperty().subtract(controlY2Property()),
                                        endXProperty().subtract(controlX2Property()))));

        arrow.getTransforms().add(rotation);

        /* add translation transform to put the arrow touching the circle's bounds */
        Translate t = new Translate(-outbound.getRadius(), 0);
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
        String labelStyle = "-fx-font: normal " + size + "pt \"sans-serif\";";
        attachedLabel.setStyle(labelStyle);
    }

    @Override
    public synchronized void updateLabelText() {
        attachedLabel.setText(underlyingEdge.element().toString());
    }

    @Override
    public synchronized void updateLabelPosition() {}

    @Override
    public synchronized void updateArrowPosition() {
        attachedArrow.getTransforms().remove(attachedArrow.getTransforms().size() - 1);
        Translate t = new Translate(-outbound.getRadius(), 0);
        attachedArrow.getTransforms().add(t);
    }
}
