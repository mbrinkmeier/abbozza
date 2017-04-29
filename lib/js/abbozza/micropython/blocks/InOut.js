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
 * @fileoverview Blocks for in and output control
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */



/**
 * block providing constants representing the digital level
 */
Abbozza.InOutLevel = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendDummyInput()
        .appendField(new Blockly.FieldDropdown([[_("io.HIGH"), "1"], [_("io.LOW"), "0"]]), "LEVEL");
    this.setOutput(true,["BOOLEAN","NUMBER"]);  
    this.setPreviousStatement(false);
    this.setNextStatement(false);
    this.setTooltip('');
  },
  
  
  generateCode : function(generator) {
  	var code = this.getFieldValue('LEVEL');

  	return code;
  }
}

Blockly.Blocks['inout_level'] = Abbozza.InOutLevel;

/**
 * Reading a digital input
 */
Abbozza.InOutDigitalRead = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendDummyInput()
    	.appendField(_("io.READDPIN"))
        .appendField(new PinDropdown(PinDropdown.DIGITAL),"PIN");
    this.setInputsInline(true);
    this.setOutput(true,["BOOLEAN","NUMBER"]);  
    this.setPreviousStatement(false);
    this.setNextStatement(false);
    this.setTooltip('');
  },
  
  
  generateCode : function(generator) {
 	var pin = generator.fieldToCode(this,"PIN");
  	// generator.checkValue(this,pin,0,Abbozza.board.digitalPins-1,"PIN_ERROR");
  	
   // if (AbbozzaGenerator.ERROR) return null;

  	var code = pin + ".read_digital()";
  	return code;
  }
}

Blockly.Blocks['inout_digital_read'] = Abbozza.InOutDigitalRead;


/**
 * Writing to a digital output
 */
Abbozza.InOutDigitalWrite = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendValueInput("LEVEL")
        .appendField(__("io.SETDPIN",0))
        .appendField(new PinDropdown(PinDropdown.DIGITAL),"PIN")
    // this.appendValueInput("LEVEL")
        .appendField(__("io.SETDPIN",1))
        .setCheck(["BOOLEAN","NUMBER"]);
    this.setOutput(false);  
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('');
  },
  
  
  generateCode : function(generator) {
    var level = generator.valueToCode(this,'LEVEL');
    var pin = generator.fieldToCode(this,"PIN");
    // generator.checkValue(this,pin,0,Abbozza.board.digitalPins-1,"PIN_ERROR");
      	
    if (AbbozzaGenerator.ERROR) return null;
  
    var code = pin + ".write_digital(" + level + ")";
    return code;
  }
  
}

Blockly.Blocks['inout_digital_write'] = Abbozza.InOutDigitalWrite;

/**
 * Read from an analogue input
 */
Abbozza.InOutAnalogRead = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendDummyInput()
    	.appendField(_("io.READAPIN"))
    	.appendField(new PinDropdown(PinDropdown.ANALOG), "PIN");
    this.setInputsInline(true);
    this.setOutput(true,"NUMBER");  
    this.setPreviousStatement(false);
    this.setNextStatement(false);
    this.setTooltip('');
  },
  
  
  generateCode : function(generator) {
 	var pin = generator.fieldToCode(this,"PIN");
  	// generator.checkValue(this,pin,0,Abbozza.board.digitalPins-1,"PIN_ERROR");
  	
   if (AbbozzaGenerator.ERROR) return null;

  	var code = pin + ".read_analog()";
  	return code;
  }
}

Blockly.Blocks['inout_analog_read'] = Abbozza.InOutAnalogRead;

/**
 * Write to an analog output (PWM)
 */
Abbozza.InOutAnalogWrite = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendValueInput("VALUE")
        .appendField(__("io.SETAPIN",0))
        .appendField(new PinDropdown(PinDropdown.ANALOG),"PIN")
        .appendField(__("io.SETAPIN",1)) 
        .setCheck("NUMBER");
    this.setOutput(false);  
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('');
  },
  
  
  generateCode : function(generator) {
  	var value = generator.valueToCode(this,'VALUE');
 	var pin = generator.fieldToCode(this,"PIN");
  	// generator.checkValue(this,pin,0,Abbozza.board.digitalPins-1,"PIN_ERROR");
  	
        if (AbbozzaGenerator.ERROR) return null;

  	var code = pin + ".write_analog(" + value + ")";
  	return code;
  }
}

Blockly.Blocks['inout_analog_write'] = Abbozza.InOutAnalogWrite;



/**
 * Reading a digital input
 */
Abbozza.InOutTouched = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendDummyInput()
        .appendField(new PinDropdown(PinDropdown.TOUCH),"PIN")
    	.appendField(_("io.TOUCHEDPIN"));
    this.setInputsInline(true);
    this.setOutput(true,["BOOLEAN","NUMBER"]);  
    this.setPreviousStatement(false);
    this.setNextStatement(false);
    this.setTooltip('');
  },
  
  
  generateCode : function(generator) {
 	var pin = generator.fieldToCode(this,"PIN");
  	// generator.checkValue(this,pin,0,Abbozza.board.digitalPins-1,"PIN_ERROR");
  	
   // if (AbbozzaGenerator.ERROR) return null;

  	var code = pin + ".is_touched()";
  	return code;
  }
}

Blockly.Blocks['inout_touched'] = Abbozza.InOutTouched;
