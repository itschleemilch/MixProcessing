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
package de.itschleemilch.mixprocessing.welcome;

import de.itschleemilch.mixprocessing.load.PApplet2;
import java.awt.geom.Point2D;
import processing.core.PFont;

/**
 * Simple Sketch to have an animated screen after booting.
 *
 * @author Sebastian Schleemilch
 */
public class WelcomeSketch extends PApplet2 {
    PFont myFont;
    int colorWheel = 0;
    final int AGENT_COUNT = 200;
    
    Point2D.Float[] agents = new Point2D.Float[AGENT_COUNT];
    Point2D.Float[] agents_move = new Point2D.Float[AGENT_COUNT];
    
    @Override
    public void setup() {
        size(800, 600);
        frameRate(25);
        colorMode(HSB, 0xFF);
        smooth();
        
        myFont = createFont("Georgia", 50);
        
        for(int i = 0; i < agents.length; i++) {
            agents[i] = new Point2D.Float(random(width), random(height));
            agents_move[i] = new Point2D.Float(-0.3f+random(0.6f), 
                    -0.3f+random(0.6f));
        }
    }
    
    private void drawAgents() {
        ellipseMode(CENTER);
        fill(255);
        noStroke();
        // draw agents
        for(int i = 0; i < agents.length; i++) {
            Point2D.Float agent = agents[i];
            ellipse(agent.x, agent.y, 5, 5);
        }
        // draw interconnects
        noFill();
        stroke(255);
        
        for(int i1 = 0; i1 < agents.length; i1++) {
            Point2D.Float agent1 = agents[i1];
            for(int i2 = 0; i2 < agents.length; i2++) {
                Point2D.Float agent2 = agents[i2];
                float dist = (float)Math.abs( agent2.distance(agent1) );
                if(dist < 20f) {
                    strokeWeight(2);
                    stroke(255);
                    line(agent1.x, agent1.y, agent2.x, agent2.y);
                }
                else if(dist < 40f) {
                    float alpha = 100f*(dist-20f)/20f;
                    strokeWeight(1);
                    stroke(255, alpha);
                    line(agent1.x, agent1.y, agent2.x, agent2.y);
                }
            }
        }
    }
    
    private void moveAgents() {
        for(int i = 0; i < agents.length; i++) {
            Point2D.Float agent = agents[i];
            Point2D.Float move = agents_move[i];
            agent.x += move.x;
            agent.y += move.y;
            
            move.x += -0.3+random(0.6f);
            move.y += -0.3+random(0.6f);
            
            if(agent.x < -10) {
                agent.x = -10;
                move.x = random(0.3f);
            }
            else if(agent.x > width+10) {
                agent.x = width+10;
                move.x = 0-random(0.3f);
            }
            if(agent.y < -10) {
                agent.y = -10;
                move.y = random(0.3f);
            }
            else if(agent.y > height+10) {
                agent.y = height+10;
                move.y = 0-random(0.3f);
            }
            
            /* Align to mouse */
            float dX = mouseX - agent.x;
            float dY = mouseY - agent.y;
            agent.x += dX*0.01;
            agent.y += dY*0.01;
        }
    }

    @Override
    public void draw() {
      smooth();
      background(colorWheel, 0xAA, 0xB0);
      
      drawAgents();
      
      fill(0xFF);
      noStroke();
      textAlign(CENTER);
      textFont(myFont);
      text("MixProcessing", width/2, height/2+20);
      
      // simulate occular
      noFill();
      stroke(0xFF, 20);
      ellipseMode(CENTER);
      float dia = height * 0.7f;
      strokeWeight(80);
      ellipse(width/2, height/2, dia, dia);
      
      
      // data prepreation
      moveAgents();
      if(colorWheel < 0xFF)
        colorWheel++;
      else
        colorWheel = 0;      
    }
    
    private static final long serialVersionUID = 1L;
}
