/**
 * @license abbozza!
 *
 * Copyright 2015 Michael Brinkmeier ( michael.brinkmeier@uni-osnabrueck.de )
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the Licenseo. You may obtain a copy of
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
 * @fileoverview The main class for the abbozza! server
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */
package de.uos.inf.did.abbozza;

import cc.arduino.packages.BoardPort;
import de.uos.inf.did.abbozza.monitor.AbbozzaMonitor;
import java.awt.Color;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.jar.JarFile;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import processing.app.Editor;
import processing.app.PreferencesData;
import processing.app.debug.RunnerException;
import processing.app.helpers.PreferencesMapException;
import processing.app.tools.Tool;

import com.sun.net.httpserver.*;
import java.awt.Component;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.MenuElement;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import processing.app.Base;
import processing.app.BaseNoGui;
// import processing.app.I18n.*;
import processing.app.debug.TargetBoard;
import processing.app.debug.TargetPackage;
import processing.app.debug.TargetPlatform;

public class Abbozza implements Tool, HttpHandler {

    public static final int VER_MAJOR = 0;
    public static final int VER_MINOR = 4;
    public static final int VER_REV = 1;
    public static final String VER_REM = "(pre-alpha)";
    
    public static final String VERSION = "" + VER_MAJOR + "." + VER_MINOR + "." + VER_REV + " " + VER_REM;
    
    private static Abbozza instance;
    public static Color COLOR = new Color(91, 103, 165);
    private static int counter;

    private Editor editor;
    public ByteArrayOutputStream logger;
    private DuplexPrintStream duplexer;
    public String sketchbookPath;
    public String runtimePath;
    // public AbbozzaErrMonitor errMonitor;
    private HttpServer httpServer;
    private int serverPort;
    private boolean isStarted = false;
    private AbbozzaMonitor monitor = null;
    private AbbozzaConfig config = null;

    private JarFile jarLocal;
    private JarFile jarGlobal;
    private File webDirLocal;
    private File webDirGlobal;

    private JarDirHandler jarHandler;

    private File lastSketchFile = null;

    // private Properties config;
    private String prefix = "/abbozza";

    @Override
    public void init(Editor editor) {
        // If there is already an Abbozza instance, silently die
        if (Abbozza.instance != null) {
            return;
        }

        // Set static instance
        Abbozza.instance = this;
          
        // Do some checks in advance
        runtimePath = PreferencesData.get("runtime.ide.path");
        sketchbookPath = PreferencesData.get("sketchbook.path");

        jarHandler = new JarDirHandler();
        findJarsAndDirs(jarHandler);

        // Load Configuration from preferences in local directory
        config = new AbbozzaConfig(sketchbookPath);        
        AbbozzaLocale.setLocale(config.getLocale());

        // AbbozzaLogger.setLevel(AbbozzaLogger.ALL);
        AbbozzaLogger.out("Version " + VERSION, AbbozzaLogger.INFO);

        if ( this.getConfig().getUpdate() ) checkForUpdate(false);


        this.editor = editor;
        
        // AbbozzaLocale.setLocale("de_DE");
        AbbozzaLogger.out(AbbozzaLocale.entry("msg.loaded"), AbbozzaLogger.INFO);

        if (config.startAutomatically()) {
            this.startServer();
            if (config.startBrowser()) {
                this.startBrowser();
            }
        }
    }

    @Override
    public void run() {
        // Do not start a second Abbozza instance!
        if (Abbozza.instance != this) {
            return;
        }

        startServer();
        startBrowser();

    }

