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
package uk.ac.leeds.ccg.andyt.chart;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.JApplet;
import javax.swing.JFrame;
import uk.ac.leeds.ccg.andyt.generic.visualisation.Generic_Visualisation;

/**
 * An abstract class for creating 2D plot visualisations.
 */
public abstract class Generic_AbstractPlotApplet extends JApplet {

    Generic_Plot Plot;

    public void run(JFrame f) {
        initPaint(f);
        // This is done twice as the image grows to accomodate the X axis label that sticks out
        paint(Plot.getG2());
        resize(f);
        /*
         * To show/not show the JFrame on screen and dispose/not disposed of it 
         * then uncomment  and comment the next 2 lines respectively as 
         * appropriate
         */
        f.setVisible(true);
        //f.dispose();

        /*
         * Save the image to a File 
         */
        Generic_Visualisation.saveImage(null,
                Plot,
                Plot.getBufferedImage(),
                10000,
                Plot.getFormat(),
                Plot.getFile());            
    }
    
    @Override
    public void init() {
//        GridBagLayout aGridBagLayout = new GridBagLayout();
//        getContentPane().setLayout(aGridBagLayout);
//        GridBagConstraints aGridBagConstraints = new GridBagConstraints();
        //Initialize drawing colors
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
    }

    protected void initPaint(JFrame f) {
        f.getContentPane().add("Center", this);
        init();
        f.pack();
        f.setSize(
                getImageWidth(),
                getImageHeight());
        f.setVisible(false);
    }

    protected void resize(JFrame f) {
        f.pack();
        // Hardcoded extra height needed for frame boundary
        int extraHeight = 30;
        // Hardcoded extra width needed for frame boundary
        int extraWidth = 9;
        f.setSize(
                getImageWidth() + extraWidth,
                getImageHeight() + extraHeight);
    }

    /**
     * Implementations are expected to override this yet call to it
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        initG2Image();
        initG2();
        Plot.draw();
    }

    public void initG2() {
        Graphics g = getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        setG2(g2);
        drawOutline();
        //g.dispose();
        setFontMetrics(g2.getFontMetrics());
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public void initG2Image() {
        BufferedImage bi = new BufferedImage(
                getImageWidth(),
                getImageHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2image = (Graphics2D) bi.getGraphics();
        setG2image(g2image);
        g2image.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
    }
    
//    //public abstract Dimension draw();
//
//    public abstract void drawData();

    public void draw(Line2D l) {
        Plot.draw(l);
    }

//    public void drawTitle(String t) {
//        Plot.drawTitle(t);
//    }
    /**
     * The title is draw above the data area and centred on the whole width of
     * the image.
     *
     * @param title
     */
    //@Override
    public void drawTitle(
            String title) {
        Plot.drawTitle(title);
//        setPaint(Color.BLACK);
//        int oldExtraHeightTop = getExtraHeightTop();
//        int textHeight = getTextHeight();
//        setExtraHeightTop(Math.max(oldExtraHeightTop, textHeight * 3));
//        //int oldImageHeight = imageHeight;
//        int titleTextWidth = getTextWidth(title) + (textHeight * 2);
//        int imageWidth = getImageWidth();
//        if (titleTextWidth <= imageWidth) {
//            int startPosition = (imageWidth / 2) - (titleTextWidth / 2) + textHeight;
//            if (startPosition < 0) {
//                startPosition = 0;
//            }
//            drawString(title, startPosition, textHeight * 2);
//        }
//        if (titleTextWidth > imageWidth) {
//            //int titleTextHeight = getTextHeight() + titleHeight / 4;
//            int extrax = titleTextWidth - imageWidth;
//            int extraWidthRight = Math.max(getExtraWidthRight(), extrax / 2);
//            setExtraWidthRight(extraWidthRight);
//            int newExtraWidthLeft = extrax - extraWidthRight;
//            if (newExtraWidthLeft > getExtraWidthLeft()) {
//                setExtraWidthLeft(newExtraWidthLeft);
//                setDataStartCol(getExtraWidthLeft());
//                setDataEndCol(getDataStartCol() + getDataWidth());
//                setOriginCol();
//            }
//            setImageWidth(titleTextWidth);
//        }
//        //+ (boundaryThickness * 2);
//        if (getExtraHeightTop() > oldExtraHeightTop) {
//            int extraHeightDiff = getExtraHeightTop() - oldExtraHeightTop;
//            setImageHeight(getImageHeight() + extraHeightDiff);
//            setDataStartRow(getExtraHeightTop());
//            setDataEndRow(getDataStartRow() + getDataHeight());
//            setDataMiddleRow(((getDataEndRow() - getDataStartRow()) / 2) + getDataStartRow());
//            setOriginRow();
//        }
//        //imageWidth = extrax + dataWidth + (boundaryThickness * 2);
//        //imageHeight += titleTextHeight + dataHeight;// + (boundaryThickness * 2);
//
////        // Debug
////        System.out.println("imageHeight " + imageHeight);
    }

