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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * RenderFrame is a Frame with the functionality to get undecorated 
 * (= loose the border). Just double-click anywhere. If the frame was maximized
 * before, the frame enters the fullscreen.
 * 
 * To show/hide the cursor, right click anywhere.
 * 
 * @author Sebastian Schleemilch
 */
public class RenderFrame extends Frame
        implements WindowListener, KeyListener {
    private final LoggingDialog logging;
    /* Invisible Cursor, switch mode with right-click */
    private final Cursor zeroCursor;
    private final ArrayList<Image> icons = new ArrayList<>();

    public RenderFrame(LoggingDialog logging) {
        super();
        this.logging = logging;
        logging.addKeyListener(this);
        setLayout(new BorderLayout());
        addWindowListener(this);
        addKeyListener(this);
        
        zeroCursor = getToolkit().createCustomCursor(
                new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR),
                new java.awt.Point(0, 0), "NOCURSOR");
        
        setSize(800, 600);
        centerWindowOnScreen();
        addIconImage();
    }
    
    public RenderFrame(String title, LoggingDialog logging) {
        this(logging);
        setTitle(title);
    }
    
    private void addIconImage()
    {
        icons.add( getIconImage("MixProcessing-Logo_16x16.png") );
        icons.add( getIconImage("MixProcessing-Logo_32x32.png") );
        icons.add( getIconImage("MixProcessing-Logo_48x48.png") );
        icons.add( getIconImage("MixProcessing-Logo_256x256.png") );
        setIconImages(icons);
    }
    
    private Image getIconImage(String name)
    {
        return getToolkit().createImage(RenderFrame.class.getResource("res/"+name));
    }
    
    public final ArrayList<Image> getIconImages() {
        return icons;
    }
    
    public final void centerWindowOnScreen()
    {
        Dimension screen = getToolkit().getScreenSize();
        int x = (screen.width-getWidth())/2;
        int y = (screen.height-getHeight())/2;
        setLocation(x, y);
    }
    
    public final void toggleFullscreen() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                setOpacity(1f);
                setVisible(false);
                dispose();
                setUndecorated(!isUndecorated());
                setVisible(true);
            }
        });
    }
    
    public final void toggleMouse()
    {
        if(getCursor() == zeroCursor)
            setCursor(Cursor.getDefaultCursor());
        else
            setCursor(zeroCursor);
    }
    
    public final void toggleOpacity() {
        if(isUndecorated()) {
            if(getOpacity() == 1f)
                setOpacity(0.5f);
            else
                setOpacity(1f);
        }
    }


    /**
     * Adds the component a MouseListener to obtain double and right clicks.
     * @param comp
     * @param constraints 
     * @see Frame#add(java.awt.Component, java.lang.Object) 
     */
    @Override
    public void add(Component comp, Object constraints) {
        super.add(comp, constraints); 
        if(comp instanceof KeyListener)
        {
            addKeyListener( (KeyListener)comp );
        }
        comp.addKeyListener(this);
    }
    
    /* WindowListener Methods */
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
    
    /* KeyListener Methods */

    @Override
    public void keyPressed(KeyEvent e) {
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_F12)
            toggleFullscreen();
        else if(e.getKeyCode() == KeyEvent.VK_F11)
            toggleMouse();
        else if(e.getKeyCode() == KeyEvent.VK_F10)
            toggleOpacity();
        else if(e.getKeyCode() == KeyEvent.VK_F2)
            logging.setVisible(!logging.isVisible());
    }
}
