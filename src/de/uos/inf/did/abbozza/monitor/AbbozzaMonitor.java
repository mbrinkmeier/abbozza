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
 * @fileoverview The abbozza! varsion of the serial monitor.
 * 
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */

package de.uos.inf.did.abbozza.monitor;

import cc.arduino.packages.BoardPort;
import de.uos.inf.did.abbozza.Abbozza;
import de.uos.inf.did.abbozza.AbbozzaLocale;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.Timer;
import processing.app.Serial;

/**
 *
 * @author mbrinkmeier
 */
public class AbbozzaMonitor extends JFrame implements ActionListener {

    private BoardPort boardPort;
    private boolean monitorEnabled;
    private boolean closed;
    private final StringBuffer updateBuffer;
    private final Timer updateTimer;
    private Serial serial;
    private StringBuffer unprocessedMsg;    
    private HashMap<String,MonitorPanel> panels;
    // private AbbozzaMonitorPanel monitor = null;

    /**
     * Creates new form AbbozzaMonitor
     *
     * @param boardport
     */
    public AbbozzaMonitor(BoardPort boardport) {
        // System.out.println("AbbozzaMonitor start");
        setBoardPort(boardport);
        // System.out.println("AbbozzaMonitor " + boardport);
        initComponents();
        // System.out.println("AbbozzaMonitor after init");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                try {
                    closed = true;
                    close();
                    Abbozza.getInstance().closeMonitor();
                } catch (Exception e) {
                    // ignore
                }
            }
        });

        updateBuffer = new StringBuffer(1048576);
        updateTimer = new Timer(33, this);  // redraw serial monitor at 30 Hz
        updateTimer.start();
        unprocessedMsg = new StringBuffer();
        
        panels = new HashMap<String,MonitorPanel>();
        TableMonitor tableMonitor = new TableMonitor();
        this.addMonitorPanel(tableMonitor, "table");
        this.addMonitorPanel(new GraphMonitor(tableMonitor.getTableModel()), "graph");
        this.addMonitorPanel(new LevelMonitor(tableMonitor.getTableModel()), "level");

        textArea.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                  maybeShowPopup(e);
            }

            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    protocolPopUp.show(e.getComponent(),e.getX(), e.getY());
               }
            }
        });
        /*
        try {
            open();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        */
        
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;
        this.setLocation(x, y);
        // System.out.println("AbbozzaMonitor " + this);
        // System.out.println("AbbozzaMonitor start");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        protocolPopUp = new javax.swing.JPopupMenu();
        resetItem = new javax.swing.JMenuItem();
        tabPanel = new javax.swing.JTabbedPane();
        textPane = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        logoPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        protocolPopUp.setToolTipText("");

        resetItem.setText("Löschen");
        resetItem.setToolTipText("Löscht das Protokoll");
        resetItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetItemActionPerformed(evt);
            }
        });
        protocolPopUp.add(resetItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("abbozza! Monitor");
        setPreferredSize(new java.awt.Dimension(750, 500));

        textArea.setColumns(20);
        textArea.setRows(5);
        textPane.setViewportView(textArea);

        tabPanel.addTab("Protokoll", textPane);

        getContentPane().add(tabPanel, java.awt.BorderLayout.CENTER);

        logoPanel.setLayout(new java.awt.BorderLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/uos/inf/did/abbozza/img/abbozza200.png"))); // NOI18N
        jLabel1.setToolTipText("");
        jLabel1.setAlignmentX(1.0F);
        logoPanel.add(jLabel1, java.awt.BorderLayout.EAST);

        getContentPane().add(logoPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void resetItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetItemActionPerformed
        textArea.setText("");
    }//GEN-LAST:event_resetItemActionPerformed

    public synchronized void addToUpdateBuffer(char buff[], int n) {
        updateBuffer.append(buff,0, n);
    }

    private synchronized String consumeUpdateBuffer() {
        String s = updateBuffer.toString();
        updateBuffer.setLength(0);
        return s;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPopupMenu protocolPopUp;
    private javax.swing.JMenuItem resetItem;
    private javax.swing.JTabbedPane tabPanel;
    private javax.swing.JTextArea textArea;
    private javax.swing.JScrollPane textPane;
    // End of variables declaration//GEN-END:variables
    @Override
    public void actionPerformed(ActionEvent e) {
        String s = consumeUpdateBuffer();

        if (s.isEmpty()) {
            return;
        }

        // Default handling
        textArea.append(s);

        // Send to all monitor panels
        processMessage(s);
        
        /* if (monitor != null) {
            monitor.processMessage(s);
        }*/
        
        // Now check line by line by prefix

        //System.out.println("gui append " + s.length());
        /*
         if (autoscrollBox.isSelected()) {
         textArea.appendTrim(s);
         textArea.setCaretPosition(textArea.getDocument().getLength());
         } else {
         textArea.appendNoTrim(s);
         }
         */
    }

    private void processMessage(String s) {
        unprocessedMsg.append(s);
        
        String cmd;
        String prefix;
        
        int end = -1;
        int start = -1;
        do {
            start = unprocessedMsg.indexOf("[[");
            if ( start >= 0 ) {
                end = unprocessedMsg.indexOf("]]",start+2);
                if ( end >= 0 ) {
                    cmd = unprocessedMsg.substring(start+2, end);
                    unprocessedMsg.delete(0, end+2);
                    int space = cmd.indexOf(' ');
                    if ( space >= 0 ) {
                        prefix = cmd.substring(0,space);
                        cmd = cmd.substring(space+1,cmd.length());
                        
                        MonitorPanel panel = panels.get(prefix);
                        if (panel != null) panel.processMessage(cmd);
                    }
                }
            }
        } while( (start != -1) && (end != -1) );
    }
    
    
    private void addMonitorPanel(MonitorPanel panel, String prefix) {
        if (panel != null) {
            tabPanel.add(panel, 0);
            panels.put(prefix, panel);
        }        
    }

    
    public BoardPort getBoardPort() {
        return boardPort;
    }

    public void setBoardPort(BoardPort boardPort) {
        this.boardPort = boardPort;
    }

    public void enableWindow(boolean enable) {
        this.setVisible(true);
        
        monitorEnabled = enable;
        
        textArea.setEnabled(enable);
        for ( MonitorPanel panel : panels.values()) {
            panel.setEnabled(enable);
        }
    }

    // Puts the window in suspend state, closing the serial port
    // to allow other entity (the programmer) to use it
    public void suspend() throws Exception {
        enableWindow(false);
        if (serial != null) {
            serial.dispose();
            serial = null;
        }
        // close();
    }

    public void resume() throws Exception {
        // Enable the window
        enableWindow(true);
        
        // If the window is visible, try to open the serial port
        if (serial != null) {
            return;
        }

        if ( boardPort != null ) {
            serial = new Serial(boardPort.getAddress(), 9600) {
                @Override
                protected void message(char buff[], int n) {
                    addToUpdateBuffer(buff, n);
                }
            };
        } else {
            String msg = AbbozzaLocale.entry("msg.no_board");
            addToUpdateBuffer(msg.toCharArray(),msg.length());
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public void open() throws Exception {
        closed = false;
        if (serial != null) {
            return;
        }
        this.setVisible(true);

        if ( boardPort != null ) {
            serial = new Serial(boardPort.getAddress(), 9600) {
                @Override
                protected void message(char buff[], int n) {
                    addToUpdateBuffer(buff, n);
                }
            };
        } else {
            String msg = "Kein Board angeschlossen!";
            addToUpdateBuffer(msg.toCharArray(),msg.length());
        }

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;
        this.setLocation(x, y);

    }

    public void close() throws Exception {
        closed = true;
        this.setVisible(false);
        if (serial != null) {
            serial.dispose();
            serial = null;
        }
    }

}
