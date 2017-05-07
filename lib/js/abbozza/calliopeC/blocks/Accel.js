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


Abbozza.AccelGet = {
    init : function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DEVIN"));
        this.setPreviousStatement(false);
        this.setNextStatement(false);            
        this.setOutput(true,"NUMBER");
        this.appendDummyInput()
            .appendField(_("dev.ACCEL_GET"))
            .appendField(new Blockly.FieldDropdown([[_("dev.ACCEL_X"),"getX"],[_("dev.ACCEL_Y"),"getY"],[_("dev.ACCEL_Z"),"getZ"],[_("dev.ACCEL_PITCH"),"getPitch"],[_("dev.ACCEL_ROLL"),"getRoll"]]),"DIR");
        this.setTooltip('');
    },
    
    generateCode : function(generator) {
        var code="";
        var dir = this.getFieldValue("DIR");
        code = "calliope.accelerometer." + dir + "()";
        return code;
    }
}

Blockly.Blocks['dev_accel_get'] = Abbozza.AccelGet;



Abbozza.GestureMenu = [
    [_("dev.UP"),"up"],
    [_("dev.DOWN"),"down"],
    [_("dev.LEFT"),"left"],
    [_("dev.RIGHT"),"right"],
    [_("dev.FACE_UP"),"face up"],
    [_("dev.FACE_DOWN"),"face down"],
    [_("dev.FREEFALL"),"freefall"],
    [_("dev.3G"),"3g"],
    [_("dev.6G"),"6g"],
    [_("dev.8G"),"8g"],
    [_("dev.SHAKE"),"shake"]
]

Abbozza.AccelGesture = {
    init : function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DEVIN"));
        this.setPreviousStatement(false);
        this.setNextStatement(false);            
        this.setOutput(true,"STRING");
        this.appendDummyInput()
            .appendField(_("dev.ACCEL_GESTURE"));
        this.setTooltip('');
    },
    
    generateCode : function(generator) {
        var code="";
        code = "calliope.accelerometer.getGesture()";
        return code;
    }
}

Blockly.Blocks['dev_accel_gesture'] = Abbozza.AccelGesture;


/*
Abbozza.AccelIsGesture = {
    init : function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DEVIN"));
        this.setPreviousStatement(false);
        this.setNextStatement(false);            
        this.setOutput(true,"BOOLEAN");
        this.appendDummyInput()
            .appendField(_("dev.ACCEL_IS_GESTURE"))
            .appendField(new Blockly.FieldDropdown([
    [_("dev.UP"),"up"],
    [_("dev.DOWN"),"down"],
    [_("dev.LEFT"),"left"],
    [_("dev.RIGHT"),"right"],
    [_("dev.FACE_UP"),"face up"],
    [_("dev.FACE_DOWN"),"face down"],
    [_("dev.FREEFALL"),"freefall"],
    [_("dev.3G"),"3g"],
    [_("dev.6G"),"6g"],
    [_("dev.8G"),"8g"],
    [_("dev.SHAKE"),"shake"]]),"GESTURE");
        this.setTooltip('');
    },
    
    generateCode : function(generator) {
        var code="";
        var gesture = this.getFieldValue("GESTURE");
        code = "accelerometer.is_gesture(\"" + gesture + "\")";
        return code;
    }
}

Blockly.Blocks['dev_accel_is_gesture'] = Abbozza.AccelIsGesture;
*/


