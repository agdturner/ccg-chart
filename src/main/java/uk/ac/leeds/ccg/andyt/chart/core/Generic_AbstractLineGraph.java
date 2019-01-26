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
package uk.ac.leeds.ccg.andyt.chart.core;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import uk.ac.leeds.ccg.andyt.generic.util.Generic_Collections;
import uk.ac.leeds.ccg.andyt.math.Generic_BigDecimal;

/**
 * An abstract class for creating Age by Gender Population visualisations and
 * possibly rendering them in a lightweight component as suited to headless
 * rendering.
 */
public abstract class Generic_AbstractLineGraph extends Generic_AbstractPlot {

    protected BigDecimal yMax;

    /**
     * yPin are a set of values that must appear if possible on the Y axis. The
     * most important of these is the first in the list, the least important is
     * the last. It is expected that only those that can fit on the axis will be
     * included.
     */
    protected ArrayList<BigDecimal> yPin;

    /**
     * If this is set, this is the distance desired between Y axis ticks.
     */
    protected BigDecimal yIncrement;

    /**
     * The number of Y axis ticks wanted in total (other than yPins).
     */
    protected int numberOfYAxisTicks;

    /**
     * The number of Y axis ticks wanted that are greater than zero.
     */
    protected int numberOfYAxisTicksGT0;

    /**
     * The number of Y axis ticks wanted that are less than zero.
     */
    protected int numberOfYAxisTicksLT0;

    protected TreeMap<BigDecimal, ?> xAxisLabels;
    protected BigDecimal xMax;
    protected BigDecimal xPin;
    protected BigDecimal xIncrement;
    protected int numberOfXAxisTicks;

    private Color[] colours;
    private ArrayList<String> labels;

    /**
     *
     * @param es -
     * @param f -
     * @param fmt Format expecting PNG
     * @param title -
     * @param dataWidth -
     * @param dataHeight -
     * @param xAxisLabel -
     * @param yAxisLabel -
     * @param drawAxesOnPlot -
     * @param ageInterval -
     * @param startAgeOfEndYearInterval -
     * @param decimalPlacePrecisionForCalculations -
     * @param significantDigits -
     * @param rm -
     */
    protected final void init(ExecutorService es, File f, String fmt,
            String title, int dataWidth, int dataHeight, String xAxisLabel,
            String yAxisLabel, boolean drawAxesOnPlot, int ageInterval,
            Integer startAgeOfEndYearInterval,
            int decimalPlacePrecisionForCalculations, int significantDigits,
            RoundingMode rm) {
        setAgeInterval(ageInterval);
        setStartAgeOfEndYearInterval(startAgeOfEndYearInterval);
        super.init(es, f, fmt, title, dataWidth, dataHeight, xAxisLabel,
                yAxisLabel, drawAxesOnPlot,
                decimalPlacePrecisionForCalculations, significantDigits, rm);
    }

    /**
     *
     * @param data -
     */
    @Override
    public void initialiseParameters(Object[] data) {
        minY = (BigDecimal) data[1];
        maxY = (BigDecimal) data[2];
        minX = (BigDecimal) data[3];
        maxX = (BigDecimal) data[4];
        labels = (ArrayList<String>) data[5];
        xAxisLabels = (TreeMap<BigDecimal, String>) data[6];
        setCellHeight();
        setCellWidth();
        setOriginRow();
        setOriginCol();
    }

