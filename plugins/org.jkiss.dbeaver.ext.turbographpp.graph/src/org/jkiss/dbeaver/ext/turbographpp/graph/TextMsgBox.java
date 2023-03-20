package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TextMsgBox {

    private static final int OVERLAY_WIDTH = 300;
    private static final int OVERLAY_HEIGHT = 80;
    public static final int MINIMAP_WIDTH = OVERLAY_WIDTH - 30;
    public static final int MINIMAP_HEIGHT = OVERLAY_HEIGHT;
    private final int OVERLAY_WH_MARGIN = 50;
    
    private List<Composite> parents;
    private Control parentComposite;
    private Shell overlayShell;
    private boolean showing; 
    
    private Text textBox;
    
    private boolean isMovePressed = false;
    private int pressedPositonX = 0;
    private int pressedPositonY = 0;

    private PaintListener paintListener;
    
    public TextMsgBox(Control composite, FXGraph graph) {

        Objects.requireNonNull(composite);

        this.parentComposite = composite;

        parents = new ArrayList<Composite>();
        Composite parent = composite.getParent();
        
        while (parent != null) {
            parents.add(parent);
            parent = parent.getParent();
        }
        
        overlayShell = new Shell(composite.getShell(), SWT.NONE);

        GridLayout gdLayout = new GridLayout(1, false);
        gdLayout.marginWidth = 0;
        gdLayout.marginHeight = 0;
        gdLayout.horizontalSpacing = 0;
        gdLayout.verticalSpacing = 0;
        overlayShell.setLayout(gdLayout);
        
        GridData gdata = new GridData();
        gdata.verticalSpan = 1;
        gdata.widthHint = MINIMAP_WIDTH;
        gdata.heightHint = MINIMAP_HEIGHT;
        
        Button moveButoon = new Button(overlayShell, SWT.NONE);
        moveButoon.setAlignment(SWT.CENTER);
        moveButoon.setText("ShortestMode");
        moveButoon.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        moveButoon.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				isMovePressed = false;
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				isMovePressed = true;
				pressedPositonX = e.x;
				pressedPositonY = e.y;
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
        
        moveButoon.addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent e) {
				if (isMovePressed) {
					if ( e.x > 0) {
						MovePosition(e.x - pressedPositonX, e.y - pressedPositonY); 
					} else {
						MovePosition(e.x - pressedPositonX, e.y - pressedPositonY);
					}
				}
			}
		});
        
        textBox = new Text(overlayShell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        textBox.setLayoutData(new GridData(GridData.FILL_BOTH));
        textBox.setEnabled(false);
        
        showing = false;
        overlayShell.open();
        overlayShell.setVisible(showing);
       
        paintListener = new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                rePosition();
            }
        };
    }

    public void show() {
        
        if (showing) {
            return;
        }

        rePosition();

        overlayShell.setVisible(true);
        
        for (Composite parent : parents) {
            parent.addPaintListener(paintListener);
        }
        
        showing = true;
    }

    public void remove() {
        if (!showing) {
            return;
        }

        if (!overlayShell.isDisposed()) {
            overlayShell.setVisible(false);
        }

        for (Composite parent : parents) {
            parent.removePaintListener(paintListener);
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

    private void rePosition() {
        if (!parentComposite.isVisible()) {
            overlayShell.setBounds(new Rectangle(0, 0, 0, 0));
            return;
        }

        Point OverlaySize = parentComposite.getSize(); 
        Point OverlayDisplayLocation = parentComposite.toDisplay(
                OverlaySize.x  - OVERLAY_WIDTH - OVERLAY_WH_MARGIN, OVERLAY_WH_MARGIN);
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
    
    private void MovePosition(int movedX, int movedY) {
    	Rectangle OverlayBounds = overlayShell.getBounds();
    	OverlayBounds.x = OverlayBounds.x + movedX;
    	OverlayBounds.y = OverlayBounds.y + movedY;
        overlayShell.setBounds(OverlayBounds);
    }
    
    public void setText(String msg) {
    	textBox.setText(msg);
    }
}

