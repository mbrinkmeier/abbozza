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
 * @fileoverview This class implements a thread which retreives entries from its
 * SerialManager's message queue and sens it via the given serial port.
 * 
 * @author michael.brinkmeier@uni-osnabrueck.de (Michael Brinkmeier)
 */
package de.uos.inf.did.abbozza.monitor;

import cc.arduino.packages.BoardPort;

/**
 *
 * @author michael
 */
public class Sender extends Thread {
   
    private boolean stopped;
    private AbbozzaMonitor _monitor;
    private BoardPort _port;
    
    public Sender(AbbozzaMonitor manager) {
        this._monitor = manager;
        this._port = manager.getBoardPort();
    }
    
    public void portChanged() {
        this._port = this._monitor.getBoardPort();        
    }
    
    public void run() {
        stopped = false;
        while ( !stopped ) {
            if ( _monitor._msgQueue.peek() != null ) {
                Message msg = _monitor._msgQueue.poll();
                sendMsg(msg);
            } else {
                // Sleep for 100 milliseconds if no message is enqueued
                try {
                    this.sleep(100);
                } catch (InterruptedException ex) {
                    stopped = true;
                }
            }            
        }
    }
    
    private void sendMsg(Message msg) {
        switch (msg.getType()) {
            case Message.MSG_SEND_AND_FORGET :
                _monitor.writeMessage(msg.toString());  
                break;
        }
    }
 
    protected void stopIt() {
        stopped = true;
    }
}
