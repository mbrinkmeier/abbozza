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

import de.uos.inf.did.abbozza.arduino.Abbozza;
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

    public static void setLocale(String loc) {
        entries = new Properties();
        locale = loc;
        Document localeXml;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            StringBuilder xmlStringBuilder = new StringBuilder();
            byte[] bytes = Abbozza.getInstance().getLocaleBytes(loc);

            ByteArrayInputStream input = new ByteArrayInputStream(bytes);
            localeXml = builder.parse(input);

            NodeList nodes = localeXml.getElementsByTagName("msg");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                String key = node.getAttributes().getNamedItem("id").getNodeValue();
                String entry = node.getTextContent();
                entries.setProperty(key, entry);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            AbbozzaLogger.out("loading of locale failed", AbbozzaLogger.ERROR);
        }
    }

    public static String getLocale() {
        return locale;
    }

    public static String entry(String key) {
        return entries.getProperty(key, key);
    }

    public static String entry(String key, String value) {
        String res = entries.getProperty(key, key);
        res = res.replace("#", value);
        return res;
    }

}
