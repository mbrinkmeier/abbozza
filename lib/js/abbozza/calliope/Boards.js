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

Board = {
    boardsXML: null,
    description: "Calliope or micro:bit",
    boardId: "calliope",
    analogIn: 3,
    analogOut: 0,
    digitalPins: 14,
    pwm: [0,1,2,5,6,7,8,9,11,12,13,14,15,16],
    internalLED: -1,
    interrupts: null,
    serialRX: [],
    serialTX: []
};


Board.init = function (systemPrefix) {
    this.load(false);
};


Board.DIGITAL_PIN = 0;
Board.TOUCH_PIN = 1;
Board.ANALOG_PIN = 2;


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

Board.getPinMenu = function(type) {
    var menu;
    if ( type == PinDropdown.ANALOG ) {
        menu = this.getAnalogPinMenu();        
    } else if ( type == PinDropdown.TOUCH ) {
        menu = this.getTouchPinMenu();
    } else {
        menu = this.getDigitalPinMenu();        
    }
    return menu;
}


Board.getTouchPinMenu = function() {
    var pins = [
        ["0","pin0"],
        ["1","pin1"],
        ["2","pin2"]
    ];
    return pins;
}


Board.getDigitalPinMenu = function() {
    var pins = [
        ["0","pin0"],
        ["1","pin1"],
        ["2","pin2"],
        ["3","pin3"],
        ["4","pin4"],
        ["5","pin5"],
        ["6","pin6"],
        ["7","pin7"],
        ["8","pin8"],
        ["9","pin9"],
        ["10","pin10"],
        ["11","pin11"],
        ["12","pin12"],
        ["13","pin13"],
        ["14","pin14"],
        ["15","pin15"],
        ["16","pin16"],
        ["19","pin19"],
        ["20","pin20"]
    ]
    return pins;
}


Board.getAnalogPinMenu = function() {
    var pins = [
        ["3","pin3"],
        ["4","pin4"],
        ["10","pin10"]
    ]
    return pins;
}


Board._isPWM = function(pin) {
    return true;
}

