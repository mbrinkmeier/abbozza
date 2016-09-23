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
    pages_ : [],
    currentPage_ : 0
}

TaskOverlayinit = function () {
    TaskOverlayoverlay_ = document.createElementNS(Blockly.HTML_NS, 'div');
    TaskOverlayoverlay_.className = "taskOverlay";
    TaskOverlayoverlay_.id = "taskOverlay";
    TaskOverlayoverlay_.style.display = "none";
    TaskOverlayoverlay_.onmousedown = TaskOverlayonmousedown;
    TaskOverlayoverlay_.onmouseover = TaskOverlayonmouseover;
    TaskOverlayminWidth = 300;
    TaskOverlayminHeight = 200;
    
    TaskOverlaycloseButton_ = document.createElementNS(Blockly.HTML_NS,'div');
    TaskOverlaycloseButton_.className = "taskOverlayButton";
    TaskOverlaycloseButton_.innerHTML = '<img src="img/iwin_close.png" width="16px"/>';
    TaskOverlaycloseButton_.onclick = function (event) { TaskOverlaycloseClicked(event); }
    TaskOverlayoverlay_.appendChild(TaskOverlaycloseButton_);
    
    TaskOverlayeditButton_ = document.createElementNS(Blockly.HTML_NS,'div');
    TaskOverlayeditButton_.className = "taskOverlayButton";
    TaskOverlayeditButton_.innerHTML = '<img src="img/iwin_edit.png" width="16px"/>';
    TaskOverlayeditButton_.onclick = function (event) { TaskOverlayeditClicked(event); }
    TaskOverlayoverlay_.appendChild(TaskOverlayeditButton_);

    TaskOverlaypage_  = document.createElementNS(Blockly.HTML_NS,'div');
    TaskOverlaypage_.className = "taskOverlayPage";
    TaskOverlaypage_.innerHTML = '';
    TaskOverlaypage_.style.display = "block";
    TaskOverlayoverlay_.appendChild(TaskOverlaypage_);

    TaskOverlaycontent_  = document.createElementNS(Blockly.HTML_NS,'pages');
    TaskOverlaycontent_.className = "taskOverlayContent";
    TaskOverlaycontent_.innerHTML = '';
    TaskOverlaycontent_.style.display = "none";
    TaskOverlayoverlay_.appendChild(TaskOverlaycontent_);

    TaskOverlayeditor_  = document.createElementNS(Blockly.HTML_NS,'textarea');
    TaskOverlayeditor_.className = "taskOverlayEditor";
    TaskOverlayeditor_.value = '';
    TaskOverlayeditor_.style.display = "none";
    TaskOverlayoverlay_.appendChild(TaskOverlayeditor_);

    TaskOverlaynav_  = document.createElementNS(Blockly.HTML_NS,'div');
    TaskOverlaynav_.className = "taskOverlayNav";
    TaskOverlaynav_.value = '';
    TaskOverlaynav_.style.display = "none";
    TaskOverlayoverlay_.appendChild(TaskOverlaynav_);
    TaskOverlayediting_ = false;

    TaskOverlaynav_.prev_ = document.createElementNS(Blockly.HTML_NS,'span');
    TaskOverlaynav_.prev_.innerHTML="&larr;";
    TaskOverlaynav_.prev_.className = "taskOverlayNavButton";
    TaskOverlaynav_.prev_.onclick = TaskOverlayprevPage_;
    TaskOverlaynav_.appendChild(TaskOverlaynav_.prev_);

    TaskOverlaynav_.pageno_ = document.createElementNS(Blockly.HTML_NS,'span');
    TaskOverlaynav_.pageno_.textContent = TaskOverlaycurrentPage_+1;
    TaskOverlaynav_.pageno_.className = "taskOverlayNavButton";
    TaskOverlaynav_.appendChild(TaskOverlaynav_.pageno_);

    TaskOverlaynav_.next_ = document.createElementNS(Blockly.HTML_NS,'span');
    TaskOverlaynav_.next_.innerHTML="&rarr;";
    TaskOverlaynav_.next_.onclick = TaskOverlaynextPage_;
    TaskOverlaynav_.next_.className = "taskOverlayNavButton";
    TaskOverlaynav_.appendChild(TaskOverlaynav_.next_);
}


TaskOverlayshow = function() {
    TaskOverlayoverlay_.style.top = "60px";
    TaskOverlayoverlay_.style.right = "20px";
    TaskOverlayoverlay_.style.display = "block";
    TaskOverlayshowContent(false);
    document.getElementsByTagName("body")[0].appendChild(TaskOverlayoverlay_);
}

