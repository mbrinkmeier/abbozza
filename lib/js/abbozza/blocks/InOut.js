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
 * Set the mode of a specific pin
 */
Abbozza.InOutPinmode = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    // this.getColour = function() { return ColorMgr.getColour(this); };
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendValueInput("PIN")
    	.setCheck("NUMBER")
        .appendField(__("io.PINMODE",0));
    this.appendDummyInput()
    	.appendField(__("io.PINMODE",1))
        .appendField(new Blockly.FieldDropdown([[_("io.INPUT"), "INPUT"], [_("io.OUTPUT"), "OUTPUT"]]), "MODE");
    this.setInputsInline(true);   
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('');
  },
  
  
  generateCode : function(generator) {
  	var pin = generator.valueToCode(this,"PIN");
    
  	var mode = this.getFieldValue('MODE');

	code = "pinMode(" + pin + "," + mode + ");";
  	return code;
  }
}


/**
 * This block allows to choose a constant pin.
 */
Abbozza.InOutPin = {

  init: function() {
	 this.pinType = 0;
     var block = this;
    this.setHelpUrl(Abbozza.HELP_URL);
    // this.getColour = function() { return ColorMgr.getColour(this); };
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.pinField = new Blockly.FieldLabel(_("io.PIN"));
    this.appendDummyInput()
    	.appendField(this.pinField)
        .appendField(new PinDropdown(), "PIN");
    this.setOutput(true,"NUMBER");  
    this.setPreviousStatement(false);
    this.setNextStatement(false);
    this.setTooltip('');
  },
  
  setType: function(type) {
	 this.pinType = type;
  },
  
  generateCode : function(generator) {
    var code = generator.fieldToCode(this,'PIN');

    return code;
  }
}

/**
 * block providing constants representing the digital level
 */
Abbozza.InOutLevel = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendDummyInput()
        .appendField(new Blockly.FieldDropdown([[_("io.HIGH"), "HIGH"], [_("io.LOW"), "LOW"]]), "LEVEL");
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


/**
 * Reading a digital input
 */
Abbozza.InOutDigitalRead = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendValueInput("PIN")
    	.appendField(_("io.READDPIN"))
    	.setCheck("NUMBER");
    this.setInputsInline(true);
    this.setOutput(true,["BOOLEAN","NUMBER"]);  
    this.setPreviousStatement(false);
    this.setNextStatement(false);
    this.setTooltip('');
  },
  
  
  generateCode : function(generator) {
 	var pin = generator.valueToCode(this,"PIN");
  	// generator.checkValue(this,pin,0,Abbozza.board.digitalPins-1,"PIN_ERROR");
  	
   // if (AbbozzaGenerator.ERROR) return null;

  	var code = "digitalRead(" + pin + ")";
  	return code;
  }
}


/**
 * Writing to a digital output
 */
Abbozza.InOutDigitalWrite = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendValueInput("PIN")
        .appendField(__("io.SETDPIN",0))
        .setCheck("NUMBER");
    this.appendValueInput("LEVEL")
        .appendField(__("io.SETDPIN",1))
        .setCheck("NUMBER");
    this.setOutput(false);  
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('');
  },
  
  
  generateCode : function(generator) {
  	var level = generator.valueToCode(this,'LEVEL');
 	var pin = generator.valueToCode(this,"PIN");
  	// generator.checkValue(this,pin,0,Abbozza.board.digitalPins-1,"PIN_ERROR");
  	
    if (AbbozzaGenerator.ERROR) return null;

  	var code = "digitalWrite(" + pin + "," + level + ");";
  	return code;
  }
}


/**
 * Read from an analogue input
 */
Abbozza.InOutAnalogRead = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendValueInput("PIN")
    	.appendField(_("io.READAPIN"))
    	.setCheck("NUMBER");
    this.setInputsInline(true);
    this.setOutput(true,"NUMBER");  
    this.setPreviousStatement(false);
    this.setNextStatement(false);
    this.setTooltip('');
  },
  
  
  generateCode : function(generator) {
 	var pin = generator.valueToCode(this,"PIN");
  	// generator.checkValue(this,pin,0,Abbozza.board.digitalPins-1,"PIN_ERROR");
  	
   if (AbbozzaGenerator.ERROR) return null;

  	var code = "analogRead(" + pin + ")";
  	return code;
  }
}


