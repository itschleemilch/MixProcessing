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

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import processing.core.PApplet;

/**
 * Collectes all Sketch-Objects and performs group-actions like the
 * event-mechanism, resizing events or the rendering of all Sketches.
 * 
 * @author Sebastian Schleemilch
 * @see Sketch
 */
public class Sketches {
    final ArrayList<Sketch> sketches = new ArrayList<>();
    int lastW = 0, lastH = 0;

    public Sketches() {
    }
    
    public void addSketch(Sketch s)
    {
        sketches.add(s);
    }
    
    /**
     * Resize all sketches and their paint buffers.
     * @param w
     * @param h 
     */
    public void updateSize(int w, int h)
    {
        lastW = w; lastH = h;
        for(int i = 0; i < sketches.size(); i++)
        {
            Sketch s = sketches.get(i);
            PApplet applet = s.getInstance();
            if(applet != null && applet.frame != null)
            {
                //applet.stop();
                if(applet.width != w || applet.height != h)
                {
                    applet.setSize(w, h);
                    //applet.width = w;
                    //applet.height = h;
                    //applet.g.setSize(w, h);
                }
                //applet.start();
            }
        }
    }
    
    public void updateSize()
    {
        this.updateSize(lastW, lastH);
    }
    
    int lastMouseX = 0; int lastMouseY = 0;
    public void mouseMoved(int x, int y, boolean dragged)
    {
        for(int i = 0; i < sketches.size(); i++)
        {
            Sketch s = sketches.get(i);
            PApplet applet = s.getInstance();
            if(applet != null)
            {
                applet.mouseX = x;
                applet.mouseY = y;
                applet.pmouseX = lastMouseX;
                applet.pmouseY = lastMouseY;
                if(dragged)
                    applet.mouseDragged();
                else
                    applet.mouseMoved();
            }
        }
        lastMouseX = x;
        lastMouseY = y;
    }
    
    public void mouseEvent(boolean pressed, boolean released, boolean clicked)
    {
        for(int i = 0; i < sketches.size(); i++)
        {
            Sketch s = sketches.get(i);
            PApplet applet = s.getInstance();
            if(applet != null)
            {
                if(clicked)
                    applet.mouseClicked();
                else if(pressed)
                {
                    applet.mousePressed = true;
                    applet.mousePressed();
                }
                else if(released)
                {
                    applet.mousePressed = false;
                    applet.mouseReleased();
                }
            }
        }
    }
    
    /**
     * 
     * @param e
     * @param state 1=pressed, 2=released, 3=typed
     */
    public void keyEvent(KeyEvent e, int state)
    {
        
        for(int i = 0; i < sketches.size(); i++)
        {
            Sketch s = sketches.get(i);
            PApplet applet = s.getInstance();
            if(applet != null)
            {
                applet.key = e.getKeyChar();
                applet.keyCode = e.getKeyCode();
                if(state == 0)
                {
                    applet.keyPressed = true;
                    applet.keyPressed();
                }
                else if(state == 1)
                {
                    applet.keyPressed = false;
                    applet.keyReleased();
                }
                else
                    applet.keyTyped();
            }
        }
    }
    
    /**
     * Renders all sketches. Currently only with basic functionallity.
     * The output area is horizontally divided to N areas, each filled with 
     * one sketche's output.
     * TODO: Editable output areas (Java2D shapes, combined with 
     * java.awt.geom.Path2D
     * @param g
     * @param io 
     */
    public void paintAll(Graphics2D g, ImageObserver io)
    {
        if(sketches.size() == 0)
            return;
        int clipWidth = lastW / sketches.size();
        for(int i = 0; i < sketches.size(); i++)
        {
            Sketch s = sketches.get(i);
            PApplet applet = s.getInstance();
            if(applet != null)
            {
                Rectangle2D clip = new Rectangle2D.Float(clipWidth * i, 0, clipWidth, lastH);;
                g.setClip(clip);
                applet.paint(g);
                
                /* Tests for 3D Modi, failed up to now
                if(applet.g != null)
                {
                    if(applet.g.image != null)
                        g.drawImage(applet.g.image, 0, 0, io);
                    else if(applet.g.canvas != null)
                        applet.g.canvas.paint(g);
                }*/
            }
        }
    }
    
}
