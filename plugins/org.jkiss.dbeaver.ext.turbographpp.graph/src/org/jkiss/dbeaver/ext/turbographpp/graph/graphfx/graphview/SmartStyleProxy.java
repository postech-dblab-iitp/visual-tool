/*
 * The MIT License
 *
 * Copyright 2020 rayjasson98
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

import javafx.scene.shape.Shape;

/**
 * This class acts as a proxy for styling of nodes.
 *
 * <p>It essentially groups all the logic, avoiding code duplicate.
 *
 * <p>Classes that have this behavior can delegate the method calls to an instance of this class.
 *
 * @author brunomnsilva
 */
public class SmartStyleProxy implements SmartStylableNode {

    public static final String DEFAULT_VERTEX = "-fx-stroke-type: inside;";

    public static final String HIGHLIGHT_VERTEX =
            "-fx-stroke-width: 9;" + "    -fx-stroke: #FF0000;" + "    -fx-stroke-type: inside;";

    public static final String DEFAULT_EDGE_LINE_WEIGHT = "3";
    public static final String DEFAULT_EDGE_LINE_COLOR = "000000";
    public static final String DEFAULT_EDGE_LINE_STYLE = "4 4 4 4";
    public static final String DEFAULT_EDGE_LINE_STRENGTH = "0.2";

    public static final String DEFAULT_EDGE =
            "-fx-stroke-width: "
                    + DEFAULT_EDGE_LINE_WEIGHT
                    + ";"
                    + " -fx-stroke: #"
                    + DEFAULT_EDGE_LINE_COLOR
                    + ";"
                    + " -fx-stroke-dash-array: "
                    + DEFAULT_EDGE_LINE_STYLE
                    + ";"
                    + " -fx-fill: transparent;"
                    + " -fx-stroke-line-cap: round;"
                    + " -fx-opacity: "
                    + DEFAULT_EDGE_LINE_STRENGTH
                    + ";";

    public static final String HIGHLIGHT_EDGE =
            "-fx-stroke-width: 5;"
                    + " -fx-stroke: #FF6D66;"
                    + " -fx-stroke-dash-array: 4 4 4 4;"
                    + " -fx-fill: transparent;"
                    + " -fx-stroke-line-cap: round;"
                    + " -fx-opacity: 1.0;";

    public static final int DEFAULT_VERTEX_LABEL_SIZE = 8;
    public static final String DEFAULT_VERTEX_LABEL =
            "-fx-font: bold " + DEFAULT_VERTEX_LABEL_SIZE + "pt \"sans-serif\";";

    public static final int DEFAULT_EDGE_LABEL_SIZE = 6;
    public static final String DEFAULT_EDGE_LABEL =
            "-fx-font: normal " + DEFAULT_EDGE_LABEL_SIZE + "pt \"sans-serif\";";

    private final Shape client;

    public SmartStyleProxy(Shape client) {
        this.client = client;
    }

    @Override
    public void setStyle(String css) {
        client.setStyle(css);
    }

    @Override
    public void setStyleClass(String cssClass) {
        client.getStyleClass().clear();
        client.setStyle(null);
        client.getStyleClass().add(cssClass);
    }

    @Override
    public void addStyleClass(String cssClass) {
        client.getStyleClass().add(cssClass);
    }

    @Override
    public boolean removeStyleClass(String cssClass) {
        return client.getStyleClass().remove(cssClass);
    }

    public static String getEdgeStyleInputValue(String Color, String style, String weight) {
        return "-fx-stroke-width: "
                + DEFAULT_EDGE_LINE_WEIGHT
                + ";"
                + " -fx-stroke: #"
                + Color
                + ";"
                + " -fx-stroke-dash-array: "
                + style
                + ";"
                + " -fx-fill: transparent;"
                + " -fx-stroke-line-cap: round;"
                + " -fx-opacity: "
                + weight
                + ";";
    }
}
