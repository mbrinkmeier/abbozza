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

/**
 *
 * @author michael
 */
public class Message {
    
    public final static int MSG_SEND_AND_FORGET = 0;
    
    private String _id;
    private String _msg;
    private int _type;
    
    public Message(int type, String id, String msg) {
        _id = id;
        _msg = msg;
        _type = type;
    }
    
    public String getMsg() {
        return _msg;
    }
    
    public String getID() {
        return _id;
    }

    public int getType() {
        return _type;
    }
    
    public String toString() {
        if ( _id.length() > 0 ) {
            return _id + " " + _msg;
        }
        return _msg;
    }

}
