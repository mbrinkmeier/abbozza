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

DraggingManager = {
    draggingClient_: null,
    ondrag_ : null,
    ondragend_ : null
}

DraggingManager.start = function(client,ondrag,ondragend) {
    DraggingManager.client_ = client;
    DraggingManager.ondrag_ = ondrag;
    DraggingManager.ondragend_ = ondragend;
    
    document.addEventListener("mousemove",DraggingManager.onmousemove,false);
    document.addEventListener("mouseup",DraggingManager.onmouseup,false);
}


DraggingManager.stop = function() {
    DraggingManager.client_ = null;
    DraggingManager.ondrag_ = null;
    DraggingManager.ondragend_ = null;
    
    document.removeEventListener("mousemove",DraggingManager.onmousemove);
    document.removeEventListener("mouseup",DraggingManager.onmouseup);    
}


DraggingManager.onmousemove = function(event) {

    if ( DraggingManager.client_ == null ) return;
    
    if ( event.buttons == 1) {   
        if ( DraggingManager.ondrag_ != null ) {
            DraggingManager.ondrag_.call(DraggingManager.client_,event);
        }
    } else if (event.buttons == 0) {
        if ( DraggingManager.ondragend_ != null ) {
            DraggingManager.ondragend_.call(DraggingManager.client_,event);
        }
    }
}


DraggingManager.onmouseup = function(event) {

    if ( DraggingManager.client_ == null ) return;
    
    if (event.buttons == 0) {
        if ( DraggingManager.ondragend_ != null ) {
            DraggingManager.ondragend_.call(DraggingManager.client_,event);
        }
        DraggingManager.stop();
    }
}