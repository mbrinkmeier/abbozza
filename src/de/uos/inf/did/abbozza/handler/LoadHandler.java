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
import de.uos.inf.did.abbozza.AbbozzaLogger;
import de.uos.inf.did.abbozza.arduino.Abbozza;
import de.uos.inf.did.abbozza.AbbozzaServer;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author mbrinkmeier
 */
public class LoadHandler extends AbstractHandler {

    public LoadHandler(AbbozzaServer abbozza) {
        super(abbozza);
    }

    @Override
    public void handle(HttpExchange exchg) throws IOException {
        try {
            String query = exchg.getRequestURI().getQuery();
            if ( query == null ) {
                String sketch = loadSketch();
                this.sendResponse(exchg, 200, "text/xml", sketch);
            } else {
                AbbozzaLogger.out("loadHandler: query " + query, AbbozzaLogger.DEBUG);
                this.sendResponse(exchg, 200, "text/xml", "");                
            }
        } catch (IOException ioe) {
            this.sendResponse(exchg, 404, "", "");
        }
    }

    public String loadSketch() throws IOException {
        String result = "";
        File lastSketchFile = _abbozzaServer.getLastSketchFile();
        BufferedReader reader;
        String path = ((lastSketchFile != null) ? lastSketchFile.getAbsolutePath() : _abbozzaServer.getSketchbookPath());
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
        
        // Prepare accessory-panel
        LoadHandlerPanel panel = new LoadHandlerPanel(chooser);
        chooser.setAccessory(panel);
        chooser.addPropertyChangeListener(panel);

        chooser.setFileFilter(new FileNameExtensionFilter("abbozza! (*.abz)", "abz"));
        chooser.setSelectedFile(lastSketchFile);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            reader = new BufferedReader(new FileReader(file));
            while (reader.ready()) {
                result = result + reader.readLine() + '\n';
            }
            reader.close();
            if (panel.applyOptions()) {
                Abbozza.getConfig().apply(panel.getOptions());
            }
            _abbozzaServer.setLastSketchFile(file);
            _abbozzaServer.setLastTaskPath(file.getParentFile().getCanonicalPath());
        } else {
            throw new IOException();
        }
        _abbozzaServer.toolIconify();
        return result;
    }

}
