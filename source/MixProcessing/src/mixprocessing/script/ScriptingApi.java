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
package mixprocessing.script;

import mixprocessing.EventManager;
import mixprocessing.MixRenderer;
import mixprocessing.RenderFrame;
import mixprocessing.channels.ChannelManagement;
import mixprocessing.channels.GroupChannel;
import mixprocessing.channels.SingleChannel;
import mixprocessing.load.SketchCompiler;
import mixprocessing.sketches.Sketch;
import mixprocessing.sketches.Sketches;
import java.awt.Shape;
import java.awt.event.KeyEvent;
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
        this.channels.eventManager = events;
        this.sketches = renderer.getSketches();
    }
    
    /*************************************************************
     * Public access to management objects
     *************************************************************/

    /**
     * Returns the channel manager
     * @return 
     */
    @ApiMethodInfo(category = "private", description = "", ignore = true)
    public final ChannelManagement getChannels() {
        return channels;
    }

    /**
     * Returns the output renderer
     * @return 
     */
    @ApiMethodInfo(category = "private", description = "", ignore = true)
    public final MixRenderer getRenderer() {
        return renderer;
    }

    /**
     * Returns the sketch manager
     * @return 
     */
    @ApiMethodInfo(category = "private", description = "", ignore = true)
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
    @ApiMethodInfo(category = "Channels", description = "Create Group")
    public final boolean channelCreateGroup(String newName, String ... sourceChannels) {
        /* Collect shapes */
        ArrayList<Shape> sourceShapes = new ArrayList<>();
        for (String sourceChannel : sourceChannels) {
            SingleChannel channel = channels.findChannel(sourceChannel);
            if(channel.getShape() != null) {
                sourceShapes.add( channel.getShape() );
            }
        }
        /* Make group */
        if(sourceShapes.isEmpty()) {
            return false;
        }
        else {
            GroupChannel group = channels.addGroupChannel();
            for(Shape sourceShape : sourceShapes) {
                group.addGroupElement(sourceShape);
            }
            group.setChannelName(newName);
            events.fireChannelsChanged();
            return true;
        }
    }
    
    /**
     * Enables a channel for rendering to the final output.
     * @param channelName
     * @return 
     */
    @ApiMethodInfo(category = "Channels", description = "Switch On")
    public final boolean channelOn(String channelName) {
        SingleChannel c = channels.findChannel(channelName);
        if(c == null) {
            return false;
        }
        else
        {
            c.setEnabled(true);
            return true;
        }
    }
    
    /**
     * Disables a channel for the rendering to the final output.
     * @param channelName
     * @return 
     */
    @ApiMethodInfo(category = "Channels", description = "Switch Off")
    public final boolean channelOff(String channelName) {
        SingleChannel c = channels.findChannel(channelName);
        if(c == null) {
            return false;
        }
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
    @ApiMethodInfo(category = "Channels", description = "Set Editing Mode")
    public final boolean channelEditing() {
        channels.setPreviewChannelOutlines(true);
        return true;
    }
    
    /**
     * Exit channel editing mode
     * @return true
     */
    @ApiMethodInfo(category = "Channels", description = "Set Normal Mode")
    public final boolean channelNormal()
    {
        channels.setPreviewChannelOutlines(false);
        return true;
    }
    
    /**
     * Returns if the current mode is editing mode.
     * @return
     */
    @ApiMethodInfo(category = "Channels", description = "Get Editing Mode")
    public final boolean channelIsEditing() {
        return channels.isPreviewChannelOutlines();
    }
    
    /**
     * Gives a channel a new name.
     * @param oldName
     * @param newName
     * @return 
     */
    @ApiMethodInfo(category = "Channels", description = "Rename")
    public final boolean channelRename(String oldName, String newName) {
        SingleChannel c = channels.findChannel(oldName);
        if(c == null) {
            return false;
        }
        else
        {
            c.setChannelName(newName);
            events.fireChannelsChanged();
            return true;
        }
    }
    
    /**
     * Sets the paintBlackFlag of the channel. The next paint-cycle will
     * fill the area with black.
     * @param channelName
     * @return 
     */
    @ApiMethodInfo(category = "Channels", description = "Fill Black")
    public final boolean channelBlacking(String channelName) {
        SingleChannel c = channels.findChannel(channelName);
        if(c == null) {
            return false;
        }
        else
        {
            c.paintBlackFlag = true;
            return true;
        }
    }
    
    /**
     * Remove a channel from the output and all links from sketches to it.
     * @param channelName
     * @return 
     */
    @ApiMethodInfo(category = "Channels", description = "Remove")
    public final boolean channelRemove(String channelName) {
        SingleChannel c = channels.findChannel(channelName);
        if(c == null) {
            return false;
        }
        else
        {
            channels.removeChannel(c);
            events.fireChannelsChanged();
            return true;
        }
    }
    
    /*************************************************************
     * Rendering Control
     *************************************************************/
    
    /**
     * Performes a full refresh including a full blacking of the screen.
     * @return true
     */
    @ApiMethodInfo(category = "Renderer", description = "Force Refresh")
    public final boolean rendererForceRefresh() {
        renderer.setForceRefresh();
        return true;
    }
    
    /**
     * Returns the set maximum frame rate of the renderer
     * @return 
     */
    @ApiMethodInfo(category = "Renderer", description = "Get Frame Rate")
    public float rendererGetFrameRate() {
        return renderer.getMaxFrameRate();
    }
    
    /**
     * Sets the absolut maximum frame rate of the renderer.
     * @param frameRate 
     * @return true
     */
    @ApiMethodInfo(category = "Renderer", description = "Set Frame Rate")
    public boolean rendererSetFrameRate(float frameRate) {
        renderer.setMaxFrameRate(frameRate);
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
    @ApiMethodInfo(category = "Sketches", description = "Set Opacity")
    public final boolean sketchAlpha(String sketchName, float value) {
        Sketch s = sketches.findSketch(sketchName);
        if(s != null) {
            s.setAlpha(value);
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * 
     * @param sketchName
     * @param value
     * @return 
     * @see EventManager#sketchAlpha(java.lang.String, float) 
     */
    @ApiMethodInfo(category = "Sketches", description = "Set Opacity", ignore = true)
    public final boolean sketchAlpha(String sketchName, double value) {
        return sketchAlpha(sketchName, (float)value);
    }
    
    /**
     * Updates the sketch-output channel association.
     * Attention: Restarts Sketch!
     * @param sketchName
     * @param channelName
     * @return 
     */
    @ApiMethodInfo(category = "Sketches", description = "Set Channel")
    public final boolean sketchSetChannelRestart(String sketchName, String channelName) {
        boolean returnValue = sketchSetChannel(sketchName, channelName);
        sketchRestart(sketchName);
        return returnValue;
    }
    
    /**
     * Updates the sketch-output channel association.
     * Attention: Does not restarting the Sketch!
     * @param sketchName
     * @param channelName
     * @return 
     */
    @ApiMethodInfo(category = "Sketches", description = "Set Channel (no Restart)")
    public final boolean sketchSetChannel(String sketchName, String channelName) {
        Sketch s = sketches.findSketch(sketchName);
        SingleChannel c = channels.findChannel(channelName);
        if(s != null && c != null) {
            channels.setSketchChannel(s, c);
            s.resetSetup();
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Deletes an sketch and it's link to an output channel.
     * @param sketchName
     * @return 
     */
    @ApiMethodInfo(category = "Sketches", description = "Remove")
    public final boolean sketchRemove(String sketchName) {
        Sketch s = sketches.findSketch(sketchName);
        if(s == null) {
            return false;
        }
        else {
            channels.unsetSketchChannel(s);
            events.fireSketchesChanged();
            return true;
        }
    }
    
    /**
     * Forces an restart of the given sketch (calls setup()).
     * @param sketchName
     * @return 
     */
    @ApiMethodInfo(category = "Sketches", description = "Restart")
    public final boolean sketchRestart(String sketchName)
    {
        Sketch s = sketches.findSketch(sketchName);
        if(s == null) {
            return false;
        }
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
    @ApiMethodInfo(category = "Sketches", description = "Set Variable")
    public final boolean sketchSetVar(String sketchName, String varName, Object newValue) {
        Sketch s = sketches.findSketch(sketchName);
        if(s == null || s.getInstance() == null) {
            return false;
        }
        else {
            Object obj = s.getInstance();
            try {
                Field field = obj.getClass().getDeclaredField(varName);
                field.setAccessible(true);
                Class<?> type = field.getType();
                
                if(type.equals(String.class)) {
                    field.set(obj, newValue.toString());
                }
                else if(type.equals(boolean.class)) {
                    field.setBoolean(obj, (boolean)newValue);
                }
                else if(type.equals(byte.class)) {
                    field.setByte(obj, (byte)newValue);
                }
                else if(type.equals(char.class)) {
                    field.setChar(obj, (char)newValue);
                }
                else if(type.equals(double.class)) {
                    field.setDouble(obj, (double)newValue);
                }
                else if(type.equals(float.class)) {
                    field.setFloat(obj, (float)newValue);
                }
                else if(type.equals(int.class)) {
                    if(newValue instanceof Double) {
                        field.setInt(obj, ((Double)newValue).intValue() );
                    }
                    else {
                        field.setInt(obj, (int)newValue);
                    }
                }
                else if(type.equals(long.class)) {
                    field.setLong(obj, (long)newValue);
                }
                else if(type.equals(short.class)) {
                    field.setShort(obj, (short)newValue);
                }
                else {
                    field.set(obj, newValue);
                }
                
                return true;
            } 
            catch (NoSuchFieldException e1) {
                System.err.printf("Variable does not exist: %s in sketch %s\n", 
                        varName, sketchName);
                return false;
            } 
            catch (IllegalAccessException e2) {
                System.err.printf("Variable's type can not be set: %s in sketch %s%s\n", 
                        varName, sketchName);
                return false;
            }
            catch (IllegalArgumentException | SecurityException e3) {
                e3.printStackTrace(System.err);
                return false;
            }
        }
    }
    
    /**
     * Get the value of a sketch's variable 
     * @param sketchName
     * @param varName
     * @return 
     */
    @ApiMethodInfo(category = "Sketches", description = "Get Variable")
    public final Object sketchGetVar(String sketchName, String varName) {
        Sketch s = sketches.findSketch(sketchName);
        if(s == null || s.getInstance() == null) {
            return null;
        }
        else {
            Object obj = s.getInstance();
            try {
                Field field = obj.getClass().getDeclaredField(varName);
                field.setAccessible(true);
                Class<?> type = field.getType();
                
                if(type.equals(boolean.class)) {
                    return field.getBoolean(obj);
                }
                else if(type.equals(byte.class)) {
                    return field.getByte(obj);
                }
                else if(type.equals(char.class)) {
                    return field.getChar(obj);
                }
                else if(type.equals(double.class)) {
                    return field.getDouble(obj);
                }
                else if(type.equals(float.class)) {
                    return field.getFloat(obj);
                }
                else if(type.equals(int.class)) {
                    return field.getInt(obj);
                }
                else if(type.equals(long.class)) {
                    return field.getLong(obj);
                }
                else if(type.equals(short.class)) {
                    return field.getShort(obj);
                }
                else {
                    return field.get(obj);
                }
            } 
            catch (NoSuchFieldException e1) {
                System.err.printf("Variable does not exist: %s in sketch %s\n", 
                        varName, sketchName);
                return null;
            } 
            catch (IllegalAccessException | IllegalArgumentException | 
                    SecurityException e3) {
                e3.printStackTrace(System.err);
                return null;
            }
        }
    }
    
    /**
     * Lists all sketch-specific variables with their names.
     * Does not list any PApplet variable names.
     * @param sketchName
     * @return 
     */
    @ApiMethodInfo(category = "Sketches", description = "Get Variables")
    public String[] sketchGetVars(String sketchName) {
        Sketch s = sketches.findSketch(sketchName);
        if(s == null || s.getInstance() == null) {
            return null;
        }
        else {
            ArrayList<String> vars = new ArrayList<>();
            Field[] fields = s.getInstance().getClass().getDeclaredFields();
            for(Field field : fields) {
                vars.add(field.getName());
            }
            return vars.toArray(new String[0]);
        }
    }
    
    /**
     * Returns a sketch's frame rate value or -1f (not found, not inited)
     * @param sketchName
     * @return 
     */
    @ApiMethodInfo(category = "Sketches", description = "Frame Rate")
    public float sketchGetFrameRate(String sketchName) {
        Sketch s = sketches.findSketch(sketchName);
        if(s == null || s.getInstance() == null) {
            return -1f;
        }
        else {
            return s.getInstance().frameRate;
        }
    } 
    
    /**
     * Returns a sketch's frame count value or -1f (not found, not inited)
     * @param sketchName
     * @return 
     */
    @ApiMethodInfo(category = "Sketches", description = "Get Frame Count")
    public int sketchGetFrameCount(String sketchName) {
        Sketch s = sketches.findSketch(sketchName);
        if(s == null || s.getInstance() == null) {
            return -1;
        }
        else {
            return s.getInstance().frameCount;
        }
    }
    
    /*************************************************************
     * User Events
     *************************************************************/
    
    /**
     * Enables or disables key events for the specific sketch
     * @param sketchName
     * @param value
     * @return 
     */
    @ApiMethodInfo(category = "Sketch Events", description = "En-/Disables Key Events")
    public final boolean sketchKeyEventsOn(String sketchName, boolean value) {
        Sketch s = sketches.findSketch(sketchName);
        if(s == null) {
            return false;
        }
        else {
            s.setReceivingKeyEvents(value);
            return true;
        }
    }
    
    /**
     * Enables or disables mouse events for the specific sketch
     * @param sketchName
     * @param value
     * @return 
     */
    @ApiMethodInfo(category = "Sketch Events", description = "En-/Disables Mouse Events")
    public final boolean sketchMouseEventsOn(String sketchName, boolean value) {
        Sketch s = sketches.findSketch(sketchName);
        if(s == null) {
            return false;
        }
        else {
            s.setReceivingMouseEvents(value);
            return true;
        }
    }
    
    /*************************************************************
     * System functions
     *************************************************************/
    
    /**
     * Loads, compiles and registers a Processing Sketch from source code.
     * @param sketchPath Path to Sketch's project folder or PDE file.
     * @return sucess of process.
     */
    @ApiMethodInfo(category = "System", description = "Load Processing Sketch")
    public final boolean systemLoad(String sketchPath) {
        final File sketchFile = new File(sketchPath);
        
        final File sketchFolder;
        if(sketchFile.isDirectory()) {
            sketchFolder = sketchFile;
        }
        else {
            sketchFolder = sketchFile.getParentFile();
        }
        
        if(sketchFile.exists()) {
            SketchCompiler compiler =  new SketchCompiler();
            Class<?> result = compiler.compileSketch(new File(sketchPath));
            if(result != null) {
                Sketch newSketch = new Sketch(result);
                newSketch.createInstance(outputWindow, sketchFolder.getAbsolutePath());
                getSketches().addSketch(newSketch);
                events.fireSketchesChanged();
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    
    /**
     * Returns an array of all sketches
     * @return Sketch[] Array
     */
    @ApiMethodInfo(category = "System", description = "List Sketches")
    public final Sketch[] systemListSketches() {
        return getSketches().getAllSketches();
    }
    
    /**
     * Returns an array of all channels
     * @return 
     */
    @ApiMethodInfo(category = "System", description = "List Channels")
    public final SingleChannel[] systemListChannels() {
        return getChannels().getAllChannels();
    }
    
    /**
     * Presses the virtual keyboard for all receiving sketches.
     * @param key pressed key as char
     * @return 
     */
    @ApiMethodInfo(category = "System", description = "Key: Press")
    public final boolean systemKeyPressed(char key) {
        getSketches().keyEvent(new KeyEvent(null, 0, 0, 0, key, key), 0);
        return true;
    }
    
    /**
     * Releases the virtual keyboard for all receiving sketches.
     * @param key released key as char
     * @return 
     */
    @ApiMethodInfo(category = "System", description = "Key: Release")
    public final boolean systemKeyReleased(char key) {
        getSketches().keyEvent(new KeyEvent(null, 0, 0, 0, key, key), 1);
        return true;
    }
    
    /**
     * Types the virtual keyboard for all receiving sketches.
     * @param key typed key as char
     * @return 
     */
    @ApiMethodInfo(category = "System", description = "Key: Typed")
    public final boolean systemKeyTyped(char key) {
        getSketches().keyEvent(new KeyEvent(null, 0, 0, 0, key, key), 2);
        return true;
    }
    
    /**
     * Moves the virtual mouse to the given position for all receiving sketches.
     * @param x
     * @param y
     * @return 
     */
    @ApiMethodInfo(category = "System", description = "Mouse: Set Position")
    public final boolean systemSetMouse(int x, int y) {
        getSketches().mouseMoved(x, y, false);
        return true;
    }
    
    /**
     * Calls the sketches' mouseClicked() method for all receiving sketches.
     * @return 
     */
    @ApiMethodInfo(category = "System", description = "Mouse: Click")
    public final boolean systemDoMouseClick() {
        getSketches().mouseEvent(false, false, true);
        return true;
    }
    
    /**
     * Log Text to the standard log output.
     * @param text
     * @return 
     */
    @ApiMethodInfo(category = "System", description = "Log Message")
    public boolean systemPrintln(String text) {
        System.out.println(text);
        return true;
    }
    
    /**
     * Freeze the current thread for the given milliseconds.
     * @param ms
     * @return 
     */
    @ApiMethodInfo(category = "System", description = "Sleep")
    public boolean systemSleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
        return true;
    }
    
} // End of ScriptingApi
