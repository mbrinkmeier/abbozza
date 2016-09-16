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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import processing.app.PreferencesData;

/**
 *
 * @author michael
 */
public class AbbozzaConfig {

    private String sketchbookPath;
    private Properties config;

    // General configuration
    private int config_serverPort = 54242;
    private boolean config_autoStart = false;
    private boolean config_browserStart = true;
    private String config_browserPath = "";
    private String config_locale = "de_DE";
    private String config_updateUrl = "http://inf-didaktik.rz.uos.de/abbozza/current/";
    private boolean config_update = false;
    
    /**
     *  Reads the configuration from the given path
     */
    public AbbozzaConfig(String path) {
        sketchbookPath = path;
        read();
    }

    /**
     * Sets the default configuration
     */
    public AbbozzaConfig() {
        sketchbookPath = null;
        setDefault();
    }
    
    
    /**
     * This method reads the configuration from the file
     * 
     * @throws IOException                console.log(response);

     */
    public void read() {
        if ( sketchbookPath == null ) return;
        File prefFile = new File(sketchbookPath + "/tools/Abbozza/Abbozza.cfg");
        config = new Properties();
        try {
            config.load(new FileInputStream(prefFile));
            set(config);        
        } catch (IOException ex) {
            setDefault();
            write();
        }        
    }


    
    /**
     * Prepare the default configuration
     */
    public void setDefault() {    
        // Check for Abbozza.cfg in global dir
        String runtimePath = PreferencesData.get("runtime.ide.path");
        File defaultFile = new File(runtimePath + "/tools/Abbozza/Abbozza.cfg");
        config = new Properties();
        try {
            config.load(new FileInputStream(defaultFile));
            AbbozzaLogger.out("Reading default configuration from " + defaultFile.getAbsolutePath(),AbbozzaLogger.INFO);
            set(config);        
            write();
        } catch (IOException ex) {
            AbbozzaLogger.out("Setting internal default configuration.",AbbozzaLogger.INFO);
            config.remove("freshInstall");
            config_serverPort = 54242;
            config_autoStart = false;
            config_browserStart = true;
            config_browserPath = "";
            config_locale = System.getProperty("user.language") + "_" + System.getProperty("user.country");
            config_updateUrl = "http://inf-didaktik.rz.uos.de/abbozza/current/";
            config_update = false;
            storeProperties(config);
            setOption("operations", true);
            setOption("localVars", true);
            setOption("noArrays", false);
            setOption("linArrays", false);
            setOption("multArrays", true);
            setOption("devices", true);
            setOptionInt("loglevel",AbbozzaLogger.NONE);
            AbbozzaLogger.setLevel(AbbozzaLogger.NONE);
            setOptionStr("updateUrl",config_updateUrl);
            setOption("update",false);
            write();
        }        
    }
    
        

    public void set(Properties properties) {
        
        if ("true".equals(properties.get("freshInstall"))) {
            setDefault();
            return;
        }        

        config = (Properties) properties.clone();
        
        // Set general configuration
        config_autoStart = ("true".equals(properties.getProperty("autoStart","false")));
        config_browserStart = ("true".equals(properties.getProperty("startBrowser","true")));
        if (config.containsKey("serverPort")) {
            config_serverPort = Integer.parseInt(properties.getProperty("serverPort","54242"));
        }
        config_browserPath = properties.getProperty("browserPath","");
        config_locale = properties.getProperty("locale",System.getProperty("user.language") + "_" + System.getProperty("user.country"));
        config_updateUrl = properties.getProperty("updateUrl","http://inf-didaktik.rz.uos.de/abbozza/current/");
        config_update = "true".equals(properties.getProperty("update","false"));
        
        if (properties.getProperty("loglevel") != null) {
            AbbozzaLogger.setLevel(Integer.parseInt(properties.getProperty("loglevel","0")));
        } else {
            AbbozzaLogger.setLevel(AbbozzaLogger.NONE);
        }
    }
    
    
    /**
     * @throws IOException
     */
    public void write() {
        if ( sketchbookPath == null ) return;
        File prefFile = new File(sketchbookPath + "/tools/Abbozza/Abbozza.cfg");
        try {
            prefFile.createNewFile();
            Properties props = get();
            props.store(new FileOutputStream(prefFile), "abbozza! preferences");
            
        } catch (IOException ex) {
            AbbozzaLogger.err("Could not write configuration file " + sketchbookPath + "/tools/Abbozza/Abbozza.cfg");                
        }
    }


    /**
     * 
     */
    public Properties get() {
        Properties props = (Properties) config.clone();

        // Write General Configuration
        storeProperties(props);
        
        return props;
    }
    
   
    private void storeProperties(Properties props) {
        props.setProperty("autoStart", config_autoStart ? "true" : "false");
        props.setProperty("startBrowser", config_browserStart ? "true" : "false");
        props.setProperty("serverPort", Integer.toString(config_serverPort));
        props.setProperty("browserPath", config_browserPath);
        props.setProperty("locale", config_locale);
        props.setProperty("loglevel", Integer.toString(AbbozzaLogger.getLevel()));
        props.setProperty("updateUrl",config_updateUrl);
        props.setProperty("update",config_update ? "true" : "false");
    }
 
    
    public void apply(String options) throws IOException {
        options = options.replace('{',' ');
        options = options.replace('}',' ');
        options = options.replace(',','\n');
        options = options.trim();
        config.load(new ByteArrayInputStream(options.getBytes()));
    }
    
    
    /**
     * Options
     */ 
    public void setOption(String option, boolean value) {
        config.setProperty("option." + option, (value) ? "true" : "false");
    }

    public boolean getOption(String option) {
        String value = config.getProperty("option." + option);
        if (value == null) {
            return false;
        }
        return (value.equals("true")) ? true : false;
    }

    public void setOptionInt(String option, int value) {
        config.setProperty("option." + option,Integer.toString(value));
    }

    public int getOptionInt(String option) {
        String value = config.getProperty("option." + option);
        if (value == null) {
            return -1;
        }
        return (Integer.parseInt(value));
    }

    public void setOptionStr(String option, String value) {
        config.setProperty("option." + option,value);
    }

    public String getOptionStr(String option) {
        String value = config.getProperty("option." + option);
        return value;
    }

    /**
     * Retreive general configuration
     */
    
    public int getServerPort() {
        return config_serverPort;
    }

    public void setServerPort(int port) {
        config_serverPort = port;
    }
    
    public boolean startAutomatically() {
        return config_autoStart;
    }
    
    public void setAutoStart(boolean flag) {
        config_autoStart = flag;
    }
    
    public boolean startBrowser() {
        return config_browserStart;
    }

    public void setBrowserStart(boolean flag) {
        config_browserStart= flag;
    }
    
    public String getBrowserPath() {
        return config_browserPath;
    }
    
    public void setBrowserPath(String path) {
        config_browserPath = path;
    }
    
    public String getLocale() {
        return config_locale;
    }

    public void setLocale(String locale ) {
        config_locale = locale;
    }

    public String getUpdateUrl() {
        return config_updateUrl;
    }

    public void setUpdateUrl(String url) {
        config_updateUrl = url;
    }

    public void setUpdate(boolean flag) {
        config_update = flag;
    }
    
    public boolean getUpdate() {
        return config_update;
    }

}
