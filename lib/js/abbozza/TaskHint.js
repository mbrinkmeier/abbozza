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
    var block = [];
    var blockid = this.hasAttribute("block") ? this.getAttribute("block") : null;
    if (blockid != null) {
        block = Abbozza.getBlocksById(blockid);
    } else {
        block.push(null);
    }
    var x = this.hasAttribute("x") ? Number(this.getAttribute("x")) : 0;
    var y = this.hasAttribute("y") ? Number(this.getAttribute("y")) : 0;
    var width = this.hasAttribute("width") ? Number(this.getAttribute("width")) : 200
    var height = this.hasAttribute("height") ? Number(this.getAttribute("height")) : 100;
    this.bubble = new InternalWindow(Blockly.mainWorkspace, block[0],"","taskHint",x,y,width,height,false);
    this.bubble.setColour("#909090");
    this.bubble.setText(this.innerHTML);
    this.bubble.resize_();
}


abbozzaHintProto.detachedCallback = function() {
    this.bubble.dispose();
    this.bubble = null;
}


var abbozzaHint = document.registerElement('abbozza-hint', {
    prototype: abbozzaHintProto
});