/**
 * Write to an analog output (PWM)
 */
Abbozza.InOutAnalogWrite = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendValueInput("PIN")
        .appendField(__("io.SETAPIN",0))
        .setCheck("NUMBER");
    this.appendValueInput("VALUE")
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
 	var pin = generator.valueToCode(this,"PIN");
  	// generator.checkValue(this,pin,0,Abbozza.board.digitalPins-1,"PIN_ERROR");
  	
    if (AbbozzaGenerator.ERROR) return null;

  	var code = "analogWrite(" + pin + "," + value + ");";
  	return code;
  }
}


Blockly.Blocks['inout_pinmode'] = Abbozza.InOutPinmode;
Blockly.Blocks['inout_pin'] = Abbozza.InOutPin;
Blockly.Blocks['inout_level'] = Abbozza.InOutLevel;
Blockly.Blocks['inout_digital_read'] = Abbozza.InOutDigitalRead;
Blockly.Blocks['inout_digital_write'] = Abbozza.InOutDigitalWrite;
Blockly.Blocks['inout_analog_read'] = Abbozza.InOutAnalogRead;
Blockly.Blocks['inout_analog_write'] = Abbozza.InOutAnalogWrite;

/**
 * Start serial communication (unused)
 */
Abbozza.SerialBegin = {
    init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendDummyInput()
        .appendField(__("serial.BEGIN",0));
    this.setOutput(false);  
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('');
  },
  
  
  generateCode : function(generator) {
  	// var rate = generator.valueToCode(this,"RATE");
  	
    if (AbbozzaGenerator.ERROR) return null;

    AbbozzaGenerator.startMonitor = true;

  	var code = "Serial.begin(28800);";
  	return code;
  }
}

Blockly.Blocks['serial_begin'] = Abbozza.SerialBegin;

/**
 * Set the serial rate (unused)
 */

Abbozza.SerialBeginRate = {
    init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendDummyInput()
        .appendField(__("serial.BEGINRATE",0))
        .appendField(new Blockly.FieldDropdown([["300","300"], ["600","600"], ["1200","1200"], 
            ["2400","2400"], ["4800","4800"], ["9600","9600"], ["14400","14400"], ["19200","19200"], 
            ["28800","28800"], ["38400","38400"] , ["57600","57600"] ,["115200","115200"]]), "RATE")
        .appendField(__("serial.BEGINRATE",1));
    this.setOutput(false);  
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('');
  },
  
  
  generateCode : function(generator) {
    var rate = this.getFieldValue("RATE");
  

    if (AbbozzaGenerator.ERROR) return null;

    AbbozzaGenerator.startMonitor = true;
    
  	var code = "Serial.begin(" + rate + ");";
  	return code;
  }

}

Blockly.Blocks['serial_begin_rate'] = Abbozza.SerialBeginRate;

/**
 * Stops serial communiucation (unused)
 */
Abbozza.SerialEnd = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendDummyInput()
        .appendField(_("serial.END"));
    this.setOutput(false);  
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('');
  },
  
  
  generateCode : function(generator) {
  	var code = "Serial.end();";
  	return code;
  }
}

Blockly.Blocks['serial_end'] = Abbozza.SerialEnd;

/**
 * Write int to the serial port (unused)
 */
Abbozza.SerialWriteInt = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendValueInput("VALUE")
        .appendField(__("serial.WRITEINT",0))
        .setCheck("NUMBER");
    this.appendDummyInput()
        .appendField(__("serial.WRITEINT",1));
    this.setOutput(false);  
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('');
  },
  
  
  generateCode : function(generator) {
  	var value = generator.valueToCode(this,'VALUE');
        
  	
        if (AbbozzaGenerator.ERROR) return null;
        
        generator.serialRequired = true;

  	var code = "Serial.print(String(" + value + "));";
  	return code;
  }
}

