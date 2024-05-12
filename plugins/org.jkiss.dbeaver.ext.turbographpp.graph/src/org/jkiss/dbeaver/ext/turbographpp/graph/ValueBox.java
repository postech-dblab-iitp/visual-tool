package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherNode;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.DataRowID;
import org.jkiss.dbeaver.ext.turbographpp.graph.internal.GraphMessages;

public class ValueBox extends MoveBox {

	private final TabFolder tabFolder;
	private final TabItem valueTab;
	
	private TableViewer tableViewer;
	private TableColumnLayout tableColumnLayout;
	private TableViewerColumn viewerColumn;
	
	private List<Info> dataList = new ArrayList<>();
		
    private static final int OVERLAY_WIDTH = 300;
    private static final int OVERLAY_HEIGHT = 340;
    private static final int TABLEVIEW_MARGIN = 10;
    
    public ValueBox(Control control) {
        super(control, GraphMessages.valbox_title, OVERLAY_WIDTH, OVERLAY_HEIGHT);
        tabFolder = new TabFolder(this.getShell(), SWT.BORDER);
        tabFolder.setEnabled(true);
        
        valueTab = new TabItem(tabFolder, SWT.NONE);
        valueTab.setText("Value");
        
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 3;
        gd.heightHint = OVERLAY_HEIGHT - TABLEVIEW_MARGIN - getMoveButtonSizeY() - valueTab.getBounds().height;
        tabFolder.setLayoutData(gd);
        
        Composite nodeComposite = new Composite(tabFolder, SWT.NONE);
        GridLayout layout1 = new GridLayout(2, false);
        layout1.marginHeight = 0;
        layout1.marginWidth = 0;
        nodeComposite.setLayout(layout1);
        valueTab.setControl(nodeComposite);
        
        createValueWidget(nodeComposite);
    }

