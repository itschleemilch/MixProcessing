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
package mixprocessing.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Easy way to store preferences. The preferences are stored at the working dir, 
 * subfolder preferences.
 *
 * @author Sebastian Schleemilch
 */
public class SinglePreference {
    
    /**
     * Storage folder for the preferences.
     * @return 
     */
    public static File getParentFolder() {
        File parent = new File(BasePath.getBasePath(), "preferences");
        parent.mkdirs();
        return parent;
    }
    /**
     * Get a preference by its key.
     * @param key
     * @param defaultValue
     * @return value or defaultValue if not set / error.
     */
    public static String getPreference(String key, String defaultValue)
    {
        String returnValue = defaultValue;
        File sourceFile = new File(getParentFolder(), key + ".txt");
        if(sourceFile.exists() && sourceFile.isFile()) { // read
            BufferedReader reader = null;
            try {
                reader = new BufferedReader( new InputStreamReader( 
                        new FileInputStream(sourceFile), "UTF-8" ) );
                returnValue = reader.readLine();
            } 
            catch (IOException e) {
                e.printStackTrace(System.err);
                returnValue = defaultValue;
            }
            finally {
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
        }
        else {// File does not exist
            returnValue = defaultValue;
        }
        return returnValue;
    }
    
    /**
     * Sets a preference.
     * @param key
     * @param value 
     */
    public static void setPreference(String key, String value) {
        File targetFile = new File(getParentFolder(), key + ".txt");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter( new OutputStreamWriter(
                    new FileOutputStream(targetFile), "UTF-8" ) );
            writer.write(value);
            writer.flush();
            writer.close();
        } 
        catch (IOException e) {
            e.printStackTrace(System.err);
        }
        finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }
}
