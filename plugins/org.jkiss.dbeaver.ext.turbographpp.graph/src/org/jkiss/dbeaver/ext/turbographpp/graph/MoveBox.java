package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class MoveBox {

    private static final int OVERLAY_WIDTH = 300;
    private static final int OVERLAY_HEIGHT = 90;
    private final int OVERLAY_WH_MARGIN = 50;

    private List<Composite> parents;
    private Composite parentComposite;
    private Shell overlayShell;
    private boolean showing;

    private boolean isMovePressed = false;
    private int pressedPositonX = 0;
    private int pressedPositonY = 0;
    private int lastPositionX = 0;
    private int lastPositionY = 0;

    private int overlay_width = OVERLAY_WIDTH;
    private int overlay_height = OVERLAY_HEIGHT;

    private PaintListener paintListener;

    private Button moveButton;

    public MoveBox(Control composite, String title) {
        this(composite, title, OVERLAY_WIDTH, OVERLAY_HEIGHT, true);
    }

    public MoveBox(Control composite, String title, boolean isUseClose) {
        this(composite, title, OVERLAY_WIDTH, OVERLAY_HEIGHT, isUseClose);
    }

    public MoveBox(Control composite, String title, int width, int height) {
        this(composite, title, width, height, true);
    }

    public MoveBox(Control composite, String title, int width, int height, boolean isUseClose) {

        Objects.requireNonNull(composite);

        this.parentComposite = (Composite) composite;
        overlay_width = width;
        overlay_height = height;

        parents = new ArrayList<Composite>();
        Composite parent = parentComposite.getParent();

        while (parent != null) {
            parents.add(parent);
            parent = parent.getParent();
        }

        overlayShell = new Shell(parentComposite.getShell(), SWT.NONE);

        GridLayout gdLayout = new GridLayout(3, false);
        gdLayout.marginWidth = 0;
        gdLayout.marginHeight = 0;
        gdLayout.horizontalSpacing = 0;
        gdLayout.verticalSpacing = 0;
        overlayShell.setLayout(gdLayout);

        GridData gdata = new GridData(GridData.FILL_HORIZONTAL);
        if (isUseClose) {
            gdata.horizontalSpan = 2;
        } else {
            gdata.horizontalSpan = 3;
        }

        moveButton = new Button(overlayShell, SWT.NONE);
        moveButton.setAlignment(SWT.CENTER);
        moveButton.setText(title);
        moveButton.setLayoutData(gdata);
        moveButton.addMouseListener(
                new MouseListener() {

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
                    public void mouseDoubleClick(MouseEvent e) {}
                });

        moveButton.addMouseMoveListener(
                new MouseMoveListener() {

                    @Override
                    public void mouseMove(MouseEvent e) {
                        if (isMovePressed) {
                            if (e.x > 0) {
                                MovePosition(e.x - pressedPositonX, e.y - pressedPositonY);
                            } else {
                                MovePosition(e.x - pressedPositonX, e.y - pressedPositonY);
                            }
                        }
                    }
                });

        Button closeButton = new Button(overlayShell, SWT.ARROW);
        gdata = new GridData();
        gdata.exclude = !isUseClose;
        closeButton.setLayoutData(gdata);

        closeButton.addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        if (showing) {
                            remove();
                        }
                    }
                });
        closeButton.setVisible(isUseClose);

        showing = false;
        overlayShell.open();
        overlayShell.setVisible(showing);

        paintListener =
                new PaintListener() {
                    @Override
                    public void paintControl(PaintEvent e) {
                        reSize();
                    }
                };

        parentComposite.addDisposeListener(
                new DisposeListener() {

                    @Override
                    public void widgetDisposed(DisposeEvent e) {
                        remove();
                    }
                });
    }

    private void rePosition() {
        if (!parentComposite.isVisible()) {
            overlayShell.setBounds(new Rectangle(0, 0, 0, 0));
            return;
        }

        Point overlaySize = parentComposite.getSize();
        Point overlayDisplayLocation =
                parentComposite.toDisplay(
                        overlaySize.x - overlay_width - OVERLAY_WH_MARGIN, OVERLAY_WH_MARGIN);
        Rectangle overlayBounds =
                new Rectangle(
                        overlayDisplayLocation.x,
                        overlayDisplayLocation.y,
                        overlay_width,
                        overlay_height);

        Rectangle intersection = overlayBounds;

        for (Composite parent : parents) {

            Rectangle parentClientArea = parent.getClientArea();
            Point parentLocation = parent.toDisplay(parentClientArea.x, parentClientArea.y);
            Rectangle parentBounds =
                    new Rectangle(
                            parentLocation.x,
                            parentLocation.y,
                            parentClientArea.width,
                            parentClientArea.height);

            intersection = intersection.intersection(parentBounds);

            if (intersection.width == 0 || intersection.height == 0) {
                break;
            }
        }

        lastPositionX = intersection.x;
        lastPositionY = intersection.y;

        overlayShell.setBounds(intersection);
    }

    public void reSize() {

        if (!parentComposite.isVisible()) {
            overlayShell.setBounds(new Rectangle(0, 0, 0, 0));
            return;
        }

        Rectangle OverlayBounds =
                new Rectangle(lastPositionX, lastPositionY, overlay_width, overlay_height);

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

    public void show(int x, int y) {
        show();
    }

    public void show() {

        if (showing) {
            return;
        }

        rePosition();

        overlayShell.setVisible(true);

        for (Composite parent : parents) {
            if (parent.getClass().equals(Composite.class)) {
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

    public Shell getShell() {
        return this.overlayShell;
    }

    public void setTitleText(String title) {
        moveButton.setText(title);
    }

    public Composite getParent() {
        return parentComposite;
    }

    public void setOverlaySize(int positionX, int positionY, int width, int height) {
        lastPositionX = positionX;
        lastPositionY = positionY;
        setOverlaySize(width, height);
    }

    public void setOverlaySize(int width, int height) {
        overlay_width = width;
        overlay_height = height + moveButton.getSize().y;
        reSize();
    }

    public void setOverlaySizeWithOutMoveButton(int width, int height) {
        overlay_width = width;
        overlay_height = height;
        reSize();
    }

    public int getMoveButtonSizeY() {
        return moveButton.getSize().y;
    }
}
