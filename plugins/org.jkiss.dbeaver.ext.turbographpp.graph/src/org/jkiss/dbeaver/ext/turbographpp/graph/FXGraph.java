package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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

import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.embed.swt.SWTFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ContextMenuEvent;

import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphPanel;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Graph;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.TurboGraphEdgeList;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartPlacementStrategy;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartRandomPlacementStrategy;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphVertex;

public class FXGraph implements GraphBase {
    
	public static final int MOUSE_WHELL_UP = 5;
	public static final int MOUSE_WHELL_DOWN = -5;
	
	public static final int MOUSE_SECONDARY_BUTTON = 3;
	
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
    
    private LayoutUpdateThread layoutUpdatethread;
    
    private ContextMenu contextMenu;
    private MenuItem redoMenu;
    private MenuItem undoMenu;
    private MenuItem highlightMenu;
    private MenuItem unHighlightMenu;
    private MenuItem deteleMenu;
    private MenuItem shortestPathAction;
    
    private SmartGraphVertex<CyperNode> selectNode = null;
    
    private boolean statusCanvasFocus = false;
    
    public FXGraph(Composite parent, int style, int width, int height) {
        
        control = parent;
        //this default option are true then fx thread issue when changed Presentation.
        Platform.setImplicitExit(false);

        canvas = new FXCanvas(parent, SWT.NONE);
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

        zoomManager = new ZoomManager(graphView, scrollPane);
        
        registerContextMenu();
        
        setCanvasListener();
        setGraphViewListener();
        setBaseListener();
        
        canvas.computeSize(1024, 768);
        canvas.setScene(scene);
    }

    private void setCanvasListener() {
    	
    	canvas.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				statusCanvasFocus = false;
				hideContextMenu();
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				statusCanvasFocus = true;
			}
		});
    	
    	canvas.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				if (graphView != null) {
					graphView.setAutomaticLayout(false);
				}
				
				hideContextMenu();
				
		        if (e.button != MOUSE_SECONDARY_BUTTON) {
		        	clearSelectNode();
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
    		if (selectNode != null) {
        		graphView.doDefaultVertexStyle(selectNode);
        	} else {
        		setUnhighlight();
        	}
    		
    		selectNode = graphVertex; 
    		graphView.doHighlightVertexStyle(graphVertex);
        });

        graphView.setEdgeDoubleClickAction(graphEdge -> {
            
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
        graphView.clear();
        
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
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			graphView.setAutomaticLayout(true);
		}
	}
	
	
	private String ramdomColor() {
		int r, g, b;
		Random random = new Random();
		r = random.nextInt(170) + 40;
		g = random.nextInt(170) + 40;
		b = random.nextInt(170) + 40;

		return "-fx-fill: #" + Integer.toHexString(r) + Integer.toHexString(g) +  Integer.toHexString(b);
	}
	
	public void setVertexSelectAction(Consumer<String> action) {
        this.nodeIDConsumer = action;
    }
	
	public void setEdgeSelectAction(Consumer<String> action) {
        this.edgeIDConsumer = action;
    }
	
	protected void registerContextMenu() {
		contextMenu = new ContextMenu();
		redoMenu = new MenuItem("Redo");
		undoMenu = new MenuItem("Undo");
		highlightMenu = new MenuItem("Highlight");
		unHighlightMenu = new MenuItem("unHighlight");
		deteleMenu = new MenuItem("Delete");
		shortestPathAction = new MenuItem("Shortest Path");
		
		redoMenu.setDisable(true);
		undoMenu.setDisable(true);
		highlightMenu.setDisable(true);
		deteleMenu.setDisable(true);
		unHighlightMenu.setDisable(true);
		shortestPathAction.setDisable(true);
		
		contextMenu.getItems().addAll(redoMenu, undoMenu, highlightMenu, unHighlightMenu, deteleMenu, shortestPathAction);
		
		contextMenuAction();
		
		graphView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>()
		{
		  @Override
		  public void handle(ContextMenuEvent event)
		  {
			if (selectNode != null && !graphView.isHighlighted()) {
				highlightMenu.setDisable(false);
			} else {
				highlightMenu.setDisable(true);
			}
			
			if (graphView.isHighlighted()) {
				unHighlightMenu.setDisable(false);
			} else {
				unHighlightMenu.setDisable(true);
			}
			  
			if (statusCanvasFocus) {
				contextMenu.show(graphView, event.getScreenX(), event.getScreenY());
			}
		  }
		});
	}
	
	private void contextMenuAction() {
		//ContextMenu
		highlightMenu.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		    	setHighlight();
		    }
		});
		
		unHighlightMenu.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		    	setUnhighlight();
		    }
		});
	}
	
	private void hideContextMenu() {
		if (contextMenu != null) {
			contextMenu.hide();	
		}
		
	}
	
	private void clearSelectNode() {
		if (selectNode != null && !graphView.isHighlighted()) {
        	graphView.doDefaultVertexStyle(selectNode);
    	}
		
		selectNode = null;
	}
	
	private void setHighlight() {
		if (selectNode != null) {
        	graphView.setHighlight(selectNode.getUnderlyingVertex());
        }
	}
	
	private void setUnhighlight() {
		graphView.setUnHighlight();
   		selectNode = null;
	}
}

