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

/**
 * The Database for handling image names
 * 
 * @returns {undefined}
 */

/**
 * A list of all images known by micropython
 * 
 * @type Array
 */
Abbozza.Images = [
"Image.HEART",
"Image.HEART_SMALL",
"Image.HAPPY",
"Image.SMILEY",
"Image.SAD",
"Image.SURPRISED",
"Image.BEARD",
"Image.MOUSTACHE",
"Image.YES",
"Image.NO" /*,
"Image.CLOCK12",
"Image.CLOCK11",
"Image.CLOCK10",
"Image.CLOCK9",
"Image.CLOCK8",
"Image.CLOCK7",
"Image.CLOCK6",
"Image.CLOCK5",
"Image.CLOCK4",
"Image.CLOCK3",
"Image.CLOCK2",
"Image.CLOCK1",
"Image.ARROW_N",
"Image.ARROW_NE",
"Image.ARROW_E",
"Image.ARROW_SE",
"Image.ARROW_S",
"Image.ARROW_SW",
"Image.ARROW_W",
"Image.ARROW_NW",
"Image.TRIANGLE",
"Image.TRIANGLE_LEFT",
"Image.CHESSBOARD",
"Image.DIAMOND",
"Image.DIAMOND_SMALL",
"Image.SQUARE",
"Image.SQUARE_SMALL",
"Image.RABBIT",
"Image.COW",
"Image.MUSIC_CROTCHET",
"Image.MUSIC_QUAVER",
"Image.MUSIC_QUAVERS",
"Image.PITCHFORK",
"Image.XMAS",
"Image.PACMAN",
"Image.TARGET",
"Image.TSHIRT",
"Image.ROLLERSKATE",
"Image.DUCK",
"Image.HOUSE",
"Image.TORTOISE",
"Image.BUTTERFLY",
"Image.STICKFIGURE",
"Image.GHOST",
"Image.SWORD",
"Image.GIRAFFE",
"Image.SKULL",
"Image.UMBRELLA",
"Image.SNAKE" */
]

/**
 * The colors for the LEDs
 * @type Array
 */
Abbozza.DisplayColors = ["#ff0000","#e80000","#d00000","#b80000","#a00000","#880000","#700000","#580000","#400000","#000000"]

/**
 * The color for the display category
 */
ColorMgr.catColor["cat.DISPLAY"] = 90;


/**
 * This block scrolls a text from left to right
 * @type type
 */
Abbozza.DisplayScroll = {
    init : function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DISPLAY"));
        this.setPreviousStatement(true);
        this.setNextStatement(true);            
        this.appendValueInput("TEXT")
            .appendField(_("block.scroll"))
            .setCheck(["STRING","NUMBER","DECIMAL","BOOLEAN"]);
        this.setTooltip('');
    },
    generateCode : function(generator) {
        var code = "";
        var text = generator.valueToCode(this,"TEXT");
        code = "calliope.display.scroll("+text+");";
        return code;
    }    
}

Blockly.Blocks['display_scroll'] = Abbozza.DisplayScroll;

/**
 * This block sets the color of a single pixel
 * 
 * @type type
 */
Abbozza.DisplaySetPixel = {
    init : function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DISPLAY"));
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setInputsInline(true);
        var colorField = new Blockly.FieldColour("#ff0000");
        colorField.setColours(Abbozza.DisplayColors)
                    .setColumns(5)
                    .setText("");        
        this.appendValueInput("COL")
           .appendField(__("block.setPixel",0))
           .setCheck("NUMBER");
        this.appendValueInput("ROW")
            .appendField(__("block.setPixel",1))
            .setCheck("NUMBER");
        this.appendValueInput("COLOR")
            .appendField(__("block.setPixel",2))
            .setCheck("NUMBER");
        this.setTooltip('');
    },
    generateCode : function(generator) {
        var code = "";
        var row = generator.valueToCode(this,"ROW");
        var col = generator.valueToCode(this,"COL");
        var color = generator.valueToCode(this,"COLOR") % 10;
        code = "calliope.display.image.setPixelValue("+col+","+row+","+color+");";
        return code;
    }
}

