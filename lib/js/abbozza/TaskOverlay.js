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

TaskOverlay = {
}

TaskOverlay.init = function () {
    TaskOverlay.overlay_ = document.createElementNS(Blockly.HTML_NS, 'div');
    TaskOverlay.overlay_.className = "taskOverlay";
    TaskOverlay.overlay_.id = "taskOverlay";
    TaskOverlay.overlay_.display = "none";
    TaskOverlay.overlay_.onmousedown = TaskOverlay.onmousedown;
    TaskOverlay.overlay_.onmouseover = TaskOverlay.onmouseover;
    TaskOverlay.minWidth = 300;
    TaskOverlay.minHeight = 200;
    
    TaskOverlay.closeButton_ = document.createElementNS(Blockly.HTML_NS,'div');
    TaskOverlay.closeButton_.className = "taskOverlayButton";
    TaskOverlay.closeButton_.innerHTML = '<img src="img/iwin_close.png" width="16px"/>';
    var overlay = this;
    TaskOverlay.closeButton_.onclick = function (event) { overlay.closeClicked(event); }
    TaskOverlay.overlay_.appendChild(TaskOverlay.closeButton_);
    
    TaskOverlay.editButton_ = document.createElementNS(Blockly.HTML_NS,'div');
    TaskOverlay.editButton_.className = "taskOverlayButton";
    TaskOverlay.editButton_.innerHTML = '<img src="img/iwin_edit.png" width="16px"/>';
    TaskOverlay.editButton_.onclick = function (event) { overlay.editClicked(event); }
    TaskOverlay.overlay_.appendChild(TaskOverlay.editButton_);

    TaskOverlay.content_  = document.createElementNS(Blockly.HTML_NS,'div');
    TaskOverlay.content_.className = "taskOverlayContent";
    TaskOverlay.content_.innerHTML = '';
    TaskOverlay.content_.style.display = "block";
    TaskOverlay.overlay_.appendChild(TaskOverlay.content_);

    TaskOverlay.editor_  = document.createElementNS(Blockly.HTML_NS,'textarea');
    TaskOverlay.editor_.className = "taskOverlayEditor";
    TaskOverlay.editor_.value = '';
    TaskOverlay.editor_.style.display = "none";
    TaskOverlay.overlay_.appendChild(TaskOverlay.editor_);

    TaskOverlay.editing_ = false;

}


TaskOverlay.show = function() {
    TaskOverlay.overlay_.style.top = "60px";
    TaskOverlay.overlay_.style.right = "20px";
    TaskOverlay.overlay_.display = "block";
    document.getElementsByTagName("body")[0].appendChild(TaskOverlay.overlay_);
}

TaskOverlay.hide = function() {
    TaskOverlay.overlay_.display = "none";
    if ( document.getElementsByClassName("taskOverlay").length > 0 ) {
        document.getElementsByTagName("body")[0].removeChild(TaskOverlay.overlay_);
    }
}

TaskOverlay.isVisible = function () {
    return ( TaskOverlay.overlay_.display == "block" );
}

TaskOverlay.setTitle = function(title) {
    TaskOverlay.title_.textContent = title;
}

TaskOverlay.getTitle = function() {
    return TaskOverlay.title_.textContent;
}

TaskOverlay.setContent = function(html) {
    TaskOverlay.content_.innerHTML = html;
}

TaskOverlay.getContent = function() {
    return TaskOverlay.content_.innerHTML;
}

TaskOverlay.closeClicked = function(event) {
    Abbozza.taskContent = this.getContent();
    TaskOverlay.hide();
    event.stopPropagation();
}

TaskOverlay.editClicked = function(event) {
    if (TaskOverlay.editing_ == false) { 
        // TaskOverlay.editor_.style = TaskOverlay.content_.style;
        TaskOverlay.editor_.style.display="block";
        var width = TaskOverlay.content_.offsetWidth  - 10;
        TaskOverlay.editor_.style.width= width  + "px";
        TaskOverlay.content_.style.display="none";
        TaskOverlay.editor_.value = TaskOverlay.content_.innerHTML; 
        TaskOverlay.editing_ = true;
        TaskOverlay.editor_.focus();
    } else {
        // TaskOverlay.content_.style = TaskOverlay.editor_.style;
        TaskOverlay.content_.style.display="block";
        TaskOverlay.editor_.style.display="none";
        TaskOverlay.content_.innerHTML = TaskOverlay.editor_.value; 
        TaskOverlay.editing_ = false;        
        Abbozza.taskContent = this.getContent();
    }
    
    // TaskOverlay.editor_ = new EditorOverlay();
    // TaskOverlay.editor_.show(this);
    event.stopPropagation();
}

