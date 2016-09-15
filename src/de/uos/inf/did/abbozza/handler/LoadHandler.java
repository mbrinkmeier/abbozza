/**
 * @license abbozza!
 *
 * Copyright 2015 Michael Brinkmeier ( michael.brinkmeier@uni-osnabrueck.de )
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
/**
 * @fileoverview ... @author michael.brinkmeier@uni-osnabrueck.de (Michael
 * Brinkmeier)
 */
package de.uos.inf.did.abbozza.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.uos.inf.did.abbozza.Abbozza;
import de.uos.inf.did.abbozza.AbbozzaLogger;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author mbrinkmeier
 */
public class LoadHandler implements HttpHandler {

    private Abbozza _abbozza;

    public LoadHandler(Abbozza instance) {
        this._abbozza = instance;
    }

    @Override
    public void handle(HttpExchange exchg) throws IOException {
        try {
            String sketch = loadSketch();
            _abbozza.sendResponse(exchg, 200, "text/xml", sketch);
        } catch (IOException ioe) {
            _abbozza.sendResponse(exchg, 404, "", "");
        }
    }

    public String loadSketch() throws IOException {
        String result = "";
        File lastSketchFile = _abbozza.getLastSketchFile();
        BufferedReader reader;
        String path = ((lastSketchFile != null) ? lastSketchFile.getAbsolutePath() : _abbozza.getSketchbookPath());
        JFileChooser chooser = new JFileChooser(path) {
            protected JDialog createDialog(Component parent)
                    throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                // config here as needed - just to see a difference
                dialog.setLocationByPlatform(true);
                // might help - can't know because I can't reproduce the problem
                dialog.setAlwaysOnTop(true);
                return dialog;
            }

        };
        chooser.setFileFilter(new FileNameExtensionFilter("abbozza! (*.abz)", "abz"));
        chooser.setSelectedFile(lastSketchFile);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            reader = new BufferedReader(new FileReader(file));
            while (reader.ready()) {
                result = result + reader.readLine() + '\n';
            }
            reader.close();
            lastSketchFile = file;
        } else {
            throw new IOException();
        }
        _abbozza.getEditor().setState(JFrame.ICONIFIED);
        _abbozza.getEditor().setExtendedState(JFrame.ICONIFIED);
        return result;
    }

}
