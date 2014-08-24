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

import mixprocessing.load.PApplet2;
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
    
    float[][] agents = new float[AGENT_COUNT][2];
    float[][] agents_move = new float[AGENT_COUNT][2];
    
    @Override
    public void setup() {
        size(800, 600);
        frameRate(25);
        colorMode(HSB, 0xFF);
        smooth();
        
        myFont = createFont("Georgia", 50);
        
        for(int i = 0; i < agents.length; i++) {
            agents[i][0] = random(width);
            agents[i][1] = random(height);
            agents_move[i][0] = -0.3f+random(0.6f);
            agents_move[i][1] = -0.3f+random(0.6f);
        }
    }
    
    private void drawAgents() {
        ellipseMode(CENTER);
        fill(255);
        noStroke();
        // draw agents
        for(int i = 0; i < agents.length; i++) {
            ellipse(agents[i][0], agents[i][1], 5, 5);
        }
        // draw interconnects
        noFill();
        stroke(255);
        
        for(int i1 = 0; i1 < agents.length; i1++) {
            float a1_x = agents[i1][0];
            float a1_y = agents[i1][1];
            for(int i2 = 0; i2 < agents.length; i2++) {
                float a2_x = agents[i2][0];
                float a2_y = agents[i2][1];
                double distSquared = Math.pow(a2_x-a1_x, 2) + Math.pow(a2_y-a1_y, 2);
                float dist = (float)Math.sqrt(distSquared);
                if(dist < 20f) {
                    strokeWeight(2);
                    stroke(255);
                    line(a1_x, a1_y, a2_x, a2_y);
                }
                else if(dist < 40f) {
                    float alpha = 100f*(dist-20f)/20f;
                    strokeWeight(1);
                    stroke(255, alpha);
                    line(a1_x, a1_y, a2_x, a2_y);
                }
            }
        }
    }
    
    private void moveAgents() {
        for(int i = 0; i < agents.length; i++) {
//            Point2D.Float agent = agents[i];
//            Point2D.Float move = agents_move[i];
            agents[i][0] += agents_move[i][0];
            agents[i][1] += agents_move[i][1];
            
            agents_move[i][0] += -0.3+random(0.6f);
            agents_move[i][1] += -0.3+random(0.6f);
            
            if(agents[i][0] < -10) {
                agents[i][0] = -10;
                agents_move[i][0] = random(0.3f);
            }
            else if(agents[i][0] > width+10) {
                agents[i][0] = width+10;
                agents_move[i][0] = 0-random(0.3f);
            }
            if(agents[i][1] < -10) {
                agents[i][1] = -10;
                agents_move[i][1] = random(0.3f);
            }
            else if(agents[i][1] > height+10) {
                agents[i][1]= height+10;
                agents_move[i][1] = 0-random(0.3f);
            }
            
            /* Align to mouse */
            float dX = mouseX - agents[i][0];
            float dY = mouseY - agents[i][1];
            agents[i][0] += dX*0.01;
            agents[i][1] += dY*0.01;
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
      if(colorWheel < 0xFF) {
          colorWheel++;
      }
      else {
          colorWheel = 0;
      }      
    }
    
    private static final long serialVersionUID = 1L;
}
