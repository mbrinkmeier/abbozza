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
            this.appendDummyInput().appendField(_("block.MAIN"));
            this.appendStatementInput("STATEMENTS")
		.setCheck("STATEMENT");
            this.setTooltip('');
            this.setMutator(new DynamicMutator( function() {
                if ( Configuration.getParameter("option.noArrays") == "true") {
                    return ['var_num_noconn', 'var_string_noconn','var_decimal_noconn', 'var_boolean_noconn'];			
                } else if ( Configuration.getParameter("option.linArrays") == "true" ) {
                    return ['var_num', 'var_string','var_decimal', 'var_boolean','arr_dimension_noconn'];			
                } else {
                    return ['var_num', 'var_string','var_decimal', 'var_boolean','arr_dimension'];
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
                var var_code = generator.variablesToCode(this.symbols,"");
                
                if ( var_code != "") {
 		   code = code + var_code;
                }
 		
                // Generate the statements of the main program
                var statements = generator.statementToCode(this, 'STATEMENTS', "");
 		code = code + statements;
 		
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
 		if (this.symbols) mutation.appendChild(this.symbols.toDOM());
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

Abbozza.repr = {
	init : function() {
            this.setHelpUrl(Abbozza.HELP_URL);
            this.setColour(ColorMgr.getCatColor("cat.VAR"));
            this.setPreviousStatement(false);
            this.setNextStatement(false);
            this.setOutput(true,"STRING");
            this.appendValueInput("VALUE")
                    .appendField(_("block.asString"))
                    .setCheck(["NUMBER","DECIMAL","BOOLEAN"]);
            this.setTooltip('');
	},

    	generateCode : function(generator) {
                // Generate code for global variables
                var code ="";
                // Generate the statements of the main program
                var value = generator.valueToCode(this, "VALUE");
 		code = "repr(" + value + ")";
 		
 		return code;                
  	}
}

Blockly.Blocks['text_as_string'] = Abbozza.repr;

/**
 * Block for the definition of local variables, parameters and return type
 */
Abbozza.FunctionDeclControl = {
    init: function () {
        this.setHelpUrl(Abbozza.HERLP_URL);
        this.setColour(ColorMgr.getColor("cat.FUNC"));
        this.appendDummyInput()
                .appendField(_("func.NAME"))
                .appendField(new FieldNameInput("<name>", Abbozza.globalSymbols, Abbozza.globalSymbols.FUN_SYMBOL), "NAME");
        this.appendDummyInput()
                .appendField(_("func.RETURNTYPE"))
                .appendField(new Blockly.FieldDropdown(Abbozza.Generator.typeList), "TYPE");
        this.appendDummyInput()
                .setAlign(Blockly.ALIGN_RIGHT)
                .appendField(_("func.PARAMETER"));
        this.appendStatementInput("PARS").setCheck("VAR_DECL");
        this.appendDummyInput()
                .setAlign(Blockly.ALIGN_RIGHT)
                .appendField(_("LOCALVARS"));
        this.appendStatementInput("VARS").setCheck("VAR_DECL");
        this.setTooltip('');
    },
    setTitle: function (title) {
    }

};

Blockly.Blocks['func_decl_control'] = Abbozza.FunctionDeclControl;


Abbozza.VariableDeclControl = {
    // variables: null,
    title: "",
    init: function () {
        this.setHelpUrl(Abbozza.HERLP_URL);
        this.setColour(ColorMgr.getCatColor("cat.VAR"));
        this.appendDummyInput().appendField(this.title, "TITLE");
        this.appendStatementInput("VARS")
                .setCheck("VAR_DECL");
        this.setTooltip('');
    },
    setTitle: function (title) {
        this.title = title;
        this.getField("TITLE").setText(title);
    }
};

Blockly.Blocks['devices_control'] = Abbozza.VariableDeclControl;

