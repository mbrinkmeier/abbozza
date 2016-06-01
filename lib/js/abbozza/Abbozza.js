/**
 * @license 
 * abbozza!
 * 
 * File: Abbozza.js
 * 
 * The core object of the abbozza! client
 * 
 * Copyright 2015 Michael Brinkmeier
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

var Abbozza = {
    modified: false,
    serverPort: -1,
    Socket: null,
    // --- not needed ?! --- Dragger: new Dragger(),
    Generator: new AbbozzaGenerator(),
    ReservedWords: ReservedWords,
    globalSymbols: null,
    blockMain: null,
    blockDevices: null,
    blockLogo: null,
    blockConf: null,
    logging: true,
    varWin: null,
    srvWin: null,
    msgWin: null,
    statusBox: null,
    devices: new DeviceDB(),
    HELP_URL: "http://didaktik.inf.ous.de",
    FUNC_COLOR: ColorMgr.getCatColor("cat.FUNC"),
    VAR_COLOR: ColorMgr.getCatColor("cat.VAR"),
    LOGIC_COLOR: ColorMgr.getCatColor("cat.LOGIC"),
    MATH_COLOR: ColorMgr.getCatColor("cat.MATH"),
    TEXT_COLOR: ColorMgr.getCatColor("cat.TEXT"),
    COND_COLOR: ColorMgr.getCatColor("cat.COND"),
    LOOP_COLOR: ColorMgr.getCatColor("cat.LOOPS"),
    INOUT_COLOR: ColorMgr.getCatColor("cat.INOUT"),
    DEV_COLOR: ColorMgr.getCatColor("cat.DEVICES"),
    VAR_SYMBOL: 0,
    PAR_SYMBOL: 1,
    FUN_SYMBOL: 2,
    // REQ_TOOL_VER: "0.4.1",
    // VERSION : "0.4.1 (pre-alpha)",
    // board : new Boards(),

    // Features : null,

    infoWin: null,
    board: Board,
    serialRate: 9600
};

// Original mutator icon
// Blockly.Mutator.prototype.png_ = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABEAAAARCAYAAAA7bUf6AAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAANyAAADcgBffIlqAAAAAd0SU1FB98DGhULOIajyb8AAAHMSURBVDjLnZS9SiRBFIXP/CQ9iIHgPoGBTo8vIAaivoKaKJr6DLuxYqKYKIqRgSCMrblmIxqsICgOmAriziIiRXWjYPdnUDvT2+PMsOyBoop7qk71vedWS5KAkrWsGUMjSYjpgSQhNoZGFLEKeGoKGMNttUpULkOhAFL3USiA70MQEBnDDeDJWtaqVaJeB7uNICAKQ1ZkDI1yufOm+XnY2YHl5c6874MxPClJiDulkMvBxYWrw/095POdU0sS4hxALqcWtreloSGpVJLGxtL49bX0+Ci9vUkzM2kcXGFbypUKxHHLBXZ3YW4ONjfh4yN1aGIiPQOQEenrg6MjR+zvZz99Y8PFT09hYCArktdfsFY6PHTr83NlUKu5+eREennJchmR/n5pYcGtJyezG6em3Dw7Kw0OZrlMOr6f1gTg4ACWlmBvz9WoifHxbDpf3Flfd+54njQ9ncYvL6WHB+n9XVpcbHOnW59IUKu5m+p11zftfLHo+qRorZ6Hh/Xt7k5fsLUl1evS1dWfG9swMiJZq9+KIlaD4P/eztkZNgz5LsAzhpvjY6JK5d9e8eioE3h95SdQbDrkhSErxvArjkl6/U/imMQYnsKQH02BT7vbZZfVOiWhAAAAAElFTkSuQmCC';

// Adapted mutator icon
Blockly.Mutator.prototype.png_ = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABEAAAARCAYAAAA7bUf6AAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAANyAAADcgBffIlqAAAAAd0SU1FB98DGhULOIajyb8AAAHMSURBVDjLnZS9SiRBFIXP/CQ9iIHgPoGBTo8vIAaivoKaKJr6DLuxYqKYKIqRgSCMrblmIxqsICgOmAriziIiRXWjYPdnUDvT2+PMsOyBoop7qk71vedWS5KAkrWsGUMjSYjpgSQhNoZGFLEKeGoKGMNttUpULkOhAFL3USiA70MQEBnDDeDJWtaqVaJeB7uNICAKQ1ZkDI1yufOm+XnY2YHl5c6874MxPClJiDulkMvBxYWrw/095POdU0sS4hxALqcWtreloSGpVJLGxtL49bX0+Ci9vUkzM2kcXGFbypUKxHHLBXZ3YW4ONjfh4yN1aGIiPQOQEenrg6MjR+zvZz99Y8PFT09hYCArktdfsFY6PHTr83NlUKu5+eREennJchmR/n5pYcGtJyezG6em3Dw7Kw0OZrlMOr6f1gTg4ACWlmBvz9WoifHxbDpf3Flfd+54njQ9ncYvL6WHB+n9XVpcbHOnW59IUKu5m+p11zftfLHo+qRorZ6Hh/Xt7k5fsLUl1evS1dWfG9swMiJZq9+KIlaD4P/eztkZNgz5LsAzhpvjY6JK5d9e8eioE3h95SdQbDrkhSErxvArjkl6/U/imMQYnsKQH02BT7vbZZfVOiWhAAAAAElFTkSuQmCC';


/*
 * oldDraw = Blockly.BlockSvg.prototype.renderDraw_;
 * Blockly.BlockSvg.prototype.renderDraw_ = function(iconWidth, inputRows) {
 * console.log("Before2"); oldDraw.call(this, iconWidth,inputRows);
 * console.log("After2"); }
 */

