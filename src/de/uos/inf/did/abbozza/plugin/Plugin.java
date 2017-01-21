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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.uos.inf.did.abbozza.AbbozzaLogger;
import de.uos.inf.did.abbozza.AbbozzaServer;
import de.uos.inf.did.abbozza.Tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
public class Plugin implements HttpHandler {

    private File _path;
    private String _id;
    private String _name;
    private String _description;
    private Vector<File> _js;
    private Node _options;
    
    public Plugin(File path) {
        this._path = path;
        this._id = null;
        this._js = new Vector<File>();
        readXML();
    }
    
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
                for ( int i = 0; i < children.hashCode(); i++) {
                    child = children.item(i);
                    String childName = child.getNodeName();
                    if (childName.equals("name")) {
                        this._name = child.getTextContent();
                    } else if (childName.equals("description")) {
                        this._description = child.getTextContent();                        
                    } else if (childName.equals("options")) {
                        _options = child.cloneNode(true);
                    } else if (childName.equals("js") ) {
                        String fileName = ((Element) child).getAttributes().getNamedItem("file").getNodeValue();
                        if ( fileName != null ) {
                            File file = new File(this._path.toString()+ "/" + fileName);
                            this._js.add(file);
                        }
                    }
                }
            }
        } catch (ParserConfigurationException ex) {
            this._id = null;
            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            this._id = null;
            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            this._id = null;
            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        } 
    }
    
    public String getId() {
        return this._id;
    }

    public String getName() {
        return this._name;
    }

    public String getDescription() {
        return this._description;
    }
    
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
    
    @Override
    public void handle(HttpExchange he) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Node getOptions() {
        return this._options.cloneNode(true);
    }
    
}
