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
 * @fileoverview A dropdownmenu field fpr the choice of a variable name.
 * 
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */

'use strict';

goog.provide('VariableDropdown');
goog.provide('VariableTypedDropdown');

goog.require('Blockly.Field');
goog.require('Blockly.FieldDropdown');
goog.require('Blockly.Msg');
goog.require('goog.asserts');
goog.require('goog.dom');
goog.require('goog.userAgent');


VariableDropdown = function(block, changeHandler) {
	VariableDropdown.superClass_.constructor.call(this, this.getMenu , changeHandler);
	this.EDITABLE = true;
	// console.log(block);
	this.block = block;
};
goog.inherits(VariableDropdown, Blockly.FieldDropdown);

VariableDropdown.prototype.getMenu = function() {
	if (this.block != null) {
		// console.log("block");
		// console.log(this.block);
		var rootBlock = this.block.getRootBlock();
		if ( rootBlock.symbols ) {
			return rootBlock.symbols.getVarMenu("");
		} else {
			return rootBlock.workspace.symbols.getVarMenu("");
		}
	} return [["<name>","<name>"]];
}

VariableDropdown.prototype.setValue = function(newValue) {
    VariableDropdown.superClass_.setValue.call(this,newValue);
    this.block.getSymbol();
}

VariableTypedDropdown = function(block, type, changeHandler) {
	VariableTypedDropdown.superClass_.constructor.call(this, this.getMenu , changeHandler);
	this.EDITABLE = true;
	// console.log(block);
	this.block = block;
	this.type = type;
};
goog.inherits(VariableTypedDropdown, Blockly.FieldDropdown);

VariableTypedDropdown.prototype.getMenu = function() {
	if (this.block != null) {
		// console.log("block");
		// console.log(this.block);
		var rootBlock = this.block.getRootBlock();
		if ( rootBlock.symbols ) {
			return rootBlock.symbols.getVarTypedMenu(this.type,"");
		} else {
			return rootBlock.workspace.symbols.getVarTypedMenu(this.type,"");
		}
	} return [["<name>","<name>"]];
}