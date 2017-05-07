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


Abbozza.ButtonPressed = {
    init : function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DEVIN"));
        this.setPreviousStatement(false);
        this.setNextStatement(false);            
        this.setOutput(true,"BOOLEAN");
        this.appendDummyInput()
            .appendField(new Blockly.FieldDropdown([[_("dev.BUTTON_A"),"calliope.buttonA"],[_("dev.BUTTON_B"),"calliope.buttonB"]]),"BUTTON")
            .appendField(_("dev.PRESSED"));
        this.setTooltip('');
    },
    
    generateCode : function(generator) {
        var code="";
        var button = this.getFieldValue("BUTTON");
        code = button+".isPressed()";
        return code;
    }
}

Blockly.Blocks['dev_button_pressed'] = Abbozza.ButtonPressed;

