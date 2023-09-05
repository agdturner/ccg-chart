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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import uk.ac.leeds.ccg.chart.core.Chart_Line;
import uk.ac.leeds.ccg.chart.data.Chart_LineData;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.generic.execution.Generic_Execution;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.math.util.Math_Collections;

/**
 * An example of generating a Line Graph visualization.
 */
public class Chart_LineExample extends Chart_Line {

    /**
     * Iff set to true then a line is added to the graph at Y = 0.
     */
    boolean drawYZero;

    public Chart_LineExample(Generic_Environment e) {
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
     * @param yMax The maximum y on the y axis.
     * @param yPin A value that must be on the y axis.
     * @param yIncrement The increment between values on the y axis.
     * @param numberOfYAxisTicks The number of y axis ticks.
     * @param drawYZero Whether or not y = zero is drawn on the map.
     * @param dcp decimalPlacePrecisionForCalculations
     * @param dcd decimalPlacePrecisionForDisplay
     * @param rm The RoundingMode.
     */
    public Chart_LineExample(Generic_Environment e, ExecutorService es, Path f,
            String format, String title, int dataWidth, int dataHeight,
            String xAxisLabel, String yAxisLabel, BigDecimal yMax,
            ArrayList<BigRational> yPin, BigDecimal yIncrement,
            int numberOfYAxisTicks, boolean drawYZero, int dcp, int dcd,
            RoundingMode rm) {
        super(e);
        if (yMax != null) {
            this.yMax = BigRational.valueOf(yMax);
        }
        this.yPin = yPin;
        if (yIncrement != null) {
            this.yIncrement = BigRational.valueOf(yIncrement);
        }
        this.numberOfYAxisTicks = numberOfYAxisTicks;
        this.drawYZero = drawYZero;
        init(es, f, format, title, dataWidth, dataHeight, xAxisLabel,
                yAxisLabel, false, dcp,
                dcd, rm);
    }

    @Override
    public void drawData() {
        TreeMap<String, TreeMap<BigDecimal, BigDecimal>> maps = getData().maps;
        TreeMap<String, Boolean> nonZero = getData().nonZero;
        Color[] colours;
        colours = getColours();
        int i = 1;
        Iterator<String> ite;
        ite = maps.keySet().iterator();
        while (ite.hasNext()) {
            String type;
            type = ite.next();
            if (nonZero == null) {
                TreeMap<BigDecimal, BigDecimal> map;
                map = maps.get(type);
                int j = i;
                while (j >= colours.length) {
                    j -= colours.length;
                }
                drawMap(map, colours[j]);
                i++;
            } else {
                if (nonZero.get(type)) {
                    TreeMap<BigDecimal, BigDecimal> map;
                    map = maps.get(type);
                    int j = i;
                    while (j >= colours.length) {
                        j -= colours.length;
                    }
                    drawMap(map, colours[j]);
                    i++;
                }
            }
        }
    }

    /**
     * @param map The map to draw.
     * @param c The colour.
     */
    public void drawMap(TreeMap<BigDecimal, BigDecimal> map, Color c) {
        int length;
        length = 3;
        int row0 = 0;
        int col0 = 0;
        setPaint(c);
        boolean first = true;
        Iterator<BigDecimal> ite = map.keySet().iterator();
        while (ite.hasNext()) {
            BigDecimal x = ite.next();
            BigDecimal y = map.get(x);
            int row = coordinateToScreenRow(BigRational.valueOf(y));
            int col = coordinateToScreenCol(BigRational.valueOf(x));
            if (first) {
                row0 = row;
                col0 = col;
                first = false;
            } else {
                //setPaint(c);
//                drawPlus(col0, row0, length);
                drawCross(col0, row0, length);
//                drawPlus(col, row, length);
                drawCross(col, row, length);
                Line2D line = new Line2D.Double(col0, row0, col, row);
                draw(line);
                row0 = row;
                col0 = col;
            }
        }
    }

    public void drawPlus(int col, int row, int length) {
        Line2D line;
        line = new Line2D.Double(col, row - length, col, row + length);
        draw(line);
        line = new Line2D.Double(col + length, row, col - length, row);
        draw(line);
    }

    /**
     * @param col The column.
     * @param row The row.
     * @param l length of each cross part
     */
    public void drawCross(int col, int row, int l) {
        draw(new Line2D.Double(col - l, row - l, col + l, row + l));
        draw(new Line2D.Double(col - l, row + l, col + l, row - l));
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
                System.out.println("Expected 2 args: args[0] title; args[1] Path."
                        + " Recieved " + args.length + " args.");
                // Use defaults
                title = "Example Line Graph";
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
            String xAxisLabel = "X";
            String yAxisLabel = "Y";
            boolean drawYZero;
//        drawYZero = true;
            drawYZero = false;
            int numberOfYAxisTicks = 11;
            BigDecimal yMax;
            yMax = null;
            ArrayList<BigRational> yPin;
            yPin = new ArrayList<>();
            yPin.add(BigRational.ZERO);
            //BigDecimal yIncrement = BigDecimal.ONE;
            BigDecimal yIncrement = null;
            //int yAxisStartOfEndInterval = 60;
            int decimalPlacePrecisionForCalculations = 10;
            int decimalPlacePrecisionForDisplay = 3;
            RoundingMode roundingMode = RoundingMode.HALF_UP;
            ExecutorService es = Executors.newSingleThreadExecutor();
            Chart_LineExample chart = new Chart_LineExample(e, es, file, format, title,
                    dataWidth, dataHeight, xAxisLabel, yAxisLabel, yMax, yPin,
                    yIncrement, numberOfYAxisTicks, drawYZero,
                    decimalPlacePrecisionForCalculations,
                    decimalPlacePrecisionForDisplay,
                    roundingMode);
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
        drawAxes();
        if (drawYZero) {
            Line2D ab;
            // Draw Y axis scale to the left side
            setPaint(Color.LIGHT_GRAY);
            int zero = coordinateToScreenRow(BigRational.ZERO);
            ab = new Line2D.Double(dataStartCol, zero, dataEndCol, zero);
            draw(ab);
        }
        drawData();
        drawLegend();
        Dimension newDim = new Dimension(imageWidth, imageHeight);
        return newDim;
    }

    public void drawAxes() {
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
        int[] yAxisDimensions = drawYAxis(textHeight, scaleTickLength,
                scaleTickAndTextSeparation, partTitleGap,
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
        int[] xAxisDimensions = drawXAxis(textHeight, scaleTickLength,
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
//            extraWidthLeft = xAxisExtraWidthLeft;
            yAxisWidth += diff;
            setYAxisWidth(yAxisWidth);
//            setOriginCol();
        }
        if (xAxisExtraWidthRight > extraWidthRight) {
            imageWidth += xAxisExtraWidthRight - extraWidthRight;
            extraWidthRight = xAxisExtraWidthRight;
        }
        xAxisHeight = xAxisExtraHeightBottom;
        if (xAxisExtraHeightBottom > extraHeightBottom) {
            int diff = xAxisExtraHeightBottom - extraHeightBottom;
            imageHeight += diff;
            extraHeightBottom = xAxisExtraHeightBottom;
        }
    }

    /**
     * @return default data for this type of chart. 
     */
    public Chart_LineData getDefaultData() {
        Chart_LineData r = new Chart_LineData();
        TreeMap<BigDecimal, BigDecimal> map = new TreeMap<>();
        //map.put(new BigDecimal(0.0d), new BigDecimal(10.0d));
        map.put(new BigDecimal(0.0d), new BigDecimal(-10.0d));
        map.put(new BigDecimal(6.0d), new BigDecimal(11.0d));
        map.put(new BigDecimal(12.0d), new BigDecimal(12.0d));
        map.put(new BigDecimal(18.0d), new BigDecimal(13.0d));
        map.put(new BigDecimal(24.0d), new BigDecimal(14.0d));
        map.put(new BigDecimal(27.0d), new BigDecimal(15.0d));
        map.put(new BigDecimal(30.0d), new BigDecimal(16.0d));
        map.put(new BigDecimal(33.0d), new BigDecimal(15.0d));
        map.put(new BigDecimal(36.0d), new BigDecimal(14.0d));
        map.put(new BigDecimal(39.0d), new BigDecimal(15.0d));
        map.put(new BigDecimal(42.0d), new BigDecimal(17.0d));
        map.put(new BigDecimal(45.0d), new BigDecimal(18.0d));
        map.put(new BigDecimal(48.0d), new BigDecimal(29.0d));
        map.put(new BigDecimal(49.0d), new BigDecimal(30.0d));
        map.put(new BigDecimal(50.0d), new BigDecimal(15.0d));
        map.put(new BigDecimal(51.0d), new BigDecimal(25.0d));
        map.put(new BigDecimal(52.0d), new BigDecimal(35.0d));
        map.put(new BigDecimal(53.0d), new BigDecimal(36.0d));
        map.put(new BigDecimal(54.0d), new BigDecimal(37.0d));
        r.maps.put("map1", map);
        TreeMap<BigDecimal, BigDecimal> map2 = new TreeMap<>();
        map2.put(new BigDecimal(0.0d), new BigDecimal(9.0d));
        map2.put(new BigDecimal(6.0d), new BigDecimal(10.0d));
        map2.put(new BigDecimal(12.0d), new BigDecimal(12.0d));
        map2.put(new BigDecimal(18.0d), new BigDecimal(14.0d));
        map2.put(new BigDecimal(24.0d), new BigDecimal(15.0d));
        map2.put(new BigDecimal(27.0d), new BigDecimal(16.0d));
        map2.put(new BigDecimal(30.0d), new BigDecimal(17.0d));
        map2.put(new BigDecimal(33.0d), new BigDecimal(18.0d));
        map2.put(new BigDecimal(36.0d), new BigDecimal(13.0d));
        map2.put(new BigDecimal(39.0d), new BigDecimal(16.0d));
        map2.put(new BigDecimal(42.0d), new BigDecimal(18.0d));
        map2.put(new BigDecimal(45.0d), new BigDecimal(19.0d));
        map2.put(new BigDecimal(48.0d), new BigDecimal(25.0d));
        map2.put(new BigDecimal(49.0d), new BigDecimal(31.0d));
        map2.put(new BigDecimal(50.0d), new BigDecimal(25.0d));
        map2.put(new BigDecimal(51.0d), new BigDecimal(25.0d));
        map2.put(new BigDecimal(52.0d), new BigDecimal(25.0d));
        map2.put(new BigDecimal(53.0d), new BigDecimal(37.0d));
        map2.put(new BigDecimal(54.0d), new BigDecimal(37.0d));
        r.maps.put("map2", map2);
        ArrayList<BigDecimal> minMax = Math_Collections.getMinMax(map);
        minY = BigRational.valueOf(minMax.get(0));
        maxY = BigRational.valueOf(minMax.get(1));
        minX = BigRational.valueOf(map.firstKey());
        maxX = BigRational.valueOf(map.lastKey());
        minMax = Math_Collections.getMinMax(map2);
        if (minY.compareTo(BigRational.valueOf(minMax.get(0))) == 1) {
            minY = BigRational.valueOf(minMax.get(0));
        }
        if (maxY.compareTo(BigRational.valueOf(minMax.get(1))) == -1) {
            maxY = BigRational.valueOf(minMax.get(1));
        }
        if (minX.compareTo(BigRational.valueOf(map2.firstKey())) == 1) {
            minX = BigRational.valueOf(map2.firstKey());
        }
        if (maxX.compareTo(BigRational.valueOf(map2.lastKey())) == -1) {
            maxX = BigRational.valueOf(map2.lastKey());
        }
        r.minY = minY;
        r.maxY = maxY;
        r.minX = minX;
        r.maxX = maxX;
        r.xAxisLabels.put(BigRational.valueOf(0), "2008 April");
        r.xAxisLabels.put(BigRational.valueOf(6), "2008 October");
        r.xAxisLabels.put(BigRational.valueOf(12), "2009 April");
        r.xAxisLabels.put(BigRational.valueOf(18), "2009 October");
        r.xAxisLabels.put(BigRational.valueOf(24), "2010 April");
        r.xAxisLabels.put(BigRational.valueOf(27), "2010 July");
        r.xAxisLabels.put(BigRational.valueOf(30), "2010 October");
        r.xAxisLabels.put(BigRational.valueOf(33), "2010 January");
        r.xAxisLabels.put(BigRational.valueOf(36), "2011 April");
        r.xAxisLabels.put(BigRational.valueOf(39), "2011 July");
        r.xAxisLabels.put(BigRational.valueOf(42), "2011 October");
        r.xAxisLabels.put(BigRational.valueOf(45), "2012 January");
        r.xAxisLabels.put(BigRational.valueOf(48), "2012 April");
        r.xAxisLabels.put(BigRational.valueOf(49), "2012 May");
        r.xAxisLabels.put(BigRational.valueOf(50), "2012 June");
        r.xAxisLabels.put(BigRational.valueOf(51), "2012 April");
        r.xAxisLabels.put(BigRational.valueOf(52), "2012 August");
        r.xAxisLabels.put(BigRational.valueOf(53), "2012 September");
        r.xAxisLabels.put(BigRational.valueOf(54), "2012 October");
        return r;
    }

    @Override
    public void drawTitle(String title) {
        super.drawTitle(title);
        int barHeight = BigRational.valueOf(getAgeInterval()).divide(getCellHeight()).integerPart().toBigDecimal().intValue();
        extraHeightTop += barHeight;
    }

    protected void drawLegend() {
//        TreeMap<String, TreeMap<BigDecimal, BigDecimal>> maps;
//        maps = (TreeMap<String, TreeMap<BigDecimal, BigDecimal>>) data[0];
        TreeMap<String, Boolean> nonZero2 = getData().nonZero2;
        Color[] colours;
        colours = getColours();
        int newLegendWidth = 0;
        int newLegendHeight = 0;
        int legendExtraWidthLeft = 0;
        int legendExtraWidthRight = 0;
        int th = getTextHeight();
        int legendExtraHeightBottom = th;
        int legendStartRow = dataEndRow + xAxisHeight;
        int symbolRow;
        int symbolCol;
        int row;
        int col;
        int symbolWidth = 10;
        // Legend Title
        int legendItemWidth = 0;
        String text = "Legend";
        int textWidth = getTextWidth(text);
        newLegendHeight += th;
        row = legendStartRow;
        //col = dataStartCol - yAxisWidth;
        col = th;
        legendItemWidth += textWidth;
        newLegendWidth = Math.max(newLegendWidth, legendItemWidth);
//        System.out.println("row " + row);
//        System.out.println("col " + col);
        setPaint(Color.DARK_GRAY);
        drawString(text, col, row);
        row += 2; // gap between "Legend" and first line
        int i = 1;
        Iterator<String> ite;
        //ite = maps.keySet().iterator();
        ite = getData().maps.keySet().iterator();
        while (ite.hasNext()) {
//            String type;
//            type = ite.next();
//            TreeMap<BigDecimal, BigDecimal> map;
//            map = maps.get(type);
            int j = i;
            while (j >= colours.length) {
                j -= colours.length;
            }
            String label;
            label = ite.next();
            if (nonZero2 == null) {
                col = th + symbolWidth + 2;
                row += th + 2;
                setPaint(Color.DARK_GRAY);
                drawString(label, col, row);
                setPaint(colours[j]);
                Line2D line = new Line2D.Double(th, row, col - 2, row - th);
                draw(line);
                newLegendHeight += th + 2;
                i++;
            } else {
                if (nonZero2.get(label)) {
                    col = th + symbolWidth + 2;
                    row += th + 2;
                    setPaint(Color.DARK_GRAY);
                    drawString(label, col, row);
                    setPaint(colours[j]);
                    Line2D line;
                    line = new Line2D.Double(th, row, col - 2, row - th);
                    draw(line);
                    newLegendHeight += th + 2;
                    i++;
                }
            }
        }
        legendHeight = newLegendHeight;
        imageHeight += newLegendHeight;
    }

    @Override
    public int[] drawYAxis(int interval, int textHeight, int startOfEndInterval, int scaleTickLength, int scaleTickAndTextSeparation, int partTitleGap, int seperationDistanceOfAxisAndData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
