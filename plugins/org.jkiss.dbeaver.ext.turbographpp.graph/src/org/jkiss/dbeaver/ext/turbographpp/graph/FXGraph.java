package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
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
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;

import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphPanel;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Graph;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.TurboGraphEdgeList;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartPlacementStrategy;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartRandomPlacementStrategy;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphVertex;

public class FXGraph implements GraphBase {
    
	public static final int MOUSE_WHELL_UP = 5;
	public static final int MOUSE_WHELL_DOWN = -5;
	
	public static final int ZOOM_MIN = 50;
	public static final int ZOOM_MAX = 500;
	
	public static final int CTRL_KEYCODE = 0x40000;
	
    private FXCanvas canvas;
    private Graph<CyperNode, CyperEdge> graph;
    private SmartGraphPanel<CyperNode, CyperEdge> graphView;
    ScrollPane scrollPane;
    private Control control;
    private Scene scene;
    
    private boolean zoomMode = false;
    private ZoomManager zoomManager;
    
    private HashMap<String, CyperNode> Nodes = new HashMap<>();
    private HashMap<String, CyperEdge> Edges = new HashMap<>();
    
    private HashMap<String, String> nodesGroup = new HashMap<>();
    
    private Consumer<String> nodeIDConsumer = null;
    private Consumer<String> edgeIDConsumer = null;
    
    private boolean autoLayout = true;
    
    private LayoutUpdateThread layoutUpdatethread;
    
    private static javafx.scene.paint.Color backgroundColor = javafx.scene.paint.Color.WHITE;
    
    public FXGraph(Composite parent, int style, int width, int height) {
        
    	control = parent;
        canvas = new FXCanvas(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).span(3, 1).applyTo(canvas);
        graph = new TurboGraphEdgeList<>();
        
        SmartPlacementStrategy strategy = new SmartRandomPlacementStrategy();
        
        graphView = new SmartGraphPanel<>(graph, strategy);
        
    	scrollPane = new ScrollPane();
    	scrollPane.setContent(graphView);
    	scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    	scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-focus-color: transparent;");
        
        scene = new Scene(scrollPane, 1024, 768);

        setCanvasListener();
        setGraphViewListener();
        setBaseListener();
        
        canvas.computeSize(1024, 768);
        canvas.setScene(scene);
        
        zoomManager = new ZoomManager(graphView, scrollPane);
        
    }

    private void setCanvasListener() {
    	canvas.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				if (graphView != null) {
					graphView.setAutomaticLayout(false);
				}
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
        
        canvas.addTouchListener(new TouchListener() {
			
			@Override
			public void touch(TouchEvent e) {
				if (graphView != null) {
					graphView.setAutomaticLayout(false);
				}
			}
		});
    }
    
    private void setGraphViewListener() {
    	graphView.setVertexDoubleClickAction((SmartGraphVertex<CyperNode> graphVertex) -> {
            System.out.println("Vertex contains element: " + graphVertex.getUnderlyingVertex().element());
            graphVertex.setStyle("-fx-fill: yellow;");
        });

        graphView.setEdgeDoubleClickAction(graphEdge -> {
            System.out.println("Edge contains element: " + graphEdge.getUnderlyingEdge().element());
            graphEdge.setStyle("-fx-stroke: black; -fx-stroke-width: 3;");
            
            graphEdge.getStylableArrow().setStyle("-fx-stroke: black; -fx-stroke-width: 3;");
            
        });
        
        graphView.setVertexSelectAction((SmartGraphVertex<CyperNode> graphVertex) -> {
            if (graphVertex.getUnderlyingVertex().element() instanceof CyperNode) {
            	CyperNode node = (CyperNode) graphVertex.getUnderlyingVertex().element();
            	String ID = node.getID();
            	if (nodeIDConsumer == null ) {
                    return;
                }
            	nodeIDConsumer.accept(ID);
            }
        });

        graphView.setEdgeSelectAction(graphEdge -> {
            if (graphEdge.getUnderlyingEdge().element() instanceof CyperEdge) {
            	CyperEdge edge = (CyperEdge) graphEdge.getUnderlyingEdge().element();
            	String ID = edge.getID();
            	if (edgeIDConsumer == null ) {
                    return;
                }
            	edgeIDConsumer.accept(ID);
            }
        });
    }
    
