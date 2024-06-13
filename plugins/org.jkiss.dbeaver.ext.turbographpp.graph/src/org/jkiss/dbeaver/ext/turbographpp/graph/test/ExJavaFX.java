package org.jkiss.dbeaver.ext.turbographpp.graph.test;

import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ExJavaFX {

    private Button button;

    public ExJavaFX(Composite parent, int style) {
        FXCanvas canvas = new FXCanvas(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).span(3, 1).applyTo(canvas);

        BorderPane layout = new BorderPane();

        Scene scene =
                new Scene(
                        layout,
                        Color.rgb(
                                parent.getShell().getBackground().getRed(),
                                parent.getShell().getBackground().getGreen(),
                                parent.getShell().getBackground().getBlue()));

        canvas.setScene(scene);

        javafx.scene.control.Label output = new javafx.scene.control.Label();
        layout.setCenter(output);

        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), output);
        rotateTransition.setByAngle(360);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), output);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(4.0);
        scaleTransition.setToY(4.0);

        button = new Button(parent, SWT.None);
        button.setText("Test");
    }
}
