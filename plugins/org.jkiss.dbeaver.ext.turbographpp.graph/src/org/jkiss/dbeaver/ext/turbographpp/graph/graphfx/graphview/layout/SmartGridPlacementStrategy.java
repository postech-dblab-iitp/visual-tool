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
package org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphPanel;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphVertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.type.DoublePoint;

public class SmartGridPlacementStrategy implements SmartPlacementStrategy {

    private final int START_VALUE = 50;
    private final int INCREMENT_VALUE = 150;

    private DoublePoint startPoint = new DoublePoint(START_VALUE, START_VALUE);
    private DoublePoint endPoint = new DoublePoint(0, 0);

    private double maxWidth = 0;
    private double maxHeight = 0;

    @Override
    public <V, E> void place(
            double width,
            double height,
            SmartGraphPanel<V, E> smartGraphPanel,
            Collection<? extends SmartGraphVertex<V>> vertices) {
        maxWidth = width;
        maxHeight = height;

        boolean first = true;

        for (SmartGraphVertex<V> vertex : sort(vertices)) {
            if (first) {
                endPoint = startPoint;
                first = false;
            } else {
                endPoint.add(INCREMENT_VALUE, 0);
                if (maxWidth < endPoint.getX()) {
                    endPoint.setPoint(START_VALUE, endPoint.getY() + INCREMENT_VALUE);
                }
            }

            if (maxHeight < endPoint.getY()) {
                maxHeight = endPoint.getY() + START_VALUE;
                smartGraphPanel.setMinSize(maxWidth, maxHeight);
                smartGraphPanel.setMaxSize(maxWidth, maxHeight);
            }

            vertex.setPosition(endPoint.getX(), endPoint.getY());
        }
    }

    protected <V> Collection<SmartGraphVertex<V>> sort(
            Collection<? extends SmartGraphVertex<V>> vertices) {

        List<SmartGraphVertex<V>> list = new ArrayList<>();
        list.addAll(vertices);

        Collections.sort(
                list,
                new Comparator<SmartGraphVertex<V>>() {
                    @Override
                    public int compare(SmartGraphVertex<V> t, SmartGraphVertex<V> t1) {
                        return t.getUnderlyingVertex()
                                .element()
                                .toString()
                                .compareToIgnoreCase(t1.getUnderlyingVertex().element().toString());
                    }
                });

        return list;
    }
}
