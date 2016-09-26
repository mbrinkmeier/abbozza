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

var abbozzaHintProto = Object.create(HTMLElement.prototype);

abbozzaHintProto.attachedCallback = function() {
    this.style.display="none";
    this.block_ = null;
    this.icon_ = null;
    this.element_ = null;
    this.bubble_ = null;
    
    if (this.hasAttribute("block")) {
        // If a block id is given
        var block = [];
        var blockid = this.hasAttribute("block") ? this.getAttribute("block") : null;
        if (blockid != null) {
            block = Abbozza.getBlocksById(blockid);
        } else {
            block.push(null);
        }
        this.block_ = block[0];
        if (this.block_ != null) {
            this.icon_ = new TaskHintIcon(this.block_,this.innerHTML);
            var dx = this.hasAttribute("dx") ? Number(this.getAttribute("dx")) : 0;
            var dy = this.hasAttribute("dy") ? Number(this.getAttribute("dy")) : 0;
            var width = this.hasAttribute("width") ? Number(this.getAttribute("width")) : 100;
            var height = this.hasAttribute("height") ? Number(this.getAttribute("height")) : 50;
            this.icon_.setBubbleShift(dx,dy);
            this.icon_.setBubbleSize(width,height);
        }
    } else if (this.hasAttribute("anchor")) {
        var id = this.getAttribute("anchor");
        this.element_ = document.getElementById(id);
        if ( this.element_ != null ) {
            var metrics = Blockly.mainWorkspace.getMetrics();
            var dx = this.hasAttribute("dx") ? Number(this.getAttribute("dx")) : 0;
            var dy = this.hasAttribute("dy") ? Number(this.getAttribute("dy")) : 0;
            var x = this.element_.offsetLeft - metrics.absoluteLeft + this.element_.offsetWidth/2;
            var y = this.element_.offsetTop - metrics.absoluteTop +  this.element_.offsetHeight/2;
            var width = this.hasAttribute("width") ? Number(this.getAttribute("width")) : 100;
            var height = this.hasAttribute("height") ? Number(this.getAttribute("height")) : 50;
            this.bubble_ = new AbbozzaBubble(Blockly.mainWorkspace, null,null,"taskHint",x,y,width,height,dx,dy);
            // this.bubble_ = new InternalWindow(Blockly.mainWorkspace, null,"","taskHint",x,y,width,height,false);
            this.bubble_.setText(this.innerHTML);
            this.bubble_.layoutBubble_();
            this.bubble_.setBubbleSize(width,height);
            // this.bubble_.setShift(dx,dy);
            // this.bubble_.setColour("#909090");
            // this.bubble_.render();
        }
    } else {
        
    }
}


abbozzaHintProto.detachedCallback = function() {
    if (this.icon_) {
        this.icon_.setVisible(false);
        if (this.block_.warning == this.icon_) this.block_.warning = null;
        this.icon_.dispose();
        this.block_.render();
        this.block_.bumpNeighbours_();
    } else if (this.bubble_) {
        this.bubble_.dispose();
    }
    this.block_ = null;
    this.icon_ = null;
    this.element_ = null;
    this.bubble_ = null;
}


var abbozzaHint = document.registerElement('abbozza-hint', {
    prototype: abbozzaHintProto
});
