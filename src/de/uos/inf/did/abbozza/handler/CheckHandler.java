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
package de.uos.inf.did.abbozza.handler;

import com.sun.net.httpserver.HttpExchange;
import de.uos.inf.did.abbozza.AbbozzaLocale;
import de.uos.inf.did.abbozza.AbbozzaLogger;
import de.uos.inf.did.abbozza.AbbozzaServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import processing.app.PreferencesData;

/**
 *
 * @author michael
 */
public class CheckHandler extends AbstractHandler {

    public CheckHandler(AbbozzaServer abbozza) {
        super(abbozza);
    }

    @Override
    public void handle(HttpExchange exchg) throws IOException {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(exchg.getRequestBody()));
            StringBuffer code = new StringBuffer();
            while (in.ready()) {
                code.append(in.readLine());
                code.append('\n');
            }
            String response = setCode(code.toString());
            if (response.equals("")) {
                this.sendResponse(exchg, 200, "text/plain", AbbozzaLocale.entry("msg.done-compiling"));
            } else {
                this.sendResponse(exchg, 400, "text/plain", response);
            }
        } catch (IOException ioe) {
            this.sendResponse(exchg, 404, "", "");
        }
    }
    
    
    public String setCode(String code) { 
        AbbozzaLogger.resetErr();
        
        String response;

        response = _abbozzaServer.compileCode(code);
        response = AbbozzaLogger.getErr();
        
        return response;
    }

}