    public void checkForUpdate(boolean reportNoUpdate) {
        
        String updateUrl = Abbozza.getConfig().getUpdateUrl();
        String version = "";
        
        int major;
        int minor;
        int rev;
        
        try {
            URL url = new URL(updateUrl+"VERSION");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            version = br.readLine();
            br.close();
            int pos = version.indexOf('.');
            major = Integer.parseInt(version.substring(0, pos));
            int pos2 = version.indexOf('.',pos+1);
            minor = Integer.parseInt(version.substring(pos+1, pos2));
            rev = Integer.parseInt(version.substring(pos2+1));
	} catch (Exception ex) {
            AbbozzaLogger.out("Could not check update version", AbbozzaLogger.INFO);
            return;
	}
        
        
        AbbozzaLogger.out("Checking for update at " + updateUrl, AbbozzaLogger.INFO);
        AbbozzaLogger.out("Update version " + major + "." + minor + "." + rev, AbbozzaLogger.INFO);
        
        if (  (major > VER_MAJOR) || 
             ((major == VER_MAJOR) && ( minor > VER_MINOR)) || 
             ((major == VER_MAJOR) && (minor == VER_MINOR) && (rev > VER_REV)) ) {
            AbbozzaLogger.out("New version found", AbbozzaLogger.INFO);
            int res = JOptionPane.showOptionDialog(null, AbbozzaLocale.entry("gui.new_version",version), AbbozzaLocale.entry("gui.new_version_title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if ( res == JOptionPane.NO_OPTION ) return;
            URL url;
            try {
                // Rename current jar
                // AbbozzaLogger.out(this.getSketchbookPath(),AbbozzaLogger.ALL);
                File cur = new File(this.getSketchbookPath()+"/tools/Abbozza/tool/Abbozza.jar");
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                String today = format.format(new Date());
                File dir = new File(this.getSketchbookPath()+"/tools/Abbozza/tool/old");
                if ( !dir.exists() ) {
                    AbbozzaLogger.out("Creating directory " + dir.getPath(), AbbozzaLogger.INFO);
                    dir.mkdir();
                }
                AbbozzaLogger.out("Moving old version to " + dir.getPath() + "/Abbozza." + today + ".jar", AbbozzaLogger.INFO);
                cur.renameTo(new File(this.getSketchbookPath()+"/tools/Abbozza/tool/old/Abbozza." + today + ".jar"));
                AbbozzaLogger.out("Downloading version " + version, AbbozzaLogger.INFO);
                url = new URL(updateUrl+"Abbozza.jar");
                URLConnection conn = url.openConnection();
                byte buffer[] = new byte[4096];
                int n = -1;
                InputStream ir = conn.getInputStream();
                FileOutputStream ow = new FileOutputStream(new File(this.getSketchbookPath()+"/tools/Abbozza/tool/Abbozza.jar"));
                while ( (n=ir.read(buffer)) != -1 ) {
                    ow.write(buffer,0,n);
                }
                ow.close();
                ir.close();
                AbbozzaLogger.out("Stopping arduino", AbbozzaLogger.INFO);
                System.exit(0);
            } catch (Exception ex) {
                Logger.getLogger(Abbozza.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            AbbozzaLogger.out(AbbozzaLocale.entry("gui.no_update"),AbbozzaLogger.INFO);
            if ( reportNoUpdate ) {
                JOptionPane.showMessageDialog(null, AbbozzaLocale.entry("gui.no_update"));
            }
        }
    }
    
    
    public void startServer() {

        if ((!isStarted) && (Abbozza.instance == this)) {

            this.isStarted = true;

            AbbozzaLogger.out("Starting ... ");

            // Start ErrorMonitor
            logger = new ByteArrayOutputStream();
            duplexer = new DuplexPrintStream(logger, System.err);
            System.setErr(duplexer);

            // errMonitor = new AbbozzaErrMonitor(logger);
            // errMonitor.start();

            serverPort = config.getServerPort();
            while (httpServer == null) {
                try {
                    httpServer = HttpServer.create(new InetSocketAddress(serverPort), 0);
                    httpServer.createContext("/abbozza/", this /* handler */);
                    httpServer.createContext("/", jarHandler);
                    httpServer.start();
                    AbbozzaLogger.out("Http-server started on port: " + serverPort, AbbozzaLogger.INFO);
                } catch (Exception e) {
                    serverPort++;
                    httpServer = null;
                }
            }

            AbbozzaLogger.out("abbozza: " + AbbozzaLocale.entry("msg.server_started", Integer.toString(config.getServerPort()) ));

            String url = "http://localhost:" + config.getServerPort() + "/abbozza.html";
            AbbozzaLogger.out("abbozza: " + AbbozzaLocale.entry("msg.server_reachable", url));
        }

    }

    /**
     * Request handling
     *
     * @param exchg
     * @throws java.io.IOException
     */
    @Override
    public void handle(HttpExchange exchg) throws IOException {
        String path = exchg.getRequestURI().getPath();
        OutputStream os = exchg.getResponseBody();

        AbbozzaLogger.out(path + " requested");

        if (!path.startsWith(prefix)) {
            String result = AbbozzaLocale.entry("msg.not_found", path);

            exchg.sendResponseHeaders(400, result.length());
            os.write(result.getBytes());
            os.close();
        } else {
            if (path.equals(prefix + "/load")) {
                try {
                    String sketch = loadSketch();
                    sendResponse(exchg, 200, "text/xml", sketch);
                } catch (IOException ioe) {
                    sendResponse(exchg, 404, "", "");
                }
            } else if (path.equalsIgnoreCase(prefix + "/save")) {
                try {
                    saveSketch(exchg.getRequestBody());
                    sendResponse(exchg, 200, "text/xml", "saved");
                } catch (IOException ioe) {
                    sendResponse(exchg, 404, "", "");
                }
            } else if (path.equalsIgnoreCase(prefix + "/check")) {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(exchg.getRequestBody()));
                    StringBuffer code = new StringBuffer();
                    while (in.ready()) {
                        code.append(in.readLine());
                        code.append('\n');
                    }
                    String response = setCode(code.toString());
                    if (response.equals("") ) {
                        sendResponse(exchg, 200, "text/plain", AbbozzaLocale.entry("msg.done-compiling"));
                    } else {
                        sendResponse(exchg, 400, "text/plain", response);                        
                    }
                } catch (IOException ioe) {
                    sendResponse(exchg, 404, "", "");
                }
            } else if (path.equals(prefix + "/upload")) {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(exchg.getRequestBody()));
                    StringBuffer code = new StringBuffer();
                    while (in.ready()) {
                        code.append(in.readLine());
                        code.append('\n');
                    }
                    String response = uploadCode(code.toString());
                    if ( response.equals("") ) {
                        sendResponse(exchg, 200, "text/plain", AbbozzaLocale.entry("msg.done-compiling"));                        
                    } else {
                        sendResponse(exchg, 400, "text/plain", response);                                                
                    }
                } catch (IOException ioe) {
                    try {
                        sendResponse(exchg, 404, "", "");
                    } catch (IOException ioe2) {
                    }
                }
            } else if (path.equals(prefix + "/config")) {
                sendResponse(exchg, 200, "text/plain", config.get().toString());
            } else if (path.equalsIgnoreCase(prefix + "/frame")) {
                Properties props = config.get();
                AbbozzaConfigDialog dialog = new AbbozzaConfigDialog(props, null, true);
                dialog.setAlwaysOnTop(true);
                dialog.setModal(true);
                dialog.toFront();
                dialog.setVisible(true);
                this.editor.setState(JFrame.ICONIFIED);
                this.editor.setExtendedState(JFrame.ICONIFIED);
                if (dialog.getState() == 0) {
                    config.set(dialog.getConfiguration());
                    AbbozzaLocale.setLocale(config.getLocale());
                    AbbozzaLogger.out("closed with " + config.getLocale());
                    config.write();
                    sendResponse(exchg, 200, "text/plain", config.get().toString());
                } else {
                   sendResponse(exchg, 440, "text/plain", "");
                }
            } else if (path.equals(prefix + "/board")) {
                connectToBoard(exchg,false);
            } else if (path.equals(prefix + "/queryboard")) {
                connectToBoard(exchg,true);
            } else if (path.equals(prefix + "/monitor")) {
                if (openMonitor()) {
                    sendResponse(exchg, 200, "text/plain", "");
                } else {
                    sendResponse(exchg, 440, "text/plain", "");
                }
            } else if (path.equals(prefix + "/monitor_resume")) {
                if (resumeMonitor()) {
                    sendResponse(exchg, 200, "text/plain", "");
                } else {
                    sendResponse(exchg, 440, "text/plain", "");
                }
            } else if (path.equals(prefix + "/version")) {
                sendResponse(exchg, 200, "text/plain", this.VERSION);
            } else {
                String line;
                BufferedReader in = new BufferedReader(new InputStreamReader(exchg.getRequestBody()));
                while (in.ready()) {
                    line = in.readLine();
                }
                sendResponse(exchg, 200, "text/plain", "");
            }
        }
    }

