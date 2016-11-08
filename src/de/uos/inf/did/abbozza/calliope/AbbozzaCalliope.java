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
package de.uos.inf.did.abbozza.calliope;

import com.sun.net.httpserver.HttpHandler;
import de.uos.inf.did.abbozza.AbbozzaLocale;
import de.uos.inf.did.abbozza.AbbozzaLogger;
import de.uos.inf.did.abbozza.AbbozzaServer;
import de.uos.inf.did.abbozza.calliope.handler.BoardHandler;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import org.python.core.PyCode;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

/**
 *
 * @author michael
 */
public class AbbozzaCalliope extends AbbozzaServer implements HttpHandler {

    private int _SCRIPT_ADDR = 0x3e000; 
    private String _pathToBoard = "";
    private AbbozzaCalliopeFrame frame;

    
    public static void main (String args[]) {
        AbbozzaCalliope abbozza = new AbbozzaCalliope();
        abbozza.init("calliope");
        
        abbozza.startServer();
        // abbozza.startBrowser("calliope.html");        
    }

    
    public void init(String system) {
        super.init(system);
    
        setPathToBoard(this.config.getOptionStr("pathToBoard"));        
        
        // Open Frame
        frame = new AbbozzaCalliopeFrame();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        frame.setVisible(true);
        frame.setState(JFrame.ICONIFIED);
    }
        
    
    public void setPathToBoard(String path) {
        _pathToBoard = path;
        if (_pathToBoard != null) {
            this.config.setOptionStr("pathToBoard", _pathToBoard);
        }
        AbbozzaLogger.out("Path to board set to " + path,4);
    }
    
    
    public String  getPathToBoard() {
        return _pathToBoard;
    }
    
    public File queryPathToBoard(String path) {
        File selectedDir = null;
        JFileChooser chooser = new JFileChooser();
        if ( path != null) {
            chooser.setCurrentDirectory(new File(path));
        }
        chooser.setDialogTitle(AbbozzaLocale.entry("gui.CalliopePath"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Select readable directory";
            }
        });
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedDir = chooser.getSelectedFile();
        } else {
        }
        return selectedDir;
    }

    @Override
    public void registerSystemHandlers() {
        httpServer.createContext("/abbozza/board", new BoardHandler(this, false));
        httpServer.createContext("/abbozza/queryboard", new BoardHandler(this, true));
    }

    @Override
    public void toolToBack() {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void toolSetCode(String code) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void toolIconify() {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String compileCode(String code) {
        AbbozzaLogger.out("Code generated",4);
        this.frame.setCode(code);
        return "";
    }

    @Override
    public String uploadCode(String code) {
        this.frame.setCode(code);        
        String java = embed(hexlify(code));
        AbbozzaLogger.out("Writing hex code to " + _pathToBoard + "/abbozza.hex",4);
        
        if ( java != "" ) {
                try {
                    PrintWriter out = new PrintWriter(_pathToBoard + "/abbozza.hex");
                    out.write(java);
                    out.flush();
                    out.close();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AbbozzaCalliope.class.getName()).log(Level.SEVERE, null, ex);
                }
        } else {
        }
        
        return "";
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private String hexlify(String code) {
        if ( code == null || code == "" ) return "";
            
        // Correct the line ends from MacOS and Windows is neccessary
        code.replace("\r\n","\n");
        code.replace("\r","\n");
        
        ByteBuffer bytes = ByteBuffer.allocate(2);
        bytes.putShort((short) code.length());
        
        bytes.put(0, (byte) (code.length() % 256));
        bytes.put(1, (byte) ((code.length() & 0x0000ff00) / 256));
        
        String len = String.format("%x", new BigInteger(1,bytes.array())).toUpperCase();
        while (len.length() < 4) len = "0" +len;

        String data =  "MP##" + code;
        // padding
        while ( data.length() % 16 != 0 ) {
            data = data + ((char) 0);
        }
        // @TODO check length of code
        String output = ":020000040003F7";
        int addr = _SCRIPT_ADDR;
        String chunk = "";
        String hexline = "";
        int checksum;
        bytes = ByteBuffer.allocate(4);
        for (int chunkPos = 0; chunkPos < data.length(); chunkPos = chunkPos+16 ) {
            chunk = data.substring(chunkPos, chunkPos+16 < data.length() ? chunkPos+16 : data.length() );
            bytes.clear();
            bytes.put(0,(byte) (chunk.length() % 256));
            bytes.putShort(1,(short) addr);
            bytes.put(3,(byte) 0);

            byte[] ch = chunk.getBytes();
            if (chunkPos == 0) {
                ch[2] = (byte) (code.length() % 256);
                ch[3] = (byte) ((code.length() & 0x0000ff00) / 256);
                // hexline = hexline.replaceFirst("4D502323","4D50" + len);
            }            

            String second = String.format("%x", new BigInteger(1,ch)).toUpperCase();
            while (second.length() < 32 ) second = "0" + second;
            
            hexline = String.format("%x", new BigInteger(1,bytes.array())).toUpperCase() 
                    + second; // String.format("%x", new BigInteger(1, chunk.getBytes())).toUpperCase();
            
            checksum = 0;
            byte[] by = bytes.array();
            for (int i = 0; i < by.length; i++) {
                checksum += by[i];
            }
            for (int i = 0; i < ch.length; i++) {
                checksum += ch[i];
            }            
            byte[] by2 = new byte[1];
            by2[0] = (byte) ((-checksum) & 0xff);
            String check = String.format("%x", new BigInteger(1,by2)).toUpperCase();
            while (check.length() < 2) check = "0" +check;
            hexline = ":" + hexline + check;
            output = output + "\n" + hexline;
            addr += 16;
        }
        output = output.replace("####", len);
        return output;
    }

    private String embed (String hexcode) {
        // the embedded hexcode should be inserted before the last two lines
        // of the runtime code.
        String runtime = "";
        try {
            runtime = new String(this.jarHandler.getBytes("/js/abbozza/calliope/runtimes/calliope.hex"));
        } catch (Exception ex) {
            return "";
        }
        
        runtime = runtime.replace("######", hexcode);
        return runtime;
    }
}