TaskOverlay.clicked = function(event) {}

TaskOverlay.onmouseover = function(event) {
    var relX = event.clientX + window.pageXOffset - event.srcElement.offsetLeft;
    var relY = event.clientY + window.pageYOffset - event.srcElement.offsetTop;    
    if ( (relY < 34) && (relX < TaskOverlay.overlay_.offsetWidth-100) ) {
        TaskOverlay.overlay_.style.cursor="move";
    } else if ( (relX < 10) && (relY > 34) ) {
        TaskOverlay.overlay_.style.cursor="w-resize";        
    } else if ((relX > TaskOverlay.overlay_.offsetWidth-10) && (relY > 34)) {
        TaskOverlay.overlay_.style.cursor="e-resize";
    } else if ( relY > TaskOverlay.overlay_.offsetHeight-10) {
            TaskOverlay.overlay_.style.cursor="s-resize";
    } else {
        TaskOverlay.overlay_.style.cursor="default";        
    }
}


TaskOverlay.onmousedown = function(event) {
    TaskOverlay.relX = event.clientX + window.pageXOffset - TaskOverlay.overlay_.offsetLeft;
    TaskOverlay.relY = event.clientY + window.pageYOffset - TaskOverlay.overlay_.offsetTop;
  
    if ( (TaskOverlay.relY < 34) && (TaskOverlay.relX < TaskOverlay.overlay_.offsetWidth-100) ) {
        // dragging
        DraggingManager.start(TaskOverlay.overlay_,TaskOverlay.ondrag,TaskOverlay.ondragend);
    } else if ((TaskOverlay.relX < 10) && (TaskOverlay.relY > 34) ) {
        // resize left
        DraggingManager.start(TaskOverlay.overlay_,TaskOverlay.resizeleft,TaskOverlay.resizeend);
    } else if ((TaskOverlay.relX > TaskOverlay.overlay_.offsetWidth-10)  && (TaskOverlay.relY > 34)) {
        // resize right
        DraggingManager.start(TaskOverlay.overlay_,TaskOverlay.resizeright,TaskOverlay.resizeend);
    } else if ( TaskOverlay.relY > TaskOverlay.overlay_.offsetHeight-10) {
        // resize bottom
        DraggingManager.start(TaskOverlay.overlay_,TaskOverlay.resizebottom,TaskOverlay.resizeend);
    } else {
    }
    event.stopPropagation();
}


TaskOverlay.ondrag = function(event) {
        var newLeft = event.clientX + window.pageXOffset - TaskOverlay.relX;
        var newRight = window.innerWidth - (newLeft + this.offsetWidth);
        var newTop = event.clientY + window.pageYOffset - TaskOverlay.relY;
        
        this.style.right = newRight + "px";
        this.style.top = newTop + "px";
}


TaskOverlay.ondragend = function(event) {}


TaskOverlay.resizeleft = function(event) {    
        var newLeft = event.clientX + window.pageXOffset - TaskOverlay.relX;
        var oldLeft = this.offsetLeft;
        var newWidth = this.offsetWidth + ( oldLeft - newLeft );   
        this.style.width = newWidth + "px";
}

TaskOverlay.resizeright = function(event) { 
        var oldLeft = this.offsetLeft;
        var oldRight = window.innerWidth - this.offsetLeft - this.offsetWidth;
        var newRight = window.innerWidth - event.clientX - window.pageXOffset;
        var newWidth = this.offsetWidth -4 + ( oldRight - newRight );

        if ( newWidth > TaskOverlay.minWidth ) {
            this.style.width = newWidth + "px"
            this.style.right = newRight + "px"
        }
}

TaskOverlay.resizebottom = function(event) {    
        var oldTop = this.offsetTop;
        var oldBottom = this.offsetTop + this.offsetHeight;
        var newBottom = event.clientY + window.pageYOffset;
        var newHeight = this.offsetHeight - 3 + ( newBottom - oldBottom );

        if ( newHeight > TaskOverlay.minHeight ) {
            this.style.height = newHeight + "px"
        }
}

TaskOverlay.resizeend = function(event) {}

