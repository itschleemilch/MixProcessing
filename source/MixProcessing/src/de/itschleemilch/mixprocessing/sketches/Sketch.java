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

import de.itschleemilch.mixprocessing.MPGraphics2D;
import de.itschleemilch.mixprocessing.RenderFrame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import processing.core.PApplet;
import processing.core.PGraphicsJava2D;

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
    boolean setupDone = false;
    static Field frameRatePeriodField = null;
    static Field frameRateLastNanosField = null;

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
                    instance.width = f.getWidth();
                    instance.height = f.getHeight();
                    setupDone = false;  
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
    
    public void resetSetup()
    {
        setupDone = false;
    }
    
    /**
     * Initial setup and Grafics settings before the sketch is drawn
     * @param bi
     * @param g 
     */
    public void doSetup(BufferedImage bi, Graphics2D g)
    {
        if(instance.g == null || !(instance.g instanceof MPGraphics2D) )
        {
           createMPGraphics(bi, g);
        }
        else
        {
            MPGraphics2D mpg2d = (MPGraphics2D) instance.g;
            mpg2d.g2 = g;
        }
        if(!setupDone )
        {
            setupDone = true;
            try {
                instance.setup();
            } catch (PApplet.RendererChangeException e) {
                createMPGraphics(bi, g);
                System.err.println("Please remove size() call in setup in sketch " + getName());
            }
        }
        MPGraphics2D mpg2d = (MPGraphics2D) instance.g;
        mpg2d.init();
        mpg2d.loadGraphicSettings();
    }
    
    /**
     * Stores internal Graphics settings after drawing this Sketch
     */
    public void storeInternalSettings()
    {
        MPGraphics2D mpg2d = (MPGraphics2D) instance.g;
        mpg2d.storeGraphicSettings();
    }
    
    private void createMPGraphics(BufferedImage bi, Graphics2D g)
    {
        MPGraphics2D mpg2d = new MPGraphics2D();
        instance.g = mpg2d;
        mpg2d.g2 = g;
        mpg2d.width = instance.width;
        mpg2d.height = instance.height;
        mpg2d.parent = instance;
        try {
            Field f = PGraphicsJava2D.class.getDeclaredField("offscreen");
            f.setAccessible(true);
            f.set(mpg2d, bi);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        mpg2d.init();
    }
    
    /**
     * Returns the Sketches Name (equals Processing sketch name)
     * @return sketch name
     */
    public String getName()
    {
        return template.getName();
    }
    
    /**
     * Checks if Sketch needs redraw depending on internal set
     * @return 
     */
    public boolean needsRedraw()
    {
        if(frameRatePeriodField != null && frameRateLastNanosField != null)
        {
            try {
                long period = frameRatePeriodField.getLong(instance);
                long lastDrawn = frameRateLastNanosField.getLong(instance);
                long diff = System.nanoTime() - lastDrawn;
                if(diff >= period)
                    return true;
                else
                    return false;
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
        return true;
    }
    
    /**
     * Sets the last-redrawn field to the current time
     */
    public void updateLastRedrawTime()
    {
        if(frameRateLastNanosField != null)
        {
            try {
                frameRateLastNanosField.setLong(instance, System.nanoTime());
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
    }
    
    static
    {
        try {
            frameRatePeriodField = PApplet.class.getDeclaredField("frameRatePeriod");
            frameRatePeriodField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        try {
            frameRateLastNanosField = PApplet.class.getDeclaredField("frameRateLastNanos");
            frameRateLastNanosField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