    public void drawOutline() {
        Plot.drawOutline();
    }

    public void setPaint(Color c) {
        Plot.setPaint(c);
    }

    public void drawAxes(
            int ageInterval,
            int startAgeOfEndYearInterval) {
        Plot.drawAxes(
                ageInterval,
                startAgeOfEndYearInterval);
    }

//    /**
//     *
//     * @param interval
//     * @param startAgeOfEndYearInterval
//     */
//    //@Override
//    public void drawAxes(
//            int interval,
//            int startAgeOfEndYearInterval) {
//        int yAxisExtraWidthLeft;
////        int yAxisExtraHeightTop = 0;
////        int yAxisExtraHeightBottom = 0;
//        int xAxisExtraWidthLeft;
//        int xAxisExtraWidthRight;
//        int xAxisExtraHeightBottom;
//        Line2D ab;
//
//        int scaleTickLength = 5;
//        int scaleTickAndTextSeparation = 3;
//        int partTitleGap = 2;
//        int textHeight = Plot.getTextHeight();
//
//        // Draw Y axis
//        int[] yAxisDimensions = drawYAxis(
//                interval,
//                textHeight,
//                startAgeOfEndYearInterval,
//                scaleTickLength,
//                scaleTickAndTextSeparation,
//                partTitleGap);
//        yAxisExtraWidthLeft = yAxisDimensions[0];
//        if (yAxisExtraWidthLeft > Plot.extraWidthLeft) {
//            int diff = yAxisExtraWidthLeft - Plot.extraWidthLeft;
//            Plot.imageWidth += diff;
//            Plot.dataStartCol += diff;
//            Plot.dataEndCol += diff;
//            Plot.extraWidthLeft = yAxisExtraWidthLeft;
//            setOriginCol();
//        }
//        Plot.yAxisWidth = yAxisExtraWidthLeft;
//
//        // Draw X axis
//        int[] xAxisDimensions = drawXAxis(
//                textHeight,
//                scaleTickLength,
//                scaleTickAndTextSeparation,
//                partTitleGap);
//        xAxisExtraWidthLeft = xAxisDimensions[0];
//        xAxisExtraWidthRight = xAxisDimensions[1];
//        xAxisExtraHeightBottom = xAxisDimensions[2];
//        if (xAxisExtraWidthLeft > Plot.extraWidthLeft) {
//            int diff = xAxisExtraWidthLeft - Plot.dataStartCol;
//            Plot.imageWidth += diff;
//            Plot.dataStartCol += diff;
//            Plot.dataEndCol += diff;
//            Plot.extraWidthLeft = xAxisExtraWidthLeft;
//            setOriginCol();
//        }
//        if (xAxisExtraWidthRight > Plot.extraWidthRight) {
//            Plot.imageWidth += xAxisExtraWidthRight - Plot.extraWidthRight;
//            Plot.extraWidthRight = xAxisExtraWidthRight;
//        }
//        Plot.xAxisHeight = xAxisExtraHeightBottom;
//        if (xAxisExtraHeightBottom > Plot.extraHeightBottom) {
//            Plot.imageHeight += xAxisExtraHeightBottom - Plot.extraHeightBottom;
//            Plot.extraHeightBottom = xAxisExtraHeightBottom;
//        }
//    }
    //@Override

//    public void initData() {
//        if (Plot.getData() == null) {
//            Object[] data = getDefaultData();
//            Plot.setData(data);
//            initialiseParameters(data);
//        }
//    }
    public Object[] getData() {
        return Plot.getData();
    }

    protected void setData(Object[] data) {
        Plot.setData(data);
    }

    /**
     * Constructs and returns an initialised JFrame.
     *
     * @param title
     * @return
     */
    protected static JFrame getJFrame(String title) {
        JFrame jFrame = new JFrame(
                title);
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        return jFrame;
    }

    public abstract void setOriginCol();

    public abstract int[] drawXAxis(
            int textHeight,
            int scaleTickLength,
            int scaleTickAndTextSeparation,
            int partTitleGap,
            int seperationDistanceOfAxisAndData);