    public void sendResponse(HttpExchange exchg, int code, String type, String response) throws IOException {
        byte[] buf = response.getBytes();
        OutputStream out = exchg.getResponseBody();
        Headers responseHeaders = exchg.getResponseHeaders();
        responseHeaders.set("Content-Type", type);
        exchg.sendResponseHeaders(code, buf.length);
        out.write(buf);
        out.close();
    }

    /**
     * 
     * @return ctions
     */
    public boolean openMonitor() {

        if (monitor != null) {
            if (resumeMonitor()) {
                monitor.toFront();
                return true;
            } else {
                return false;
            }
        }

        BoardPort port = Base.getDiscoveryManager().find(PreferencesData.get("serial.port"));
        monitor = new AbbozzaMonitor(port);
        try {
            monitor.open();
            monitor.setVisible(true);
            monitor.toFront();
            monitor.setAlwaysOnTop(true);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            return false;
        }
        return true;
    }

    public boolean resumeMonitor() {
        if (monitor == null) {
            return false;
        }
        try {
            monitor.resume();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public void closeMonitor() {
        monitor = null;
    }
    
    public void setEditorText(final String code) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                editor.setText(code);
            }
        });        
    }

    public String uploadCode(String code) {
        // System.out.println("hier");

        logger.reset();
        
        String response;
        boolean flag = PreferencesData.getBoolean("editor.save_on_verify");
        PreferencesData.setBoolean("editor.save_on_verify", false);

        editor.getSketch().getCurrentCode().setProgram(code);
        editor.getSketch().getCurrentCode().setModified(true);
        setEditorText(code);
        
        try {
            editor.getSketch().prepare();
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }

        ThreadGroup group = Thread.currentThread().getThreadGroup();
        Thread[] threads = new Thread[group.activeCount()];
        group.enumerate(threads, false);

        try {
            if (monitor != null) {
                monitor.suspend();
            }

            editor.getSketch().save();
            editor.handleExport(false);
        } catch (Exception ex) {
        }

        Thread[] threads2 = new Thread[group.activeCount()];
        group.enumerate(threads2, false);

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
        while ( (last != null) && (last.isAlive()) ) {
        }

        // response = this.errMonitor.getContent();
        response = logger.toString();
        
        // System.out.println("upload response: " + response);
        
        PreferencesData.setBoolean("editor.save_on_verify", flag);
        return response;
    }

    
    public String setCode(String code) {     
        logger.reset();

        String response;
        boolean flag = PreferencesData.getBoolean("editor.save_on_verify");
        PreferencesData.setBoolean("editor.save_on_verify", false);

        setEditorText(code);
        
        editor.getSketch().getCurrentCode().setProgram(code);
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
        
        
        // response = this.errMonitor.getContent();
        response = logger.toString();
        PreferencesData.setBoolean("editor.save_on_verify", flag);
        
        return response;
    }

    public void serialMonitor() {
        this.editor.handleSerial();
    }

    public String loadSketch() throws IOException {
        String result = "";
        BufferedReader reader;
        String path = ((lastSketchFile != null) ? lastSketchFile.getAbsolutePath() : getSketchbookPath());
        JFileChooser chooser = new JFileChooser(path) {
            @Override
            protected JDialog createDialog(Component parent)
                    throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                // config here as needed - just to see a difference
                dialog.setLocationByPlatform(true);
                // might help - can't know because I can't reproduce the problem
                dialog.setAlwaysOnTop(true);
                return dialog;
            }

        };
        chooser.setFileFilter(new FileNameExtensionFilter("abbozza! (*.abz)", "abz"));
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            reader = new BufferedReader(new FileReader(file));
            while (reader.ready()) {
                result = result + reader.readLine() + '\n';
            }
            reader.close();
            lastSketchFile = file;
        } else {
            throw new IOException();
        }
        this.editor.setState(Frame.ICONIFIED);
        this.editor.setExtendedState(JFrame.ICONIFIED);
        return result;
    }

    public void saveSketch(InputStream stream) throws IOException {
        String path = ((lastSketchFile != null) ? lastSketchFile.getAbsolutePath() : getSketchbookPath());
        JFileChooser chooser = new JFileChooser(path) {
            @Override
            protected JDialog createDialog(Component parent)
                    throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                // config here as needed - just to see a difference
                dialog.setLocationByPlatform(true);
                // might help - can't know because I can't reproduce the problem
                dialog.setAlwaysOnTop(true);
                return dialog;
            }

        };
        chooser.setFileFilter(new FileNameExtensionFilter("abbozza! (*.abz)", "abz"));
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".abz") && !file.getName().endsWith(".ABZ")) {
                file = new File(file.getPath() + ".abz");
            }
            FileWriter writer;

            if (!file.equals(lastSketchFile) && file.exists()) {
                int answer = JOptionPane.showConfirmDialog(null, AbbozzaLocale.entry("msg.file_overwrite", file.getName()), "", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            writer = new FileWriter(file);
            // StreamResult result = new StreamResult(writer);

            String line;
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            while (in.ready()) {
                line = in.readLine();
                writer.write(line);
            }
            writer.close();
            in.close();
            lastSketchFile = file;
        }
        this.editor.setState(JFrame.ICONIFIED);
        this.editor.setExtendedState(JFrame.ICONIFIED);
    }

    public void startBrowser() {
        Runtime runtime = Runtime.getRuntime();

        if ((config.getBrowserPath() != null) && (!config.getBrowserPath().equals(""))) {
            String cmd = config.getBrowserPath() + " http://localhost:" + serverPort + "/abbozza.html";
            try {
                AbbozzaLogger.out("Starting browser " + cmd);
                runtime.exec(cmd);
                editor.toBack();
            } catch (IOException e) {
                // TODO Browser could not be started
            }
        } else {
            Object[] options = {AbbozzaLocale.entry("msg.cancel"), AbbozzaLocale.entry("msg.open_standard_browser"), AbbozzaLocale.entry("msg.give_browser")};
            Object selected = JOptionPane.showOptionDialog(null, AbbozzaLocale.entry("msg.no_browser_given"),
                    AbbozzaLocale.entry("msg.no_browser_given"),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                    null, options, options[0]);
            switch (selected.toString()) {
                case "0":
                    break;
                case "1":
                    boolean failed = false;
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop desktop = Desktop.getDesktop();
                            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                                String url = "localhost:" + serverPort + "/abbozza.html";
                                Desktop.getDesktop().browse(new URI(url));
                            } else {
                                failed = true;
                            }
                        } catch (IOException | URISyntaxException e) {
                            failed = true;
                        }
                    } else {
                        failed = true;
                    }
                    if (failed) {
                        JOptionPane.showMessageDialog(null, AbbozzaLocale.entry("msg.cant_open_standard_browser"), "abbozza!", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case "2":
                    AbbozzaConfigDialog dialog = new AbbozzaConfigDialog(config.get(), null, true);
                    dialog.setModal(true);
                    dialog.setVisible(true);
                    if (dialog.getState() == 0) {
                        config.set(dialog.getConfiguration());
                        AbbozzaLocale.setLocale(config.getLocale());
                        config.write();
                        String cmd = config.getBrowserPath() + " http://localhost:" + serverPort + "/abbozza.html";
                        try {
                            AbbozzaLogger.out("Starting browser " + cmd);
                            runtime.exec(cmd);
                            editor.toBack();
                        } catch (IOException e) {
                        // TODO Browser could not be started
                        }
                        // sendResponse(exchg, 200, "text/plain", abbozza.getProperties().toString());
                    } else {
                        // sendResponse(exchg, 440, "text/plain", "");
                    }
                    break;
            }
        }
    }

    
    public boolean connectToBoard(HttpExchange exchg, boolean query) {
        String port = null;
        String board = null;
        List<BoardPort> ports = Base.getDiscoveryManager().discovery();
        for(int i = 0; i < ports.size(); i++) {
            AbbozzaLogger.out("port " + ports.get(i).getAddress() + " " + ports.get(i).getLabel() + " " + ports.get(i).getBoardName(),AbbozzaLogger.INFO);
            if (ports.get(i).getBoardName() != null) {
                port = ports.get(i).getAddress();
                board = ports.get(i).getBoardName();
                AbbozzaLogger.out("Found '" + board + "' on " + port);

                BaseNoGui.selectSerialPort(port);

                TargetPlatform platform = BaseNoGui.getTargetPlatform();
                for (TargetBoard targetBoard : platform.getBoards().values()) {
                    AbbozzaLogger.out(">> " + targetBoard.getName() + " == " + board);
                    if (targetBoard.getName().equals(board)) {
                        BaseNoGui.selectBoard(targetBoard);
                    }
                }

                Base.INSTANCE.onBoardOrPortChange();
            }
        }

        TargetBoard targetBoard = BaseNoGui.getTargetBoard();
        TargetPlatform platform = BaseNoGui.getTargetPlatform();
        AbbozzaLogger.out("targetBoard: " + targetBoard.getId(),AbbozzaLogger.INFO);

        try {
            if (board != null) {
                AbbozzaLogger.out("board found " + targetBoard.getId() + " " + targetBoard.getName() + " " + port ,AbbozzaLogger.INFO);
                sendResponse(exchg, 200, "text/plain", targetBoard.getId() + "|" + targetBoard.getName() + "|" + port);
                return true;
            } else {
                AbbozzaLogger.out("no board found",AbbozzaLogger.INFO);
                
                if (query == false) {
                    AbbozzaLogger.out("IDE set to : " + targetBoard.getId() + " " + targetBoard.getName() + " " + port,AbbozzaLogger.INFO);
                    sendResponse(exchg, 201, "text/plain", targetBoard.getId() + "|" + targetBoard.getName() + "|" + port);
                    return false;
                } else {                    
                    // Cycle through all packages
                    Vector<BoardListEntry> boards = new Vector<BoardListEntry>();
                    
                    for (TargetPackage targetPackage : BaseNoGui.packages.values()) {
                        // For every package cycle through all platform
                        for (TargetPlatform targetPlatform : targetPackage.platforms()) {
                                
                            // Add a title for each platform
                            String platformLabel = targetPlatform.getPreferences().get("name"); 
                            if (platformLabel != null && !targetPlatform.getBoards().isEmpty()) {
                                
                                for (TargetBoard tboard : targetPlatform.getBoards().values()) {
                                    boards.add(new BoardListEntry(tboard));
                                }
                            }
                        }
                    }
                    BoardListEntry result = (BoardListEntry) JOptionPane.showInputDialog(null, AbbozzaLocale.entry("msg.select_board"), AbbozzaLocale.entry("msg.no_board"), JOptionPane.PLAIN_MESSAGE, null, boards.toArray(),BaseNoGui.getTargetBoard());
                    if (result != null) {
                        AbbozzaLogger.out("selected : " + result.getId() ,AbbozzaLogger.INFO);
                        sendResponse(exchg, 201, "text/plain", result.getId() + "|" + result.getName() + "|???");
                        BaseNoGui.selectBoard(result.getBoard());
                        Base.INSTANCE.onBoardOrPortChange();
                    } else {
                        AbbozzaLogger.out("IDE set to : " + targetBoard.getId() + " " + targetBoard.getName() + " " + port,AbbozzaLogger.INFO);
                        sendResponse(exchg, 201, "text/plain", targetBoard.getId() + "|" + targetBoard.getName() + "|" + port);
                        return false;
                    }
                    return false;
                }
            }
        } catch (IOException ex) {
            return false;
        }
    }

    
    public void findJarsAndDirs(JarDirHandler jarHandler) {
        webDirLocal = new File(sketchbookPath + "/tools/Abbozza/web");
        if (!webDirLocal.exists()) {
            AbbozzaLogger.out(webDirLocal.getAbsolutePath() + " not found");
            webDirLocal = null;
        } else {
            AbbozzaLogger.out("Local directory: " + webDirLocal.getAbsolutePath());
        }
        try {
            jarLocal = new JarFile(sketchbookPath + "/tools/Abbozza/tool/Abbozza.jar");
            AbbozzaLogger.out("Local jar : " + jarLocal.getName());
        } catch (IOException e) {
            AbbozzaLogger.out("Local jar not found");
        }
        webDirGlobal = new File(runtimePath + "/tools/Abbozza/web");
        if (!webDirGlobal.exists()) {
            AbbozzaLogger.out(webDirGlobal.getAbsolutePath() + " not found");
            webDirGlobal = null;
        } else {
            AbbozzaLogger.out("Global directory: " + webDirGlobal.getAbsolutePath());
        }
        try {
            jarGlobal = new JarFile(runtimePath + "/tools/Abbozza/tool/Abbozza.jar");
            AbbozzaLogger.out("Global jar : " + jarGlobal.getName());
        } catch (IOException e) {
            AbbozzaLogger.out("Global Jar not found");
        }

        jarHandler.clear();
        jarHandler.addDir(webDirLocal);
        jarHandler.addJar(jarLocal);
        jarHandler.addDir(webDirGlobal);
        jarHandler.addJar(jarGlobal);
    }
    
    public int getRunningServerPort() {
        return serverPort;
    }
    
    public static Abbozza getInstance() {
        return instance;
    }

    public static AbbozzaConfig getConfig() {
        return instance.config;
    }
    
    public String getSketchbookPath() {
        return sketchbookPath;
    }

    public void print(String message) {
        AbbozzaLogger.out(message);
    }

    public void processMessage(String message) {
        this.editor.setText(message);
    }

    @Override
    public String getMenuTitle() {
        return "abbozza!";
    }

    public JarFile getJarLocal() {
        return jarLocal;
    }

    public JarFile getJarGlobal() {
        return jarGlobal;
    }

    public File getWebDirLocal() {
        return webDirLocal;
    }

    public File getWebDirGlobal() {
        return webDirGlobal;
    }

    public byte[] getLocaleBytes(String locale) throws IOException {
        AbbozzaLogger.out("Loading locale " + locale);
        if (jarHandler != null) {
            byte[] bytes = jarHandler.getBytes("/js/languages/" + locale + ".xml");
            if (bytes != null) {
                AbbozzaLogger.out("Loaded locale " + locale);
                return bytes;
            }
        }
        AbbozzaLogger.out("Could not find /js/languages/" + locale + ".xml", AbbozzaLogger.ERROR);
        return null;
    }

    public Vector getLocales() {
        Vector locales = new Vector();
        if (jarHandler != null) {
            try {
                byte[] bytes = jarHandler.getBytes("/js/languages/locales.xml");
                if (bytes != null) {
                    Document localeXml;

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder;
                    builder = factory.newDocumentBuilder();
                    StringBuilder xmlStringBuilder = new StringBuilder();
                    ByteArrayInputStream input = new ByteArrayInputStream(bytes);
                    localeXml = builder.parse(input);

                    NodeList nodes = localeXml.getElementsByTagName("locale");
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Node node = nodes.item(i);
                        String id = node.getAttributes().getNamedItem("id").getNodeValue();
                        String name = node.getTextContent();
                        locales.add(new LocaleEntry(name,id));
                    }

                    AbbozzaLogger.out("Loaded list of locales");
                }
            } catch (IOException | SAXException  | ParserConfigurationException ex) {
                AbbozzaLogger.out("Could not find /js/languages/locales.xml", AbbozzaLogger.ERROR);
            }
        }
        return locales;        
    }
    
    
        public Document getOptionTree() {
        Document optionsXml = null;
        if (jarHandler != null) {
            try {
                byte[] bytes = jarHandler.getBytes("/js/abbozza/options.xml");
                if (bytes != null) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder;
                    builder = factory.newDocumentBuilder();
                    StringBuilder xmlStringBuilder = new StringBuilder();
                    ByteArrayInputStream input = new ByteArrayInputStream(bytes);
                    optionsXml = builder.parse(input);
                }
            } catch (IOException | SAXException  | ParserConfigurationException ex) {
                AbbozzaLogger.out("Could not find /js/abbozza/options.xml", AbbozzaLogger.ERROR);
            }
        }
        return optionsXml;        
    }

}
