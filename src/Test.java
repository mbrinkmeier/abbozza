
import de.uos.inf.did.abbozza.AbbozzaLogger;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import processing.app.Serial;

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
import jssc.*;

/**
 *
 * @author michael
 */
public class Test implements SerialPortEventListener {

    public static SerialPort port;
    
    public static void main(String args[]) throws FileNotFoundException, IOException, SerialPortException {
        String[] portNames = SerialPortList.getPortNames();

        if (portNames.length == 0) {
            System.out.println("No serial ports found");
            // return null;
        } else if (portNames.length == 1) {
            System.out.println("Unique port found: " + portNames[0]);
            // return portNames[0];
        } else {
            System.out.println("Several ports found:");
            for (int i = 0; i < portNames.length; i++) {
                System.out.println("\t" + portNames[i]);
            }
        }

//        port = new SerialPort("/dev/ttyACM0");
//        Test test = new Test();
//        try {
//            port.openPort();
//            port.addEventListener(test,SerialPort.MASK_RXCHAR);
//            port.setParams(SerialPort.BAUDRATE_115200,
//                         SerialPort.DATABITS_8,
//                         SerialPort.STOPBITS_1,
//                         SerialPort.PARITY_NONE);
//            String s;
//        } catch (SerialPortException ex) {
//            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if(event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                String receivedData = port.readString(event.getEventValue());
                System.out.println("Received response: " + receivedData);
            }
            catch (SerialPortException ex) {
                System.out.println("Error in receiving string from COM-port: " + ex);
            }
        }
    }

}
