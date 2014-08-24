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
package mixprocessing.webserver;

/**
 * Helper class to get a content type from a given file name.
 *
 * @author Sebastian Schleemilch
 */
public class MIME {
    
    /**
     * Returns the content type for the specific filename's extension.
     * Usage for HTTP Header generation.
     * @param fileName
     * @return content type or default(application/octet-stream)
     */
    public static final String findMime(String fileName) {
        String fileExtension;
        int extensionStart = fileName.lastIndexOf(".");
        if(extensionStart > -1) {
            fileExtension = fileName.substring(extensionStart+1).toLowerCase();
        }
        else { // has no file extension
            return "application/octet-stream";
        }
        switch (fileExtension) {
            case "css":
                return "text/css";
            case "gif":
                return "image/gif";
            case "html":
            case "htm":
                return "text/html; charset=UTF-8";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "js":
                return "text/javascript";
            case "png":
                return "image/png";
            default:
                return "application/octet-stream";
        }
    }
}
