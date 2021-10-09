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

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.math.MathContext;
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
import uk.ac.leeds.ccg.math.number.Math_BigRational;

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
     * @param dpc decimal place precision calculations
     * @param dpd decimal place precision for calculations
     * @param rm The RoundingMode.
     */
    public Chart_ScatterExample(Generic_Environment e, ExecutorService es,
            Path f, String format, String title, int dataWidth,
            int dataHeight, String xAxisLabel, String yAxisLabel,
            boolean drawOriginLinesOnPlot, int dpc, int dpd, RoundingMode rm) {
        super(e);
        init(es, f, format, title, dataWidth, dataHeight,
                xAxisLabel, yAxisLabel, drawOriginLinesOnPlot, dpc, dpd, rm);
    }

    public static void main(String[] args) {
        try {
            Generic_Environment e = new Generic_Environment(new Generic_Defaults());
            /*
         * Initialise title and Path to write image to
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
            int dpc = 10;
            int dpd = 3;
            RoundingMode rm = RoundingMode.HALF_UP;
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Chart_ScatterExample plot = new Chart_ScatterExample(e, executorService, file,
                    format, title, dataWidth, dataHeight, xAxisLabel,
                    yAxisLabel, drawOriginLinesOnPlot, dpc, dpd, rm);
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
        //int seperationDistanceOfAxisAndData = partTitleGap;
        //int seperationDistanceOfAxisAndData = partTitleGap * 2;
        //int seperationDistanceOfAxisAndData = textHeight;
        int xAxisExtraHeightBottom = stl + sd1 + sd2;
//                    row + scaleTickLength + (textHeight / 3));;
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
        String text_String;
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
                Math_BigRational x = imageColToXCoordinate(col);
                if (x.compareTo(Math_BigRational.ZERO) == 0 || col == startCol) {
                    text_String = "0";
                } else {
                    //text_String = "" + x.stripTrailingZeros().toPlainString();
                    //text_String = "" + x.round(mc).stripTrailingZeros().toString();
                    //text_String = "" + x.stripTrailingZeros().toString();
                    text_String = "" + x.toBigDecimal(new MathContext(significantDigits)).toString();
                }
                textWidth = getTextWidth(text_String);
                xAxisMaxLabelHeight = Math.max(xAxisMaxLabelHeight, textWidth);
                angle = Math.PI / 2;
                writeText(text_String, angle, col - (th / 3), row + sd1 + stl);
//                    row + scaleTickLength + (textHeight / 3));
            }
        }
        // From the origin left
        for (int col = startCol; col >= dataStartCol; col -= increment) {
            if (col >= dataStartCol) {
                ab = new Line2D.Double(col, row, col, row + stl);
                draw(ab);
                Math_BigRational x = imageColToXCoordinate(col);
                if (x.compareTo(Math_BigRational.ZERO) == 0 || col == startCol) {
                    text_String = "0";
                } else {
                    //text_String = "" + x.stripTrailingZeros().toPlainString();
                    //text_String = "" + x.round(mc).stripTrailingZeros().toString();
                    //text_String = "" + x.stripTrailingZeros().toString();
                    text_String = "" + x.toBigDecimal(new MathContext(significantDigits)).toString();
                }
                textWidth = getTextWidth(text_String);
                xAxisMaxLabelHeight = Math.max(xAxisMaxLabelHeight, textWidth);
                angle = Math.PI / 2;
                writeText(text_String, angle, col - (th / 3),
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
        text_String = xAxisLabel;
        textWidth = getTextWidth(text_String);
        xAxisExtraHeightBottom += ptg;
        // Calculate if the xAxisLabel will require the imageWidth to increase.
        // If the xAxisLable is wider than the XAxis it might be best to split 
        // it and write it on multiple lines.  
        int currentWidth = xAxisExtraWidthLeft + dataWidth + xAxisExtraWidthRight;
        int endxAxisLabelPostion = dataStartCol + (dataWidth / 2) + (textWidth / 2);
        if (endxAxisLabelPostion > currentWidth) {
            xAxisExtraWidthRight += endxAxisLabelPostion - currentWidth;
        }
        drawString(text_String,
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
        String text;
        int tw;
        int row;
        int increment;
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
        increment = th;
        while (((dataHeight * th) + 4) / increment > dataHeight) {
            increment += th;
        }
        int yAxisMaxLabelWidth = 0;
        // From the origin up
        for (row = originRow; row >= dataStartRow; row -= increment) {
            if (row <= dataEndRow) {
                ab = new Line2D.Double(col, row, col - stl, row);
                draw(ab);
                Math_BigRational y = imageRowToYCoordinate(row);
                if (y.compareTo(Math_BigRational.ZERO) == 0 || row == originRow) {
                    text = "0";
                } else {
                    //text_String = "" + y.stripTrailingZeros().toPlainString();
                    //text_String = "" + y.round(mc).stripTrailingZeros().toString();
                    text = "" + y.toBigDecimal(new MathContext(significantDigits)).toString();
                }
                tw = getTextWidth(text);
                yAxisMaxLabelWidth = Math.max(yAxisMaxLabelWidth, tw);
                drawString(
                        text,
                        col - sd1 - stl - tw,
                        row + (th / 3));
            }
        }
        // From the origin down
        for (row = originRow; row <= dataEndRow; row += increment) {
            if (row <= dataEndRow) {
                ab = new Line2D.Double(
                        col,
                        row,
                        col - stl,
                        row);
                draw(ab);
                Math_BigRational y = imageRowToYCoordinate(row);
                if (y.compareTo(Math_BigRational.ZERO) == 0 || row == originRow) {
                    text = "0";
                } else {
                    //text_String = "" + y.stripTrailingZeros().toPlainString();
                    //text_String = "" + y.round(mc).stripTrailingZeros().toString();
                    text = "" + y.toBigDecimal(new MathContext(significantDigits)).toString();
                }
                tw = getTextWidth(text);
                yAxisMaxLabelWidth = Math.max(yAxisMaxLabelWidth, tw);
                drawString(
                        text,
                        col - sd1 - stl - tw,
                        row + (th / 3));
            }
        }
        yAxisExtraWidthLeft += stl + sd1 + yAxisMaxLabelWidth;
        // Add the Y axis label
        setPaint(Color.BLACK);
        text = yAxisLabel;
        tw = getTextWidth(text);
        yAxisExtraWidthLeft += (th * 2) + ptg;
        double angle = 3.0d * Math.PI / 2.0d;
        writeText(text, angle, 3 * th / 2,
                dataMiddleRow + (tw / 2));
        // Draw line on origin
        if (isDrawOriginLinesOnPlot()) {
            if (originCol <= dataEndCol && originCol >= dataStartCol) {
                setPaint(Color.LIGHT_GRAY);
                ab = new Line2D.Double(
                        originCol,
                        dataStartRow,
                        originCol,
                        dataEndRow);
                draw(ab);
            }
        }
        r[0] = yAxisExtraWidthLeft;
        return r;
    }

//    /**
//     * Draws the X axis
//     * returns the height
//     */
//    @Override
//    public int[] drawXAxis(
//            int textHeight,
//            int scaleTickLength,
//            int scaleTickAndTextSeparation,
//            int partTitleGap) {
//        int[] result = new int[3];
//        // Draw X axis
//        int xAxisExtraWidthLeft = 0;
//        int xAxisExtraWidthRight = 0;
//        int xAxisExtraHeightBottom = scaleTickLength + scaleTickAndTextSeparation + textHeight;
//    
//         // Draw X axis scale below the data
//        setPaint(Color.GRAY);
//        int row = dataEndRow + textHeight;
//        Line2D ab = new Line2D.Double(
//                dataStartCol,
//                row,
//                dataEndCol,
//                row);
//        draw(ab);
//        int increment = textHeight;
//        while (((dataWidth * textHeight) + 4) / increment > dataWidth) {
//            increment += textHeight;
//        }
//        String text_String;
//        int textWidth;
//        double angle;
//        int theXaxisMaxLabelHeight = 0;
//        // From the origin right
//        for (int col = this.originCol; col <= this.dataEndCol; col += increment) {
//            ab = new Line2D.Double(
//                    col,
//                    row,
//                    col,
//                    row + scaleTickLength);
//            draw(ab);
//            BigDecimal x = imageColToXCoordinate(col);
//            if (x.compareTo(BigDecimal.ZERO) == 0 || col == this.originCol) {
//                text_String = "0";
//            } else {
//                //text_String = "" + x.stripTrailingZeros().toPlainString();
//                //text_String = "" + x.round(mc).stripTrailingZeros().toString();
//                //text_String = "" + x.stripTrailingZeros().toString();
//                text_String = "" + Math_BigDecimal.roundStrippingTrailingZeros(
//                        x,
//                        Math_BigDecimal.getDecimalPlacePrecision(x, significantDigits),
//                        _RoundingMode).toString();
//            }
//            textWidth = getTextWidth(text_String);
//            theXaxisMaxLabelHeight = Math.max(theXaxisMaxLabelHeight, textWidth);
//            angle = Math.PI / 2;
//            writeText(
//                    text_String,
//                    angle,
//                    col - (textHeight / 3),
//                    row + scaleTickLength + (textHeight / 3));
//        }
//        // From the origin left
//        for (int col = this.originCol; col >= this.dataStartCol; col -= increment) {
//            ab = new Line2D.Double(
//                    col,
//                    row,
//                    col,
//                    row + scaleTickLength);
//            draw(ab);
//            BigDecimal x = imageColToXCoordinate(col);
//            if (x.compareTo(BigDecimal.ZERO) == 0 || col == this.originCol) {
//                text_String = "0";
//            } else {
//                //text_String = "" + x.stripTrailingZeros().toPlainString();
//                //text_String = "" + x.round(mc).stripTrailingZeros().toString();
//                //text_String = "" + x.stripTrailingZeros().toString();
//                text_String = "" + Math_BigDecimal.roundStrippingTrailingZeros(
//                        x,
//                        Math_BigDecimal.getDecimalPlacePrecision(x, significantDigits),
//                        _RoundingMode).toString();
//            }
//            textWidth = getTextWidth(text_String);
//            theXaxisMaxLabelHeight = Math.max(theXaxisMaxLabelHeight, textWidth);
//            angle = Math.PI / 2;
//            writeText(
//                    text_String,
//                    angle,
//                    col - (textHeight / 3),
//                    row + scaleTickLength + (textHeight / 3));
//        }
//        // Add the X axis label
//        setPaint(Color.BLACK);
//        text_String = "X";
//        textWidth = getTextWidth(text_String);
//        drawString(
//                text_String,
//                dataStartCol + (dataWidth / 2),
//                row + theXaxisMaxLabelHeight + scaleTickLength + ((3 * textHeight) / 2) + 4);
//        
//        
//        result[0] = xAxisExtraWidthLeft;
//        result[1] = xAxisExtraWidthRight;
//        result[2] = xAxisExtraHeightBottom;
//        
//        return result;
//    
//    }
//    
//    @Override
//    public int[] drawYAxis(
//            int interval,
//            int textHeight,
//            int startAgeOfEndYearInterval,
//            int scaleTickLength,
//            int scaleTickAndTextSeparation,
//            int partTitleGap) {
//        int[] result = new int[1];
//        int yAxisExtraWidthLeft = 0;
//        
//        // Draw Y axis
//        //setOriginCol();
//        setPaint(Color.GRAY);
//        Line2D ab = new Line2D.Double(
//                originCol,
//                dataStartRow,
//                originCol,
//                dataEndRow);
//        draw(ab);
//        // Draw Y axis scale to the left side
//        setPaint(Color.GRAY);
//        int col = dataStartCol;
//        ab = new Line2D.Double(
//                col,
//                dataEndRow,
//                col,
//                dataStartRow);
//        draw(ab);
//        int increment = textHeight;
//        while (((dataHeight * textHeight) + 4) / increment > dataHeight) {
//            increment += textHeight;
//        }
//        int maxTickTextWidth = 0;
//        String text_String;
//        int textWidth;
//        // From the origin up
//        for (int row = this.originRow; row >= this.dataStartRow; row -= increment) {
//            ab = new Line2D.Double(
//                    col,
//                    row,
//                    col - scaleTickLength,
//                    row);
//            draw(ab);
//            BigDecimal y = imageRowToYCoordinate(row);
//            if (y.compareTo(BigDecimal.ZERO) == 0 || row == this.originRow) {
//                text_String = "0";
//            } else {
//                //text_String = "" + y.stripTrailingZeros().toPlainString();
//                //text_String = "" + y.round(mc).stripTrailingZeros().toString();
//                //text_String = "" + y.stripTrailingZeros().toString();
//                text_String = "" + Math_BigDecimal.roundStrippingTrailingZeros(
//                        y,
//                        Math_BigDecimal.getDecimalPlacePrecision(y, significantDigits),
//                        _RoundingMode).toString();
//            }
//             textWidth = getTextWidth(text_String);
//            drawString(
//                    text_String,
//                    col - 3 - scaleTickLength - textWidth,
//                    row + (textHeight / 3));
//            maxTickTextWidth = Math.max(maxTickTextWidth, textWidth);
//        }
//       // From the origin down
//        for (int row = this.originRow; row <= this.dataEndRow; row += increment) {
//            ab = new Line2D.Double(
//                    col,
//                    row,
//                    col - scaleTickLength,
//                    row);
//            draw(ab);
//            BigDecimal y = imageRowToYCoordinate(row);
//            if (y.compareTo(BigDecimal.ZERO) == 0 || row == this.originRow) {
//                text_String = "0";
//            } else {
//                //text_String = "" + y.stripTrailingZeros().toPlainString();
//                    //text_String = "" + y.round(mc).stripTrailingZeros().toString();
//                //text_String = "" + y.stripTrailingZeros().toString();
//                text_String = "" + Math_BigDecimal.roundStrippingTrailingZeros(
//                        y,
//                        Math_BigDecimal.getDecimalPlacePrecision(y, significantDigits),
//                        _RoundingMode).toString();
//            }
//             textWidth = getTextWidth(text_String);
//            drawString(
//                    text_String,
//                    col - 3 - scaleTickLength - textWidth,
//                    row + (textHeight / 3));
//         maxTickTextWidth = Math.max(maxTickTextWidth, textWidth);
//        }
//         yAxisExtraWidthLeft += scaleTickLength + scaleTickAndTextSeparation + maxTickTextWidth;
//        // Y axis label
//        setPaint(Color.BLACK);
//        text_String = "Y";
//        textWidth = getTextWidth(text_String);
//        double angle = 3.0d * Math.PI / 2.0d;
//        col = 3 * textHeight / 2;
//        writeText(
//                text_String,
//                angle,
//                col,
//                dataMiddleRow + (textWidth / 2));
//        result[0] = yAxisExtraWidthLeft;
//        return result;
//    
//    }
    protected void drawPoints(Color color, Chart_ScatterData data) {
        if (data != null) {
            Iterator<BigRational2> ite = data.xyData.iterator();
            setPaint(color);
            while (ite.hasNext()) {
                BigRational2 xy = ite.next();
                Point2D p = coordinateToScreen(xy.getX(),xy.getY());
                draw(p);
            }
        }
    }

    @Override
    public void setOriginCol() {
        originCol = coordinateToScreenCol(Math_BigRational.ZERO);
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
        r.maxX = Math_BigRational.valueOf(Double.MIN_VALUE);
        r.minX = Math_BigRational.valueOf(Double.MAX_VALUE);
        r.maxY = Math_BigRational.valueOf(Double.MIN_VALUE);
        r.minY = Math_BigRational.valueOf(Double.MAX_VALUE);
//        for (int i = -100; i < 328; i++) {         
//            for (int j = -100; j < 0; j++) {
//        for (int i = -100; i < 100; i++) {
//            for (int j = -100; j < 100; j++) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
//        for (int i = -15; i < 10; i++) {
//            for (int j = -9; j < 12; j++) {
                double random_0 = random.nextDouble();
                Math_BigRational x = Math_BigRational.valueOf((i + random.nextDouble()) * random_0);
                Math_BigRational y = Math_BigRational.valueOf(((j + i) / 2) * random_0);
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
