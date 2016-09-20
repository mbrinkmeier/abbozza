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

TaskOverlay = function () {
    this.overlay_ = document.createElementNS(Blockly.HTML_NS, 'div');
    this.overlay_.className = "taskOverlay";
    this.overlay_.id = "taskOverlay";
    this.overlay_.display = "none";
}


TaskOverlay.prototype.show = function() {
    this.overlay_.display = "block";
    document.getElementsByTagName("body")[0].appendChild(this.overlay_);
}

TaskOverlay.prototype.hide = function() {
    console.log("Hier");
    this.overlay_.display = "none";
    document.getElementsByTagName("body")[0].removeChild(this.overlay_);
}

TaskOverlay.prototype.isVisible = function () {
    return ( this.overlay_.display == "block" );
}