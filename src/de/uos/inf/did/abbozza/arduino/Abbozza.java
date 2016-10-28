/**
 * @license abbozza!
 *
 * Copyright 2015 Michael Brinkmeier ( michael.brinkmeier@uni-osnabrueck.de )
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the Licenseo. You may obtain a copy
 * of the License at
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
 * @fileoverview The main class for the abbozza! server
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */
package de.uos.inf.did.abbozza.arduino;

import de.uos.inf.did.abbozza.handler.JarDirHandler;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import processing.app.Editor;
import processing.app.PreferencesData;
import processing.app.tools.Tool;

import com.sun.net.httpserver.*;
import de.uos.inf.did.abbozza.AbbozzaConfig;
import de.uos.inf.did.abbozza.AbbozzaConfigDialog;
import de.uos.inf.did.abbozza.AbbozzaLocale;
import de.uos.inf.did.abbozza.AbbozzaLogger;
import de.uos.inf.did.abbozza.AbbozzaServer;
import de.uos.inf.did.abbozza.arduino.handler.BoardHandler;
import de.uos.inf.did.abbozza.handler.CheckHandler;
import de.uos.inf.did.abbozza.handler.ConfigDialogHandler;
import de.uos.inf.did.abbozza.handler.ConfigHandler;
import de.uos.inf.did.abbozza.handler.LoadHandler;
import de.uos.inf.did.abbozza.handler.MonitorHandler;
import de.uos.inf.did.abbozza.handler.SaveHandler;
import de.uos.inf.did.abbozza.handler.TaskHandler;
import de.uos.inf.did.abbozza.handler.UploadHandler;
import de.uos.inf.did.abbozza.handler.VersionHandler;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import processing.app.debug.RunnerException;
import processing.app.helpers.PreferencesMapException;

public class Abbozza extends AbbozzaServer implements Tool, HttpHandler {

    public static Color COLOR = new Color(91, 103, 165);
    private static int counter;

    private Editor editor;

    public String runtimePath;

    @Override
    public void init(Editor editor) {
        this.editor = editor;
        super.init("arduino");        
    }

    @Override
    public void run() {
        // Do not start a second Abbozza instance!
        if (Abbozza.getInstance() != this) {
            return;
        }

        startServer();
        startBrowser("arduino.html");
    }

    @Override
    public void setPaths() {
        sketchbookPath = PreferencesData.get("sketchbook.path");
        configPath = sketchbookPath + "/tools/Abbozza/Abbozza.cfg";
        localJarPath = sketchbookPath + "/tools/Abbozza/tool/";
        globalJarPath = PreferencesData.get("runtime.ide.path") + "/";
        runtimePath = globalJarPath;
    }
 
    
    @Override
    public void registerSystemHandlers() {
        httpServer.createContext("/abbozza/board", new BoardHandler(this, false));
        httpServer.createContext("/abbozza/queryboard", new BoardHandler(this, true));
    }

    
    public void serialMonitor() {
        this.editor.handleSerial();
    }


    public void findJarsAndDirs(JarDirHandler jarHandler) {
        jarHandler.clear();
        jarHandler.addDir(sketchbookPath + "/tools/Abbozza/web", "Local directory");
        jarHandler.addJar(sketchbookPath + "/tools/Abbozza/tool/Abbozza.jar", "Local jar");
        jarHandler.addDir(runtimePath + "tools/Abbozza/web", "Global directory");
        jarHandler.addJar(runtimePath + "tools/Abbozza/tool/Abbozza.jar", "Global jar");
    }

    public void print(String message) {
        AbbozzaLogger.out(message);
    }

    public void processMessage(String message) {
        this.editor.getCurrentTab().setText(message);
        // this.editor.setText(message);
    }

    @Override
    public String getMenuTitle() {
        return "abbozza!";
    }

    public Editor getEditor() {
        return editor;
    }
    
    
    // Moves the arduino IDE window to the back
    @Override
    public void toolToBack() {
        editor.toBack();
    }

    @Override
    public void toolIconify() {
        editor.setState(JFrame.ICONIFIED);
        editor.setExtendedState(JFrame.ICONIFIED);
    }

    @Override
    public void toolSetCode(String code) {
        // editor.getSketch().getCurrentCode().setProgram(code);
        setEditorText(code);
        // editor.getSketch().getCurrentCode().setModified(true);        
    }

    private void setEditorText(final String code) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    editor.getCurrentTab().setText(code);
                    // editor.setText(code);
                }
            });
        } catch (InterruptedException ex) {
            Logger.getLogger(Abbozza.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Abbozza.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String compileCode(String code) {
        toolSetCode(code);        

        // Compile sketch                
        try {
            AbbozzaLogger.out(AbbozzaLocale.entry("msg.compiling"), AbbozzaLogger.INFO);
            editor.statusNotice("abbozza!: " + AbbozzaLocale.entry("msg.compiling"));
            //editor.getSketch().prepare();
            // editor.getSketch().build(false, false);
            editor.getSketchController().build(false, false);
            editor.statusNotice("abbozza!: " + AbbozzaLocale.entry("msg.done_compiling"));
            AbbozzaLogger.out(AbbozzaLocale.entry("msg.done_compiling"), AbbozzaLogger.INFO);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            editor.statusError(e);
            AbbozzaLogger.out(AbbozzaLocale.entry("msg.done_compiling"), AbbozzaLogger.INFO);
        }
        
        return logger.toString();
    }

    @Override
    public String uploadCode(String code) {
        // try {
        //     editor.getSketch().prepare();
        // } catch (IOException ioe) {
        //     ioe.printStackTrace(System.err);
        // }

        ThreadGroup group = Thread.currentThread().getThreadGroup();
        Thread[] threads = new Thread[group.activeCount()];
        group.enumerate(threads, false);

        monitorHandler.suspend();

        toolSetCode(code);        

        try {
            editor.statusNotice("abbozza!: " + AbbozzaLocale.entry("msg.compiling"));
            editor.handleExport(false);
            editor.statusNotice("abbozza!: " + AbbozzaLocale.entry("msg.done_compiling"));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            editor.statusError(e);
            AbbozzaLogger.out(AbbozzaLocale.entry("msg.done_compiling"), AbbozzaLogger.INFO);
        }
    
        Thread[] threads2 = new Thread[group.activeCount()];
        group.enumerate(threads2, false);

        // Find the exporting thread
        Thread last = null;
        int j;

        int i = threads2.length - 1;
        while ((i >= 0) && (last == null)) {

            j = threads.length - 1;
            while ((j >= 0) && (threads[j] != threads2[i])) {
                j--;
            }

            if (j < 0) {
                last = threads2[i];
            }
            i--;
        }
        
        // Wait for the termination of the export thread
        while ((last != null) && (last.isAlive())) {}
        
        return logger.toString();
    }
    
}

