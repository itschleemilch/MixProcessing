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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Supply the whole application with static internet resources.
 *
 * @author Sebastian Schleemilch
 */
public class InternetShortcuts {
    
    /**
     * Opens an internet link with user's default web browser.
     * @param url
     * @return suceess of opening (false=error)
     */
    public static boolean openShortcut(String url) {
        if(Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (URISyntaxException | IOException e) {
                return false;
            }
            return true;
        }
        else {
            return false;
        }
    }
    
    public static final String USER_MANUAL = "https://github.com/itschleemilch/MixProcessing/blob/master/documentation/user/user_manual.md";
    
    public static final String PROJECT_HOME = "https://github.com/itschleemilch/MixProcessing";
    public static final String PROJECT_WEBSITE = "http://itschleemilch.github.io/MixProcessing/";
    
    public static final String PROCESSING_DOWNLOAD = "https://processing.org/download/";
}
