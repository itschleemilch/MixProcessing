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

import de.itschleemilch.mixprocessing.sketches.Sketch;
import de.itschleemilch.mixprocessing.sketches.Sketches;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Basic demo of the current MixProcessing codebase. Loades all sketches
 * within the subfolder jarSource and outputs a divided view of all sketches with
 * full interaction (mouse, keyboard).
 * 
 * @author Sebastian Schleemilch
 */
public class Main {
    
    /**
     * Outputs licence notices at the terminal.
     */
    private static void infoText()
    {
        InputStream in = null;
        try {
            in = Main.class.getResourceAsStream("info.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;
            while((line = br.readLine()) != null)
                System.out.println(line);
        } catch (IOException e) {
            System.err.println("Error while reading info text.");
        }
        finally {
            try {
                in.close();
            } 
            catch (Exception e) { /*ignore exception here*/ }
        }
        System.out.println();
    }
    
    /**
     * Sets graphics system configurations. This has to be improved, since
     * the settings must be editable by the user.
     * 
     * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/2d/flags.html">System Properties for Java 2D</a>
     */
    private static void graphicsSettings()
    {
        // disable AWT background erase in general
        System.setProperty("sun.awt.noerasebackground", "true");
        // activate OpenGL acceleration
//        System.setProperty("sun.java2d.opengl", "true");
        // turn on acceleration of translucent images
//        System.setProperty("sun.java2d.translaccel", "true");
//        System.setProperty("sun.java2d.ddforcevram", "true");
    }
    
    private static Class[] readSketches(File sourceDir)
    {
        JarManagement jars = new JarManagement(sourceDir);
        jars.readJars();
        return jars.getSketchClasses();
    }

    /**
     * This is the MixProcessing start routine.
     * 
     * @param args Start Parameters are not used.
     */
    public static void main(String[] args) {
        infoText();
        graphicsSettings();
        
        /* Subfolder, where all exported Processing Sketches (e.g. bubbles.jar) 
        have to be placed. Also all data files have to be stored here. */
        File jarSource = new File("jarSource"); 
        jarSource.mkdirs(); // creates the path
        
        /* Currently CW-Change disabled: not needed
        // Change current working directory
        System.setProperty("user.dir", jarSource.getAbsolutePath()); */
        
        Class[] sketchClasses = readSketches(jarSource);
        Sketches sketches = new Sketches();
        
        RenderFrame frame = new RenderFrame("MixProcessing");
        frame.centerWindowOnScreen();
        
        MixRenderer renderer = new MixRenderer(sketches);
        frame.add(renderer, BorderLayout.CENTER);
        try {
            Thread.sleep(200); // let Renderer start
        } catch (Exception e) {
        }
        renderer.init();
        frame.setVisible(true);
        
        // initialise Processing Sketches
        String sketchPath = jarSource.getAbsolutePath();
        for (int i = 0; i < sketchClasses.length; i++) {
            Sketch s = new Sketch( sketchClasses[i] );
            sketches.addSketch(s);
            s.createInstance(frame, sketchPath);
            System.out.printf("INITIATED SKETCH %s\n", s.getName());
        }
    }
    
}
