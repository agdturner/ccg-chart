/**
 * Copyright 2015 Andy Turner, The University of Leeds, UK
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

import ch.obermuhlner.math.big.BigRational;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import uk.ac.leeds.ccg.chart.data.Chart_BarData;
import uk.ac.leeds.ccg.chart.data.Chart_Data;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.math.Math_BigDecimal;
import uk.ac.leeds.ccg.generic.util.Generic_Collections;

/**
 * An abstract class for creating Age by Gender Population visualisations and
 * possibly rendering them in a lightweight component as suited to headless
 * rendering.
 */
public abstract class Chart_Bar extends Chart {

    protected int xAxisIncrement;
    protected int numberOfYAxisTicks;
    protected BigDecimal yPin;
    protected BigRational yAxisIncrement;
    protected int barWidth;
    protected int barGap;

    public Chart_Bar(Generic_Environment e) {
        super(e);
    }

    /**
     * @param es The ExecutorService.
     * @param f The Path.
     * @param format The format.
     * @param title The title.
     * @param dataWidth The data width.
     * @param dataHeight The data height.
     * @param xAxisLabel The x axis label.
     * @param yAxisLabel The y axis label.
     * @param drawAxesOnPlot if {@code true} draw lines on the plot.
     * @param ageInterval The age interval.
     * @param startAgeOfEndYearInterval The start age of the end year interval.
     * @param dpc The decimal place precision for calculations.
     * @param sd significant digits
     * @param rm The RoundingMode.
     */
    protected final void init(ExecutorService es, Path f, String format,
            String title, int dataWidth, int dataHeight, String xAxisLabel,
            String yAxisLabel, boolean drawAxesOnPlot, int ageInterval,
            Integer startAgeOfEndYearInterval, int dpc, int sd,
            RoundingMode rm) {
        setAgeInterval(ageInterval);
        setStartAgeOfEndYearInterval(startAgeOfEndYearInterval);
        super.init(es, f, format, title, dataWidth, dataHeight, xAxisLabel,
                yAxisLabel, drawAxesOnPlot, dpc, sd, rm);
    }

    @Override
    public void initialiseParameters(Chart_Data data) {
        Chart_BarData d = (Chart_BarData) data;
        minX = data.minX;
        maxX = BigRational.valueOf(BigDecimal.valueOf(d.map.lastKey()).multiply(d.intervalWidth));
        minY = BigRational.ZERO;
        maxY = data.maxY;
        setCellHeight();
        setCellWidth();
        setOriginRow();
        setOriginCol();
        BigRational cellWidth = getCellWidth();
        if (cellWidth.compareTo(BigRational.ZERO) == 0) {
            barWidth = 1;
        } else {
            barWidth = BigRational.valueOf(d.intervalWidth).divide(cellWidth).integerPart().toBigDecimal().intValue() - (2 * barGap);
        }
        if (barWidth < 1) {
            barWidth = 1;
        }
        int xIncrement;
        xIncrement = xAxisIncrement;
        if (xIncrement == 0) {
            xIncrement = 1;
            xAxisIncrement = xIncrement;
        }
    }

    @Override
    public Chart_BarData getData() {
        return (Chart_BarData) data;
    }
    
    @Override
    public void setOriginCol() {
        originCol = dataStartCol;
        //setOriginCol((getDataStartCol() + getDataEndCol()) / 2);
//        if (minX.compareTo(BigDecimal.ZERO) == 0) {
//            originCol = dataStartCol;
//            //originCol = dataStartCol - dataEndCol / 2;
//        } else {
//            if (cellWidth.compareTo(BigDecimal.ZERO) == 0) {
//                originCol = dataStartCol;
//            } else {
//                originCol = Math_BigDecimal.divideRoundIfNecessary(
//                    BigDecimal.ZERO.subtract(minX),
//                    cellWidth,
//                    0,
//                    _RoundingMode).intValueExact()
//                    + dataStartCol;
//            }
//        }
    }

