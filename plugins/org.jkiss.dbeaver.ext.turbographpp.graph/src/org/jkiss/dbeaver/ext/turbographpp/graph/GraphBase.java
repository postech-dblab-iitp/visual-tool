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
package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.ui.UIIcon;

public interface GraphBase {

    public enum LayoutStyle {
        HORIZONTAL_TREE(
                DBeaverIcons.getImage(UIIcon.BUTTON_LAYOUT_TREE_HORIZONTAL),
                "Horizontal-Tree Layout"),
        VERTICAL_TREE(
                DBeaverIcons.getImage(UIIcon.BUTTON_LAYOUT_TREE_VERTICAL), "Vertical-Tree Layout"),
        GRID(DBeaverIcons.getImage(UIIcon.BUTTON_LAYOUT_GRID), "Grid Layout"),
        RADIAL(DBeaverIcons.getImage(UIIcon.BUTTON_LAYOUT_CIRCLE), "Circle(Radial) Layout"),
        SPRING(DBeaverIcons.getImage(UIIcon.BUTTON_LAYOUT_FORCE_DIRECTED), "Spring Layout");

        LayoutStyle(Image image, String text) {
            this.image = image;
            this.text = text;
        }

        private Image image;
        private String text;

        public Image getImage() {
            return image;
        }

        public String getText() {
            return text;
        }
    }

    public void setCursor(Cursor cursor);

    public void setForeground(Color color);

    public void setBackground(Color color);

    public void setFont(Font font);

    public void setLayout(Layout layout);

    public void setLayoutData(Object layoutData);

    public Control getControl();

    public void addKeyListener(KeyListener keyListener);

    public void addMouseWheelListener(MouseWheelListener mouseWheelListener);

    public Control getGraphModel();

    public Object addNode(String id, List<String> label, LinkedHashMap<String, Object> attr);

    public Object addEdge(
            String id,
            List<String> type,
            String startNodeID,
            String endNodeID,
            LinkedHashMap<String, Object> attr);

    public boolean setHighlight(String nodeID);

    public boolean unHighlight();

    public void setLayoutAlgorithm(LayoutStyle layoutStyle);

    public void clearGraph();

    public void clear();

    public void finalize();

    public void setVertexSelectAction(Consumer<String> action);

    public void setEdgeSelectAction(Consumer<String> action);

    public void setDefaultLayoutAlgorithm();

    public void drawGraph(boolean refreshMetadata, double width, double height);

    public int getNumNodes();

    public int getNumEdges();
}
