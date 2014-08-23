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
package de.itschleemilch.mixprocessing.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

/**
 * Supply the whole application with static internet resources.
 *
 * @author Sebastian Schleemilch
 */
public class FolderShortcuts {
    
    /**
     * Opens the local explorer with the given path.
     * @param path
     * @return 
     */
    public static boolean openShortcut(File path) {
        if(Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(path);
            } catch (IOException e) {
                return false;
            }
            return true;
        }
        else {
            return false;
        }
    }
}
