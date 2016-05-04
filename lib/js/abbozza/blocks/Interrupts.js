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
 * @fileoverview 
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */

/**
 * Activating interrupts
 */
Abbozza.Interrupts = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(Abbozza.INOUT_COLOR);
    this.appendDummyInput()
        .appendField(__("int.INTERRUPTS",0));
    this.setInputsInline(true);   
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('');
  },
  
  generateCode : function(generator) {
	return "interrupts()";
  }
}

/**
 * Deactivating interrupts
 */
Abbozza.NoInterrupts = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(Abbozza.INOUT_COLOR);
    this.appendDummyInput()
        .appendField(__("int.NOINTERRUPTS",0));
    this.setInputsInline(true);   
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('');
  },
  
  generateCode : function(generator) {
	return "noInterrupts()";
  }
}

/**
 * Attaching operation to interrupt
 */
Abbozza.AttachInterrupt = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(Abbozza.INOUT_COLOR);
   this.appendDummyInput()
    	.appendField(__("int.ATTACH",0))
        .appendField(new PinDropdown(PinDropdown.INTERRUPT), "PIN");
    this.appendDummyInput("TYPE")
    	.appendField(__("int.ATTACH",1))
        .appendField(new Blockly.FieldDropdown([[_("int.CHANGE"),"CHANGE"],[_("int.LOW"), "LOW"], [_("int.HIGH"), "HIGH"], [_("int.RISING"), "RISING"],[_("int.FALLING"),"FALLING"]]), "TYPE");
    this.appendStatementInput("CODE");
    this.setInputsInline(true);   
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('');
  },
  
  generateCode : function(generator) {
	return "noInterrupts()";
  }
  
}

/**
 * Picks a specific character from a text
 *
Abbozza.TextCharAt = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(Abbozza.TEXT_COLOR);
    this.appendValueInput("POS")
        .appendField(__("txt.LETTERAT",0))
        .setCheck("NUMBER");
    this.appendValueInput("TEXT")
        .appendField(__("txt.LETTERAT",1))
        .setCheck("STRING");
    this.setInputsInline(true);
    this.setOutput(true, "STRING");
    this.setTooltip('');
  },
  
  generateCode : function(generator) {
  		var code = "";
  		var text = generator.valueToCode(this,"TEXT");
  		var pos = generator.valueToCode(this,"POS");
  		
  		code = text + ".charAt(" + pos + ")";
  		return code;
  }
}

/**
 * Concatenation of two strings
 *
Abbozza.TextConcat = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(Abbozza.TEXT_COLOR);
    this.appendValueInput("TEXT1")
        .setCheck("STRING");
    this.appendValueInput("TEXT2")
        .setCheck("STRING");
    this.setInputsInline(true);
    this.setOutput(true, "STRING");
    this.setTooltip('');
  },
  
  generateCode : function(generator) {
  		var code = "";
  		var text1 = generator.valueToCode(this,"TEXT1");
  		var text2 = generator.valueToCode(this,"TEXT2");
  		
  		code = text1 + "+" + text2;
  		return code;
  }
}
*/

Blockly.Blocks['int_interrupts'] = Abbozza.Interrupts;
Blockly.Blocks['int_no_interrupts'] = Abbozza.NoInterrupts;
Blockly.Blocks['int_attach'] = Abbozza.AttachInterrupt;
