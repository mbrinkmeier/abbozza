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
 * @fileoverview This class provides a simple window for the abbozza! workspace!
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */

'use strict';

goog.provide('InternalWindow');

goog.require('Blockly.Bubble');
goog.require('Blockly.Icon');
goog.require('goog.userAgent');


/**
 * Class for a comment.
 * @param {!Blockly.Block} block The block associated with this comment.
 * @extends {Blockly.Icon}
 * @constructor
 */
InternalWindow = function(workspace,block,title,classname,x,y,width,height, opt_closeHandler) {
  this.wintitle = title;
  this.classname = classname;
  this.svgGroup_ = Blockly.createSvgElement('g',{},null);
  this.svgPath_ = Blockly.createSvgElement('path', { 'class':'blocklypath'}, this.svgGroup_);
  
  InternalWindow.superClass_.constructor.call(this, 
  		workspace,
  		this.createSvg_(), 
  		// block.svgPath_,
  		this.svgPath_,
        x,y,width,height);
  var size = this.getBubbleSize();
  // this.setVisible(true);
  this.registerResizeEvent(this, this.resize_);
  this.setBubbleSize(width, height);
  Blockly.bindEvent_(this.textarea_, 'mouseup', this, this.textareaFocus_);
  Blockly.bindEvent_(this.head_,'mousedown', this, this.bubbleMouseDown_);
  if ( opt_closeHandler ) {
	  Blockly.bindEvent_(this.closer_,'mousedown', this, opt_closeHandler);
  }
};
goog.inherits(InternalWindow, Blockly.Bubble);


InternalWindow.prototype.textareaFocus_ = function(e) {
  // Ideally this would be hooked to the focus event for the comment.
  // However doing so in Firefox swallows the cursor for unknown reasons.
  // So this is hooked to mouseup instead.  No big deal.
  this.promote_();
  // Since the act of moving this node within the DOM causes a loss of focus,
  // we need to reapply the focus.
  this.textarea_.focus();
};

InternalWindow.prototype.renderArrow_ = function() {};


InternalWindow.prototype.createSvg_ = function() {
  this.foreignObject_ = Blockly.createSvgElement('foreignObject',
      {'x': Blockly.Bubble.BORDER_WIDTH, 'y': Blockly.Bubble.BORDER_WIDTH},
      null);
  var body = document.createElementNS(Blockly.HTML_NS, 'body');
  body.setAttribute('xmlns', Blockly.HTML_NS);
  body.className = 'blocklyMinimalBody';
  
  this.head_ = document.createElementNS(Blockly.HTML_NS, 'div');
  this.head_.id = this.classname + 'Head';
  this.head_.className = "iwinHead";
  this.head_.appendChild(document.createTextNode(this.wintitle));
  body.appendChild(this.head_);
  
  this.closer_ = document.createElementNS(Blockly.HTML_NS,"div");
  this.closer_.id = this.classname + "Closer";
  this.closer_.className = "iwinCloser";
  // this.closer_.innerHTML ='<img src="img/close.png" width="20px">';
  this.head_.appendChild(this.closer_);

  this.textarea_ = document.createElementNS(Blockly.HTML_NS,'textarea');
  this.textarea_.id = this.classname;
  this.textarea_.className = "iwin";
  this.textarea_.setAttribute('dir', Blockly.RTL ? 'RTL' : 'LTR');
  this.textarea_.setAttribute('readonly', 'false');
  this.text_ = document.createTextNode("");
  this.textarea_.appendChild(this.text_);
  body.appendChild(this.textarea_);  
  
  this.foreignObject_.appendChild(body);
  Blockly.bindEvent_(this.textarea_, 'mouseup', this, this.textareaFocus_);
  return this.foreignObject_;
};


InternalWindow.prototype.setLocation = function(x,y) {
	this.relativeLeft_ = x;
	this.relativeTop_ = y;
};


InternalWindow.prototype.resize_ = function() {
  var size = this.getBubbleSize();
  var doubleBorderWidth = 2 * Blockly.Bubble.BORDER_WIDTH;
  this.foreignObject_.setAttribute('width', size.width - doubleBorderWidth);
  this.foreignObject_.setAttribute('height', size.height - doubleBorderWidth);
  this.textarea_.style.width = (size.width - doubleBorderWidth - 2) + 'px';
  this.textarea_.style.height = (size.height - doubleBorderWidth - 22) + 'px';
};

InternalWindow.prototype.setText = function(text) {
  this.text_.textContent  = text;
};

InternalWindow.prototype.appendText = function(text) {
	this.text_.textContent = this.text_.textContent + text;
        this.textarea_.scrollTop = this.textarea_.scrollHeight;
}

InternalWindow.prototype.close = function() {
	this.dispose();
}