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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import uk.ac.leeds.ccg.chart.core.Chart_Bar;
import uk.ac.leeds.ccg.chart.data.Chart_BarData;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.generic.execution.Generic_Execution;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.math.util.Math_Collections;
import uk.ac.leeds.ccg.math.util.Math_Collections.CountsLabelsMins;

/**
 * An example of generating a Bar Chart Visualization.
 */
public class Chart_BarExample extends Chart_Bar {

    public Chart_BarExample(Generic_Environment e) {
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
     * @param oom The Order of Magnitude for rounding precision.
     * @param rm The RoundingMode.
     */
    public Chart_BarExample(Generic_Environment e, ExecutorService es, Path f,
            String format, String title, int dataWidth, int dataHeight,
            String xAxisLabel, String yAxisLabel,
            boolean drawOriginLinesOnPlot, //Ignored
            int barGap, int xIncrement, BigDecimal yMax, BigDecimal yPin,
            BigDecimal yIncrement, int numberOfYAxisTicks, int oomx, int oomy, 
            RoundingMode rm) {
        super(e);
        this.barGap = barGap;
        this.xAxisIncrement = xIncrement;
        this.numberOfYAxisTicks = numberOfYAxisTicks;
        this.yPin = yPin;
        this.yAxisIncrement = BigRational.valueOf(yIncrement);
        init(es, f, format, title, dataWidth, dataHeight, xAxisLabel,
                yAxisLabel, drawOriginLinesOnPlot, oomx, oomy, rm);
    }

    @Override
    public void drawData() {
        setPaint(Color.DARK_GRAY);
        Chart_BarData d = getData();
        Iterator<Map.Entry<Integer, Integer>> ite;
        Map.Entry<Integer, Integer> entry;
        Integer interval;
        Integer count;
        // Draw bars
        ite = d.counts.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            interval = entry.getKey();
            count = entry.getValue();
            BigRational centre = d.centres.get(interval);
            int row = getRow(BigRational.valueOf(count));
            int barHeight = dataEndRow - row;
            if (barHeight == 0) {
                barHeight = 1;
                row -= 1;
            }
            int col = getCol(centre) + barGap;
            setPaint(Color.DARK_GRAY);
            fillRect(col, row, barWidth, barHeight);
        }
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
            BigDecimal yMax = null;
            BigDecimal yPin = BigDecimal.ZERO;
            BigDecimal yIncrement = BigDecimal.ONE;
            //int yAxisStartOfEndInterval = 60;
            int oomx = -2;
            int oomy = -1;
            RoundingMode rm = RoundingMode.HALF_UP;
            ExecutorService es = Executors.newSingleThreadExecutor();
            Chart_BarExample chart = new Chart_BarExample(e, es, file, format, title,
                    dataWidth, dataHeight, xAxisLabel, yAxisLabel,
                    drawOriginLinesOnPlot, barGap, xIncrement, yMax, yPin,
                    yIncrement, numberOfYAxisTicks, oomx, oomy, rm);
            chart.setData(chart.getDefaultData());
            chart.vis.getHeadlessEnvironment();
            chart.run();
            Future future = chart.future;
            Generic_Execution exec = new Generic_Execution(e);
            exec.shutdownExecutorService(es, future, chart);
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

    /**
     * @return default data for this type of chart. 
     */
    public Chart_BarData getDefaultData() {
        Chart_BarData r = new Chart_BarData();
        r.intervalWidth = BigRational.valueOf(xAxisIncrement);
        TreeMap<String, BigRational> map = new TreeMap<>();
        map.put("A", BigRational.valueOf(0));
        map.put("B", BigRational.valueOf(1.0d));
        map.put("C", BigRational.valueOf(2.0d));
        map.put("D", BigRational.valueOf(3.0d));
        map.put("E", BigRational.valueOf(4.0d));
        map.put("F", BigRational.valueOf(5.0d));
        map.put("G", BigRational.valueOf(6.0d));
        map.put("H", BigRational.valueOf(7.0d));
        map.put("I", BigRational.valueOf(8.0d));
        map.put("J", BigRational.valueOf(9.0d));
        map.put("K", BigRational.valueOf(10.0d));
        map.put("L", BigRational.valueOf(11.0d));
        map.put("M", BigRational.valueOf(12.0d));
        map.put("N", BigRational.valueOf(13.0d));
        map.put("O", BigRational.valueOf(14.0d));
        map.put("P", BigRational.valueOf(15.0d));
        map.put("Q", BigRational.valueOf(16.0d));
        map.put("R", BigRational.valueOf(2.0d));
        map.put("S", BigRational.valueOf(4.0d));
        map.put("T", BigRational.valueOf(6.0d));
        map.put("U", BigRational.valueOf(8.0d));
        map.put("V", BigRational.valueOf(10.0d));
        map.put("W", BigRational.valueOf(12.0d));
        map.put("X", BigRational.valueOf(14.0d));
        map.put("Y", BigRational.valueOf(16.0d));
        map.put("Z", BigRational.valueOf(4.0d));
        ArrayList<BigRational> minMax = Math_Collections.getMinMax(map);
        r.min = minMax.get(0);
        r.max = minMax.get(1);
        CountsLabelsMins intervalCountsLabelsMins = Math_Collections.getIntervalCountsLabelsMins(
                r.min, r.intervalWidth, map);
        r.counts = intervalCountsLabelsMins.counts;
        return r;
    }

    @Override
    public void drawTitle(String title) {
        super.drawTitle(title);
        int barHeight = BigRational.valueOf(getAgeInterval()).divide(getCellHeight()).integerPart().toBigDecimal().intValue();
        extraHeightTop += barHeight;
    }

    @Override
    public int[] drawYAxis(int interval, int textHeight, int startOfEndInterval, int scaleTickLength, int scaleTickAndTextSeparation, int partTitleGap, int seperationDistanceOfAxisAndData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
