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


package de.itschleemilch.mixprocessing.sketches;

import de.itschleemilch.mixprocessing.RenderFrame;
import java.awt.Frame;
import processing.core.PApplet;

/**
 * Manages a Processing Sketch and a created instance of it. The sketch
 * is represented as a class file. To save RAM/CPU, the instance can be created
 * and deleted.
 * 
 * @author Sebastian Schleemilch
 */
public class Sketch {
    final Class template;
    PApplet instance = null;

    /**
     * Creates a Processing sketch represenation.
     * @param template base class of the sketch
     */
    public Sketch(Class template) {
        this.template = template;
    }
    
    /**
     * Returns the instance of the sketch, if created.
     * @return the instance or null
     * @see Sketch#createInstance(java.awt.Frame, java.lang.String) 
     */
    public PApplet getInstance()
    {
        if(instance == null)
        {
            return null;
        }
        else
            return instance;
    }
    
    /**
     * Creates an instance of the sketch and initiates it. 
     * @param f Access to AWT System for the Sketch
     * @param sketchPath Data-Path where files can be loaded
     * @return the created instance
     */
    public PApplet createInstance(RenderFrame f, String sketchPath)
    {
        if(getInstance() != null)
            return getInstance();
        else
        {
            try {
                Object localInstance = template.newInstance();
                if(localInstance instanceof PApplet)
                {
                    instance = (PApplet) localInstance;
                    instance.frame = f;
                    instance.sketchPath = sketchPath;
                    instance.init();
                    instance.size(f.getWidth(), f.getHeight()); // Voreinstellung und JAVA2d Modi setzen
                    instance.start();
                    f.addToInvisiblePanel(instance);
                }
                return instance;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    
    /**
     * If the sketch is currently not used, the created instance should be
     * distroyed to save ressources (RAM, CPU)
     */
    public void deleteInstance(RenderFrame f)
    {
        if(instance != null)
        {
            instance.stop();
            instance.destroy();
            f.removeFocusListener(instance);
            instance = null;
        }
    }
    
    /**
     * Returns the Sketches Name (equals Processing sketch name)
     * @return sketch name
     */
    public String getName()
    {
        return template.getName();
    }
    
}
