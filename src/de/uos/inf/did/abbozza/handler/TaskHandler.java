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
package de.uos.inf.did.abbozza.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import de.uos.inf.did.abbozza.arduino.Abbozza;
import de.uos.inf.did.abbozza.AbbozzaLogger;
import de.uos.inf.did.abbozza.AbbozzaServer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author mbrinkmeier
 */
public class TaskHandler extends AbstractHandler {
    
    public TaskHandler(AbbozzaServer abbozza) {
        super(abbozza);
    }

    @Override
    public void handle(HttpExchange exchg) throws IOException {
        String taskPath = this._abbozzaServer.getConfiguration().getTaskPath();
        
        String path = exchg.getRequestURI().getPath();
        path = path.substring(6);
        AbbozzaLogger.out("TaskHandler: " + taskPath + path + " requested", AbbozzaLogger.INFO);
        OutputStream os = exchg.getResponseBody();
        
        byte[] bytearray = getBytes(taskPath + path);
                
        if (bytearray == null) {
            String result = "abbozza! : " + path + " nicht gefunden!";
            // System.out.println(result);

            exchg.sendResponseHeaders(400, result.length());
            os.write(result.getBytes());
            os.close();
            return;
        }

        Headers responseHeaders = exchg.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/xml");

        AbbozzaLogger.out(new String(bytearray),AbbozzaLogger.ALL);
        // ok, we are ready to send the response.
        exchg.sendResponseHeaders(200, bytearray.length);
        os.write(bytearray, 0, bytearray.length);
        os.close();    
    }
    
    
    public byte[] getBytes(String path) throws IOException {
        File file = new File(path); 
        if (!file.exists()) {
            return null;
        }
        
        String taskPath = this._abbozzaServer.getConfiguration().getTaskPath();
        if (!file.getCanonicalPath().startsWith(taskPath)) {
            return null;
        }
        
        FileInputStream fis = new FileInputStream(file);

        byte[] bytearray = new byte[(int) file.length()];
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(bytearray, 0, bytearray.length);
        bis.close();

        return bytearray;
    }

}
