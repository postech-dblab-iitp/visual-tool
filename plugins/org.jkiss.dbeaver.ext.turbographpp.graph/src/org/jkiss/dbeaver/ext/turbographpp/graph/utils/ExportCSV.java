package org.jkiss.dbeaver.ext.turbographpp.graph.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.data.CypherNode;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.FxEdge;
import org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graph.Vertex;

public class ExportCSV {

	public static boolean exportCSV(String folderPath, String nodeFileName, String edgeFilename, 
			Collection<Vertex<CypherNode>> vertices,
			Collection<FxEdge<CypherEdge, CypherNode>> edges) {
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
	
	
	private static String writeEdges(Collection<FxEdge<CypherEdge, CypherNode>> edges) {
		StringBuilder strBuilder = new StringBuilder();
		
		LinkedHashMap<String,String> data = new LinkedHashMap<String, String>();
		
		if (!edges.isEmpty()) {
			Iterator<FxEdge<CypherEdge, CypherNode>> itr = edges.iterator();
			strBuilder.append("ID,Type,StartNodeID,EndNodeID");
			while(itr.hasNext()) {
				CypherEdge cyperEdge = itr.next().element();
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
				CypherEdge cyperEdge = itr.next().element();
				strBuilder.append(cyperEdge.getID() + ",");
				for (String type : cyperEdge.getTypes()) {
					strBuilder.append(type + ",");
					break;
				}
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
	
	private static String writeNodes(Collection<Vertex<CypherNode>> vertices) {
		StringBuilder strBuilder = new StringBuilder();
		
		LinkedHashMap<String,String> data = new LinkedHashMap<String, String>();
		
		if (!vertices.isEmpty()) {
			Iterator<Vertex<CypherNode>> itr = vertices.iterator();
			strBuilder.append("ID,Label");
			while(itr.hasNext()) {
				CypherNode cyperNode = itr.next().element();
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
				CypherNode cyperNode = itr.next().element();
				strBuilder.append(cyperNode.getID() + ",");
				for (String label : cyperNode.getLabels()) {
					strBuilder.append(label);
					break; //temp
				}
				
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