Blockly.Blocks['display_set_pixel'] = Abbozza.DisplaySetPixel;


/**
 * This block returns the color value of a pixel
 * 
 * @type type
 */
Abbozza.DisplayGetPixel = {
    init : function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DISPLAY"));
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setInputsInline(true);
        this.setOutput(true,"NUMBER");
        this.appendValueInput("COL")
           .appendField(__("block.getPixel",0))
           .setCheck("NUMBER");
        this.appendValueInput("ROW")
            .appendField(__("block.getPixel",1))
            .setCheck("NUMBER");
        this.setTooltip('');
    },
    generateCode : function(generator) {
        var code = "";
        var row = generator.valueToCode(this,"ROW");
        var col = generator.valueToCode(this,"COL");
        code = "calliope.display.image.getPixelValue(" + col + "," + row + ")";
        return code;
    }
}

Blockly.Blocks['display_get_pixel'] = Abbozza.DisplayGetPixel;


/**
 * This block sets all pixel to black
 * 
 * @type type
 */

Abbozza.DisplayClear = {
    init : function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DISPLAY"));
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.appendDummyInput()
           .appendField(_("block.displayClear"));
        this.setTooltip('');
    },
    generateCode : function(generator) {
        var code = "";
        code = "calliope.display.clear();";
        return code;
    }
}

Blockly.Blocks['display_clear'] = Abbozza.DisplayClear;


/**
 * This blockk switches the display on
 * 
 * @type type
 */

Abbozza.DisplayOn = {
    init : function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DISPLAY"));
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.appendDummyInput()
           .appendField(_("block.displayOn"));
        this.setTooltip('');
    },
    generateCode : function(generator) {
        var code = "";
        code = "calliope.display.enable();";
        return code;
    }
}

Blockly.Blocks['display_on'] = Abbozza.DisplayOn;


/**
 * This block switches the display off
 * 
 * @type type
 */

Abbozza.DisplayOff = {
    init : function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DISPLAY"));
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.appendDummyInput()
           .appendField(_("block.displayOff"));
        this.setTooltip('');
    },
    generateCode : function(generator) {
        var code = "";
        code = "calliope.display.disable();";
        return code;
    }
}

Blockly.Blocks['display_off'] = Abbozza.DisplayOff;


/**
 * This block provides a dropdown menu for a display coordinate
 * 
 * @type type
 */
Abbozza.DisplayCoord = {
    init : function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DISPLAY"));
        this.setPreviousStatement(false);
        this.setNextStatement(false);            
        this.setOutput(true,"NUMBER");
        this.appendDummyInput()
                .appendField(new Blockly.FieldDropdown([["0","0"],["1","1"],["2","2"],["3","3"],["4","4"]]),"VALUE");
        this.setTooltip('');        
    },
    generateCode : function(generator) {
        var code = this.getFieldValue("VALUE");
        return code;
    }
}

Blockly.Blocks['display_coord'] = Abbozza.DisplayCoord;


/**
 * This block provides a color picker for the leds
 * 
 * @type type
 */
Abbozza.DisplayColor = {
    init : function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DISPLAY"));
        this.setPreviousStatement(false);
        this.setNextStatement(false);            
        this.setOutput(true,"NUMBER");
        var colorField = new Blockly.FieldColour("#ff0000");
        colorField.setColours(Abbozza.DisplayColors)
                    .setColumns(5)
                    .setText("");        
        this.appendDummyInput()
            .appendField(colorField,"COLOR");        
        this.setTooltip('');        
    },
    generateCode : function(generator) {
        var color = this.getFieldValue("COLOR");
        switch (color) {
            case "#ff0000": return 9;
            case "#e80000": return 8;
            case "#d00000": return 7;
            case "#b80000": return 6;
            case "#a00000": return 5;
            case "#880000": return 4;
            case "#700000": return 3;
            case "#580000": return 2;
            case "#400000": return 1;
            case "#000000": return 0;
        }        
        return 0;
    }
}

