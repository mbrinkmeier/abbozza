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

import de.uos.inf.did.abbozza.AbbozzaLogger;
import de.uos.inf.did.abbozza.AbbozzaServer;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author michael
 */
public class PluginManager {

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
        AbbozzaLogger.out("PluginManager: " + path,AbbozzaLogger.INFO);    
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
                    AbbozzaLogger.out("PluginManager: Directory " + dirs[i].toString() + " found",AbbozzaLogger.INFO);                
                }
            }
        }   

        // Check global dir
        path = new File(this._abbozza.getLocalPluginPath());
        AbbozzaLogger.out("PluginManager: " + path,AbbozzaLogger.INFO);        
        dirs = path.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        
        if (dirs != null) {
            for (int i=0; i < dirs.length; i++) {
                plugin = new Plugin(dirs[i]);
                if (plugin.getId() != null ) {
                     AbbozzaLogger.out("PluginManager: Directory " + dirs[i].toString() + " found",AbbozzaLogger.INFO);
                }
            }
        }
        AbbozzaLogger.out("PluginManager: plugins detected",AbbozzaLogger.INFO);        
        
    }
    
}
