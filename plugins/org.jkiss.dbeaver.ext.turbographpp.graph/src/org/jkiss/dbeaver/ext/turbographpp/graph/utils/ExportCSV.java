package org.jkiss.dbeaver.ext.turbographpp.graph.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.jkiss.dbeaver.ext.turbographpp.graph.data.CyperEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CyperNode;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;

public class ExportCSV {

	public static boolean exportCSV(String folderPath, String nodeFileName, String edgeFilename, 
			Collection<Vertex<CyperNode>> vertices,
			Collection<FxEdge<CyperEdge, CyperNode>> edges) {
		try {
		    BufferedWriter nodeWriter = new BufferedWriter(new FileWriter(folderPath + File.separator + nodeFileName));
		    nodeWriter.write(writeNodes(vertices));
		    nodeWriter.close();
		    
		    BufferedWriter edgeWriter = new BufferedWriter(new FileWriter(folderPath + File.separator + edgeFilename));
		    edgeWriter.write(writeEdges(edges));
		    edgeWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	private static String writeEdges(Collection<FxEdge<CyperEdge, CyperNode>> edges) {
		StringBuilder strBuilder = new StringBuilder();
		
		LinkedHashMap<String,String> data = new LinkedHashMap<String, String>();
		
		if (!edges.isEmpty()) {
			Iterator<FxEdge<CyperEdge, CyperNode>> itr = edges.iterator();
			strBuilder.append("ID,Type,StartNodeID,EndNodeID");
			while(itr.hasNext()) {
				CyperEdge cyperEdge = itr.next().element();
				for (String key : cyperEdge.getProperties().keySet()) {
					data.put(key, "");
				}
			}
			
			for (String key : data.keySet()) {
				strBuilder.append("," + key);
			}
			
			strBuilder.append("\r\n");
			
			itr = edges.iterator();
			
			while(itr.hasNext()) {
				CyperEdge cyperEdge = itr.next().element();
				strBuilder.append(cyperEdge.getID() + ",");
				strBuilder.append(cyperEdge.getType() + ",");
				strBuilder.append(cyperEdge.getStartNodeID() + ",");
				strBuilder.append(cyperEdge.getEndNodeID());
				
				for (String key : data.keySet()) {
					data.put(key, "");
				}
				
				for (String key : cyperEdge.getProperties().keySet()) {
					data.replace(key, cyperEdge.getProperty(key));
				}
				
				for (String key : data.keySet()) {
					strBuilder.append("," + data.get(key));
				}
				
				strBuilder.append("\r\n");
			}
		}

		return strBuilder.toString();
	}
	
	private static String writeNodes(Collection<Vertex<CyperNode>> vertices) {
		StringBuilder strBuilder = new StringBuilder();
		
		LinkedHashMap<String,String> data = new LinkedHashMap<String, String>();
		
		if (!vertices.isEmpty()) {
			Iterator<Vertex<CyperNode>> itr = vertices.iterator();
			strBuilder.append("ID,Label");
			while(itr.hasNext()) {
				CyperNode cyperNode = itr.next().element();
				for (String key : cyperNode.getProperties().keySet()) {
					data.put(key, "");
				}
			}
			
			for (String key : data.keySet()) {
				strBuilder.append("," + key);
			}
			
			strBuilder.append("\r\n");
			
			itr = vertices.iterator();
			
			while(itr.hasNext()) {
				CyperNode cyperNode = itr.next().element();
				strBuilder.append(cyperNode.getID() + ",");
				strBuilder.append(cyperNode.getLabel());
				
				for (String key : data.keySet()) {
					data.put(key, "");
				}
				
				for (String key : cyperNode.getProperties().keySet()) {
					data.replace(key, cyperNode.getProperty(key).toString());
				}
				
				for (String key : data.keySet()) {
					strBuilder.append("," + data.get(key));
				}
				
				strBuilder.append("\r\n");
			}
		}
		
		return strBuilder.toString();
	}
	
}
