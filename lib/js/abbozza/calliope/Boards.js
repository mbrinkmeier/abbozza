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
    if ( query == false ) {
        Connection.getText("/abbozza/board",
            function (response) {
                // var val = response.split("|");
                // Abbozza.showInfoMessage(_("msg.board_found",[val[1],[2]]));
                // Board._apply(response.split("|")[0]);
                document.getElementById("connect").style.backgroundColor = "#00d000";
            },
            function (response) {
                Abbozza.showInfoMessage(_("msg.no_board"));
                // Board._apply(response.split("|")[0]);                    
                document.getElementById("connect").style.backgroundColor = "#d00000";
            }
        );
    } else {
        Connection.getText("/abbozza/queryboard",
            function (response) {
                var val = response.split("|");
                Abbozza.showInfoMessage(_("msg.board_found",[val[1],[2]]));
                // Board._apply(response.split("|")[0]);
                document.getElementById("connect").style.backgroundColor = "#00d000";
            },
            function (response) {
                Abbozza.showInfoMessage(_("msg.no_board"));
                // Board._apply(response.split("|")[0]);
                document.getElementById("connect").style.backgroundColor = "#d00000";
            }
        );
        
    }    
}


Board.getTouchPinMenu = function() {
    var pins = [
        ["Pin 0","pin0"],
        ["Pin 1","pin1"],
        ["Pin 2","pin2"]
    ];
    return pins;
}


Board.getDigitalPinMenu = function() {
    var pins = [
        ["Pin 5","pin5"],
        ["Pin 6","pin6"],
        ["Pin 7","pin7"],
        ["Pin 8","pin8"],
        ["Pin 9","pin9"],
        ["Pin 11","pin11"],
        ["Pin 12","pin12"],
        ["Pin 13","pin13"],
        ["Pin 14","pin14"],
        ["Pin 15","pin15"],
        ["Pin 16","pin16"],
        ["Pin 19","pin19"],
        ["Pin 20","pin20"]
    ]
    return pins;
}


Board.getAnalogPinMenu = function() {
    var pins = [
        ["Pin 3","pin3"],
        ["Pin 4","pin4"],
        ["Pin 10","pin10"]
    ]
    return pins;
}