    private void createValueWidget(Composite composite) {
    	tableColumnLayout = new TableColumnLayout();
    	composite.setLayout(tableColumnLayout);
    	
    	tableViewer = new TableViewer(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tableViewer.setUseHashlookup(true);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		ColumnViewerToolTipSupport.enableFor(tableViewer,ToolTip.NO_RECREATE);
		
		tableViewer.setContentProvider(new ArrayContentProvider());
		
		createColumns();
		
		MenuManager menuManager = new MenuManager();
		Menu contextMenu = menuManager.createContextMenu(tableViewer.getControl());
		
		menuManager.add(new Action(GraphMessages.valbox_copy_value) {
	        @Override
	        public void run() {
	        	contextMenuAction(true);
	        }
	    });
		
		menuManager.add(new Action(GraphMessages.valbox_copy_name) {
	        @Override
	        public void run() {
	        	contextMenuAction(false);
	        }
	    });
		
		tableViewer.getControl().setMenu(contextMenu);
    }
    
    private void createColumns() {
		for (ColumnEnum column : ColumnEnum.values()) {
			createTableViewerColumn(column.getTitle(), column.getWidth(), column.getAlignment(), column.isResizable(),
					column.isMoveable());
		}
	}
    
    private TableViewerColumn createTableViewerColumn(String title, int width, int alignment, boolean resizable,
			boolean moveable) {
		viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);

		TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setAlignment(alignment);
		column.setMoveable(moveable);
		
		tableColumnLayout.setColumnData(column, new ColumnWeightData(width, width, resizable));
		viewerColumn.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public String getToolTipText(Object element) {
				Info info = (Info)element;
				return info.type + " : " + info.value;
			}

			@Override
			public Point getToolTipShift(Object object) {
				return new Point(5, 5);
			}

			@Override
			public int getToolTipDisplayDelayTime(Object object) {
				return 1500;
			}

			@Override
			public int getToolTipTimeDisplayed(Object object) {
				return 5000;
			}
			
			@Override
			public void update(ViewerCell cell) {
				Info info = (Info)cell.getElement();
				String text;
				if (cell.getColumnIndex() == 0) {
					text = info.name;
				} else {
					text = info.value;
				}
				cell.setText(text);
			}
		});
		
		return viewerColumn;
	}
    
    enum ColumnEnum {
		NAME(GraphMessages.valbox_table_name, 25, SWT.CENTER, true, true),
		VALUE(GraphMessages.valbox_table_value, 40, SWT.LEFT, true, true);

		private final String title;
	    private final int width;
	    private final int alignment;
	    private final boolean resizable;
	    private final boolean moveable;

		private ColumnEnum(String text, int width, int alignment, boolean resizable, boolean moveable) {
			this.title = text;
			this.width = width;
			this.alignment = alignment;
			this.resizable = resizable;
			this.moveable = moveable;
		}

		public String getTitle() {
			return title;
		}

		public int getAlignment() {
			return alignment;
		}

		public boolean isResizable() {
			return resizable;
		}

		public boolean isMoveable() {
			return moveable;
		}

		public int getWidth() {
			return width;
		}
	}
    
    class Info {

		String name;
		String value;
		String type;

		Info(String name, String value, String type) {
			this.name = name;
			this.value = value;
			this.type = type;
		}
		
		Info(String name, String value) {
			this.name = name;
			this.value = value;
			this.type = "String";
		}
	}
    
    private void contextMenuAction(boolean isGetValue) {
    	IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        String data = null;
        
        if (selection.getFirstElement() instanceof Info) {
        	Info info = (Info)selection.getFirstElement();
        	if (isGetValue) {
        		data = info.value;
        	} else {
        		data = info.name;
        	}
        }
        
        Clipboard cb = new Clipboard(this.getShell().getDisplay());
        
        if (data != null) {
        	Transfer textTransfer = (Transfer) TextTransfer.getInstance();
			cb.setContents(new Object[]{data},
					new Transfer[]{textTransfer});
        }
    }
    
    public void open(int positionX, int positionY) {
    	show();
    	setOverlaySize(positionX, positionY, tabFolder.getSize().x, tabFolder.getSize().y);
    }
    
    public void updateItem(Object obj) {
        
    	Object getOjb = null;
    	
    	if (obj instanceof CypherNode) {
    		dataList.clear();
    		
    		CypherNode node = (CypherNode)obj;
    		dataList.add(new Info(DataRowID.NODE_EDGE_ID, node.getID()));
    		dataList.add(new Info(DataRowID.NODE_LABEL, String.valueOf(node.getLabels())));
    		for (String key : node.getProperties().keySet()) {
    			getOjb = node.getProperty(key);
    			dataList.add(new Info(key, String.valueOf(getOjb), getOjb == null ? "null" : getOjb.getClass().getTypeName()));
    		}
    	} else if (obj instanceof CypherEdge) {
    		dataList.clear();
    		
    		CypherEdge edge = (CypherEdge)obj;
    		dataList.add(new Info(DataRowID.NODE_EDGE_ID, edge.getID()));
    		dataList.add(new Info(DataRowID.NODE_LABEL, String.valueOf(edge.getTypes())));
    		dataList.add(new Info(DataRowID.NEO4J_EDGE_START_ID, edge.getStartNodeID()));
    		dataList.add(new Info(DataRowID.NEO4J_EDGE_END_ID, edge.getEndNodeID()));
    		for (String key : edge.getProperties().keySet()) {
    			getOjb = edge.getProperty(key);
    			dataList.add(new Info(key, String.valueOf(getOjb), getOjb == null ? "null" : getOjb.getClass().getTypeName()));
    		}
    	}
    	
		if (tableViewer != null && dataList != null) {
			tableViewer.setInput(dataList);
		}
    }

    @Override
    public void remove() {
        super.remove();
    }
    
    @Override
    public void show() {
        super.show();
    }
    
    @Override
    public void show(int x, int y) {
    }
    
}
