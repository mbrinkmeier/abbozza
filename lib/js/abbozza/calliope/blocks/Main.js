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
 * @fileoverview The blocks for the main block
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */


Abbozza.Main = {
    title : "<title>",
	symbols : null,
	// test: "",
	
	init : function() {
            this.setHelpUrl(Abbozza.HELP_URL);
            this.setColour(ColorMgr.getCatColor("cat.VAR"));
            this.setPreviousStatement(false);
            this.setNextStatement(false);
            this.appendDummyInput().appendField(_("MAIN"));
            this.appendStatementInput("STATEMENTS")
		.setCheck("STATEMENT");
            this.setTooltip('');
            this.setMutator(new DynamicMutator( function() {
                if ( Configuration.getParameter("option.noArrays") == "true") {
                    return ['devices_num_noconn', 'devices_string_noconn','devices_decimal_noconn', 'devices_boolean_noconn'];			
                } else if ( Configuration.getParameter("option.linArrays") == "true" ) {
                    return ['devices_num', 'devices_string','devices_decimal', 'devices_boolean','arr_dimension_noconn'];			
                } else {
                    return ['devices_num', 'devices_string','devices_decimal', 'devices_boolean','arr_dimension'];
                }
            }));
            this.setDeletable(false);
	},

        setSymbolDB : function(db) {
            this.symbols = db;
	},
	
	setTitle : function(title) {},
	
	
	compose: function(topBlock) {
            Abbozza.composeSymbols(topBlock,this);
	},

	decompose: function(workspace) {
            return Abbozza.decomposeSymbols(workspace,this,_("GLOBALVARS"),false);
	},


	generateSetupCode : function(generator) {
            return "";
	},
	
	generateCode : function(generator) {
                // Generate code for global variables
                var code ="";
                var var_code = generator.variablesToCode("");
                
                if ( var_code != "") {
                   code = "/*\n * Globale Variablen\n */\n";
 		   code = code + this.symbols.toCode("") + "\n";
                }
 		
                // Generate the statements of the main program
                var statements = generator.statementToCode(this, 'STATEMENTS', "   ");
 		
 		code = code + statements;
 		
 		return code;                
  	},
 	-
 	check : function(block) {
 		return "Test";
 	},
 	

 	mutationToDom: function() {
 		// Abbozza.log("variables to Dom")
 		var mutation = document.createElement('mutation');
 		var title = document.createElement('title');
 		title.appendChild(document.createTextNode(this.title));
 		mutation.appendChild(title);
 		if ( this.symbols) mutation.appendChild(this.symbols.toDOM());
 		// Abbozza.log(mutation);
		return mutation;
	},

	domToMutation: function(xmlElement) {
		var child;
 		// Abbozza.log("variables from Dom")
 		for ( var i = 0; i < xmlElement.childNodes.length; i++) {
 			child = xmlElement.childNodes[i];
 			// Abbozza.log(child);
 			if ( child.tagName == 'symbols') {
 				if ( this.symbols == null ) {
 					this.setSymbolDB(new SymbolDB(null,this));
 				}
 				this.symbols.fromDOM(child);
 				// Abbozza.log(this.symbols);
 			} else if ( child.tagName == 'title' ) {
 				// Abbozza.log("title : " + child.textContent);
 				this.setTitle(child.textContent);
 			}
 		}
	}
	
};


Blockly.Blocks['main'] = Abbozza.Main;

