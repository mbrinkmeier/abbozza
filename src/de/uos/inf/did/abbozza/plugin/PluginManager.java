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
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.uos.inf.did.abbozza.AbbozzaLogger;
import de.uos.inf.did.abbozza.AbbozzaServer;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

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


    public void registerPluginHandlers(HttpServer server) {
        Enumeration<Plugin> plugins = _plugins.elements();
        while ( plugins.hasMoreElements()) {
            Plugin plugin = plugins.nextElement();
            server.createContext("/abbozza/plugin/" + plugin.getId(), plugin.getHttpHandler());
            AbbozzaLogger.out("PluginManager: Plugin " + plugin.getId() + " registered HttpHandler",AbbozzaLogger.INFO);
        }
    }
    
    
    public void mergeFeatures(Document features) {
        Enumeration<Plugin> plugins = _plugins.elements();
        Node root = features.getElementsByTagName("features").item(0);
        while ( plugins.hasMoreElements()) {
            Plugin plugin = plugins.nextElement();
            Node feature = plugin.getFeature();
            try {
            features.adoptNode(feature);
            root.appendChild(feature);
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        }
    }

    @Override
    public void handle(HttpExchange exchg) throws IOException {
        String response = "";
        String path = exchg.getRequestURI().getPath();
        OutputStream os = exchg.getResponseBody();
        Headers responseHeaders = exchg.getResponseHeaders();

        AbbozzaLogger.out("PluginManager: " + path + " requested",AbbozzaLogger.DEBUG);
        
        if (path.equals("/abbozza/plugins/plugins.js")) {
            Enumeration<Plugin> plugins = _plugins.elements();
            while ( plugins.hasMoreElements()) {
                Plugin plugin = plugins.nextElement();
                if ( plugin.isActivated() ) {
                    response = response + "\n" + plugin.getJavaScript();
                }
            }
            responseHeaders.set("Content-Type", "text/javascript");
        } else {
            response = "Found plugins:";
            Enumeration<Plugin> plugins = _plugins.elements();
            while ( plugins.hasMoreElements()) {
                Plugin plugin = plugins.nextElement();
                response = response + "\n\t" + plugin.getId();
                if ( plugin.isActivated() ) {
                    response = response + " [activated]";
                } else {
                    response = response + " [deactivated]";                    
                }
            }
            responseHeaders.set("Content-Type", "text/plain");
        }
        exchg.sendResponseHeaders(200, response.length());
        os.write(response.getBytes());
        os.close();
    }

}