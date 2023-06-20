package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jkiss.dbeaver.ext.turbographpp.graph.internal.GraphMessages;

public class GuideBox extends MoveBox {

    private static final int OVERLAY_WIDTH = 300;
    private static final int OVERLAY_HEIGHT = 90;
    public static final int MINIMAP_WIDTH = OVERLAY_WIDTH - 30;
    public static final int MINIMAP_HEIGHT = OVERLAY_HEIGHT;
    
    private Text textBox;
    private Combo propertyCombo;
    
    private Composite composite;
    
    public GuideBox(Control control, FXGraph graph) {
    	super(control, GraphMessages.guidebox_title);
        
    	composite = new Composite(this.getShell(), SWT.NONE);
    	GridData gd = new GridData();
        GridLayout layout1 = new GridLayout(2, false);
        layout1.marginHeight = 0;
        layout1.marginWidth = 0;
        layout1.horizontalSpacing = 5;
        layout1.verticalSpacing = 2;
        composite.setLayout(layout1);
        composite.setLayoutData(gd);
    	
        textBox = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
        gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        gd.verticalSpan = 3;
        //gd.heightHint = 300;
        textBox.setLayoutData(gd);
        
        Label label = new Label(composite, SWT.CENTER);
        label.setAlignment(SWT.CENTER);
        gd = new GridData();
        gd.horizontalIndent = 10;
        label.setLayoutData(gd);
        label.setText(GraphMessages.guidebox_properties_label);
        
        propertyCombo = new Combo(composite, SWT.READ_ONLY);
        propertyCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        propertyCombo.setEnabled(false);
        
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
    
    public void open() {
    	show();
    	setOverlaySize(composite.getSize().x, composite.getSize().y);
    }
}

