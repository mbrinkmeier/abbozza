/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uos.inf.did.abbozza.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.uos.inf.did.abbozza.AbbozzaLocale;
import de.uos.inf.did.abbozza.AbbozzaLogger;
import de.uos.inf.did.abbozza.AbbozzaServer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 *
 * @author michael
 */
public class JarDirHandler implements HttpHandler {

    private Vector<Object> entries;

    public JarDirHandler() {
        entries = new Vector<Object>();
    }

    public void addDir(String path, String name) {
        File file = new File(path);
        if (!file.exists()) {
            AbbozzaLogger.err("JarHandler: " + name + " : " + file.getAbsolutePath() + " not found");
            file = null;
        } else {
            AbbozzaLogger.out("JarHandler: " + name + " : " + file.getAbsolutePath(),AbbozzaLogger.INFO);
            return;
        }
        entries.add(file);
    }
    
    
    public void addDir(File dir) {
        entries.add(dir);
    }

    
    public void addJar(String path, String name) {
        JarFile file;
        try {
            file = new JarFile(path);
            AbbozzaLogger.out("JarHandler: " + name + " : " + file.getName(),AbbozzaLogger.INFO);
        } catch (IOException e) {
            AbbozzaLogger.err("JarHandler: " + name + " not found (" + path + ")");
            return;
        }        
        entries.add(file);
    }
    
    
    public void addJar(JarFile jar) {
        entries.add(jar);
    }

    public void clear() {
        entries.clear();
    }

    @Override
    public void handle(HttpExchange exchg) throws IOException {

        String path = exchg.getRequestURI().getPath();
        OutputStream os = exchg.getResponseBody();
        
        byte[] bytearray = getBytes(path);

        if (bytearray == null) {
            String result = "abbozza! : " + path + " nicht gefunden!";
            // System.out.println(result);

            exchg.sendResponseHeaders(400, result.length());
            os.write(result.getBytes());
            os.close();
            return;
        }

        Headers responseHeaders = exchg.getResponseHeaders();
        if (path.endsWith(".css")) {
            responseHeaders.set("Content-Type", "text/css");
        } else if (path.endsWith(".js")) {
            responseHeaders.set("Content-Type", "text/javascript");
        } else if (path.endsWith(".xml")) {
            responseHeaders.set("Content-Type", "text/xml");
        } else if (path.endsWith(".svg")) {
            responseHeaders.set("Content-Type", "image/svg+xml");            
        } else if (path.endsWith(".abz")) {
            responseHeaders.set("Content-Type", "text/xml");            
        } else if (path.endsWith(".png")) {
            responseHeaders.set("Content-Type", "image/png");
        } else if (path.endsWith(".html")) {
            responseHeaders.set("Content-Type", "text/html");
        } else {
            responseHeaders.set("Content-Type", "text/text");            
        }

        // ok, we are ready to send the response.
        exchg.sendResponseHeaders(200, bytearray.length);
        os.write(bytearray, 0, bytearray.length);
        os.close();
    }

    public byte[] getBytes(String path) throws IOException {
        AbbozzaLogger.out("JarHandler: Reading " + path, AbbozzaLogger.INFO);
        byte[] bytearray = null;
        int tries = 0;

        while ((tries < 3) && (bytearray == null)) {

            Enumeration<Object> it = entries.elements();
            while (it.hasMoreElements() && (bytearray == null)) {
                Object entry = it.nextElement();
                if (entry instanceof JarFile) {
                    bytearray = getBytesFromJar((JarFile) entry, path);
                } else if (entry instanceof File) {
                    bytearray = getBytesFromDir((File) entry, path);
                }
            }

            if (bytearray == null) {
                tries++;
                AbbozzaServer.getInstance().findJarsAndDirs(this);
            }
        }

        if (bytearray == null) {
            AbbozzaLogger.out(AbbozzaLocale.entry("msg.not_found",path),AbbozzaLogger.ERROR);
        }
        return bytearray;
    }
    
    
    public byte[] getBytesFromDir(File webDir, String path) throws IOException {
        File file = new File(webDir + path);
        if (!file.exists()) {
            return null;
        }
        
        // Check if the requested file is below the given directory
        if (!file.getCanonicalPath().startsWith(webDir.getCanonicalPath())) {
            return null;
        }

        FileInputStream fis = new FileInputStream(file);

        byte[] bytearray = new byte[(int) file.length()];
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(bytearray, 0, bytearray.length);
        bis.close();

        return bytearray;
    }

    
    public byte[] getBytesFromJar(JarFile jar, String path) throws IOException {

        path = path.substring(1, path.length());
        ZipEntry entry = this.getEntry(jar, path);
        if (entry == null) {
            return null;
        }
        InputStream fis = jar.getInputStream(entry);

        if (fis == null) {
            return null;
        }

        byte[] bytearray = new byte[(int) entry.getSize()];
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(bytearray, 0, bytearray.length);
        bis.close();

        return bytearray;
    }

    
    public ZipEntry getEntry(JarFile jar, String name) {
        ZipEntry entry = jar.getEntry(name);
        if (entry != null) {
            return entry;
        }
        return null;
    }
    
    
    public InputStream getInputStream(String path) throws IOException {
        AbbozzaLogger.out("JarHandler: Reading " + path, AbbozzaLogger.INFO);
        InputStream stream = null;
        int tries = 0;

        while ((tries < 3) && (stream == null)) {

            Enumeration<Object> it = entries.elements();
            while (it.hasMoreElements() && (stream == null)) {
                Object entry = it.nextElement();
                if (entry instanceof JarFile) {
                    stream = getInputStreamFromJar((JarFile) entry, path);
                } else if (entry instanceof File) {
                    stream = getInputStreamFromDir((File) entry, path);
                }
            }

            if (stream == null) {
                tries++;
                AbbozzaServer.getInstance().findJarsAndDirs(this);
            }
        }

        if (stream == null) {
            AbbozzaLogger.out(AbbozzaLocale.entry("msg.not_found",path),AbbozzaLogger.ERROR);
        }
        return stream;        
    }


    public InputStream getInputStreamFromDir(File webDir, String path) throws IOException {
        File file = new File(webDir + path);
        if (!file.exists()) {
            return null;
        }
        
        // Check if the requested file is below the given directory
        if (!file.getCanonicalPath().startsWith(webDir.getCanonicalPath())) {
            return null;
        }

        FileInputStream fis = new FileInputStream(file);

        return fis;
    }

    
    public InputStream getInputStreamFromJar(JarFile jar, String path) throws IOException {

        path = path.substring(1, path.length());
        ZipEntry entry = this.getEntry(jar, path);
        if (entry == null) {
            return null;
        }
        InputStream fis = jar.getInputStream(entry);

        return fis;
    }
    
}