/**
 * Initialization of Abbozza
 */
Abbozza.init = function () {

    window.name = "abbozzaWorkspace";

    // Check APIs
    if (window.File && window.FileReader && window.FileList && window.Blob) {
    } else {
        alert('The File APIs are not fully supported in this browser.');
    }
    
    for (var key in Blockly.Blocks) {
        Blockly.Blocks[key].getColour = function() { return ColorMgr.getBlockColour(this); };
    }
     
    // 
    // --- not needed ?! ---
    // Initialize DraggingManager
    // this.Dragger.init();
    // ------

    Board.init();

    Configuration.load();

    Blockly.bindEvent_(document, 'blocklySelectChange', null,
            Abbozza.changeSelection);
    window.Blockly = Blockly;

    Blockly.mainWorkspace.fireChangeEvent = function () {
        Abbozza.modified = true;
    };

    var but = document.getElementById("generate");
    but.setAttribute("title",_("gui.generate_button"));
    but = document.getElementById("upload");
    but.setAttribute("title",_("gui.upload_button"));
    but = document.getElementById("new");
    but.setAttribute("title",_("gui.new_button"));
    but = document.getElementById("load");
    but.setAttribute("title",_("gui.load_button"));
    but = document.getElementById("save");
    but.setAttribute("title",_("gui.save_button"));
    but = document.getElementById("serial");
    but.setAttribute("title",_("gui.serial_button"));
    but = document.getElementById("connect");
    but.setAttribute("title",_("gui.connect_button"));
    but = document.getElementById("config");
    but.setAttribute("title",_("gui.config_button"));
    but = document.getElementById("info");
    but.setAttribute("title",_("gui.info_button"));
    
    // Inject starting Blocks
    this.newSketch();
    
    /*
    this.varWin = new InternalWindow(Blockly.mainWorkspace,
            this.blockMain, _("VARIABLES"), "abbozzaVarWin", 250, 50,
            250, 250);
    this.varWin.setText("Hi!");
    */
   
};

/**
 * The main operations
 */

/**
 * This operation builds a new sketch.
 */
Abbozza.newSketch = function () {
    if (this.modified && !this.askForSave())
        return;

    this.clear();
    // Blockly.Block.obtain(Blockly.mainWorkspace,'base_setup');

    var symbols;

    this.globalSymbols = new SymbolDB();
    Blockly.mainWorkspace.symbols = this.globalSymbols;

    if (Configuration.getParameter("option.devices") == "true") {
        Blockly.Xml.domToWorkspace(Blockly.mainWorkspace, document
                .getElementById('startBlocks'));
    } else {
        Blockly.Xml.domToWorkspace(Blockly.mainWorkspace, document
                .getElementById('startBlocksWoDevices'));
    }

    var blocks = Blockly.mainWorkspace.getTopBlocks();

    // First look for devices

    for (var i = 0; i < blocks.length; i++) {
        if (blocks[i].type == "main") { // devices
            this.blockMain = blocks[i];
            // this.blockMain.setTitle(_("GLOBALVARS"));
            this.blockMain.setPreviousStatement(false);
            this.blockMain.setSymbolDB(this.globalSymbols);
        } else if (blocks[i].type=="devices") {
            this.blockDevices = blocks[i];
        }
    }

    
    // Now look for the rest
    for (i = 0; i < blocks.length; i++) {
        if (blocks[i].type == "func_loop") {
            symbols = new SymbolDB();
            blocks[i].setSymbolDB(symbols);
            this.globalSymbols.addFunction("loop", "VOID", null);
            this.globalSymbols.addChild(symbols, "loop");
        } else if (blocks[i].type == "func_setup") {
            symbols = new SymbolDB();
            blocks[i].setSymbolDB(symbols);
            this.globalSymbols.addFunction("setup", "VOID", null);
            this.globalSymbols.addChild(symbols, "setup");
        }
    }

    var ww = Blockly.mainWorkspace.getWidth();
    /* 
    this.varWin = new InternalWindow(Blockly.mainWorkspace,
            this.blockMain, _("VARIABLES"), "abbozzaVarWin", ww - 250, 50,
            250, 250, Abbozza.test);
    this.varWin.setText("");
    */
   
    this.blockLogo = null;
    this.blockConf = null;

    this.modified = false;
}

