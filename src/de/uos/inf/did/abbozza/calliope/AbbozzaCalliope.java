/**
 * @license
 * abbozza!
 *
 * Copyright 2015 Michael Brinkmeier ( michael.brinkmeier@uni-osnabrueck.de )
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * @fileoverview ...
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */
package de.uos.inf.did.abbozza.calliope;

import com.sun.net.httpserver.HttpHandler;
import de.uos.inf.did.abbozza.AbbozzaServer;
import de.uos.inf.did.abbozza.calliope.handler.BoardHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.python.core.PyCode;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

/**
 *
 * @author michael
 */
public class AbbozzaCalliope extends AbbozzaServer implements HttpHandler {
    
    private PythonInterpreter _interpreter;
    private String _pathToBoard;
    
    public static void main (String args[]) {
        AbbozzaCalliope abbozza = new AbbozzaCalliope();
        abbozza.init("calliope");
        
        abbozza.startServer();
        abbozza.startBrowser("calliope.html");        
    }

    public void init(String system) {
        super.init(system);
        
        try {
            String uflashScript = new String(this.jarHandler.getBytes("/uflash/uflash.py"));
            _interpreter = new PythonInterpreter();
            _interpreter.exec(uflashScript);
            PyObject findMicrobit = _interpreter.get("find_microbit");
            PyObject path = findMicrobit.__call__();
            _pathToBoard = path.asString();
        } catch (IOException ex) {
            Logger.getLogger(AbbozzaCalliope.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    public void setBoardPath(String path) {
        _pathToBoard = path;
    }
    
    @Override
    public void registerSystemHandlers() {
        httpServer.createContext("/abbozza/board", new BoardHandler(this, false));
        httpServer.createContext("/abbozza/queryboard", new BoardHandler(this, true));
    }

    @Override
    public void toolToBack() {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void toolSetCode(String code) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void toolIconify() {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String compileCode(String code) {
        System.out.println(code);
        return "";
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String uploadCode(String code) {
        System.out.println("Compile ...");
        try {
            PyObject hexlify = _interpreter.get("hexlify");
            PyObject runtime = _interpreter.get("_RUNTIME");
            PyObject embedHex = _interpreter.get("embed_hex");
            PyObject saveHex = _interpreter.get("save_hex");
            PyObject hexcode = hexlify.__call__(new PyString(code));
            PyObject[] args = new PyObject[2];
            args[0] = runtime;
            args[1] = hexcode;
            PyObject finalcode = embedHex.__call__(args);
            PyObject result = saveHex.__call__(finalcode,new PyString(_pathToBoard));
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return "Error";
        }
        return "";
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
