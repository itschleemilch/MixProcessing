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
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import processing.core.PApplet;

/**
 * RenderFrame is a Frame with the functionality to get undecorated 
 * (= loose the border). Just double-click anywhere. If the frame was maximized
 * before, the frame enters the fullscreen.
 * 
 * To show/hide the cursor, right click anywhere.
 * 
 * The frame also contains invisible versions of the Processing Sketches, to give
 * them a valid access to the AWT hierarchy. 
 * 
 * @author Sebastian Schleemilch
 */
public class RenderFrame extends Frame
        implements WindowListener, MouseListener {
    /* Invisible Cursor, switch mode with right-click */
    Cursor zeroCursor;
    /* Insisible Area to hold all Processing Sketches */
    Panel invisiblePanel = new Panel();

    public RenderFrame() {
        super();
        setLayout(new BorderLayout());
        addWindowListener(this);
        addMouseListener(this);
        
        zeroCursor = getToolkit().createCustomCursor(
                new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR),
                new java.awt.Point(0, 0), "NOCURSOR");
        setCursor(zeroCursor);
        
        invisiblePanel.setLayout(null);
        super.add(invisiblePanel, BorderLayout.EAST);
        Dimension zeroDim = new Dimension(0, 0);
        invisiblePanel.setMinimumSize(zeroDim);
        invisiblePanel.setMaximumSize(zeroDim);
        invisiblePanel.setPreferredSize(zeroDim);
        
        setSize(300, 200);
        addIconImage();
    }
    
    public RenderFrame(String title) {
        this();
        setTitle(title);
    }
    
    private void addIconImage()
    {
        ArrayList<Image> icons = new ArrayList<>();
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
    
    /**
     * Adds a Processing sketch invisible to the frame to give it a valid 
     * access to the AWT hierarchy. 
     * 
     * @param c Processing Sketch to be added
     */
    public void addToInvisiblePanel(PApplet c)
    {
        invisiblePanel.add(c);
    }
    
    /**
     * Removes a Processing sketch from the invisible area (e.g. because it will
     * be deleted)
     * @param c Processing Sketch to be removed
     */
    public void removeFromInsisiblePanel(PApplet c)
    {
        invisiblePanel.remove(c);
    }
    
    public void centerWindowOnScreen()
    {
        Dimension screen = getToolkit().getScreenSize();
        int x = (screen.width-getWidth())/2;
        int y = (screen.height-getHeight())/2;
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
        comp.addMouseListener(this);
    }
    
    /* WindowListener Methods */
    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    /* MouseListener Methods */
    
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) // Doppelklick
        {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setVisible(false);
                    dispose();
                    setUndecorated(!isUndecorated());
                    setVisible(true);
                }
            });
            
        }
        if(e.getButton() == MouseEvent.BUTTON3) // Rechtsklick
        {
            if(getCursor() == zeroCursor)
                setCursor(Cursor.getDefaultCursor());
            else
                setCursor(zeroCursor);
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
    
}
