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
package mixprocessing.load;

import processing.core.PApplet;

/**
 * Parent class for in-memory compiled sketches
 *
 * @author Sebastian Schleemilch
 * @see SketchCompiler
 */
public class PApplet2 extends PApplet {

    public PApplet2() {
        frameCount = 0;
    }

    /**
     * Override to prevent crash during sketch setup.
     * @param w
     * @param h 
     */
    @Override
    public void size(int w, int h) {
    }

    /**
     * Override to prevent crash during sketch setup.
     * @param w
     * @param h
     * @param renderer 
     */
    @Override
    public void size(int w, int h, String renderer) {
    }

    /**
     * Override to prevent crash during sketch setup.
     * @param w
     * @param h
     * @param renderer
     * @param path 
     */
    @Override
    public void size(int w, int h, String renderer, String path) {
    }
    
    
    private static final long serialVersionUID = 1L;
}
