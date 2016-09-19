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
package de.uos.inf.did.abbozza;

import de.uos.inf.did.abbozza.handler.JarDirHandler;
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
import de.uos.inf.did.abbozza.handler.BoardHandler;
import de.uos.inf.did.abbozza.handler.CheckHandler;
import de.uos.inf.did.abbozza.handler.ConfigDialogHandler;
import de.uos.inf.did.abbozza.handler.ConfigHandler;
import de.uos.inf.did.abbozza.handler.LoadHandler;
import de.uos.inf.did.abbozza.handler.MonitorHandler;
import de.uos.inf.did.abbozza.handler.SaveHandler;
import de.uos.inf.did.abbozza.handler.TaskHandler;
import de.uos.inf.did.abbozza.handler.UploadHandler;
import de.uos.inf.did.abbozza.monitor.VersionHandler;
import java.awt.Component;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
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
    public static final int VER_REV = 5;
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
    public MonitorHandler monitorHandler;
    // public AbbozzaErrMonitor errMonitor;
    private HttpServer httpServer;
    private int serverPort;
    private boolean isStarted = false;
    // private AbbozzaMonitor monitor = null;
    private AbbozzaConfig config = null;

    private JarDirHandler jarHandler;
    // private TaskHandler taskHandler;

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

        if (this.getConfig().getUpdate()) {
            checkForUpdate(false);
        }

        this.editor = editor;

        // AbbozzaLocale.setLocale("de_DE");
        AbbozzaLogger.out(AbbozzaLocale.entry("msg.loaded"), AbbozzaLogger.INFO);

        // taskHandler = new TaskHandler(this);
        // AbbozzaLogger.out("Path to tasks : " + config.getTaskPath(),AbbozzaLogger.ALL);
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
            URL url = new URL(updateUrl + "VERSION");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            version = br.readLine();
            br.close();
            int pos = version.indexOf('.');
            major = Integer.parseInt(version.substring(0, pos));
            int pos2 = version.indexOf('.', pos + 1);
            minor = Integer.parseInt(version.substring(pos + 1, pos2));
            rev = Integer.parseInt(version.substring(pos2 + 1));
        } catch (Exception ex) {
            AbbozzaLogger.out("Could not check update version", AbbozzaLogger.INFO);
            return;
        }

        AbbozzaLogger.out("Checking for update at " + updateUrl, AbbozzaLogger.INFO);
        AbbozzaLogger.out("Update version " + major + "." + minor + "." + rev, AbbozzaLogger.INFO);

        if ((major > VER_MAJOR)
                || ((major == VER_MAJOR) && (minor > VER_MINOR))
                || ((major == VER_MAJOR) && (minor == VER_MINOR) && (rev > VER_REV))) {
            AbbozzaLogger.out("New version found", AbbozzaLogger.INFO);
            int res = JOptionPane.showOptionDialog(null, AbbozzaLocale.entry("gui.new_version", version), AbbozzaLocale.entry("gui.new_version_title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (res == JOptionPane.NO_OPTION) {
                return;
            }
            URL url;
            try {
                // Rename current jar
                // AbbozzaLogger.out(this.getSketchbookPath(),AbbozzaLogger.ALL);
                File cur = new File(this.getSketchbookPath() + "/tools/Abbozza/tool/Abbozza.jar");
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                String today = format.format(new Date());
                File dir = new File(this.getSketchbookPath() + "/tools/Abbozza/tool/old");
                if (!dir.exists()) {
                    AbbozzaLogger.out("Creating directory " + dir.getPath(), AbbozzaLogger.INFO);
                    dir.mkdir();
                }
                AbbozzaLogger.out("Moving old version to " + dir.getPath() + "/Abbozza." + today + ".jar", AbbozzaLogger.INFO);
                cur.renameTo(new File(this.getSketchbookPath() + "/tools/Abbozza/tool/old/Abbozza." + today + ".jar"));
                AbbozzaLogger.out("Downloading version " + version, AbbozzaLogger.INFO);
                url = new URL(updateUrl + "Abbozza.jar");
                URLConnection conn = url.openConnection();
                byte buffer[] = new byte[4096];
                int n = -1;
                InputStream ir = conn.getInputStream();
                FileOutputStream ow = new FileOutputStream(new File(this.getSketchbookPath() + "/tools/Abbozza/tool/Abbozza.jar"));
                while ((n = ir.read(buffer)) != -1) {
                    ow.write(buffer, 0, n);
                }
                ow.close();
                ir.close();
                AbbozzaLogger.out("Stopping arduino", AbbozzaLogger.INFO);
                System.exit(0);
            } catch (Exception ex) {
                Logger.getLogger(Abbozza.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            AbbozzaLogger.out(AbbozzaLocale.entry("gui.no_update"), AbbozzaLogger.INFO);
            if (reportNoUpdate) {
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

            serverPort = config.getServerPort();
            while (httpServer == null) {
                try {
                    httpServer = HttpServer.create(new InetSocketAddress(serverPort), 0);
                    httpServer.createContext("/abbozza/load", new LoadHandler(this));
                    httpServer.createContext("/abbozza/save", new SaveHandler(this));
                    httpServer.createContext("/abbozza/check", new CheckHandler(this));
                    httpServer.createContext("/abbozza/upload", new UploadHandler(this));
                    httpServer.createContext("/abbozza/config", new ConfigHandler(this));
                    httpServer.createContext("/abbozza/frame", new ConfigDialogHandler(this));
                    httpServer.createContext("/abbozza/board", new BoardHandler(this, false));
                    httpServer.createContext("/abbozza/queryboard", new BoardHandler(this, true));
                    this.monitorHandler = new MonitorHandler(this);
                    httpServer.createContext("/abbozza/monitor", monitorHandler);
                    httpServer.createContext("/abbozza/monitorresume", monitorHandler);
                    httpServer.createContext("/abbozza/version", new VersionHandler(this));
                    httpServer.createContext("/abbozza/", this /* handler */);
                    httpServer.createContext("/task/", new TaskHandler(this));
                    httpServer.createContext("/", jarHandler);
                    httpServer.start();
                    AbbozzaLogger.out("Http-server started on port: " + serverPort, AbbozzaLogger.INFO);
                } catch (Exception e) {
                    serverPort++;
                    httpServer = null;
                }
            }

            AbbozzaLogger.out("abbozza: " + AbbozzaLocale.entry("msg.server_started", Integer.toString(config.getServerPort())));

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
            String line;
            BufferedReader in = new BufferedReader(new InputStreamReader(exchg.getRequestBody()));
            while (in.ready()) {
                line = in.readLine();
            }
            Headers responseHeaders = exchg.getResponseHeaders();
            responseHeaders.set("Content-Type", "text/plain");
            exchg.sendResponseHeaders(200, 0);
            os.close();
        }
    }

    public void monitorIsClosed() {
        this.monitorHandler.close();
    }

    public void setEditorText(final String code) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    editor.setText(code);
                }
            });
        } catch (InterruptedException ex) {
            Logger.getLogger(Abbozza.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Abbozza.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    public String uploadCode(String code) {
        // System.out.println("hier");

        logger.reset();

        String response;
        boolean flag = PreferencesData.getBoolean("editor.save_on_verify");
        /*
         PreferencesData.setBoolean("editor.save_on_verify", false);

         editor.getSketch().getCurrentCode().setProgram(code);
         setEditorText(code);

         editor.getSketch().getCurrentCode().setModified(true);
         */

        try {
            editor.getSketch().prepare();
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }

        ThreadGroup group = Thread.currentThread().getThreadGroup();
        Thread[] threads = new Thread[group.activeCount()];
        group.enumerate(threads, false);

        this.monitorHandler.suspend();

        try {
            editor.getSketch().save();
            editor.handleExport(false);
        } catch (IOException ex) {
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
        while ((last != null) && (last.isAlive())) {
        }

        response = logger.toString();

        PreferencesData.setBoolean("editor.save_on_verify", flag);
        return response;
    }

    public void serialMonitor() {
        this.editor.handleSerial();
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
                    AbbozzaConfigDialog dialog = new AbbozzaConfigDialog(config.get(), null, true, true);
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

    public void findJarsAndDirs(JarDirHandler jarHandler) {
        jarHandler.clear();
        jarHandler.addDir(sketchbookPath + "/tools/Abbozza/web", "Local directory");
        jarHandler.addJar(sketchbookPath + "/tools/Abbozza/tool/Abbozza.jar", "Local jar");
        jarHandler.addDir(runtimePath + "/tools/Abbozza/web", "Global directory");
        jarHandler.addJar(runtimePath + "/tools/Abbozza/tool/Abbozza.jar", "Global jar");
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
                        locales.add(new LocaleEntry(name, id));
                    }

                    AbbozzaLogger.out("Loaded list of locales");
                }
            } catch (IOException | SAXException | ParserConfigurationException ex) {
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
            } catch (IOException | SAXException | ParserConfigurationException ex) {
                AbbozzaLogger.out("Could not find /js/abbozza/options.xml", AbbozzaLogger.ERROR);
            }
        }
        return optionsXml;
    }

    public Editor getEditor() {
        return editor;
    }

    public AbbozzaConfig getConfiguration() {
        return config;
    }

    public File getLastSketchFile() {
        return lastSketchFile;
    }

    public void setLastSketchFile(File lastSketchFile) {
        this.lastSketchFile = lastSketchFile;
    }

    public int openConfigDialog() {
        Editor editor = this.getEditor();
        AbbozzaConfig config = this.getConfiguration();
        Properties props = config.get();
        AbbozzaLogger.out("Hier", AbbozzaLogger.ALL);
        AbbozzaConfigDialog dialog = new AbbozzaConfigDialog(props, null, false, true);
        AbbozzaLogger.out("Da", AbbozzaLogger.ALL);
        dialog.setAlwaysOnTop(true);
        dialog.setModal(true);
        dialog.toFront();
        dialog.setVisible(true);
        editor.setState(JFrame.ICONIFIED);
        editor.setExtendedState(JFrame.ICONIFIED);
        if (dialog.getState() == 0) {
            config.set(dialog.getConfiguration());
            AbbozzaLocale.setLocale(config.getLocale());
            AbbozzaLogger.out("closed with " + config.getLocale());
            config.write();
            return 0;
            //sendResponse(exchg, 200, "text/plain", config.get().toString());
        } else {
            return 1;
            //sendResponse(exchg, 440, "text/plain", "");
        }

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
