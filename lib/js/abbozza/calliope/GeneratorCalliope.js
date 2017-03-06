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
 * @fileoverview The Code Generator of Abbozza.
 * 
 * It provides several methods for the generation of code from
 * the blocks.
 * 
 * It allows to add erros, which are diaplayed by icons.
 * 
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */

 /**
  * Each value block has to have at most one of the following types:
  * "NUMBER", "TEXT", "DECIMAL, "BOOLEAN"
  * 
  * Each block can have the following types in addition:
  * "VAR"
  * 
  * Plug types
  * "ARR_DIM", "VAR_DECL"
  */

/**
 * Definition of the dictionary of reserved words
 */

ReservedWords.list = ",setup,loop,if,else,for,switch,case,while,do,break,continue,return,goto," +
	"#define,#include,HIGH,LOW,INPUT,OUTPUT,INPUT_PULLUP,LED_BUILTIN,true,false," +
	"void,boolean,char,unsigned,byte,int,word,long,short,float,double,string,String," +
	"sizeof,PROGMEM,pinMode,digitalWrite,digitalRead,analogReference,analogRead," +
	"analogWrite,analogReadResolution,analogWriteResolutinon,tone,noTone,shiftOut," +
	"shiftIn,pulseIn,millis,micros,delay,delayMicroseconds,min,max,abs,constrain," +
	"map,pow,sqrt,sin,cos,tan,randomSeed,random,lowByte,highByte,bitRead,bitWrite,bitSet," +
	"bitClear,bit,attachInterrupt,detachInterrupt,interrupts,noInterrupts,Serial,Stream,"+
	"Keyboard,Mouse,"+
	"Serial.available,Serial.begin,Serial.end,Serial.find,Serial.findUntil,Serial.flush," +
	"Serial.parseFloat,Serial.parseInt,Serial.peek,Serial.print,Serail.println,Serial.read," +
	"Serial.readBytes,Serial.readBytesUntil,Serial.setTimeout,Serial.write,Serial.serialEvent," +
	"Stream.available,Stream.read,Stream.flush,Stream.find,Stream.findUnti,Stream.peek," +
	"Stream.readBytes,Stream.readBytesUntil,Stream.readString,Stream.readStringUntil,Stream.parseInt," +
	"Stream.parsefloat,Stream.setTimeout," +
	"Mouse.begin,Mouse.click,Mouse.end,Mouse.move,Mouse.press,Mouse.release,Mouse.isPressed," +
	"Keyboard.begin,Keyboard.end,Keyboard.press,Keyboard.print,Keyboard.println,Keyboard.release," +
	"Keyboard.releaseAll,Keyboard.write";


/**
 * This operation is the entry point for code generation.
 * It Iterates through the top blocks of the workspace, generates their
 * code and combines it. In addition it adds libraries and additional
 * required statements not directly generated. 
 * 
 * The genrated code is of the form
 * 
 *  from microbit import *
 *  <LIBRARIES>
 * 
 *  <PRESETUP_DEVICE_CODE>
 *  
 *  <GLOBAL VARIABLES>
 *
 *  <MAIN_CODE>
 *
 *  <FUNCTIONS GENERATED FROM BLOCKS>
 */
