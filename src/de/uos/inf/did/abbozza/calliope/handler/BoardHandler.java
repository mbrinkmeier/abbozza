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
 * @fileoverview  * Ths class handles the board request
 * It detects a mounted Calliope Mini or micro:bit board
 *
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */
package de.uos.inf.did.abbozza.calliope.handler;

import com.sun.net.httpserver.HttpExchange;
import de.uos.inf.did.abbozza.AbbozzaLocale;
import de.uos.inf.did.abbozza.AbbozzaLogger;
import de.uos.inf.did.abbozza.calliope.AbbozzaCalliope;
import de.uos.inf.did.abbozza.handler.AbstractHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

public class BoardHandler extends AbstractHandler {

    private boolean _query;

    /**
     * Initialize the board request handler
     *
     * @param abbozza
     * @param query This flag indicates wether the user should be asked for the
     * path to the board if it is not found.
     */
    public BoardHandler(AbbozzaCalliope abbozza, boolean query) {
        super(abbozza);
        this._query = query;
    }

    /**
     * Handle a request
     *
     * @param exchg The HttoExchange object representing the request and the
     * response
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchg) throws IOException {
        AbbozzaCalliope server = (AbbozzaCalliope) _abbozzaServer;

        // Get the set path
        String path = server.getPathToBoard();
        AbbozzaLogger.out("[BoardHandler] Path to board is " + path, AbbozzaLogger.DEBUG);

        // Get the board if possible
        String board = this.findBoard();

        File dir;

        if (board != null) {
            dir = new File(board);
        } else {
            dir = new File("");
        }

        // If no board was found, ask the user, is required
        if (board == "" && this._query) {
            AbbozzaLogger.out("[BoardHandler] User is queried for path to store hex", AbbozzaLogger.DEBUG);
            // Give the old path as default
            dir = queryPathToBoard(path);
            if (dir != null) {
                server.setPathToBoard(dir.getCanonicalPath());
                AbbozzaLogger.out("BoardHandler] Path set to " + dir.getCanonicalPath(), AbbozzaLogger.DEBUG);
            } else {
                sendResponse(exchg, 201, "text/plain", "Query aborted");
            }
        }

        if (!dir.exists() || !dir.isDirectory() || !dir.canWrite()) {
            // If no board was found, send the path known to the server
            if (path == null) {
                AbbozzaLogger.out("BoardHandler] Board not found. No alternative path given.", AbbozzaLogger.DEBUG);
                sendResponse(exchg, 201, "text/plain", "");
            } else {
                AbbozzaLogger.out("BoardHandler] Board not found. Using given path " + path, AbbozzaLogger.DEBUG);
                sendResponse(exchg, 200, "text/plain", path);
            }
        } else {
            // If board was found, use it
            server.setPathToBoard(dir.getCanonicalPath());
            AbbozzaLogger.out("BoardHandler]  Board found at : " + dir.getCanonicalPath(), AbbozzaLogger.DEBUG);
            sendResponse(exchg, 200, "text/plain", dir.getCanonicalPath());
        }
    }

    /**
     * This method tries to detect a connected CalliopeMINI or the micro:bit
     *
     * @return The path to the board as string or an empty string
     */
    private String findBoard() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // In windows system the drives are scanned for their volume name
            File[] roots = File.listRoots();
            for (int i = 0; i < roots.length; i++) {
                try {
                    String volume = FileSystemView.getFileSystemView().getSystemDisplayName(roots[i]);
                    if (volume.contains("MINI") || volume.contains("MICROBIT")) {
                        AbbozzaLogger.out("Board found at " + roots[i].getCanonicalPath(), AbbozzaLogger.INFO);
                        return roots[i].getCanonicalPath();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(BoardHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return "";
        } else if (os.contains("linux") || os.contains("mac")) {
            try {
                // In posix systems (linux and Mac OsX) the system command 'mount' 
                // is used to detect the volume
                Process process = Runtime.getRuntime().exec("mount");
                process.waitFor();
                InputStreamReader reader = new InputStreamReader(process.getInputStream());
                BufferedReader volumes = new BufferedReader(reader);
                String volume;
                while (volumes.ready()) {
                    volume = volumes.readLine();
                    if (volume.contains("MINI") || volume.contains("MICROBIT")) {
                        volume = volume.split(" ")[2];
                        AbbozzaLogger.out("[BoardHandler.findBoard] Board found at " + volume, AbbozzaLogger.INFO);
                        if (volume.contains("MINI")) {
                            this._abbozzaServer.setBoardName("calliope");
                        } else {
                            this._abbozzaServer.setBoardName("microbit");
                        }
                        return volume;
                    }
                }
                volumes.close();
                AbbozzaLogger.out("No board found", 4);
            } catch (Exception ex) {
                AbbozzaLogger.err(ex.getMessage());
                return "";
            }
        } else {
            // Currently no other system is supported
            AbbozzaLogger.err("Operating system " + os + " not supported");
        }
        return "";
    }

    /**
     * This method queries the user for the bpath to the board.
     *
     * @param path The current path
     * @return The new path
     */
    public File queryPathToBoard(String path) {
        File selectedDir = null;
        JFileChooser chooser = new JFileChooser();
        if (path != null) {
            chooser.setCurrentDirectory(new File(path));
        }
        chooser.setDialogTitle(AbbozzaLocale.entry("gui.CalliopePath"));
        BoardChooserPanel boardPanel = new BoardChooserPanel();
        AbbozzaLogger.out("Hier2",AbbozzaLogger.INFO);
        chooser.setAccessory(boardPanel);
        if ((this._abbozzaServer.getBoardName()!=null) && (this._abbozzaServer.getBoardName().equals("microbit"))) {
            boardPanel.setMicrobit();
        } else {
            boardPanel.setCalliope();
        }
        AbbozzaLogger.out("Hier",AbbozzaLogger.INFO);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Select readable directory";
            }
        });
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedDir = chooser.getSelectedFile();
            if (boardPanel.isCalliope()) {
                this._abbozzaServer.setBoardName("calliope");
            } else {
                this._abbozzaServer.setBoardName("microbit");
            }
        } else {
        }
        return selectedDir;
    }

}
