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

import de.itschleemilch.mixprocessing.channels.ChannelEditing;
import de.itschleemilch.mixprocessing.channels.ChannelManagement;
import de.itschleemilch.mixprocessing.sketches.Sketches;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 * Outputs the sketches and uses double buffering (ideally page flipping, system
 * depending). Also receives all Mouse- and KeyEvents and passed them to the 
 * Processing Sketches.
 * 
 * @author Sebastian Schleemilch
 */
public class MixRenderer extends Canvas 
    implements ComponentListener, MouseListener, MouseMotionListener, KeyListener, Runnable {
    private final ChannelManagement channels;
    private final Sketches sketches;
    /* Double Buffers, offImg: Sketches, offImg2: Sketches+Editormode*/
    private BufferedImage offImg = null, offImg2 = null;
    
    private final ChannelEditing channelEditor;
    /* Self-Resetting Flag: If set-> causes full black background redraw */
    private boolean forceRefresh = false;
    
    public MixRenderer(Sketches sketches)
    {
        super();
        this.channels = new ChannelManagement();
        this.sketches = sketches;
        addComponentListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        
        channelEditor = new ChannelEditing(channels);
        addMouseListener(channelEditor);
    }
    
    public final void init()
    {
        sketches.updateSize(getWidth(), getHeight());
        new Thread(this).start();
    }

    /**
     * Returns a reference to the channel-editor
     * @return 
     */
    public ChannelEditing getChannelEditor() {
        return channelEditor;
    }
    
    /**
     * Returns all managed output channels
     * @return 
     */
    public ChannelManagement getChannels() {
        return channels;
    }

    /**
     * Returns all managed Sketches
     * @return 
     */
    public Sketches getSketches() {
        return sketches;
    }

    /**
     * Can be polled to wait until the refresh has been done.
     * @return 
     */
    public boolean isForceRefreshWaiting() {
        return forceRefresh;
    }
    
    public void setForceRefresh() {
        this.forceRefresh = true;
    }

    @Override
    public final void run() {
        while(true)
        {
            repaint();
            try {
                // TODO: Avoid fixed rate sleep, replace with framerate controller.
                Thread.sleep(15); // max. Framerate: 66 Hz
            } catch (InterruptedException e) {
            }
        } // while
    }

    @Override
    public final void paint(Graphics g) {
        Graphics2D g2d;
        if(offImg == null || offImg.getWidth() != getWidth() || offImg.getHeight() != getHeight())
        {
            offImg = getGraphicsConfiguration().createCompatibleImage(getWidth(), getHeight());
            g2d = offImg.createGraphics();
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            offImg2 = getGraphicsConfiguration().createCompatibleImage(getWidth(), getHeight());
        }
        else
            g2d = offImg.createGraphics();
        if(forceRefresh)
        {
            forceRefresh = false;
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        sketches.paintAll(offImg, g2d, channels);
        g2d.dispose();
        
        if(channels.isPreviewChannelOutlines()) {
            Graphics2D g2d_2 = offImg2.createGraphics();
            g2d_2.drawImage(offImg, 0, 0, this);
            channels.paintChannelOutlines( g2d_2 );
            channelEditor.paintEditorPath( g2d_2 );
            g2d_2.dispose();
            g.drawImage(offImg2, 0, 0, this);
        }
        else {
            g.drawImage(offImg, 0, 0, this);
        }
        
        
    }

    @Override
    public final void update(Graphics g) {
        paint(g); //To change body of generated methods, choose Tools | Templates.
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
