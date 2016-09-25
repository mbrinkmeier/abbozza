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
    console.log("Hint: attached");
    console.log(this.getAttribute("block"));
    var x = Number(this.getAttribute("x"));
    var y = Number(this.getAttribute("y"));
    var width = Number(this.getAttribute("width"));
    var height = Number(this.getAttribute("height"));
    this.bubble = new InternalWindow(Blockly.mainWorkspace,null,"","",x,y,width,height,false);
    this.bubble.setText(this.innerHTML);
    this.bubble.resize_();
}

abbozzaHintProto.detachedCallback = function() {
    console.log("Hint: detached");
    this.bubble.dispose();
    this.bubble = null;
}


var abbozzaHint = document.registerElement('abbozza-hint', {
    prototype: abbozzaHintProto
});