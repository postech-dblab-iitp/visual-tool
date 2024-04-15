package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;

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
import org.jkiss.dbeaver.ext.turbographpp.graph.chart.GraphChart;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherNode;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.DeleteGraphElement;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.GraphDataModel;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.NodesEdges;
import org.jkiss.dbeaver.ext.turbographpp.graph.dialog.CSVDialog;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.TurboGraphList;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphalgorithms.ShortestPath;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.layout.SmartPlacementStrategy;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.layout.SmartRandomPlacementStrategy;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.layout.SmartCircularGroupPlacementStrategy;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.layout.SmartGridPlacementStrategy;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.layout.SmartHorizotalTreePlacementStrategy;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.layout.SmartVerticalTreePlacementStrategy;
import org.jkiss.dbeaver.ext.turbographpp.graph.internal.GraphMessages;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartGraphVertex;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview.SmartStyleProxy;
import org.jkiss.dbeaver.ext.turbographpp.graph.utils.ExportCSV;
import org.jkiss.dbeaver.model.DBPDataSource;

public class FXGraph implements GraphBase {
    
	public static final int MOUSE_WHELL_UP = 5;
	public static final int MOUSE_WHELL_DOWN = -5;
	
	public static final int MOUSE_SECONDARY_BUTTON = 3;

	public static final int CTRL_KEYCODE = 0x40000;
	public static final int Z_KEYCODE = 0x7a;
	public static final int Y_KEYCODE = 0x79;
	
    private FXCanvas canvas;
    private TurboGraphList<CypherNode, CypherEdge> graph;
    private SmartGraphPanel<CypherNode, CypherEdge> graphView;
    Group graphGroup;
    private ScrollPane scrollPane;
    private VBox vBox;
    
    private Control control;
    private Scene scene;
    
    private MiniMap miniMap;
    
    private boolean ctrlKeyMode = false;
    private ZoomManager zoomManager;
    
    private GraphDataModel dataModel = new GraphDataModel();
    
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
    private MenuItem designMenu;

    private SmartGraphVertex<CypherNode> selectNode = null;
    
    private boolean statusCanvasFocus = false;
    
    private double lastWidth = 0;
    private double lastHeight = 0;
    
    private double lastViewportWidth = 0;
    private double lastViewportHeight = 0;
    
    private ArrayList<DeleteGraphElement> undoList = new ArrayList<>();
    private ArrayList<DeleteGraphElement> redoList = new ArrayList<>();
    private NodesEdges shortestList = new NodesEdges();
    
    private boolean shortestMode = false;
    private SmartGraphVertex<CypherNode> startVertex;
    private SmartGraphVertex<CypherNode> endVertex;
    
    private ShortestGuideBox guideBox;
    private DesignBox designBox;
    private GraphChart chartBox;
    private ValueBox valBox;
    
    private DBPDataSource parentDataSource;
    
    private LayoutStyle lastLayoutstyle = LayoutStyle.SPRING;
    
    public FXGraph(Composite parent, int style) {
    	this(parent, style, null);
    }
    
