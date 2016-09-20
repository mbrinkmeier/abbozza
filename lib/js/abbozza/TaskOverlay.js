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

    /*
    this.title_ = document.createElementNS(Blockly.HTML_NS,'div');
    this.title_.className = "taskOverlayTitle";
    this.title_.textContent = "Titel";
    this.overlay_.appendChild(this.title_);
    */
   
    this.closeButton_ = document.createElementNS(Blockly.HTML_NS,'div');
    this.closeButton_.className = "taskOverlayButton";
    this.closeButton_.innerHTML = '<img src="img/iwin_close.png" width="16px"/>';
    var overlay = this;
    this.closeButton_.onclick = function (event) { overlay.closeClicked(event); }
    this.overlay_.appendChild(this.closeButton_);
    
    this.editButton_ = document.createElementNS(Blockly.HTML_NS,'div');
    this.editButton_.className = "taskOverlayButton";
    this.editButton_.innerHTML = '<img src="img/iwin_edit.png" width="16px"/>';
    this.editButton_.onclick = function (event) { overlay.editClicked(event); }
    this.overlay_.appendChild(this.editButton_);

    this.content_  = document.createElementNS(Blockly.HTML_NS,'div');
    this.content_.className = "taskOverlayContent";
    this.content_.innerHTML = '';
    this.overlay_.appendChild(this.content_);
}


TaskOverlay.prototype.show = function() {
    this.overlay_.display = "block";
    document.getElementsByTagName("body")[0].appendChild(this.overlay_);
}

TaskOverlay.prototype.hide = function() {
    this.overlay_.display = "none";
    document.getElementsByTagName("body")[0].removeChild(this.overlay_);
}

TaskOverlay.prototype.isVisible = function () {
    return ( this.overlay_.display == "block" );
}

TaskOverlay.prototype.setTitle = function(title) {
    this.title_.textContent = title;
}

TaskOverlay.prototype.getTitle = function() {
    return this.title_.textContent;
}

TaskOverlay.prototype.setContent = function(html) {
    this.content_.innerHTML = html;
}

TaskOverlay.prototype.getContent = function() {
    return this.content_.innerHTML;
}

TaskOverlay.prototype.closeClicked = function(event) {
    Abbozza.taskContent = this.getContent();
    Abbozza.openTaskOverlay();
}

TaskOverlay.prototype.editClicked = function(event) {
    this.editor_ = new EditorOverlay();
    this.editor_.show(this);
}