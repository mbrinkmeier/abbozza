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
import de.uos.inf.did.abbozza.AbbozzaLocale;
import de.uos.inf.did.abbozza.AbbozzaLogger;
import de.uos.inf.did.abbozza.AbbozzaServer;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.System.in;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mbrinkmeier
 */
public class SaveHandler extends AbstractHandler {

    public SaveHandler(AbbozzaServer abbozza) {
        super(abbozza);
    }

    
    public void handle(HttpExchange exchg) throws IOException {
        try {
            saveSketch(exchg.getRequestBody());
            this.sendResponse(exchg, 200, "text/xml", "saved");
        } catch (IOException ioe) {
            this.sendResponse(exchg, 404, "", "");
        }
    }

    public void saveSketch(InputStream stream) throws IOException {
        // Read the XML-Document
        // Read in into XML-document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();

            // Read DOM from stream
            Document xml = builder.parse(stream);
            Node root = xml.getFirstChild();

            // Find description
            NodeList descriptions = xml.getElementsByTagName("description");
            Element desc;
            if (descriptions.getLength() == 0) {
                desc = xml.createElement("description");
                desc.setTextContent("abbozza! sketch.");
                root.appendChild(desc);
            } else {
                desc = (Element) descriptions.item(0);

            }

            // Find options
            NodeList options = xml.getElementsByTagName("options");
            Element opts;
            if (options.getLength() == 0) {
                opts = xml.createElement("options");
                root.appendChild(opts);
            } else {
                opts = (Element) options.item(0);
            }
            
            // Find system
            NodeList systems = xml.getElementsByTagName("system");
            Element sysXml;
            if (systems.getLength() == 0) {
                sysXml = xml.createElement("system");
                root.appendChild(sysXml);
            } else {
                sysXml = (Element) systems.item(0);
            }
            
            // Generate JFileChooser
            File lastSketchFile = new File(_abbozzaServer.getLastSketchFile().toURI());
            String path = ((lastSketchFile != null) ? lastSketchFile.getAbsolutePath() : _abbozzaServer.getSketchbookPath());
            JFileChooser chooser = new JFileChooser(path) {
                protected JDialog createDialog(Component parent)
                        throws HeadlessException {
                    JDialog dialog = super.createDialog(parent);
                    dialog.setLocationByPlatform(true);
                    dialog.setAlwaysOnTop(true);
                    return dialog;
                }
            };

            // Prepare accessory-panel
            SaveHandlerPanel panel = new SaveHandlerPanel();
            chooser.setAccessory(panel);
            chooser.addPropertyChangeListener(panel);            
            
            panel.setDescription(desc.getTextContent());
            panel.setOptionSelected(opts.getAttribute("apply").equals("yes") ? true : false);
            panel.setUndeletableSelected(opts.getAttribute("protected").equals("yes") ? true : false);

            // Prepare File filters
            chooser.setFileFilter(new FileNameExtensionFilter("abbozza! (*.abz)", "abz", "ABZ"));
            if ( lastSketchFile.isDirectory() ) {
                chooser.setCurrentDirectory(lastSketchFile);            
            } else {
                chooser.setSelectedFile(lastSketchFile);
            }

            // Show FileChooser
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if (!file.getName().endsWith(".abz") && !file.getName().endsWith(".ABZ")) {
                    file = new File(file.getPath() + ".abz");
                }
                FileWriter writer;

                if (!file.equals(lastSketchFile) && file.exists()) {
                    int answer = JOptionPane.showConfirmDialog(null, AbbozzaLocale.entry("msg.file_overwrite", file.getName()), "", JOptionPane.YES_NO_OPTION);
                    if (answer == JOptionPane.NO_OPTION) {
                        return;
                    }
                }

                // Prepare output writer
                writer = new FileWriter(file);

                // 1st step: Update description
                desc.setTextContent(panel.getDescription());
                // 2nd step: Set attribute "apply"
                opts.setAttribute("apply", panel.isOptionSelected() ? "yes" : "no");
                // 3rd step: Set attribute "protected"
                opts.setAttribute("protected", panel.isUndeletableSelected() ? "yes" : "no");
                // 4th step: Set system
                sysXml.setTextContent(this._abbozzaServer.getSystem());

                
                // Write DOM to file via Transformer
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                // Do not add a surrounding xml tag
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");               
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(new DOMSource(xml), new StreamResult(file));

                /*
                 String line;
                 BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                 while (in.ready()) {
                 line = in.readLine();
                 writer.write(line);
                 }
                 */
                writer.close();
                in.close();
                _abbozzaServer.setLastSketchFile(file.toURI().toURL());
            }
        } catch (Exception ex) {
            AbbozzaLogger.out(ex.toString(), AbbozzaLogger.DEBUG);
            ex.printStackTrace(System.err);
        }
        _abbozzaServer.toolIconify();
    }

}
