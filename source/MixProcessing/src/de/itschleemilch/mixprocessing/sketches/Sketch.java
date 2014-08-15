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
    private final Class template;
    private PApplet instance = null;
    private boolean setupDone = false;
    private boolean receivingMouseEvents = true, receivingKeyEvents = true;

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
    public final PApplet getInstance()
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
    public final PApplet createInstance(RenderFrame f, String sketchPath)
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
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace(System.err);
            }
            return null;
        }
    }
    
    /**
     * If the sketch is currently not used, the created instance should be
     * distroyed to save ressources (RAM, CPU)
     * 
     * @param f 
     */
    public final void deleteInstance(RenderFrame f)
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
     * Sketch's setup method is called again
     */
    public final void resetSetup()
    {
        setupDone = false;
    }
    
    /**
     * Initial setup and Grafics settings before the sketch is drawn
     * @param bi
     * @param g 
     */
    public final void doSetup(BufferedImage bi, Graphics2D g)
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
    public final void storeInternalSettings()
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
     * Checks if Sketch needs redraw depending on internal set
     * @return 
     */
    public final boolean needsRedraw()
    {
        if(FRAME_RATE_PERIOD_FIELD != null && FRAME_RATE_LAST_NANOS_FIELD != null)
        {
            try {
                long period = FRAME_RATE_PERIOD_FIELD.getLong(instance);
                long lastDrawn = FRAME_RATE_LAST_NANOS_FIELD.getLong(instance);
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
    public final void updateLastRedrawTime()
    {
        if(FRAME_RATE_LAST_NANOS_FIELD != null)
        {
            try {
                FRAME_RATE_LAST_NANOS_FIELD.setLong(instance, System.nanoTime());
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
    }

    public final boolean isReceivingKeyEvents() {
        return receivingKeyEvents;
    }

    /**
     * Sets if the sketch receives future key events.
     * @param enabled 
     */
    public final void setReceivingKeyEvents(boolean enabled) {
        this.receivingKeyEvents = enabled;
    }

    public final boolean isReceivingMouseEvents() {
        return receivingMouseEvents;
    }

    /**
     * Sets if the sketch receives future mouse events.
     * @param enabled 
     */
    public final void setReceivingMouseEvents(boolean enabled) {
        this.receivingMouseEvents = enabled;
    }
    
    
    /**
     * Returns the Sketches Name (equals Processing sketch name)
     * @return sketch name
     */
    public final String getName()
    {
        return template.getName();
    }
    
    private static final Field FRAME_RATE_PERIOD_FIELD;
    private static final Field FRAME_RATE_LAST_NANOS_FIELD;
    /**
     * Creates global variables needed for reflection methods during
     * PApplet modification.
     */
    static
    {
        Field fRP_F = null, fRLN_F = null;
        try {
            fRP_F = PApplet.class.getDeclaredField("frameRatePeriod");
            fRP_F.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        FRAME_RATE_PERIOD_FIELD = fRP_F;
        try {
            fRLN_F = PApplet.class.getDeclaredField("frameRateLastNanos");
            fRLN_F.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        FRAME_RATE_LAST_NANOS_FIELD = fRLN_F;
    }
}
