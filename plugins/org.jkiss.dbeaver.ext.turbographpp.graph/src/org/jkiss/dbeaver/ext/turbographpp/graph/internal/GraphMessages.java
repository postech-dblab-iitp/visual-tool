/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2021 DBeaver Corp and others
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
package org.jkiss.dbeaver.ext.turbographpp.graph.internal;

import org.eclipse.osgi.util.NLS;

public class GraphMessages extends NLS {
	static final String BUNDLE_NAME = "org.jkiss.dbeaver.ext.turbographpp.graph.internal.GraphResources"; //$NON-NLS-1$

	public static String fxgraph_export_csv_dialog_title;
	public static String fxgraph_export_csv_dialog_default_msg;
	public static String fxgraph_export_csv_dialog_error_msg_select_folder;
	public static String fxgraph_export_csv_dialog_error_msg_inpt_filename;
	public static String fxgraph_export_csv_dialog_label_folder;
	public static String fxgraph_export_csv_dialog_label_filename;
	public static String fxgraph_export_csv_dialog_label_node;
	public static String fxgraph_export_csv_dialog_label_edge;
	public static String fxgraph_export_csv_dialog_directory_dialog_title;
	public static String shortest_guidebox_title;
	public static String shortest_guidebox_properties_label;
	public static String designbox_title; 
	public static String designbox_table_col_item;
	public static String designbox_table_col_value;
	public static String graphbox_title;
	public static String graphbox_radio_by_visualGraph;
	public static String graphbox_radio_by_all;
	public static String graphbox_radio_by_query;
	public static String valbox_title; 
	public static String valbox_table_value;
	public static String valbox_table_name;
	public static String valbox_copy_value;
	public static String valbox_copy_name;
	public static String valbox_tooltip_msg;

    static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, GraphMessages.class);
	}

	private GraphMessages() {
	}
}