    /**
     * Draws the Y axis.
     *
     * @param textHeight -
     * @param scaleTickLength -
     * @param seperationDistanceOfAxisAndData -
     * @param partTitleGap -
     * @param scaleTickAndTextSeparation -
     * @return an int[] result for setting display parameters where: result[0] =
     * yAxisExtraWidthLeft;
     */
    //@Override
    public int[] drawYAxis(int textHeight, int scaleTickLength,
            int scaleTickAndTextSeparation, int partTitleGap,
            int seperationDistanceOfAxisAndData) {
        int[] result = new int[1];
        MathContext mc;
        mc = new MathContext(dpc,
                RoundingMode.HALF_UP);
        BigRational rowValue;
        BigRational pin;
        BigRational yIncrement;
        pin = BigRational.valueOf(getyPin());
        yIncrement = getyIncrement();

        if (pin != null) {
            // Initialise rowValue the lowest value in the
            int pinCompareToMinY;
            pinCompareToMinY = pin.compareTo(minY);
            if (pinCompareToMinY != 0) {
                if (pinCompareToMinY == 1) {
                    int pinCompareToMaxY;
                    pinCompareToMaxY = pin.compareTo(maxY);
                    if (pinCompareToMaxY != 1) {
                        rowValue = pin;
                        while (rowValue.compareTo(minY) != 1) {
                            rowValue = rowValue.subtract(yIncrement);
                        }
                    } else {
                        throw new UnsupportedOperationException(this.getClass().getName() + ".drawYAxis(int, BigDecimal, BigDecimal, int, int, int, int)");
                    }
                } else {
                    rowValue = pin;
                    while (rowValue.compareTo(minY) == -1) {
                        rowValue = rowValue.add(yIncrement);
                    }
                }
            } else {
                rowValue = minY;
            }
        } else {
            rowValue = minY;
        }

        int numberOfTicks;
        if (yIncrement != null) {
            if (rowValue != null) {
                numberOfTicks = ((maxY.subtract(rowValue)).divide(yIncrement)).integerPart().toBigDecimal().intValue();
            } else {
                numberOfTicks = ((maxY.subtract(minY)).divide(yIncrement)).integerPart().toBigDecimal().intValue();
            }
        } else {
            numberOfTicks = getnumberOfYAxisTicks();
            if (rowValue != null) {
                yIncrement = (maxY.subtract(rowValue)).divide(BigRational.valueOf(numberOfTicks));
            } else {
                yIncrement = (maxY.subtract(minY)).divide(BigRational.valueOf(numberOfTicks));
            }
        }

        int yAxisExtraWidthLeft = scaleTickLength + scaleTickAndTextSeparation
                + seperationDistanceOfAxisAndData;
        int col = dataStartCol - seperationDistanceOfAxisAndData;
        Line2D ab;
        // Draw Y axis scale to the left side
        setPaint(Color.GRAY);
        ab = new Line2D.Double(col, dataEndRow, col, dataStartRow);
        draw(ab);
        setPaint(Color.GRAY);
        String text_String;
        int textWidth;
        int maxTickTextWidth = 0;
        boolean first = true;
        int row0 = coordinateToScreenRow(rowValue);
        int previousRow = row0;
        for (int i = 0; i < numberOfTicks; i++) {
            int row = coordinateToScreenRow(rowValue);
            setPaint(Color.GRAY);
            ab = new Line2D.Double(col, row, col - scaleTickLength, row);
            draw(ab);
            if (first || (previousRow - row) > textHeight) {
                text_String = "" + rowValue;
                textWidth = getTextWidth(text_String);
                drawString(text_String,
                        col - scaleTickAndTextSeparation - scaleTickLength - textWidth,
                        //row);
                        row + (textHeight / 3));
                maxTickTextWidth = Math.max(maxTickTextWidth, textWidth);
                previousRow = row;
                first = false;
            }
            rowValue = rowValue.add(yIncrement);
        }
        // <drawEndOfYAxisTick>
        int row = coordinateToScreenRow(maxY);
        setPaint(Color.GRAY);
        ab = new Line2D.Double(col, row, col - scaleTickLength, row);
        draw(ab);
        if ((previousRow - row) > textHeight) {
            text_String = "" + rowValue;
            textWidth = getTextWidth(text_String);
            drawString(text_String,
                    col - scaleTickAndTextSeparation - scaleTickLength - textWidth,
                    //row);
                    row + (textHeight / 3));
            maxTickTextWidth = Math.max(maxTickTextWidth, textWidth);
        }
        // </drawEndOfYAxisTick>
        yAxisExtraWidthLeft += maxTickTextWidth;
        // Y axis label
        setPaint(Color.BLACK);
        textWidth = getTextWidth(yAxisLabel);
        double angle = 3.0d * Math.PI / 2.0d;
        col = 3 * textHeight / 2;
        writeText(yAxisLabel, angle, col, dataMiddleRow + (textWidth / 2));
        yAxisExtraWidthLeft += (textHeight * 2) + partTitleGap;
        result[0] = yAxisExtraWidthLeft;
        return result;
    }

