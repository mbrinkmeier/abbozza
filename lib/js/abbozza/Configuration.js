/**
 * @license
 * abbozza!
 *
 * File: Configuration.js
 * 
 * Copyright 2015 Michael Brinkmeier
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
 * @fileoverview A singleton object managing the configuration of the abbozza! client.
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */

// Required for the pop-down menu entries
goog.require('Blockly.Msg');


/**
 * The configuration object stores the parameters and manages the locale.
 */
var Configuration = {
    locale: "de_DE",
    // --- deprectaed -- locales		: [],
    xmlLocale: null,
    parameters: []
}



/**
 * This operation loads the parameters from the abbozza! server.
 * It initializes the toolbox.
 */
Configuration.load = function () {

    // var waiting = true;

    Connection.getTextSynced("/abbozza/config",
            function (response) {
                Configuration._apply(response, true);
            },
            function (response) {
                console.log("error");
            }
    );

    Board.load(false);
}

/**
 * This operation loads the parameters from a given string.
 * It rebuilds the toolbox.
 * 
 * The sting has to be of the form 
 * { parameter=value , parameter=value, ... }
 */
Configuration.apply = function (config) {
    this._apply(config, false);
}


/**
 * This operation parses the string and stores the parameters.
 */
Configuration._apply = function (config, init) {
    config = config.replace(/{/g, "");
    config = config.replace(/}/g, "");
    var pars = config.split(",");
    for (var i = 0; i < pars.length; i++) {
        pars[i] = pars[i].trim();
        pair = pars[i].split("=");
        this.setParameter(pair[0], pair[1]);
    }

    // Check the Coloring strategy
    if (Configuration.getParameter("option.colorType") == "true") {
        ColorMgr._strategy = ColorMgr.BY_TYPE;        
    } else if (Configuration.getParameter("option.colorCategory") == "true") {
        ColorMgr._strategy = ColorMgr.BY_CATEGORY;
    } else {
        ColorMgr._strategy = ColorMgr.BY_DEFAULT;            
    }

    if (init) {
        ToolboxMgr.rebuild(true);
    } else {
        ToolboxMgr.rebuild(false);
    }


    /*
    // TODO remove/add device block
    var blocks = Blockly.mainWorkspace.getTopBlocks();
    var devBlock = null;
        
    if (Configuration.getParameter("option.devices") == "true") {
        for (var i = 0; i < blocks.length; i++) {
            if (blocks[i].type=="devices") {
                devBlock = blocks[i];
            }
        }

        if ( devBlock == null) {
            // Add new device block
            // Blockly.mainWorkspace.newBlock("devices","devices");
            Blockly.mainWorkspace.render();
        }
    } else {
        for (var i = 0; i < blocks.length; i++) {
            if (blocks[i].type == "devices") {
                // Remove block
                // Blockly.mainWorkspace.removeTopBlock(blocks[i]);
                // Blockly.mainWorkspace.render();
            }
        }
        
    }
    */
}

/**
 * This operation sets a parameter to the given value.
 * If it already exists the value is replaced. Otherwise its is
 * added.
 */
Configuration.setParameter = function (par, value) {
    var set = false;

    for (var i = 0; i < this.parameters.length; i++) {
        if (this.parameters[i][0] == par) {
            this.parameters[i][1] = value;
            set = true;
        }
    }
    if (!set)
        this.parameters.push([par, value]);

    // Apply the parameter, id possible
    switch (par) {
        case "locale" :
            this.setLocale(value);
            break;
        case "serverPort" :
            Abbozza.serverPort = value;
            break;
        default:
            break;
    }

}

/**
 * This operation gets the value of a given parameter.
 */
Configuration.getParameter = function (par) {
    for (var i = 0; i < this.parameters.length; i++) {
        if (this.parameters[i][0] == par) {
            return this.parameters[i][1];
        }
    }
    return null;
}

/**
 * This operation sets the locale.
 */
