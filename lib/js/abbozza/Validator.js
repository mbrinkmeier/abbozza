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
 * @fileoverview Some validator functions for names, array dimensions etc
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */


Validator = function() {}
	
	
/**
 * Number Validator
 */
Validator.lengthValidator = function(text,max) {
	if (text === null) {
	   return null;
	}
	text = text.replace(/O/ig, '0');
	// Strip out thousands separators.
	text = text.replace(/,/g, '');
	var n = parseInt(text || 0);
	if ( isNaN(n) ) return null;
	if ( n < 1 ) return "1";
	if ( (max != -1) && (n>max) ) return String(max);
	return String(n);
}


Validator.numberValidator = function(text) {
	if (text === null) {
	   return null;
	}
	text = text.replace(/O/ig, '0');
	// Strip out thousands separators.
	text = text.replace(/,/g, '');
	var n = parseInt(text || 0);
	if ( isNaN(n) ) return null;
	if ( n < 1 ) return "1";
	return String(n);
}

/**
 * Validates the given name and changes it if neccessary.
 * The name has to bee of the form: [a-zA-Z_][a-zA-Z0-9_]*
 */
Validator.nameValidator = function(name) {
	if (name == null) return null;
	var result = name.replace(/[\W]/g,"_");
	result = result.replace(/^[0-9]*/g,"");
	return result;
}


Validator.numericalValidator = function(text) {
	if ( text == null) return null;
	var f = parseFloat(text || 0.0);
	if ( isNaN(f) ) return null;
	var frac = f - Math.floor(f);
	if ( frac == 0.0 ) {
		f = parseInt(text || 0);
	}
	return String(f);
}