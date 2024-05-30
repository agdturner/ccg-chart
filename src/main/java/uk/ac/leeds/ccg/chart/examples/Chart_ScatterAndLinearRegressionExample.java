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
import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.math3.stat.regression.SimpleRegression;
//import org.apache.commons.math.stat.regression.SimpleRegression;
import uk.ac.leeds.ccg.chart.data.Chart_ID;
import uk.ac.leeds.ccg.chart.data.Chart_Point;
import uk.ac.leeds.ccg.chart.data.Chart_ScatterData;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;

/**
 * An example of generating a Scatter Plot visualization with a linear
 * regression line.
 */
public class Chart_ScatterAndLinearRegressionExample extends Chart_ScatterExample {

    public Chart_ScatterAndLinearRegressionExample(Generic_Environment e) {
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
     * @param drawOriginLinesOnPlot If {@code true} origin lines are drawn.
     * @param oom The Order of Magnitude for rounding precision.
     * @param rm The RoundingMode.
     */
    public Chart_ScatterAndLinearRegressionExample(Generic_Environment e,
            ExecutorService es, Path f, String format, String title,
            int dataWidth, int dataHeight, String xAxisLabel, String yAxisLabel,
            boolean drawOriginLinesOnPlot, int oomx, int oomy, RoundingMode rm) {
        super(e);
        init(es, f, format, title, dataWidth, dataHeight, xAxisLabel,
                yAxisLabel, drawOriginLinesOnPlot, oomx, oomy, rm);
    }

    public static void main(String[] args) {
        try {
            Generic_Environment e = new Generic_Environment(new Generic_Defaults());
            /**
             * Initialise title and Path to write image to
             */
            String title;
            Path file;
            String format = "PNG";
            if (args.length != 2) {
                System.out.println("Expected 2 args: args[0] title; args[1] Path."
                        + " Recieved " + args.length + " args.");
                // Use defaults
                title = "Scatter Plot And Linear Regression";
                System.out.println("Default title: " + title);
                file = Paths.get(System.getProperty("user.dir"),
                        "data", "output",
                        title.replace(" ", "_") + "." + format);
                System.out.println("Use default Path: " + file.toString());
            } else {
                title = args[0];
                file = Paths.get(args[1]);
            }
            int dataWidth = 256;//250;
            int dataHeight = 256;
            String xAxisLabel = "Expected (X)";
            String yAxisLabel = "Observed (Y)";
            boolean drawOriginLinesOnPlot = false;//true;
            int oomx = -2;
            int oomy = -1;
            RoundingMode rm = RoundingMode.HALF_UP;
            ExecutorService es = Executors.newSingleThreadExecutor();
            Chart_ScatterAndLinearRegressionExample plot;
            plot = new Chart_ScatterAndLinearRegressionExample(e, es, file, format,
                    title, dataWidth, dataHeight, xAxisLabel, yAxisLabel,
                    drawOriginLinesOnPlot, oomx, oomy, rm);
            plot.setData(plot.getDefaultData());
            plot.vis.getHeadlessEnvironment();
            plot.run();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public void drawData() {
        double[][] dataD;
        dataD = getDataAsDoubleArray(getData().data);
        drawYEqualsXLineData(dataD);
        /*
         * rp[0] is the y axis intercept;
         * rp[1] is the change in y relative to x (gradient or slope);
         * rp[2] is the rank correlation coefficient (RSquare);
         * rp[3] is data[0].length.
         */
        double[] rp;
        rp = getSimpleRegressionParameters(dataD);
        drawRegressionLine(rp, dataD);
        drawPoints(Color.DARK_GRAY, getData());
        if (addLegend) {
            drawLegend(rp);
        }
    }

    @Override
    public Dimension draw() {
        drawOutline();
        drawTitle(title);
        //System.out.println("dataStartCol " + dataStartCol);
        drawAxes(0, 0);
        if (data == null) {
            data = getDefaultData();
        }
        Chart_ScatterData d = getData();
        drawPoints(Color.DARK_GRAY, d);
        double[][] dataAsDoubleArray = getDataAsDoubleArray(getData().data);
        drawYEqualsXLineData(dataAsDoubleArray);
        /*
         * rp[0] is the y axis intercept;
         * rp[1] is the change in y relative to x (gradient or
         * slope); rp[2] is the rank correlation coefficient
         * (RSquare); rp[3] is data[0].length.
         */
        double[] rp;
        rp = getSimpleRegressionParameters(dataAsDoubleArray);
        drawRegressionLine(rp, dataAsDoubleArray);
        if (addLegend) {
            drawLegend(rp);
        }
        Dimension newDim = new Dimension(imageWidth, imageHeight);
        return newDim;
    }

//    /**
//     * Draws the X axis returns the height
//     */
//    @Override
//    public int[] drawXAxis(
//            int textHeight,
//            int scaleTickLength,
//            int scaleTickAndTextSeparation,
//            int partTitleGap) {
//        int[] result = new int[3];
//
//        int xAxisExtraWidthLeft = 0;
//        int xAxisExtraWidthRight = 0;
//        //int seperationDistanceOfAxisAndData = partTitleGap;
//        //int seperationDistanceOfAxisAndData = partTitleGap * 2;
//        int seperationDistanceOfAxisAndData = textHeight;
//        int xAxisExtraHeightBottom = scaleTickLength + scaleTickAndTextSeparation + seperationDistanceOfAxisAndData;
////                    row + scaleTickLength + (textHeight / 3));;
//
//        setPaint(Color.LIGHT_GRAY);
//        Line2D ab = new Line2D.Double(
//                dataStartCol,
//                originRow,
//                dataEndCol,
//                originRow);
//        draw(ab);
//        // Draw X axis scale below the data
//        setPaint(Color.GRAY);
//        //int row = dataEndRow - text_Height;
//        //int row = dataEndRow - partTitleGap;
//        int row = dataEndRow + seperationDistanceOfAxisAndData;
//        ab = new Line2D.Double(
//                dataStartCol,
//                row,
//                dataEndCol,
//                row);
//        draw(ab);
//        int increment = textHeight;
//        while (((dataWidth * textHeight) + 4) / increment > dataWidth) {
//            increment += textHeight;
//        }
//        int xAxisMaxLabelHeight = 0;
//        String text_String;
//        int textWidth;
//        double angle;
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
//                text_String = "" + Generic_BigDecimal.roundStrippingTrailingZeros(
//                        x,
//                        Generic_BigDecimal.getDecimalPlacePrecision(x, significantDigits),
//                        _RoundingMode).toString();
//            }
//            textWidth = getTextWidth(text_String);
//            xAxisMaxLabelHeight = Math.max(xAxisMaxLabelHeight, textWidth);
//            angle = Math.PI / 2;
//            writeText(
//                    text_String,
//                    angle,
//                    col - (textHeight / 3),
//                    row + scaleTickAndTextSeparation + scaleTickLength);
////                    row + scaleTickLength + (textHeight / 3));
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
//                text_String = "" + Generic_BigDecimal.roundStrippingTrailingZeros(
//                        x,
//                        Generic_BigDecimal.getDecimalPlacePrecision(x, significantDigits),
//                        _RoundingMode).toString();
//            }
//            textWidth = getTextWidth(text_String);
//            xAxisMaxLabelHeight = Math.max(xAxisMaxLabelHeight, textWidth);
//            angle = Math.PI / 2;
//            writeText(
//                    text_String,
//                    angle,
//                    col - (textHeight / 3),
//                    row + scaleTickAndTextSeparation + scaleTickLength);
////                    row + scaleTickLength + (textHeight / 3));
//        }
//        xAxisExtraHeightBottom += xAxisMaxLabelHeight;
//        //xAxisExtraHeightBottom += scaleTickAndTextSeparation + scaleTickLength + seperationDistanceOfAxisAndData;
//        xAxisExtraWidthRight += textHeight * 2;
//        xAxisExtraWidthLeft += textHeight / 2;
//
//        // Add the X axis label
//        setPaint(Color.BLACK);
//        text_String = xAxisLabel;
//        //textWidth = getTextWidth(text_String);
//
//        xAxisExtraHeightBottom += partTitleGap;
//
//        drawString(
//                text_String,
//                dataStartCol + (dataWidth / 2),
//                row + xAxisExtraHeightBottom);
//
//        if (!addLegend) {
//            xAxisExtraHeightBottom += textHeight;
//        }
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
//            int interval, // ignored
//            int textHeight,
//            int startAgeOfEndYearInterval, // ignored
//            int scaleTickLength,
//            int scaleTickAndTextSeparation,
//            int partTitleGap) {
//        int[] result = new int[1];
//        //int seperationDistanceOfAxisAndData = partTitleGap * 2;
//        int seperationDistanceOfAxisAndData = textHeight;
//        int yAxisExtraWidthLeft = scaleTickLength + scaleTickAndTextSeparation + seperationDistanceOfAxisAndData;
//
//        Line2D ab;
//        int text_Height = getTextHeight();
//        String text_String;
//        int text_Width;
//        int row;
//        int increment;
//
//        // Draw Y axis
//        //setOriginCol();
//        setPaint(Color.GRAY);
//        ab = new Line2D.Double(
//                originCol,
//                dataStartRow,
//                originCol,
//                dataEndRow);
//        draw(ab);
//
//        // Draw Y axis labels
//        setPaint(Color.GRAY);
//        //int col = dataStartCol - text_Height;
//        //int col = dataStartCol - partTitleGap;
//        int col = dataStartCol - seperationDistanceOfAxisAndData;
//        ab = new Line2D.Double(
//                col,
//                dataEndRow,
//                col,
//                dataStartRow);
//        draw(ab);
//        increment = text_Height;
//        while (((dataHeight * text_Height) + 4) / increment > dataHeight) {
//            increment += text_Height;
//        }
//        int yAxisMaxLabelWidth = 0;
//        // From the origin up
//        for (row = this.originRow; row >= this.dataStartRow; row -= increment) {
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
//                text_String = "" + Generic_BigDecimal.roundStrippingTrailingZeros(
//                        y,
//                        Generic_BigDecimal.getDecimalPlacePrecision(y, significantDigits),
//                        _RoundingMode).toString();
//            }
//            text_Width = getTextWidth(text_String);
//            yAxisMaxLabelWidth = Math.max(yAxisMaxLabelWidth, text_Width);
//            drawString(
//                    text_String,
//                    col - scaleTickAndTextSeparation - scaleTickLength - text_Width,
//                    row + (textHeight / 3));
//        }
//        // From the origin down
//        for (row = this.originRow; row <= this.dataEndRow; row += increment) {
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
//                text_String = "" + Generic_BigDecimal.roundStrippingTrailingZeros(
//                        y,
//                        Generic_BigDecimal.getDecimalPlacePrecision(y, significantDigits),
//                        _RoundingMode).toString();
//            }
//            text_Width = getTextWidth(text_String);
//            yAxisMaxLabelWidth = Math.max(yAxisMaxLabelWidth, text_Width);
//            drawString(
//                    text_String,
//                    col - scaleTickAndTextSeparation - scaleTickLength - text_Width,
//                    row + (textHeight / 3));
//        }
//        yAxisExtraWidthLeft += scaleTickLength + scaleTickAndTextSeparation + yAxisMaxLabelWidth;
//
//        // Add the Y axis label
//        setPaint(Color.BLACK);
//        text_String = yAxisLabel;
//        text_Width = getTextWidth(text_String);
//        yAxisExtraWidthLeft += (textHeight * 2) + partTitleGap;
//        double angle = 3.0d * Math.PI / 2.0d;
//        writeText(
//                text_String,
//                angle,
//                3 * textHeight / 2,
//                dataMiddleRow + (text_Width / 2));
//        // Draw Y axis
//        setPaint(Color.LIGHT_GRAY);
//        ab = new Line2D.Double(
//                originCol,
//                dataStartRow,
//                originCol,
//                dataEndRow);
//
//        draw(ab);
//
//
//        result[0] = yAxisExtraWidthLeft;
//        return result;
//
//    }
//
//    @Override
//    public void setOriginCol() {
//        originCol = getCol(BigDecimal.ZERO);
//        System.out.println("originCol " + originCol);
////        if (minX.compareTo(BigDecimal.ZERO) == 0) {
////            originCol = dataStartCol;
////            //originCol = dataStartCol - dataEndCol / 2;
////        } else {
////            if (cellWidth.compareTo(BigDecimal.ZERO) == 0) {
////                originCol = dataStartCol;
////            } else {
////                originCol = Generic_BigDecimal.divideRoundIfNecessary(
////                        minX,
////                        cellWidth,
////                        0,
////                        _RoundingMode).intValueExact()
////                        + dataStartCol;
////            }
////        }
//    }
    /**
     * @param rp rp[0] is the y axis intercept; rp[1] is the change in y
     * relative to x (gradient or slope); rp[2] is the rank correlation
     * coefficient (RSquare); rp[3] is data[0].length.
     */
    protected void drawLegend(
            double[] rp) {
//        int[] result = new int[3];

        int newLegendWidth = 0;
        int newLegendHeight = 0;
//        int legendExtraWidthLeft = 0;
//        int legendExtraWidthRight = 0;
        int textHeight = getTextHeight();
        int legendExtraHeightBottom = textHeight;

        int legendStartRow = dataEndRow + xAxisHeight;
//        int legendStartRow = this.dataEndRow + this.xAxisHeight / 2;
        int symbolRow;
        int row;
        int symbolCol;
        int col;
        int symbolWidth = 10;
        // Legend Title
        setPaint(Color.DARK_GRAY);
        int legendItemWidth = 0;

        String text = "Legend";
        int textWidth = getTextWidth(text);
        newLegendHeight += textHeight;
        row = legendStartRow + newLegendHeight;
        //col = dataStartCol - yAxisWidth;
        col = textHeight;
        legendItemWidth += textWidth;
        newLegendWidth = Math.max(newLegendWidth, legendItemWidth);
        drawString(
                text,
                col,
                row);
        Point2D.Double point = new Point2D.Double();

        // Point marker
        legendItemWidth = 0;
        newLegendHeight += textHeight;
        symbolRow = legendStartRow + newLegendHeight;
        legendItemWidth += symbolWidth;
        symbolCol = col + symbolWidth / 2;
        point.setLocation(
                symbolCol,
                symbolRow);
        setPaint(Color.DARK_GRAY);
        draw(point);
        row += ((3 * textHeight) / 2) - 2;
        newLegendHeight += (textHeight / 2) - 2;
        col += symbolWidth + 4;
        setPaint(Color.GRAY);
        text = "Data Point";
        textWidth = getTextWidth(text);
        legendItemWidth += textWidth;
        newLegendWidth = Math.max(newLegendWidth, legendItemWidth);
        drawString(
                text,
                col,
                row);
        // Y = X line
        setPaint(Color.LIGHT_GRAY);
        //int itemSymbolWidth = (symbolCol + symbolWidth / 2) - (symbolCol - symbolWidth / 2);
        //legendItemWidth = itemSymbolWidth;        
        //legendItemWidth = symbolWidth + 4;
        symbolRow += textHeight;
        draw(new Line2D.Double(
                symbolCol - symbolWidth / 2,
                (symbolRow + textHeight / 2) - 2,
                symbolCol + symbolWidth / 2,
                (symbolRow - textHeight / 2) + 2));
        setPaint(Color.GRAY);
        row += textHeight;
        text = "Y = X";
        textWidth = getTextWidth(text);
        drawString(
                text,
                col,
                row);
        legendItemWidth = textWidth;
        newLegendWidth = Math.max(newLegendWidth, legendItemWidth);
        newLegendHeight += textHeight + 4;

        // Regression line
        setPaint(Color.DARK_GRAY);
        legendItemWidth = symbolWidth + 4;
        symbolRow += textHeight;
        row += textHeight;
        draw(new Line2D.Double(
                symbolCol - symbolWidth / 2,
                (symbolRow + textHeight / 2) - 2,
                symbolCol + symbolWidth / 2,
                (symbolRow - textHeight / 2) + 2));
        setPaint(Color.GRAY);
        // Y = mX + c
        // generalise m
        int scale = 4;
        BigDecimal m;
        if (Double.isNaN(rp[1])) {
            m = BigDecimal.ZERO;
        } else {
            m = BigDecimal.valueOf(rp[1]);
        }
        RoundingMode roundingMode = getRoundingMode();
        m = m.setScale(scale, roundingMode);
        m = m.stripTrailingZeros();
//        m = Generic_BigDecimal.roundStrippingTrailingZeros(
//                m, 
//                decimalPlacePrecision,
//                _RoundingMode);
        BigDecimal c;
        if (Double.isNaN(rp[0])) {
            c = BigDecimal.ZERO;
        } else {
            c = BigDecimal.valueOf(rp[0]);
        }
        c = c.setScale(scale, roundingMode);
        c = c.stripTrailingZeros();
        BigDecimal rsquare;
        if (Double.isNaN(rp[2])) {
            rsquare = BigDecimal.ZERO;
        } else {
            rsquare = BigDecimal.valueOf(rp[2]);
        }
        rsquare = rsquare.setScale(3, roundingMode);
        rsquare = rsquare.stripTrailingZeros();
        String equation;
        if (c.compareTo(BigDecimal.ZERO) != -1) {
            equation = "Y = (" + m + " * X) + " + c + "";
        } else {
            equation = "Y = (" + m + " * X) - " + c.negate() + "";
        }
        drawString(
                equation,
                col,
                row);
        textWidth = getTextWidth(equation);
        legendItemWidth += textWidth;
        newLegendWidth = Math.max(newLegendWidth, legendItemWidth);
        newLegendHeight += textHeight;

        // Rsquare component
        String rsquare_String = "RSquare = " + rsquare;
        textWidth = getTextWidth(rsquare_String);
        legendItemWidth = textWidth;
        row += textHeight;
        drawString(
                rsquare_String,
                col,
                row);
        //setLegendHeight(row - legendStartRow);
        newLegendWidth = Math.max(newLegendWidth, legendItemWidth);
        newLegendHeight += (2 * textHeight);

        if (newLegendWidth > legendWidth) {
            //int diff = newLegendWidth - legendWidth;
            if (newLegendWidth > imageWidth) {
                imageWidth = newLegendWidth;
                extraWidthRight = (newLegendWidth - extraWidthLeft - dataWidth);
            }
            legendWidth = newLegendWidth;
        }
        if (newLegendHeight > legendHeight) {
            //int diff = newLegendHeight - legendHeight;
            //int heightForLegend = legendStartRow - dataStartRow + newLegendHeight;
            int newExtraHeightBottom = newLegendHeight + xAxisHeight;
            if (newExtraHeightBottom > extraHeightBottom) {
                int diff2 = newExtraHeightBottom - extraHeightBottom;
                extraHeightBottom = newExtraHeightBottom;
                imageHeight = imageHeight + diff2;
            }
            legendHeight = newLegendHeight;
        }
//        result[0] = legendExtraWidthLeft;
//        result[1] = legendExtraWidthRight;
//        result[2] = legendExtraHeightBottom;

//        return result;
    }

    protected double[][] getDataAsDoubleArray() {
        return getDataAsDoubleArray(getData().data);
    }

    protected double[][] getDataAsDoubleArray(
            HashMap<Chart_ID, Chart_Point> data) {
        double[][] r = new double[2][data.size()];
        Iterator<Chart_Point> ite = data.values().iterator();
        Chart_Point xyData;
        /*
         * data[0][] are the y values data[1][] are the x values
         */
        int n = 0;
        while (ite.hasNext()) {
            xyData = ite.next();
            r[0][n] = xyData.getY().toDouble();
            r[1][n] = xyData.getX().toDouble();
            n++;
        }
        return r;
    }

    protected void drawYEqualsXLineData(double[][] dataAsDoubleArray) {
        double[][] yEqualsXLineData = getYEqualsXLineData(
                dataAsDoubleArray);
        setPaint(Color.LIGHT_GRAY);
        draw(new Line2D.Double(
                getCol(BigRational.valueOf(yEqualsXLineData[1][0])),
                getRow(BigRational.valueOf(yEqualsXLineData[0][0])),
                getCol(BigRational.valueOf(yEqualsXLineData[1][1])),
                getRow(BigRational.valueOf(yEqualsXLineData[0][1]))));
    }

    protected void drawRegressionLine(
            double[] rp,
            double[][] dataAsDoubleArray) {
        double[][] regressionLineXYLineData = getXYLineData(
                dataAsDoubleArray,
                rp);
        setPaint(Color.BLACK);
        draw(new Line2D.Double(
                getCol(BigRational.valueOf(regressionLineXYLineData[1][0])),
                getRow(BigRational.valueOf(regressionLineXYLineData[0][0])),
                getCol(BigRational.valueOf(regressionLineXYLineData[1][1])),
                getRow(BigRational.valueOf(regressionLineXYLineData[0][1]))));
//                getCol(BigDecimal.valueOf(regressionLineXYLineData[0][1])),
//                getRow(BigDecimal.valueOf(regressionLineXYLineData[1][0])),
//                getCol(BigDecimal.valueOf(regressionLineXYLineData[0][0])),
//                getRow(BigDecimal.valueOf(regressionLineXYLineData[1][1]))));
    }

    /**
     * @param data double[2][] where: data[0][] are the y values data[1][] are
     * the x values
     * @return double[] result where: <ul> <li>result[0] is the y axis
     * intercept;</li> <li>result[1] is the change in y relative to x (gradient
     * or slope);</li> <li>result[2] is the rank correlation coefficient
     * (RSquare);</li> <li>result[3] is data[0].length.</li> </ul>
     */
    public static double[] getSimpleRegressionParameters(double[][] data) {
        double[] result = new double[4];
        // org.apache.commons.math.stat.regression.SimpleRegression;
        SimpleRegression a_SimpleRegression = new SimpleRegression();
        //System.out.println("data.length " + data[0].length);
        for (int i = 0; i < data[0].length; i++) {
            a_SimpleRegression.addData(data[1][i], data[0][i]);
            //aSimpleRegression.addData(data[0][i], data[1][i]);
        }
        result[0] = a_SimpleRegression.getIntercept();
        result[1] = a_SimpleRegression.getSlope();
        result[2] = a_SimpleRegression.getRSquare();
        result[3] = data[0].length;
        return result;
    }

    /**
     * @param data Data.
     * @param lp lineParameters
     * @return double[][] r the line
     */
    public static double[][] getXYLineData(double[][] data, double[] lp) {
        double[][] r = new double[2][2];
        double miny = Double.MAX_VALUE;
        double maxy = -Double.MAX_VALUE;
        double minx = Double.MAX_VALUE;
        double maxx = -Double.MAX_VALUE;
        for (int j = 0; j < data[0].length; j++) {
            minx = Math.min(minx, data[0][j]);
            maxx = Math.max(maxx, data[0][j]);
            miny = Math.min(miny, data[1][j]);
            maxy = Math.max(maxy, data[1][j]);
        }
        r[0][0] = minx;
        r[0][1] = maxx;
        r[1][0] = miny;
        r[1][1] = maxy;
//        System.out.println("miny " + minx);
//        System.out.println("maxy " + maxx);
//        System.out.println("minx " + miny);
//        System.out.println("maxx " + maxy);
        double m = lp[1];
        double c = lp[0];
        // y = (m * x) + c
        // x = (y - c) / m
        // minyx stores the y at minx
        double minyx;
        // maxyx stores the y at maxx 
        double maxyx;
        if (m != 0) {
            minyx = (minx - c) / m;
            maxyx = (maxx - c) / m;
        } else {
            minyx = miny;
            maxyx = maxy;
        }
        // minxy stores the x at miny
        double minxy = (m * miny) + c;
        // maxxy stores the x at maxy
        double maxxy = (m * maxy) + c;

        if (maxxy < maxx) {
            r[0][1] = maxxy;
        } else {
            r[1][1] = maxyx;
        }
        if (minxy > minx) {
            r[0][0] = minxy;
        } else {
            r[1][0] = minyx;
        }

        if (maxyx < maxy) {
            r[1][1] = maxyx;
        } else {
            r[0][1] = maxxy;
        }

        if (minyx > miny) {
            r[1][0] = minyx;
        } else {
            r[0][0] = minxy;
        }

        if (Double.isNaN(r[1][0])) {
            if (Double.isNaN(r[0][0])) {
                r[1][0] = 0;
                r[0][0] = 0;
            } else {
                r[1][0] = r[0][0];
                //result[1][0] = 0;
            }
        }
        if (Double.isNaN(r[1][1])) {
            if (Double.isNaN(r[0][1])) {
                r[1][1] = 0;
                r[0][1] = 0;
            } else {
                r[1][1] = r[0][1];
                //result[1][1] = 0;
            }
        }
        System.out.println("Line Segment");
        System.out.println("(minx,miny) (" + r[1][0] + "," + r[0][0] + ")");
        System.out.println("(maxx,maxy) (" + r[1][1] + "," + r[0][1] + ")");
        return r;
    }

    /**
     * @param data The data
     * @return The y equals x line.
     */
    public static double[][] getYEqualsXLineData(double[][] data) {
        double[][] lineChartData = new double[2][2];
        // minx is the minimum x value in data[1]
        double minx = Double.MAX_VALUE;
        // maxx is the maximum x value in data[1]
        double maxx = -Double.MAX_VALUE;
        // miny is the minimum y value in data[0]
        double miny = Double.MAX_VALUE;
        // maxy is the maximum y value in data[1]
        double maxy = -Double.MAX_VALUE;
        for (int j = 0; j < data[0].length; j++) {
            miny = Math.min(miny, data[0][j]);
            maxy = Math.max(maxy, data[0][j]);
            minx = Math.min(minx, data[1][j]);
            maxx = Math.max(maxx, data[1][j]);
        }
        lineChartData[0][0] = miny;
        lineChartData[0][1] = maxy;
        lineChartData[1][0] = minx;
        lineChartData[1][1] = maxx;
        //System.out.println("maxx " + maxx);
        if (maxx < maxy) {
            lineChartData[0][1] = maxx;
        } else {
            lineChartData[1][1] = maxy;
        }
        if (minx > miny) {
            lineChartData[0][0] = minx;
        } else {
            lineChartData[1][0] = miny;
        }
        return lineChartData;
    }

    /**
     *
     * @param simpleRegressionParameters are as per the output returned from
     * getSimpleRegressionParameters(double[][])
     * @return String[] result where; result[0] is of the form "Y = m * X + c"
     * where m and c are numbers result[1]
     */
    public static String[] getSimpleRegressionParametersStrings(double[] simpleRegressionParameters) {
        String[] result = new String[3];
        result[0] = "Y = " + simpleRegressionParameters[1] + " * X + " + simpleRegressionParameters[0];
        result[1] = "RSquare " + simpleRegressionParameters[2];
        result[2] = "n " + simpleRegressionParameters[3];
        System.out.println(result[0]);
        System.out.println(result[1]);
        System.out.println(result[2]);
        return result;
    }
}
