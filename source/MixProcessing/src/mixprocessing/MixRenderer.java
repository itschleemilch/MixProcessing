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

package mixprocessing;

import mixprocessing.channels.ChannelEditing;
import mixprocessing.channels.ChannelManagement;
import mixprocessing.sketches.Sketches;
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
    private BufferedImage offImg = null;
    private Graphics2D offscreenG = null;
    
    private final ChannelEditing channelEditor;
    /* Self-Resetting Flag: If set-> causes full black background redraw */
    private boolean forceRefresh = true;
    
    private int repaintSleep = 16; // used within repaint loop
    
    public MixRenderer(Sketches sketches)
    {
        super();
        this.sketches = sketches;
        this.channels = new ChannelManagement(sketches);
        addComponentListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        
        channelEditor = new ChannelEditing(channels);
        addMouseListener(channelEditor);
        
        setMaxFrameRate(35f);
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
    public final ChannelEditing getChannelEditor() {
        return channelEditor;
    }
    
    /**
     * Returns all managed output channels
     * @return 
     */
    public final ChannelManagement getChannels() {
        return channels;
    }

    /**
     * Returns all managed Sketches
     * @return 
     */
    public final Sketches getSketches() {
        return sketches;
    }

    /**
     * Can be polled to wait until the refresh has been done.
     * @return 
     */
    public final boolean isForceRefreshWaiting() {
        return forceRefresh;
    }
    
    public final void setForceRefresh() {
        this.forceRefresh = true;
    }
    
    /**
     * Returns the set maximum frame rate.
     * @return 
     */
    public final float getMaxFrameRate() {
        return 1000f / (float)repaintSleep;
    }
    
    /**
     * Sets the absolut maximum frame rate. 
     * @param frameRate 
     */
    public final void setMaxFrameRate(float frameRate) {
        float sleep = 1000f / frameRate;
        int newSleep = Math.round(sleep);
        if(newSleep < 10) { // set lower limit
            newSleep = 10;
        }
        repaintSleep = newSleep;
    }

    @Override
    public final void run() {
        Thread.currentThread().setName("MP Renderer Loop");
        while(true)
        {
            repaint();
            try {
                // TODO: Avoid fixed rate sleep, replace with framerate controller.
                Thread.sleep(repaintSleep);
            } catch (InterruptedException e) {
            }
        } // while
    }

    @Override
    public final void paint(Graphics g) {
        /* Offscreen Images out of date? */
        if(offImg == null || offImg.getWidth() != getWidth() || offImg.getHeight() != getHeight())
        {
            offImg = getGraphicsConfiguration().createCompatibleImage(getWidth(), getHeight());
            offscreenG = offImg.createGraphics();
            offscreenG.clearRect(0, 0, getWidth(), getHeight());
            forceRefresh = true;
        }
        if(forceRefresh)
        {
            forceRefresh = false;
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            offscreenG.setColor(Color.BLACK);
            offscreenG.fillRect(0, 0, getWidth(), getHeight());
        }
        sketches.paintAll(offImg, offscreenG, channels);
        
        
        if(channels.isPreviewChannelOutlines()) {
            channels.paintChannelOutlines( offscreenG );
            channelEditor.paintEditorPath( offscreenG );
            g.drawImage(offImg, 0, 0, this);
        }
        else {
            g.drawImage(offImg, 0, 0, this);
        }
    }

    @Override
    public final void update(Graphics g) {
        paint(g);
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
    
    private static final long serialVersionUID = 1L;
}
