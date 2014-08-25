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
package mixprocessing.channels;

import mixprocessing.EventManager;
import mixprocessing.sketches.Sketch;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import mixprocessing.sketches.Sketches;

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
    
    private final Sketches sketches;
    private int creationCounter = 0;

    public ChannelManagement(Sketches sketches) {
        this.sketches = sketches;
    }
    
    public final SingleChannel addChannel()
    {
        SingleChannel channel = new SingleChannel(creationCounter);
        channels.add(channel);
        if(eventManager != null) {
            eventManager.fireChannelsChanged();
        }
        creationCounter++;
        return channel;
    }
    
    public final SingleChannel addChannel(Shape s)
    {
        SingleChannel c = addChannel();
        c.setShape(s);
        return c;
    }
    
    /**
     * Returns a new and empty Group Channel Object
     * @return 
     */
    public final GroupChannel addGroupChannel()
    {
        GroupChannel channel = new GroupChannel(channels.size());
        channels.add(channel);
        if(eventManager != null) {
            eventManager.fireChannelsChanged();
        }
        creationCounter++;
        return channel;
    }
    
    /**
     * Deletes a Channel from the List
     * @param channel 
     */
    public final void removeChannel(SingleChannel channel)
    {
        channels.remove(channel);
        // Also remove association
        for(Sketch s : sketches.getAllSketches()) {
            if(s.getOutputChannel().equals(channel)) {
                s.setOutputChannel(null);
            }
        }
    }
    
    /**
     * Searches for the first output channel with the given name
     * @param name
     * @return 
     */
    public final SingleChannel findChannel(String name) {
        for (SingleChannel channel : channels) {
            if(channel.getChannelName().equals(name)) {
                return channel;
            }
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
        g.setFont(new Font("Arial", Font.BOLD, 10));
        final int STR_MODE_WITH = g.getFontMetrics().stringWidth("EDIT MODE");
        g.setColor(Color.BLACK);
        g.fillRect(10, 0, STR_MODE_WITH, 10);
        g.setColor(Color.RED);
        g.drawString("EDIT MODE", 10, 10);
        for (SingleChannel c : channels) {
            Shape s = c.getShape();
            if(s != null)
            {
                g.setColor(Color.RED);
                g.draw(s);
                Rectangle bounds = s.getBounds();
                final int STR_NAME_WIDTH = g.getFontMetrics().stringWidth(c.getChannelName());
                
                if(c instanceof GroupChannel) {
                    int x = bounds.x + ( bounds.width - STR_NAME_WIDTH) / 2;
                    int y = bounds.y + ( bounds.height - g.getFontMetrics().getHeight() ) / 2;
                    g.setColor(Color.BLACK);
                    g.fillRect(x, y-10, STR_NAME_WIDTH, 10);
                    g.setColor(Color.RED);
                    g.drawString(c.getChannelName(), x, y);
                }
                else {
                    int x_add = (bounds.width-STR_NAME_WIDTH)/2;
                    int x = bounds.x+x_add;
                    int y = bounds.y + bounds.height + g.getFontMetrics().getHeight() + 4;
                    g.setColor(Color.BLACK);
                    g.fillRect(x, y-10, STR_NAME_WIDTH, 10);
                    g.setColor(Color.RED);
                    g.drawString(c.getChannelName(), x, y);
                }
                ChannelEditing.drawControlPoints(g, s);
            }
        }
    }
    
    /**
     * Fills areas whre the paintBlack Flag is set by the user.
     * Needed to switch them on/off.
     * @param g 
     */
    public final void paintBlackedChannels(Graphics2D g)
    {
        g.setColor(Color.BLACK);
        for (SingleChannel c : channels) {
            if(c.paintBlackFlag && c.getShape() != null)
            {
                c.paintBlackFlag = false;
                g.fill(c.getShape());
            }
        }
    }
    
    /**
     * Association of sketches to channels
     * @param s
     * @return if found or null
     */
    public final SingleChannel getChannelForSketch(Sketch s)
    {
        return s.getOutputChannel();
    }
    /**
     * Returns a 0x0 rectangle shape to perform a contant
     * paint rate to all sketches even if they are on disabled channels.
     * @return 
     */
    public final Shape getNullChannelShape() {
        return offChannel;
    }
    
    /**
     * Associates a sketch to output on the given channel
     * @param sketch source Sketch
     * @param channel output target
     */
    public final void setSketchChannel(Sketch sketch, SingleChannel channel)
    {
        sketch.setOutputChannel(channel);
    }
    
    /**
     * Unsets a Sketch-Channel Link to stop outputting
     * @param sketch 
     */
    public final void unsetSketchChannel(Sketch sketch) {
        sketch.setOutputChannel(null);
    }
}
