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
    
    private JarDirHandler _jarHandler;
    
    public TaskHandler(AbbozzaServer abbozza, JarDirHandler jarHandler) {
        super(abbozza);
        this._jarHandler = jarHandler;
    }

    @Override
    public void handle(HttpExchange exchg) throws IOException {
        String taskPath = this._abbozzaServer.getConfiguration().getTaskPath();
        
        String path = exchg.getRequestURI().getPath();
        path = path.substring(5);
        AbbozzaLogger.out("TaskHandler: " + taskPath + path + " requested", AbbozzaLogger.INFO);
        OutputStream os = exchg.getResponseBody();
        
        byte[] bytearray = getBytes(taskPath + path);
                
        if (bytearray == null) {
            AbbozzaLogger.out("TaskHandler: " + taskPath + path + " not found! Looking in jars!", AbbozzaLogger.INFO);
            AbbozzaLogger.out("TaskHandler: Looking for " + "/tasks/" + AbbozzaServer.getInstance().getSystem() + path, AbbozzaLogger.INFO);
            // String result = "abbozza! : " + path + " not found in task directory! Looking in jars.";
            bytearray = this._jarHandler.getBytes("/tasks/" + AbbozzaServer.getInstance().getSystem() + path);
        }
        
        if (bytearray == null) {        
            AbbozzaLogger.out("TaskHandler: tasks" + path + " not found!", AbbozzaLogger.INFO);
            String result = "abbozza! : " + path + " not found!";
            // System.out.println(result);

            exchg.sendResponseHeaders(400, result.length());
            os.write(result.getBytes());
            os.close();
            return;
        }

        Headers responseHeaders = exchg.getResponseHeaders();
        if (path.endsWith(".css")) {
            responseHeaders.set("Content-Type", "text/css");
        } else if (path.endsWith(".js")) {
            responseHeaders.set("Content-Type", "text/javascript");
        } else if (path.endsWith(".xml")) {
            responseHeaders.set("Content-Type", "text/xml");
        } else if (path.endsWith(".svg")) {
            responseHeaders.set("Content-Type", "image/svg+xml");            
        } else if (path.endsWith(".abz")) {
            responseHeaders.set("Content-Type", "text/xml");            
        } else if (path.endsWith(".png")) {
            responseHeaders.set("Content-Type", "image/png");
        } else if (path.endsWith(".html")) {
            responseHeaders.set("Content-Type", "text/html");
        } else {
            responseHeaders.set("Content-Type", "text/text");            
        }

        // AbbozzaLogger.out(new String(bytearray),AbbozzaLogger.INFO);
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
