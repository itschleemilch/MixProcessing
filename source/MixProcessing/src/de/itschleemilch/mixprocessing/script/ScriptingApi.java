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
package de.itschleemilch.mixprocessing.script;

import de.itschleemilch.mixprocessing.EventManager;
import de.itschleemilch.mixprocessing.MixRenderer;
import de.itschleemilch.mixprocessing.RenderFrame;
import de.itschleemilch.mixprocessing.channels.ChannelManagement;
import de.itschleemilch.mixprocessing.channels.GroupChannel;
import de.itschleemilch.mixprocessing.channels.SingleChannel;
import de.itschleemilch.mixprocessing.load.SketchCompiler;
import de.itschleemilch.mixprocessing.sketches.Sketch;
import de.itschleemilch.mixprocessing.sketches.Sketches;
import java.awt.Shape;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * MixProcessing's scripting API.
 * Important: All methods have to return a value. 
 * Reason: Remote API returns a value to each call.
 *
 * @author Sebastian Schleemilch
 */
public class ScriptingApi {
    protected EventManager events;
    protected final RenderFrame outputWindow;
    protected final MixRenderer renderer;
    protected final ChannelManagement channels;
    protected final Sketches sketches;
    
    /**
     * Only called by EventManager
     * @param outputWindow
     * @param renderer 
     */
    public ScriptingApi(RenderFrame outputWindow, MixRenderer renderer) {
        this.events = null;
        this.outputWindow = outputWindow;
        this.renderer = renderer;
        this.channels = renderer.getChannels();
        this.sketches = renderer.getSketches();
    }
    
    /**
     * Public Constructor
     * @param events
     * @param outputWindow
     * @param renderer 
     */
    public ScriptingApi(EventManager events, RenderFrame outputWindow, MixRenderer renderer) {
        this.events = events;
        this.outputWindow = outputWindow;
        this.renderer = renderer;
        this.channels = renderer.getChannels();
        this.sketches = renderer.getSketches();
    }
    
    /*************************************************************
     * Public access to management objects
     *************************************************************/

    /**
     * Returns the channel manager
     * @return 
     */
    public final ChannelManagement getChannels() {
        return channels;
    }

    /**
     * Returns the output renderer
     * @return 
     */
    public final MixRenderer getRenderer() {
        return renderer;
    }