    public abstract int[] drawYAxis(
            int interval,
            int textHeight,
            int startOfEndInterval,
            int scaleTickLength,
            int scaleTickAndTextSeparation,
            int partTitleGap,
            int seperationDistanceOfAxisAndData);

    //public abstract void initialiseParameters(Object[] data);
    public void initialiseParameters(Object[] data) {
        this.Plot.initialiseParameters(data);
    }

    public void fillRect(
            int x,
            int y,
            int width,
            int height) {
        Plot.fillRect(x, y, width, height);
    }

    public void draw(
            Rectangle2D r) {
        Plot.draw(r);
    }

    public void draw(Point2D p) {
        Plot.draw(p);
    }

    public void drawString(String s, int col, int row) {
        Plot.drawString(s, col, row);
    }

    public int getTextWidth(String s) {
        return Plot.getTextWidth(s);
    }

    public int getTextHeight() {
        return Plot.getTextHeight();
    }

    public void writeText(String s, double angle, int col, int row) {
        Plot.writeText(s, angle, col, row);
    }

    public Graphics2D getG2image() {
        return Plot.getG2image();
    }

    public void setG2image(Graphics2D g2image) {
        Plot.setG2image(g2image);
    }

    public Graphics2D getG2() {
        return Plot.getG2();
    }

    public void setG2(Graphics2D g2) {
        Plot.setG2(g2);
    }

    public FontMetrics getFontMetrics() {
        return Plot.getFontMetrics();
    }

    public void setFontMetrics(FontMetrics fontMetrics) {
        Plot.setFontMetrics(fontMetrics);
    }
    
    public String getTitle() {
        return Plot.getTitle();
    }

    protected void setTitle(String title) {
        Plot.setTitle(title);
    }

    public int getImageWidth() {
        return Plot.getImageWidth();
    }

    protected void setImageWidth(int imageWidth) {
        Plot.setImageWidth(imageWidth);
    }

    public int getImageHeight() {
        return Plot.getImageHeight();
    }

    protected void setImageHeight(int imageHeight) {
        Plot.setImageHeight(imageHeight);
    }

    public int getDataWidth() {
        return Plot.getDataWidth();
    }

    protected void setDataWidth(int dataWidth) {
        Plot.setDataWidth(dataWidth);
    }

    public int getDataHeight() {
        return Plot.getDataHeight();
    }

    protected void setDataHeight(int dataHeight) {
        Plot.setDataHeight(dataHeight);
    }

    public int getDataStartRow() {
        return Plot.getDataStartRow();
    }

    protected void setDataStartRow(int dataStartRow) {
        Plot.setDataStartRow(dataStartRow);
    }

    public int getDataMiddleRow() {
        return Plot.getDataMiddleRow();
    }

    protected void setDataMiddleRow(int dataMiddleRow) {
        Plot.setDataMiddleRow(dataMiddleRow);
    }

    public int getDataEndRow() {
        return Plot.getDataEndRow();
    }

    protected void setDataEndRow(int dataEndRow) {
        Plot.setDataEndRow(dataEndRow);
    }

    public int getDataStartCol() {
        return Plot.getDataStartCol();
    }

    protected void setDataStartCol(int dataStartCol) {
        Plot.setDataStartCol(dataStartCol);
    }

    public int getDataEndCol() {
        return Plot.getDataEndCol();
    }

    protected void setDataEndCol(int dataEndCol) {
        Plot.setDataEndCol(dataEndCol);
    }

    public int getxAxisHeight() {
        return Plot.getxAxisHeight();
    }

    protected void setxAxisHeight(int xAxisHeight) {
        Plot.setxAxisHeight(xAxisHeight);
    }

    public int getyAxisWidth() {
        return Plot.getyAxisWidth();
    }

    protected void setyAxisWidth(int yAxisWidth) {
        Plot.setyAxisWidth(yAxisWidth);
    }

    public String getxAxisLabel() {
        return Plot.getxAxisLabel();
    }

    protected void setxAxisLabel(String xAxisLabel) {
        Plot.setxAxisLabel(xAxisLabel);
    }

    public String getyAxisLabel() {
        return Plot.getyAxisLabel();
    }

    protected void setyAxisLabel(String yAxisLabel) {
        Plot.setyAxisLabel(yAxisLabel);
    }

    public int getExtraWidthLeft() {
        return Plot.getExtraWidthLeft();
    }

