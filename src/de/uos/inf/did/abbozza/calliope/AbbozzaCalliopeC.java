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

/**
 *
 * @author mbrinkmeier
 */
public class AbbozzaCalliopeC extends AbbozzaCalliope {
    
    public static void main (String args[]) {
        AbbozzaCalliopeC abbozza = new AbbozzaCalliopeC();
        abbozza.init("calliopeC");
        
        abbozza.startServer();
        // abbozza.startBrowser("calliope.html");        
    }

    @Override
    public String compileCode(String code) {
        AbbozzaLogger.out("Code generated",4);
        this.frame.setCode(code);
        return "";
    }

    @Override
    public String uploadCode(String code) {
        this.frame.setCode(code);        
        /*
        String java = embed(hexlify(code));
        AbbozzaLogger.out("Writing hex code to " + _pathToBoard + "/abbozza.hex",4);
        
        if ( java != "" ) {
                try {
                    PrintWriter out = new PrintWriter(_pathToBoard + "/abbozza.hex");
                    out.write(java);
                    out.flush();
                    out.close();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AbbozzaCalliope.class.getName()).log(Level.SEVERE, null, ex);
                }
        } else {
        }
        */
        return "";
    }

}
