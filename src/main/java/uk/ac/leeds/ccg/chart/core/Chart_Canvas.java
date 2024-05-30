/**
 * Copyright 2012 Andy Turner, The University of Leeds, UK
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package uk.ac.leeds.ccg.chart.core;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Chart_Canvas extends Canvas {

    private static final long serialVersionUID = 1L;

    public Chart_Canvas() {
    }

    public Chart Plot;
    public Graphics2D g2d;
    public BufferedImage bi;
    public Rectangle rect;

    @Override
    public Graphics getGraphics() {
        return this.g2d;
    }

    public BufferedImage getBufferedImage() {
        return bi;
    }

    //public int paintedCounter; 
    /**
     * This is a bit strange as the input Graphics g are ignored!
     *
     * @param g The graphics.
     */
    @Override
    public void paint(Graphics g) {

        //Rectangle rect = getBounds();
        bi = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D) bi.getGraphics();

//        g2d = (Graphics2D) g;
        // draw all components
        Dimension d = Plot.draw(g2d);
        setDimension(d);

//        paintedCounter ++;
//        if (paintedCounter ==2) {
//            int debug = 1;
//             Generic_Visualisation.saveImage(
//                    this,
//                    bi,
//                    10000,
//                    Plot.getFormat(),
//                    Plot.getFile());
//        }
        //_Generic_Plot.output();
    }

//    @Override
//    public void repaint() {
//        
//    }
//    public Dimension paintAndGetNewDimensions(Graphics g) {
//        //Rectangle rect = getBounds();
//        bi = new BufferedImage(
//                rect.width,
//                rect.height,
//                BufferedImage.TYPE_INT_ARGB);
//        g2d = (Graphics2D) bi.getGraphics();
//        // draw all components
//        return Plot.draw();
//    }
    // Override this to do custom drawing
    public void draw() {
        drawOutline();
        g2d.setFont(new Font("Arial", Font.ITALIC, 12));
        g2d.drawString("Test", 32, 8);
    }

    public void drawOutline() {
        //Color color = g2d.getColor();
        g2d.setPaint(Color.WHITE);
        Rectangle2D r2 = new Rectangle2D.Double(getX(), getY(), getWidth() - 1, 
                getHeight() - 1);
//        g2d.fillRect(getX(),getY(),(2 * getWidth()) - 1,(2 * getHeight()) - 1);
        g2d.fillRect(getX(), getY(), getWidth() - 1, getHeight() - 1);
        g2d.setPaint(Color.DARK_GRAY);
        g2d.draw(r2);
        //setPaint(color);
    }

    public void setDimension(Dimension d) {
        rect = new Rectangle(d);
    }
}