    @Override
    public void setOriginCol() {
        originCol = dataStartCol;
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
        RoundingMode rm;
        rm = RoundingMode.HALF_UP;
        MathContext mc;
        mc = new MathContext(decimalPlacePrecisionForCalculations, rm);
        BigDecimal y;
        if (yPin != null) {
            BigDecimal maxYPin;
            BigDecimal minYPin;
            maxYPin = Generic_Collections.getMax(yPin);
            minYPin = Generic_Collections.getMin(yPin);
            minY = minYPin.min(minY);
            maxY = maxYPin.max(maxY);
            setCellHeight();
        }
//            if (minY.compareTo(BigDecimal.ZERO) > 1) {
//                rowValue = minY;
//            }
//            // Initialise rowValue the lowest value
//            int minYPin_CompareTo_minY;
//            minYPin_CompareTo_minY = minYPin.compareTo(minY);
//            if (minYPin_CompareTo_minY != 0) {
//                if (minYPin_CompareTo_minY == 1) {
//                    int minYPin_CompareTo_MaxY;
//                    minYPin_CompareTo_MaxY = minYPin.compareTo(maxY);
//                    if (minYPin_CompareTo_MaxY != 1) {
//                        rowValue = minYPin;
//                        while (rowValue.compareTo(minY) != 1) {
//                            rowValue = rowValue.subtract(yIncrement);
//                        }
//                    } else {
//                        throw new UnsupportedOperationException(this.getClass().getName() + ".drawYAxis(int, int, int, int, int)");
//                        //rowValue = minY;
//                    }
//                } else {
//                    setMinY(yPin);
//                    setCellHeight();
//                    rowValue = yPin;
////                    while (rowValue.compareTo(minY) == -1) {
////                        rowValue = rowValue.add(yIncrement);
////                    }
//                }
//            } else {
//                rowValue = minY;
//            }
//        } else {
//            rowValue = minY;
//        }
        boolean hasPositives;
        hasPositives = maxY.compareTo(BigDecimal.ZERO) == 1;
        boolean hasNegatives;
        hasNegatives = minY.compareTo(BigDecimal.ZERO) == -1;
        /**
         * If yIncrement is not set, then set it. If it is set, then set
         * numberOfYAxisTicksGT0 and numberOfXAxisTicksGT0
         */
        if (yIncrement == null) {
            yIncrement = (maxY.subtract(minY)).divide(
                    new BigDecimal(numberOfYAxisTicks), mc);
            initNumberOfYAxisTicksGT0(hasPositives, mc);
            initNumberOfYAxisTicksLT0(hasNegatives, mc);
            yIncrement = (maxY.subtract(minY)).divide(
                    new BigDecimal(numberOfYAxisTicksGT0 + numberOfYAxisTicksLT0), mc);
        } else {
            initNumberOfYAxisTicksGT0(hasPositives, mc);
            initNumberOfYAxisTicksLT0(hasNegatives, mc);
        }
        int yAxisExtraWidthLeft = scaleTickLength + scaleTickAndTextSeparation
                + seperationDistanceOfAxisAndData;
        int col = dataStartCol - seperationDistanceOfAxisAndData;
        Line2D ab;
        // Draw Y axis scale to the left side
        setPaint(Color.GRAY);
        ab = new Line2D.Double(col, dataEndRow, col, dataStartRow);
        draw(ab);
        int textWidth;
        int maxTickTextWidth = 0;
        int tickTextEndCol;
        tickTextEndCol = col - scaleTickAndTextSeparation - scaleTickLength;
        int row;
        BitSet rows = new BitSet();
        // Add the yPins that fit
        Iterator<BigDecimal> ite;
        ite = yPin.iterator();
        while (ite.hasNext()) {
            y = ite.next();
            row = coordinateToScreenRow(y);
            // Add Y Axis Mark
            maxTickTextWidth = addYAxisMark(row, col, scaleTickLength, y,
                    maxTickTextWidth, textHeight, rows, tickTextEndCol, rm);
        }

        // Add a pin at zero
        if (hasNegatives && hasPositives) {
            y = BigDecimal.ZERO;
            row = coordinateToScreenRow(BigDecimal.ZERO);
            // Add Y Axis Mark
            maxTickTextWidth = addYAxisMark(row, col, scaleTickLength, y,
                    maxTickTextWidth, textHeight, rows, tickTextEndCol, rm);
        }

        // Add incremental scale elemnts greater than zero
        y = yIncrement;
        for (int i = 0; i < numberOfYAxisTicksGT0; i++) {
            row = coordinateToScreenRow(y);
            System.out.println(row);
            if (row >= dataStartRow) {
                // Add Y Axis Mark
                maxTickTextWidth = addYAxisMark(row, col, scaleTickLength, y,
                        maxTickTextWidth, textHeight, rows, tickTextEndCol, rm);
            }
            y = y.add(yIncrement);
        }

        // drawEndOfYAxisTick
        row = coordinateToScreenRow(maxY);
        // Add Y Axis Mark
        maxTickTextWidth = addYAxisMark(row, col, scaleTickLength, y,
                maxTickTextWidth, textHeight, rows, tickTextEndCol, rm);

        // Add incremental scale elements less than zero
        y = yIncrement.negate();
        for (int i = 0; i < numberOfYAxisTicksLT0; i++) {
            row = coordinateToScreenRow(y);
            if (row <= dataEndRow) {
                // Add Y Axis Mark
                maxTickTextWidth = addYAxisMark(row, col, scaleTickLength, y,
                        maxTickTextWidth, textHeight, rows, tickTextEndCol, rm);
            }
            y = y.add(yIncrement.negate());
        }

        // drawStartOfYAxisTick
        row = coordinateToScreenRow(minY);
        maxTickTextWidth = addYAxisMark(row, col, scaleTickLength, y,
                maxTickTextWidth, textHeight, rows, tickTextEndCol, rm);

        yAxisExtraWidthLeft += maxTickTextWidth;

        // Y axis label
        setPaint(Color.BLACK);
        textWidth = getTextWidth(yAxisLabel);
        double angle = 3.0d * Math.PI / 2.0d;
        col = 3 * textHeight / 2;
        writeText(yAxisLabel, angle, col, dataMiddleRow + (textWidth / 2));
        yAxisExtraWidthLeft += (textHeight * 2) + partTitleGap;
        int[] r;
        r = new int[1];
        r[0] = yAxisExtraWidthLeft;
        return r;
    }

