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

import de.itschleemilch.mixprocessing.events.ChannelsChangedListener;
import de.itschleemilch.mixprocessing.events.SketchesChangedListener;
import de.itschleemilch.mixprocessing.load.SketchCompiler;
import de.itschleemilch.mixprocessing.script.ScriptingApi;
import de.itschleemilch.mixprocessing.sketches.Sketch;
import java.io.File;
import java.util.ArrayList;

/**
 * MixProcessing's global event registry and implementation of skripting interface
 *
 * @author Sebastian Schleemilch
 */
public class EventManager extends ScriptingApi {
    
    
    private final ArrayList<ChannelsChangedListener> ccListener = new ArrayList<>();
    private final ArrayList<SketchesChangedListener> scListener = new ArrayList<>();

    public EventManager(RenderFrame outputWindow, MixRenderer renderer) {
        super(outputWindow, renderer);        
        this.events = this;
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
    
    /*************************************************************
     * User Events
     *************************************************************/
    
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
     * Adds a new ChannelsChangedListener to the Event System
     * @param ccL 
     */
    public final void addChannelsChangedListener(ChannelsChangedListener ccL) {
        ccListener.add(ccL);
    }
    
    /**
     * Fires a new ChannelChanged Event ((new, deleted, replaced)
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
    
    
    /**
     * Adds a new SketchesChangedListener to the Event System
     * 
     * @param scL 
     */
    public final void addSketchesChangedListener(SketchesChangedListener scL) {
        scListener.add(scL);
    }
    
    /**
     * Fires a new SketchChanged Event (new, deleted, replaced)
     */
    public final void fireSketchesChanged()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(SketchesChangedListener listener : scListener) {
                    try {
                        listener.sketchesChanged();
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
        }).start();
    }
}
