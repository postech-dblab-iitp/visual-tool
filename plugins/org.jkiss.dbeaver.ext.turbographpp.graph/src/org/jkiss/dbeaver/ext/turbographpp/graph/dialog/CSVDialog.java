package org.jkiss.dbeaver.ext.turbographpp.graph.dialog;

import java.time.LocalDate;
import java.time.LocalTime;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jkiss.dbeaver.ext.turbographpp.graph.internal.GraphMessages;

public class CSVDialog extends TitleAreaDialog {

	private Text FolderPath;
	private Text FileName;
    private Text txtNodeFileName;
    private Text txtEdgeFileName;

    private String folderPath;
    private String nodeFileName;
    private String edgeFileName;

    public CSVDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
        setTitle(GraphMessages.fxgraph_export_csv_dialog_title);
        setMessage(GraphMessages.fxgraph_export_csv_dialog_default_msg, IMessageProvider.INFORMATION);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(4, false);
        container.setLayout(layout);

        Label folderLabel = new Label(container, SWT.NONE);
        folderLabel.setText(GraphMessages.fxgraph_export_csv_dialog_label_folder);

        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = GridData.FILL;
        gd.horizontalSpan = 2;
        
        GridData gd1 = new GridData();
        gd1.grabExcessHorizontalSpace = true;
        gd1.horizontalAlignment = GridData.FILL;
        gd1.horizontalSpan = 3;

        FolderPath = new Text(container, SWT.BORDER);
        FolderPath.setLayoutData(gd);
        FolderPath.setEnabled(false);

        Button button = new Button(container, SWT.PUSH);
        button.setText("...");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog( parent.getShell(), SWT.NONE );
                dialog.setText(GraphMessages.fxgraph_export_csv_dialog_directory_dialog_title);
                String folder = dialog.open();
                if ( folder != null ) {
                	FolderPath.setText(folder);
                }
            }
        });
        
        Label fileLabel = new Label(container, SWT.NONE);
        fileLabel.setText(GraphMessages.fxgraph_export_csv_dialog_label_filename);
        
        FileName = new Text(container, SWT.BORDER);
        
        FileName.setText(LocalDate.now().toString() + "_" + LocalTime.now().getHour() + "_" + LocalTime.now().getMinute() + "_" + LocalTime.now().getSecond());
        FileName.setLayoutData(gd1);

        Label nodeLabel = new Label(container, SWT.NONE);
        nodeLabel.setText(GraphMessages.fxgraph_export_csv_dialog_label_node);

        txtNodeFileName = new Text(container, SWT.BORDER);
        txtNodeFileName.setLayoutData(gd1);
        txtNodeFileName.setEnabled(false);
        txtNodeFileName.setText(FileName.getText() + "_Node.csv");
        
        Label edgeLabel = new Label(container, SWT.NONE);
        edgeLabel.setText(GraphMessages.fxgraph_export_csv_dialog_label_edge);

        txtEdgeFileName = new Text(container, SWT.BORDER);
        txtEdgeFileName.setLayoutData(gd1);
        txtEdgeFileName.setEnabled(false);
        txtEdgeFileName.setText(FileName.getText() + "_Edge.csv");
        
        FileName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				txtNodeFileName.setText(FileName.getText() + "_Node.csv");
				txtEdgeFileName.setText(FileName.getText() + "_Edge.csv");
			}
		});
        
        return area;
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    // save content of the Text fields because they get disposed
    // as soon as the Dialog closes
    private void saveInput() {
    	folderPath = FolderPath.getText();
    	nodeFileName = txtNodeFileName.getText();
    	edgeFileName = txtEdgeFileName.getText();
    }

    @Override
    protected void okPressed() {
    	if (FolderPath.getText().isEmpty()) {
    		setMessage(GraphMessages.fxgraph_export_csv_dialog_error_msg_select_folder, IMessageProvider.ERROR);
    		return;
    	}
    	
    	if (FileName.getText().isEmpty()) {
    		setMessage(GraphMessages.fxgraph_export_csv_dialog_error_msg_inpt_filename, IMessageProvider.ERROR);
    		return;
    	}
    	
        saveInput();
        super.okPressed();
    }

    public String getFolderPath() {
        return folderPath;
    }
    
    public String getNodeFileName() {
        return nodeFileName;
    }

    public String getEdgeFileName() {
        return edgeFileName;
    }
}