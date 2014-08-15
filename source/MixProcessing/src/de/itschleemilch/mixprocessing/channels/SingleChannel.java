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

import java.awt.Shape;

/**
 * Represents an single output channel
 *
 * @author Sebastian Schleemilch
 */
public class SingleChannel {
    private Shape shape = null;
    private String channelName;
    private boolean enabled = true;
    public boolean paintBlackFlag = false;

    public SingleChannel(int runningID) {
        this.channelName = "channel"+runningID;
    }

    /**
     * Wheather the channel is drawn or not
     * @return 
     */
    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Internal name for the user, can be edited
     * @return 
     */
    public final String getChannelName() {
        return channelName;
    }

    public final void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    /**
     * Returns the Shape object, that represents the output area. 
     * @return Shape or null
     */
    public final Shape getShape() {
        return shape;
    }

    public final void setShape(Shape shape) {
        this.shape = shape;
    }
}
