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

import de.uos.inf.did.abbozza.arduino.Abbozza;
import de.uos.inf.did.abbozza.AbbozzaLogger;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.DoubleStream.builder;
import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mbrinkmeier
 */
public class LoadHandlerPanel extends javax.swing.JPanel implements PropertyChangeListener {

    private JFileChooser chooser;
    private String options = "{}";
    private boolean applyOptions = false;
    
    /**
     * Creates new form LoadHandlerPanel
     */
    public LoadHandlerPanel(JFileChooser chooser) {
        this.chooser = chooser;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        description = new javax.swing.JTextArea();

        jScrollPane1.setEnabled(false);

        description.setEditable(false);
        description.setColumns(20);
        description.setRows(5);
        description.setAutoscrolls(false);
        jScrollPane1.setViewportView(description);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea description;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      String fileName = evt.getPropertyName();
                      
      if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(fileName))
      {
          this.description.setText("");
      }

      if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(fileName))
      {
         // Extract selected file's File object.

         File file = (File) evt.getNewValue();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

          try {
              builder = factory.newDocumentBuilder();

            // Read DOM from stream
            Document xml = builder.parse(new FileInputStream(file));
            NodeList descs = xml.getElementsByTagName("description");
            if (descs.getLength() == 0 ) {
                this.description.setText("");
            } else {
                this.description.setText(descs.item(0).getTextContent());
            }
            NodeList opts = xml.getElementsByTagName("options");
            if (opts.getLength() != 0 ) {
                this.options = opts.item(0).getTextContent();
                this.applyOptions = ((Element) opts.item(0)).getAttribute("apply").equals("yes") ? true : false;
            }

            // Read DOM from stream
          } catch (Exception ex) {
              this.description.setText("");
          }

       }
    }
    
    
    public String getOptions() {
        return this.options;
    }
    
    public boolean applyOptions() {
        return this.applyOptions;
    }
    
}