    /**
     * 
     * @param row
     * @param col
     * @param scaleTickLength
     * @param y
     * @param maxTickTextWidth
     * @param textHeight
     * @param rows
     * @param tickTextEndCol
     * @param rm
     * @return 
     */
    protected int addYAxisMark(int row, int col, int scaleTickLength,
            BigDecimal y, int maxTickTextWidth, int textHeight, BitSet rows,
            int tickTextEndCol, RoundingMode rm) {
        int r = maxTickTextWidth;
        //System.out.println(textHeight);
        int tHDiv3 = textHeight / 3;
        int tHPlus = 3 * textHeight / 2 ;
        // Check that this can be added
        int bitsetRow = (row - dataStartRow + tHDiv3)/ tHPlus;
        System.out.println(bitsetRow);
        if (!rows.get(bitsetRow)) {
            Line2D.Double ab;
            String tickText;
            int textWidth;
            int tickTextStartCol;
            setPaint(Color.GRAY);
            ab = new Line2D.Double(col, row, col - scaleTickLength, row);
            draw(ab);
            rows.set(bitsetRow, true);
            tickText = "" + Generic_BigDecimal.roundIfNecessary(y, 2, rm);
            textWidth = getTextWidth(tickText);
            tickTextStartCol = tickTextEndCol - textWidth;
            drawString(tickText, tickTextStartCol, row + tHDiv3);
            r = Math.max(r, textWidth);
        }
        return r;
    }

    public void initNumberOfYAxisTicksGT0(boolean hasPositives, MathContext mc) {
        if (hasPositives) {
            numberOfYAxisTicksGT0 = ((maxY).divide(yIncrement, mc)).intValue() + 1;
        }
    }

