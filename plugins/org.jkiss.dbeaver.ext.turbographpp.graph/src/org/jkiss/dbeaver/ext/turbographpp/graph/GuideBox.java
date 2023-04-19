package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jkiss.dbeaver.ext.turbographpp.graph.internal.GraphMessages;

public class GuideBox {

    private static final int OVERLAY_WIDTH = 300;
    private static final int OVERLAY_HEIGHT = 90;
    public static final int MINIMAP_WIDTH = OVERLAY_WIDTH - 30;
    public static final int MINIMAP_HEIGHT = OVERLAY_HEIGHT;
    private final int OVERLAY_WH_MARGIN = 50;
    
    private List<Composite> parents;
    private Control parentComposite;
    private Shell overlayShell;
    private boolean showing; 
    
    private Text textBox;
    private Combo propertyCombo;
    
    private boolean isMovePressed = false;
    private int pressedPositonX = 0;
    private int pressedPositonY = 0;
    private int lastPositionX = 0;
    private int lastPositionY = 0;

    private PaintListener paintListener;
    
    public GuideBox(Control composite, FXGraph graph) {

        Objects.requireNonNull(composite);

        this.parentComposite = composite;

        parents = new ArrayList<Composite>();
        Composite parent = parentComposite.getParent();
        
        while (parent != null) {
            parents.add(parent);
            parent = parent.getParent();
        }
        
        overlayShell = new Shell(parentComposite.getShell(), SWT.NONE);

        GridLayout gdLayout = new GridLayout(2, false);
        gdLayout.marginWidth = 0;
        gdLayout.marginHeight = 0;
        gdLayout.horizontalSpacing = 5;
        gdLayout.verticalSpacing = 0;
        overlayShell.setLayout(gdLayout);
        
        GridData gdata = new GridData(GridData.FILL_HORIZONTAL);
        gdata.horizontalSpan = 2;
        
        Button moveButoon = new Button(overlayShell, SWT.NONE);
        moveButoon.setAlignment(SWT.CENTER);
        moveButoon.setText(GraphMessages.fxgraph_guidebox_title);
        moveButoon.setLayoutData(gdata);
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
        
        textBox = new Text(overlayShell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        gd.verticalSpan = 3;
        //gd.heightHint = 300;
        textBox.setLayoutData(gd);
        
        Label label = new Label(overlayShell, SWT.CENTER);
        label.setAlignment(SWT.CENTER);
        GridData gd1 = new GridData();
        gd1.horizontalIndent = 10;
        label.setLayoutData(gd1);
        label.setText(GraphMessages.fxgraph_guidebox_properties_label);
        propertyCombo = new Combo(overlayShell, SWT.READ_ONLY);
        propertyCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        propertyCombo.setEnabled(false);
        
        showing = false;
        overlayShell.open();
        overlayShell.setVisible(showing);
       
        paintListener = new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
            	reSize();
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
        	if (parent.getClass().equals(Composite.class)) {
        		System.out.println("parent " + parent.toString());
        		parent.addPaintListener(paintListener);
        	}
        }
        
        showing = true;
    }

    public void remove() {
    	
    	lastPositionX = 0;
    	lastPositionY = 0;
    	
        if (!showing) {
            return;
        }

        if (!overlayShell.isDisposed()) {
            overlayShell.setVisible(false);
        }
        
        for (Composite parent : parents) {
        	if (parent.getClass().equals(Composite.class)) {
        		parent.removePaintListener(paintListener);
        	}
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

        lastPositionX = intersection.x;
        lastPositionY = intersection.y;
        overlayShell.setBounds(intersection);
    }
    
    private void reSize() {
        if (!parentComposite.isVisible()) {
            overlayShell.setBounds(new Rectangle(0, 0, 0, 0));
            return;
        }

        Rectangle OverlayBounds = new Rectangle(
        		lastPositionX , lastPositionY, OVERLAY_WIDTH, OVERLAY_HEIGHT);

        Rectangle intersection = OverlayBounds;

        overlayShell.setBounds(intersection);
    }
    
    private void MovePosition(int movedX, int movedY) {
    	Rectangle OverlayBounds = overlayShell.getBounds();
    	OverlayBounds.x = OverlayBounds.x + movedX;
    	OverlayBounds.y = OverlayBounds.y + movedY;
    	lastPositionX = OverlayBounds.x;
    	lastPositionY = OverlayBounds.y;
        overlayShell.setBounds(OverlayBounds);
    }
    
    public void setText(String msg) {
    	textBox.setText(msg);
    }
    
    public void clearComboList() {
    	propertyCombo.setEnabled(false);
    	propertyCombo.removeAll();
    	
    }
    
    public void setComboList(Set<String> list) {
    	propertyCombo.setEnabled(true);
    	propertyCombo.removeAll();
    	propertyCombo.add("Default Weight (1)");
    	
    	for (String item : list) {
    		propertyCombo.add(item);
    	}
    	
    	propertyCombo.select(0);
    	
    }
    
    public String getSelectedProperty() {
    	if (propertyCombo.getSelectionIndex() != 0) {
    		return propertyCombo.getText();
    	}
    	return null;
    }
}

