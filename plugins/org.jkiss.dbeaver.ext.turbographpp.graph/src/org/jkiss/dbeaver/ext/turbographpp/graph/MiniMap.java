package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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

    private final int OVERLAY_WIDTH = 430;
    private final int OVERLAY_HEIGHT = 300;
    private final int MINIMAP_WIDTH = OVERLAY_WIDTH - 30;
    private final int MINIMAP_HEIGHT = OVERLAY_HEIGHT;
    private final int OVERLAY_WH_MARGIN = 50;
    
    private List<Composite> parents;
    private Control parentComposite;
    private Shell overlayShell;
    private ControlListener controlListener;
    private DisposeListener disposeListener;
    private PaintListener paintListener;
    private PaintListener canvasPaintListener;
    private boolean showing;
    private Canvas miniMapCanvas;
    private Image captureImage;
    private Button zoomIn;
    private Button zoomOut;

    public MiniMap(Control composite) {

        Objects.requireNonNull(composite);

        this.parentComposite = composite;

        parents = new ArrayList<Composite>();
        Composite parent = composite.getParent();
        
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

        disposeListener = new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                remove();
            }
        };

        overlayShell = new Shell(composite.getShell(), SWT.NONE);

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

        canvasPaintListener = new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                if (captureImage != null && !captureImage.isDisposed()) {
                    e.gc.drawImage(captureImage, 0, 0,
                            captureImage.getBounds().width, captureImage.getBounds().height,
                            0, 0, MINIMAP_WIDTH, MINIMAP_HEIGHT);
                	//captureImage.dispose();
                }
            }
        };
        
        zoomIn = new Button(overlayShell, SWT.PUSH | SWT.CENTER);
        zoomIn.setText("+");
        gdata = new GridData();
        gdata.widthHint = OVERLAY_WIDTH - MINIMAP_WIDTH;
        gdata.heightHint = MINIMAP_HEIGHT / 2;
        zoomIn.setLayoutData(gdata);

        zoomOut = new Button(overlayShell, SWT.PUSH | SWT.CENTER);
        zoomOut.setText("-");
        gdata = new GridData();
        gdata.widthHint = OVERLAY_WIDTH - MINIMAP_WIDTH;
        gdata.heightHint = MINIMAP_HEIGHT / 2;
        zoomOut.setLayoutData(gdata);

        showing = false;
        overlayShell.open();
        overlayShell.setVisible(showing);
    }

    public void show() {
        
        if (showing) {
            return;
        }

        rePosition();

        overlayShell.setVisible(true);

        parentComposite.addControlListener(controlListener);
        parentComposite.addDisposeListener(disposeListener);
        parentComposite.addPaintListener(paintListener);
        miniMapCanvas.addPaintListener(canvasPaintListener);

        for (Composite parent : parents) {
            parent.addControlListener(controlListener);
            parent.addPaintListener(paintListener);
        }

        showing = true;
    }

    public void remove() {
    	System.out.println("remove");
    	
    	if (captureImage != null) {
    		captureImage.dispose();
    	}
    	
        if (!showing) {
            return;
        }

        if (!parentComposite.isDisposed()) {
            parentComposite.removeControlListener(controlListener);
            parentComposite.removeDisposeListener(disposeListener);
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
            overlayShell.setBounds(new Rectangle(0, 0, 0, 0));
            return;
        }

        Point OverlaySize = parentComposite.getSize(); 
        Point OverlayDisplayLocation = parentComposite.toDisplay(
                OverlaySize.x  - OVERLAY_WIDTH - OVERLAY_WH_MARGIN, OverlaySize.y - OVERLAY_HEIGHT - OVERLAY_WH_MARGIN);
        Rectangle OverlayBounds = new Rectangle(
                OverlayDisplayLocation.x , OverlayDisplayLocation.y, OVERLAY_WIDTH, OVERLAY_HEIGHT);

        Rectangle intersection = OverlayBounds;

        for (Composite parent : parents) {

            Rectangle parentClientArea = parent.getClientArea();
            Point parentLocation = parent.toDisplay(parentClientArea.x, parentClientArea.y);
            Rectangle parentBounds = new Rectangle(parentLocation.x, parentLocation.y, parentClientArea.width, parentClientArea.height);

            intersection = intersection.intersection(parentBounds);

            if (intersection.width == 0 || intersection.height == 0) {
                break;
            }
        }

        overlayShell.setBounds(intersection);
    }
    
    public void addZoominListner(SelectionListener listner) {
        zoomIn.addSelectionListener(listner);
    }
    
    public void addZoomOutListner(SelectionListener listner) {
        zoomOut.addSelectionListener(listner);
    }

}
