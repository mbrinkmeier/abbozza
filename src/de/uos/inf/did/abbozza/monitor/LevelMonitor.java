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
 * @fileoverview This monitor panel shows the current level of al channels.
 * 
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */

package de.uos.inf.did.abbozza.monitor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JPopupMenu;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author mbrinkmeier
 */
public class LevelMonitor extends MonitorPanel implements TableModelListener {

    private TableMonitorModel myTable;
    private Color[] colors;

    /**
     * Creates new form GraphMonitor
     */
    public LevelMonitor(TableMonitorModel table) {
        myTable = table;
        myTable.addTableModelListener(this);
        initComponents();
        colors = new Color[5];
        colors[0] = Color.RED;
        colors[1] = Color.GREEN;
        colors[2] = Color.CYAN;
        colors[3] = Color.ORANGE;
        colors[4] = Color.BLACK;

        initComponents();
        // levelPanel.setTableModel(table);
        // levelPanel.addMouseListener(new MonitorMouseListener(this));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void processMessage(String s) {
    }

    @Override
    public JPopupMenu getPopUp() {
        return null;
    }

    public String getName() {
        return "Pegel";
    }

    public void paint(Graphics gr) {
        Graphics2D gr2d = (Graphics2D) gr;
        gr.setColor(Color.BLACK);
        gr.fillRect(0, 0, getWidth(), getHeight());

        Rectangle rect = this.getVisibleRect();

        int channelWidth = getWidth() / 5;
        int lastRow = myTable.getRowCount() - 1;
        int y = 0;
        
        for (int col = 1; col <= 5; col++) {
            int value = 0;
            try {
                value = ((Integer) myTable.getValueAt(lastRow, col)).intValue();
            } catch (Exception ex) {
                value = -1;
            }
            gr.setColor(colors[col - 1]);
            switch (myTable.getType(col - 1)) {
                case '0':
                    y = (getHeight() - 20) * (1 - value);
                    break;
                case '1':
                    y = (getHeight() - 20) * (1023 - value) / 1023;
                    break;
                case '3' :
                    y = (getHeight()-20)*(32767-value)/65535;
                    break;
                default:
                    y = (getHeight() - 20) * (65535 - value) / 65535;
            }
            gr.setFont(new Font("SansSerif", Font.BOLD, 16));
            gr.drawLine(channelWidth * (col - 1), y+10, channelWidth, y+10);
            gr.fillRect(channelWidth * (col - 1)+25, y, channelWidth-50, 20);
            gr.setColor(Color.WHITE);
            int w = gr.getFontMetrics().stringWidth("Kanal " + col);
            int h = gr.getFontMetrics().getHeight();
            gr.drawString("Kanal " + col, channelWidth * (col - 1) + ((channelWidth - w) / 2), y + 10 + 6);
        }
    }

    public void setTableModel(TableMonitorModel model) {
        myTable = model;
        myTable.addTableModelListener(this);
        this.revalidate();
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        this.revalidate();
        this.repaint();
    }

}
