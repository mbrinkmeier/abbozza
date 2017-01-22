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
package de.uos.inf.did.abbozza;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;

/**
 *
 * @author michael
 */
public class AbbozzaLocale {

    private static String locale;
    private static Properties entries;

    /**
     * Set the current locale and reads it from the xml-files
     * 
     * @param loc 
     */
    public static void setLocale(String loc) {
        entries = new Properties();
        locale = loc;
        
        addLocaleXml("/js/languages/" + locale + ".xml");
        addLocaleXml("/js/abbozza/" +  AbbozzaServer.getInstance().getSystem() + "/languages/" + locale + ".xml");
        // Document doc = AbbozzaServer.getPluginManager().getLocales(locale); 
        // addLocaleXml(doc);
    }

    /**
     * Adds the entries of a given locale-xml file
     * 
     * @param path 
     */
    public static void addLocaleXml(String path) {
        Document localeXml;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        if ( AbbozzaServer.getInstance().jarHandler == null ) return;
        
        try {
            AbbozzaLogger.out("Loading locale from " + path,4);
            InputStream stream = AbbozzaServer.getInstance().jarHandler.getInputStream(path);
            builder = factory.newDocumentBuilder();
            StringBuilder xmlStringBuilder = new StringBuilder();
            localeXml = builder.parse(stream);
            
            NodeList nodes = localeXml.getElementsByTagName("msg");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                String key = node.getAttributes().getNamedItem("id").getNodeValue();
                String entry = node.getTextContent();
                entries.setProperty(key, entry);
            }
        } catch (Exception ex) {
            AbbozzaLogger.out("AbbozzaLocale: " + path + " not found");
        }
    }
    
    /**
     * Adds the entries of a given Document
     * 
     * @param path 
     */
    public static void addLocaleXml(Document localeXml) {
        if (localeXml == null) return;
        
        try {           
            NodeList nodes = localeXml.getElementsByTagName("msg");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                String key = node.getAttributes().getNamedItem("id").getNodeValue();
                String entry = node.getTextContent();
                entries.setProperty(key, entry);
            }
        } catch (Exception ex) {
            AbbozzaLogger.stackTrace(ex);
        }
    }

    /**
     * gets the current locale
     * 
     * @return 
     */
    public static String getLocale() {
        return locale;
    }

    /**
     * Returns an entry of the current locale
     * 
     * @param key
     * @return 
     */
    public static String entry(String key) {
        return entries.getProperty(key, key);
    }

    /**
     * Returns an entry of the current locale and replaces a '#' by the
     * given string.
     * 
     * @param key The key of the entry
     * @param value The replacement for '#' in the found string
     * @return 
     */
    public static String entry(String key, String value) {
        String res = entries.getProperty(key, key);
        res = res.replace("#", value);
        return res;
    }

}
