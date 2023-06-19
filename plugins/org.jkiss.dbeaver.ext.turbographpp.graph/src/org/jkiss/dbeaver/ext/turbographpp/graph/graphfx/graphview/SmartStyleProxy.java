/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jkiss.dbeaver.ext.turbographpp.graph.graphfx.graphview;

import javafx.scene.shape.Shape;

/**
 * This class acts as a proxy for styling of nodes.
 * 
 * It essentially groups all the logic, avoiding code duplicate.
 * 
 * Classes that have this behavior can delegate the method calls to an instance
 * of this class.
 * 
 * @author brunomnsilva
 */
public class SmartStyleProxy implements SmartStylableNode {
	
	public static final String DEFAULT_VERTEX = "-fx-stroke-type: inside;";
    
	public static final String HIGHLIGHT_VERTEX = "-fx-stroke-width: 9;"
    		+ "    -fx-stroke: #FF0000;"
    		+ "    -fx-stroke-type: inside;";

	public static final String DEFAULT_EDGE_LINE_WEIGHT = "3";
	public static final String DEFAULT_EDGE_LINE_COLOR = "000000";
	public static final String DEFAULT_EDGE_LINE_STYLE = "4 4 4 4";
	public static final String DEFAULT_EDGE_LINE_STRENGTH = "0.2";
	
    public static final String DEFAULT_EDGE = 
    		"-fx-stroke-width: " + DEFAULT_EDGE_LINE_WEIGHT + ";"
    		+ " -fx-stroke: #" + DEFAULT_EDGE_LINE_COLOR + ";"
    		+ " -fx-stroke-dash-array: " + DEFAULT_EDGE_LINE_STYLE + ";"
    		+ " -fx-fill: transparent;"
    		+ " -fx-stroke-line-cap: round;"
    		+ " -fx-opacity: " + DEFAULT_EDGE_LINE_STRENGTH + ";";
	
    public static final String HIGHLIGHT_EDGE = "-fx-stroke-width: 5;"
    		+ " -fx-stroke: #FF6D66;"
    		+ " -fx-stroke-dash-array: 4 4 4 4;"
    		+ " -fx-fill: transparent;"
    		+ " -fx-stroke-line-cap: round;"
    		+ " -fx-opacity: 1.0;";
    
    public static final int DEFAULT_VERTEX_LABEL_SIZE = 8;
    public static final String DEFAULT_VERTEX_LABEL = "-fx-font: bold " 
    		+ DEFAULT_VERTEX_LABEL_SIZE 
    		+ "pt \"sans-serif\";";
    
    public static final int DEFAULT_EDGE_LABEL_SIZE = 5;
    public static final String DEFAULT_EDGE_LABEL = "-fx-font: normal " 
    		+ DEFAULT_EDGE_LABEL_SIZE 
    		+ "pt \"sans-serif\";";
    
    private final Shape client;
    
    public SmartStyleProxy(Shape client) {
        this.client = client;
    }
    
    @Override
    public void setStyle(String css) {
        client.setStyle(css);
    }

    @Override
    public void setStyleClass(String cssClass) {
        client.getStyleClass().clear();
        client.setStyle(null);
        client.getStyleClass().add(cssClass);
    }

    @Override
    public void addStyleClass(String cssClass) {
        client.getStyleClass().add(cssClass);
    }

    @Override
    public boolean removeStyleClass(String cssClass) {
        return client.getStyleClass().remove(cssClass);
    }
    
    public static String getEdgeStyleInputValue(String Color, String style, String weight) {
    	return "-fx-stroke-width: "+ DEFAULT_EDGE_LINE_WEIGHT + ";"
        		+ " -fx-stroke: #" + Color + ";"
        		+ " -fx-stroke-dash-array: " + style + ";"
        		+ " -fx-fill: transparent;"
        		+ " -fx-stroke-line-cap: round;"
        		+ " -fx-opacity: " + weight + ";";
    }
}
