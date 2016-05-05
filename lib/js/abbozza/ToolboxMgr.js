/**
 * @license abbozza!
 * 
 * File: ToolboxMgr.js
 * 
 * This singleton object manages the toolbox
 * 
 * Copyright 2015 Michael Brinkmeier
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

ToolboxMgr = {
    features: null,
    /* colors: [["cat.FUNC", 310],
        ["cat.VAR", 345],
        ["cat.LOGIC", 210],
        ["cat.MATH", 230],
        ["cat.TEXT", 160],
        ["cat.COND", 210],
        ["cat.LOOPS", 120],
        ["cat.INOUT", 80],
        ["cat.DEVICES", 270],
        ["cat.SERIAL", 80],
        ["color.BOOLEAN", 210], // blue
        ["color.NUMBER", 120], // green
        ["color.DECIMAL", 80], // alt. green
        ["color.STRING", 40], // brown
        ["color.DEVICE", 270], // blue
        ["color.VAR", 345],
        ["color.FUNC", 310]
    ], */
    
    /**
     * This operation rebuilds the toolbox. If init is set to true,
     * the toolbox is injected into the document. If it is set to false,
     * the toolbox is updated. 
     */
    rebuild: function (init) {
        this.features = Connection.getXMLSynced("js/abbozza/features.xml");

        var myToolbox = ToolboxMgr.toolboxFromFeature("feat.BASE");
        if (Configuration.getParameter("option.operations") == "true")
            ToolboxMgr.mergeFeature(myToolbox, "feat.FUNC");
        /* if (Configuration.getParameter("option.serialRate") == "true") {
         ToolboxMgr.mergeFeature(myToolbox, "feat.SERIAL");
         ToolboxMgr.mergeFeature(myToolbox, "feat.SERIALRATE");
         }*/
        if (Configuration.getParameter("option.devices") == "true") {
            ToolboxMgr.mergeFeature(myToolbox, "feat.DEVICES");
        }
        if (Configuration.getParameter("option.serial") == "true") {
            ToolboxMgr.mergeFeature(myToolbox, "feat.SERIAL");
        }
        
        var entries = myToolbox.getElementsByTagName("category");
        for (var i = 0; i < entries.length; i++) {
            var cat = entries[i].id;
            entries[i].setAttribute("name", _(cat));
            var children = entries[i].getElementsByTagName("block");
            for (var j = 0; j < children.length; j++) {
                var child = children[j];
                var type = children[j].getAttribute("type");
                if ( Blockly.Blocks[type] && child.parentNode == entries[i] ) {
                    Blockly.Blocks[type]._category = cat;
                }
            }
        }
        
        for ( var type in Blockly.Blocks ) {
            if ( Blockly.Blocks[type]._category == undefined ) {
               if (type.startsWith("var_") || type.startsWith("arr_")) {
                   Blockly.Blocks[type]._category = "cat.VAR";
               } else {
                   Blockly.Blocks[type]._category = "cat.FUNC";
               }
            }
        }
       
        
        if (init) {
            Blockly.inject(document.body, {
                toolbox: myToolbox,
                trashcan: true,
                scrollbars: true
            });
        } else {
            Blockly.mainWorkspace.updateToolbox(myToolbox);
        }
        
        this.colorize();
    },
    
    /**
     * This operation builds a toolbox from a given feature.
     */
    toolboxFromFeature: function (name) {

        var feature = this.features.getElementById(name);

        if (feature == null)
            return null;

        return feature.cloneNode(true);

        /*
         if (feature == null)
         return null;
         
         var toolbox = feature.cloneNode(true);
         
         var entries = toolbox.getElementsByTagName("category");
         for (var i = 0; i < entries.length; i++) {
         entries[i].setAttribute("name", _(entries[i].id));
         }
         
         return toolbox;
         */
    },
    
    /**
     * This operation merges the feature specific toolbox into the given one.
     */
    mergeFeature: function (toolbox, name) {

        var feature = this.features.getElementById(name);
        if (feature == null)
            return;
        
        var cats = feature.getElementsByTagName("category");

        for (i = 0; i < cats.length; i++) {
            var toolcat = null;
            var tcats = toolbox.getElementsByTagName("category");
            var j = 0;
            while ((j < tcats.length) && (cats[i].id != tcats[j].id)) {
                // console.log(j + " " + tcats.length + " " + cats[i].id + " " + tcats[j].id);
                j++;
            }
            if (j < tcats.length) {
                for (var k = 0; k < cats[i].children.length; k++) {
                    tcats[j].appendChild(cats[i].children[k].cloneNode(false));
                }
            } else {
                toolbox.appendChild(cats[i].cloneNode(true));
            }
        }
    },
    
    /**
     * This operation colorizes the toolbox
     */
    colorize: function () {
        var treeItems = document.querySelectorAll('[role="treeitem"]');
        for (var i = 0; i < treeItems.length; i++) {
            var id = treeItems[i].id + ".label";
            var el = document.getElementById(id);
            var content = el.textContent;
            var cat = this.getCategory(content);
            var color = ColorMgr.getCatColor(cat);
            treeItems[i].style.backgroundColor = Blockly.makeColour(color);
            treeItems[i].style.color = "#ffffff";
        }

        /*
        console.log(Blockly.mainWorkspace.toolbox_.tree_.element_);
        for ( var catKey in Blockly.mainWorkspace.toolbox_.tree_.children_ ) {
            var cat = Blockly.mainWorkspace.toolbox_.tree_.children_[catKey];
            for ( var key in cat.blocks ) {
                var block = cat.blocks[key];
                // Blockly.Blocks[type].setColour(ColorMgr.getCatColor(cat));
            }
        }
        */
    },
    
    /**
     * This operation returns the color of a specific category.
     */
    /*
    getColor: function (cat) {

        var i = 0;
        while (i < this.colors.length) {
            if (this.colors[i][0] == cat)
                return this.colors[i][1];
            i++;
        }
        return 310;
    },*/
    
    getCategory: function (id) {
        var children = Configuration.xmlLocale
                .getElementsByTagName("categories")[0].childNodes;
        for (var i = 0; i < children.length; i++) {
            if (children[i].textContent == id)
                return children[i].id;
        }

        return null;
    }
        

    /*
     getCategory : function(name) {
     var cats = Abbozza.xmlLocale.getElementByTagName("category");
     for (var i = 0; i < cats.length; i++) {
     if (cats.childNodes[i].textContent == name) {
     return cats.childNodes[i].id;
     }
     }
     return null;
     },
     */

    /*
     addCategory : function(id, color) {
     var toolbox = this.getToolbox();
     var category = this.getCategory(toolbox, id);
     
     if (category)
     return;
     
     var child = document.createElement("category");
     child.id = id;
     child.setAttribute("name", displayname);
     toolbox.appendChild(child);
     
     Blockly.mainWorkspace.updateToolbox(toolbox);
     this.colorize();
     },
     */

    /*
     addBlock : function(id, category) {
     var toolbox = this.getToolbox();
     var category = this.getCategory(toolbox, id);
     
     if (!category)
     return;
     
     var child = document.createElement("block");
     child.setAttribute("type", "id");
     },
     */

    /*
     addBlockWithValues : function(id, category, values) {
     var toolbox = this.getToolbox();
     var category = this.getCategory(toolbox, id);
     
     if (!category)
     return;
     
     var child = document.createElement("block");
     child.setAttribute("type", "id");
     for (var i = 0; i < values.length; i++) {
     var name = values[i][0];
     var vid = values[i][1];
     var ch = document.createElement("value");
     ch.setAttribute("name", name);
     var block = document.createElement("block");
     block.setAttribute("type", vid);
     ch.appendChild(block);
     child.appendChild(ch);
     }
     },
     */

    /*
     getToolbox : function() {
     return document.getElementById("toolbox");
     },
     
     */


}