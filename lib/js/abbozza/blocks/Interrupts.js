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
    this.setColour(ColorMgr.getCatColor("cat.INT"));
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
    this.setColour(ColorMgr.getCatColor("cat.INT"));
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
    this.setColour(ColorMgr.getCatColor("cat.INT"));
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

Blockly.Blocks['int_interrupts'] = Abbozza.Interrupts;
Blockly.Blocks['int_no_interrupts'] = Abbozza.NoInterrupts;
Blockly.Blocks['int_attach'] = Abbozza.AttachInterrupt;
