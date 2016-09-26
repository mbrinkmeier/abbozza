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
    HELP_URL: "http://inf-didaktik.rz.uni-osnabrueck.de/abbozza",
    systemPrefix : "arduino",
    
    sketchDescription: "",
    sketchApplyOptions: false,
    sketchProtected: false,
    
    // taskTitle: "Ein toller Titel",
    // taskContent: "",

    VAR_SYMBOL: 0,
    PAR_SYMBOL: 1,
    FUN_SYMBOL: 2,

    infoWin: null,
    board: Board,
    serialRate: 9600
};

// Original mutator icon
// Blockly.Mutator.prototype.png_ = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABEAAAARCAYAAAA7bUf6AAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAANyAAADcgBffIlqAAAAAd0SU1FB98DGhULOIajyb8AAAHMSURBVDjLnZS9SiRBFIXP/CQ9iIHgPoGBTo8vIAaivoKaKJr6DLuxYqKYKIqRgSCMrblmIxqsICgOmAriziIiRXWjYPdnUDvT2+PMsOyBoop7qk71vedWS5KAkrWsGUMjSYjpgSQhNoZGFLEKeGoKGMNttUpULkOhAFL3USiA70MQEBnDDeDJWtaqVaJeB7uNICAKQ1ZkDI1yufOm+XnY2YHl5c6874MxPClJiDulkMvBxYWrw/095POdU0sS4hxALqcWtreloSGpVJLGxtL49bX0+Ci9vUkzM2kcXGFbypUKxHHLBXZ3YW4ONjfh4yN1aGIiPQOQEenrg6MjR+zvZz99Y8PFT09hYCArktdfsFY6PHTr83NlUKu5+eREennJchmR/n5pYcGtJyezG6em3Dw7Kw0OZrlMOr6f1gTg4ACWlmBvz9WoifHxbDpf3Flfd+54njQ9ncYvL6WHB+n9XVpcbHOnW59IUKu5m+p11zftfLHo+qRorZ6Hh/Xt7k5fsLUl1evS1dWfG9swMiJZq9+KIlaD4P/eztkZNgz5LsAzhpvjY6JK5d9e8eioE3h95SdQbDrkhSErxvArjkl6/U/imMQYnsKQH02BT7vbZZfVOiWhAAAAAElFTkSuQmCC';

// Adapted mutator icon
Blockly.Mutator.prototype.png_ = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABEAAAARCAYAAAA7bUf6AAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAANyAAADcgBffIlqAAAAAd0SU1FB98DGhULOIajyb8AAAHMSURBVDjLnZS9SiRBFIXP/CQ9iIHgPoGBTo8vIAaivoKaKJr6DLuxYqKYKIqRgSCMrblmIxqsICgOmAriziIiRXWjYPdnUDvT2+PMsOyBoop7qk71vedWS5KAkrWsGUMjSYjpgSQhNoZGFLEKeGoKGMNttUpULkOhAFL3USiA70MQEBnDDeDJWtaqVaJeB7uNICAKQ1ZkDI1yufOm+XnY2YHl5c6874MxPClJiDulkMvBxYWrw/095POdU0sS4hxALqcWtreloSGpVJLGxtL49bX0+Ci9vUkzM2kcXGFbypUKxHHLBXZ3YW4ONjfh4yN1aGIiPQOQEenrg6MjR+zvZz99Y8PFT09hYCArktdfsFY6PHTr83NlUKu5+eREennJchmR/n5pYcGtJyezG6em3Dw7Kw0OZrlMOr6f1gTg4ACWlmBvz9WoifHxbDpf3Flfd+54njQ9ncYvL6WHB+n9XVpcbHOnW59IUKu5m+p11zftfLHo+qRorZ6Hh/Xt7k5fsLUl1evS1dWfG9swMiJZq9+KIlaD4P/eztkZNgz5LsAzhpvjY6JK5d9e8eioE3h95SdQbDrkhSErxvArjkl6/U/imMQYnsKQH02BT7vbZZfVOiWhAAAAAElFTkSuQmCC';


/**
 * Initialization of Abbozza
 */
