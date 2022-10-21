package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.HashMap;

import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public interface GraphBase {

	public enum LayoutStyle {
		HORIZONTAL, HORIZONTAL_TREE, VERTICAL, VERTICAL_TREE, DIRECTED, GRID, HORIZONTAL_SHIFT, RADIAL, SPRING
	}

	public void setCursor (Cursor cursor);
	public void setForeground (Color color);
	public void setBackground (Color color);
	public void setFont(Font font);
	public void setLayout (Layout layout);
	public void setLayoutData (Object layoutData);
	public Control getControl ();
	public void addKeyListener(KeyListener keyListener);
	public void addMouseWheelListener(MouseWheelListener mouseWheelListener);
	
    public Control getGraphModel();
    public Object addNode(String id, String label, HashMap<String, Object> attr, Color color);
    public Object addEdge(String id, String label, String startNodeID, String endNodeID, HashMap<String, String> attr);
    public boolean setHighlight(String nodeID);
    public boolean unHighlight();
    public void setLayoutAlgorithm(LayoutStyle layoutStyle);
    public void clearGraph();
    public void clear();
    public void finalize();
}