    public FXGraph(Composite parent, int style, DBPDataSource dataSource) {
        this.control = parent;
        this.parentDataSource = dataSource; 
        //this default option are true then fx thread issue when changed Presentation.
        Platform.setImplicitExit(false);

        canvas = new FXCanvas(parent, SWT.NONE);

        graph = new TurboGraphList<>();
        
        SmartPlacementStrategy strategy = new SmartRandomPlacementStrategy();
        
        graphView = new SmartGraphPanel<>(graph, strategy);
        
        graphGroup = new Group();
        graphGroup.getChildren().addAll(graphView);
        
        vBox = new VBox();
        vBox.getChildren().add(graphGroup);
        
        scrollPane = new ScrollPane();
        scrollPane.setContent(vBox);

        scene = new Scene(scrollPane);

        zoomManager = new ZoomManager(graphView, scrollPane);
        
        registerContextMenu();
        
        setCanvasListener();
        setGraphViewListener();
        setBaseListener();
        
        canvas.setScene(scene);
        
        createMiniMap(canvas);
        guideBox = new ShortestGuideBox(canvas, this);
        
        designBox = new DesignBox(canvas, this);
        
        chartBox = new GraphChart(canvas, this, parentDataSource);
        
        valBox = new ValueBox(canvas);
        
        parent.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (miniMap != null) {
					miniMap.remove();
				} 
				
				if (guideBox != null) {
					guideBox.remove();
				}
				
				if (designBox != null) {
					designBox.remove();
				}
				
				if (chartBox != null) {
				    chartBox.remove();
				}
			}
		});
        
        parent.addListener( SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event event) {
            	double parentWidth = parent.getBounds().width;
            	double parentHeight = parent.getBounds().height;
            	double graphViewWidth = graphView.getBoundsInParent().getWidth();
            	double graphViewHeight = graphView.getBoundsInParent().getHeight();
            	
            	resize(parentWidth > graphViewWidth ? parentWidth : graphViewWidth, 
            			parentHeight > graphViewHeight ? parentHeight : graphViewHeight);
            	
            }
        });
    }

    private void setCanvasListener() {
    	
    	canvas.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				
				if (miniMap.isShowing()) {
				
					Double hValue, vValue;
					double inParentWidth, inParentHeight;
					lastViewportWidth = scrollPane.getViewportBounds().getWidth();
					lastViewportHeight = scrollPane.getViewportBounds().getHeight();
					inParentWidth =  graphView.getBoundsInParent().getWidth();
					inParentHeight = graphView.getBoundsInParent().getHeight();
					hValue = scrollPane.getHvalue();
					vValue = scrollPane.getVvalue();
				
					miniMap.setPointRectAngel(inParentWidth, inParentHeight, lastViewportWidth, lastViewportHeight, vValue, hValue);
					
				}
			}
		});
    	
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
        graphView.setVertexDoubleClickAction(
                (SmartGraphVertex<CypherNode> graphVertex) -> {
                    if (shortestMode) {
                        if (isEmptyStartVertex()) {
                            startVertex = graphVertex;
                            graphView.doHighlightVertexStyle(startVertex);
                            guideBox.setComboList(getincidentEdgesProperies(startVertex.getUnderlyingVertex()));
                            guideBox.setText(GraphMessages.shortest_please_select_end);
                        } else if (isEmptyEndVertex()) {
                            endVertex = graphVertex;
                            graphView.doHighlightVertexStyle(endVertex);
                            String result = "";
                            if (!runShortest(guideBox.getSelectedProperty())) {
                                result = GraphMessages.shortest_not_find_search_path + "\n";
                            }
                            guideBox.setText(result + GraphMessages.shortest_please_select_first +"\n"
                            		+ GraphMessages.shortest_info_path + " "  
                                    + GraphMessages.shortest_info_count + " = " + graph.getPathCount() + " "
                                    + GraphMessages.shortest_info_weight + " = " + graph.getLastWeight() + "\n"
                            		+ graph.getLastPathString());
                        } else {
                            unShortestMode();
                            startVertex = null;
                            endVertex = null;

                            startVertex = graphVertex;
                            graphView.doHighlightVertexStyle(startVertex);
                            guideBox.setComboList(getincidentEdgesProperies(startVertex.getUnderlyingVertex()));
                            guideBox.setText(GraphMessages.shortest_please_select_end);
                        }

                    } else {
                        setUnhighlight();

                        selectNode = graphVertex;
                        graphView.doHighlightVertexStyle(graphVertex);

                        if (graphVertex.getUnderlyingVertex().element() instanceof CypherNode) {
                            CypherNode node =
                                    (CypherNode) graphVertex.getUnderlyingVertex().element();
                            String ID = node.getID();
                            if (nodeIDConsumer == null) {
                                return;
                            }
                            nodeIDConsumer.accept(ID);
                        }
                    }
                });

        graphView.setEdgeDoubleClickAction(graphEdge -> {});

        graphView.setVertexSelectAction(
                (SmartGraphVertex<CypherNode> graphVertex) -> {
                    if (graphVertex.getUnderlyingVertex().element() instanceof CypherNode) {
                        CypherNode node = (CypherNode) graphVertex.getUnderlyingVertex().element();
                        String ID = node.getID();
                        if (nodeIDConsumer == null) {
                            return;
                        }
                        nodeIDConsumer.accept(ID);
                        designBox.setSelectItem(node);
                        valBox.updateItem(node);
                    }
                });

        graphView.setEdgeSelectAction(
                graphEdge -> {
                    if (graphEdge.getUnderlyingEdge().element() instanceof CypherEdge) {
                        CypherEdge edge = (CypherEdge) graphEdge.getUnderlyingEdge().element();
                        String ID = edge.getID();
                        if (edgeIDConsumer == null) {
                            return;
                        }
                        edgeIDConsumer.accept(ID);
                        designBox.setSelectItem(edge);
                        valBox.updateItem(edge);
                    }
                });

        graphView.setVertexMovedAction(
                event -> {
                    miniMapUpdate();
                });
    }
    
    private void setBaseListener() {
        this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == CTRL_KEYCODE) {
					ctrlKeyMode = false;
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == CTRL_KEYCODE ) {
					ctrlKeyMode = true;
				}
				
				if (ctrlKeyMode) {
					if (e.keyCode == Z_KEYCODE) {
						doUndo();
					} else if (e.keyCode == Y_KEYCODE) {
						doRedo();
					}
				}
				
				if (selectNode != null && e.keyCode == SWT.DEL) {
					doDelete(true);
				}
				
			}
		});
		
		this.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseScrolled(MouseEvent e) {
				if (ctrlKeyMode) {
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
    	if (guideBox != null) {
    		guideBox.remove();
    	}
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
        graphView.setInitSize(width, height);
    }
    
    private void graphInit(boolean refreshMetadata) {
        if (refreshMetadata) {
            zoomManager.setDefaultZoom();
            graphView.init();
            graphView.update();
            layoutUpdatethread = new LayoutUpdateThread();
            layoutUpdatethread.start();
        } else {
            graphView.updateAndWait();
            setLayoutAlgorithm(lastLayoutstyle);
        }

        selectNode = null;
        shortestMode = false;
        startVertex = null;
        endVertex = null;
    }
    
    public void drawGraph(boolean refreshMetadata, double width, double height) {
    	resize(width, height);
    	graphInit(refreshMetadata);
    }

    @Override
    public Object addNode(String id, List<String> labels, LinkedHashMap<String, Object> attr) {
    	//For Group Color
    	Object v = null;
    	String fillColor = "";
    	for (String label : labels) {
	    	if (nodesGroup.get(label) == null) {
	    		nodesGroup.put(label, ramdomColor());
	    	}
	    	
	    	if (dataModel.getNode(id) != null) {
	    	    return null;
	    	}
	    	
	    	fillColor = nodesGroup.get(label);
	    	
    	}
    	CypherNode node = new CypherNode(id, labels, attr, fillColor);
    	v = graph.insertVertex(node);
    	dataModel.putNode(id, labels, (Vertex<CypherNode>)v);
    	return v;
    }

    @Override
    public Object addEdge(String id, List<String> types, String startNodeID, String endNodeID,
            LinkedHashMap<String, Object> attr) {
        if (dataModel.getEdge(id) != null) {
            return null;
        }
        
    	CypherEdge edge = new CypherEdge(id, types, attr, startNodeID, endNodeID);
    	Object e = graph.insertEdge(dataModel.getNode(startNodeID), dataModel.getNode(endNodeID), edge);
    	dataModel.putEdge(id, types, (FxEdge<CypherEdge, CypherNode>)e);
        return e;
    }

    private boolean removeNode(SmartGraphVertex<CypherNode> node) {
    	if (node != null) {
	    	graph.removeVertex(node.getUnderlyingVertex());
	    	return true;
    	}
    	
    	return false;
    }
    
    private Vertex<CypherNode> restoreNode(SmartGraphVertex<CypherNode> node, double x, double y) {
    	if (node != null) {
    		node.getUnderlyingVertex().element().setLastPosition(x, y);
    		Vertex<CypherNode> v = graph.insertVertex(node.getUnderlyingVertex().element());
	    	return v;
    	}
    	return null;
    }
    
    private boolean restoreEdge(CypherEdge edge) {
    	if (edge != null) {
    		graph.insertEdge(dataModel.getNode(edge.getStartNodeID()).element(), dataModel.getNode(edge.getEndNodeID()).element(), edge);
	    	return true;
    	}
    	return false;
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
    	shortestMode = false;
    	startVertex = null;
    	endVertex = null;
    	dataModel.clear();
    	undoList.clear();
        redoList.clear();
        graph.clearElement();
        graphView.clear();
        nodesGroup.clear();
        
        subClose();
    }

    @Override
    public void clear() {
    }
    
    @Override
    public void setLayoutAlgorithm(LayoutStyle layoutStyle) {
        resize(lastWidth, lastHeight);
        setAutomaticLayout(false);
        lastLayoutstyle = layoutStyle;
        
        switch (layoutStyle) {
            case RADIAL:
                graphView.setSmartPlacementStrategy(new SmartCircularGroupPlacementStrategy());
                break;
            case SPRING:
                graphView.setSmartPlacementStrategy(new SmartRandomPlacementStrategy());
                if (!miniMap.isShowing()) {
                    //setAutomaticLayout(true);
                    layoutUpdatethread = new LayoutUpdateThread();
                    layoutUpdatethread.start();
                }
                break;
            case HORIZONTAL_TREE:
                graphView.setSmartPlacementStrategy(new SmartHorizotalTreePlacementStrategy());
                break;
            case VERTICAL_TREE:
                graphView.setSmartPlacementStrategy(new SmartVerticalTreePlacementStrategy());
                break;
            case GRID:
                graphView.setSmartPlacementStrategy(new SmartGridPlacementStrategy());
                break;
            default:
                break;
        }
        
        miniMapUpdate();
    }
	
	public void setDefaultLayoutAlgorithm() {
	    lastLayoutstyle = LayoutStyle.SPRING;
		graphView.setSmartPlacementStrategy(new SmartRandomPlacementStrategy());
	}
	
	public LayoutStyle getLastLayoutAlgorithm() {
	    return lastLayoutstyle;
	}

	@Override
	public void setCursor(Cursor cursor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setForeground(Color color) {
//		String rgb = Integer.toHexString(color.getRed())
//		+ Integer.toHexString(color.getGreen())
//		+ Integer.toHexString(color.getBlue());
//		
		canvas.setForeground(color);
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

	public int getNumNodes() {
		int ret = graph.numVertices();
		if (ret <= 0) {
			return 0;
		}
		return ret;	
	}
	
	public int getNumEdges() {
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
				Thread.sleep(700);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			setAutomaticLayout(true);
			
			if (miniMap.isShowing()) {
				try {
					Thread.sleep(500);
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
		r = random.nextInt(80) + 70;
		g = random.nextInt(170) + 70;
		b = random.nextInt(170) + 70;

		return Integer.toHexString(r) + Integer.toHexString(g) +  Integer.toHexString(b);
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
		designMenu = new MenuItem("Design");

		redoMenu.setDisable(true);
		undoMenu.setDisable(true);
		highlightMenu.setDisable(true);
		deteleMenu.setDisable(true);
		unHighlightMenu.setDisable(true);

		contextMenu.getItems().addAll(redoMenu, undoMenu, highlightMenu, unHighlightMenu, deteleMenu, designMenu);
		
		contextMenuAction();
		
		graphView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>()
		{
		  @Override
		  public void handle(ContextMenuEvent event)
		  {
			if (selectNode != null && !graphView.isHighlighted()) {
				highlightMenu.setDisable(false);
				deteleMenu.setDisable(false);
			} else {
				highlightMenu.setDisable(true);
				deteleMenu.setDisable(true);
			}
			
			if (graphView.isHighlighted()) {
				unHighlightMenu.setDisable(false);
			} else {
				unHighlightMenu.setDisable(true);
			}
			  
			if (statusCanvasFocus) {
				contextMenu.show(graphView, event.getScreenX(), event.getScreenY());
			}
			
			if (redoList.size() != 0) {
				redoMenu.setDisable(false);
			} else {
				redoMenu.setDisable(true);
			}
			
			if (undoList.size() != 0) {
				undoMenu.setDisable(false);
			} else {
				undoMenu.setDisable(true);
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
		
		deteleMenu.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				doDelete(true);
			}
		});
		
		redoMenu.setOnAction(new EventHandler<ActionEvent>() {
					
			@Override
			public void handle(ActionEvent arg0) {
				doRedo();
			}
		});

		undoMenu.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				doUndo();
			}
		});
		
		designMenu.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				designBox.open((int)contextMenu.getX(), (int)contextMenu.getY());
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
	
	private void doDelete(boolean delete) {
		if (selectNode != null) {
			DeleteGraphElement deleteModel = new DeleteGraphElement(selectNode, selectNode.getPositionCenterX(), selectNode.getPositionCenterY());
            deleteModel.addEdges(graph.incomingEdges(selectNode.getUnderlyingVertex()));
            deleteModel.addEdges(graph.outboundEdges(selectNode.getUnderlyingVertex()));
			removeNode(selectNode);
			if (undoList.size() > 4) {
				undoList.remove(0);
			}
			undoList.add(deleteModel);
			graphView.update();
			selectNode = null;
			
			if (delete) {
				redoList.clear();
			}
        }
	}
	
	private void doRedo() {
		if (redoList.size() != 0) {
			int idx = redoList.size() -1;
			DeleteGraphElement deleteModel = redoList.get(idx); 
			clearSelectNode();
			selectNode = graphView.getGraphVertex(deleteModel.getVertex());
			doDelete(false);
			
			redoList.remove(idx);
			graphView.update();
			selectNode= null;
			
		}
	}
	
	private void doUndo() {
		if (undoList.size() != 0) {
			int idx = undoList.size() -1;
			DeleteGraphElement deleteModel = undoList.get(idx); 
			Vertex<CypherNode> vertex = restoreNode(deleteModel.getNode(), deleteModel.getPositionX(), deleteModel.getPositionY());
			deleteModel.setVertex(vertex);
			Iterator<FxEdge<CypherEdge, CypherNode>> itr = deleteModel.getEdges().iterator();
			while(itr.hasNext()) {
				CypherEdge cyperEdge = itr.next().element();
				restoreEdge(cyperEdge);
			}
			
			if (redoList.size() > 4) {
				redoList.remove(0);
			}
			
			redoList.add(deleteModel);
			undoList.remove(idx);
			graphView.update();
		}
	}
	
	private void setHighlight() {
		if (selectNode != null) {
        	graphView.setHighlight(selectNode.getUnderlyingVertex());
        }
	}
	
    private void setUnhighlight() {

        if (selectNode != null) {
            graphView.doDefaultVertexStyle(selectNode);
            selectNode = null;
        }

        graphView.setUnHighlight();
    }
	public void setAutomaticLayout (boolean value) {
		if (graphView != null) {
		    System.out.println("setAutomaticLayout : " + value);
			graphView.setAutomaticLayout(value);
		}
	}
	
	private void createMiniMap(Composite parent) {
        miniMap = new MiniMap(parent);
        miniMap.addZoominListner(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
       			zoomIn();
            }
        });
        
        miniMap.addZoomOutListner(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
       			zoomOut();
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
//		System.out.println(LocalTime.now());
		setGraphScaleForCapture();
		ImageData imageData = getCaptureImage();
		setGraphLastScale();
//		System.out.println(LocalTime.now());
		if (imageData != null) {
			Image image = new Image(null, imageData);
			return image;
		}
		
		return null;
	
	}
	
	private void setGraphScaleForCapture() {
		if (lastHeight * lastWidth > 480000) {
		    graphView.setScaleX(800 / lastWidth);
		    graphView.setScaleY(600 / lastHeight);
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

	public boolean exportCSV() {
		
		CSVDialog csvDialog = new CSVDialog(this.getControl().getShell());
		
		csvDialog.create();
		if (csvDialog.open() == Window.OK) {
			return ExportCSV.exportCSV(csvDialog.getFolderPath(), csvDialog.getNodeFileName(), csvDialog.getEdgeFileName(), 
					graph.vertices(), graph.edges());
		}
		
		return false;
	}
	
	public void setShortestMode(boolean status) {
		shortestMode = status;
		
		if (status) {
            setUnhighlight();

			startVertex = null;
			endVertex = null;
			
			guideBox.setText(GraphMessages.shortest_please_select_first);
			guideBox.open();
			
		} else {
			unShortestMode();
			guideBox.remove();
		}
	}
	
	public boolean getShortestMode() {
		return shortestMode;
	}
	
	public boolean runShortest(String propertyName) {
		if(startVertex != null && endVertex != null) {
			shortestList = ShortestPath.start(graph, graphView, startVertex.getUnderlyingVertex(), endVertex.getUnderlyingVertex(), propertyName);
			if (shortestList == null || shortestList.getEdges().size() == 0) {
				graphView.getStylableVertex(startVertex.getUnderlyingVertex()).setStyle(SmartStyleProxy.DEFAULT_VERTEX + startVertex.getUnderlyingVertex().element().getFillColor());
				graphView.getStylableVertex(endVertex.getUnderlyingVertex()).setStyle(SmartStyleProxy.DEFAULT_VERTEX + endVertex.getUnderlyingVertex().element().getFillColor());
				return false;
			} else {
			    return true;
			}
        } else{
        	guideBox.setText(GraphMessages.shortest_please_select_between + "\n\n");
        	return false;
        }
	}
	
	private void unShortestMode() {
		if (shortestList != null) {
			for(Vertex<CypherNode> node : shortestList.getNodes()) {
				graphView.getStylableVertex(node).setStyle(SmartStyleProxy.DEFAULT_VERTEX + node.element().getFillColor());
			}
					
					
			for(FxEdge<CypherEdge, CypherNode> edge : shortestList.getEdges()) {
				CypherEdge cyperEdge = edge.element();
				graphView.getStylableEdge(edge).setStyle(
						SmartStyleProxy.getEdgeStyleInputValue(
								cyperEdge.getLineColor(), 
								cyperEdge.getLineStyle(), 
								cyperEdge.getLineStrength()));
			}
			
			shortestList = null;
		} else {
			if (startVertex != null) {
				graphView.doDefaultVertexStyle(startVertex);
				startVertex = null;
			}
			
			if (endVertex != null) {
				graphView.doDefaultVertexStyle(endVertex);
				endVertex = null;
			}
		}
		
	}
	
	private boolean isEmptyStartVertex() {
		if (startVertex == null) {
			return true;
		}
		return false;
	}
	
	private boolean isEmptyEndVertex() {
		if (endVertex == null) {
			return true;
		}
		return false;
	}
	
	private Set<String> getincidentEdgesProperies(Vertex<CypherNode> vertex) {
		Collection<FxEdge<CypherEdge, CypherNode>> edges;
		Set<String> properties = new HashSet <>();
		if (vertex != null) {
            edges = graph.incomingEdges(vertex);
			for (FxEdge<CypherEdge, CypherNode> edge : edges) {
				properties.addAll(edge.element().getProperties().keySet());
			}
			
			if (properties.size() > 0) {
				return properties;
			}
		}
		
		return properties;
	}
	
	public GraphDataModel getDataModel() {
		return dataModel;
	}
	
	public SmartGraphPanel<CypherNode, CypherEdge> getGraphView() {
		return graphView;
	}
	
	public TurboGraphList<CypherNode, CypherEdge> getGraph() {
		return graph;
	}
	
	public void designEditorShow() {
		designBox.open(Display.getCurrent().getCursorLocation().x, Display.getCurrent().getCursorLocation().y);
	}
	
	public void chartShow() {
	    chartBox.open(Display.getCurrent().getCursorLocation().x, Display.getCurrent().getCursorLocation().y);
	}
	
	public void valueShow() {
	    valBox.open(Display.getCurrent().getCursorLocation().x, Display.getCurrent().getCursorLocation().y);
	}
	
	public void setCurrentQuery(String query, int rowCount) {
	    chartBox.setCurrentQuery(query, rowCount);
	}
	
//	public void updateChart(HashMap<String, Object> data) {
//        Platform.runLater(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        chartBox.updateChart(data);
//                    }
//                });
//   
//	}

	private void subClose() {
		if (chartBox != null) chartBox.remove();
		if (designBox != null) designBox.remove();
		if (guideBox != null) guideBox.remove();
		if (valBox != null) valBox.remove();
	}
	
}