Blockly.Blocks['serial_write_int'] = Abbozza.SerialWriteInt;

/**
 * Writes a byte to the serial port. (unused)
 */
Abbozza.SerialWriteByte = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendValueInput("VALUE")
        .appendField(__("serial.WRITEBYTE",0))
        .setCheck("NUMBER");
    this.appendDummyInput()
        .appendField(__("serial.WRITEBYTE",1));
    this.setOutput(false);  
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('');
  },
  
  
  generateCode : function(generator) {
  	var value = generator.valueToCode(this,'VALUE');
        
  	
        if (AbbozzaGenerator.ERROR) return null;

        generator.serialRequired = true;
  	var code = "Serial.println(String(" + value + "));";
  	return code;
  }
}

Blockly.Blocks['serial_write_byte'] = Abbozza.SerialWriteByte;

/**
 * Write a string to the serial port. (unused)
 */
Abbozza.SerialPrint = {
  init: function() {
     this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendValueInput("VALUE")
        .appendField(__("serial.PRINT",0))
        .setCheck("STRING");
    this.appendDummyInput()
        .appendField(__("serial.PRINT",1));
    this.setOutput(false);  
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('');
 },
  
  
  generateCode : function(generator) {
  	var value = generator.valueToCode(this,'VALUE');
  	// generator.checkValue(this,pin,0,Abbozza.board.digitalPins-1,"PIN_ERROR");
  	
    if (AbbozzaGenerator.ERROR) return null;

        generator.serialRequired = true;

  	var code = "Serial.println(" + value + ");";
  	return code;
  }
}

Blockly.Blocks['serial_print'] = Abbozza.SerialPrint;

/**
 * Writes a string with a newline to the serial port.
 */
Abbozza.SerialPrintLn = {
   init: function() {
     this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendValueInput("VALUE")
        .appendField(__("serial.PRINTLN",0))
        .setCheck("STRING");
    this.appendDummyInput()
        .appendField(__("serial.PRINTLN",1));
    this.setOutput(false);  
    this.setInputsInline(true);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('');
 },
  
  
  generateCode : function(generator) {
  	var value = generator.valueToCode(this,'VALUE');
  	// generator.checkValue(this,pin,0,Abbozza.board.digitalPins-1,"PIN_ERROR");
  	
    if (AbbozzaGenerator.ERROR) return null;

        generator.serialRequired = true;

  	var code = "Serial.println(" + value + ");";
  	return code;
  }
}

Blockly.Blocks['serial_println'] = Abbozza.SerialPrintLn;

/**
 * Writes the given values to the serial port. The Monitor is able to parse them.
 */
