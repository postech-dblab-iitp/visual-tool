package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class MiniMap {

    private final static int OVERLAY_WIDTH = 430;
    private final static int OVERLAY_HEIGHT = 300;
    public static final int MINIMAP_WIDTH = OVERLAY_WIDTH - 30;
    public static final int MINIMAP_HEIGHT = OVERLAY_HEIGHT;
    private final int OVERLAY_WH_MARGIN = 50;
    private final int POINT_MARGIN_WIDTH = 2;
    private final int POINT_MARGIN_HEIGHT = 5;
    
    private List<Composite> parents;
    private Control parentComposite;
    private Shell overlayShell;
    private ControlListener controlListener;
    private PaintListener paintListener;
    private PaintListener canvasPaintListener;
    private boolean showing;
    private Canvas miniMapCanvas;
    private Image captureImage;
    private Button zoomIn;
    private Button zoomOut;
    
    private Rectangle pointRectAngel;

    public MiniMap(Composite composite) {

        Objects.requireNonNull(composite);

        this.parentComposite = composite;

        parents = new ArrayList<Composite>();
        Composite parent = composite;
        
        while (parent != null) {
            parents.add(parent);
            parent = parent.getParent();
        }

        controlListener = new ControlListener() {
            @Override
            public void controlMoved(ControlEvent e) {
                rePosition();
            }

            @Override
            public void controlResized(ControlEvent e) {
                rePosition();
            }
        };

        paintListener = new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                rePosition();
            }
        };

        overlayShell = new Shell(composite.getShell(), SWT.NONE);

        createMiniMapCanvas(overlayShell);

        pointRectAngel = new Rectangle(0, 0, 0, 0);
        
        canvasPaintListener = new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                if (captureImage != null && !captureImage.isDisposed()) {
                	Color color = parentComposite.getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
                	e.gc.setForeground(color);
                	e.gc.setLineWidth(4);
                	e.gc.drawImage(captureImage, 0, 0,
                			captureImage.getBounds().width, captureImage.getBounds().height,
                			0, 0, MINIMAP_WIDTH, MINIMAP_HEIGHT);
                	e.gc.drawRectangle(pointRectAngel);
                	color.dispose();
                	//captureImage.dispose();
                }
            }
        };
        
        zoomIn = new Button(overlayShell, SWT.PUSH | SWT.CENTER);
        zoomIn.setText("+");
        GridData gdata = new GridData();
        gdata.widthHint = OVERLAY_WIDTH - MINIMAP_WIDTH;
        gdata.heightHint = MINIMAP_HEIGHT / 2;
        zoomIn.setLayoutData(gdata);

        zoomOut = new Button(overlayShell, SWT.PUSH | SWT.CENTER);
        zoomOut.setText("-");
        gdata = new GridData();
        gdata.widthHint = OVERLAY_WIDTH - MINIMAP_WIDTH;
        gdata.heightHint = MINIMAP_HEIGHT / 2;
        zoomOut.setLayoutData(gdata);

        System.out.println("create showing set false");
        showing = false;
        overlayShell.open();
        overlayShell.setVisible(showing);
    }
    
    private void createMiniMapCanvas(Shell overlayShell) {
    	GridLayout gdLayout = new GridLayout(2, false);
        gdLayout.marginWidth = 0;
        gdLayout.marginHeight = 0;
        gdLayout.horizontalSpacing = 0;
        gdLayout.verticalSpacing = 0;
        overlayShell.setLayout(gdLayout);
        
        GridData gdata = new GridData();
        gdata.verticalSpan = 2;
        gdata.widthHint = MINIMAP_WIDTH;
        gdata.heightHint = MINIMAP_HEIGHT;
    	
    	miniMapCanvas = new Canvas(overlayShell, SWT.NONE | SWT.BORDER);
        miniMapCanvas.setLayout(gdLayout);
        miniMapCanvas.setLayoutData(gdata);
    }

    private void addListnener(Composite newComposite) {
    	
    	parentComposite = newComposite;
        Composite composite = newComposite;
    	 
    	while (composite != null) {
            parents.add(composite);
            composite = composite.getParent();
        }
    	
    	addListnener();
    }
    
    private void addListnener() {
    	
    	parentComposite.addControlListener(controlListener);
        parentComposite.addPaintListener(paintListener);
        miniMapCanvas.addPaintListener(canvasPaintListener);

        for (Composite parent : parents) {
        	if (!parent.isDisposed()) {
	            parent.addControlListener(controlListener);
	            parent.addPaintListener(paintListener);
        	}
        }
    }
    
    private void removeListnener() {
    	
    	if (!parentComposite.isDisposed()) {
            parentComposite.removeControlListener(controlListener);
            parentComposite.removePaintListener(paintListener);
        }
        
        if (!miniMapCanvas.isDisposed()) {
            miniMapCanvas.removePaintListener(canvasPaintListener);
        }

        for (Composite parent : parents) {
            if (!parent.isDisposed()) {
                parent.removeControlListener(controlListener);
                parent.removePaintListener(paintListener);
            }
        }
    }
    
    public void show() {
        if (showing) {
            return;
        }

        rePosition();

        overlayShell.setVisible(true);

        addListnener();

        parentComposite.redraw();
        
        showing = true;
    }

    public void remove() {
    	if (captureImage != null) {
    		captureImage.dispose();
    	}
    	
        if (!showing) {
            return;
        }

        removeListnener();
        
        if (!overlayShell.isDisposed()) {
            overlayShell.setVisible(false);
        }

        showing = false;
    }

    public void setBackground(Color background) {
        overlayShell.setBackground(background);
    }

    public Color getBackground() {
        return overlayShell.getBackground();
    }

    public void setAlpha(int alpha) {
        overlayShell.setAlpha(alpha);
    }

    public int getAlpha() {
        return overlayShell.getAlpha();
    }

    public boolean isShowing() {
        return showing;
    }

    public void setImage(Image image) {
        captureImage = image;
    }
    
    public void reDraw() {
        miniMapCanvas.redraw();    
    }
    
    private void rePosition() {
        if (!parentComposite.isVisible()) {
        	if (!overlayShell.isDisposed()) {
        		overlayShell.setBounds(new Rectangle(0, 0, 0, 0));
        	}
        	return;
        }

        Point OverlaySize = parentComposite.getSize(); 
        Point OverlayDisplayLocation = parentComposite.toDisplay(
                OverlaySize.x  - OVERLAY_WIDTH - OVERLAY_WH_MARGIN, OverlaySize.y - OVERLAY_HEIGHT - OVERLAY_WH_MARGIN);
        Rectangle OverlayBounds = new Rectangle(
                OverlayDisplayLocation.x , OverlayDisplayLocation.y, OVERLAY_WIDTH, OVERLAY_HEIGHT);

        Rectangle intersection = OverlayBounds;

        if (!overlayShell.isDisposed()) {
        	overlayShell.setBounds(intersection);
        }
    }
    
    public void addZoominListner(SelectionListener listner) {
        zoomIn.addSelectionListener(listner);
    }
    
    public void addZoomOutListner(SelectionListener listner) {
        zoomOut.addSelectionListener(listner);
    }
    
    public void addCavasMouseListener(MouseListener listner) {
    	miniMapCanvas.addMouseListener(listner);
    }

    
    public void setPointRectAngel(double viewWidth, double viewHeight, double viewportWidth, double viewportHeight, double vValue, double hValue) {
    	
    	int pointX, pointY, width, height;
    	int leftWidth, leftHeight;
    	
    	width = (int) (viewportWidth/viewWidth * MINIMAP_WIDTH);
		height = (int) (viewportHeight/viewHeight * MINIMAP_HEIGHT);
		
		width = width >= MINIMAP_WIDTH ? MINIMAP_WIDTH : width;
		height = height >= MINIMAP_HEIGHT ? MINIMAP_HEIGHT : height;
		
		leftWidth =  MINIMAP_WIDTH - width;
		leftHeight =  MINIMAP_HEIGHT - height;
		
		pointX = (int) (leftWidth * hValue);
		pointY = (int) (leftHeight * vValue);
		
    	pointRectAngel.x = pointX;
    	pointRectAngel.y = pointY;
   		pointRectAngel.width = width - POINT_MARGIN_WIDTH;
   		pointRectAngel.height = height - POINT_MARGIN_HEIGHT;
    	
   		if (miniMapCanvas != null && !miniMapCanvas.isDisposed()) {
   			miniMapCanvas.redraw();
    	}
    }

    public void changeParent(Composite composite, Shell shell) {
    	removeListnener();

        overlayShell.dispose();
        overlayShell = new Shell(shell, SWT.NONE);
        
        if (showing) {
        	overlayShell.setVisible(true);
        	show();
        }

        GridLayout gdLayout = new GridLayout(2, false);
        gdLayout.marginWidth = 0;
        gdLayout.marginHeight = 0;
        gdLayout.horizontalSpacing = 0;
        gdLayout.verticalSpacing = 0;
        overlayShell.setLayout(gdLayout);
        
        GridData gdata = new GridData();
        gdata.verticalSpan = 2;
        gdata.widthHint = MINIMAP_WIDTH;
        gdata.heightHint = MINIMAP_HEIGHT;
        
        createMiniMapCanvas(overlayShell);
        
        addListnener(composite);
    }
    
}

