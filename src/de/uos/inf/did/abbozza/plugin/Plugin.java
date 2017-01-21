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

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.uos.inf.did.abbozza.AbbozzaLogger;
import de.uos.inf.did.abbozza.AbbozzaServer;
import de.uos.inf.did.abbozza.Tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class implements a basic plugin for abbozza! 
 * It provides a handler for requests with path "/plugin/<name>/".
 * 
 * @author michael
 */
public class Plugin {

    private File _path;
    private String _id;
    private String _name;
    private String _description;
    private Vector<File> _js;
    private Node _options;
    private Node _feature;
    private PluginHandler _handler;
    
    /**
     * Instantiate the pugin. 
     * 
     * @param path 
     */
    public Plugin(File path) {
        this._handler = null;
        this._path = path;
        this._id = null;
        this._js = new Vector<File>();
        this._feature = null;
        readXML();
    }
    
    /**
     * Read the plugin details from plugin.xml
     */
    private void readXML() {
        try {
            File xmlFile = new File(this._path + "/plugin.xml");
            Document pluginXml;
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            StringBuilder xmlStringBuilder = new StringBuilder();
            pluginXml = builder.parse(new FileInputStream(xmlFile));

            NodeList plugins = pluginXml.getElementsByTagName("plugin");
            if ( plugins.getLength() > 0) {
                Node root = plugins.item(0);
                this._id = root.getAttributes().getNamedItem("id").getNodeValue();
                NodeList children = root.getChildNodes();
                Node child;
                for ( int i = 0; i < children.getLength(); i++) {
                    child = children.item(i);
                    String childName = child.getNodeName();
                    
                    // Get the display name
                    if (childName.equals("name")) {
                        this._name = child.getTextContent();
                        
                    // Get the description
                    } else if (childName.equals("description")) {
                        this._description = child.getTextContent();                        
                        
                    // Get the option trees
                    } else if (childName.equals("options")) {
                        _options = child.cloneNode(true);
                    } else if (childName.equals("js") ) {
                        String fileName = ((Element) child).getAttributes().getNamedItem("file").getNodeValue();
                        if ( fileName != null ) {
                            File file = new File(this._path.toString()+ "/" + fileName);
                            this._js.add(file);
                        }
                        
                    // Get the handler calss
                    } else if (childName.equals("handler")) {
                        String className = ((Element) child).getAttributes().getNamedItem("class").getNodeValue();                            
                        URLClassLoader classLoader = new URLClassLoader(new URL[]{_path.toURI().toURL()}, AbbozzaServer.class.getClassLoader() );
                        Class handlerClass = classLoader.loadClass(className);
                        this._handler = (PluginHandler) handlerClass.newInstance();
                        this._handler.setPlugin(this);

                    // Get the feature tree
                    } else if (childName.equals("feature")) {
                        this._feature = child;
                    }
                    
                }
            }
        } catch (Exception ex) {
            this._id = null;
            AbbozzaLogger.err("Plugin: \n" + ex.toString());
            AbbozzaLogger.stackTrace(ex);
        }
    }
    
    /**
     * Returns the id of the plugin
     * 
     * @return the id
     */
    public String getId() {
        return this._id;
    }

    /**
     * Returns the display name of the plugin
     * 
     * @return the display name
     */
    public String getName() {
        return this._name;
    }

    /**
     * Returns a description of the plugin
     * 
     * @return the description
     */
    public String getDescription() {
        return this._description;
    }
    
    /**
     * Returns a concatenation of all javascript files belonging to the plugin
     * 
     * @return the concatenated javascript files
     */
    public String getJavaScript() {
        String code = "";
        for (int i = 0; i < _js.size() ; i++) {
            try {
                code = code + "\n" + new String(Tools.readBytes(_js.get(i)));
            } catch (IOException ex) {
                AbbozzaLogger.err("Plugin: " + _js.get(i).toString() + " coulod not be read!");
            }
        }
        return code;
    }
    
    /**
     * Gets a http handler for the context /plugin/<id>
     * 
     * @return The http handler
     */
    public PluginHandler getHttpHandler() {
        return this._handler;
    }
    
    /**
     * Returns an document node describing all options of the plugin
     * 
     * @return the document node
     */
    public Node getOptions() {
        return this._options.cloneNode(true);
    }
    
    public boolean isActivated() {
        return AbbozzaServer.getConfig().getOption(_id + ".enabled");
    }

    public Node getFeature() {
        return this._feature.cloneNode(true);
    }

}