Configuration.setLocale = function (loc) {
    this.locale = loc;

    this.xmlLocale = Connection.getXMLSynced("/js/languages/" + this.locale + ".xml",
            function (xml) {
            },
            function (xml) {
            }
    );

    if (Blockly.mainWorkspace)
        Blockly.mainWorkspace.resize();

    Blockly.Msg.COLLAPSE_ALL = _("menu.collapse_blocks");
    Blockly.Msg.COLLAPSE_BLOCK = _("menu.collapse_block");
    Blockly.Msg.ADD_COMMENT = _("menu.add_comment");
    Blockly.Msg.DELETE_BLOCK = _("menu.delete_block");
    Blockly.Msg.DUPLICATE_BLOCK = _("menu.duplicate_block");
    Blockly.Msg.EXPAND_ALL = _("menu.expand_all");
    Blockly.Msg.EXPAND_BLOCK = _("menu.block_expand");
    Blockly.Msg.HELP = _("menu.help");
    Blockly.Msg.DELETE_X_BLOCKS = _("menu.delete_x_blocks");
    Blockly.Msg.ENABLE_BLOCK = _("menu.enable_block");
    Blockly.Msg.DISABLE_BLOCK = _("menu.disable_block");
    Blockly.Msg.EXTERNAL_INPUTS = _("menu.external_inputs");
    Blockly.Msg.INLINE_INPUTS = _("menu.inline_inputs");

}


/**
 * DEPRECATED OPERATIONS
 */
Configuration.readCookie = function () {
    var e = new Error("Configuratio readCookie deprecated");
    console.log(e.stack);
    /*
     var pars;
     var parameters;
     var par;
     var val;
     var i;
     // First check cookie
     parameters = document.cookie.split(";");
     for ( i = 0; i < parameters.length; i++) {
     par = (parameters[i].split('='))[0];
     val = (parameters[i].split('='))[1];
     this.setParameter(par,val);
     }*/
}


Configuration.readParameters = function () {
    var e = new Error("Configuratio readParameters deprecated");
    console.log(e.stack);
    /*
     var pars;
     var parameters;
     var par;
     var val;
     var i;
     pars = location.search.split('?')[1];
     if (pars) {
     parameters = pars.split('&');
     for ( i = 0; i < parameters.length; i++) {
     par = parameters[i].split('=')[0];
     val = parameters[i].split('=')[1];
     this.setParameter(par,val);
     }
     }
     */
}


Configuration.readBlock = function (block) {
    var e = new Error("Configuratio.readBlock deprecated");
    console.log(e.stack);
    /*
     var newLocale = block.getFieldValue("LOCALE");
     if ( newLocale != this.locale ) {
     this.setLocale(newLocale);
     }
     this.writeCookie();
     */
}


Configuration.setBlock = function (block) {
    var e = new Error("Configuratio.setBlock deprecated");
    // block.setFieldValue(this.locale,"LOCALE");
}


Configuration.writeCookie = function () {
    var e = new Error("Configuratio.writeCookie deprecated");
    console.log(e.stack);
    /*
     document.cookie = "";
     var d = new Date();
     var cookie = "";
     d.setTime(d.getTime() + (365*24*60*60*1000));	    
     var expires = "expires="+d.toUTCString();
     for ( var i = 0; i < this.parameters.length; i++ ) {
     cookie = cookie + this.parameters[i][0] + "=" + this.parameters[i][1] + ";"
     }
     document.cookie = cookie + expires;
     */
}


Configuration.checkLocale = function (loc) {
    Abbozza.showDeprecatedMsg("Configuration.checkLocale");
    /* for (var i = 0; i < this.locales.length; i++) {
     if ( loc == this.locales[i][1]) return true;
     }	
     return false;
     */
    return false;
}



Configuration.getLocales = function () {
    Abbozza.showDeprecatedMsg("Configuration.getLocales");
    // return this.locales;
    return null;
}


Configuration.readFeatures = function () {
    Abbozza.showDeprecatedMsg("Configuration.readFeatures");
    /*
     if (window.XMLHttpRequest)
     {
     xhttp=new XMLHttpRequest();
     }
     else // code for IE5 and IE6
     {
     xhttp=new ActiveXObject("Microsoft.XMLHTTP");
     }
     xhttp.open("GET","js/abbozza/features.xml", false);
     xhttp.send();
     this.features = xhttp.responseXML;
     */
}


Configuration.setLocales = function (xmlLocales) {
    var elements = xmlLocales.getElementsByTagName("locale");
    for (var i = 0; i < elements.length; i++) {
        this.locales.push([elements[i].textContent, elements[i].id]);
    }
}