TaskOverlayhide = function() {
    TaskOverlayoverlay_.style.display = "none";
    TaskOverlaypage_.style.display = "block";
    TaskOverlayeditor_.style.display = "none";
    TaskOverlayeditor_.value = "";
    TaskOverlayediting_ = false;
    
    if ( document.getElementsByClassName("taskOverlay").length > 0 ) {
        document.getElementsByTagName("body")[0].removeChild(TaskOverlayoverlay_);
    }
}


TaskOverlayshowEditor = function () {
        TaskOverlayeditor_.style.display="block";
        var width = TaskOverlaypage_.offsetWidth  - 10;
        TaskOverlayeditor_.style.width= width  + "px";
        TaskOverlaypage_.style.display="none";
        TaskOverlaynav_.style.display="none";
        TaskOverlayeditor_.value = TaskOverlaygetContent(); 
        TaskOverlayediting_ = true;
        TaskOverlayeditor_.focus();    
}


TaskOverlayshowContent = function (getContentFromEditor) {
        // TaskWindow.content_.style = TaskWindow.editor_.style;
        TaskOverlaypage_.style.display="block";
               
        TaskOverlayeditor_.style.display="none";
        if (getContentFromEditor) TaskOverlaysetContent(TaskOverlayeditor_.value,false); 

         if ( TaskOverlaypages_.length > 1) {
            TaskOverlaynav_.style.display="block";
        } else {
            TaskOverlaynav_.style.display="none";
        }
        
        TaskOverlayeditor_.value = ""; 
        TaskOverlayediting_ = false;
}


TaskOverlayisVisible = function () {
    return ( TaskOverlayoverlay_.style.display == "block" );
}

TaskOverlaysetTitle = function(title) {
    TaskOverlaytitle_.textContent = title;
}

TaskOverlaygetTitle = function() {
    return TaskOverlaytitle_.textContent;
}


TaskOverlaysetContent = function(html, reset) {
    TaskOverlaycontent_.innerHTML = html;
    TaskOverlaypages_ = TaskOverlaycontent_.getElementsByTagName('page');
            
    if (TaskOverlaypages_.length == 0 ) {
        // If no page is given, the whole thing is the page
        var page = document.createElementNS(Blockly.HTML_NS,"page");
        page.innerHTML = TaskOverlaycontent_.innerHTML;
        TaskOverlaypages_ = [ page ] ;
    }    
            
    // Go to the current page
    if ( (TaskOverlaycurrentPage_ < TaskOverlaypages_.length ) && !reset) {
        TaskOverlaysetPage_(TaskOverlaycurrentPage_);
    } else {
        TaskOverlaysetPage_(0);
    }
}


TaskOverlaysetPage_ = function (page) {
    // If page is out of scope, do nothing
    if ((page >= TaskOverlaypages_.length) || (page < 0)) return;
        
    // Show selected page
    TaskOverlaycurrentPage_ = page;
    // if (TaskWindow.pages_[TaskWindow.currentPage_].innerHTML) {
        
    TaskOverlaypage_.innerHTML = TaskOverlaypages_[TaskOverlaycurrentPage_].innerHTML;
    // } else {
    // console.log("da");
    //     TaskWindow.page_.innerHTML = "";
    // }
    
    TaskOverlaynav_.pageno_.textContent = TaskOverlaycurrentPage_ + 1 ;
}

TaskOverlayprevPage_ = function() {
    if ( TaskOverlaycurrentPage_ > 0 ) {
        TaskOverlaysetPage_(TaskOverlaycurrentPage_-1);
    }
}

TaskOverlaynextPage_ = function() {
    if ( TaskOverlaycurrentPage_ <  TaskOverlaypages_.length-1 ) {
        TaskOverlaysetPage_(TaskOverlaycurrentPage_+1);
    }
}

TaskOverlaygetContent = function() {
    return TaskOverlaycontent_.innerHTML;
}

TaskOverlaysetSize = function(width,height) {
    width = (width > TaskOverlayminWidth ) ? width : TaskOverlayminWidth;
    height = (height > TaskOverlayminHeight ) ? height : TaskOverlayminHeight;
    TaskOverlayoverlay_.style.width = width +"px";
    TaskOverlayoverlay_.style.height = height +"px";
}

TaskOverlaygetWidth = function() {
    return TaskOverlayoverlay_.offsetWidth;
}

TaskOverlaygetHeight = function() {
    return TaskOverlayoverlay_.offsetHeight;
}


