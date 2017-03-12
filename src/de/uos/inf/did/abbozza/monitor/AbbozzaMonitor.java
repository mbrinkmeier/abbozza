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
import com.sun.net.httpserver.HttpExchange;
import de.uos.inf.did.abbozza.arduino.Abbozza;
import de.uos.inf.did.abbozza.AbbozzaLocale;
import de.uos.inf.did.abbozza.AbbozzaServer;
import de.uos.inf.did.abbozza.arduino.handler.SerialHandler;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.AbstractQueue;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;
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
    protected ArrayBlockingQueue<Message> _msgQueue;
    protected HashMap<String,Message> _waitingMsg;
    private Sender _sender;
    
    // private AbbozzaMonitorPanel monitor = null;

    /**
     * Creates new form AbbozzaMonitor
     *
     * @param boardport
     */
    public AbbozzaMonitor(BoardPort boardport) {
        setBoardPort(boardport);
        _msgQueue = new ArrayBlockingQueue<Message>(10);
        _waitingMsg = new HashMap<String,Message>();
        
        _sender = new Sender(this);
        _sender.start();
        
        initComponents();
        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        this.sendText.getEditor().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendTextEditorActionPerformed(evt);
            }
        });
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                try {
                    _sender.stopIt();
                    closed = true;
                    close();
                    AbbozzaServer.getInstance().monitorIsClosed();
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
        java.awt.GridBagConstraints gridBagConstraints;

        protocolPopUp = new javax.swing.JPopupMenu();
        resetItem = new javax.swing.JMenuItem();
        tabPanel = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        textPane = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        sendButton = new javax.swing.JButton();
        sendText = new javax.swing.JComboBox();
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
        setMinimumSize(new java.awt.Dimension(640, 480));
        setPreferredSize(new java.awt.Dimension(640, 480));

        java.awt.GridBagLayout jPanel1Layout = new java.awt.GridBagLayout();
        jPanel1Layout.columnWidths = new int[] {0};
        jPanel1Layout.rowHeights = new int[] {0, 17, 0};
        jPanel1Layout.columnWeights = new double[] {100.0};
        jPanel1Layout.rowWeights = new double[] {100.0};
        jPanel1.setLayout(jPanel1Layout);

        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setRows(5);
        textPane.setViewportView(textArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(textPane, gridBagConstraints);

        sendButton.setMnemonic(KeyEvent.VK_ENTER);
        sendButton.setText(AbbozzaLocale.entry("gui.send"));
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(sendButton, gridBagConstraints);
        sendButton.getAccessibleContext().setAccessibleName("sendButton");
        sendButton.getAccessibleContext().setAccessibleDescription("");

        sendText.setEditable(true);
        sendText.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(sendText, gridBagConstraints);

        tabPanel.addTab("Protokoll", jPanel1);

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

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        String msg = (String) this.sendText.getEditor().getItem();
        if (msg != null && !msg.isEmpty()) {
            this.sendMessage(msg);
            this.sendText.addItem(new String(msg));
            this.sendText.setSelectedItem(null);
        }
    }//GEN-LAST:event_sendButtonActionPerformed

    private void sendTextEditorActionPerformed(java.awt.event.ActionEvent evt) {                                         
        String msg = evt.getActionCommand();
        if (msg != null && !msg.isEmpty() ) {
            this.sendMessage(msg);
            this.sendText.addItem(new String(msg));
            this.sendText.setSelectedItem(null);
        }
    }

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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JPopupMenu protocolPopUp;
    private javax.swing.JMenuItem resetItem;
    private javax.swing.JButton sendButton;
    private javax.swing.JComboBox sendText;
    private javax.swing.JTabbedPane tabPanel;
    private javax.swing.JTextArea textArea;
    private javax.swing.JScrollPane textPane;
    // End of variables declaration//GEN-END:variables
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Check waiting messages for timed out requests
        if ( !_waitingMsg.isEmpty() ) {
            Set<String> keys = _waitingMsg.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
                Message msg = _waitingMsg.get(key);
                // Remove timed out requests
                if ( msg.isTimedOut() ) {
                    _waitingMsg.remove(key);
                    try {
                        msg.getHandler().sendResponse(msg.getHttpExchange(), 400, "text/plain", "query timed out!");
                    } catch (IOException ex) {
                        Logger.getLogger(AbbozzaMonitor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        // Check update buffer
        String s = consumeUpdateBuffer();

        if (s.isEmpty()) {
            return;
        }

        // Default handling
        appendText(s);

        // Send to all monitor panels
        processMessage(s);        

    }
    
    /**
     * Write a message to the serial port
     * 
     * @param msg The message
     */
    protected void writeMessage(String msg) {
        serial.write(msg);
        appendText("-> " + msg + "\n");
    }
    
    /**
     * Enque a message for sending without timeout and without waiting for it.
     * 
     * @param msg The message
     */
    public void sendMessage(String msg) {
        if ( this.boardPort != null ) {
            _msgQueue.add(new Message("",msg));
        }
    }

    /**
     * Enque a message from a HTTP-request for sending. Assign an ID to it
     * and wait for a response, if the timeout is positive.
     * 
     * @param msg The message to be send
     * @param exchg The HttpExchange object representing the request
     * @param handler The Handler handling the request
     * @param timeout The timeout for the response (if greater than zero)
     */
    public void sendMessage(String msg, HttpExchange exchg, SerialHandler handler, long timeout) {
        if ( this.boardPort == null ) return;
        if ( timeout > 0 ) {
            String id = "_" + Long.toHexString(System.currentTimeMillis());
            Message _msg = new Message(id,msg,exchg,handler,timeout); 
            _msgQueue.add(_msg);
        } else {
            sendMessage(msg);
            try {
                handler.sendResponse(exchg, 200, "text/plain", "");
            } catch (IOException ex) {
                Logger.getLogger(AbbozzaMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Append a text to the textfield showing the communication.
     * 
     * @param msg The text to be appended
     */
    protected void appendText(String msg) {
        this.textArea.append(msg);
    }
    
    /**
     * Process a message received from the board. If it is enclose
     * double brackets [[ <prefix> <msg> ]]. Check the <prefix> and send it 
     * to the appropriate Panel. If <prefix> is of the form _.* it is
     * processed by the Monitor itself, since it is an answer to a request.
     * 
     * @param s The string received
     */
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
                        MonitorPanel panel = panels.get(prefix);
                        if (panel != null) {
                            cmd = cmd.substring(space+1,cmd.length());
                            panel.processMessage(cmd);
                        } else {
                            respondTo(cmd);
                        }
                    }
                }
            }
        } while( (start != -1) && (end != -1) );
    }
    
    /**
     * This method responds to a received message wih leading id.
     * 
     * @param msg The message
     */
    private void respondTo(String msg) {
        int pos;
        Message _msg;
        msg = msg.trim();
        if ( msg.startsWith("_") ) {
            pos = msg.indexOf(' ');
            String id = msg.substring(0, pos);
            msg = msg.substring(pos).trim();
            _msg = _waitingMsg.get(id);
            if ( _msg != null ) {
                _waitingMsg.remove(id);
                try {
                    _msg.getHandler().sendResponse(_msg.getHttpExchange(), 200, "text/plain", msg);
                } catch (IOException ex) {
                    Logger.getLogger(AbbozzaMonitor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    /**
     * Add a Panel to the Monitor. Messages enclosed in double brackets 
     * of the form [[ <prefix> <msg> ]] are send to the panel.
     * 
     * @param panel The panel to be added
     * @param prefix The prefix of messages handled by the panel
     */
    private void addMonitorPanel(MonitorPanel panel, String prefix) {
        if (panel != null) {
            tabPanel.add(panel, 0);
            panels.put(prefix, panel);
        }        
    }

    /**
     * Returns the current port is a board is connected, null otherwise.
     * @return 
     */
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
            String msg = "No board connected!";
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

    public void addWaitingMsg(Message msg) {
        _waitingMsg.put(msg.getID(), msg);
    }
    
}