    public void initNumberOfYAxisTicksLT0(boolean hasNegatives, MathContext mc) {
        if (hasNegatives) {
            numberOfYAxisTicksLT0 = ((minY).divide(yIncrement, mc)).negate().intValue() + 1;
        }
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
            int seperationDistanceOfAxisAndData
    ) {
        int[] result = new int[3];
//        Object[] data = getData();
        BigDecimal colValue;
        if (xPin != null) {
            // Initialise colValue
            int pinCompareToMinX;
            pinCompareToMinX = xPin.compareTo(minX);
            if (pinCompareToMinX != 0) {
                if (pinCompareToMinX == 1) {
                    int pinCompareToMaxX;
                    pinCompareToMaxX = xPin.compareTo(maxX);
                    if (pinCompareToMaxX != 1) {
                        colValue = xPin;
                        while (colValue.compareTo(minX) != 1) {
                            colValue = colValue.subtract(xIncrement);
                        }
                    } else {
                        throw new UnsupportedOperationException(this.getClass().getName() + ".drawXAxis(int, int, int, int, int)");
                    }
                } else {
                    colValue = xPin;
                    while (colValue.compareTo(minX) == -1) {
                        colValue = colValue.add(xIncrement);
                    }
                }
            } else {
                colValue = minX;
            }
        } else {
            colValue = minX;
        }
//        if (xIncrement != null) {
//            if (colValue != null) {
//                numberOfXAxisTicks = ((maxX.subtract(colValue)).divide(xIncrement, mc)).intValue();
//            } else {
//                numberOfXAxisTicks = ((maxX.subtract(minX)).divide(xIncrement, mc)).intValue();
//            }
//        } else {
//            if (colValue != null) {
//                xIncrement = (maxX.subtract(colValue)).divide(new BigDecimal(numberOfXAxisTicks), mc);
//            } else {
//                xIncrement = (maxX.subtract(minX)).divide(new BigDecimal(numberOfXAxisTicks), mc);
//            }
//        }

        int xAxisExtraWidthLeft = 0;
        int extraAxisLength;
        extraAxisLength = 0;
        int xAxisExtraWidthRight = extraAxisLength;
        int xAxisExtraHeightBottom = seperationDistanceOfAxisAndData
                + scaleTickLength + scaleTickAndTextSeparation;
        Line2D ab;
        setPaint(Color.GRAY);
        int row = dataEndRow + seperationDistanceOfAxisAndData;
        // draw XAxis Line
        ab = new Line2D.Double(dataStartCol, row, dataEndCol + extraAxisLength,
                row);
        draw(ab);
        // Add ticks and labels
        int textRow = row + scaleTickLength + scaleTickAndTextSeparation;
        String text_String;
        int textWidth;
        int maxWidth = 0;
        double angle = 3.0d * Math.PI / 2.0d;
        int col = dataStartCol;
        int previousCol = col;
        if (xAxisLabels != null) {
            boolean first = true;
            Iterator<BigDecimal> ite;
            ite = xAxisLabels.keySet().iterator();
            while (ite.hasNext()) {
                BigDecimal x;
                x = ite.next();
                Object label = xAxisLabels.get(x);
                col = coordinateToScreenCol(x);
                if (col >= dataStartCol) {
                    ab = new Line2D.Double(col, row, col, row
                            + scaleTickLength);
                    draw(ab);
                    if (first || (col - previousCol) > textHeight) {
                        text_String = label.toString();
                        textWidth = getTextWidth(text_String);
                        writeText(text_String, angle, col + (textHeight / 3),
                                textRow + textWidth);

                        maxWidth = Math.max(maxWidth, textWidth);
                        previousCol = col;
                        first = false;
                    }
                }
            }
        } else {
            boolean first = true;
            BigDecimal x = minX;
            int i = 0;
            while (x.compareTo(maxX) != 1) {
//        for (int i = 0; i < numberOfTicks; i ++) {
                //String label = labels.get(value);
                x = minX.add(BigDecimal.valueOf(i));
                col = coordinateToScreenCol(x.multiply(xIncrement));
                if (col >= dataStartCol) {
                    ab = new Line2D.Double(col, row, col,
                            row + scaleTickLength);
                    draw(ab);
                    if (first || (col - previousCol) > textHeight) {
                        text_String = x.toPlainString();
                        textWidth = getTextWidth(text_String);
                        writeText(text_String, angle, col + (textHeight / 3),
                                textRow + textWidth);

                        maxWidth = Math.max(maxWidth, textWidth);
                        previousCol = col;
                        first = false;
                    }
                }
                i++;
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

    /**
     * @return the colours
     */
    public Color[] getColours() {
        if (colours == null) {
            initColours();
        }
        return colours;
    }

    /**
     * @param colours the colours to set
     */
    public final void setColours(Color[] colours) {
        this.colours = colours;
    }

    public void initColours() {
        colours = new Color[11];
        colours[0] = Color.BLACK;
        colours[1] = Color.BLUE;
        colours[2] = Color.CYAN;
        colours[3] = Color.GREEN;
        colours[4] = Color.MAGENTA;
        colours[5] = Color.ORANGE;
        colours[6] = Color.PINK;
        colours[7] = Color.RED;
        colours[8] = Color.YELLOW;
        colours[9] = Color.DARK_GRAY;
        colours[10] = Color.LIGHT_GRAY;
    }

    /**
     * @return the labels
     */
    public ArrayList<String> getLabels() {
        return labels;
    }

    /**
     * @param labels the labels to set
     */
    public final void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    /**
     * @return the xAxisLabels
     */
    public TreeMap<BigDecimal, ?> getxAxisLabels() {
        return xAxisLabels;
    }

    /**
     * @param xAxisLabels the xAxisLabels to set
     */
    public final void setxAxisLabels(TreeMap<BigDecimal, ?> xAxisLabels) {
        this.xAxisLabels = xAxisLabels;
    }

}