TaskOverlaycloseClicked = function(event) {
    if ( TaskOverlayediting_ ) {
        TaskOverlayshowContent(true);
    }
    TaskOverlayhide();
    event.stopPropagation();        
}

TaskOverlayeditClicked = function(event) {
    if (TaskOverlayediting_ == false) { 
        TaskOverlayshowEditor();
    } else {
        TaskOverlayshowContent(true);
    }    
    event.stopPropagation();
}

TaskOverlayclicked = function(event) {}

TaskOverlayonmouseover = function(event) {
    var relX = event.clientX + window.pageXOffset - event.srcElement.offsetLeft;
    var relY = event.clientY + window.pageYOffset - event.srcElement.offsetTop;    
    if ( (relY < 34) && (relX < TaskOverlayoverlay_.offsetWidth-100) ) {
        TaskOverlayoverlay_.style.cursor="move";
    } else if ( (relX < 10) && (relY > 34) ) {
        TaskOverlayoverlay_.style.cursor="w-resize";        
    } else if ((relX > TaskOverlayoverlay_.offsetWidth-10) && (relY > 34)) {
        TaskOverlayoverlay_.style.cursor="e-resize";
    } else if ( relY > TaskOverlayoverlay_.offsetHeight-10) {
            TaskOverlayoverlay_.style.cursor="s-resize";
    } else {
        TaskOverlayoverlay_.style.cursor="pointer";        
    }
}


TaskOverlayonmousedown = function(event) {
    TaskOverlayrelX = event.clientX + window.pageXOffset - TaskOverlayoverlay_.offsetLeft;
    TaskOverlayrelY = event.clientY + window.pageYOffset - TaskOverlayoverlay_.offsetTop;
  
    if ( (TaskOverlayrelY < 34) && (TaskOverlayrelX < TaskOverlayoverlay_.offsetWidth-100) ) {
        // dragging
        DraggingManager.start(TaskOverlayoverlay_,TaskOverlayondrag,TaskOverlayondragend);
    } else if ((TaskOverlayrelX < 10) && (TaskOverlayrelY > 34) ) {
        // resize left
        DraggingManager.start(TaskOverlayoverlay_,TaskOverlayresizeleft,TaskOverlayresizeend);
    } else if ((TaskOverlayrelX > TaskOverlayoverlay_.offsetWidth-10)  && (TaskOverlayrelY > 34)) {
        // resize right
        DraggingManager.start(TaskOverlayoverlay_,TaskOverlayresizeright,TaskOverlayresizeend);
    } else if ( TaskOverlayrelY > TaskOverlayoverlay_.offsetHeight-10) {
        // resize bottom
        DraggingManager.start(TaskOverlayoverlay_,TaskOverlayresizebottom,TaskOverlayresizeend);
    } else {
        // TaskWindow.setPage_(TaskWindow.currentPage_+1);
    }
    event.stopPropagation();
}


TaskOverlayondrag = function(event) {
        var newLeft = event.clientX + window.pageXOffset - TaskOverlayrelX;
        var newRight = window.innerWidth - (newLeft + this.offsetWidth);
        var newTop = event.clientY + window.pageYOffset - TaskOverlayrelY;
        
        this.style.right = newRight + "px";
        this.style.top = newTop + "px";
}


TaskOverlayondragend = function(event) {}


TaskOverlayresizeleft = function(event) {    
        var newLeft = event.clientX + window.pageXOffset - TaskOverlayrelX;
        var oldLeft = this.offsetLeft;
        var newWidth = this.offsetWidth + ( oldLeft - newLeft );   
        this.style.width = newWidth + "px";
}

TaskOverlayresizeright = function(event) { 
        var oldLeft = this.offsetLeft;
        var oldRight = window.innerWidth - this.offsetLeft - this.offsetWidth;
        var newRight = window.innerWidth - event.clientX - window.pageXOffset;
        var newWidth = this.offsetWidth -4 + ( oldRight - newRight );

        if ( newWidth > TaskOverlayminWidth ) {
            this.style.width = newWidth + "px"
            this.style.right = newRight + "px"
        }
}

TaskOverlayresizebottom = function(event) {    
        var oldTop = this.offsetTop;
        var oldBottom = this.offsetTop + this.offsetHeight;
        var newBottom = event.clientY + window.pageYOffset;
        var newHeight = this.offsetHeight - 3 + ( newBottom - oldBottom );

        if ( newHeight > TaskOverlayminHeight ) {
            this.style.height = newHeight + "px"
        }
}

TaskOverlayresizeend = function(event) {}

