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
package de.uos.inf.did.abbozza.monitor;

import com.sun.net.httpserver.HttpExchange;
import de.uos.inf.did.abbozza.arduino.handler.SerialHandler;

/**
 *
 * @author michael
 */
public class Message {
                
    private String _id;
    private String _msg;
    private HttpExchange _exchg;
    private SerialHandler _handler;
    private long _timeout;
    private long _stoptime;
    
    public Message(String id, String msg) {
        _id = id;
        _msg = msg;
        _exchg = null;
        _handler = null;
        _timeout = 0;
    }

    public Message(String id, String msg, HttpExchange exchg, SerialHandler handler, long timeout) {
        _id = id;
        _msg = msg;
        _exchg = exchg;
        _handler = handler;
        _timeout = timeout;
    }
    
    public String getMsg() {
        return _msg;
    }
    
    public String getID() {
        return _id;
    }

    public String toString() {
        if ( _id.length() > 0 ) {
            return _id + " " + _msg;
        }
        return _msg;
    }

    public HttpExchange getHttpExchange() {
        return _exchg;
    }
    
    public SerialHandler getHandler() {
        return _handler;
    }

    public long getTimeout() {
        return _timeout;
    }
    
    public void startTimeOut() {
        _stoptime = System.currentTimeMillis() + _timeout;
    }
    
    public boolean isTimedOut() {
        return (System.currentTimeMillis() > _stoptime );
    }
}