    /**
     * Draw the X axis.
     *
     * @param textHeight -
     * @param scaleTickLength -
     * @param seperationDistanceOfAxisAndData -
     * @param partTitleGap -
     * @param scaleTickAndTextSeparation -
     * @return an int[] result for setting display parameters where: result[0] =
     * xAxisExtraWidthLeft; result[1] = xAxisExtraWidthRight; result[2] =
     * xAxisExtraHeightBottom.
     */
    @Override
    public int[] drawXAxis(int textHeight, int scaleTickLength,
            int scaleTickAndTextSeparation, int partTitleGap,
            int seperationDistanceOfAxisAndData) {
//        MathContext mc;
//        mc = new MathContext(getDecimalPlacePrecisionForCalculations(), getRoundingMode());               
////        Object[] intervalCountsLabelsMins;
////        intervalCountsLabelsMins = (Object[]) data[0];
////        TreeMap<Integer, Integer> counts;
////        counts = (TreeMap<Integer, Integer>) intervalCountsLabelsMins[0];
////        TreeMap<Integer, String> labels;
////        labels = (TreeMap<Integer, String>) intervalCountsLabelsMins[1];
////        TreeMap<Integer, BigDecimal> mins;
////        mins = (TreeMap<Integer, BigDecimal>) intervalCountsLabelsMins[2];
//        int xIncrement;
//        xIncrement = getxIncrement();
//        if (xIncrement == 0) {
//            xIncrement = 1;
//            setxIncrement(xIncrement);
//        }
        int xAxisTickIncrement = xAxisIncrement;
        int xIncrementWidth = coordinateToScreenCol(
                BigRational.valueOf(xAxisTickIncrement)) - dataStartCol;
        int[] result = new int[3];
        int xAxisExtraWidthLeft = 0;
        int extraAxisLength;
        //extraAxisLength = xIncrementWidth + (barGap * 2) + (barWidth / 2);
        extraAxisLength = xIncrementWidth + (barGap * 2);
        int xAxisExtraWidthRight = extraAxisLength;
        int xAxisExtraHeightBottom = seperationDistanceOfAxisAndData
                + scaleTickLength + scaleTickAndTextSeparation;
        Line2D ab;
        setPaint(Color.GRAY);
        int row = dataEndRow + seperationDistanceOfAxisAndData;
        // draw XAxis Line
        ab = new Line2D.Double(dataStartCol, row, dataEndCol + extraAxisLength, row);
        draw(ab);
        // Add ticks and labels
        int textRow = row + scaleTickLength + scaleTickAndTextSeparation;
        String text_String;
        int textWidth;
        int maxWidth = 0;
        double angle = 3.0d * Math.PI / 2.0d;
        int colCenterer = barGap + barWidth / 2;
        int col = dataStartCol + colCenterer;
        int previousCol = col;
        boolean first = true;
        Chart_BarData d = getData();
        Iterator<Integer> ite2;
        ite2 = d.counts.keySet().iterator();
        while (ite2.hasNext()) {
            Integer value = ite2.next();
            String label = d.labels.get(value);
            BigRational min = BigRational.valueOf(d.mins.get(value));
            col = coordinateToScreenCol(min) + colCenterer;
            //col = value * xAxisTickIncrement + col0;
            //System.out.println("" + value + ", " + count + ", \"" + label +  "\"");        
            ab = new Line2D.Double(col, row, col, row + scaleTickLength);
            draw(ab);
            if (first || (col - previousCol) > textHeight) {
                text_String = label;
                textWidth = getTextWidth(text_String);
                writeText(text_String, angle, col + (textHeight / 3), textRow + textWidth);

                maxWidth = Math.max(maxWidth, textWidth);
                previousCol = col;
                first = false;
            }
        }
        xAxisExtraWidthLeft += textHeight;
        xAxisExtraHeightBottom += maxWidth;
        xAxisExtraWidthRight += textHeight / 2;
        textRow += maxWidth + partTitleGap + textHeight;
        xAxisExtraHeightBottom += partTitleGap + textHeight;
        setPaint(Color.BLACK);
        text_String = xAxisLabel;
        textWidth = getTextWidth(text_String);
        drawString(text_String,
                (dataEndCol - dataStartCol) / 2 + dataStartCol - textWidth / 2,
                textRow);
        int endOfAxisLabelCol = (dataEndCol - dataStartCol) / 2 + dataStartCol + textWidth / 2 + 1; // Add one to cover rounding issues.
        if (endOfAxisLabelCol > dataEndCol + xAxisExtraWidthRight) {
            int diff = endOfAxisLabelCol - (dataEndCol + xAxisExtraWidthRight);
            xAxisExtraWidthRight += diff;
        }
        xAxisExtraHeightBottom += textHeight + 2;
        result[0] = xAxisExtraWidthLeft;
        result[1] = xAxisExtraWidthRight;
        result[2] = xAxisExtraHeightBottom;
        return result;
    }

//    /**
//     * Draw the X axis.
//     *
//     * @param seperationDistanceOfAxisAndData
//     * @param partTitleGap
//     * @param scaleTickAndTextSeparation
//     * @return an int[] result for setting display parameters where: result[0] =
//     * xAxisExtraWidthLeft; result[1] = xAxisExtraWidthRight; result[2] =
//     * xAxisExtraHeightBottom.
//     */
//    @Override
//    public int[] drawXAxis(
//            int textHeight,
//            int scaleTickLength,
//            int scaleTickAndTextSeparation,
//            int partTitleGap,
//            int seperationDistanceOfAxisAndData) {
//
//        int xAxisTickIncrement = getxIncrement();
//        int xIncrementWidth = coordinateToScreenCol(BigDecimal.valueOf(xAxisTickIncrement)) - getDataStartCol();
//
//        int[] result = new int[3];
//        int xAxisExtraWidthLeft = 0;
//        int xAxisExtraWidthRight = xIncrementWidth;
//        int xAxisExtraHeightBottom = seperationDistanceOfAxisAndData
//                + scaleTickLength + scaleTickAndTextSeparation;
//        int dataStartCol = getDataStartCol();
//        //int originRow = getOriginRow();
//        int dataEndRow = getDataEndRow();
//        int dataEndCol = getDataEndCol();
////        setDataEndCol(dataEndCol);
////        setDataWidth(getDataWidth() + xIncrementWidth);
////        setImageWidth(getImageWidth() + xIncrementWidth);
//        BigDecimal maxX = getMaxX();
//        int significantDigits = getSignificantDigits();
//        Line2D ab;
//        setPaint(Color.GRAY);
//        int row = dataEndRow + seperationDistanceOfAxisAndData;
//        // draw XAxis Line
//        ab = new Line2D.Double(
//                dataStartCol,
//                row,
//                dataEndCol + xIncrementWidth,
//                row);
//        draw(ab);
//        // Add ticks and labels
//        int textRow = row + scaleTickLength + scaleTickAndTextSeparation;
//        String text_String;
//        int textWidth;
//        MathContext mc = new MathContext(
//                significantDigits, RoundingMode.CEILING);
//        //int mini = getMinY().intValue();
//        //int maxi = maxX.round(mc).intValueExact();
//        
//        BigDecimal minX;
//        minX = getMinX();
//        int iterations;
//        iterations = maxX.subtract(minX).divide(BigDecimal.valueOf(xAxisTickIncrement), mc).intValue();
//        int maxWidth = 0;
//        double angle = 3.0d * Math.PI / 2.0d;
//        
//        BigDecimal x;
//        x = minX;
//        
//        BigDecimal xIncrement;
//        xIncrement = BigDecimal.valueOf(xAxisTickIncrement);
//        int col0;
//        int col = getDataStartCol() + getBarGap() + getBarWidth() / 2;
//        col0 = col;
////        for (int i = mini; i <= maxi + xAxisTickIncrement; i += xAxisTickIncrement) {
//        for (int i = 0; i <= iterations; i ++) {
//            ab = new Line2D.Double(
//                    col,
//                    row,
//                    col,
//                    row + scaleTickLength);
//            draw(ab);
//            if (i == 0 || (col - col0) > textHeight) {
//                text_String = "" + x;
//
//                textWidth = getTextWidth(text_String);
//
////            int textRow0 = textRow + textWidth;
//                writeText(
//                        text_String,
//                        angle,
//                        col + (textHeight / 3), //col - (textWidth / 2),
//                        textRow + textWidth);
//
//                maxWidth = Math.max(maxWidth, textWidth);
//                col0 = col;
//            }
////            drawString(
////                    text_String,
////                    col - (textWidth / 2),
////                    textRow);
//            if (i == 0) {
//                // Add to imageWidth as this label sticks out
//                //xAxisExtraWidthLeft += (textWidth / 2) + textHeight;
//                xAxisExtraWidthLeft += textHeight;
//            }
//            x = x.add(xIncrement);
//            col += coordinateToScreenCol(x);
//
//            
//
//        }
//        xAxisExtraHeightBottom += maxWidth;
////        // Check to see if plot needs to grow
////        if (xAxisExtraWidthLeft > xAxisExtraWidthLeft) {
////            int diff = xAxisExtraWidthLeft - dataStartCol;
////            imageWidth += diff;
////            dataStartCol += diff;
////            dataEndCol += diff;
////            xAxisExtraWidthLeft = xAxisExtraWidthLeft;
////            setOriginCol();
////        }
//
//        // Add to imageWidth as this label sticks out
////        xAxisExtraWidthRight += (textWidth / 2) + textHeight;
//        xAxisExtraWidthRight += textHeight / 2;
////        if (xAxisExtraWidthRight > xAxisExtraWidthRight) {
////            imageWidth += axesExtraWidthRight - xAxisExtraWidthRight;
////            xAxisExtraWidthRight = axesExtraWidthRight;
////        }
//        // Add axis labels
////        textRow += textHeight + partTitleGap;
//        textRow += maxWidth + partTitleGap + textHeight;
////        xAxisExtraHeightBottom += textHeight + partTitleGap;
//        xAxisExtraHeightBottom += partTitleGap + textHeight;
//        setPaint(Color.BLACK);
//        text_String = getxAxisLabel();
////        text_String = "Population";
//        textWidth = getTextWidth(text_String);
//        drawString(
//                text_String,
//                (dataEndCol - dataStartCol) / 2 - (textWidth / 2),
//                //dataEndCol + xAxisExtraHeightBottom + 2,
//                textRow);
//        xAxisExtraHeightBottom += textHeight + 2;
//        result[0] = xAxisExtraWidthLeft;
//        result[1] = xAxisExtraWidthRight;
//        result[2] = xAxisExtraHeightBottom;
//        return result;
//    }
    public int getnumberOfYAxisTicks() {
        return numberOfYAxisTicks;
    }

    public void setnumberOfYAxisTicks(int numberOfYAxisTicks) {
        this.numberOfYAxisTicks = numberOfYAxisTicks;
    }

    /**
     * @return the yPin
     */
    public BigDecimal getyPin() {
        return yPin;
    }

    /**
     * @param yPin the yPin to set
     */
    public void setyPin(BigDecimal yPin) {
        this.yPin = yPin;
    }

    /**
     * @return the yAxisIncrement
     */
    public BigRational getyIncrement() {
        return yAxisIncrement;
    }

    /**
     * @param yIncrement the yAxisIncrement to set
     */
    public void setyIncrement(BigRational yIncrement) {
        this.yAxisIncrement = yIncrement;
    }
}
