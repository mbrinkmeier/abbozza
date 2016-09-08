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
 * @fileoverview A handler for the block colors 
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */

var ColorMgr = {
};

ColorMgr.BY_CATEGORY = 1;
ColorMgr.BY_TYPE = 2;
ColorMgr.BY_DEFAULT = 0;
    
ColorMgr._strategy = ColorMgr.BY_TYPE;


ColorMgr.catColor = new Array();

ColorMgr.catColor["cat.CTRL"]=330;
ColorMgr.catColor["cat.COND"]=0;    
ColorMgr.catColor["cat.LOOPS"]=345;
ColorMgr.catColor["cat.FUNC"]=330;
ColorMgr.catColor["cat.VAR"]=300;

ColorMgr.catColor["cat.LOGIC"]=285;
ColorMgr.catColor["cat.MATH"]=270;
ColorMgr.catColor["cat.TEXT"]=240;

ColorMgr.catColor["cat.INOUT"]=160;
ColorMgr.catColor["cat.DEVICES"]=135;
ColorMgr.catColor["cat.DEVIN"]=115;
ColorMgr.catColor["cat.DEVOUT"]=90;

ColorMgr.catColor["cat.SERIAL"]=60;
ColorMgr.catColor["cat.INT"]=45;

ColorMgr.catColor["cat.TEACHER"]=30;

ColorMgr.typeColor = new Array();

ColorMgr.typeColor["BOOLEAN"]=285; 
ColorMgr.typeColor["NUMBER"]=270;  
ColorMgr.typeColor["DECIMAL"]=255; 
ColorMgr.typeColor["STRING"]=240;  
ColorMgr.typeColor["DEVICE"]=135;  
ColorMgr.typeColor["VAR"]=300;
ColorMgr.typeColor["FUNC"]=330;
ColorMgr.typeColor["PIN"]=115;
ColorMgr.typeColor[""]=230;



ColorMgr.getCatColor = function(cat) {
    
    if ( cat == undefined ) return 0;
    
    if ( !ColorMgr.catColor[cat] ) return 0;
    
    return ColorMgr.catColor[cat];
}

ColorMgr.getColor = function(cat) {
    if ( !ColorMgr.catColor[cat] ) return 0;
    
    return ColorMgr.catColor[cat];
}


ColorMgr.getBlockColour = function(block) {

    if (this._strategy == this.BY_CATEGORY) {
        return ColorMgr.getCatColor(block._category);
    } else if (this._strategy == this.BY_DEFAULT) {
        return block.colourHue_;
    } 
    
    var type;
   
    if (!block.outputConnection || !block.outputConnection.check_) {
        type = "";    
    } else {
        type = block.outputConnection.check_[0];
    }    
    return ColorMgr.typeColor[type];
}


ColorMgr.colorizeBlocks = function(toolbox) {
    var entries = toolbox.getElementsByTagName("category");
    for (var i = 0; i < entries.length; i++) {
        var blocks = entries[i].getElementsByTagName("block");
        console.log(entries[i].getAttribute("id"));
        var color = ColorMgr.getCatColor(entries[i].getAttribute("id"));
        for (var j= 0; j < blocks.length; j++) {
            var type = blocks[j].getAttribute("type");
            Blockly.Blocks[type].setColour(color);
        }
    }
}