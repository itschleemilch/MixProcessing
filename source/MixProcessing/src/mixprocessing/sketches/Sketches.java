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

import java.awt.Composite;
import java.awt.Font;
import mixprocessing.channels.ChannelManagement;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import mixprocessing.channels.SingleChannel;
import processing.core.PApplet;

/**
 * Collectes all Sketch-Objects and performs group-actions like the
 * event-mechanism, resizing events or the rendering of all Sketches.
 * 
 * @author Sebastian Schleemilch
 * @see Sketch
 */
public class Sketches {
    private final ArrayList<Sketch> sketches = new ArrayList<>();
    private int lastW = 0, lastH = 0;
    private int lastMouseX = 0, lastMouseY = 0;
    
    private final AffineTransform oneMatrix = new AffineTransform();

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
    public final void updateSize(int w, int h)
    {
        lastW = w; lastH = h;
        for(int i = 0; i < sketches.size(); i++)
        {
            Sketch s = sketches.get(i);
            s.resetSetup();
            PApplet applet = s.getInstance();
            if( applet != null && applet.frame != null && 
                    (applet.width != w || applet.height != h) )
            {
                applet.width = w;
                applet.height = h;
                if(applet.g != null)
                {
                    applet.g.width = w;
                    applet.g.height = h;
                }
            }
        }
    }
    
    public final void updateSize()
    {
        this.updateSize(lastW, lastH);
    }
    
    public final void mouseMoved(int x, int y, boolean dragged)
    {
        for (Sketch s : sketches) {
            PApplet applet = s.getInstance();
            if(applet != null && s.isReceivingMouseEvents())
            {
                applet.mouseX = x;
                applet.mouseY = y;
                applet.pmouseX = lastMouseX;
                applet.pmouseY = lastMouseY;
                if(dragged) {
                    applet.mouseDragged();
                }
                else {
                    applet.mouseMoved();
                }
            }
        }
        lastMouseX = x;
        lastMouseY = y;
    }
    
    public final void mouseEvent(boolean pressed, boolean released, boolean clicked)
    {
        for (Sketch s : sketches) {
            PApplet applet = s.getInstance();
            if(applet != null)
            {
                if(s.isReceivingMouseEvents()) {
                    if(clicked) {
                        applet.mouseClicked();
                    }
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
                else {
                    applet.mousePressed = false;
                }
            }
        }
    }
    
    /**
     * 
     * @param event
     * @param state 0=pressed, 1=released, 2=typed
     */
    public final void keyEvent(KeyEvent event, int state)
    {
        for (Sketch s : sketches) {
            PApplet applet = s.getInstance();
            if(applet != null)
            {
                if(s.isReceivingKeyEvents()) {
                    applet.key = event.getKeyChar();
                    applet.keyCode = event.getKeyCode();
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
                    else {
                        applet.keyTyped();
                    }
                }
                else {
                    applet.keyPressed = false;
                }
            }
        }
    }
    
    /**
     * Renders all sketches. Currently only with basic functionallity.
     * The output area is horizontally divided to N areas, each filled with 
     * one sketche's output.
     * TODO: Editable output areas (Java2D shapes, combined with 
     * java.awt.geom.Path2D
     * @param bi Double Buffer Image
     * @param g Graphics Object from double buffer
     * @param channels
     */
    public final void paintAll(BufferedImage bi, Graphics2D g, ChannelManagement channels)
    {
        /* Save internal graphic settings */
        final AffineTransform old_at = g.getTransform();
        final Paint old_paint = g.getPaint();
        final Shape old_clip = g.getClip();
        final Stroke old_stroke = g.getStroke();
        final Composite old_composite = g.getComposite();
        final Font old_font = g.getFont();
        final RenderingHints old_rHints = g.getRenderingHints();
            
        for(int i = 0; i < sketches.size(); i++)
        {
            Sketch sketch = sketches.get(i);
            PApplet applet = sketch.getInstance();
            
            if(applet != null && sketch.needsRedraw())
            {
                
                
                final SingleChannel channel = channels.getChannelForSketch(sketch);
                final Shape clip;
                if(channel != null && channel.isEnabled() && 
                        channel.getShape() != null) {
                    clip = channel.getShape();
                }
                else {
                    clip = channels.getNullChannelShape();
                }
                g.setTransform(oneMatrix); // reset transformation
                g.setClip(clip);
                
                sketch.doSetup(bi, g); 
                applet.draw();
                sketch.storeInternalSettings();
                sketch.updateLastRedrawTime();
            }
        }
        
        /* Restore internal graphic settings */
        g.setTransform(old_at);
        g.setPaint(old_paint);
        g.setClip(old_clip);
        g.setStroke(old_stroke);
        g.setComposite(old_composite);
        g.setFont(old_font);
        Iterator<?> keys = old_rHints.keySet().iterator();
        while(keys.hasNext())
        {
            Object key = keys.next();
            Object value = old_rHints.get( key );
            g.setRenderingHint((RenderingHints.Key) key, value);
        }
            
        channels.paintBlackedChannels(g);
    }
    
    /**
     * Searches for a Sketch
     * @param name
     * @return  Sketch or null
     */
    public final Sketch findSketch(String name)
    {
        for (Sketch sketch : sketches) {
            if(sketch.getName().equals(name)) {
                return sketch;
            }
        }
        return null;
    }
    
    /**
     * Returns all stored sketches
     * @return 
     */
    public final Sketch[] getAllSketches()
    {
        return sketches.toArray(new Sketch[0]);
    }
    
}