    protected void setExtraWidthLeft(int extraWidthLeft) {
        Plot.setExtraWidthLeft(extraWidthLeft);
    }

    public int getExtraWidthRight() {
        return Plot.getExtraWidthRight();
    }

    protected void setExtraWidthRight(int extraWidthRight) {
        Plot.setExtraWidthRight(extraWidthRight);
    }

    public int getExtraHeightTop() {
        return Plot.getExtraHeightTop();
    }

    protected void setExtraHeightTop(int extraHeightTop) {
        Plot.setExtraHeightTop(extraHeightTop);
    }

    public int getExtraHeightBottom() {
        return Plot.getExtraHeightBottom();
    }

    protected void setExtraHeightBottom(int extraHeightBottom) {
        Plot.setExtraHeightBottom(extraHeightBottom);
    }

    public BigDecimal getMaxX() {
        return Plot.maxX;
    }

    protected void setMaxX(BigDecimal maxX) {
        Plot.maxX = maxX;
    }

    public BigDecimal getMinX() {
        return Plot.minX;
    }

    protected void setMinX(BigDecimal minX) {
        Plot.minX = minX;
    }

    public BigDecimal getMaxY() {
        return Plot.maxY;
    }

    protected void setMaxY(BigDecimal maxY) {
        Plot.maxY =maxY;
    }

    public BigDecimal getMinY() {
        return Plot.minY;
    }

    protected void setMinY(BigDecimal minY) {
        Plot.minY =minY;
    }

    public int getDecimalPlacePrecisionForCalculations() {
        return Plot.getDecimalPlacePrecisionForCalculations();
    }

    protected void setDecimalPlacePrecisionForCalculations(int dp) {
        Plot.setDecimalPlacePrecisionForCalculations(dp);
    }

    public int getDecimalPlacePrecisionForDisplay() {
        return Plot.getDecimalPlacePrecisionForDisplay();
    }

    protected void setDecimalPlacePrecisionForDisplay(int dp) {
        Plot.setDecimalPlacePrecisionForDisplay(dp);
    }

    public int getSignificantDigits() {
        return Plot.getSignificantDigits();
    }

    protected void setSignificantDigits(int significantDigits) {
        Plot.setSignificantDigits(significantDigits);
    }
    
    public RoundingMode getRoundingMode() {
        return Plot.getRoundingMode();
    }

    protected void setRoundingMode(RoundingMode rm) {
        Plot.setRoundingMode(rm);
    }
    
    public BigDecimal getCellHeight() {
        return Plot.getCellHeight();
    }

    protected void setCellHeight(BigDecimal cellHeight) {
        Plot.setCellHeight(cellHeight);
    }

    protected void setCellHeight() {
        Plot.setCellHeight();
    }

    public BigDecimal getCellWidth() {
        return Plot.getCellWidth();
    }

    protected void setCellWidth(BigDecimal cellWidth) {
        Plot.setCellWidth(cellWidth);
    }

    public void setCellWidth() {
        Plot.setCellWidth();
    }

    protected int getOriginRow() {
        return Plot.getOriginRow();
    }

    public void setOriginRow(int originRow) {
        Plot.setOriginRow(originRow);
    }

    protected void setOriginRow() {
        Plot.setOriginRow();
    }

    public int getOriginCol() {
        return Plot.getOriginCol();
    }

    protected void setOriginCol(int originCol) {
        Plot.setOriginCol(originCol);
    }

    public boolean isAddLegend() {
        return Plot.isAddLegend();
    }

    public void setAddLegend(boolean addLegend) {
        Plot.setAddLegend(addLegend);
    }

    public int getLegendHeight() {
        return Plot.getLegendHeight();
    }

    protected void setLegendHeight(int legendHeight) {
        Plot.setLegendHeight(legendHeight);
    }

    public int getLegendWidth() {
        return Plot.getLegendWidth();
    }

    protected void setLegendWidth(int legendWidth) {
        Plot.setLegendWidth(legendWidth);
    }

    public int getAgeInterval() {
        return Plot.getAgeInterval();
    }

    protected void setAgeInterval(int ageInterval) {
        Plot.setAgeInterval(ageInterval);
    }

    public int getStartAgeOfEndYearInterval() {
        return Plot.getStartAgeOfEndYearInterval();
    }

    protected void setStartAgeOfEndYearInterval(int startAgeOfEndYearInterval) {
        Plot.setStartAgeOfEndYearInterval(startAgeOfEndYearInterval);
    }
}
