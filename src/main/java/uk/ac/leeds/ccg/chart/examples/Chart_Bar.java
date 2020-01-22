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
import java.awt.Dimension;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import uk.ac.leeds.ccg.chart.core.Chart_AbstractBar;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.math.Math_BigDecimal;
import uk.ac.leeds.ccg.generic.execution.Generic_Execution;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.generic.util.Generic_Collections;

/**
 * An example of generating a Bar Chart Visualization.
 */
public class Chart_Bar extends Chart_AbstractBar {

    public Chart_Bar(Generic_Environment e) {
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
     * @param barGap The number of pixels between one bar and the next.
     * @param xIncrement The increment on the x axis.
     * @param yMax The maximum y on the y axis.
     * @param yPin A value that must be on the y axis.
     * @param yIncrement The increment between values on the y axis.
     * @param numberOfYAxisTicks The number of y axis ticks.
     * @param dpc The decimal place precision for calculations.
     * @param dpd The decimal place precision for display.
     * @param rm The RoundingMode.
     */
    public Chart_Bar(Generic_Environment e, ExecutorService es, Path f,
            String format, String title, int dataWidth, int dataHeight,
            String xAxisLabel, String yAxisLabel,
            boolean drawOriginLinesOnPlot, //Ignored
            int barGap, int xIncrement, BigDecimal yMax, BigDecimal yPin,
            BigDecimal yIncrement, int numberOfYAxisTicks,
            int dpc,
            int dpd,
            RoundingMode rm) {
        super(e);
        this.barGap = barGap;
        this.xAxisIncrement = xIncrement;
        this.numberOfYAxisTicks = numberOfYAxisTicks;
        this.yPin = yPin;
        this.yAxisIncrement = yIncrement;
        init(es, f, format, title, dataWidth, dataHeight, xAxisLabel,
                yAxisLabel, drawOriginLinesOnPlot,
                dpc,
                dpd,
                rm);
    }

    @Override
    public void drawData() {
        setPaint(Color.DARK_GRAY);
        Object[] intervalCountsAndLables;
        intervalCountsAndLables = (Object[]) data[0];
        TreeMap<Integer, Integer> counts;
        counts = (TreeMap<Integer, Integer>) intervalCountsAndLables[0];
        TreeMap<Integer, BigDecimal> centres;
        centres = (TreeMap<Integer, BigDecimal>) intervalCountsAndLables[2];

        BigDecimal[] minMaxBigDecimal;
        minMaxBigDecimal = (BigDecimal[]) data[1];
        BigDecimal minValue;
        minValue = minMaxBigDecimal[0];

        BigDecimal intervalWidth;
        intervalWidth = (BigDecimal) data[2];

        Iterator<Map.Entry<Integer, Integer>> ite;
        Map.Entry<Integer, Integer> entry;
        Integer interval;
        Integer count;
        // Draw bars
        ite = counts.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            interval = entry.getKey();
            count = entry.getValue();
            BigDecimal centre = centres.get(interval);
            int row = coordinateToScreenRow(
                    //new BigDecimal(count).multiply(cellHeight));
                    new BigDecimal(count));
            int barHeight = dataEndRow - row;
            if (barHeight == 0) {
                barHeight = 1;
                row -= 1;
            }
            int col = coordinateToScreenCol(
                    //minValue.add(new BigDecimal(interval).multiply(intervalWidth)))
                    //minValue.add(centre))
                    centre)
                    + barGap;
            setPaint(Color.DARK_GRAY);
            fillRect(col, row, barWidth, barHeight);
        }
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
                title = "Example Bar Chart";
                System.out.println("Use default title: " + title);
                Path outdir = e.files.getOutputDir();
                file = Paths.get(outdir.toString(), title.replace(" ", "_") + "." + format);
                System.out.println("Use default Path: " + file.toString());
            } else {
                title = args[0];
                file = Paths.get(args[1]);
            }
            int dataWidth = 500;
            int dataHeight = 250;
            String xAxisLabel = "Population";
            String yAxisLabel = "Count of Areas";
            boolean drawOriginLinesOnPlot = true;
            int barGap = 1;
            int xIncrement = 1;
            int numberOfYAxisTicks = 11;
            BigDecimal yMax;
            yMax = null;
            BigDecimal yPin = BigDecimal.ZERO;
            BigDecimal yIncrement = BigDecimal.ONE;
            //int yAxisStartOfEndInterval = 60;
            int decimalPlacePrecisionForCalculations = 10;
            int decimalPlacePrecisionForDisplay = 3;
            RoundingMode roundingMode = RoundingMode.HALF_UP;
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Chart_Bar chart = new Chart_Bar(e,
                    executorService,
                    file,
                    format,
                    title,
                    dataWidth,
                    dataHeight,
                    xAxisLabel,
                    yAxisLabel,
                    drawOriginLinesOnPlot,
                    barGap,
                    xIncrement,
                    yMax,
                    yPin,
                    yIncrement,
                    numberOfYAxisTicks,
                    decimalPlacePrecisionForCalculations,
                    decimalPlacePrecisionForDisplay,
                    roundingMode);
            chart.setData(chart.getDefaultData());
            chart.vis.getHeadlessEnvironment();
            chart.run();
            Future future = chart.future;
            Generic_Execution exec = new Generic_Execution(e);
            exec.shutdownExecutorService(executorService, future, chart);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public Dimension draw() {
        drawOutline();
        drawTitle(title);
        drawAxes(2);
//        drawAxes(getAgeInterval(), getStartAgeOfEndYearInterval());
        drawData();
        Dimension newDim = new Dimension(imageWidth, imageHeight);
        return newDim;
    }

