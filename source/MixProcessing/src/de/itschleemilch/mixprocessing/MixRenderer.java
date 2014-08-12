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

import de.itschleemilch.mixprocessing.sketches.Sketches;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;

/**
 * Outputs the sketches and uses double buffering (ideally page flipping, system
 * depending). Also receives all Mouse- and KeyEvents and passed them to the 
 * Processing Sketches.
 * 
 * @author Sebastian Schleemilch
 */
public class MixRenderer extends Canvas 
    implements ComponentListener, MouseListener, MouseMotionListener, KeyListener, Runnable {
    final Sketches sketches;
    final long FRAME_RATE = 60L;
    final long FRAME_PERIOD = (1000L) / FRAME_RATE;
    boolean renderTaskRunning = false;
    
    public MixRenderer(Sketches sketches)
    {
        super();
        this.sketches = sketches;
        addComponentListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        setIgnoreRepaint(true);
    }
    
    public void init()
    {
        sketches.updateSize(getWidth(), getHeight());
        new Thread(this).start();
    }

    @Override
    public void run() {
        while(true)
        {
            BufferStrategy bs = getBufferStrategy();
            if(bs == null && isDisplayable())
            {
                createBufferStrategy(2);
                bs = getBufferStrategy();
                
            }
            if(bs != null)
            {
                Graphics g = null;
                try {
                    do{
                        g = bs.getDrawGraphics();
                        sketches.paintAll((Graphics2D)g, this);
                        g.dispose();
                    }while(bs.contentsLost());
                    bs.show();
                } catch (Exception e) {
                    //e.printStackTrace(System.err);
                } finally{
                    if(g != null)
                        g.dispose();
                }
            }//(bs != null)
            try {
                // TODO: Avoid fixed rate sleep, replace with framerate controller.
                Thread.sleep(15); // max. Framerate: 66 Hz
            } catch (InterruptedException e) {
            }
        } // while
    }

    public void render(Graphics g) {
        sketches.paintAll((Graphics2D)g, this);
    }
    
    /* Event Handling starts here */
    
    @Override
    public void componentResized(ComponentEvent e){
        sketches.updateSize(getWidth(), getHeight());
    }
    @Override
    public void componentMoved(ComponentEvent e){}
    @Override
    public void componentShown(ComponentEvent e){}
    @Override
    public void componentHidden(ComponentEvent e){}

    @Override
    public void mouseClicked(MouseEvent e) {
        sketches.mouseEvent(false, false, true);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        sketches.mouseEvent(true, false, false);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        sketches.mouseEvent(false, true, false);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        sketches.mouseMoved(e.getX(), e.getY(), false);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        sketches.mouseMoved(e.getX(), e.getY(), true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        sketches.keyEvent(e, 0);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        sketches.keyEvent(e, 1);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        sketches.keyEvent(e, 2);
    }
    
    
}
