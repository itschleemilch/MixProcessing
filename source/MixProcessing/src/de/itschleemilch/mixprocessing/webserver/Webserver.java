/*
 MixProcessing - Live Mixing of Processing Sketches 
 https://github.com/itschleemilch/MixProcessing

 Copyright (c) 2014 Sebastian Schleemilch

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.itschleemilch.mixprocessing.webserver;

import de.itschleemilch.mixprocessing.EventManager;
import de.itschleemilch.mixprocessing.script.ScriptRunner;
import de.itschleemilch.mixprocessing.util.SinglePreference;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Integrated Webserver. 
 * Experimental.
 * Applications: Remote UI (prototyping, may also for the fimal version), remote API
 *
 * @author Sebastian Schleemilch
 */
public class Webserver extends Thread {
    private ServerSocket server = null;
    
    private final EventManager eventManager;
    private final ScriptRunner scriptRunner;
    
    private File fileStorage = null;
    
    /**
     * Creates a new Webserver object.
     * @param eventManager 
     * @see Webserver#startServer() 
     */
    public Webserver(EventManager eventManager) {
        this.eventManager = eventManager;
        this.scriptRunner = new ScriptRunner(eventManager);
        initServerStorage();
        startServer();
    }
    
    /**
     * Inites the fileStorage object and creates pathes if needed. The path
     * can be user-edited with the preference key in KEY_STORAGE.
     * @see Webserver#KEY_STORAGE
     */
    private void initServerStorage() {
        String storageFolder = SinglePreference.getPreference(
                KEY_STORAGE, null);
        if(storageFolder == null) {
            fileStorage = new File("webserver");
            fileStorage.mkdirs();
        }
        else {
            fileStorage = new File(storageFolder);
            fileStorage.mkdirs();
        }
        SinglePreference.setPreference(KEY_STORAGE, fileStorage.getAbsolutePath());
    }
    
    
    /**
     * Starts or restarts the Server
     */
    public final void startServer() {
        int serverPort = 8080;
        String portPreference = SinglePreference.getPreference(
                    KEY_PORT, "8080");
        try {
            serverPort = Integer.parseInt(portPreference);            
        } catch (NumberFormatException e) {
            serverPort = 8080;
        }
        
        // Stop a running server instance
        stopServer();
        // Open new server instance
        try {
            server = new ServerSocket(serverPort);
        } catch (IOException e) {
            server = null;
            System.err.println("Server can not open port: " + serverPort);
        }
        if(server != null) {
            start(); // start Thread to accept clients
        }
        
        SinglePreference.setPreference(KEY_PORT, Integer.toString(serverPort));
    }

    /**
     * If the server is running, then the server is stopped
     */
    public final void stopServer()
    {
        if(server != null) {
            try {
                server.close();
            } catch (Exception e) {
            }
            try {
                if(isAlive())
                    stop();
            } catch (Exception e) {
            }
            server = null;
        }
    }
    
    /**
     * Server's main loop to accept new clients
     */
    @Override
    public final void run() {
        while (true) { // Accept new clients
            try {
                final Socket client = server.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processClient(client);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (Exception e) {
            }
        } // while
    }
    
    private void processClient(final Socket client) {
        /* Open Input Stream, read request */
        final BufferedReader reader;
        final String clientCmd;
        try {
            reader= new BufferedReader( new InputStreamReader(
                client.getInputStream() ) );
            clientCmd = reader.readLine();
        } catch (Exception e) {
            // Failed to open input stream or to read first line
            try{client.close();}catch(Exception ee) {}
            return;
        }
        /* Determine HTTP request parameters */
        final String clientCmdLowerCase = clientCmd.toLowerCase();
        final String method = clientCmdLowerCase.substring(0, clientCmdLowerCase.indexOf(' '));
        
        // Get resource (eliminate first /) and get-param, example: GET /infotext.html HTTP/1.1
        final String query = clientCmd.substring(clientCmd.indexOf(" ") + 2, 
                clientCmd.lastIndexOf(" ") );
        
        int getParamBeginning = query.indexOf('?');
        final String resource = (getParamBeginning > -1) 
                ? query.substring(0, getParamBeginning) : query;
        
        /* Send requested file */
        final File requestedFile = new File(fileStorage, resource);
        OutputStream output = null;
        try {
            output = client.getOutputStream();
            if (requestedFile.exists() && requestedFile.isFile()) {
                sendFile(output, requestedFile);
            } 
            else {
                File defaultFile = new File(fileStorage, "index.html");
                sendFile(output, defaultFile);
            }
        } catch (IOException e) {
        } finally {
            if(output != null)
                try{output.close();} catch(IOException e) {}
            try{client.close();}catch(IOException ee) {}
        }
    } // process


    private void sendFile(OutputStream out, File sourceFile) throws IOException {
        final StringBuilder header = new StringBuilder();
        final String mime = MIME.findMime(sourceFile.getName());
        /* Output HTTP Header  */
        header.append("HTTP/1.1 200 OK\r\n");
        header.append("Server: ").append(SERVER_NAME).append("\r\n");
        header.append("Content-Length: ").append( Long.toString(sourceFile.length()) ).append("\r\n");
        header.append("Connection: close\r\n");
        header.append("Content-Type: ").append(mime).append("\r\n");
        header.append("\r\n");
        out.write(header.toString().getBytes("UTF-8"));
        /* Redirect file to client */
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(sourceFile);
            int read;
            byte[] buffer = new byte[1024];
            while ((read = fis.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
        } finally {
            if(fis != null)
                try{fis.close();} catch(Exception e) {}
        }
        /* Flush contents  */
        out.flush();
    }

    
    private final static String KEY_STORAGE = "webserver.storage";  
    private final static String KEY_PORT = "webserver.port";   
    
    private final static String SERVER_NAME = "MixProcessing Control Server";
}
