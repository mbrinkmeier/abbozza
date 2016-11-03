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
package de.uos.inf.did.abbozza.calliope.handler;

import de.uos.inf.did.abbozza.arduino.handler.*;
import cc.arduino.packages.BoardPort;
import com.sun.net.httpserver.HttpExchange;
import de.uos.inf.did.abbozza.arduino.Abbozza;
import de.uos.inf.did.abbozza.AbbozzaLocale;
import de.uos.inf.did.abbozza.AbbozzaLogger;
import de.uos.inf.did.abbozza.BoardListEntry;
import de.uos.inf.did.abbozza.calliope.AbbozzaCalliope;
import de.uos.inf.did.abbozza.handler.AbstractHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import processing.app.Base;
import processing.app.BaseNoGui;
import processing.app.debug.TargetBoard;
import processing.app.debug.TargetPackage;
import processing.app.debug.TargetPlatform;

/**
 *
 * @author michael
 */
public class BoardHandler extends AbstractHandler {

    private boolean _query;
    private PythonInterpreter _interpreter;

    public BoardHandler(AbbozzaCalliope abbozza, boolean query) {
        super(abbozza);
        this._query = query;
    }

    @Override
    public void handle(HttpExchange exchg) throws IOException {
        AbbozzaCalliope server = (AbbozzaCalliope) _abbozzaServer;
        String path = this.findBoard();
        File dir;

        AbbozzaLogger.err("after findBoard: " + path);
        
        if (path != null ) {
            dir = new File(path);
        } else {
            dir = new File("");
        }

        if ( this._query ) {
            dir = server.queryPathToBoard(path);
            server.setPathToBoard(dir.getCanonicalPath());
        }
        
        if ( !dir.exists() || !dir.isDirectory() || !dir.canWrite() ) {
            sendResponse(exchg, 201, "text/plain", "calliope not found");            
        } else {
            sendResponse(exchg, 200, "text/plain", path);
        }
    }

    
    private String findBoard() {
        String os = System.getProperty("os.name").toLowerCase();
        if ( os.contains("win") ) {        
        } else if ( os.contains("mac") ) {
        } else if ( os.contains("linux") ) {
            try {
                Process process = Runtime.getRuntime().exec("mount");
                process.waitFor();
                InputStreamReader reader = new InputStreamReader(process.getInputStream());
                BufferedReader volumes = new BufferedReader(reader);
                String volume;
                while(volumes.ready()) {
                    volume = volumes.readLine();
                    if ( volume.contains("MINI") || volume.contains("MICROBIT")) {
                        AbbozzaLogger.err("in findBoard: " + volume);
                        return volume;
                    }
                 }
                 return "";
            } catch (Exception ex) {
                return "";
            }
        } else {
            
        }
        return "";
    } 
   
}
