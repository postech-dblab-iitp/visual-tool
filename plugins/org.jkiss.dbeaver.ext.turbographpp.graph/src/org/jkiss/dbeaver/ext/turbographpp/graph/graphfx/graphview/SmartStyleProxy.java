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

    public static final String DEFAULT_EDGE = "-fx-stroke-width: 2;"
    		+ " -fx-stroke: #000000;"
    		+ " -fx-stroke-dash-array: 2 5 2 5;"
    		+ " -fx-fill: transparent;"
    		+ " -fx-stroke-line-cap: round;"
    		+ " -fx-opacity: 0.8;";
	
    public static final String HIGHLIGHT_EDGE = "-fx-stroke-width: 4;"
    		+ " -fx-stroke: #FF6D66;"
    		+ " -fx-stroke-dash-array: 2 5 2 5;"
    		+ " -fx-fill: transparent;"
    		+ " -fx-stroke-line-cap: round;"
    		+ " -fx-opacity: 0.8;";
    
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
    
}
