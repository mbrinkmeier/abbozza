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
package de.uos.inf.did.abbozza.handler;

import com.sun.net.httpserver.HttpExchange;
import de.uos.inf.did.abbozza.AbbozzaLogger;
import de.uos.inf.did.abbozza.arduino.Abbozza;
import de.uos.inf.did.abbozza.AbbozzaServer;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author mbrinkmeier
 */
public class LoadHandler extends AbstractHandler {
    
    public LoadHandler(AbbozzaServer abbozza) {
        super(abbozza);
    }

    @Override
    public void handle(HttpExchange exchg) throws IOException {
        try {
            String query = exchg.getRequestURI().getQuery();
            if ( query == null ) {
                String sketch = loadSketch();
                this.sendResponse(exchg, 200, "text/xml", sketch);
            } else {
                AbbozzaLogger.out("loadHandler: load " + query, AbbozzaLogger.DEBUG);
                String sketch = loadSketch(query);
                this.sendResponse(exchg, 200, "text/xml", sketch);
            }
        } catch (IOException ioe) {
            this.sendResponse(exchg, 404, "", "");
        }
    }

    public String loadSketch() throws IOException {
        String result = "";
        File lastSketchFile = _abbozzaServer.getLastSketchFile();
        BufferedReader reader;
        String path = ((lastSketchFile != null) ? lastSketchFile.getAbsolutePath() : _abbozzaServer.getSketchbookPath());
        JFileChooser chooser = new JFileChooser(path) {
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                // config here as needed - just to see a difference
                dialog.setLocationByPlatform(true);
                // might help - can't know because I can't reproduce the problem
                dialog.setAlwaysOnTop(true);
                return dialog;
            }
        };
        
        // Prepare accessory-panel
        LoadHandlerPanel panel = new LoadHandlerPanel(chooser);
        chooser.setAccessory(panel);
        chooser.addPropertyChangeListener(panel);

        chooser.setFileFilter(new FileNameExtensionFilter("abbozza! Sketches und Aufgabenarchive (*.abz, *.abj)", "abz","abj"));
        chooser.setSelectedFile(lastSketchFile);
        int choice = chooser.showOpenDialog(null);
        if ((choice == JFileChooser.APPROVE_OPTION) || (panel.getUrl() != null)) {
            URL url;
            if (panel.getUrl() != null ) {
                url = new URL(panel.getUrl());
            } else {
                File file = chooser.getSelectedFile();                
                url = file.toURI().toURL();
            }
            
            if ( url.toString().endsWith("abj") ) {
                    result = getStartFromAbj(url);
            } else {
                    result = getSketchFromFile(url); 
            }
            
            if (panel.applyOptions()) {
                Abbozza.getConfig().apply(panel.getOptions());
            }            
        } else {
            throw new IOException();
        }
        _abbozzaServer.toolIconify();
        return result;
    }

    
    public String loadSketch(String path) throws IOException {
        String result = "";
        URL url;
        
        // Check path
        try {
            url = new URL(path);
            AbbozzaLogger.out("LoadHandler: loading from given url " + path ,AbbozzaLogger.DEBUG);                
            if ( path.endsWith("abj")) {
                path = "jar:" + url.toString() + "!/start.abz";
                url = new URL(path);
            }
        } catch (MalformedURLException ex) {
            // Interpret path as path to local file
            // If path is absolute
            if ( path.startsWith("/")) {
                AbbozzaLogger.out("LoadHandler: loading from absolute path " + path ,AbbozzaLogger.DEBUG);                
                url = new URL("file://" + path);
            } else {
                AbbozzaLogger.out("LoadHandler: loading from relative path " + path ,AbbozzaLogger.DEBUG);                
                String basePath = _abbozzaServer.getTaskAnchor();
                AbbozzaLogger.out("LoadHandler: using anchor " + basePath ,AbbozzaLogger.DEBUG);                
                if (basePath == null) {
                    basePath = _abbozzaServer.getSketchbookPath();
                }                
                url = new URL(basePath + "/" + path);
            }
        }
        AbbozzaLogger.out("LoadHandler: load " + url.toString(),AbbozzaLogger.DEBUG);
        String u = url.toString();
        String anchor = u.substring(0,u.lastIndexOf('/'));
        AbbozzaLogger.out("LoadHandler: load anchor " + anchor,AbbozzaLogger.DEBUG);
        _abbozzaServer.setTaskAnchor(anchor);

        URLConnection conn = url.openConnection();
        InputStream inStream = conn.getInputStream();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        
        while (reader.ready()) {
            result = result + reader.readLine()+"\n";
        }
                
        return result;                
    }

    
        private String getStartFromAbj(URL abj) throws MalformedURLException {
        String result = "";
        try {
            URL url = new URL("jar:" + abj.toString() + "!/start.abz");
            AbbozzaLogger.out("LoadHandler: Open abj " + url.toString(),AbbozzaLogger.DEBUG);
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            while (reader.ready()) {
                 result = result + reader.readLine() + '\n';
             }
            reader.close();
            // _abbozzaServer.setLastSketchFile(abj.toString());
            _abbozzaServer.setTaskAnchor("jar:" + abj.toString() + "!");
        } catch (Exception ex) {
            AbbozzaLogger.err("LoadHandler: Could not open " + abj.toString());
            return null;
        }
        return result;
    }

    private String getSketchFromFile(URL abz) throws FileNotFoundException, IOException {
        String result = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(abz.openStream()));
        while (reader.ready()) {
            result = result + reader.readLine() + '\n';
        }
        reader.close();
        // _abbozzaServer.setLastSketchFile(file);
        _abbozzaServer.setTaskAnchor(abz.toString().substring(0,abz.toString().lastIndexOf('/')));
        return result;
    }

    
    private String getStartFromAbj(File file) throws MalformedURLException {
        String result = "";
        try {
            URL url = new URL("jar:file://" + file.getCanonicalPath() + "!/start.abz");
            AbbozzaLogger.out("LoadHandler: Open abj " + url.toString(),AbbozzaLogger.DEBUG);
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            while (reader.ready()) {
                 result = result + reader.readLine() + '\n';
             }
            reader.close();
            _abbozzaServer.setLastSketchFile(file);
            _abbozzaServer.setTaskAnchor("jar:file://" + file.getCanonicalPath() + "!");
        } catch (Exception ex) {
            AbbozzaLogger.err("LoadHandler: Could not open " + file.getPath());
            return null;
        }
        return result;
    }

    private String getSketchFromFile(File file) throws FileNotFoundException, IOException {
        String result = "";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            result = result + reader.readLine() + '\n';
        }
        reader.close();
        _abbozzaServer.setLastSketchFile(file);
        _abbozzaServer.setTaskAnchor("file://" + file.getParentFile().getCanonicalPath());
        return result;
    }

}
