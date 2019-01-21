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
package uk.ac.leeds.ccg.andyt.chart.core;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Generic_Canvas extends Canvas {

    public Generic_Canvas() {
    }

    public Generic_AbstractPlot Plot;
    public Graphics2D G2D;
    public BufferedImage BI;
    public Rectangle Rect;

    @Override
    public Graphics getGraphics() {
        return this.G2D;
    }

    public BufferedImage getBufferedImage() {
        return BI;
    }

    //public int paintedCounter; 
    /**
     * This is a bit strange as the input Graphics g are ignored!
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {

        //Rectangle Rect = getBounds();
        BI = new BufferedImage(Rect.width, Rect.height, BufferedImage.TYPE_INT_ARGB);
        G2D = (Graphics2D) BI.getGraphics();

//        G2D = (Graphics2D) g;
        // draw all components
        Dimension d = Plot.draw(G2D);
        setDimension(d);

//        paintedCounter ++;
//        if (paintedCounter ==2) {
//            int debug = 1;
//             Generic_Visualisation.saveImage(
//                    this,
//                    BI,
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
//        //Rectangle Rect = getBounds();
//        BI = new BufferedImage(
//                Rect.width,
//                Rect.height,
//                BufferedImage.TYPE_INT_ARGB);
//        G2D = (Graphics2D) BI.getGraphics();
//        // draw all components
//        return Plot.draw();
//    }
    // Override this to do custom drawing
    public void draw() {
        drawOutline();
        G2D.setFont(new Font("Arial", Font.ITALIC, 12));
        G2D.drawString("Test", 32, 8);
    }

    public void drawOutline() {
        //Color color = G2D.getColor();
        G2D.setPaint(Color.WHITE);
        Rectangle2D rect = new Rectangle2D.Double(
                getX(),
                getY(),
                getWidth() - 1,
                getHeight() - 1);
//        G2D.fillRect(getX(),getY(),(2 * getWidth()) - 1,(2 * getHeight()) - 1);
        G2D.fillRect(getX(), getY(), getWidth() - 1, getHeight() - 1);
        G2D.setPaint(Color.DARK_GRAY);
        G2D.draw(rect);
        //setPaint(color);
    }

    public void setDimension(Dimension d) {
        Rect = new Rectangle(d);
    }
}
