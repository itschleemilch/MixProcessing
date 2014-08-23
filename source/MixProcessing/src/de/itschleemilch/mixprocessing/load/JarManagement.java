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

package de.itschleemilch.mixprocessing.load;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Scanner;
import processing.core.PApplet;

/**
 * Scannes a specified folder for valid Processing Sketches and collects them.
 *
 * @author Sebastian Schleemilch
 */
public class JarManagement {
    /* location where exported Processing sketches are stored */
    private final File jarFolder;
    /* Stores all loaded sketches and their runtime instances */
    private final ArrayList<Class<?>> sketches = new ArrayList<>();

    /**
     * Create new JarManagement object. Afterwards the readJars() method can be
     * called.
     * @param jarFolder folder to be scanned
     * @see JarManagement#readJars() 
     */
    public JarManagement(File jarFolder) {
        this.jarFolder = jarFolder;
        System.out.printf("Opening Sketches at: %s\n", jarFolder.getAbsoluteFile().toString());
    }
    
    /**
     * Searches for JAR (Java Archive) files within the jarFolder.
     * @see JarManagement#readJar(java.io.File) 
     */
    public final void readJars()
    {
        File[] jars = jarFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() & pathname.getName().endsWith(".jar");
            }
        });
        sketches.clear();
        for (File jar : jars) {
            readJar(jar);
        }
    }
    /**
     * Scans the JAR archive for a manifest file and loades the declared
     * main class. If the main class is a valid Processing Sketch (instance
     * of PApplet), the class is added to the sketches list. The sketch is not
     * instanced, nor inited yet.
     * 
     * @param jar Jar File to be scanned.
     */
    private void readJar(File jar)
    {
        System.out.printf("\tTest JAR file: %s \n", jar.toURI());
        URLClassLoader cloader = null;
        String mainClass = null;
        try {
            URL jarURL = new URL("jar", "",jar.toURI()+"!/");    
            cloader = URLClassLoader.newInstance(new URL[] {jarURL });   
            URL res = cloader.findResource("META-INF/MANIFEST.MF");
            if(res == null)
            {
                System.out.printf("\t\tCan't find a Manifest-File at %s\n", jarURL);
                return;
            }
            System.out.printf("\t\tScan Manifest: %s\n", res);
            Scanner input = new Scanner(res.openStream(), "UTF-8");
            String line;
            while(input.hasNextLine())
            {
                line = input.nextLine();
                //System.out.printf("\t\t(%s)\n", line);
                if(line.toLowerCase().startsWith("main-class:"))
                {
                    mainClass = line.substring("Main-Class: ".length()).trim();
                }
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        } 
        if(cloader != null && mainClass != null)
        {
            System.out.printf("\t\tDiscovered main class: %s from %s\n", mainClass, jar.getName());
            try {
                Class<?> sketchClass = cloader.loadClass(mainClass);
                Class<?> superClass = sketchClass.getSuperclass();
                if(superClass.equals(PApplet.class))
                {
                    sketches.add(sketchClass);
                    System.out.printf("\t\tSketch loaded: %s\n", mainClass);
                }
                else
                    System.out.printf("\t\tClass is no Processing Sketch: %s\n", mainClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace(System.err);
            }
        }
        else if(cloader != null)
        {
            System.out.printf("\t\tCan't find a main class.\n");
        }
        else if(mainClass != null)
        {
            System.out.printf("\t\tCan't create classloader.\n");
        }
    }
    
    /**
     * Returns all found processing sketches as their plain classes.
     * @return all sketch classes within the scanned folder
     */
    public final Class<?>[] getSketchClasses()
    {
        return sketches.toArray(new Class<?>[0]);
    }
    
    public static final String KEY_SKETCH_JAR_SOURCE = "sketches.source.exported";
}
