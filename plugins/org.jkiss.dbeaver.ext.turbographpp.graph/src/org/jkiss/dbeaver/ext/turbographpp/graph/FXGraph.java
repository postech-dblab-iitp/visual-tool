package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.time.LocalTime;
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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TouchEvent;
import org.eclipse.swt.events.TouchListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;

import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.embed.swt.SWTFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.VBox;

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

	public static final int CTRL_KEYCODE = 0x40000;
	
    private FXCanvas canvas;
    private Graph<CyperNode, CyperEdge> graph;
    private SmartGraphPanel<CyperNode, CyperEdge> graphView;
    Group graphGroup;
    private ScrollPane scrollPane;
    private VBox vBox;
    
    private Control control;
    private Scene scene;
    
    private MiniMap miniMap;
    
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
    
    private double lastWidth = 0;
    private double lastHeight = 0;
    
    private double lastViewportWidth = 0;
    private double lastViewportHeight = 0;
    
    public FXGraph(Composite parent, int style) {
        control = parent;
        //this default option are true then fx thread issue when changed Presentation.
        Platform.setImplicitExit(false);

        canvas = new FXCanvas(parent, SWT.NONE);

        graph = new TurboGraphEdgeList<>();
        
        SmartPlacementStrategy strategy = new SmartRandomPlacementStrategy();
        
        graphView = new SmartGraphPanel<>(graph, strategy);
        
        graphGroup = new Group();
        graphGroup.getChildren().addAll(graphView);
        
        vBox = new VBox();
        vBox.getChildren().addAll(graphGroup);
        
        scrollPane = new ScrollPane();
        scrollPane.setContent(vBox);

        scene = new Scene(scrollPane);

        zoomManager = new ZoomManager(graphView, scrollPane);
        
        registerContextMenu();
        
        setCanvasListener();
        setGraphViewListener();
        setBaseListener();
        
        canvas.setScene(scene);
        
        createMiniMap(parent);
        
        canvas.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				Double hValue, vValue;
				
				lastViewportWidth = scrollPane.getViewportBounds().getWidth();
				lastViewportHeight = scrollPane.getViewportBounds().getHeight();
				lastWidth =  graphView.getBoundsInParent().getWidth();
				lastHeight = graphView.getBoundsInParent().getHeight();
				hValue = scrollPane.getHvalue();
				vValue = scrollPane.getVvalue();
			
				miniMap.setPointRectAngel(lastWidth, lastHeight, lastViewportWidth, lastViewportHeight, vValue, hValue);
			}
		}); 
        
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
				setAutomaticLayout(false);
				
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
				setAutomaticLayout(false);
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
    		
    		if (graphVertex.getUnderlyingVertex().element() instanceof CyperNode) {
            	CyperNode node = (CyperNode) graphVertex.getUnderlyingVertex().element();
            	String ID = node.getID();
            	if (nodeIDConsumer == null ) {
                    return;
                }
            	nodeIDConsumer.accept(ID);
           }
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
        
        graphView.setVertexMovedAction(event -> {
        	miniMapUpdate();
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
    	
    	lastWidth = width;
    	lastHeight = height;
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
		graphView.setStyle("-fx-background-color: #" + rgb + "; -fx-background: #" + rgb);
		vBox.setStyle("-fx-background-color: #" + rgb + "; -fx-background: #" + rgb);
		scrollPane.setStyle("-fx-background-color: #" + rgb + "; -fx-background: #" + rgb);
		canvas.setBackground(color);
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
		int ret = graph.numVertices();
		if (ret <= 0) {
			return 0;
		}
		return ret;	
	}
	
	public int getEdges() {
		int ret = graph.numEdges();
		if (ret <= 0) {
			return 0;
		}
		return ret;	
	}

	public ImageData getCaptureImage() {
		WritableImage writableImage = graphView.snapshot(new SnapshotParameters(), null);
		if (writableImage != null) {
			return SWTFXUtils.fromFXImage(writableImage, null);
		}
//		if (graphView != null) {
//			Bounds bounds = graphView.localToScene(graphView.getBoundsInLocal());
//			//WritableImage writableImage = graphView.snapshot(new SnapshotParameters(), null);
//			WritableImage writableImage = new WritableImage((int) bounds.getWidth(), (int) bounds.getHeight());
//			graphView.snapshot(null, writableImage);
//			return SWTFXUtils.fromFXImage(writableImage, null);
//		} 
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
	
	
	private class LayoutUpdateThread extends Thread {
		@Override
		public void run() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			setAutomaticLayout(true);
			
			if (miniMap.isShowing()) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				setAutomaticLayout(false);
				miniMapUpdate();
			}
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
	
	public void setAutomaticLayout (boolean value) {
		if (graphView != null) {
			graphView.setAutomaticLayout(value);
		}
	}
	
	private void createMiniMap(Composite parent) {
        miniMap = new MiniMap(parent);
        miniMap.addZoominListner(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
       			zoomIn();
            }

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
            
        });
        
        miniMap.addZoomOutListner(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
       			zoomOut();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        
        miniMap.addCavasMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				MoveScrollBar(e.x / (double)MiniMap.MINIMAP_WIDTH, e.y / (double)MiniMap.MINIMAP_HEIGHT);
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
    }
	
	public void setMiniMapVisible(boolean visible) {
        if (miniMap != null) {
            if (visible) {
                miniMap.show();
                setAutomaticLayout(false);
                miniMapUpdate();
                
            } else {
                miniMap.remove();
            }
       }
	}
	
	public void miniMapUpdate () {
    	Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (miniMap != null && miniMap.isShowing()) {
                    miniMap.setImage(graphImageCapture());
                    miniMap.reDraw();
                }
            }
        });
    }
	
	private Image graphImageCapture() {
		System.out.println(LocalTime.now());
		setGraphScaleForCapture();
		ImageData imageData = getCaptureImage();
		setGraphLastScale();
		System.out.println(LocalTime.now());
		if (imageData != null) {
			Image image = new Image(null, imageData);
			return image;
		}
		
		return null;
	
	}
	
	private void setGraphScaleForCapture() {
		if (lastHeight * lastWidth > 480000) {
			graphView.setScaleX(800/lastWidth);
			graphView.setScaleX(600/lastHeight);
		}
	}
	
	private void setGraphLastScale() {
		graphView.setScaleX(zoomManager.getZoomLevel());
		graphView.setScaleY(zoomManager.getZoomLevel());
	}
	
	public void MoveScrollBar(double hValue, double vValue) {
		scrollPane.setHvalue(hValue);
		scrollPane.setVvalue(vValue);
	}

}

