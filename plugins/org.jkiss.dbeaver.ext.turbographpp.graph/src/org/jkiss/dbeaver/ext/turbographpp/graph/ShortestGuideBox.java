package org.jkiss.dbeaver.ext.turbographpp.graph;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jkiss.dbeaver.ext.turbographpp.graph.internal.GraphMessages;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.ui.UIIcon;

public class ShortestGuideBox extends MoveBox {

    private static final int OVERLAY_WIDTH = 300;
    private static final int OVERLAY_HEIGHT = 150;
    private static final int INFO_LOG_HEIGHT = 200;
    
    private Text textBox;
    private Button extendButton;
    private Combo propertyCombo;
    
    private Composite composite;
    
    private boolean isExtend = false;
    
    public ShortestGuideBox(Control control, FXGraph graph) {
    	super(control, GraphMessages.shortest_guidebox_title, OVERLAY_WIDTH, OVERLAY_HEIGHT, false);
        
    	composite = new Composite(this.getShell(), SWT.NONE);
    	GridData gd = new GridData();
    	gd.horizontalSpan = 3;
    	gd.horizontalAlignment = SWT.FILL;
    	gd.verticalAlignment = SWT.FILL;
        GridLayout layout1 = new GridLayout(2, false);
        layout1.marginHeight = 0;
        layout1.marginWidth = 0;
        layout1.verticalSpacing = 0;
        composite.setLayout(layout1);
        composite.setLayoutData(gd);
    	
        Label label = new Label(composite, SWT.CENTER);
        label.setAlignment(SWT.CENTER);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        label.setText(GraphMessages.shortest_guidebox_properties_label);
        
        propertyCombo = new Combo(composite, SWT.READ_ONLY);
        propertyCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        propertyCombo.setEnabled(false);
        
        textBox = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.READ_ONLY);
        gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        textBox.setLayoutData(gd);
        
        extendButton = new Button(composite, SWT.NONE);
        gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        gd.heightHint = 10;
        extendButton.setLayoutData(gd);
        extendButton.setImage(DBeaverIcons.getImage(UIIcon.ARROW_DOWN));
        extendButton.addSelectionListener(new SelectionAdapter(){
        	@Override
            public void widgetSelected(SelectionEvent e) {
        		GridData gd = (GridData)textBox.getLayoutData();
        		if (isExtend) {
        			gd.heightHint = label.getSize().y;
        			textBox.setLayoutData(gd);
        			extendButton.setImage(DBeaverIcons.getImage(UIIcon.ARROW_DOWN));
        		} else {
        			gd.heightHint = INFO_LOG_HEIGHT;
        			textBox.setLayoutData(gd);
        			extendButton.setImage(DBeaverIcons.getImage(UIIcon.ARROW_UP));
        		}
        		composite.getParent().layout(true);
        		setOverlaySize(composite.getSize().x, composite.getSize().y);
        		isExtend = !isExtend;
            }
        });
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

