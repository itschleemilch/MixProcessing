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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Method;

/**
 *
 * @author Sebastian Schleemilch
 */
public class ChannelEditing {
    
    private static void drawControlPoint(Graphics2D g, float x, float y)
    {
        g.fill( new Rectangle2D.Float(x-4, y-4, 8, 8) );
    }
    
    public static void drawControlPoints(Graphics2D g, Shape s)
    {
        Rectangle2D bounds = s.getBounds2D();
        g.setColor(Color.MAGENTA);
        g.draw(bounds);
        drawControlPoint(g, (float)(bounds.getX()), (float)(bounds.getY()) );
        drawControlPoint(g, (float)(bounds.getX()+bounds.getWidth()), (float)(bounds.getY()) );
        drawControlPoint(g, (float)(bounds.getX()+bounds.getWidth()), (float)(bounds.getY()+bounds.getHeight()) );
        drawControlPoint(g, (float)(bounds.getX()), (float)(bounds.getY()+bounds.getHeight()) );
        
        Method[] methods = s.getClass().getMethods();
        for(int i = 0; i < methods.length; i++)
        {
            if(methods[i].getName().startsWith("set"))
            {
                System.out.println(methods[i].getName());
            }
        }
    }
    
}
