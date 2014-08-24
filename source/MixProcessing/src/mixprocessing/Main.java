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

package mixprocessing;

import mixprocessing.load.JarManagement;
import mixprocessing.script.ScriptRunner;
import mixprocessing.script.ScriptingApi;
import mixprocessing.sketches.Sketch;
import mixprocessing.sketches.Sketches;
import mixprocessing.util.BasePath;
import mixprocessing.util.SinglePreference;
import mixprocessing.webserver.Webserver;
import mixprocessing.welcome.WelcomeChannels;
import mixprocessing.welcome.WelcomeFrame;
import mixprocessing.welcome.WelcomeSketch;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Basic demo of the current MixProcessing codebase. Loades all sketches
 * within the subfolder jarSource and outputs a divided view of all sketches with
 * full interaction (mouse, keyboard).
 * 
 * @author Sebastian Schleemilch
 */
public class Main {
    private static boolean userGUI_Logging = false;
    
    private static LoggingDialog initLogging()
    {
        if(userGUI_Logging) {
            LoggingDialog.initLogging();
        }
        LoggingDialog log = new LoggingDialog();
        return log;
    }
    
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
            while((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error while reading info text.");
        }
        finally {
            try {
                if(in != null) {
                    in.close();
                }
            } 
            catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
        System.out.println();
    }
    
    /**
     * Sets graphics system configurations. This has to be improved, since
     * the settings must be editable by the user.
     * 
     * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/2d/flags.html">System Properties for Java 2D</a>
     * @see <a href="https://developer.apple.com/library/mac/documentation/java/Reference/Java_PropertiesRef/Articles/JavaSystemProperties.html">Java System Property Reference for Mac</a>
     */
    private static void graphicsSettings()
    {
        // disable AWT background erase in general
        System.setProperty("sun.awt.noerasebackground", "true");
        // activate OpenGL acceleration
//        System.setProperty("sun.java2d.opengl", "true");
        // turn on acceleration of translucent images
        System.setProperty("sun.java2d.translaccel", "true");
        System.setProperty("sun.java2d.ddforcevram", "true");
        
        /* Use Apple’s Quartz renderer instead of Sun’s 2D renderer */
        if( System.getProperty("os.name").toLowerCase().contains("mac") ) {
            System.setProperty("apple.awt.graphics.UseQuartz", "true");
        }
        
        /* Swing Settings */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | 
                InstantiationException | UnsupportedLookAndFeelException e) {
            e.printStackTrace(System.err);
        }        
    }
    
    private static Class<?>[] readSketches(File sourceDir)
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
        LoggingDialog logging = initLogging();
        infoText();
        graphicsSettings();
        
        /* Subfolder, where all exported Processing Sketches (e.g. bubbles.jar) 
        have to be placed. Also all data files have to be stored here. */
        File defaultSource = new File(BasePath.getBasePath(), "jarSource"); 
        String jarSourcePath = SinglePreference.getPreference(
                JarManagement.KEY_SKETCH_JAR_SOURCE, 
                defaultSource.getAbsolutePath() );
        
        File jarSource = new File(jarSourcePath); 
        jarSource.mkdirs(); // creates the path, if not exists
        
        SinglePreference.setPreference(JarManagement.KEY_SKETCH_JAR_SOURCE, 
                jarSource.getAbsolutePath());
        
        RenderFrame frame = new RenderFrame("MixProcessing", logging);
        RenderFrame.centerWindowOnScreen(frame);
        
        logging.setIconImages( frame.getIconImages() );
        //logging.setVisible(true);
        
        // Search for Processing Library
        boolean pLibraryFound = false;
        while(!pLibraryFound)
        {
            try {
                Class.forName("processing.core.PApplet");
                pLibraryFound = true;
            } catch (ClassNotFoundException e) {
                ProcessingLibraryLoader pll = new ProcessingLibraryLoader();
                pll.setIconImages(frame.getIconImages());
                pll.setVisible(true);
                while(pll.isVisible()) {
                    Thread.yield();
                } // wait for ProcessingLibraryLoader
            }
        }
        
        Class<?>[] sketchClasses = readSketches(jarSource);
        Sketches sketches = new Sketches();
        
        // add welcome Sketch
        ArrayList<Class<?>> totalSketchList = new ArrayList<>( 
                Arrays.asList(sketchClasses) );
        totalSketchList.add(WelcomeSketch.class);

        
        // Renderer
        MixRenderer renderer = new MixRenderer(sketches);
        // Event System
        EventManager eventManager = new EventManager();
        // Scripting API
        ScriptingApi api = new ScriptingApi(eventManager, frame, renderer);
        frame.setScriptingAPI(api);
        
        frame.add(renderer, BorderLayout.CENTER);
        renderer.init();
        //frame.setVisible(true); -> moved after showing welcome screen
        
        // initialise Processing Sketches
        String sketchPath = jarSource.getAbsolutePath();
        for (Class<?> sketchClasse : totalSketchList) {
            Sketch s = new Sketch(sketchClasse);
            sketches.addSketch(s);
            s.createInstance(frame, sketchPath);
            System.out.printf("Initialise Sketch: %s\n", s.getName());
        }
        
        // Scripting API
        ScriptRunner scriptRunner = new ScriptRunner(api);
        ScriptingFrame scripting = new ScriptingFrame(eventManager, api, scriptRunner);
        scripting.setIconImages( frame.getIconImages() );
        //scripting.setVisible(true);
        scripting.setLocation(0, logging.getHeight()+10);
        
        /* Init Welcome Settings */
        WelcomeChannels.addWelcomeChannels(api.getChannels());
        api.sketchOutput(WelcomeSketch.class.getSimpleName(), 
                WelcomeChannels.CHANNEL_NAMES[0]);
        
        // Welcome Screen
        WelcomeFrame welcome = new WelcomeFrame(logging, scripting);
        welcome.setIconImages(frame.getIconImages());
        int welcomeX = frame.getLocation().x - welcome.getWidth();
        int welcomeY = frame.getLocation().y;
        welcome.setLocation(welcomeX, welcomeY);
        welcome.setVisible(true);
        
        // Show renderer
        frame.setVisible(true);
        
        // Webinterface
        Webserver webserver = new Webserver(scriptRunner);
        webserver.startServer();
    }
    
    private static final long serialVersionUID = 1L;
}
