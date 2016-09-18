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
package de.uos.inf.did.abbozza.handler;

import com.sun.net.httpserver.HttpExchange;
import de.uos.inf.did.abbozza.Abbozza;
import de.uos.inf.did.abbozza.AbbozzaConfig;
import de.uos.inf.did.abbozza.AbbozzaConfigDialog;
import de.uos.inf.did.abbozza.AbbozzaLocale;
import de.uos.inf.did.abbozza.AbbozzaLogger;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JFrame;
import processing.app.Editor;

/**
 *
 * @author michael
 */
public class ConfigDialogHandler extends AbstractHandler {

    public ConfigDialogHandler(Abbozza abbozza) {
        super(abbozza);
    }

    @Override
    public void handle(HttpExchange exchg) throws IOException {
        Editor editor = _abbozza.getEditor();
        AbbozzaConfig config = _abbozza.getConfiguration();
        Properties props = config.get();
        AbbozzaConfigDialog dialog = new AbbozzaConfigDialog(props, null, false, true);
        dialog.setAlwaysOnTop(true);
        dialog.setModal(true);
        dialog.toFront();
        dialog.setVisible(true);
        editor.setState(JFrame.ICONIFIED);
        editor.setExtendedState(JFrame.ICONIFIED);
        if (dialog.getState() == 0) {
            config.set(dialog.getConfiguration());
            AbbozzaLocale.setLocale(config.getLocale());
            AbbozzaLogger.out("closed with " + config.getLocale());
            config.write();
            sendResponse(exchg, 200, "text/plain", config.get().toString());
        } else {
            sendResponse(exchg, 440, "text/plain", "");
        }
    }

}
