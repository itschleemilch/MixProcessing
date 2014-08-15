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
package de.itschleemilch.mixprocessing.channels;

import de.itschleemilch.mixprocessing.EventManager;
import de.itschleemilch.mixprocessing.sketches.Sketch;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Manages the output channels (variable output areas) and the
 * sketch-association. 
 * 
 * Provides drawing methods specific for changing channels (enable/disable, edit).
 *
 * @author Sebastian Schleemilch
 */
public class ChannelManagement {
    private final ArrayList<SingleChannel> channels = new ArrayList<>();
    private boolean previewChannelOutlines = true;
    private final Rectangle2D.Float offChannel = new Rectangle2D.Float(0, 0, 0, 0);
    public EventManager eventManager = null; // is set external by EventManager

    public ChannelManagement() {
        addChannel(new java.awt.geom.Ellipse2D.Float(10, 20, 300, 400));
        GeneralPath.Float path1 = new GeneralPath.Float();
        path1.moveTo(320, 50);
        path1.curveTo(330, 20, 390, 60, 450, 50);
        path1.lineTo(500, 500);
        path1.curveTo(410, 420, 450, 470, 330, 400);
        path1.closePath();
        addChannel(path1);
        addChannel(new java.awt.geom.RoundRectangle2D.Float(510, 30, 100, 300, 30, 60));
    }
    
    public final SingleChannel addChannel()
    {
        SingleChannel channel = new SingleChannel(channels.size());
        channels.add(channel);
        if(eventManager != null)
            eventManager.fireChannelsChanged();
        return channel;
    }
    
    public final SingleChannel addChannel(Shape s)
    {
        SingleChannel c = addChannel();
        c.setShape(s);
        return c;
    }
    
    /**
     * Removes a Channel from the List
     * @param channel 
     */
    public final void removeChannel(SingleChannel channel)
    {
        channels.remove(channel);
    }
    
    /**
     * Searches for the first output channel with the given name
     * @param name
     * @return 
     */
    public final SingleChannel findChannel(String name) {
        for (SingleChannel channel : channels) {
            if(channel.getChannelName().equals(name))
                return channel;
        }
        return null;
    }
    
    /**
     * Returns all channels stored by manager
     * @return 
     */
    public final SingleChannel[] getAllChannels()
    {
        return channels.toArray(new SingleChannel[0]);
    }

    /**
     * Preview-Mode: Draws Channel Outlines and allows drawing of new channels
     * @return 
     */
    public final boolean isPreviewChannelOutlines() {
        return previewChannelOutlines;
    }

    /**
     * Preview-Mode: Draws Channel Outlines and allows drawing of new channels
     * @param previewChannelOutlines 
     */
    public void setPreviewChannelOutlines(boolean previewChannelOutlines) {
        this.previewChannelOutlines = previewChannelOutlines;
    }
    
    /**
     * Draws a red outline to the channels
     * @param g 
     */
    public final void paintChannelOutlines(Graphics2D g)
    {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        for (SingleChannel c : channels) {
            Shape s = c.getShape();
            if(s != null)
            {
                g.setColor(Color.RED);
                g.draw(s);
                Rectangle bounds = s.getBounds();
                int y = bounds.y + bounds.height + g.getFontMetrics().getHeight()+4;
                int x_add = (bounds.width-g.getFontMetrics().stringWidth(c.getChannelName()))/2;
                g.drawString(c.getChannelName(), bounds.x+x_add, y);
                ChannelEditing.drawControlPoints(g, s);
            }
        }
    }
    
    /**
     * Fills disabled areas with black - needed to switch them on/off
     * @param g 
     */
    public final void paintDisabledChannels(Graphics2D g)
    {
        g.setColor(Color.BLACK);
        for (SingleChannel c : channels) {
            if(!c.isEnabled() && c.getShape() != null)
            {
                g.fill(c.getShape());
            }
        }
    }
    
    /**
     * Association of sketches to channels
     * @param s
     * @param index
     * @return 
     */
    public final Shape getOutputChannel(Sketch s, int index)
    {
        if(index >= 0 && index < channels.size())
        {
            SingleChannel c = channels.get(index);
            if(c.isEnabled())
                return c.getShape();
        }
        return offChannel;
    }
}
