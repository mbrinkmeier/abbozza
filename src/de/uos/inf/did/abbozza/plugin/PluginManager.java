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
package de.uos.inf.did.abbozza.plugin;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.uos.inf.did.abbozza.AbbozzaLogger;
import de.uos.inf.did.abbozza.AbbozzaServer;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author michael
 */
public class PluginManager implements HttpHandler {

    private AbbozzaServer _abbozza;
    private Hashtable<String,Plugin> _plugins;
    
    public PluginManager(AbbozzaServer server) {
        AbbozzaLogger.out("PluginManager: Started",AbbozzaLogger.INFO);
        this._abbozza = server;
        this._plugins = new Hashtable<String,Plugin>();
        this.detectPlugins();
        
    }
    
    
    private void detectPlugins() {
        Plugin plugin;
        
        // Check local dir
        File path = new File(this._abbozza.getGlobalPluginPath());
        AbbozzaLogger.out("PluginManager: Checking local dir " + path,AbbozzaLogger.INFO);    
        File[] dirs = null;
        dirs = path.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        
        if (dirs != null) {
            for (int i=0; i < dirs.length; i++) {
                plugin = new Plugin(dirs[i]);
                if (plugin.getId() != null ) {                    
                    AbbozzaLogger.out("PluginManager: Plugin " + plugin.getId() + " found in " + dirs[i].toString() ,AbbozzaLogger.INFO);
                    this._plugins.put(plugin.getId(), plugin);
                }
            }
        }   

        // Check global dir
        path = new File(this._abbozza.getLocalPluginPath());
        AbbozzaLogger.out("PluginManager: Checking global dir " + path,AbbozzaLogger.INFO);        
        dirs = path.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        
        if (dirs != null) {
            for (int i=0; i < dirs.length; i++) {
                plugin = new Plugin(dirs[i]);
                if (plugin.getId() != null ) {
                    AbbozzaLogger.out("PluginManager: Plugin " + plugin.getId() + " found in " + dirs[i].toString() ,AbbozzaLogger.INFO);
                    this._plugins.put(plugin.getId(), plugin);
                }
            }
        }
    }
    
    /**
     * Get an iterator over all plugins
     * 
     * @return The iterator
     */
    public Enumeration<Plugin> plugins() {
        return this._plugins.elements();
    }

    public Plugin getPlugin(String id) {
        return this._plugins.get(id);
    }

    @Override
    public void handle(HttpExchange exchg) throws IOException {
        String response = "";
        String path = exchg.getRequestURI().getPath();
        OutputStream os = exchg.getResponseBody();
        Headers responseHeaders = exchg.getResponseHeaders();

        AbbozzaLogger.out("PluginManager: " + path + " requested",AbbozzaLogger.INFO);
        
        if (path.equals("/plugins/plugins.js")) {
            Enumeration<Plugin> plugins = _plugins.elements();
            while ( plugins.hasMoreElements()) {
                Plugin plugin = plugins.nextElement();
                response = response + "\n" + plugin.getJavaScript();
            }
            responseHeaders.set("Content-Type", "text/javascript");
        } else {
            response = "Found plugins:";
            Enumeration<Plugin> plugins = _plugins.elements();
            while ( plugins.hasMoreElements()) {
                Plugin plugin = plugins.nextElement();
                response = response + "\n\t" + plugin.getId();
            }
            responseHeaders.set("Content-Type", "text/plain");
        }
        exchg.sendResponseHeaders(200, response.length());
        os.write(response.getBytes());
        os.close();
    }
    
}