Blockly.Blocks['display_color'] = Abbozza.DisplayColor;


/**
 * This block displays one of the predefined images
 * 
 * @type type
 */

Abbozza.DisplayShowInternalImage = {
    init: function() {
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DISPLAY"));
        this.setPreviousStatement(true);
        this.setNextStatement(true);            
        this.setOutput(false);
        this.appendDummyInput()
                .appendField(_("block.displayImage"))
                .appendField(new Blockly.FieldDropdown(this.getMenu),"IMAGE");
        this.setTooltip('');
    },
    getMenu : function() {
        var menu = [];
        for (var idx = 0; idx < Abbozza.Images.length; idx++) {
            menu.push([_(Abbozza.Images[idx]),Abbozza.Images[idx].replace(".","_")]);
        }
        return menu;
    },
    generateCode : function(generator) {
        var code = "calliope.display.print(" + this.getFieldValue("IMAGE") +",0);";
        return code;
    }
}

Blockly.Blocks['display_show_internal_image'] = Abbozza.DisplayShowInternalImage;


Abbozza.DisplayImage = {
    init : function() {
        this.colors = ["#ff0000","#e80000","#d00000","#b80000","#a00000","#880000","#700000","#580000","#400000","#000000","#999999","#999999"];
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DISPLAY"));
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.appendDummyInput().appendField(_("block.displayMatrix"));
        for (var row = 0; row < 5; row++) {
            var input = this.appendDummyInput();
            for (var col = 0; col < 5; col++) {
                var field = new Blockly.FieldColour("#000000");
                field.setColours(this.colors)
                     .setColumns(4)
                     .setText("");
                input.appendField(field,"F"+col+row)
                     .setAlign(Blockly.ALIGN_CENTRE);
            }
        }
        this.setOutput(false);
        this.setTooltip('');
    },
    
    generateCode: function(generator) {
        var row;
        var col;
        var code = "";
        var image = "";
        var unchanged = false;
        // check if an unchanged led occurs
        for (row=0; row <5; row++) {
            for (col=0; col < 5; col++) {
                var field = "F"+col+row;
                var value = this.getFieldValue(field);
                switch (value) {
                    case "#000000": image = "0"; break;
                    case "#400000": image = "30"; break;
                    case "#580000": image = "60"; break;
                    case "#700000": image = "90"; break;
                    case "#880000": image = "120"; break;
                    case "#a00000": image = "150"; break;
                    case "#b80000": image = "180"; break;
                    case "#d00000": image = "210"; break;
                    case "#e80000": image = "230"; break;
                    case "#ff0000": image = "255"; break;
                    default:
                           image = "";
                }
                if (image != "") {
                    code = code + "calliope.display.image.setPixelValue(" + col + "," + row + "," + image + ");\n";
                }
            }
        }
        return code;
    }
}

Blockly.Blocks['display_image'] = Abbozza.DisplayImage;

/**
 * This block shows a given image
 * 
 * @type type
 */
Abbozza.DisplayShowImage = {
    init : function() {
        this.colors = ["#ff0000","#e80000","#d00000","#b80000","#a00000","#880000","#700000","#580000","#400000","#000000","#999999","#999999"];
        this.setHelpUrl(Abbozza.HELP_URL);
        this.setColour(ColorMgr.getCatColor("cat.DISPLAY"));
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.appendValueInput("IMAGE")
            .appendField(_("block.displayMatrix"))
            .setCheck(["STRING","IMAGE"]);
        this.setTooltip('');
    },
    
    generateCode: function(generator) {
        var code ="";
        var image = generator.valueToCode(this,"IMAGE");
        code = ""
        code = "calliope.display.show(Image(\""+image+"\"))";
        return code;
    }
}

Blockly.Blocks['display_show_image'] = Abbozza.DisplayShowImage;

