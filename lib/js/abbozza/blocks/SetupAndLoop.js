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
 * @fileoverview The blocks for the main operations setup() and loop()
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */

Abbozza.FuncSetup = {
	symbols : null,
	name: "setup",
	
  	init: function() {
	    this.setHelpUrl(Abbozza.HELP_URL);
    	this.setColour(ColorMgr.getCatColor("cat.FUNC"));
    	this.appendDummyInput()
        	.appendField(_("SETUP"));
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

	generateCode : function(generator) {
 		
 		var statements = generator.statementToCode(this, 'STATEMENTS', "   ");
 		
 		var code = "";

 		code = code + "void setup() {\n";
 		code = code + this.symbols.toCode("   ");
                code = code + "###setuphook###\n";
 		code = code + Abbozza.blockMain.generateSetupCode(generator);
 		code = code + statements;
 		code = code + "\n}\n";
 		return code;
 	},
 	
 	/*
 	check : function(block) {
 		return "Test";
 	},
 	*/
 	
 	compose : function(topBlock) {
 		Abbozza.composeSymbols(topBlock,this);
 	},

 	decompose : function(workspace) {
 		return Abbozza.decomposeSymbols(workspace,this,_("LOCALVARS"),false);
 	},
 	
 	mutationToDom: function() {
 		// Abbozza.log("variables to Dom")
 		var mutation = document.createElement('mutation');
 		if (this.symbols != null) mutation.appendChild(this.symbols.toDOM());
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
 					this.setSymbolDB(new SymbolDB(null));
 				}
 				this.symbols.fromDOM(child);
 				// Abbozza.log(this.symbols);
 			}
 		}
	},
	
	
	updateLook: function() {
		
		var no = 0;
		while ( this.getInput("VAR"+no) ) {
			this.removeInput("VAR"+no);
			no = no+1;
		}
 		no = 0;
		while ( this.getInput("PAR"+no) ) {
			this.removeInput("PAR"+no);
			no = no+1;
		}

		var entry;
		var variables = this.symbols.getVariables(true);
 		for ( var i = 0; i < variables.length; i++ ) {
 			entry = variables[i];
   			this.appendDummyInput("VAR"+i).appendField(_(entry[1]) + " " + entry[0] + Abbozza.lenAsString(entry[2]));
   			if ( this.getInput("STATEMENTS")) this.moveInputBefore("VAR"+i,"STATEMENTS");
 		}		
	}


};


Blockly.Blocks['func_setup'] = Abbozza.FuncSetup;


Abbozza.FuncLoop = {
	symbols: null,
	name: "loop",

  	init: function() {
	    this.setHelpUrl(Abbozza.HELP_URL);
    	this.setColour(ColorMgr.getCatColor("cat.FUNC"));
    	this.appendDummyInput()
	        .appendField(_("LOOP"));
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
	
 	generateCode : function(generator) {
 		var statements = generator.statementToCode(this, 'STATEMENTS', "   ");

 		var code = "";

 		code = code + "void loop() {\n";
 		code = code + this.symbols.toCode("   ");
 		
 		code = code + statements;
 		
 		code = code + "\n}\n";
 		return code;
 	},
 	
 	
 	/*
 	check : function(block) {
 		return "Test";
 	},
 	*/
 	
 	compose : function(topBlock) {
 		Abbozza.composeSymbols(topBlock,this);
 	},

 	decompose : function(workspace) {
 		return Abbozza.decomposeSymbols(workspace,this,_("LOCALVARS"),false);
 	},

 	mutationToDom: function() {
 		// Abbozza.log("variables to Dom")
 		var mutation = document.createElement('mutation');
 		if (this.symbols != null ) mutation.appendChild(this.symbols.toDOM());
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
 					this.setSymbolDB(new SymbolDB(null));
 				}
 				this.symbols.fromDOM(child);
 				// Abbozza.log(this.symbols);
 			}
 		}
	},
	
	
	updateLook: function() {
		
		var no = 0;
		while ( this.getInput("VAR"+no) ) {
			this.removeInput("VAR"+no);
			no = no+1;
		}
 		no = 0;
		while ( this.getInput("PAR"+no) ) {
			this.removeInput("PAR"+no);
			no = no+1;
		}

		var entry;
		var variables = this.symbols.getVariables(true);
 		for ( var i = 0; i < variables.length; i++ ) {
 			entry = variables[i];
   			this.appendDummyInput("VAR"+i).appendField(_(entry[1]) + " " + entry[0] + Abbozza.lenAsString(entry[2]));
   			if ( this.getInput("STATEMENTS")) this.moveInputBefore("VAR"+i,"STATEMENTS");
   			no++;
 		}		
	}

};

Blockly.Blocks['func_loop'] = Abbozza.FuncLoop;

