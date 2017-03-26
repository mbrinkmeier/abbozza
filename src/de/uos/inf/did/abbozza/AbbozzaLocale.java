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
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author michael
 */
public class AbbozzaLocale {

    private static String locale;
    private static Document localeXml;
    // private static Hashtable entries;
    // private static Properties entries;

    /**
     * Set the current locale and reads it from the xml-files
     * 
     * @param loc 
     */
    public static void setLocale(String loc) {
        // entries = new Properties();
        locale = loc;
        localeXml = buildLocale();
        // entries = new Hashtable<String,String>();
        
        NodeList nodes = localeXml.getElementsByTagName("msg");
        for (int i = 0; i < nodes.getLength(); i++ ) {
            Element node = (Element) nodes.item(i);
            node.setIdAttribute("id", true);
        }
        
        
        // addLocaleXml("/js/languages/" + locale + ".xml");
        // addLocaleXml("/js/abbozza/" +  AbbozzaServer.getInstance().getSystem() + "/languages/" + locale + ".xml");
        // Document doc = AbbozzaServer.getPluginManager().getLocales(locale); 
        // addLocaleXml(doc);
    }

        private static Document buildLocale() {
        try {
            // Read the xml file for the global feature
            
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setSchema(schema);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document localeXml = builder.newDocument();
            Element root = localeXml.createElement("languages");
            localeXml.appendChild(root);
            
            String locale = AbbozzaLocale.locale;
            Document globalLocale = fetchLocale("/js/languages/" + locale + ".xml");
                    
            Element foundElement = null;
            if ( globalLocale != null ) {
                NodeList languages = globalLocale.getElementsByTagName("language");
                for ( int i = 0; i < languages.getLength(); i++ ) {
                    Element element = (Element) languages.item(i);
                    if ( (foundElement == null) || (locale.equals(element.getAttribute("id"))) ) {
                        foundElement = element;
                    }
                }
                if ( foundElement != null ) {
                    Element child = (Element) foundElement.cloneNode(true);
                    localeXml.adoptNode(child);
                    root.appendChild(child);
                    child.setAttribute("id","global_" + locale);
                }
            }
            
            Document systemLocale = fetchLocale("/js/abbozza/" + AbbozzaServer.getInstance().getSystem() + "/languages/" + locale + ".xml");
            
            foundElement = null;
            if ( globalLocale != null ) {
                NodeList languages = systemLocale.getElementsByTagName("language");
                for ( int i = 0; i < languages.getLength(); i++ ) {
                    Element element = (Element) languages.item(i);
                    if ( (foundElement == null) || (locale.equals(element.getAttribute("id"))) ) {
                        foundElement = element;
                    }
                }
                if ( foundElement != null ) {
                    Element child = (Element) foundElement.cloneNode(true);
                    localeXml.adoptNode(child);
                    root.appendChild(child);
                    child.setAttribute("id",AbbozzaServer.getInstance().getSystem() + "_" + locale);
                }
            }
            
            // Add locales from Plugins
            AbbozzaServer.getPluginManager().addLocales(locale,root);
            
            return localeXml;
        } catch (Exception ex) {
            AbbozzaLogger.stackTrace(ex);
            return null;
        }
    }

    private static Document fetchLocale(String path) {
        Document localeXml = null;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        
        try {
            AbbozzaLogger.out("LocaleHandler: Loading locale from " + path,AbbozzaLogger.INFO);
            InputStream stream = AbbozzaServer.getInstance().getJarHandler().getInputStream(path);
            
            builder = factory.newDocumentBuilder();
            localeXml = builder.parse(stream);            
        } catch (Exception ex) {
            AbbozzaLogger.out("LocaleHandler: " + path + " not found");
            localeXml = null;
        }
       
        return localeXml;
    }

    /**
     * Adds the entries of a given locale-xml file
     * 
     * @param path 
     */
    /*
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
    */
    
    /**
     * Adds the entries of a given Document
     * 
     * @param path 
     */
    /*
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
    */
    
    /**
     * gets the current locale
     * 
     * @return 
     */
    public static String getLocale() {
        return locale;
    }

    public static Document getLocaleXml() {
        return AbbozzaLocale.localeXml;
    }
    
    /**
     * Returns an entry of the current locale
     * 
     * @param key
     * @return 
     */
    public static String entry(String key) {
        Element el = localeXml.getElementById(key);
        if ( el == null ) return key;
        return el.getTextContent();
        // entries.getProperty(key, key);
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
        // String res = entries.getProperty(key, key);
        String res = entry(key);
        res = res.replace("#", value);
        return res;
    }

}
