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


var ReservedWords = {};

ReservedWords.check = function(text) {
    var myRegExp = new RegExp(".*," + text+ ",.*", 'i');
    return this.list.match(myRegExp);
}

/**
 * A simple constructor setting the default values.
 */

AbbozzaGenerator = function() {
        this.serialRequired = false;
        this.globalVars = "";
        this.preSetup = "";
	this.libraries = [];
        this.setupHookCode = "";
        this.startMonitor = false;
};

/**
 * Adds an entry to the list of imported libraries
 * @param {type} library The library
 * @param {type} symbol The symbol from the library
 * @returns {undefined}
 */
AbbozzaGenerator.prototype.addLibrary = function(library,symbol) {
    if (!symbol) symbol = "*";
    // Check if the library was added already
    for (var i = 0; i < this.libraries.length; i++ ) {
        if ( (this.libraries[i][0] == library) && (this.libraries[i][1] == symbol) ) return;
    }
    this.libraries.push(Array(library,symbol));
}

/**
 * Generates the code for an library entry.
 * Must be overwritten by subclasses.
 * 
 * @param {array} library The library entry. 0: name of the library, 1: symbol
 * @returns {string}
 */
AbbozzaGenerator.prototype.libraryToCode = function(library) {
    return "## AbbozzaGenerator.libraryToCode must be definde !!! ##";
}

/**
 * Generate the code for all imported libraries.
 * 
 * @returns {string}
 */
AbbozzaGenerator.prototype.librariesToCode = function() {
    var code="";
    for (var i = 0; i < this.libraries.length; i++ ) {
        code = code + this.libraryToCode(this.libraries[i]) + "\n";
    }
    return code;
}

/**
 * Adds a variable declaration of a global variable.
 * @returns {string}
 */
AbbozzaGenerator.prototype.addGlobalVar = function() {
}

/**
 * This method checks if the block has a method called generateCode.
 * If this is the case, it is called for code generation.
 * 
 * If the method does not exist, the corresponding entry of AbbozzaCode is
 * used. Each entry is an array with the following entries:
 * 
 * 0 : The code template, using "#" as positions for replacements
 * 1 : An array describing the replacements ordered by appearance in the 
 *     template
 * 2 : A function called for code generation. It is executed by the block
 *     and the generator is given as its only parameter.
 * 
 * Each description of an replacement in [1] can be of one of the following
 * types:
 * 
 * "V_NAME" : The replacement is the code generateb by the value at the 
 *            blocks ValueInput NAME. If a certain type has to be enforced,
 *            it is added as third part: V_NAME_TYPE
 *            
 * "F_NAME" : The replacement is the code generateb by the value at the 
 *            blocks FieldValue NAME.
 *            
 * "S_NAME" : The replacement is the code generateb by the value at the 
 *            blocks Statement input NAME
 *            
 * function : The replacement is returned by the given function, which is
 *            executed on the blocj, with the generator as its single parameter
 */
AbbozzaGenerator.prototype._toCode = function(block) {
    if (block.generateCode) return block.generateCode(this);
    
    if (AbbozzaCode[block.type]) {
        // Get the template
        var code = AbbozzaCode[block.type][0];
        var values = AbbozzaCode[block.type][1];
        var func = null;
        if ( AbbozzaCode[block.type].length == 3 ) {
            func = AbbozzaCode[block.type][2];
            if ( typeof func == "function") {
                func.call(block,this);
            }
        }
        if (values) {
            // Iterate through values
            for (var i = 0; i < values.length ; i ++ ) {
                // Check type of value
                var replacement = "";
                if ( typeof values[i] == "function") {
                    replacement = values[i].call(block,this);
                } else if (typeof values[i] == "string") {
                    var name = values[i].substring(2);
                    if ( values[i].match(/^F_.*/) ) {
                       replacement = this.fieldToCode(block,name);
                    } else if ( values[i].match(/^V_.*/) ) {
                      var type = null;
                       // Check for enforced type
                       if ( name.match(/.*_.*/) ) {
                           var tokens = name.split("_");
                           type = tokens[1];
                           name = tokens[0];
                       }
                       replacement = this.valueToCode(block,name,type);
                    } else if ( values[i].match(/^S_.*/) ) {
                       replacement = this.statementToCode(block,name,"   ");
                    }
                } else {
                    replacement = "";
                }
                code = code.replace(/#/,replacement);
            }
        }   
        return(code);
    } else {
        return "";
    }
}

