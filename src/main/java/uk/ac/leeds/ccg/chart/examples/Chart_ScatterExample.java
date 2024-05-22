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
package uk.ac.leeds.ccg.chart.examples;

import ch.obermuhlner.math.big.BigRational;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import uk.ac.leeds.ccg.chart.core.Chart;
import uk.ac.leeds.ccg.chart.data.Chart_ScatterData;
import uk.ac.leeds.ccg.chart.data.BigRational2;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.math.arithmetic.Math_BigDecimal;
import uk.ac.leeds.ccg.math.arithmetic.Math_BigRational;

/**
 * An example of generating a Scatter Plot visualization.
 */
public class Chart_ScatterExample extends Chart {

    public Chart_ScatterExample(Generic_Environment e) {
        super(e);
    }

    /**
     * @param e The Generic_Environment.
     * @param es The ExecutorService.
     * @param f The Path.
     * @param format The format.
     * @param title The title.
     * @param dataWidth The data width.
     * @param dataHeight The data height.
     * @param xAxisLabel The x axis label.
     * @param yAxisLabel The y axis label.
     * @param drawOriginLinesOnPlot If {@code true} then origin lines are drawn
     * on the plot.
     * @param oomx The order of magnitude for rounding precision for x values.
     * @param oomy The order of magnitude for rounding precision for y values.
     * @param rm The RoundingMode.
     */
    public Chart_ScatterExample(Generic_Environment e, ExecutorService es,
            Path f, String format, String title, int dataWidth,
            int dataHeight, String xAxisLabel, String yAxisLabel,
            boolean drawOriginLinesOnPlot, int oomx, int oomy, RoundingMode rm) {
        super(e);
        init(es, f, format, title, dataWidth, dataHeight, xAxisLabel,
                yAxisLabel, drawOriginLinesOnPlot, oomx, oomy, rm);
    }