    private void setBaseListener() {
        this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == CTRL_KEYCODE) {
					zoomMode = false;
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == CTRL_KEYCODE) {
					zoomMode = true;
				}
			}
		});
		
		this.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseScrolled(MouseEvent e) {
				if (zoomMode) {
					if (e != null) {
						if (e.count == MOUSE_WHELL_UP) {
							zoomOut();
						} else if (e.count == MOUSE_WHELL_DOWN){
							zoomIn();
						}
						
					} 
				}
			}
		});
    }
    
    public void finalize() {
        
    }
    
    @Override
    public Control getGraphModel() {
        return canvas;
    }
    
    public void resize(double width, double height) {
    	graphView.setMinSize(width, height);
    	graphView.setMaxSize(width, height);
    }
    
    private void graphInit() {
    	zoomManager.setDefaultZoom();
    	graphView.init();
    	graphView.update();
    	layoutUpdatethread = new LayoutUpdateThread();
    	layoutUpdatethread.start();
    }
    
    public void drawGraph(double width, double height) {
    	resize(width, height);
    	graphInit();
    }

    @Override
    public Object addNode(String id, String label, HashMap<String, Object> attr, Color color) {
    	//For Group Color
    	String fillColor = nodesGroup.get(label);
    	if (nodesGroup.get(label) == null) {
    		fillColor = ramdomColor();
    		nodesGroup.put(label, ramdomColor());
    	}
    	
    	CyperNode node = new CyperNode(id, label, attr, fillColor);
    	Nodes.put(id, node);
    	Object v = graph.insertVertex(node);
        return v;
    }

    @Override
    public Object addEdge(String id, String label, String startNodeID, String endNodeID,
            HashMap<String, String> attr) {
    	//System.out.println("addEdge id : " + id + " startNodeID : " + startNodeID + " endNodeID : " + endNodeID);
    	CyperEdge edge = new CyperEdge(id, label, attr, startNodeID, endNodeID);
    	Edges.put(id, edge);
    	Object e = graph.insertEdge(Nodes.get(startNodeID), Nodes.get(endNodeID), edge);
        return e;
    }

    public void updateNodeLabel(String label) {
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
		String rgb = Integer.toHexString(color.getRed())
		+ Integer.toHexString(color.getGreen())
		+ Integer.toHexString(color.getBlue());
	}

	@Override
	public void setBackground(Color color) {
		String rgb = Integer.toHexString(color.getRed())
				+ Integer.toHexString(color.getGreen())
				+ Integer.toHexString(color.getBlue());
		graphView.setStyle("-fx-background-color: #" + rgb);
		scrollPane.setStyle("-fx-background-color: #" + rgb);
	}

	@Override
	public void setFont(Font font) {
		
	}

	@Override
	public void setLayout(Layout layout) {
		
	}

	@Override
	public void setLayoutData(Object layoutData) {
		
	}

	@Override
	public Control getControl() {
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
			Bounds bounds = graphView.localToScene(graphView.getBoundsInLocal());
			WritableImage writableImage = new WritableImage((int) bounds.getWidth(), (int) bounds.getHeight());
			graphView.snapshot(null, writableImage);
			return SWTFXUtils.fromFXImage(writableImage, null);
		} 
		return null;
	}
	
	public void setDefaultZoom() {
		zoomManager.setDefaultZoom();
	}
	
	public void setZoomLevel(int level) {
		//zoomManager.setZoomLevel(level);
	}
	
	public void zoomIn() {
		zoomManager.zoomIn();
	}
	
	public void zoomOut() {
		zoomManager.zoomOut();
	}
	
	
	class LayoutUpdateThread extends Thread {
		@Override
		public void run() {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			graphView.setAutomaticLayout(true);
		}
	}
	
	
	private String ramdomColor() {
		int r, g, b;
		Random random = new Random();
		r = random.nextInt(216) + 40;
		g = random.nextInt(216) + 40;
		b = random.nextInt(216) + 40;

		return "-fx-fill: #" + Integer.toHexString(r) + Integer.toHexString(g) +  Integer.toHexString(b);
	}
	
	public void setVertexSelectAction(Consumer<String> action) {
        this.nodeIDConsumer = action;
    }
	
	public void setEdgeSelectAction(Consumer<String> action) {
        this.edgeIDConsumer = action;
    }
}

