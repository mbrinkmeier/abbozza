/**
 * @license
 * abbozza!
 *
 * File: Boards.js
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
 * 
 * SYSTEM SPECIFIC
 */

/**
 * @fileoverview A singleton object managing the parameters of the current board.
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */

Board = {};


Board.init = function (systemPrefix) {
    this.load(false);
};


Board.load = function (query) {
    console.log("hier");
    if ( query == false ) {
        console.log("no query");
        Connection.getTextSynced("/abbozza/board",
            function (response) {
                console.log(document);
                console.log(response);
                // var val = response.split("|");
                // Abbozza.showInfoMessage(_("msg.board_found",[val[1],[2]]));
                // Board._apply(response.split("|")[0]);
                document.getElementById("connect").style.backgroundColor = "#00d000";
            },
            function (response) {
                console.log("failed");
                Abbozza.showInfoMessage(_("msg.no_board"));
                // Board._apply(response.split("|")[0]);                    
                document.getElementById("connect").style.backgroundColor = "#d00000";
            }
        );
        console.log("queried");
    } else {
        Connection.getTextSynced("/abbozza/queryboard",
            function (response) {
                var val = response.split("|");
                Abbozza.showInfoMessage(_("msg.board_found",[val[1],[2]]));
                Board._apply(response.split("|")[0]);
                document.getElementById("connect").style.backgroundColor = "#00d000";
            },
            function (response) {
                Abbozza.showInfoMessage(_("msg.no_board"));
                Board._apply(response.split("|")[0]);
                document.getElementById("connect").style.backgroundColor = "#d00000";
            }
        );
        
    }    
}