AbbozzaGenerator.prototype.workspaceToCode = function(opt_workspace) {
  ErrorMgr.clearErrors();	
  this.startGenerator = false;
    
  this.serialRequired = false;
  this.startMonitor = false;
    
  // Fetch all blocks from the workspace
  var workspace = opt_workspace || Blockly.mainWorkspace;
  this.preSetup = "";
  this.setupHookCode = "";
  this.libraries = [];
  var code = "";
  // this.init(workspace);
  var origBlocks = workspace.getTopBlocks(true);

  /**
   * Rearrange the top blocks:
   * blocks[0] : device block if present
   * blocks[1] : main block
   * blocks[i] : function declarations
   * 
   * Initialize device and maion block by null.
   */
  var blocks = [null,null];   
  for ( var i = 0; i<origBlocks.length; i++) {
    block = origBlocks[i];
    if ( block.type == "devices" ) {
    	blocks[0] = block;
    } else if ( block.type == "main" ) {
        blocks[1] = block;
    } else if ( block.type == "func_decl") {
        blocks.push(block);
    }
  }
  
  // Iterate through the blocks and generate the code
  for (var x = blocks.length-1; x >= 0; x--) {
    var block = blocks[x];
    if ( block ) {  // catching missing device block
        var line = this.topBlockToCode(block, "");
        if (line) {
            code = code + line;
        }
    }
  }

  // Check if some block requires the initialization of the serial communication.
  // If yes, add the code:
  // Serial.begin( <rate> );
  if ( this.serialRequired == true ) {
    this.setupHookCode = this.setupHookCode + "Serial.begin(" + Abbozza.serialRate + ");" + "\n";
  }

  // Add the setuphook for other blocks.
  // Each block may add some code to the setuphook. It is added in the 
  // order the blocks are called to generate their code.
  this.setupHookCode = "// setup hook\n" + this.setupHookCode;
  this.setupHookCode = this.setupHookCode.replace(/^/g,"\t");
  
  // Replace the setuphook by the constructed code.
  code = code.replace(/###setuphook###/g,"\t"+this.setupHookCode);
 
  // Prepend the variable declarations
  code = this.finish(code);
    
  // Final scrubbing of whitespace.
  code = code.replace(/^\s+\n/, '');
  code = code.replace(/\n\s+$/, '\n');
  code = code.replace(/[ \t]+\n/g, '\n');

  return code;
};


/**
 * Prepend the generated code with a general
 * comment, required libraries and pre setup code.
 * 
 * @param {string} code Generated code.
 * @return {string} Completed code.
 */
AbbozzaGenerator.prototype.finish = function(code) {
       // Generate the global import    
       acode = "from microbit import *\n";
       
       // Add required libraries in the form
       // from <library[0]> import <library[1]>
       if (this.libraries.length != 0 ) {
           for (var i = 0; i < this.libraries.length; i++) {
               if ( this.libraries[i][0] && this.libraries[i][0] != "" ) {
                acode = acode + "from " + this.libraries[i][0] + " import " + this.libraries[i][1];
            } else {
                acode = acode + "import " + this.libraries[i][1];
            }
           }
           acode = acode;
        }
       
       // Add pre setup code
       if (this.preSetup != "" ) {
           acode = acode + "/*\n * Vorbereitungen\n */\n" + this.preSetup + "\n\n";
       }    
       return acode +"\n" + code + "\n";
};


/**
 * Add one or more lines of pre setup code
 */
AbbozzaGenerator.prototype.addPreSetup = function(line) {
	this.preSetup = this.preSetup +"\n" + line;
}

/**
 * Add on or more lines of code in setup() 
 */
AbbozzaGenerator.prototype.addSetupCode = function(line) {
	this.setupHookCode = this.setupHookCode +"\n" + line;
}

/**
 * Add a required library. Each is a twodimensional arrya
 * [0]: name of the library
 * [1]: imported symbols (*)
 */
AbbozzaGenerator.prototype.addLibrary = function(lib) {
        for (var i = 0; i < this.libraries.length; i++ ) {
            if ((this.libraries[i][0] == lib[0]) && (this.libraries[i][1] == lib[1])) return;
        }
        console.log("adding  " + lib[0] + "," + lib[1]);
	this.libraries.push(lib);
}

/**
 * This operation generates the code of a top block.
 * It adds multi- and single-line comments before the code.
 */
AbbozzaGenerator.prototype.topBlockToCode = function(block) {
    
	var code = this._toCode(block);
        
        // Add a block comment before the generated code
	var comment = block.getCommentText();
	if ( comment.indexOf('\n') != -1 ) {
		comment = "/**\n * " + comment.replace(/\n/g,"\n * ");
		comment = comment + "\n */\n";
		code = comment + code + "\n";
	} else {
		// One comment line
 		if ( (comment != null) && (comment != "") ) {
 			comment = "// " + comment + "\n";
 		} else comment = "";
	 	code = comment +  code;
	}
 	return code;
}

/**
 * This operation generates the code for the given block.
 */
AbbozzaGenerator.prototype.blockToCode = function(block) {
    
        // Call the blocks code generation operation
	var code = this._toCode(block);
        
        // Add an inline comment
	var comment = block.getCommentText();
	if ( (comment != null) && (comment != "" )) {
		code = code + "\t//" + comment;
	}
	
	return code;
}

/**
 * This wrapper calls the blocks generateCode(<generator>) operation, 
 * if it is defined. Otherwise it uses the given entry in AbbozzaCode.
 */
//AbbozzaGenerator.prototype._toCode = function(block) {
//    if (AbbozzaCode[block.type]) {
//        // Get the template
//        var code = AbbozzaCode[block.type][0];
//        var values = AbbozzaCode[block.type][1];
//        var func = null;
//        if ( AbbozzaCode[block.type].length == 3 ) {
//            func = AbbozzaCode[block.type][2];
//            if (typeof func == "function") {
//                func.call(this,this);
//            }
//        }
//        if (values) {
//            // Iterate through values
//            for (var i = 0; i < values.length ; i ++ ) {
//                // Check type of value
//                var replacement = "";
//                if ( typeof values[i] == "function") {
//                    replacement = values[i].call(block,this);
//                } else if (typeof values[i] == "string") {
//                    var name = values[i].substring(2);
//                    if ( values[i].match(/^F_.*/) ) {
//                       replacement = this.fieldToCode(block,name);
//                    } else if ( values[i].match(/^V_.*/) ) {
//                       replacement = this.valueToCode(block,name);                       
//                    } else if ( values[i].match(/^S_.*/) ) {
//                       replacement = this.statementToCode(block,name,"    ");
//                    }
//                } else {
//                    replacement = "";
//                }
//                code = code.replace(/#/,replacement);
//            }
//        }   
//        return(code);
//    } else if (block.generateCode) {
//        return block.generateCode(this);
//    } else {
//        return "";
//    }
//}

/**
 * Generates a string for a symbol of the form
 * <type> <name><arraydim> \t //<comment>
 */
AbbozzaGenerator.prototype.symbolToCode = function(symbol) {
    var name = symbol[0];
    var type = symbol[1];
    var len = symbol[2];
    var code = "";
    
    if ( type == "NUMBER" ) {
        code = name + " = " + this.lenAsTuple(len,"0");
    } else if ( type == "DECIMAL" ) {
        code = name + " = " + this.lenAsTuple(len,"0.0"); 
    } else if ( type == "STRING" ) {
        code = name + ' = ' + this.lenAsTuple(len,'""');         
    } else if ( type == "BOOLEAN" ) {
        code = name + " = " + this.lenAsTuple(len,"False"); 
    }
    // var code = keyword(type) + " " + name + Abbozza.lenAsString(len);
    
    return code;
}


AbbozzaGenerator.prototype.lenAsTuple = function(len,value) {
    if ( len == null ) return value;
    
    var tuple = value;
    var dim = ""
    
    for ( var i = len.length-1; i >= 0 ; i--) {
        dim = tuple; 
        for ( var j = 1; j < len[i]; j++) {
            dim = dim + "," + tuple;
        }
        tuple = "[" + dim + "]";
    }
    
    return "array(" + tuple + ")";
}


/**
 * Generates a string of symbols of the form
 * <symbol> <seperator> <symbol> <separator> ... <symbol>
 * 
 * <separator> = "," for parameters
 * <separator> = "" for local variables
 */
AbbozzaGenerator.prototype.symbolsToCode = function(symbols, separator, prefix) {
    var comment;
    var pars = "";
    if (symbols.length > 0) {
        if (symbols.length == 1) separator = "";
        pars = this.symbolToCode(symbols[0]);
        comment = symbols[0][4];
        if (comment && comment != "" ) {
            code = code + separator + "\t//" + comment.replace(/\n/g, " ");
        }    
        for ( var i=1; i < symbols.length; i++) {
            if ( i == symbols.length - 1) separator = "";
            comment = symbols[i][4];
            pars = pars + separator + "\t//"  + comment.replace(/\n/g, " ")
                    + this.symbolToCode(symbols[i]);
        }
    } else {
        return "";
    }
}
    
/**
 * Generates the list of variables in the symbolDB of the form
 * <type> <name><dimension>;
 * <type> <name><dimension>;
 * ...
 */
AbbozzaGenerator.prototype.variablesToCode = function(symbols,prefix) {
    var code = "";
    var variables = symbols.getVariables(true);
    for ( var i = 0; i < variables.length; i++ ) {
        var entry = variables[i];
 	code = code + prefix + this.symbolToCode(entry); // keyword(entry[1]) + " " + entry[0] + Abbozza.lenAsString(entry[2]) + ";";
 	if (( entry[4] != "") && (entry[4] != null)) 
            code = code + "\t// " + entry[4].replace(/\n/g," ");
        code = code + "\n";
    }
    return code;
}

/**
 * Generates a list of parameters for functions.
 */
AbbozzaGenerator.prototype.parametersToCode = function(symbols,prefix) {
    var parameters = symbols.getParameters(true);
    var pars = "";
    var entry;
    
    if (parameters.length > 0) {
        entry = parameters[0];
        pars = " " + entry[0];
        for (var i = 1; i < parameters.length; i++) {
            entry = parameters[i];
            pars = pars + ", " + entry[0];
        }
        pars = pars + " ";
    } else {
        pars = "";
    }
    
    return pars;
}

/**
 * This operation generates the code of a sequence of statements connected to
 * 'block' at the input 'name'. 'prefix' is added to the beginning of each line. 
 */
AbbozzaGenerator.prototype.statementToCode = function(block, name, prefix) {
	var code = "\n";
        
        // Iterate through all statements
	var current = block.getInputTargetBlock(name);
        if (!current) return "";
	while (current) {
		var line = this.blockToCode(current);
		if ( line )
			code = code + line + "\n";
		current = current.getNextBlock();
	}
        // Add the prefix in front of the first line
        // and replace each newline by newline + prefix
        // code = 
	// code = code.replace(/\n$/g,"");
	code = code.replace(/\n/g,"\n"+prefix);
        code = code.trim();
        code = "\n" + prefix + code + "\n";
        // code = code + "\n";
        
	return code;
}

/**
 * This operation generates the code from a value input of a given block.
 * The type of the code is not checked.
 * 
 * block: The block whose code has to be generated
 * name: The name of the value input
 * defaultVal: The default value to be returned, if the input does not produce code.
 */
AbbozzaGenerator.prototype.valueToCodeUnchecked = function(block,name,defaultVal) {

    // Return the default value, if the input doesn't exist
    if ( block.getInput(name) == null ) {
		return defaultVal;
    }

    // Get the block connected to the input
    var target = block.getInputTargetBlock(name);

    // If it doesn't exist, return the default value
    if ( target == null ) {
        return defaultVal;
    }

    // Return the blocks code.
    return this._toCode(target);
}

/**
 * This operation generates the code of a block connected to the
 * input <name> of the given <block>. It adds a type cast if opt_enforcedType
 * is given. null is returned, if input the input does not exist or no block
 * is connected to it. In this case an error is set.
 */
AbbozzaGenerator.prototype.valueToCode = function(block,name,opt_enforcedType) {
    
	if ( block.getInput(name) == null ) {
		ErrorMgr.addError(block, _("err.NOINPUT"));
		return null;
	}
	var target = block.getInputTargetBlock(name);
		
	if ( target == null ) {
		ErrorMgr.addError(block,_("err.EMPTYINPUT"));
		return null;
	}
	
	var code = this._toCode(target);
	if ( opt_enforcedType ) {
		code = this.enforceType(code,opt_enforcedType);
	}
	
	return code;
}

/**
 * This operation returns the code generated by the field <name>. 
 * If the field doesn't exist or contains a placeholder ( <default>,
 * <name> or ???), it sets an error.
 */
AbbozzaGenerator.prototype.fieldToCode = function(block,name) {
    var content = block.getFieldValue(name);
    if (content == null )
        ErrorMgr.addError(block,_("err.NOVALUE"));
    if ( (content == "<default>") || ( content == "???") || (content == "<name>") ) 
        ErrorMgr.addError(block,_("err.DEFAULT_VALUE"));     
    return keyword(content);
}

/**
 * This operation retreives the type of a value input.
 */
AbbozzaGenerator.prototype.getTypeOfValue = function(block,name) {
	if ( block.getInput(name) == null ) {
		ErrorMgr.addError(block,_("err.NOVALUE"));
		return null;
	}
	var target = block.getInputTargetBlock(name);
	
	if ( target == null ) {
		ErrorMgr.addError(block,_("err.EMPTYVALUE"));
		return null;
	}
	
	var check = target.outputConnection.check_;
	if (!goog.isArray(check)) {
      check = [check];
	}
	for ( var i = 0; i < check.length; i++) {
		if ( (check[i] == "NUMBER") || (check[i] == "TEXT") || (check[i] == "DECIMAL") || (check[i] == "BOOLEAN")) {
			return check[i];
		}
	}
	return check[0];
}


/**
 * This operation adds a type cast to the given code.
 */
AbbozzaGenerator.prototype.enforceType = function(code,enfType) {
	switch(enfType) {
		case "NUMBER":
			code =  keyword("NUMBER") + "(" + code +")";
			break;
		case "STRING":
		case "TEXT":
			code = "repr(" + code +")";
			break;
		case "DECIMAL":
			code = keyword("DECIMAL") + "(" + code +")";
			break;
		case "BBOLEAN":
			code = "(" + keyword("NUMBER") + "(" + code +") != 0 )";
			break;
	}	
        return code;
}

/**
 * Naked values are top-level blocks with outputs that aren't plugged into
 * anything.  A trailing semicolon is needed to make this legal.
 * @param {string} line Line of generated code.
 * @return {string} Legal line of code.
 */
AbbozzaGenerator.prototype.scrubNakedValue = function(line) {
  return line + ';\n';
};

/**
 * Encode a string as a properly escaped JavaScript string, complete with
 * quotes.
 * @param {string} string Text to encode.
 * @return {string} JavaScript string.
 * @private
 */
AbbozzaGenerator.prototype.quote_ = function(string) {
  // TODO: This is a quick hack.  Replace with goog.string.quote
  string = string.replace(/\\/g, '\\\\')
                 .replace(/\n/g, '\\\n')
                 .replace(/'/g, '\\\'');
  return '\'' + string + '\'';
};

/**
 * Common tasks for generating JavaScript from blocks.
 * Handles comments for the specified block and any connected value blocks.
 * Calls any statements following this block.
 * @param {!Block} block The current block.
 * @param {string} code The JavaScript code created for this block.
 * @return {string} JavaScript code with comments and subsequent blocks added.
 * @private
 */
AbbozzaGenerator.prototype.scrub_ = function(block, code) {
  var commentCode = '';
  // Only collect comments for blocks that aren't inline.
  if (!block.outputConnection || !block.outputConnection.targetConnection) {
    // Collect comment for this block.
    var comment = block.getCommentText();
    if (comment) {
      commentCode += this.prefixLines(comment, '// ') + '\n';
    }
    // Collect comments for all value arguments.
    // Don't collect comments for nested statements.
    for (var x = 0; x < block.inputList.length; x++) {
      if (block.inputList[x].type == Blockly.INPUT_VALUE) {
        var childBlock = block.inputList[x].connection.targetBlock();
        if (childBlock) {
          var comment = this.allNestedComments(childBlock);
          if (comment) {
            commentCode += this.prefixLines(comment, '// ');
          }
        }
      }
    }
  }
  var nextBlock = block.nextConnection && block.nextConnection.targetBlock();
  var nextCode = this.blockToCode(nextBlock);
  return commentCode + code + nextCode;
};

/**
 * Replaces marks by Field and input values.
 * 
 * fields: ##name##
 * inputs #name#
 */
/*
AbbozzaGenerator.prototype.replace = function(block, pattern) {

	// Search fields
	var pos = 0;
	var value = "";
	var name = "";
	while ( (pos = pattern.indexOf("##")) >= 0) {
		name = pattern.substring(pos+2);
		name = name.substring(0,name.indexOf("##"));
		value = block.getFieldValue(name);
		pattern.replace("##"+name+"##",value);
	}
	
	return pattern;
}
*/

/*
AbbozzaGenerator.prototype.checkValue = function(block,value,min,max,msg) {
		
	console.log("AbbozzaGenerator.checkValue");
	if ( (value < min) || (value > max) ) {
		if (this.ERROR_BLOCK == null) {
			console.log(block);
			this.setError(block,_(msg));
		}
	}

}
*/

AbbozzaGenerator.prototype.combine = function(text,args) {
	var pattern;
	for ( var i = 0 ; i < args.length ; i++ ) {
		pattern = "/#" + i + "/g";
		text.replace(pattern,args[i]);
		console.log(pattern);
	}
	return text;
}




AbbozzaGenerator.prototype.setError = function (block, text) {
	console.log("AbbozzaGenerator.setError deprecated");
	// this.ERROR = true;
	// this.ERROR_TEXT = text;
	// this.ERROR_BLOCK = block;
}

AbbozzaGenerator.prototype.typeList = function() {
	return [[_("VOID"),"VOID"],[_("NUMBER"),"NUMBER"],[_("STRING"),"STRING"],[_("DECIMAL"),"DECIMAL"],[_("BOOLEAN"),"BOOLEAN"]];
}

/**
 * The keywords for abbozza! labels
 */
__keywords = [
		["VOID","void"],
		["NUMBER","int"],
		["STRING","String"],
		["DECIMAL","double"],
		["BOOLEAN","boolean"],
		["TRUE","True"],
		["FALSE","False"],
		["AND","and"],
		["OR","or"],
		["EQUALS","=="],
		["INEQUAL","!="],
		["LESS","<"],
		["LESSEQ","<="],
		["GREATER",">"],
		["GREATEREQ",">="],		
                ["ROUND","round"],
                ["FLOOR","math.floor"],
                ["CEIL","math.ceil"],
		["ABS", "math.fabs"],
		["SQRT", "math.sqrt"],
		["SIN", "math.sin"], 
		["COS", "math.cos"],
		["TAN", "math.tan"],
		["ASIN", "math.asin"], 
		["ACOS", "math.acos"],
		["ATAN", "math.atan"],
		["MIN", "min"],
		["MAX", "max"],
		["PLUS", "+"],
		["MINUS", "-"],
		["MULT", "*"],
		["DIV", "/"],
		["MOD", "%"],
		["POWER", "^"]
	];
__dict = __keywords;


/**
 * This operation retrieves the correct keyword described by an abbozza! label.
 */
keyword = function(tag) {
	for (var i = 0; i < __keywords.length; i++) {
		if ( __keywords[i][0] == tag ) 
		return __keywords[i][1];
	}
	return tag;
}

setKeyword = function(key,word) {
    for (var i = 0; i < __keywords.length; i++) {
	if ( __keywords[i][0] == key ) {
            __keywords[i][1] = word;
        }
    }
}