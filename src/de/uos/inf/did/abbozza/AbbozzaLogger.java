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
package de.uos.inf.did.abbozza;

import java.util.Vector;

/**
 *
 * @author michael
 */
public class AbbozzaLogger {

    static public final int NONE = 0;
    static public final int ERROR = 1;
    static public final int WARNING = 2;
    static public final int INFO = 3;
    static public final int ALL = 4;

    static private int level;
    
    static private Vector<AbbozzaLoggerListener> _listeners = new Vector<AbbozzaLoggerListener>();

    public static void addListener(AbbozzaLoggerListener listener) {
        if ( !_listeners.contains(listener) ) {
            _listeners.add(listener);
        }
    }
    
    public static void removeListener(AbbozzaLoggerListener listener) {
       _listeners.remove(listener);
    }
    
    private static void fire(String message) {
        for ( AbbozzaLoggerListener listener: _listeners) {
            listener.logged(message);
        }
    }
    
    public static void setLevel(int lvl) {
        level = lvl;
    }

    public static int getLevel() {
        return level;
    }

    public static void out(String msg) {
        if (level > NONE) {
            System.out.println("abbozza! [out] : " + msg);
            fire("abbozza! [out] : " + msg);
        }
    }

    public static void out(String msg, int lvl) {
        if (lvl <= level) {
            System.out.println("abbozza! [out] : " + msg);
            fire("abbozza! [out] : " + msg);
        }
    }

    public static void err(String msg) {
        System.out.println("abbozza! [err] : " + msg);
            fire("abbozza! [err] : " + msg);
    }
}
