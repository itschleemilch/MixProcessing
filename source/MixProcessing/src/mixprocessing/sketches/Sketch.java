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


package mixprocessing.sketches;

import mixprocessing.MPGraphics2D;
import mixprocessing.RenderFrame;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import mixprocessing.channels.SingleChannel;
import processing.core.PApplet;
import processing.core.PGraphicsJava2D;

/**
 * Manages a Processing Sketch and a created instance of it. The sketch
 * is represented as a class file. To save RAM/CPU, the instance can be created
 * and deleted.
 * 
 * @author Sebastian Schleemilch
 */
public class Sketch implements Comparable<Sketch> {
    private final Class<?> template;
    private PApplet instance = null;
    private boolean setupDone = false;
    private boolean receivingMouseEvents = true, receivingKeyEvents = true;
    private float alpha = 1.0f; // 1.0: opace, 0.0: transparent
    
    private SingleChannel outputChannel = null;

    /**
     * Creates a Processing sketch represenation.
     * @param template base class of the sketch
     */
    public Sketch(Class<?> template) {
        this.template = template;
    }
    
    /*************************************************************
     * Instantiation code
     *************************************************************/
    
    /**
     * Returns the instance of the sketch, if created.
     * @return the instance or null
     * @see Sketch#createInstance(mixprocessing.RenderFrame, java.lang.String) 
     */
    public final PApplet getInstance()
    {
        if(instance == null)
        {
            return null;
        }
        else {
            return instance;
        }
    }
    
    /**
     * Creates an instance of the sketch and initiates it. 
     * @param f Access to AWT System for the Sketch
     * @param sketchPath Data-Path where files can be loaded
     * @return the created instance
     */
    public final PApplet createInstance(RenderFrame f, String sketchPath)
    {
        if(getInstance() != null) {
            return getInstance();
        }
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
                    instance.frameCount = 0;
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
    
    /*************************************************************
     * Initialisation and graphics methods
     *************************************************************/
    
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
        MPGraphics2D mpg2d;
        if(instance.g == null || !(instance.g instanceof MPGraphics2D) ) {
           createMPGraphics(bi, g);
        }
        mpg2d = (MPGraphics2D) instance.g;
        mpg2d.g2 = g;
       
        boolean callSetup = false;
        if(!setupDone )
        {
            setupDone = true;
            instance.frameCount = 0;
            mpg2d.init();
            callSetup = true;
        }
        
        mpg2d.loadGraphicSettings();
        // Set sketch's opacity
        if(alpha < 1.0f) {
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            mpg2d.g2.setComposite(ac);
        }
        if(callSetup) {
            try {
                instance.setup();
            } catch (PApplet.RendererChangeException e) {
                createMPGraphics(bi, g);
                System.err.println("Please remove size() call in setup in sketch " + getName());
            }
        }
    }
    
    /**
     * Stores internal Graphics settings after drawing this Sketch
     */
    public final void storeInternalSettings()
    {
        MPGraphics2D mpg2d = (MPGraphics2D) instance.g;
        mpg2d.storeGraphicSettings();
        instance.frameCount++;
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
        } catch (NoSuchFieldException | SecurityException | 
                IllegalArgumentException | IllegalAccessException e) {
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
                return diff >= period;
            } catch (IllegalArgumentException | IllegalAccessException e) {
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
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace(System.out);
            }
        }
    }
    
    /*************************************************************
     * Normal get/set methods
     *************************************************************/
    
    /**
     * Returns the current output cannel
     * @return (or null, if not present)
     */
    public SingleChannel getOutputChannel() {
        return outputChannel;
    }

    /**
     * Sets the desired output channel for this Sketch. Can be null if the
     * Sketch should be off.
     * @param outputChannel 
     */
    public void setOutputChannel(SingleChannel outputChannel) {
        this.outputChannel = outputChannel;
    }
    
    /**
     * Returns the alpha value of the Sketch
     * @return 
     * @see Sketch#setAlpha(float) 
     */
    public float getAlpha() {
        return alpha;
    }
    
    /**
     * Sets the alpha value (how opaque the sketch is drawn.)
     * 
     * @param alpha 0: transparent. 1: opaque.
     */
    public void setAlpha(float alpha) {
        if(alpha < 0) {
            this.alpha = 0.0f;
        }
        else if(alpha > 1.0f) {
            this.alpha = 1.0f;
        }
        else {
            this.alpha = alpha;
        }
    }
    
    /**
     * Returns the Sketches Name (equals Processing sketch name)
     * @return sketch name
     */
    public final String getName()
    {
        return template.getSimpleName();
    }
    
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(Sketch o) {
        return getName().compareTo(o.getName());
    }
    
    /*************************************************************
     * EVENT HANDLING
     *************************************************************/

    /**
     * Returns if this sketch should receive key events
     * @return 
     */
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

    /**
     * Returns if this sketch should receive mouse events
     * @return 
     */
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

    /*************************************************************
     * Static Code: Global variables
     *************************************************************/
    
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
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace(System.out);
        }
        FRAME_RATE_PERIOD_FIELD = fRP_F;
        try {
            fRLN_F = PApplet.class.getDeclaredField("frameRateLastNanos");
            fRLN_F.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace(System.out);
        }
        FRAME_RATE_LAST_NANOS_FIELD = fRLN_F;
    }
}
