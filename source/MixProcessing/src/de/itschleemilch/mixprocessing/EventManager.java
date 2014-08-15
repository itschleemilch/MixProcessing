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

import de.itschleemilch.mixprocessing.channels.ChannelManagement;
import de.itschleemilch.mixprocessing.channels.SingleChannel;
import de.itschleemilch.mixprocessing.events.ChannelsChangedListener;
import de.itschleemilch.mixprocessing.sketches.Sketch;
import de.itschleemilch.mixprocessing.sketches.Sketches;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

/**
 *
 * @author Sebastian Schleemilch
 */
public class EventManager {
    private final MixRenderer renderer;
    private final ChannelManagement channels;
    private final Sketches sketches;
    
    private final ArrayList<ChannelsChangedListener> ccListener = new ArrayList<ChannelsChangedListener>();

    public EventManager(MixRenderer renderer) {
        this.renderer = renderer;
        this.channels = renderer.getChannels();
        this.sketches = renderer.getSketches();
        
        setExternalReferences();
    }
    
    private void setExternalReferences()
    {
        final EventManager em = this;
        new Thread(new Runnable() {

            @Override
            public void run() {
                channels.eventManager = em;
            }
        }).start();
    }

    public ChannelManagement getChannels() {
        return channels;
    }

    public MixRenderer getRenderer() {
        return renderer;
    }

    public Sketches getSketches() {
        return sketches;
    }
    
    
    /*************************************************************
     * Channel Control
     *************************************************************/
    
    /**
     * 
     * @param newName Name of the new Group Channel
     * @param sourceChannels Shapes to be implemented
     * @return 
     */
    public final boolean createGroupChannel(String newName, String ... sourceChannels) {
        ArrayList<Shape> sourceShapes = new ArrayList<>();
        for (String sourceChannel : sourceChannels) {
            SingleChannel channel = channels.findChannel(sourceChannel);
            if(channel.getShape() != null)
                sourceShapes.add( channel.getShape() );
        }
        if(sourceShapes.size()==0)
            return false;
        else {
            GeneralPath gp = new GeneralPath();
            for(Shape sourceShape : sourceShapes) {
                gp.append(sourceShape, false);
            }
            channels.addChannel(gp).setChannelName(newName);
            return true;
        }
    }
    
    public final boolean channelOn(String channelName) {
        SingleChannel c = channels.findChannel(channelName);
        if(c == null)
            return false;
        else
        {
            c.setEnabled(true);
            return true;
        }
    }
    
    public final boolean channelOff(String channelName) {
        SingleChannel c = channels.findChannel(channelName);
        if(c == null)
            return false;
        else
        {
            c.setEnabled(false);
            return true;
        }
    }
    
    /**
     * Enter channel editing mode
     */
    public final void channelEditing() {
        channels.setPreviewChannelOutlines(true);
    }
    
    /**
     * Exit channel editing mode
     */
    public final void channelNormal()
    {
        channels.setPreviewChannelOutlines(false);
    }
    
    public final boolean channelRename(String oldName, String newName) {
        SingleChannel c = channels.findChannel(oldName);
        if(c == null)
            return false;
        else
        {
            c.setChannelName(newName);
            return true;
        }
    }
    
    /**
     * Sets the paintBlackFlag of the channel. The next paint-cycle will
     * fill the area with black.
     * @param channelName
     * @return 
     */
    public final boolean channelBlacking(String channelName) {
        SingleChannel c = channels.findChannel(channelName);
        if(c == null)
            return false;
        else
        {
            c.paintBlackFlag = true;
            return true;
        }
    }
    
    public final boolean channelRemove(String channelName) {
        SingleChannel c = channels.findChannel(channelName);
        if(c == null)
            return false;
        else
        {
            channels.removeChannel(c);
            return true;
        }
    }
    
    /*************************************************************
     * Rendering Control
     *************************************************************/
    public final void forceRefresh() {
        renderer.setForceRefresh();
        while(renderer.isForceRefreshWaiting());
    }
    
    /*************************************************************
     * Sketch Control
     *************************************************************/
    
    /**
     * Updates the sketch<->output channel association.
     * Attention: Restarts Sketch!
     * @param sketchName
     * @param channelName
     * @return 
     */
    public final boolean outputSketch(String sketchName, String channelName) {
        boolean returnValue = outputSketchNoRestart(sketchName, channelName);
        restartSketch(sketchName);
        return returnValue;
    }
    
    /**
     * Updates the sketch<->output channel association.
     * Attention: Does not restarting the Sketch!
     * @param sketchName
     * @param channelName
     * @return 
     */
    public final boolean outputSketchNoRestart(String sketchName, String channelName) {
        Sketch s = sketches.findSketch(sketchName);
        SingleChannel c = channels.findChannel(channelName);
        if(s != null && c != null) {
            channels.setSketchChannel(s, c);
            s.resetSetup();
            return true;
        }
        else return false;
    }
    
    public final boolean restartSketch(String sketchName)
    {
        Sketch s = sketches.findSketch(sketchName);
        if(s == null)
            return false;
        else {
            s.resetSetup();
            return true;
        }
    }
    
    public final boolean enableKeyEvents(String sketchName, boolean value) {
        Sketch s = sketches.findSketch(sketchName);
        if(s == null)
            return false;
        else {
            s.setReceivingKeyEvents(value);
            return true;
        }
    }
    
    public final boolean enableMouseEvents(String sketchName, boolean value) {
        Sketch s = sketches.findSketch(sketchName);
        if(s == null)
            return false;
        else {
            s.setReceivingMouseEvents(value);
            return true;
        }
    }
    
    /*************************************************************
     * Event System
     *************************************************************/
    
    /**
     * Add a new ChannelsChangedListener to the Event System
     * @param ccL 
     */
    public final void addChannelsChangedListener(ChannelsChangedListener ccL) {
        ccListener.add(ccL);
    }
    
    /**
     * Called by ChannelManagment Object
     */
    public final void fireChannelsChanged()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(ChannelsChangedListener listener : ccListener) {
                    try {
                        listener.channelsChanged();
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
        }).start();
    }
}
