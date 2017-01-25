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
import de.uos.inf.did.abbozza.AbbozzaLogger;
import de.uos.inf.did.abbozza.AbbozzaServer;
import de.uos.inf.did.abbozza.Tools;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author michael
 */
public class LocaleHandler extends AbstractHandler {
    
    public LocaleHandler(AbbozzaServer abbozza) {
        super(abbozza);
    }

    @Override
    public void handle(HttpExchange exchg) throws IOException {
        sendResponse(exchg, 200, "text/xml", Tools.documentToString(getLocales()));
    }

    private Document getLocales() {
        System.out.println("AAAA");
        // Read the xml file for the global feature
        Document localeXml = null;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        
        String locale = this._abbozzaServer.getConfiguration().getLocale();
        Document globalLocale = getLocale("/js/languages/" + locale + ".xml");
        Document systemLocale = getLocale("/js/abbozza/" + this._abbozzaServer.getSystem() + "/languages/" + locale + ".xml");
        
        if (systemLocale != null ) {
            Node root = globalLocale.adoptNode(systemLocale.getDocumentElement());
            globalLocale.appendChild(root);                
        }
        
        Document pluginLocale = AbbozzaServer.getPluginManager().getLocales(locale);

        if (pluginLocale != null ) {
            System.out.println(Tools.documentToString(pluginLocale));
        System.out.println("BB");
            Node root = globalLocale.adoptNode(pluginLocale.getDocumentElement());
        System.out.println("CC");
            globalLocale.appendChild(root);                
        System.out.println("DD");
        }
        System.out.println("BBBB");
        
        System.out.println(Tools.documentToString(globalLocale));
        System.out.println("CCCC");
        return globalLocale;
    }

    private Document getLocale(String path) {
        Document localeXml = null;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        
        try {
            AbbozzaLogger.out("LocaleHandler: Loading locale from " + path,AbbozzaLogger.INFO);
            InputStream stream = this._abbozzaServer.getJarHandler().getInputStream(path);
            
            builder = factory.newDocumentBuilder();
            localeXml = builder.parse(stream);            
        } catch (Exception ex) {
            AbbozzaLogger.out("LocaleHandler: " + path + " not found");
            localeXml = null;
        }
       
        return localeXml;
    }
    
}