Abbozza.SerialTable = {
  init: function() {
    var thisblock = this;
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getColor("cat.INOUT"));
    this.appendDummyInput()
        .appendField("Sende Werte an Plotter");
    this.appendValueInput("CHANNEL0")
        .setCheck(["BOOLEAN","NUMBER"])
        .setAlign(Blockly.ALIGN_RIGHT)
        .appendField("Kanal 1")
        .appendField(new Blockly.FieldDropdown([["digital","0"],["0 .. 1023","1"],["0 .. 65535","2"]], 
            function(value) {
              switch (value) {
                case "0" :
                    thisblock.getInput("CHANNEL0").setCheck(["BOOLEAN","NUMBER"]);
                    break;
                case "1" :
                case "2" :
                    thisblock.getInput("CHANNEL0").setCheck(["NUMBER","BOOLEAN"]);
                }                
            }
        ),"CHANNEL0_TYPE");
    this.appendValueInput("CHANNEL1")
        .setCheck(["BOOLEAN","NUMBER"])
        .setAlign(Blockly.ALIGN_RIGHT)
        .appendField("Kanal 2")
        .appendField(new Blockly.FieldDropdown([["digital","0"],["0 .. 1023","1"],["0 .. 65535","2"]], 
            function(value) {
              switch (value) {
                case "0" :
                    thisblock.getInput("CHANNEL1").setCheck(["BOOLEAN","NUMBER"]);
                    break;
                case "1" :
                case "2" :
                    thisblock.getInput("CHANNEL1").setCheck(["NUMBER","BOOLEAN"]);
                }                
            }
        ),"CHANNEL1_TYPE");
    this.appendValueInput("CHANNEL2")
        .setCheck(["BOOLEAN","NUMBER"])
        .setAlign(Blockly.ALIGN_RIGHT)
        .appendField("Kanal 3")
        .appendField(new Blockly.FieldDropdown([["digital","0"],["0 .. 1023","1"],["0 .. 65535","2"]], 
            function(value) {
              switch (value) {
                case "0" :
                    thisblock.getInput("CHANNEL2").setCheck(["BOOLEAN","NUMBER"]);
                    break;
                case "1" :
                case "2" :
                    thisblock.getInput("CHANNEL2").setCheck(["NUMBER","BOOLEAN"]);
                }                
            }
        ),"CHANNEL2_TYPE");
    this.appendValueInput("CHANNEL3")
        .setCheck(["BOOLEAN","NUMBER"])
        .setAlign(Blockly.ALIGN_RIGHT)
        .appendField("Kanal 4")
        .appendField(new Blockly.FieldDropdown([["digital","0"],["0 .. 1023","1"],["0 .. 65535","2"]], 
            function(value) {
              switch (value) {
                case "0" :
                    thisblock.getInput("CHANNEL3").setCheck(["BOOLEAN","NUMBER"]);
                    break;
                case "1" :
                case "2" :
                    thisblock.getInput("CHANNEL3").setCheck(["NUMBER","BOOLEAN"]);
                }                
            }
        ),"CHANNEL3_TYPE");
    this.appendValueInput("CHANNEL4")
        .setCheck(["BOOLEAN","NUMBER"])
        .setAlign(Blockly.ALIGN_RIGHT)
        .appendField("Kanal 5")
        .appendField(new Blockly.FieldDropdown([["digital","0"],["0 .. 1023","1"],["0 .. 65535","2"]], 
            function(value) {
              switch (value) {
                case "0" :
                    thisblock.getInput("CHANNEL4").setCheck(["BOOLEAN","NUMBER"]);
                    break;
                case "1" :
                case "2" :
                    thisblock.getInput("CHANNEL4").setCheck(["NUMBER","BOOLEAN"]);
                }                
            }
        ),"CHANNEL4_TYPE");
    this.setOutput(false);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setTooltip('');
 },
  
  generateCode : function(generator) {
        // generator.addLibrary("#include <abbozza.h>");
        
        var pin0 = generator.valueToCodeUnchecked(this,"CHANNEL0",-1);
        var pin1 = generator.valueToCodeUnchecked(this,"CHANNEL1",-1);
        var pin2 = generator.valueToCodeUnchecked(this,"CHANNEL2",-1);
        var pin3 = generator.valueToCodeUnchecked(this,"CHANNEL3",-1);
        var pin4 = generator.valueToCodeUnchecked(this,"CHANNEL4",-1);
        
        var type0 = generator.fieldToCode(this,"CHANNEL0_TYPE");
        var type1 = generator.fieldToCode(this,"CHANNEL1_TYPE");
        var type2 = generator.fieldToCode(this,"CHANNEL2_TYPE");
        var type3 = generator.fieldToCode(this,"CHANNEL3_TYPE");
        var type4 = generator.fieldToCode(this,"CHANNEL4_TYPE");

        var type = "" + type0 + type1 + type2 + type3 + type4;
        
        if (AbbozzaGenerator.ERROR) return null;

        generator.serialRequired = true;

  	// var code = "__measure("+pin0+","+pin1+","+pin2+","+pin3+","+pin4+");";
        var code = 'Serial.println("[[table ' + type + '," + String(millis()) + "," + String(' + pin0 + ') + "," + String(' + pin1 + ') + "," ' 
          + ' + String(' + pin2 + ') + "," + String(' + pin3 + ') + "," + String(' + pin4 + ') + "]]");';

  	return code;
  }
}

Blockly.Blocks['serial_table'] = Abbozza.SerialTable;
