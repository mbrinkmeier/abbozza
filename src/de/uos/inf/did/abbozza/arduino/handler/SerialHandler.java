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
package de.uos.inf.did.abbozza.arduino.handler;

import com.sun.net.httpserver.HttpExchange;
import de.uos.inf.did.abbozza.AbbozzaServer;
import de.uos.inf.did.abbozza.handler.AbstractHandler;
import de.uos.inf.did.abbozza.monitor.AbbozzaMonitor;
import java.io.IOException;

/**
 *
 * @author michael
 */
public class SerialHandler extends AbstractHandler {

    public SerialHandler(AbbozzaServer abbozza) {
        super(abbozza);
    }
    
    @Override
    public void handle(HttpExchange he) throws IOException {
        String query = he.getRequestURI().getQuery();
        // msg=<msg>&timeout=<time>
        // No timeout means that the request is not waitung
        AbbozzaMonitor monitor = this._abbozzaServer.monitorHandler.getMonitor();
        if ( monitor != null ) {
           monitor.sendMessage(query, he, this, 30000);
        } else {
           sendResponse(he, 400, "text/plain", "No board listens!"); 
        }
    }
    
}
