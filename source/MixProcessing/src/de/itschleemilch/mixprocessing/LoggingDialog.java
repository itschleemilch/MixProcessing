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
package de.itschleemilch.mixprocessing;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;


/**
 * Creates an Windows with the console logging outputs
 *
 * @author Sebastian Schleemilch
 */
public class LoggingDialog extends Frame implements WindowListener {
    private final TextArea outArea, errArea;

    public LoggingDialog() {
        super("Logging");
        setLayout(new BorderLayout(3, 5));
        addWindowListener(this);
        
        outArea = new TextArea();
        errArea = new TextArea();
        configureTextArea(outArea);
        configureTextArea(errArea);
        runReader(OUT_READER, outArea);
        runReader(ERR_READER, errArea);
        
        Panel centerPanel = new Panel(new BorderLayout());
        centerPanel.add(outArea, BorderLayout.CENTER);
        centerPanel.add(errArea, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        
        setSize(640, 480);
    }
    
    private void configureTextArea(TextArea area)
    {
        area.setEditable(true);
        area.setFocusable(false);
    }
    
    private void runReader(final BufferedReader reader, final TextArea target) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader br = reader;
                TextArea output = target;
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                while (true) {
                    try {
                        if(br.ready()) {
                            String line = br.readLine();
                            if(line != null)
                                output.append(line + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                    try {
                        Thread.yield(); // sleep some time
                    } catch (Exception e) {
                    }
                } // while(true)
            } // run
        }).start();
    }
    
    /* WindowListener Methods */
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        setVisible(false);
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
    
    static BufferedReader OUT_READER = null, ERR_READER = null;
    public static void initLogging()
    {
        PipedOutputStream outPipe = new PipedOutputStream();
        try {
            System.setOut(new PrintStream(outPipe, true, "UTF-8") );
            PipedInputStream outPIS = new PipedInputStream( outPipe );
            OUT_READER = new BufferedReader( new InputStreamReader(outPIS, "UTF-8") );
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        
        PipedOutputStream errPipe = new PipedOutputStream();
        try {
            System.setErr(new PrintStream(errPipe, true, "UTF-8") );
            PipedInputStream errPIS = new PipedInputStream( errPipe );
            ERR_READER = new BufferedReader( new InputStreamReader(errPIS, "UTF-8") );
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
    
    private static final long serialVersionUID = 1L;
}
