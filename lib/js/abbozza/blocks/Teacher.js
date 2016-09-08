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
 * @fileoverview Blocks for teachers
 * 
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */

/**
 * A block containing a text for instructions or hints
 */
Abbozza.TeacherText = {
  init: function() {
    this.setHelpUrl(Abbozza.HELP_URL);
    this.setColour(ColorMgr.getCatColor("cat.TEACHER"));
    this.appendDummyInput()
            .appendField(new Blockly.FieldTextInput(_("gui.task")), "TEXT");
    this.setOutput(false);
    this.setTooltip('');
    this.setCommentText("");
  },
  
  generateCode : function(generator) {
  	return "";
  }
  
};

Blockly.Blocks['teacher_text'] = Abbozza.TeacherText;

