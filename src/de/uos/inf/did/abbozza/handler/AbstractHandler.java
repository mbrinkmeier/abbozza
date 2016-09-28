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

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.uos.inf.did.abbozza.arduino.Abbozza;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author michael
 */
public abstract class AbstractHandler implements HttpHandler {

    protected Abbozza _abbozza;
    
    public AbstractHandler(Abbozza abbozza) {
        this._abbozza = abbozza;
    }
    
    public void sendResponse(HttpExchange exchg, int code, String type, String response) throws IOException {
        byte[] buf = response.getBytes();
        OutputStream out = exchg.getResponseBody();
        Headers responseHeaders = exchg.getResponseHeaders();
        responseHeaders.set("Content-Type", type);
        exchg.sendResponseHeaders(code, buf.length);
        out.write(buf);
        out.close();
    }
    
}
