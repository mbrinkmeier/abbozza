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

    // public String sketchbookPath;
    public String runtimePath;
    // public AbbozzaErrMonitor errMonitor;
    // private AbbozzaMonitor monitor = null;

    // private TaskHandler taskHandler;

    // private Properties config;

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
        // httpServer.createContext("/abbozza/load", new LoadHandler(this));
        // httpServer.createContext("/abbozza/save", new SaveHandler(this));
        // httpServer.createContext("/abbozza/check", new CheckHandler(this));
        // httpServer.createContext("/abbozza/upload", new UploadHandler(this));
        // httpServer.createContext("/abbozza/config", new ConfigHandler(this));
        // httpServer.createContext("/abbozza/frame", new ConfigDialogHandler(this));
        httpServer.createContext("/abbozza/board", new BoardHandler(this, false));
        httpServer.createContext("/abbozza/queryboard", new BoardHandler(this, true));
        // this.monitorHandler = new MonitorHandler(this);
        // httpServer.createContext("/abbozza/monitor", monitorHandler);
        // httpServer.createContext("/abbozza/monitorresume", monitorHandler);
        // httpServer.createContext("/abbozza/version", new VersionHandler(this));
        // httpServer.createContext("/abbozza/", this /* handler */);
        // httpServer.createContext("/task/", new TaskHandler(this));
    }

    /*    protected void checkSketch() {
     if (!editor.isVisible()) {
     AbbozzaLogger.out("Editor not visible. Making it visible again.", AbbozzaLogger.INFO);
     editor.setVisible(true);
     }
     // @TODO Check if the current sketch is read only and open a new sketch
     if (editor.getSketch().isReadOnly(BaseNoGui.librariesIndexer.getInstalledLibraries(), BaseNoGui.getExamplesPath())) {
     AbbozzaLogger.out("Current Sketch is read only", AbbozzaLogger.INFO);
     Base.INSTANCE.handleNewReplace();
     }
     }
     */
    
    /*
    public String uploadCode(String code) {
        // System.out.println("hier");

        logger.reset();

        String response;
        boolean flag = PreferencesData.getBoolean("editor.save_on_verify");
        
        // PreferencesData.setBoolean("editor.save_on_verify", false);
        //
        // editor.getSketch().getCurrentCode().setProgram(code);
        // setEditorText(code);
        // 
        // editor.getSketch().getCurrentCode().setModified(true);
        

        try {
            editor.getSketch().prepare();
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }

        ThreadGroup group = Thread.currentThread().getThreadGroup();
        Thread[] threads = new Thread[group.activeCount()];
        group.enumerate(threads, false);

        this.monitorHandler.suspend();

        // try {
        // editor.getSketch().save();
        editor.handleExport(false);
        // } catch (IOException ex) {
        // }

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

        response = logger.toString();

        PreferencesData.setBoolean("editor.save_on_verify", flag);
        return response;
    }
    */
    
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
        // editor.getSketch().getCurrentCode().setModified(true);
        
        // Compile sketch                
        try {
            AbbozzaLogger.out(AbbozzaLocale.entry("msg.compiling"), AbbozzaLogger.INFO);
            editor.statusNotice("abbozza!: " + AbbozzaLocale.entry("msg.compiling"));
            //editor.getSketch().prepare();
            // editor.getSketch().build(false, false);
            editor.getSketchController().build(false, false);
            editor.statusNotice("abbozza!: " + AbbozzaLocale.entry("msg.done_compiling"));
            AbbozzaLogger.out(AbbozzaLocale.entry("msg.done_compiling"), AbbozzaLogger.INFO);
        } catch (IOException | RunnerException  | PreferencesMapException e) {
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

        // try {
        // editor.getSketch().save();
        editor.handleExport(false);
        // } catch (IOException ex) {
        // }

        AbbozzaLogger.out("Hier",4);
        
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
//    public AbbozzaMonitor getMonitor() {
//        return monitor;
//    }
//    public void suspendMonitor() {
//        try {
//            if (monitor != null) {
//                monitor.suspend();
//            }
//
//            editor.getSketch().save();
//            editor.handleExport(false);
//        } catch (Exception ex) {
//        }
//    }
/**
 *
 * @return ctions
 */
//    public boolean openMonitor() {
//
//        if (monitor != null) {
//            if (resumeMonitor()) {
//                monitor.toFront();
//                return true;
//            } else {
//                return false;
//            }
//        }
//
//        BoardPort port = Base.getDiscoveryManager().find(PreferencesData.get("serial.port"));
//        monitor = new AbbozzaMonitor(port);
//        try {
//            monitor.open();
//            monitor.setVisible(true);
//            monitor.toFront();
//            monitor.setAlwaysOnTop(true);
//        } catch (Exception ex) {
//            ex.printStackTrace(System.out);
//            return false;
//        }
//        return true;
//    }
//
//    public boolean resumeMonitor() {
//        if (monitor == null) {
//            return false;
//        }
//        try {
//            monitor.resume();
//        } catch (Exception ex) {
//            return false;
//        }
//        return true;
//    }

/*    public void sendResponse(HttpExchange exchg, int code, String type, String response) throws IOException {
 byte[] buf = response.getBytes();
 OutputStream out = exchg.getResponseBody();
 Headers responseHeaders = exchg.getResponseHeaders();
 responseHeaders.set("Content-Type", type);
 exchg.sendResponseHeaders(code, buf.length);
 out.write(buf);
 out.close();
 } */
//    public boolean connectToBoard(HttpExchange exchg, boolean query) {
//        String port = null;
//        String board = null;
//        List<BoardPort> ports = Base.getDiscoveryManager().discovery();
//        for (int i = 0; i < ports.size(); i++) {
//            AbbozzaLogger.out("port " + ports.get(i).getAddress() + " " + ports.get(i).getLabel() + " " + ports.get(i).getBoardName(), AbbozzaLogger.INFO);
//            if (ports.get(i).getBoardName() != null) {
//                port = ports.get(i).getAddress();
//                board = ports.get(i).getBoardName();
//                AbbozzaLogger.out("Found '" + board + "' on " + port);
//
//                BaseNoGui.selectSerialPort(port);
//
//                TargetPlatform platform = BaseNoGui.getTargetPlatform();
//                for (TargetBoard targetBoard : platform.getBoards().values()) {
//                    AbbozzaLogger.out(">> " + targetBoard.getName() + " == " + board);
//                    if (targetBoard.getName().equals(board)) {
//                        BaseNoGui.selectBoard(targetBoard);
//                    }
//                }
//
//                Base.INSTANCE.onBoardOrPortChange();
//            }
//        }
//
//        TargetBoard targetBoard = BaseNoGui.getTargetBoard();
//        TargetPlatform platform = BaseNoGui.getTargetPlatform();
//        AbbozzaLogger.out("targetBoard: " + targetBoard.getId(), AbbozzaLogger.INFO);
//
//        try {
//            if (board != null) {
//                AbbozzaLogger.out("board found " + targetBoard.getId() + " " + targetBoard.getName() + " " + port, AbbozzaLogger.INFO);
//                sendResponse(exchg, 200, "text/plain", targetBoard.getId() + "|" + targetBoard.getName() + "|" + port);
//                return true;
//            } else {
//                AbbozzaLogger.out("no board found", AbbozzaLogger.INFO);
//
//                if (query == false) {
//                    AbbozzaLogger.out("IDE set to : " + targetBoard.getId() + " " + targetBoard.getName() + " " + port, AbbozzaLogger.INFO);
//                    sendResponse(exchg, 201, "text/plain", targetBoard.getId() + "|" + targetBoard.getName() + "|" + port);
//                    return false;
//                } else {
//                    // Cycle through all packages
//                    Vector<BoardListEntry> boards = new Vector<BoardListEntry>();
//
//                    for (TargetPackage targetPackage : BaseNoGui.packages.values()) {
//                        // For every package cycle through all platform
//                        for (TargetPlatform targetPlatform : targetPackage.platforms()) {
//
//                            // Add a title for each platform
//                            String platformLabel = targetPlatform.getPreferences().get("name");
//                            if (platformLabel != null && !targetPlatform.getBoards().isEmpty()) {
//
//                                for (TargetBoard tboard : targetPlatform.getBoards().values()) {
//                                    boards.add(new BoardListEntry(tboard));
//                                }
//                            }
//                        }
//                    }
//                    BoardListEntry result = (BoardListEntry) JOptionPane.showInputDialog(null, AbbozzaLocale.entry("msg.select_board"), AbbozzaLocale.entry("msg.no_board"), JOptionPane.PLAIN_MESSAGE, null, boards.toArray(), BaseNoGui.getTargetBoard());
//                    if (result != null) {
//                        AbbozzaLogger.out("selected : " + result.getId(), AbbozzaLogger.INFO);
//                        sendResponse(exchg, 201, "text/plain", result.getId() + "|" + result.getName() + "|???");
//                        BaseNoGui.selectBoard(result.getBoard());
//                        Base.INSTANCE.onBoardOrPortChange();
//                    } else {
//                        AbbozzaLogger.out("IDE set to : " + targetBoard.getId() + " " + targetBoard.getName() + " " + port, AbbozzaLogger.INFO);
//                        sendResponse(exchg, 201, "text/plain", targetBoard.getId() + "|" + targetBoard.getName() + "|" + port);
//                        return false;
//                    }
//                    return false;
//                }
//            }
//        } catch (IOException ex) {
//            return false;
//        }
//    }

/*
 public String setCode(String code) {     
 logger.reset();
        
 String response;
 boolean flag = PreferencesData.getBoolean("editor.save_on_verify");
 PreferencesData.setBoolean("editor.save_on_verify", false);
        
 editor.getSketch().getCurrentCode().setProgram(code);
 setEditorText(code);
                
 editor.getSketch().getCurrentCode().setModified(true);
        
 try {
 AbbozzaLogger.out(AbbozzaLocale.entry("msg.compiling"), AbbozzaLogger.INFO);
 editor.statusNotice("abbozza!: " + AbbozzaLocale.entry("msg.compiling"));
 editor.getSketch().prepare();
 editor.getSketch().save();
 editor.getSketch().build(false, false);
 editor.statusNotice("abbozza!: " + AbbozzaLocale.entry("msg.done_compiling"));
 AbbozzaLogger.out(AbbozzaLocale.entry("msg.done_compiling"), AbbozzaLogger.INFO);
 } catch (IOException | RunnerException | PreferencesMapException e) {
 e.printStackTrace(System.out);
 editor.statusError(e);
 AbbozzaLogger.out(AbbozzaLocale.entry("msg.done_compiling"), AbbozzaLogger.INFO);
 }
        
        
 response = logger.toString();
 PreferencesData.setBoolean("editor.save_on_verify", flag);
        
 return response;
 }
 */