/**
 * 
 * @type typeTest block
 * 
 */
Abbozza.TestBlock = {
      	init: function() {
            this.setHelpUrl(Abbozza.HELP_URL);
            this.setColour(ColorMgr.getCatColor("cat.FUNC"));
            var text = new Blockly.FieldTextInput("0");
            var slider = new Blockly.FieldSlider(42,-90,90,1,text,null);
            this.appendDummyInput()
                    .appendField("Test")
                    .appendField(text,"Value")
                    .appendField(slider);
  	}
  
}
Blockly.Blocks['test'] = Abbozza.TestBlock;


Abbozza.FuncMain = {
    title : "<title>",
	symbols : null,
	// test: "",
	
	init : function() {
		this.setHelpUrl(Abbozza.HELP_URL);
		this.setColour(ColorMgr.getCatColor("cat.VAR"));
		this.setPreviousStatement(false);
		this.setNextStatement(false);
                this.appendDummyInput().appendField(_("SETUP"));
		this.appendStatementInput("SETUP_STATEMENTS")
			.setCheck("STATEMENT");
                this.appendDummyInput().appendField(_("LOOP"));
		this.appendStatementInput("LOOP_STATEMENTS")
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
	
	setTitle : function(title) {
                // this.title = title;
		// this.getField_("TITLE").setText(title);
	},
	
	
	compose: function(topBlock) {
		Abbozza.composeSymbols(topBlock,this);
	},

	decompose: function(workspace) {
		return Abbozza.decomposeSymbols(workspace,this,_("GLOBALVARS"),false);
	},


	generateSetupCode : function(generator) {
		/*
		this.checkDevices();
 		var statements = generator.statementToCode(this, 'DEVICES', "   ");
 		return statements;
                */
               return "";
	},
	
	generateCode : function(generator) {
                // Generate code for global variables

                var code ="";
                var var_code = this.symbols.toCode("");
                
                if ( var_code != "") {
                   code = "/*\n * Globale Variablen\n */\n";
 		   code = code + this.symbols.toCode("") + "\n";
               }
 		
 		var setup_statements = generator.statementToCode(this, 'SETUP_STATEMENTS', "   ");

                code = code + "/* \n * Die setup Operation\n */\n";
 		code = code + "void setup() {\n";
 		// code = code + this.symbols.toCode("   "); // local variables
                code = code + "###setuphook###\n";
 		code = code + Abbozza.blockMain.generateSetupCode(generator);
 		code = code + setup_statements;
 		code = code + "\n}\n\n\n";

                var loop_statements = generator.statementToCode(this, 'LOOP_STATEMENTS', "   ");

                code = code + "/*\n * Die Hauptschleife\n */\n";
 		code = code + "void loop() {\n";
 		// code = code + this.symbols.toCode("   "); // local variables
 		
 		code = code + loop_statements;
 		
 		code = code + "\n}\n";
 		return code;
                
  	},
 	
 	check : function(block) {
 		return "Test";
 	},
 	

 	mutationToDom: function() {
 		// Abbozza.log("variables to Dom")
 		var mutation = document.createElement('mutation');
 		var title = document.createElement('title');
 		title.appendChild(document.createTextNode(this.title));
 		mutation.appendChild(title);
 		mutation.appendChild(this.symbols.toDOM());
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
	

        /*
	getDevices : function() {
		var devices = [];
		var current = this.getInputTargetBlock("DEVICES");
		while (current) {
			devices.push(current);
			current = current.getNextBlock();
		}
		return devices;
	},
	
	getDevicesByType : function(devtype) {
		var devices = [];
		var current = this.getInputTargetBlock("DEVICES");
		while (current) {
			if ( current.devtype == devtype ) {
				// devices.push([current.getFieldValue("NAME"),current]);
				devices.push([current.getFieldValue("NAME"),current.getFieldValue("NAME")]);
			}
			current = current.getNextBlock();
		}
		if ( devices.length == 0) {
			return [["<name>","<name>"]];
		}
		return devices;
	},
	
	
	getDevice: function(name) {
		var current = this.getInputTargetBlock("DEVICES");
		while (current) {
			if ( current.getFieldValue("NAME") == name ) {
				return current;
			}
			current = current.getNextBlock();
		}
		return null;
	},
	
	checkDevices: function() {
		var devices = this.getDevices();
		for (var i = devices.length-1; i>= 1; i--) {
			for (var j = 0; j < i; j++) {
				if ( devices[i].getFieldValue("NAME") == devices[j].getFieldValue("NAME")) {
					ErrorMgr.addError(devices[i],_("err.DEVICE"));
					break;
				}
			}
		}
	}
	*/
};


Blockly.Blocks['main'] = Abbozza.FuncMain;

