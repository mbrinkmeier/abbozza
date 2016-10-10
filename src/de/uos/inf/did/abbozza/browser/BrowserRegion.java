/**
 * @license abbozza!
 *
 * Copyright 2015 Michael Brinkmeier ( michael.brinkmeier@uni-osnabrueck.de )
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/**
 * @fileoverview ... @author michael.brinkmeier@uni-osnabrueck.de (Michael
 * Brinkmeier)
 */
package de.uos.inf.did.abbozza.browser;

import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import netscape.javascript.JSObject;

/**
 *
 * @author mbrinkmeier
 */
public class BrowserRegion extends Region {
    
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
     
    public BrowserRegion() {
        //apply the styles
        getStyleClass().add("browser");
        // Testing access
        
        JSObject window = (JSObject) webEngine.executeScript("window");
        LoggingBridge bridge = new LoggingBridge();
        window.setMember("java", bridge);
        webEngine.executeScript("console.log = function(message)\n" +
            "{\n" +
            "    java.log(message);\n" +
            "};");        
        webEngine.executeScript("console.err = function(message)\n" +
            "{\n" +
            "    java.err(message);\n" +
            "};");        
        // load the web page
        // System.out.println("Loading");
        webEngine.load("http://localhost:54242/arduino.html");
        // webEngine.load("file:///home/mbrinkmeier/Projekte/abbozza/lib/arduino.html");
        //add the web view to the scene
        getChildren().add(browser);
        // Worker worker = webEngine.getLoadWorker();
        // while (worker.isRunning()) {
        //     System.out.println(worker.getMessage());
        // } 
    }
        private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
 
    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }
 
    @Override protected double computePrefWidth(double height) {
        return 750;
    }
 
    @Override protected double computePrefHeight(double width) {
        return 500;
    }
}
