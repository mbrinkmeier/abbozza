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
import java.io.File;
import java.io.IOException;
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
        String path = server.getPathToBoard();
        File dir;
        
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
            sendResponse(exchg, 200, "text/plain", "calliope found");
        }
    }

}
