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
'use strict';

goog.provide('AbbozzaBubble');

goog.require('Blockly.Bubble');
goog.require('Blockly.Workspace');
goog.require('goog.dom');
goog.require('goog.math');
goog.require('goog.userAgent');


AbbozzaBubble = function(workspace, content, shape, classname,
                          anchorX, anchorY,
                          bubbleWidth, bubbleHeight, shiftX, shiftY) {
  this.shiftX_ = shiftX;
  this.shiftY_ = shiftY;
  this.classname_ = classname;
  if (content == null) {
      this.svgGroup_ = Blockly.createSvgElement('g',{},null);      
      this.svgPath_ = Blockly.createSvgElement('path', { 'class':'blocklypath'}, this.svgGroup_);
      AbbozzaBubble.superClass_.constructor.call(this, workspace, this.createSvg_(), this.svgPath_, anchorX, anchorY, bubbleWidth, bubbleHeight);
      this.registerResizeEvent(this, this.resize_);  
  } else {
      AbbozzaBubble.superClass_.constructor.call(this, workspace, content, shape, anchorX, anchorY, bubbleWidth, bubbleHeight);
  }
};
goog.inherits(AbbozzaBubble, Blockly.Bubble);

AbbozzaBubble.prototype.setClassname = function(classname) {
    this.classname_ = classname;
};

AbbozzaBubble.prototype.setShift = function ( shiftX, shiftY ) {
    this.shiftX_ = shiftX;
    this.shiftY_ = shiftY;
}

AbbozzaBubble.prototype.layoutBubble_ = function() {
    AbbozzaBubble.superClass_.layoutBubble_.call(this);
    this.relativeLeft_ = this.relativeLeft_ + this.shiftX_;
    this.relativeTop_ = this.relativeTop_ + this.shiftY_;
}

AbbozzaBubble.prototype.positionBubble_ = function() {
  var left;
  if (this.workspace_.RTL) {
    left = this.anchorX_ - this.relativeLeft_ - this.width_;
  } else {
    left = this.anchorX_ + this.relativeLeft_;
  }
  var top = this.relativeTop_ + this.anchorY_;
  this.bubbleGroup_.setAttribute('transform',
      'translate(' + left + ',' + top + ')');
};



AbbozzaBubble.prototype.createSvg_ = function() {
  this.foreignObject_ = Blockly.createSvgElement('foreignObject',
      {'x': Blockly.Bubble.BORDER_WIDTH, 'y': Blockly.Bubble.BORDER_WIDTH},
      null);
  var body = document.createElementNS(Blockly.HTML_NS, 'BODY');
  body.setAttribute('xmlns', Blockly.HTML_NS);
  body.className = 'blocklyMinimalBody';
  // body.className = this.classname_;
  
  /*
  this.head_ = document.createElementNS(Blockly.HTML_NS, 'div');
  this.head_.className = this.classname + "Head";
  this.head_.appendChild(document.createTextNode(this.wintitle));
  body.appendChild(this.head_);
   
  this.closer_ = document.createElementNS(Blockly.HTML_NS,"img");
  this.closer_.className = this.classname +  "Button";
  this.closer_.src = "../img/iwin_close.png";
  this.closer_.setAttribute("title",_("gui.save"));
  this.head_.appendChild(this.closer_);

  if (this.editable) {
     this.editor_  = document.createElementNS(Blockly.HTML_NS,"img");
     this.editor_.className = this.classname +  "Button";
     this.editor_.src = "../img/iwin_edit.png";
     this.editor_.setAttribute("title",_("gui.edit"));
     this.head_.appendChild(this.editor_);
  } else {
     this.editor_ = null;
  }
  */
 
  this.textarea_ = document.createElementNS(Blockly.HTML_NS,'div');
  this.textarea_.className = this.classname_ + "Content";
  // this.textarea_.setAttribute('dir', Blockly.RTL ? 'RTL' : 'LTR');
  this.textarea_.setAttribute('readonly', 'true');
  // this.inner_ = this.textarea_;
  // this.inner_ = document.createElementNS(Blockly.HTML_NS,'div');
  // this.inner_.className = "iwinInner";
  // this.textarea_.appendChild(this.inner_);
  body.appendChild(this.textarea_);  
  
  this.foreignObject_.appendChild(body);
  return this.foreignObject_;
}
  
AbbozzaBubble.prototype.setText = function(text) {
    this.textarea_.innerHTML = text;
}

AbbozzaBubble.prototype.resize_ = function() {
  var size = this.getBubbleSize();
  var doubleBorderWidth = 2 * Blockly.Bubble.BORDER_WIDTH;
  this.foreignObject_.setAttribute('width', size.width - doubleBorderWidth);
  this.foreignObject_.setAttribute('height', size.height - doubleBorderWidth);
  this.textarea_.style.width = (size.width - doubleBorderWidth - 10) + 'px';
  this.textarea_.style.height = (size.height - doubleBorderWidth - 10) + 'px';
};

