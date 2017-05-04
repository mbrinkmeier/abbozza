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
package de.uos.inf.did.abbozza.calliope;

import de.uos.inf.did.abbozza.AbbozzaLogger;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mbrinkmeier
 */
public class AbbozzaCalliopeC extends AbbozzaCalliope {

    protected String _buildPath;

    public static void main(String args[]) {
        AbbozzaCalliopeC abbozza = new AbbozzaCalliopeC();
        abbozza.init("calliopeC");

        abbozza.startServer();
        // abbozza.startBrowser("calliope.html");        
    }

    /**
     * Initialize Server
     *
     * @param system
     */
    public void init(String system) {
        super.init(system);
        _buildPath = System.getProperty("user.home") + "/abbozza/build/calliope/";
        AbbozzaLogger.out("Build path set to " + _buildPath, AbbozzaLogger.INFO);
    }

    /**
     * Copy code to <buildPath>/source/abbozza.cpp and compile it.
     *
     * @param code
     * @return
     */
    @Override
    public String compileCode(String code) {
        AbbozzaLogger.out("Code generated", AbbozzaLogger.INFO);
        // Set code in frame
        this.frame.setCode(code);

        String errMsg = "";
        int exitValue = 1;
        
        InputStream err;
        InputStream stdout;

        // Redirect error stream
        // PrintStream origErr = System.err;
        // ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        // PrintStream newErr = new PrintStream(buffer);
        // System.setErr(newErr);
        // Copy code to <buildPath>/source/abbozza.cpp
        AbbozzaLogger.out("Writing code to " + _buildPath + "source/abbozza.cpp");
        if (code != "") {
            try {
                PrintWriter out = new PrintWriter(_buildPath + "source/abbozza.cpp");
                out.write(code);
                out.flush();
                out.close();

                // Now compile it by calling "yt build" in _buildPath
                String scriptName = "compile.sh";
                String osName = System.getProperty("os.name");
                if (osName.indexOf("Windows") != -1) {
                    scriptName = "compile.bat";
                }
                Process proc = Runtime.getRuntime().exec(_buildPath + scriptName);
                err = proc.getErrorStream();
                stdout = proc.getInputStream();

                proc.waitFor();
                
                exitValue = proc.exitValue();
                
                BufferedReader buf = new BufferedReader(new InputStreamReader(err));
                while (buf.ready()) {
                    errMsg = errMsg + "\n" + buf.readLine();
                }
            } catch (FileNotFoundException ex) {
                AbbozzaLogger.out(ex.getLocalizedMessage(), AbbozzaLogger.ERROR);
            } catch (IOException ex) {
                AbbozzaLogger.out(ex.getLocalizedMessage(), AbbozzaLogger.ERROR);
            } catch (InterruptedException ex) {
                AbbozzaLogger.out(ex.getLocalizedMessage(), AbbozzaLogger.ERROR);
            }

            // Reset error stream
            // newErr.flush();
            // System.setErr(origErr);
            // Fetch response
            if (exitValue > 0) {
                AbbozzaLogger.out(errMsg, AbbozzaLogger.ERROR);
            } else {
                errMsg = "";
                AbbozzaLogger.out("Compilation successful", AbbozzaLogger.INFO);
            }
        }

        return errMsg;
    }

    @Override
    public String uploadCode(String code) {

        String errMsg = compileCode(code);

        if (errMsg.length() == 0) {
            FileInputStream in = null;
            try {
                AbbozzaLogger.out("Copying " + _buildPath + "build/calliope-mini-classic-gcc/source/abbozza-combined.hex to " + _pathToBoard + "/abbozza.hex", 4);
                in = new FileInputStream(_buildPath + "build/calliope-mini-classic-gcc/source/abbozza-combined.hex");
                PrintWriter out = new PrintWriter(_pathToBoard + "/abbozza.hex");
                while (in.available() > 0) {
                    out.write(in.read());
                }
                out.flush();
                out.close();
                in.close();
            } catch (FileNotFoundException ex) {
                AbbozzaLogger.err(ex.getLocalizedMessage());
            } catch (IOException ex) {
                AbbozzaLogger.err(ex.getLocalizedMessage());
            }
        }

        return "";
    }

}