Abbozza.showInfo = function () {
    Connection.getText("/abbozza/info");
}

Abbozza.selectBoard = function () {
    Board.load(true);
}

Abbozza.showConfiguration = function () {
    Abbozza.openOverlay(_("msg.open_config"));
    Connection.getText("/abbozza/frame", function (text) {
        Abbozza.closeOverlay();
        Configuration.apply(text);
    }, function (text) {
        Abbozza.closeOverlay();
    });
}

Abbozza.loadSketch = function () {
    if (this.modified && !this.askForSave())
        return;

    var xml = document.createElement("abbozza");
    var sketch = Connection.getXML("/abbozza/load", function (sketch) {
        Abbozza.setBlocks(sketch.firstChild);
        this.modified = false;
    }, function (response) {
        console.log("error: " + response);
    });
}

Abbozza.saveSketch = function () {
    var xml = Abbozza.workspaceToDom(Blockly.mainWorkspace);

    Connection.sendXML("/abbozza/save", Blockly.Xml.domToText(xml), function (
            response) {
        // console.log("success: " + response);
        this.modified = false;
    }, function (response) {
        // console.log("error: " + response);
    });
}

Abbozza.generateSketch = function () {
    Abbozza.openOverlay(_("msg.generate_sketch"));
    var code = this.Generator.workspaceToCode();
    if (!ErrorMgr.hasErrors()) {
        Abbozza.appendOverlayText(_("msg.code_generated"));
        var content = document.createTextNode(code);
        Abbozza.appendOverlayText(_("msg.compiling"));
        Connection.sendText("/abbozza/check", code,
                function (response) {
                    Abbozza.appendOverlayText(_("msg.done_compiling"));
                    Abbozza.closeOverlay();
                },
                function (response) {
                    Abbozza.appendOverlayText(_("msg.error_compiling"));
                    Abbozza.closeOverlay();
                }
        );  
    } else {
        Abbozza.closeOverlay();
    }
}

Abbozza.uploadSketch = function () {
    Abbozza.openOverlay(_("msg.generate_sketch"));
    var code = this.Generator.workspaceToCode();
    if (!ErrorMgr.hasErrors()) {
        Abbozza.appendOverlayText(_("msg.code_generated"));
        Abbozza.appendOverlayText(_("msg.compiling"));
        var content = document.createTextNode(code);
        // Check for board
        var boardFound = false;
        Connection.getTextSynced("/abbozza/board",
            function(response) {
                boardFound = true;
            },
            function (response) {
                boardFound = false;
                Abbozza.appendOverlayText(_("msg.no_board"))
                Abbozza.overlayWaitForClose();
            });
         if ( !boardFound ) return;
        Connection.sendText("/abbozza/upload", code,
                function (response) {
                    Connection.getText("/abbozza/monitor_resume");
                    Abbozza.closeOverlay();
                },
                function (response) {
                    Abbozza.appendOverlayText(_("msg.err_upload"));
                    Abbozza.overlayWaitForClose();
                    Connection.getText("/abbozza/monitor_resume");
                });
    } else {
        Abbozza.closeOverlay();
    }
}

Abbozza.serialMonitor = function () {
    Connection.getText("/abbozza/monitor",
            function (text) {
            },
            function (text) {
                // alert(_("msg.no_board"));
            });
}


/**
 * This function returns the localized string.
 * 
 * @param id The id of the String.
 * @param args An array of arguments inserted into the string (e.g. /#1 for args[1] )
 * 
 * @returns The respective localized string
 */
_ = function (id, args) {
    var el = Configuration.xmlLocale.getElementById(id);
    if (el) {
        if (args == null) {
            return el.textContent;
        } else {
            var text = el.textContent;
            var pattern;
            for (var i = 0; i < args.length; i++) {
                pattern = "/#" + i + "/g";
                text.replace(pattern, args[i]);
            }
        }
    }
    return "<" + id + ">";
}

