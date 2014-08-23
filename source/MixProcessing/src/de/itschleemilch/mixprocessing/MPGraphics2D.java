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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Field;
import java.util.Iterator;
import processing.core.PGraphicsJava2D;

/**
 * Used to replace processing.core.PGraphicsJava2D
 * Allows a minimal implementation to get PApplet running without having been
 * initialised.
 * 
 * @author Sebastian Schleemilch
 */
public class MPGraphics2D extends PGraphicsJava2D {
    private final Composite defaultComposite;
    /* Variables that store the Graphics2D states */
    private Color storeBackground = null, storeColor = null;
    private Composite storeComposite = null;
    private Font storeFont = null;
    private Paint storePaint = null;
    private RenderingHints storeHints = null;
    private Stroke storeStroke = null;
    private AffineTransform storeTransform = null;

    public MPGraphics2D() {
        defaultComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
        try {
            Field f = PGraphicsJava2D.class.getDeclaredField("defaultComposite");
            f.setAccessible(true);
            f.set(this, defaultComposite);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace(System.err);
        }
    }
    
    /**
     * Should be called before every usage
     */
    public final void init()
    {
        checkSettings();
        resetMatrix(); // reset model matrix
        vertexCount = 0;
        storeGraphicSettings();
    }
    
    /**
     * Saves the Graphic Context Settings
     */
    public final void storeGraphicSettings() {
        storeBackground = g2.getBackground();
        storeColor = g2.getColor();
        storeComposite = g2.getComposite();
        storeFont = g2.getFont();
        storePaint = g2.getPaint();
        storeHints = g2.getRenderingHints();
        storeStroke = g2.getStroke();
        storeTransform = g2.getTransform();
    }
    
    /**
     * Loads the Graphic Context Settings
     */
    public final void loadGraphicSettings() {
        if(storeBackground != null) {
            g2.setBackground(storeBackground);
        }
        if(storeColor != null) {
            g2.setColor(storeColor);
        }
        if(storeComposite != null) {
            g2.setComposite(storeComposite);
        }
        if(storeFont != null) {
            g2.setFont(storeFont);
        }
        if(storePaint != null) {
            g2.setPaint(storePaint);
        }
        if(storeHints != null)
        {
            Iterator<?> keys = storeHints.keySet().iterator();
            while(keys.hasNext())
            {
                Object key = keys.next();
                Object value = storeHints.get( key );
                g2.setRenderingHint((RenderingHints.Key) key, value);
            }
        }
        if(storeStroke != null) {
            g2.setStroke(storeStroke);
        }
        if(storeTransform != null) {
            g2.setTransform(storeTransform);
        }
    }
    

    /**
     * Necessary override, to fix not inited Processing Sketch
     * @param iwidth
     * @param iheight 
     */
    @Override
    public final void setSize(int iwidth, int iheight) {
        width = iwidth;
        height = iheight;
    }

    /**
     * Prevent allocating buffers
     */
    @Override
    protected void allocate() {
    }

    @Override
    public void requestDraw() {
    }

    @Override
    public void beginDraw() {
    }

    @Override
    public void endDraw() {
    }

    @Override
    protected void defaultSettings() {
        /* From original PGraphicsJava2D: */
        if (quality > 0) {
            smooth();
        }
        else {
            noSmooth();
        }
        colorMode(RGB, 255);
        fill(255);
        stroke(0);
        strokeWeight(DEFAULT_STROKE_WEIGHT);
        strokeJoin(DEFAULT_STROKE_JOIN);
        strokeCap(DEFAULT_STROKE_CAP);
        shape = 0;
        rectMode(CORNER);
        ellipseMode(DIAMETER);
        autoNormal = true;
        textFont = null;
        textSize = 12;
        textLeading = 14;
        textAlign = LEFT;
        textMode = MODEL;
        background(backgroundColor);
        blendMode(BLEND);
        settingsInited = true;
        
        /* Extended: */
        /*
        storeBackground = null; storeColor = null;
        storeComposite = null;
        storeFont = null;
        storePaint = null;
        storeHints = null;
        storeStroke = null;
        storeTransform = null;*/
    }
    
    /**
     * Overwrite was necessary because of exceptions
     */
    @Override
    public void backgroundImpl() {
        Color bgColor = new Color(backgroundColor);
        Composite oldComposite = g2.getComposite();
        g2.setComposite(defaultComposite);
        
        pushMatrix();
        resetMatrix();
      
        g2.setColor(bgColor);
        g2.fillRect(0, 0, width, height);
        
        popMatrix();
        g2.setComposite(oldComposite);
    }

}
