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
    public final boolean channelOn(String name) {
        SingleChannel c = channels.findChannel(name);
        if(c == null)
            return false;
        else
        {
            c.setEnabled(true);
            return true;
        }
    }
    
    public final boolean channelOff(String name) {
        SingleChannel c = channels.findChannel(name);
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
    
    public final boolean channelRemove(String name) {
        SingleChannel c = channels.findChannel(name);
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
    public final boolean restartSketch(String name)
    {
        Sketch s = sketches.findSketch(name);
        if(s == null)
            return false;
        else {
            s.resetSetup();
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