    public static void main(String[] args) {
        try {
            Generic_Environment e = new Generic_Environment(new Generic_Defaults());
            /**
             * Initialise title and Path to write image to.
             */
            String title;
            Path file;
            String format = "PNG";
            if (args.length != 2) {
                System.out.println(
                        "Expected 2 args:"
                        + " args[0] title;"
                        + " args[1] Path."
                        + " Recieved " + args.length + " args.");
                // Use defaults
                title = "Scatter Plot";
                System.out.println("Use default title: " + title);
                file = Paths.get(System.getProperty("user.dir"),
                        "data", "output",
                        title.replace(" ", "_") + "." + format);
                System.out.println("Use default Path: " + file.toString());
            } else {
                title = args[0];
                file = Paths.get(args[1]);
            }
            int dataWidth = 400;//250;
            int dataHeight = 657;
            String xAxisLabel = "Expected";
            String yAxisLabel = "Observed";
            boolean drawOriginLinesOnPlot = true;
            int oomx = -1;
            int oomy = -2;
            RoundingMode rm = RoundingMode.HALF_UP;
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Chart_ScatterExample plot = new Chart_ScatterExample(e,
                    executorService, file,
                    format, title, dataWidth, dataHeight, xAxisLabel,
                    yAxisLabel, drawOriginLinesOnPlot, oomx, oomy, rm);
            plot.setData(plot.getDefaultData());
            plot.vis.getHeadlessEnvironment();
            plot.setStartAgeOfEndYearInterval(0); // To avoid null pointer
            plot.run();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public void drawData() {
        drawPoints(Color.DARK_GRAY, getData());
    }

    /**
     * Draws the X axis returns the height
     *
     * @param th textHeight
     * @param stl scaleTickLength
     * @param sd1 scaleTickAndTextSeparation
     * @param sd2 seperationDistanceOfAxisAndData
     * @param ptg partTitleGap
     * @return int[] r where:
     * <ul>
     * <li>r[0] = xAxisExtraWidthLeft</li>
     * <li>r[1] = xAxisExtraWidthRight</li>
     * <li>r[2] = xAxisExtraHeightBottom</li>
     * </ul>
     */
    @Override
    public int[] drawXAxis(int th, int stl, int sd1, int ptg, int sd2) {
        int[] r = new int[3];
        int xAxisExtraWidthLeft = 0;
        int xAxisExtraWidthRight = 0;
        int xAxisExtraHeightBottom = stl + sd1 + sd2;
        setPaint(Color.LIGHT_GRAY);
        // Draw X axis below the data
        setPaint(Color.GRAY);
        int row = dataEndRow + sd2;
        Line2D ab = new Line2D.Double(dataStartCol, row, dataEndCol, row);
        draw(ab);
        /*
         * Draw X axis ticks and labels below the X axis
         */
        int increment = th;
        while (((dataWidth * th) + 4) / increment > dataWidth) {
            increment += th;
        }
        int xAxisMaxLabelHeight = 0;
        String s;
        int textWidth;
        double angle;
        // From the origin right
        int startCol = originCol;
        //int startCol = getDataStartCol();
        RoundingMode rm = getRoundingMode();
        for (int col = startCol; col <= dataEndCol; col += increment) {
            if (col >= dataStartCol) {
                ab = new Line2D.Double(col, row, col, row + stl);
                draw(ab);
                BigRational x = imageColToXCoordinate(col);
                if (x.compareTo(BigRational.ZERO) == 0 || col == startCol) {
                    s = "0";
                } else {
                    s = Math_BigRational.round(x, oomx, rm).toString();
                }
                textWidth = getTextWidth(s);
                xAxisMaxLabelHeight = Math.max(xAxisMaxLabelHeight, textWidth);
                angle = Math.PI / 2;
                writeText(s, angle, col - (th / 3), row + sd1 + stl);
//                    row + scaleTickLength + (textHeight / 3));
            }
        }
        // From the origin left
        for (int col = startCol; col >= dataStartCol; col -= increment) {
            if (col >= dataStartCol) {
                ab = new Line2D.Double(col, row, col, row + stl);
                draw(ab);
                BigRational x = imageColToXCoordinate(col);
                if (x.compareTo(BigRational.ZERO) == 0 || col == startCol) {
                    s = "0";
                } else {
                    s = Math_BigRational.round(x, oomx, rm).toString();
                }
                textWidth = getTextWidth(s);
                xAxisMaxLabelHeight = Math.max(xAxisMaxLabelHeight, textWidth);
                angle = Math.PI / 2;
                writeText(s, angle, col - (th / 3),
                        row + sd1 + stl);
//                    row + scaleTickLength + (textHeight / 3));
            }
        }
        xAxisExtraHeightBottom += xAxisMaxLabelHeight;
        //xAxisExtraHeightBottom += scaleTickAndTextSeparation + scaleTickLength + seperationDistanceOfAxisAndData;
        xAxisExtraWidthRight += th * 2;
        xAxisExtraWidthLeft += th / 2;
        // Add the X axis label
        setPaint(Color.BLACK);
        s = xAxisLabel;
        textWidth = getTextWidth(s);
        xAxisExtraHeightBottom += ptg;
        // Calculate if the xAxisLabel will require the imageWidth to increase.
        // If the xAxisLabel is wider than the XAxis it might be best to split 
        // it and write it on multiple lines...  
        int currentWidth = xAxisExtraWidthLeft + dataWidth + xAxisExtraWidthRight;
        int endxAxisLabelPostion = dataStartCol + (dataWidth / 2) + (textWidth / 2);
        if (endxAxisLabelPostion > currentWidth) {
            xAxisExtraWidthRight += endxAxisLabelPostion - currentWidth;
        }
        drawString(s,
                dataStartCol + (dataWidth / 2) - (textWidth / 2),
                row + xAxisExtraHeightBottom);
        // Draw line on origin
        if (isDrawOriginLinesOnPlot()) {
            if (originRow <= dataEndRow && originRow >= dataStartRow) {
                setPaint(Color.LIGHT_GRAY);
                ab = new Line2D.Double(dataStartCol, originRow, dataEndCol,
                        originRow);
                draw(ab);
            }
        }
        xAxisExtraHeightBottom += (2 * th);
        if (addLegend) {
            xAxisExtraHeightBottom += (2 * th);
        }
        r[0] = xAxisExtraWidthLeft;
        r[1] = xAxisExtraWidthRight;
        r[2] = xAxisExtraHeightBottom;
        return r;
    }

    /**
     *
     * @param interval ignored
     * @param th textHeight
     * @param saeyi startAgeOfEndYearInterval ignored
     * @param stl scaleTickLength
     * @param sd1 scaleTickAndTextSeparation
     * @param ptg partTitleGap
     * @param sd2 seperationDistanceOfAxisAndData
     * @return
     */
    @Override
    public int[] drawYAxis(int interval, int th, int saeyi, int stl,
            int sd1, int ptg, int sd2) {
        int[] r = new int[1];
        int yAxisExtraWidthLeft = stl + sd1 + sd2;
        Line2D ab;
        String s;
        int tw;
        int row;

        RoundingMode rm = getRoundingMode();
        // Draw Y axis to left of data
        //setOriginCol();
        setPaint(Color.GRAY);
        int col = dataStartCol - sd2;
        ab = new Line2D.Double(col, dataEndRow, col, dataStartRow);
        draw(ab);
        /*
         * Draw Y axis ticks and labels to left of Y axis
         */
        // Value labels can't be closer than this otherwise they will overlap. 
        int minIncrement = th;
        while (((dataHeight * th) + 4) / minIncrement > dataHeight) {
            minIncrement += th;
        }
        int yAxisMaxLabelWidth = 0;
        /**
         * From the origin up
         */
        // Calculate the maximum number of ticks. 
        int mtu = 0;
        for (row = originRow; row >= dataStartRow; row -= minIncrement) {
            if (row <= dataEndRow) {
                mtu++;
            }
        }
        System.out.println("maximum number of ticks " + mtu);
        // Calculate the number of ticks.
        BigDecimal maxYbd = data.maxY.toBigDecimal();
        int oommsdmaxY = Math_BigDecimal.getOrderOfMagnitudeOfMostSignificantDigit(maxYbd);
        BigRational maxYr = Math_BigRational.round(maxY, oommsdmaxY, RoundingMode.UP);
        BigDecimal unit = new BigDecimal(BigInteger.ONE, oommsdmaxY);
        int msd = Math_BigDecimal.getMostSignificantDigit(maxYr.toBigDecimal());
        BigDecimal floor = unit.multiply(new BigDecimal(msd));
        if (msd == 9) {
            maxYbd = floor.add(unit);
        } else if (msd == 7 || msd == 3) {
            maxYbd = floor.add(unit);
        } else if (msd == 6){
            maxYbd = floor.add(unit.add(unit));
        } else if (msd == 5){
            maxYbd = floor.add(unit.add(unit).add(unit));
        } else {
            maxYbd = floor;
        }
        maxYr = BigRational.valueOf(maxYbd);        
        int maxYri = maxYr.intValue();
        while (maxYri < mtu) {
            maxYri *= 2;
        }
        maxYri /= 2;
        int nt = (originRow - dataStartRow) / maxYri;
        System.out.println("rounded number of ticks " + nt);
        // Calculate the yIncrement
        BigRational yInc = maxYr.divide(nt);
        System.out.println("yInc " + yInc);
        // draw
        BigRational y = BigRational.ZERO;
        while (y.compareTo(maxYr) == -1) {
            row = getRow(y);
            if (row >= dataStartRow) {
                ab = new Line2D.Double(col, row, col - stl, row);
                draw(ab);
                if (y.compareTo(BigRational.ZERO) == 0 || row == originRow) {
                    s = "0";
                } else {
                    s = Math_BigRational.round(y, oomy, rm).toString();
                }
                tw = getTextWidth(s);
                yAxisMaxLabelWidth = Math.max(yAxisMaxLabelWidth, tw);
                drawString(s, col - sd1 - stl - tw, row + (th / 3));
            }
                y = y.add(yInc);
        }
        /*
        for (row = originRow; row >= dataStartRow; row -= increment) {
            if (row <= dataEndRow) {
                ab = new Line2D.Double(col, row, col - stl, row);
                draw(ab);
                BigRational y = imageRowToYCoordinate(row);
                if (y.compareTo(BigRational.ZERO) == 0 || row == originRow) {
                    s = "0";
                } else {
                    s = Math_BigRational.round(y, oomy, rm).toString();
                }
                tw = getTextWidth(s);
                yAxisMaxLabelWidth = Math.max(yAxisMaxLabelWidth, tw);
                drawString(s, col - sd1 - stl - tw, row + (th / 3));
            }
        }
        */
        // From the origin down
        // draw
        y = BigRational.ZERO;
        while (y.compareTo(maxYr) == 1 ) {
            row = getRow(y);
            if (row <= dataEndRow) {
                ab = new Line2D.Double(col, row, col - stl, row);
                draw(ab);
                if (y.compareTo(BigRational.ZERO) == 0 || row == originRow) {
                    s = "0";
                } else {
                    s = Math_BigRational.round(y, oomy, rm).toString();
                }
                tw = getTextWidth(s);
                yAxisMaxLabelWidth = Math.max(yAxisMaxLabelWidth, tw);
                drawString(s, col - sd1 - stl - tw, row + (th / 3));
            }
            y = y.subtract(yInc);
        }
        /*
        for (row = originRow; row <= dataEndRow; row += increment) {
            if (row <= dataEndRow) {
                ab = new Line2D.Double(col, row, col - stl, row);
                draw(ab);
                BigRational y = imageRowToYCoordinate(row);
                if (y.compareTo(BigRational.ZERO) == 0 || row == originRow) {
                    s = "0";
                } else {
                    s = Math_BigRational.round(y, oomy, rm).toString();
                }
                tw = getTextWidth(s);
                yAxisMaxLabelWidth = Math.max(yAxisMaxLabelWidth, tw);
                drawString(s, col - sd1 - stl - tw, row + (th / 3));
            }
        }
         */
        yAxisExtraWidthLeft += stl + sd1 + yAxisMaxLabelWidth;
        // Add the Y axis label
        setPaint(Color.BLACK);
        s = yAxisLabel;
        tw = getTextWidth(s);
        yAxisExtraWidthLeft += (th * 2) + ptg;
        double angle = 3.0d * Math.PI / 2.0d;
        writeText(s, angle, 3 * th / 2,
                dataMiddleRow + (tw / 2));
        // Draw line on origin
        if (isDrawOriginLinesOnPlot()) {
            if (originCol <= dataEndCol && originCol >= dataStartCol) {
                setPaint(Color.LIGHT_GRAY);
                ab = new Line2D.Double(originCol, dataStartRow, originCol, dataEndRow);
                draw(ab);
            }
        }
        r[0] = yAxisExtraWidthLeft;
        return r;
    }
    
    protected void drawPoints(Color color, Chart_ScatterData data) {
        if (data != null) {
            Iterator<BigRational2> ite = data.xyData.iterator();
            setPaint(color);
            while (ite.hasNext()) {
                BigRational2 xy = ite.next();
                Point2D p = coordinateToScreen(xy.getX(), xy.getY());
                draw(p);
            }
        }
    }

    @Override
    public void setOriginCol() {
        originCol = getCol(BigRational.ZERO);
//        System.out.println("originCol " + originCol);
//        
//        if (minX.compareTo(BigDecimal.ZERO) == 0) {
//            originCol = dataStartCol;
//            //originCol = dataStartCol - dataEndCol / 2;
//        } else {
//            if (cellWidth.compareTo(BigDecimal.ZERO) == 0) {
//                originCol = dataStartCol;
//            } else {
//                originCol = Math_BigDecimal.divideRoundIfNecessary(
//                        minX,
//                        cellWidth,
//                        0,
//                        _RoundingMode).intValueExact()
//                        + dataStartCol;
//            }
//        }
    }

    /**
     * @return default data for this type of chart.
     */
    public Chart_ScatterData getDefaultData() {
        return getDefaultData(true);
    }

    public static Chart_ScatterData getDefaultData(boolean ignore) {
        Random random = new Random(0);
        Chart_ScatterData r = new Chart_ScatterData();
        r.maxX = BigRational.valueOf(Double.MIN_VALUE);
        r.minX = BigRational.valueOf(Double.MAX_VALUE);
        r.maxY = BigRational.valueOf(Double.MIN_VALUE);
        r.minY = BigRational.valueOf(Double.MAX_VALUE);
//        for (int i = -100; i < 328; i++) {         
//            for (int j = -100; j < 0; j++) {
//        for (int i = -100; i < 100; i++) {
//            for (int j = -100; j < 100; j++) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
//        for (int i = -15; i < 10; i++) {
//            for (int j = -9; j < 12; j++) {
                double random_0 = random.nextDouble();
                BigRational x = BigRational.valueOf((i + random.nextDouble()) * random_0);
                BigRational y = BigRational.valueOf(((j + i) / 2) * random_0);
                //BigDecimal y = BigDecimal.valueOf((j + i) * random_0);
                if (x.compareTo(r.maxX) == 1) {
                    r.maxX = x;
                }
                if (x.compareTo(r.minX) == -1) {
                    r.minX = x;
                }
                if (y.compareTo(r.maxY) == 1) {
                    r.maxY = y;
                }
                if (y.compareTo(r.minY) == -1) {
                    r.minY = y;
                }
                BigRational2 p = new BigRational2(x, y);
                r.xyData.add(p);
            }
        }
        return r;
    }

    @Override
    public Chart_ScatterData getData() {
        return (Chart_ScatterData) data;
    }
}
