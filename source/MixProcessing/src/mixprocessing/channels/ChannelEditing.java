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
package mixprocessing.channels;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Handles the channel editing.
 *
 * @author Sebastian Schleemilch
 */
public class ChannelEditing implements MouseListener {
    public enum STATES {WAITING, START, LINE_TO, END}
    private final ChannelManagement channels;
    private STATES state = STATES.WAITING;
    private GeneralPath path;    

    public ChannelEditing(ChannelManagement channels) {
        this.channels = channels;
        reinit();
    }

    /**
     * Returns the internal state of the path-editor
     * @return 
     */
    public final STATES getState() {
        return state;
    }
    
    private void reinit() {
        path = new GeneralPath();
        state = STATES.WAITING;
    }
    
    /**
     * User wants to draw a new path
     */
    private void startNewPath()
    {
        reinit();
    }
    
    private void endPath() {
        channels.addChannel(path);
        reinit();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(!channels.isPreviewChannelOutlines()) {
            return;
        }
        
        if(state == STATES.WAITING) {
            if(e.getClickCount() > 1) { // start new click
                startNewPath();
                path.moveTo(e.getX(), e.getY());
                state = STATES.LINE_TO;
            }
        }
        else if(state == STATES.START) { // currently not used
            path.moveTo(e.getX(), e.getY());
            state = STATES.LINE_TO;
        }
        else if(state == STATES.LINE_TO)
        {
            if(e.getClickCount() < 2) {
                path.lineTo(e.getX(), e.getY());
            }
            else {
                path.lineTo(e.getX(), e.getY());
                path.closePath();
                state = STATES.END;
                endPath();
            }
        }
    }
    
    /**
     * Draws the current data within the editor
     * @param g 
     */
    public final void paintEditorPath(Graphics2D g) {
        if(state != STATES.WAITING)
        {
            Point2D lastPoint = path.getCurrentPoint();
            if(lastPoint != null) {
                drawControlPoint(g, 
                        (float)lastPoint.getX(), (float)lastPoint.getY());
            }
            g.setColor(Color.MAGENTA);
            g.draw(path);
        }
    }
    
    
    
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
    }
    
}
