/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2024 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    public DoublePoint add(DoublePoint p) {
        this.x = this.x + p.x;
        this.y = this.y + p.y;
        return this;
    }

    public DoublePoint setX(double x) {
        this.x = x;
        return this;
    }

    public DoublePoint addX(double x) {
        this.x = this.x + x;
        return this;
    }

    public DoublePoint setY(double y) {
        this.y = y;
        return this;
    }

    public DoublePoint addY(double y) {
        this.y = this.y + y;
        return this;
    }
}