Abbozza.init = function (systemPrefix) {

    this.systemPrefix = systemPrefix;
    
    window.name = "abbozzaWorkspace";

    // Check APIs
    if (window.File && window.FileReader && window.FileList && window.Blob) {
    } else {
        alert('The File APIs are not fully supported in this browser.');
    }

    for (var key in Blockly.Blocks) {
        Blockly.Blocks[key].getColour = function () {
            return ColorMgr.getBlockColour(this);
        };
    }

    // 
    // --- not needed ?! ---
    // Initialize DraggingManager
    // this.Dragger.init();
    // ------

    Board.init(this.systemPrefix);

    Configuration.load();

    TaskWindow.init();
    
    Blockly.bindEvent_(document, 'blocklySelectChange', null,
            Abbozza.changeSelection);
    window.Blockly = Blockly;

    Blockly.mainWorkspace.fireChangeEvent = function () {
        Abbozza.modified = true;
    };
    
    var but = document.getElementById("generate");
    but.setAttribute("title", _("gui.generate_button"));
    but = document.getElementById("upload");
    but.setAttribute("title", _("gui.upload_button"));
    but = document.getElementById("new");
    but.setAttribute("title", _("gui.new_button"));
    but = document.getElementById("load");
    but.setAttribute("title", _("gui.load_button"));
    but = document.getElementById("save");
    but.setAttribute("title", _("gui.save_button"));
    but = document.getElementById("serial");
    but.setAttribute("title", _("gui.serial_button"));
    but = document.getElementById("connect");
    but.setAttribute("title", _("gui.connect_button"));
    but = document.getElementById("config");
    but.setAttribute("title", _("gui.config_button"));
    but = document.getElementById("info");
    but.setAttribute("title", _("gui.info_button"));

    // Check request for query
    if (window.location.search != "") {
        var sketchPath = window.location.search.substring(1);
        Connection.getXML(sketchPath,
                function (response) {
                    Abbozza.setSketch(response);
                },
                function (response) {
                    var args = [];
                    args.push(sketchPath);
                    args.push("test");
                    Abbozza.openOverlay(_("msg.invalid_sketch", args))
                    Abbozza.overlayWaitForClose();
                    Abbozza.newSketch();
                }
        );
    } else {
        // Inject starting Blocks
        this.newSketch();
    }

    /*
     this.varWin = new InternalWindow(Blockly.mainWorkspace,
     this.blockMain, _("VARIABLES"), "abbozzaVarWin", 250, 50,
     250, 250,true);
     this.varWin.setText("<p><H1>Hi!</H1> Alles klar?</p><img src='img/close.png'>");
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

    TaskWindow.setContent("",true);
    TaskWindow.setSize(600,400);
    // Abbozza.taskContent = "";
    Abbozza.sketchDescription = "";
    Abbozza.sketchApplyOptions = false;
    Abbozza.sketchProtected =false;

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
        } else if (blocks[i].type == "devices") {
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

Abbozza.setSketch = function (sketch) {
    TaskWindow.hide();
    this.modified = false;
    // Retrieve description from sketch
    var desc = sketch.getElementsByTagName("description");
    if (desc[0]) {
        Abbozza.sketchDescription = desc[0].textContent;
    } else {
        Abbozza.sketchDescription = _("msg.sketch_description");
    }

    // Fetch Sketch options
    var opts = sketch.getElementsByTagName("options");
    if (opts[0]) {
        Abbozza.sketchApplyOptions = (opts[0].getAttribute("apply") == "yes");
        Abbozza.sketchProtected = (opts[0].getAttribute("protected") == "yes");

        if (Abbozza.sketchApplyOptions) {
            var options = opts[0].textContent;
            Configuration.apply(options);
        }
    } else {
        Abbozza.sketchApplyOptions = false;
        Abbozza.sketchProtected = false;
    }
    
    var task = sketch.getElementsByTagName("task");
    if (task[0]) {
        TaskWindow.setContent(task[0].textContent,true);
        TaskWindow.setSize(task[0].getAttribute("width"),task[0].getAttribute("height"));
        // Abbozza.taskContent = task[0].textContent;
    } else {
        TaskWindow.setContent("",true);
        TaskWindow.setSize(600,400);
        // Abbozza.taskContent="";
    }
    
    Abbozza.setBlocks(sketch.firstChild);

    if (Abbozza.sketchProtected) {
        var blocks = Blockly.mainWorkspace.getAllBlocks();
        for (var i = 0; i < blocks.length; i++) {
            blocks[i].setDeletable(false);
        }
    }
    
    // Check if TaskWindow contains something
    if (TaskWindow.getContent() != "") {
        TaskWindow.show();
    }
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
    var sketch = Connection.getXML("/abbozza/load",
            function (sketch) {
                Abbozza.setSketch(sketch);
            },
            function (response) {
                console.log("error: " + response);
            }
    );
}

Abbozza.saveSketch = function () {
    var xml = Abbozza.workspaceToDom(Blockly.mainWorkspace);

    var desc = document.createElement("description");
    desc.textContent = Abbozza.sketchDescription;
    xml.appendChild(desc);

    var opts = document.createElement("options");
    xml.appendChild(opts);
    opts.setAttribute("apply", Abbozza.sketchApplyOptions ? "yes" : "no");
    opts.setAttribute("protected", Abbozza.sketchProtected ? "yes" : "no");

    var task = document.createElement("task");
    task.textContent = TaskWindow.getContent(); // Abbozza.taskContent;
    task.setAttribute("width",TaskWindow.getWidth());
    task.setAttribute("height",TaskWindow.getHeight());

    xml.appendChild(task);

    opts.textContent = Configuration.getOptionString();

    // console.log(xml);    

    Connection.sendXML("/abbozza/save", Blockly.Xml.domToText(xml),
            function (response) {
                this.modified = false;
            },
            function (response) {
                // console.log(": " + response);
            }
    );
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
        var content = document.createTextNode(code);
        // Check for board
        var boardFound = false;
        Connection.getTextSynced("/abbozza/board",
                function (response) {
                    boardFound = true;
                },
                function (response) {
                    boardFound = false;
                    Abbozza.appendOverlayText(_("msg.no_board"));
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
                    // Abbozza.overlayWaitForClose();
                });
        if (!boardFound)
            return;

        Abbozza.appendOverlayText(_("msg.compiling"));
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
                pattern = "";
                text = text.replace(/#/, args[i]);
            }
            return text;
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
    TaskWindow.hide();
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
    this.showInfoMessage(text, true);
}

Abbozza.showInfoMessage = function (text, open) {
    if (this.infoWin == null && open == true) {
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
    if (this.infoWin)
        this.infoWin.appendText(text + "\n");
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


Abbozza.openTaskWindow = function() {
    TaskWindow.show();
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

Abbozza.overlayWaitForClose = function () {
    this.overlay_closeable = true;
    Abbozza.appendOverlayText("");
    Abbozza.appendOverlayText(_("msg.click_to_continue"));
    overlay_content.style.backgroundColor = "#ffd0d0";
}

Abbozza.overlayClicked = function (overlay, event) {
    if (this.overlay_closeable)
        Abbozza.closeOverlay();
}

Abbozza.overlayKeyPressed = function () {
    console.log("press");
    console.log(event);
}

Blockly.BlockSvg.prototype.customContextMenu = function (menuOptions) {
    var block = this;
    if (this.isDeletable()) {
        var protectOption = {
            text: _("gui.protect_block"),
            enabled: true,
            callback: function () {
                block.setDeletable(false);
            }
        }
        menuOptions.push(protectOption);
    } else {
        var unprotectOption = {
            text: _("gui.unprotect_block"),
            enabled: true,
            callback: function () {
                block.setDeletable(true);
            }
        }
        menuOptions.push(unprotectOption);
    }
    var idOption = {
        text: _("gui.block_id") + (Abbozza.getBlockId(this)!="" ? " (" + Abbozza.getBlockId(this) + ")" : ""),
        enabled: true,
        callback: function () {
            Abbozza.setBlockId(block);
        }
    }
    menuOptions.push(idOption);
}

Abbozza.getBlockId = function(block) {
    return block.data;
}

Abbozza.setBlockId = function(block) {
    var id = Abbozza.getBlockId(block);
    id = prompt(_("gui.ask_for_id"),id);
    if ( id != null ) {
        block.data = id;
    }
}

Abbozza.getBlocksById = function (id) {
    var id_;
    var result = [];
    var blocks = Blockly.mainWorkspace.getAllBlocks();
    for (var i = 0; i < blocks.length; i++) {
        id_ = Abbozza.getBlockId(blocks[i]);
        if (id_ == id) {
            result.push(blocks[i]);
        }
    }
    return result;
}