__ = function (id, num) {
    var el = Configuration.xmlLocale.getElementById(id);
    if (el) {
        var text = el.textContent;
        var ar = text.split("#");
        if (num < ar.length) {
            return text.split("#")[num].trim();
        } else {
            return "";
        }
    }
    return "<" + id + ">";
}


Abbozza.splitStr = function (text, num) {
    return text.split("#")[num]
}

Abbozza.printf = function (text, args) {
    var result = text;
    if (args) {
        for (var i = 0; i < args.length; i++) {
            result.replace(/\#/, args[i]);
        }
    }
    return result;
}

/**
 * Misc operations
 */

Abbozza.askForSave = function () {
    var result = confirm(_("msg.drop_sketch"));
    return result;
}

Abbozza.changeSelection = function (event) {
    if (Blockly.selected == null)
        return;
    var block = Blockly.selected.getRootBlock();
    if (block.symbols && Abbozza.varWin) {
        Abbozza.varWin.setText(block.symbols.toString());
    }
};

Abbozza.createEditor_ = function () {
    var foreignObject_ = Blockly.createSvgElement('foreignObject', {
        'x': Blockly.Bubble.BORDER_WIDTH,
        'y': Blockly.Bubble.BORDER_WIDTH
    }, null);
    var body = document.createElementNS(Blockly.HTML_NS, 'body');
    body.setAttribute('xmlns', Blockly.HTML_NS);
    body.className = 'blocklyMinimalBody';
    var textarea_ = document.createElementNS(Blockly.HTML_NS, 'textarea');
    textarea_.className = 'blocklyCommentTextarea';
    textarea_.setAttribute('dir', Blockly.RTL ? 'RTL' : 'LTR');
    body.appendChild(textarea_);
    foreignObject_.appendChild(body);
    Blockly.bindEvent_(textarea_, 'mouseup', this, null);
    return foreignObject_;
}

Abbozza.clear = function () {
    ErrorMgr.clearErrors();
    if (this.varWin)
        this.varWin.dispose();
    Blockly.mainWorkspace.clear();
}

Abbozza.setBlocks = function (xml) {
    this.clear();

    // var xml = Blockly.Xml.textToDom(xml_text);

    Blockly.Xml.domToWorkspace(Blockly.mainWorkspace, xml);
    
    // Run through topBlocks and get everything right
    var topBlocks = Blockly.mainWorkspace.getTopBlocks();

    for (var i = 0; i < topBlocks.length; i++) {
        if (topBlocks[i].type == "main") {
            this.blockMain = topBlocks[i];
            this.globalSymbols = topBlocks[i].symbols;
            Blockly.mainWorkspace.symbols = this.globalSymbols;
        } else if (topBlocks[i].type == "devices") {
            this.blockDevices = topBlocks[i];
        }
    }

    for (i = 0; i < topBlocks.length; i++) {
        if (topBlocks[i].type == "func_loop") {
            this.globalSymbols.addFunction("loop", "VOID", null);
            this.globalSymbols.addChild(topBlocks[i].symbols, "loop");
        } else if (topBlocks[i].type == "func_setup") {
            this.globalSymbols.addFunction("setup", "VOID", null);
            this.globalSymbols.addChild(topBlocks[i].symbols, "setup");
        } else if (topBlocks[i].type == "func_decl") {
            var name = topBlocks[i].name;
            this.globalSymbols.addChild(topBlocks[i].symbols, name);
        }

    }

    var ww = Blockly.mainWorkspace.getWidth();
    /*
    this.varWin = new InternalWindow(Blockly.mainWorkspace,
            this.blockMain, _("VARIABLES"), "abbozzaVarWin", ww - 250, 50,
            250, 250, Abbozza.test);
    this.varWin.setText("");
    */
   
    this.blockLogo = null;
    this.blockConf = null;

    var blocks = Blockly.mainWorkspace.getAllBlocks();
    for (var i = 0; i < blocks.length; i++) {
        if (blocks[i].onload) {
            blocks[i].onload();
        }
    }
    Blockly.mainWorkspace.render();
}

Abbozza.workspaceToDom = function (workspace) {
    var width; // Not used in LTR.
    if (Blockly.RTL) {
        width = workspace.getWidth();
    }
    var xml = goog.dom.createDom('xml');
    var blocks = workspace.getTopBlocks(true);
    for (var i = 0, block; block = blocks[i]; i++) {
        var element = Blockly.Xml.blockToDom_(block);
        var xy = block.getRelativeToSurfaceXY();
        element.setAttribute('x', Blockly.RTL ? width - xy.x : xy.x);
        element.setAttribute('y', xy.y);
        xml.appendChild(element);
    }
    return xml;
}

Abbozza.showDeprecatedMsg = function (text) {
    var err = new Error(text + " deprecated");
    console.log(err.stack);
}


Abbozza.hideError = function () {
    e = new Error("Abbozza.hideError deprecated");
    console.log(e.stack);
    if (this.error != null) {
        this.error.setVisible(false);
        this.error.dispose();
    }
}

Abbozza.addError = function (block, text) {
    e = new Error("Abbozza.addError deprecated");
    console.log(e.stack);
    ErrorMgr.addError(block, text);
}

Abbozza.hasError = function () {
    e = new Error("Abbozza.hasError deprecated");
    console.log(e.stack);
    return ErrorMgr.hasErrors();
}

Abbozza.delError = function (block) {
    e = new Error("Abbozza.delError deprecated");
    console.log(e.stack);
    return ErrorMgr.clearBlock(block);
}

Abbozza.showError = function (block, text) {
    e = new Error("Abbozza.showError deprecated");
    console.log(e.stack);
    this.hideError();
    Abbozza.Generator.ERROR = true;
    Abbozza.Generator.ERROR_TEXT = text;
    this.error = new AbbozzaError(block, text);
}

Abbozza.serverMessage = function (text) {
    if (this.srvWin == null) {
        var vw = Blockly.mainWorkspace.getMetrics().viewWidth;
        var vh = Blockly.mainWorkspace.getMetrics().viewHeight;
        // var wh = Blockly.mainWorkspace.getHeight();
        this.srvWin = new InternalWindow(Blockly.mainWorkspace,
                this.blockMain, _("SERVERMSG"), "abbozzaSrvWin", 100,
                vh - 30, 500, 230, function (event) {
                    if (Abbozza.srvWin != null) {
                        Abbozza.srvWin.dispose();
                        Abbozza.srvWin = null;
                    }
                });
        this.srvWin.setText("");
    }
    this.srvWin.appendText(text);
}

Abbozza.showInfoMessage = function (text) {
    this.showInfoMessage(text,true);
}

Abbozza.showInfoMessage = function (text,open) {
    if (this.infoWin == null && open==true) {
        var vw = Blockly.mainWorkspace.getMetrics().viewWidth;
        var vh = Blockly.mainWorkspace.getMetrics().viewHeight;
        // var wh = Blockly.mainWorkspace.getHeight();
        this.infoWin = new InternalWindow(Blockly.mainWorkspace,
                this.blockMain, _("INFOMSG"), "abbozzaMsgWin", 0,
                vh - 30, 500, 130, function (event) {
                    if (Abbozza.infoWin != null) {
                        Abbozza.infoWin.dispose();
                        Abbozza.infoWin = null;
                    }
                });
        this.infoWin.setText("");

    }
    if (this.infoWin) this.infoWin.appendText(text + "\n");
}


Abbozza.addDisposeHandler = function (block) {
    if (!block.onDispose)
        return;
    block.oldDispose_ = block.dispose;
    block.dispose = function (healStack, animate, opt_dontRemoveFromWorkspace) {
        block.onDispose();
        block.oldDispose_(healStack, animate, opt_dontRemoveFromWorkspace);
    }
}

Abbozza.openOverlay = function (text) {
    overlay = document.getElementById("overlay");
    overlay_content = document.getElementById("overlay_content");
    overlay.style.display = "block";
    overlay.style.zIndex = 42;
    overlay_content.innerHTML = "<span>" + text + "</span>";
    this.overlay_closeable = false;
}

Abbozza.appendOverlayText = function (text) {
    overlay_content.innerHTML = overlay_content.innerHTML + "<br/><span>" + text + "</span>";
    
}

Abbozza.closeOverlay = function () {
    overlay = document.getElementById("overlay");
    overlay.style.display = "none";
    overlay.style.zIndex = -1;    
    this.overlay_closeable = false;
    overlay_content.style.backgroundColor = "#f0f0f0";
}

Abbozza.overlayWaitForClose = function() {
    this.overlay_closeable = true;
    Abbozza.appendOverlayText("");
    Abbozza.appendOverlayText(_("msg.click_to_continue"));
    overlay_content.style.backgroundColor = "#ffd0d0";
}

Abbozza.overlayClicked = function(overlay,event) {
    if ( this.overlay_closeable) Abbozza.closeOverlay();
}

Abbozza.overlayKeyPressed = function() {
    console.log("press");
    console.log(event);
}