Abbozza.AccelWasGesture = {
    init : function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DEVIN"));
        this.setPreviousStatement(false);
        this.setNextStatement(false);            
        this.setOutput(true,"BOOLEAN");
        this.appendDummyInput()
            .appendField(_("dev.ACCEL_WAS_GESTURE"))
            .appendField(new Blockly.FieldDropdown([
    [_("dev.UP"),"MICROBIT_ACCELEROMETER_EVT_TILT_UP"],
    [_("dev.DOWN"),"MICROBIT_ACCELEROMETER_EVT_TILT_DOWN"],
    [_("dev.LEFT"),"MICROBIT_ACCELEROMETER_EVT_TILT_LEFT"],
    [_("dev.RIGHT"),"MICROBIT_ACCELEROMETER_EVT_TILT_RIGHT"],
    [_("dev.FACE_UP"),"MICROBIT_ACCELEROMETER_EVT_FACE_UP"],
    [_("dev.FACE_DOWN"),"MICROBIT_ACCELEROMETER_EVT_FACE_DOWN"],
    [_("dev.FREEFALL"),"MICROBIT_ACCELEROMETER_EVT_FREEFALL"],
    [_("dev.3G"),"MICROBIT_ACCELEROMETER_EVT_3G"],
    [_("dev.6G"),"MICROBIT_ACCELEROMETER_EVT_6G"],
    [_("dev.8G"),"MICROBIT_ACCELEROMETER_EVT_8G"],
    [_("dev.SHAKE"),"MICROBIT_ACCELEROMETER_EVT_SHAKE"]]),"GESTURE");
        this.setTooltip('');
    },
    
    generateCode : function(generator) {
        var code="";
        var gesture = this.getFieldValue("GESTURE");
        code = "(calliope.accelerometer.getGesture() == " + gesture + ")";
        return code;
    }
}

Blockly.Blocks['dev_accel_was_gesture'] = Abbozza.AccelWasGesture;


Abbozza.Gesture = {
    init : function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DEVIN"));
        this.setPreviousStatement(false);
        this.setNextStatement(false);            
        this.setOutput(true,"STRING");
        this.appendDummyInput()
            .appendField(_("dev.GESTURE"))
            .appendField(new Blockly.FieldDropdown([
                [_("dev.UP"),"MICROBIT_ACCELEROMETER_EVT_TILT_UP"],
                [_("dev.DOWN"),"MICROBIT_ACCELEROMETER_EVT_TILT_DOWN"],
                [_("dev.LEFT"),"MICROBIT_ACCELEROMETER_EVT_TILT_LEFT"],
                [_("dev.RIGHT"),"MICROBIT_ACCELEROMETER_EVT_TILT_RIGHR"],
                [_("dev.FACE_UP"),"MICROBIT_ACCELEROMETER_EVT_FACE_UP"],
                [_("dev.FACE_DOWN"),"MICROBIT_ACCELEROMETER_EVT_FACE_DOWN"],
                [_("dev.FREEFALL"),"MICROBIT_ACCELEROMETER_EVT_FREEFALL"],
                [_("dev.3G"),"MICROBIT_ACCELEROMETER_EVT_3G"],
                [_("dev.6G"),"MICROBIT_ACCELEROMETER_EVT_6G"],
                [_("dev.8G"),"MICROBIT_ACCELEROMETER_EVT_8G"],
                [_("dev.SHAKE"),"MICROBIT_ACCELEROMETER_EVT_SHAKE"]]),"GESTURE");
        this.setTooltip('');
    },
    
    generateCode : function(generator) {
        var code="";
        var gesture = this.getFieldValue("GESTURE");
        code = gesture;
        return code;
    }
}

Blockly.Blocks['dev_gesture'] = Abbozza.Gesture;


Abbozza.CompassGet = {
    init : function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DEVIN"));
        this.setPreviousStatement(false);
        this.setNextStatement(false);            
        this.setOutput(true,"NUMBER");
        this.appendDummyInput()
            .appendField(_("dev.COMPASS_GET"))
            .appendField(new Blockly.FieldDropdown([[_("dev.COMPASSL_X"),"getX"],[_("dev.COMPASS_Y"),"getY"],[_("dev.COMPASS_Z"),"getZ"],[_("dev.COMPASS_FIELD"),"getFieldStrength"]]),"DIR");
        this.setTooltip('');
    },
    
    generateCode : function(generator) {
        var code="";
        var dir = this.getFieldValue("DIR");
        code = "calliope.compass." + dir + "()";
        return code;
    }
}

Blockly.Blocks['dev_compass_get'] = Abbozza.CompassGet;



Abbozza.ThermometerGet = {
    init : function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DEVIN"));
        this.setPreviousStatement(false);
        this.setNextStatement(false);            
        this.setOutput(true,"NUMBER");
        this.appendDummyInput()
            .appendField(_("dev.THERMOMETER_GET"))
        this.setTooltip('');
    },
    
    generateCode : function(generator) {
        var code="";
        code = "calliope.thermometer.getTemperature()";
        return code;
    }
}

Blockly.Blocks['dev_thermometer_get'] = Abbozza.ThermometerGet;
