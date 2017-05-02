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
  // if ( this.serialRequired == true ) {
  //  this.setupHookCode = this.setupHookCode + "Serial.begin(" + Abbozza.serialRate + ");" + "\n";
  // }

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
               if ( this.libraries[i][0] && this.libraries[i][1] != "" ) {
                acode = acode + "from " + this.libraries[i][0] + " import " + this.libraries[i][1];
            } else {
                acode = acode + "import " + this.libraries[i][0];
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
 * Generates the list of variables in the symbolDB of the form
 * <type> <name><dimension>
 * <type> <name><dimension>
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
		["POWER", "**"]
	];
__dict = __keywords;

