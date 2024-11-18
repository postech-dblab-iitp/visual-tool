/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2024 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ext.turbographpp.graph;

import com.equo.chromium.swt.Browser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class GraphBrowser {
    private FXGraph graphview;
    private Composite mainComposite;
    private Browser swtBrowser;
    private Button refreshButton;

    public GraphBrowser(Composite parent, FXGraph graph) {
        createBrowserArea(parent);
        graphview = graph;
    }

    protected Composite createBrowserArea(Composite parent) {
        mainComposite = new Composite(parent, SWT.BORDER);

        mainComposite.setLayout(new GridLayout(4, true));

        Label browserLabel = new Label(mainComposite, SWT.NONE);
        GridData data =
                new GridData(
                        GridData.HORIZONTAL_ALIGN_BEGINNING,
                        GridData.VERTICAL_ALIGN_CENTER,
                        false,
                        false);
        browserLabel.setText("Browser Url : ");
        browserLabel.setLayoutData(data);

        final Text browserUrl = new Text(mainComposite, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 3;
        browserUrl.setLayoutData(data);
        browserUrl.setText("http://localhost:3000");

        Label jsonLabel = new Label(mainComposite, SWT.NONE);
        data =
                new GridData(
                        GridData.HORIZONTAL_ALIGN_BEGINNING,
                        GridData.VERTICAL_ALIGN_CENTER,
                        false,
                        false);
        jsonLabel.setText("JsonData URL : ");
        jsonLabel.setLayoutData(data);

        final Text jsonUrl = new Text(mainComposite, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 2;
        jsonUrl.setLayoutData(data);
        jsonUrl.setText("http://localhost:3000/update-graph");

        Button send = new Button(mainComposite, SWT.PUSH);
        send.setText("Send");
        data = new GridData(GridData.FILL_HORIZONTAL);
        send.setLayoutData(data);
        send.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        String jUrl = jsonUrl.getText();
                        String bUrl = browserUrl.getText();
                        if (!jUrl.isEmpty()) {
                            graphview.sendJsonData(jUrl);
                        }
                        if (!bUrl.isEmpty()) {
                            swtBrowser.setUrl(bUrl);
                        }
                    }
                });

        refreshButton = new Button(mainComposite, SWT.PUSH);
        refreshButton.setText("Refresh");
        data =
                new GridData(
                        GridData.HORIZONTAL_ALIGN_END,
                        GridData.VERTICAL_ALIGN_CENTER,
                        false,
                        false);
        data.horizontalSpan = 4;
        refreshButton.addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        swtBrowser.refresh();
                    }
                });

        swtBrowser = new Browser(mainComposite, SWT.NONE);
        data = new GridData(GridData.FILL, GridData.FILL, true, true, 4, 1);
        data.widthHint = 800;
        data.heightHint = 600;
        swtBrowser.setLayoutData(data);

        return parent;
    }

    public Composite getComposite() {
        return mainComposite;
    }
}
