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
 * @fileoverview Blocks for the handling of servo motors
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */

Abbozza.DeviceSerial = {
    devtype: DEV_TYPE_SERIAL,
    init: function () {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getColor("color.DEVICE"));
        // this.appendValueInput("PIN")
        this.appendDummyInput()
                .appendField(_("dev.DEVICE") + ": " + _("dev.SERIAL"))
                .appendField(new FieldDevNameInput("<default>", Abbozza.blockDevices, this), "NAME")
                .appendField("rx : ")
                .appendField(new PinDropdown(PinDropdown.DIGITAL), "RX")
                .appendField("tx : ")
                .appendField(new PinDropdown(PinDropdown.DIGITAL), "TX")
        // .appendField(new Blockly.FieldDropdown( function() { return Abbozza.board.getPWMPinMenu(); }  ), "PIN");
        // .setCheck("NUMBER");
        this.setInputsInline(true);
        this.setOutput(false);
        this.setPreviousStatement(true, "DEVICE");
        this.setNextStatement(true, "DEVICE");
        this.setTooltip('');
        Abbozza.addDisposeHandler(this);
    },
    
    getName: function () {
        return this.getFieldValue("NAME");
    },
    
    generateCode: function (generator) {
        generator.addLibrary("#include <SoftwareSerial.h>");
        var rx = generator.fieldToCode(this, "RX");
        var tx = generator.fieldToCode(this, "TX");
        generator.addPreSetup("SoftwareSerial _dev_" + this.getName() + "(" + rx + "," + tx + ");");
        generator.addSetupCode("_dev_"+this.getName()+".begin(9600);");
    },
    onDispose: function () {
        Abbozza.devices.delDevice(this.getName());
    }


};

Blockly.Blocks['dev_serial'] = Abbozza.DeviceSerial;


Abbozza.DeviceSerialWrite = {
    init: function () {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getColor("color.NUMBER"));
                
        this.appendValueInput("VALUE")
                .appendField(_("dev.WRITEBYTE"))        
                .setCheck("NUMBER");
        this.appendDummyInput("VALUE")
                .appendField(_("dev.TOLINE"))
                .appendField(new DeviceDropdown(this, DEV_TYPE_SERIAL), "NAME");
        this.setInputsInline(true);
        this.setOutput(false);
        this.setPreviousStatement(true, "STATEMENT");
        this.setNextStatement(true, "STATEMENT");
        this.setTooltip('');
    },
    /*  setName : function(name) {
     this.name = name;
     },*/


    generateCode: function (generator) {
        var device = Abbozza.blockDevices.getDevice(generator.fieldToCode(this, "NAME"));
        var value = generator.valueToCode(this, "VALUE");

        if (device == null) {
            ErrorMgr.addError(this, "UNKNOWN_DEVICE");
            return;
        }

        var name = "_dev_" + device.getName();
        // generator.valueToCode(device,"PIN");
        return name + ".write(byte(" + value + "));";
    }

};

Blockly.Blocks['dev_serial_write'] = Abbozza.DeviceSerialWrite;


//Abbozza.DeviceServoRead = {
//    init: function () {
//        this.setHelpUrl(Abbozza.HELP_URL);
//        this.setColour(ColorMgr.getColor("color.NUMBER"));
//        this.appendDummyInput("PIN")
//                .appendField(_("dev.VALUEOF"))
//                .appendField(_("dev.SERVO"))
//                .appendField(new DeviceDropdown(this, DEV_TYPE_SERVO), "NAME");
//        this.setInputsInline(false);
//        this.setOutput(true, "NUMBER");
//        this.setPreviousStatement(false);
//        this.setNextStatement(false);
//        this.setTooltip('');
//    },
//    /*  setName : function(name) {
//     this.name = name;
//     },*/
//
//
//    generateCode: function (generator) {
//        var device = Abbozza.blockDevices.getDevice(generator.fieldToCode(this, "NAME"));
//
//        if (device == null) {
//            ErrorMgr.addError(this, "UNKNOWN_DEVICE");
//        }
//
//        var name = "_dev_" + device.getName();
//        var pin = generator.fieldToCode(device,"PIN");
//        return name + ".read()";
//    }
//
//}
//Blockly.Blocks['dev_servo_read'] = Abbozza.DeviceServoRead;
