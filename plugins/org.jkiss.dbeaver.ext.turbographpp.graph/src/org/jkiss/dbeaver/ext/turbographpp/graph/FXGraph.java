package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.TouchEvent;
import org.eclipse.swt.events.TouchListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

import javafx.embed.swt.FXCanvas;
import javafx.embed.swt.SWTFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphPanel;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Graph;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.GraphEdgeList;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartPlacementStrategy;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartCircularSortedPlacementStrategy;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphVertex;

public class FXGraph implements GraphBase {
    
    private FXCanvas canvas;
    private Graph<String, String> graph;
    private SmartGraphPanel<String, String> graphView;
    private Control control;
    private Scene scene;
    private ZoomManager zoomManager;
    
    private static javafx.scene.paint.Color backgroundColor = javafx.scene.paint.Color.WHITE;
    
    public FXGraph(Composite parent, int style, int width, int height) {
        
    	System.out.println("FXGraph size width : " + width + " height : " + height);
    	control = parent;
        canvas = new FXCanvas(parent, SWT.NONE);
        graph = new GraphEdgeList<>();
        
        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        graphView = new SmartGraphPanel<>(graph, strategy);
        graphView.setStyle("-fx-background-color: transparent");
        graphView.setAutomaticLayout(true);

        scene = new Scene(graphView, 1024, 768);

        graphView.init();
        
        graphView.setVertexDoubleClickAction((SmartGraphVertex<String> graphVertex) -> {
            System.out.println("Vertex contains element: " + graphVertex.getUnderlyingVertex().element());
            graphVertex.setStyle("-fx-fill: yellow;");
        });

        graphView.setEdgeDoubleClickAction(graphEdge -> {
            System.out.println("Edge contains element: " + graphEdge.getUnderlyingEdge().element());
            graphEdge.setStyle("-fx-stroke: black; -fx-stroke-width: 3;");
            
            graphEdge.getStylableArrow().setStyle("-fx-stroke: black; -fx-stroke-width: 3;");
            
        });
        
        canvas.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				if (graphView != null) {
					graphView.setAutomaticLayout(false);
				}
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
        
        canvas.addTouchListener(new TouchListener() {
			
			@Override
			public void touch(TouchEvent e) {
				// TODO Auto-generated method stub
				System.out.println("addTouchListener touch " + e.toString());
				
			}
		});
        
        canvas.setScene(scene);
        
        zoomManager = new ZoomManager(graphView);
    }

    public void finalize() {
        
    }
    
    @Override
    public Control getGraphModel() {
        return canvas;
    }
    
    public void resize(double width, double height) {
    	System.out.println("graph resize width : " + width + " height : " + height);
    	graphView.setPrefSize(width, height);
    	graphView.update();
    }

    @Override
    public Object addNode(String id, String label, HashMap<String, Object> attr, Color color) {
    	Object v = graph.insertVertex(id);
        return v;
    }

    @Override
    public Object addEdge(String id, String label, String startNodeID, String endNodeID,
            HashMap<String, String> attr) {
    	Object e = graph.insertEdge(startNodeID, endNodeID, id);
        return e;
    }

    @Override
    public boolean setHighlight(String nodeID) {
        return false;
    }

    @Override
    public boolean unHighlight() {
        return false;
    }

    @Override
    public void clearGraph() {
        graph.clearElement();
        graphView.update();
        
    }

    @Override
    public void clear() {
        
    }
    
	@Override
	public void setLayoutAlgorithm(LayoutStyle layoutStyle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCursor(Cursor cursor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setForeground(Color color) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBackground(Color color) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFont(Font font) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLayout(Layout layout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLayoutData(Object layoutData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Control getControl() {
		// TODO Auto-generated method stub
		return control;
	}

	public void addKeyListener(KeyListener keyListener) {
		canvas.addKeyListener(keyListener);
	}

	public void addMouseWheelListener(MouseWheelListener mouseWheelListener) {
		canvas.addMouseWheelListener(mouseWheelListener);
		
	}

	public int getNodes() {
		return graph.numVertices();	
	}
	
	public int getEdges() {
		return graph.numEdges();	
	}

	public ImageData getCaptureImage() {
		if (graphView != null) {
			WritableImage writableImage = new WritableImage((int) graphView.getWidth(), (int) graphView.getHeight());
			graphView.snapshot(null, writableImage);
	        
			return SWTFXUtils.fromFXImage(writableImage, null);
		} 
		return null;
	}
	
	public void setDefaultZoom() {
		//zoomManager.setDefaultZoom();
	}
	
	public void setZoomLevel(int level) {
		//zoomManager.setZoomLevel(level);
	}
	
}

