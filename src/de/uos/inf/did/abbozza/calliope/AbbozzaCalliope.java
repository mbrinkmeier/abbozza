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
    
    
    public static void main (String args[]) {
        AbbozzaCalliope abbozza = new AbbozzaCalliope();
        abbozza.init("calliope");
        
        abbozza.startServer();
        abbozza.startBrowser("calliope.html");
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
        String pathToBoard = "./calliope.hex";
        System.out.println("Compile ...");
        try {
            final String script = new String(this.jarHandler.getBytes("/uflash/uflash.py"));
            final PythonInterpreter interpreter = new PythonInterpreter();
            System.out.println("compiling script ...");
            // SwingUtilities.invokeAndWait( new Runnable() {
            //    @Override
            //    public void run() {
                    interpreter.exec(script);
                    System.out.println("... script compiled");
                    PyObject hexlify = interpreter.get("hexlify");
                    PyObject runtime = interpreter.get("_RUNTIME");
                    PyObject embedHex = interpreter.get("embed_hex");
                    PyObject saveHex = interpreter.get("save_hex");
                    System.out.println("Calling ... " + hexlify);
                    PyObject hexcode = hexlify.__call__(new PyString(code));
                    PyObject[] args = new PyObject[2];
                    args[0] = runtime;
                    args[1] = hexcode;
                    PyObject finalcode = embedHex.__call__(args);
                    PyObject result = saveHex.__call__(finalcode,new PyString(pathToBoard));
            //    }
            // });
            System.out.println("... end");            
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return "Error";
        }
        return "";
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
