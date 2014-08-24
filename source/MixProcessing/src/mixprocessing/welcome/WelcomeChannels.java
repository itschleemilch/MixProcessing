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
package mixprocessing.welcome;

import mixprocessing.Main;
import mixprocessing.RenderFrame;
import mixprocessing.channels.ChannelManagement;
import java.awt.geom.Ellipse2D;

/**
 * Adds welcome channel(s) to the ChannelManger. Used at booting.
 *
 * @author Sebastian Schleemilch
 * @see Main
 */
public class WelcomeChannels {
    
    public static final void addWelcomeChannels(ChannelManagement cman) {
        int dia = ( 10 * Math.min(
                RenderFrame.STARTUP_WIDTH, RenderFrame.STARTUP_HEIGHT) ) / 13;
        int x = (RenderFrame.STARTUP_WIDTH-dia)/2;
        int y = (RenderFrame.STARTUP_HEIGHT-dia)/2;
        Ellipse2D.Float ellipse = new Ellipse2D.Float(x, y, dia, dia);
        cman.addChannel(ellipse).setChannelName(CHANNEL_NAMES[0]);
    }
    public static String[] CHANNEL_NAMES = {"circle1"};
}
