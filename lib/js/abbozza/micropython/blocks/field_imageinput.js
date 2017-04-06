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

goog.provide('FieldImageInput');

goog.require('Blockly.Field');
goog.require('Blockly.FieldTextInput');
goog.require('Blockly.Msg');
goog.require('goog.asserts');
goog.require('goog.dom');
goog.require('goog.userAgent');

var ImageDB = {
    _images : []
}

ImageDB.rebuild = function() {
}

ImageDB.getImages = function() {
}

ImageDB.contains = function(name) {
    for (var idx= 0; this._images.length; idx++) {
        if ( this._images[idx] == name ) return true;
    }
    return false;
}

ImageDB.addImage = function(name) {
    console.log("hier");
    this._images.push(name);
}

ImageDB.validateName = function(newName) {
}

ImageDB.changeListener = function() {
    console.log("hier");
}

FieldImageInput = function(text) {
	FieldImageInput.superClass_.constructor.call(this, text);
	this.editing = false;
	this.oldText = text;
};
goog.inherits(FieldImageInput, Blockly.FieldTextInput);


FieldImageInput.prototype.showEditor_ = function(opt_quietInput) {
	this.oldText = this.getText();
	this.editing = true;
	Blockly.FieldTextInput.prototype.showEditor_.call(this,opt_quietInput);
};


FieldImageInput.prototype.widgetDispose_ = function() {
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
    	if ( ImageDB ) {
	    ImageDB.delete(oldText);
            text = thisField.checkValue(text);
            thisField.setText(text);	    	
	}
    } else {
    }	    
    thisField.sourceBlock_.rendered && thisField.sourceBlock_.render();
    Blockly.unbindEvent_(htmlInput.onKeyDownWrapper_);
    Blockly.unbindEvent_(htmlInput.onKeyUpWrapper_);
    Blockly.unbindEvent_(htmlInput.onKeyPressWrapper_);
    Blockly.unbindEvent_(htmlInput.onWorkspaceChangeWrapper_);
    Blockly.FieldTextInput.htmlInput_ = null;
    Blockly.WidgetDiv.DIV.style.width = 'auto';
  }
};


FieldImageInput.prototype.checkValue = function(text) {
        text = Validator.nameValidator(text);
	if ( ImageDB ) {
            text = ImageDB.getLegalName(text);
        }
	return text;
};
