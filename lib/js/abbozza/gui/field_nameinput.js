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
 * @fileoverview An input Field for the symbol names.
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */

'use strict';

goog.provide('FieldNameInput');

goog.require('Blockly.Field');
goog.require('Blockly.FieldTextInput');
goog.require('Blockly.Msg');
goog.require('goog.asserts');
goog.require('goog.dom');
goog.require('goog.userAgent');


FieldNameInput = function(text, symbols, opt_symbolType) {
	FieldNameInput.superClass_.constructor.call(this, text);
	this.editing = false;
	this.symbols = symbols;
	this.oldText = text;
	if ( opt_symbolType )
		this.symbolType = opt_symbolType;
	else
		this.symbolType = Abbozza.VAR_SYMBOL;
};
goog.inherits(FieldNameInput, Blockly.FieldTextInput);


FieldNameInput.prototype.showEditor_ = function(opt_quietInput) {
	this.oldText = this.getText();
	this.editing = true;
	Blockly.FieldTextInput.prototype.showEditor_.call(this,opt_quietInput);
};


FieldNameInput.prototype.widgetDispose_ = function() {
 var thisField = this;
 return function() {
 	// mark editing as stopped
    thisField.editing = false;
    // fetch old and new text
    var htmlInput = Blockly.FieldTextInput.htmlInput_;
    var text = htmlInput.value;
    var oldText = thisField.oldText;
    var block = thisField.sourceBlock_;
    
    if ( oldText != text ) {
	    // Abbozza.log("NameField changed from " + oldText + " to " + text);
    	// Abbozza.log("SymbolDB : ");
    	// Abbozza.log(thisField.symbols);
    	if ( thisField.symbols ) {
	    	// Abbozza.log("Removing " + oldText + " from symbols");
	    	thisField.symbols.delete(oldText);
	    	// Abbozza.log("Checking name: ");
	    	// Abbozza.log("\t before : " + text);
   		    text = thisField.checkValue(text);
	    	// Abbozza.log("\t after : " + text);
	    	thisField.setText(text);
	    	
	    	switch ( thisField.symbolType ) {
	    		case Abbozza.VAR_SYMBOL:
	    			// do nothing
	    			break;
	    		case Abbozza.PAR_SYMBOL:
	    			// do nothing
	    			break;
	    		case Abbozza.FUN_SYMBOL:
	    			// add the 
	    			var type = block.getFieldValue("TYPE");
	    			// Abbozza.log("Adding function " + type + " " + text + "()");
	    			thisField.symbols.addFunction(text,type);
	    			break;
	    	}
	    }
    } else {
    	// Abbozza.log("NameField content didn't change: " + text);
    }	    
    // console.log("after check " + text);
    /*
    		if (thisField.sourceBlock_ && thisField.changeHandler_) {
     		text = thisField.changeHandler_(text);
     		if (text === null) {
	       	// Invalid edit.
       		text = htmlInput.defaultValue;
    	}
    */
    // if ( thisField.editEndHandler) thisField.editEndHandler(text, thisField.oldText);
    thisField.sourceBlock_.rendered && thisField.sourceBlock_.render();
    Blockly.unbindEvent_(htmlInput.onKeyDownWrapper_);
    Blockly.unbindEvent_(htmlInput.onKeyUpWrapper_);
    Blockly.unbindEvent_(htmlInput.onKeyPressWrapper_);
	   Blockly.unbindEvent_(htmlInput.onWorkspaceChangeWrapper_);
    Blockly.FieldTextInput.htmlInput_ = null;
    // Delete the width property.
    Blockly.WidgetDiv.DIV.style.width = 'auto';
  }
};


FieldNameInput.prototype.checkValue = function(text) {
        text = Validator.nameValidator(text);
	if ( this.symbols ) {
            text = this.symbols.getLegalName(text);
        } else {
            text = Abbozza.globalSymbols.getLegalName(text);
        }
	return text;
};
