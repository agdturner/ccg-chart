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

import ch.obermuhlner.math.big.BigRational;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import uk.ac.leeds.ccg.chart.data.Chart_AgeGenderData;
import uk.ac.leeds.ccg.chart.data.Chart_Data;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.math.arithmetic.Math_BigRational;

/**
 * An abstract class for creating Age by Gender Population visualisations and
 * possibly rendering them in a lightweight component as suited to headless
 * rendering.
 */
public abstract class Chart_AgeGender extends Chart {

    public Chart_AgeGender(Generic_Environment e) {
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
     * @param oom The order of magnitude for the precision.
     * @param rm The RoundingMode.
     */
    protected final void init(ExecutorService es, Path f, String format,
            String title, int dataWidth, int dataHeight, String xAxisLabel,
            String yAxisLabel, boolean drawAxesOnPlot, int ageInterval,
            Integer startAgeOfEndYearInterval, int oomx, int oomy, RoundingMode rm) {
        setAgeInterval(ageInterval);
        setStartAgeOfEndYearInterval(startAgeOfEndYearInterval);
        super.init(es, f, format, title, dataWidth, dataHeight, xAxisLabel,
                yAxisLabel, drawAxesOnPlot, oomx, oomy, rm);
    }

    @Override
    public void initialiseParameters(Chart_Data data) {
        Chart_AgeGenderData d = (Chart_AgeGenderData) data;
        maxX = d.maxX;
        minX = maxX.negate();
        maxY = BigRational.valueOf(getStartAgeOfEndYearInterval() + getAgeInterval());
        minY = BigRational.ZERO;
        setCellHeight();
        setCellWidth();
        setOriginRow();
        setOriginCol();
    }

    @Override
    public void setOriginCol() {
//        originCol = dataStartCol;
        originCol = ((dataStartCol + dataEndCol) / 2);
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
     * @param interval -
     * @param textHeight -
     * @param seperationDistanceOfAxisAndData -
     * @param partTitleGap -
     * @param scaleTickLength -
     * @param startAgeOfEndYearInterval -
     * @param scaleTickAndTextSeparation -
     * @return an int[] result for setting display parameters where: result[0] =
     * yAxisExtraWidthLeft;
     */
    @Override
    public int[] drawYAxis(int interval, int textHeight,
            int startAgeOfEndYearInterval, int scaleTickLength,
            int scaleTickAndTextSeparation, int partTitleGap,
            int seperationDistanceOfAxisAndData) {
        int[] r = new int[1];
        int yAxisExtraWidthLeft = 0;
        Line2D ab;
        // Draw origin
        if (isDrawOriginLinesOnPlot()) {
            setPaint(Color.LIGHT_GRAY);
            ab = new Line2D.Double(originCol, dataStartRow, originCol, dataEndRow);
            draw(ab);
        }
//        // Draw Y axis scale to the left side
//        setPaint(Color.GRAY);
//        int col = getDataStartCol();
//        ab = new Line2D.Double(
//                col,
//                dataEndRow,
//                col,
//                dataStartRow);
//        draw(ab);
        setPaint(Color.GRAY);
        BigRational cellHeight = getCellHeight();
        int barHeight;
        if (cellHeight.compareTo(BigRational.ZERO) == 0) {
            barHeight = 1;
        } else {
            barHeight = BigRational.valueOf(interval).divide(getCellHeight()).integerPart().toBigDecimal().intValue();
        }
        int barHeightdiv2 = barHeight / 2;

        int increment = interval;
        while (((startAgeOfEndYearInterval * textHeight) + 4) / increment > dataHeight) {
            increment += interval;
        }
        String text;
        int maxTickTextWidth = 0;
        int col = dataStartCol;
        int miny_int = minY.integerPart().toBigDecimal().intValue();
        //for (int i = miny_int; i <= startAgeOfEndYearInterval; i += increment) {
        for (int i = miny_int; i <= maxY.integerPart().toBigDecimal().intValue(); i += increment) {

            // int row = getRow(BigDecimal.valueOf(i));
            int row = getRow(BigRational.valueOf(i)) - barHeightdiv2;
            //int row = getRow(BigDecimal.valueOf(i)) - barHeight;

            setPaint(Color.GRAY);
//            ab = new Line2D.Double(col, row, col - scaleTickLength, row);
//            draw(ab);
            //text = "" + i + " - " + (i + increment);
            text = "" + i;
            int textWidth = getTextWidth(text);
            drawString(text,
                    col - scaleTickAndTextSeparation - scaleTickLength - textWidth,
                    //row);
                    row + (textHeight / 3));
            maxTickTextWidth = Math.max(maxTickTextWidth, textWidth);
        }
        yAxisExtraWidthLeft += scaleTickLength + scaleTickAndTextSeparation + maxTickTextWidth;
        // Y axis label
        setPaint(Color.BLACK);
        int textWidth = getTextWidth(yAxisLabel);
        double angle = 3.0d * Math.PI / 2.0d;
        col = 3 * textHeight / 2;
        writeText(yAxisLabel, angle, col, dataMiddleRow + (textWidth / 2));
        yAxisExtraWidthLeft += (textHeight * 2) + partTitleGap;
        r[0] = yAxisExtraWidthLeft;
        return r;
    }

    /**
     * Draw the X axis.
     *
     * @param th Text height.
     * @param scaleTickLength -
     * @param seperationDistanceOfAxisAndData -
     * @param partTitleGap -
     * @param scaleTickAndTextSeparation -
     * @return an int[] result for setting display parameters where: result[0] =
     * xAxisExtraWidthLeft; result[1] = xAxisExtraWidthRight; result[2] =
     * xAxisExtraHeightBottom.
     */
    @Override
    public int[] drawXAxis(int th, int scaleTickLength,
            int scaleTickAndTextSeparation, int partTitleGap,
            int seperationDistanceOfAxisAndData) {
        int[] result = new int[3];
        int xAxisExtraWidthLeft = 0;
        int xAxisExtraWidthRight = 0;
        int xAxisExtraHeightBottom = seperationDistanceOfAxisAndData
                + scaleTickLength + scaleTickAndTextSeparation + th;
        //int originRow = getOriginRow();
        Line2D ab;
        setPaint(Color.GRAY);
        int row = dataEndRow + seperationDistanceOfAxisAndData;
        // draw XAxis Line
        ab = new Line2D.Double(dataStartCol, row, dataEndCol, row);
        draw(ab);
        // Add ticks and labels
        // origin tick and label
        int textRow = row + scaleTickLength + scaleTickAndTextSeparation + th;
        String s = "0";
        int textWidth = getTextWidth(s);
        ab = new Line2D.Double(originCol, row, originCol, row + scaleTickLength);
        draw(ab);
        drawString(s, originCol - (textWidth / 2), textRow);
        // Left end scale tick and label
        if (maxX != null) {
            s = Math_BigRational.round(maxX, oomx, rm).toPlainString();
            textWidth = getTextWidth(s);
        }
        ab = new Line2D.Double(dataStartCol, row, dataStartCol, row + scaleTickLength);
        draw(ab);
        drawString(s, dataStartCol - (textWidth / 2), textRow);
        // Add to imageWidth as this label sticks out
        xAxisExtraWidthLeft += (textWidth / 2) + th;
//        // Check to see if plot needs to grow
//        if (xAxisExtraWidthLeft > xAxisExtraWidthLeft) {
//            int diff = xAxisExtraWidthLeft - dataStartCol;
//            imageWidth += diff;
//            dataStartCol += diff;
//            dataEndCol += diff;
//            xAxisExtraWidthLeft = xAxisExtraWidthLeft;
//            setOriginCol();
//        }
        // Right end scale tick and label
        //text_String = maxX.toBigInteger().toString();
        s = Math_BigRational.round(maxX, oomx, rm).toPlainString();
        textWidth = getTextWidth(s);
        ab = new Line2D.Double(dataEndCol, row, dataEndCol, row + scaleTickLength);
        draw(ab);
        drawString(s, dataEndCol - (textWidth / 2), textRow);
        // Add to imageWidth as this label sticks out
        xAxisExtraWidthRight += (textWidth / 2) + th;
//        if (xAxisExtraWidthRight > xAxisExtraWidthRight) {
//            imageWidth += axesExtraWidthRight - xAxisExtraWidthRight;
//            xAxisExtraWidthRight = axesExtraWidthRight;
//        }
        // Add axis labels
        setPaint(Color.DARK_GRAY);
        textRow += th + partTitleGap;
        s = "Male";
        textWidth = getTextWidth(s);
        xAxisExtraHeightBottom += th + partTitleGap;
        drawString(s,
                ((dataStartCol + originCol) / 2) - (textWidth / 2),
                textRow);
        setPaint(Color.DARK_GRAY);
        s = "Female";
        textWidth = getTextWidth(s);
        drawString(s,
                ((dataEndCol + originCol) / 2) - (textWidth / 2), textRow);
        textRow += th + partTitleGap;
        xAxisExtraHeightBottom += th + partTitleGap;
        setPaint(Color.BLACK);
        s = xAxisLabel;
//        text_String = "Population";
        textWidth = getTextWidth(s);
        drawString(s, originCol - (textWidth / 2), textRow);
        xAxisExtraHeightBottom += th;
        result[0] = xAxisExtraWidthLeft;
        result[1] = xAxisExtraWidthRight;
        result[2] = xAxisExtraHeightBottom;
        return result;
    }
}
