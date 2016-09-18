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
package de.uos.inf.did.abbozza.handler;

import cc.arduino.packages.BoardPort;
import com.sun.net.httpserver.HttpExchange;
import de.uos.inf.did.abbozza.Abbozza;
import de.uos.inf.did.abbozza.monitor.AbbozzaMonitor;
import java.io.IOException;
import processing.app.Base;
import processing.app.PreferencesData;

/**
 *
 * @author michael
 */
public class MonitorHandler extends AbstractHandler {

    private AbbozzaMonitor monitor;
    
    public MonitorHandler(Abbozza abbozza) {
        super(abbozza);
    }

    @Override
    public void handle(HttpExchange exchg) throws IOException {
        String path = exchg.getRequestURI().getPath();
        boolean result = false;
        if (path.endsWith("/monitor")) {
            result = open();
        } else {
            result = resume();
        }
        if (result) {
            sendResponse(exchg, 200, "text/plain", "");
        } else {
            sendResponse(exchg, 440, "text/plain", "");
        }
    }

    public boolean open() {
        if (monitor != null) {
            if (resume()) {
                monitor.toFront();
                return true;
            } else {
                return false;
            }
        }

        BoardPort port = Base.getDiscoveryManager().find(PreferencesData.get("serial.port"));
        monitor = new AbbozzaMonitor(port);
        try {
            monitor.open();
            monitor.setVisible(true);
            monitor.toFront();
            monitor.setAlwaysOnTop(true);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            return false;
        }
        return true;
    }

    public boolean resume() {
        if (monitor == null) {
            return false;
        }
        try {
            monitor.resume();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
    
    public void suspend() {
        try {
            if (monitor != null) {
                monitor.suspend();
            }
        } catch (Exception ex) {
        }
    }

    public void close() {
        monitor = null;
    }

}