    public void drawAxes(int interval) {
        int yAxisExtraWidthLeft;
//        int yAxisExtraHeightTop = 0;
//        int yAxisExtraHeightBottom = 0;
        int xAxisExtraWidthLeft;
        int xAxisExtraWidthRight;
        int xAxisExtraHeightBottom;
        int scaleTickLength = getDefaultScaleTickLength();
        int scaleTickAndTextSeparation = getDefaultScaleTickAndTextSeparation();
        int partTitleGap = getDefaultPartTitleGap();
        int textHeight = getTextHeight();
        //int seperationDistanceOfAxisAndData = textHeight;
        int seperationDistanceOfAxisAndData = 2;
        // Draw Y axis
        int[] yAxisDimensions = drawYAxis(
                textHeight,
                scaleTickLength,
                scaleTickAndTextSeparation,
                partTitleGap,
                seperationDistanceOfAxisAndData);
        yAxisExtraWidthLeft = yAxisDimensions[0];
        if (yAxisExtraWidthLeft > extraWidthLeft) {
            int diff = yAxisExtraWidthLeft - extraWidthLeft;
            imageWidth += diff;
            dataStartCol += diff;
            dataEndCol += diff;
            extraWidthLeft = yAxisExtraWidthLeft;
            yAxisWidth += diff;
            //setExtraWidthLeft(yAxisExtraWidthLeft);
            setYAxisWidth(yAxisWidth);
        }
        //setYAxisWidth(yAxisExtraWidthLeft);
        // Draw X axis
        int[] xAxisDimensions;
        xAxisDimensions = drawXAxis(textHeight, scaleTickLength,
                scaleTickAndTextSeparation, partTitleGap,
                seperationDistanceOfAxisAndData);
        xAxisExtraWidthLeft = xAxisDimensions[0];
        xAxisExtraWidthRight = xAxisDimensions[1];
        xAxisExtraHeightBottom = xAxisDimensions[2];
        if (xAxisExtraWidthLeft > extraWidthLeft) {
            int diff = xAxisExtraWidthLeft - extraWidthLeft;
            imageWidth += diff;
            dataStartCol += diff;
            dataEndCol += diff;
            extraWidthLeft = xAxisExtraWidthLeft;
            yAxisWidth += diff;
            setYAxisWidth(yAxisWidth);
//            setOriginCol();
        }
        if (xAxisExtraWidthRight > extraWidthRight) {
            imageWidth += xAxisExtraWidthRight - extraWidthRight;
            extraWidthRight = xAxisExtraWidthRight;
        }
        xAxisHeight += xAxisExtraHeightBottom;
        if (xAxisExtraHeightBottom > extraHeightBottom) {
            int diff = xAxisExtraHeightBottom - extraHeightBottom;
            imageHeight += diff;
        }
    }

    @Override
    public Object[] getDefaultData() {
        Object[] result;
        result = new Object[3];
        BigDecimal intervalWidth;
        intervalWidth = new BigDecimal(xAxisIncrement);
        TreeMap<String, BigDecimal> map;
        map = new TreeMap<>();
        map.put("A", new BigDecimal(0.0d));
        map.put("B", new BigDecimal(1.0d));
        map.put("C", new BigDecimal(2.0d));
        map.put("D", new BigDecimal(3.0d));
        map.put("E", new BigDecimal(4.0d));
        map.put("F", new BigDecimal(5.0d));
        map.put("G", new BigDecimal(6.0d));
        map.put("H", new BigDecimal(7.0d));
        map.put("I", new BigDecimal(8.0d));
        map.put("J", new BigDecimal(9.0d));
        map.put("K", new BigDecimal(10.0d));
        map.put("L", new BigDecimal(11.0d));
        map.put("M", new BigDecimal(12.0d));
        map.put("N", new BigDecimal(13.0d));
        map.put("O", new BigDecimal(14.0d));
        map.put("P", new BigDecimal(15.0d));
        map.put("Q", new BigDecimal(16.0d));
        map.put("R", new BigDecimal(2.0d));
        map.put("S", new BigDecimal(4.0d));
        map.put("T", new BigDecimal(6.0d));
        map.put("U", new BigDecimal(8.0d));
        map.put("V", new BigDecimal(10.0d));
        map.put("W", new BigDecimal(12.0d));
        map.put("X", new BigDecimal(14.0d));
        map.put("Y", new BigDecimal(16.0d));
        map.put("Z", new BigDecimal(4.0d));
        BigDecimal[] minMaxBigDecimal;
        minMaxBigDecimal = Generic_Collections.getMinMaxBigDecimal(map);
        Object[] intervalCountsLabelsMins;
        BigDecimal min = minMaxBigDecimal[0];
        //BigDecimal max = minMaxBigDEcimal[1];

        MathContext mc;
        mc = new MathContext(decimalPlacePrecisionForCalculations, getRoundingMode());

        intervalCountsLabelsMins = Generic_Collections.getIntervalCountsLabelsMins(
                min, intervalWidth, map, mc);
        result[0] = intervalCountsLabelsMins;
        result[1] = minMaxBigDecimal;
        result[2] = intervalWidth;
        return result;
    }

    @Override
    public void drawTitle(String title) {
        super.drawTitle(title);
        int barHeight = Math_BigDecimal.divideRoundIfNecessary(
                BigDecimal.valueOf(getAgeInterval()),
                getCellHeight(),
                0,
                getRoundingMode()).intValue();
        extraHeightTop += barHeight;
    }

    @Override
    public int[] drawYAxis(int interval, int textHeight, int startOfEndInterval, int scaleTickLength, int scaleTickAndTextSeparation, int partTitleGap, int seperationDistanceOfAxisAndData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