    /**
     * Returns the sketch manager
     * @return 
     */
    public final Sketches getSketches() {
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
        if(sourceShapes.isEmpty())
            return false;
        else {
            GroupChannel group = channels.addGroupChannel();
            for(Shape sourceShape : sourceShapes) {
                group.addGroupElement(sourceShape);
            }
            group.setChannelName(newName);
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
     * @return true
     */
    public final boolean channelEditing() {
        channels.setPreviewChannelOutlines(true);
        return true;
    }
    
    /**
     * Exit channel editing mode
     * @return true
     */
    public final boolean channelNormal()
    {
        channels.setPreviewChannelOutlines(false);
        return true;
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
    public final boolean forceRefresh() {
        renderer.setForceRefresh();
        while(renderer.isForceRefreshWaiting())
            Thread.yield();
        return true;
    }
    
    /*************************************************************
     * Sketch Control
     *************************************************************/
    
    /**
     * Sets the alpha value (how opaque the sketch is drawn.)
     * @param sketchName
     * @param value new opacity (0: transparent, 1: opaque)
     * @return 
     */
    public final boolean sketchAlpha(String sketchName, float value) {
        Sketch s = sketches.findSketch(sketchName);
        if(s != null) {
            s.setAlpha(value);
            return true;
        }
        else
            return false;
    }
    
    /**
     * 
     * @param sketchName
     * @param value
     * @return 
     * @see EventManager#sketchAlpha(java.lang.String, float) 
     */
    public final boolean sketchAlpha(String sketchName, double value) {
        return sketchAlpha(sketchName, (float)value);
    }
    
    /**
     * Updates the sketch<->output channel association.
     * Attention: Restarts Sketch!
     * @param sketchName
     * @param channelName
     * @return 
     */
    public final boolean sketchOutput(String sketchName, String channelName) {
        boolean returnValue = sketchOutputNoRestart(sketchName, channelName);
        sketchRestart(sketchName);
        return returnValue;
    }
    
    /**
     * Updates the sketch<->output channel association.
     * Attention: Does not restarting the Sketch!
     * @param sketchName
     * @param channelName
     * @return 
     */
    public final boolean sketchOutputNoRestart(String sketchName, String channelName) {
        Sketch s = sketches.findSketch(sketchName);
        SingleChannel c = channels.findChannel(channelName);
        if(s != null && c != null) {
            channels.setSketchChannel(s, c);
            s.resetSetup();
            return true;
        }
        else return false;
    }
    
    public final boolean sketchRemove(String sketchName) {
        Sketch s = sketches.findSketch(sketchName);
        if(s == null)
            return false;
        else {
            channels.unsetSketchChannel(s);
            return true;
        }
    }
    
    public final boolean sketchRestart(String sketchName)
    {
        Sketch s = sketches.findSketch(sketchName);
        if(s == null)
            return false;
        else {
            s.resetSetup();
            return true;
        }
    }
    
    
    /**
     * Set sketch's variables. Includes public, private and protected ones.
     * 
     * @param sketchName Sketch, which's variable should be modified.
     * @param varName Name of variable to be modified.
     * @param newValue New value for the modified variable.
     * @return 
     */
    public final boolean sketchSetVar(String sketchName, String varName, Object newValue) {
        Sketch s = sketches.findSketch(sketchName);
        if(s == null || s.getInstance() == null)
            return false;
        else {
            Object obj = s.getInstance();
            Class type = null;
            try {
                Field field = obj.getClass().getDeclaredField(varName);
                field.setAccessible(true);
                type = field.getType();
                
                if(type.equals(String.class))
                    field.set(obj, newValue.toString());
                
                else if(type.equals(boolean.class))
                    field.setBoolean(obj, (boolean)newValue);
                
                else if(type.equals(byte.class))
                    field.setByte(obj, (byte)newValue);
                
                else if(type.equals(char.class))
                    field.setChar(obj, (char)newValue);
                
                else if(type.equals(double.class))
                    field.setDouble(obj, (double)newValue);
                
                else if(type.equals(float.class))
                    field.setFloat(obj, (float)newValue);
                
                else if(type.equals(int.class)) {
                    if(newValue instanceof Double)
                        field.setInt(obj, ((Double)newValue).intValue() );
                    else
                        field.setInt(obj, (int)newValue);
                }
                
                else if(type.equals(long.class))
                    field.setLong(obj, (long)newValue);
                
                else if(type.equals(short.class))
                    field.setShort(obj, (short)newValue);
                
                else
                    field.set(obj, newValue);
                
                return true;
            } 
            catch (NoSuchFieldException e1) {
                System.err.printf("Variable does not exist: %s in sketch %s\n", 
                        varName, sketchName);
                return false;
            } 
            catch (IllegalAccessException e2) {
                System.err.printf("Variable's type can not be set: %s in sketch %s, type: %s\n", 
                        varName, sketchName, type.getName());
                return false;
            }
            catch (Exception e3) {
                e3.printStackTrace(System.err);
                return false;
            }
        }
    }
    
    /*************************************************************
     * Environment
     *************************************************************/
    
    /**
     * Loads, compiles and registers a Processing Sketch from source code.
     * @param sketchPath Path to Sketch's project folder or PDE file.
     * @return sucess of process.
     */
    public final boolean environmentLoad(String sketchPath) {
        final File sketchFile = new File(sketchPath);
        final File sketchFolder = (sketchFile.isDirectory()) ? sketchFile : sketchFile.getParentFile();
        if(sketchFile.exists()) {
            SketchCompiler compiler =  new SketchCompiler();
            Class result = compiler.compileSketch(new File(sketchPath));
            if(result != null) {
                Sketch newSketch = new Sketch(result);
                newSketch.createInstance(outputWindow, sketchFolder.getAbsolutePath());
                getSketches().addSketch(newSketch);
                events.fireSketchesChanged();
                return true;
            }
            else
                return false;
        }
        else
            return false;
    }
    
} // End of ScriptingApi
