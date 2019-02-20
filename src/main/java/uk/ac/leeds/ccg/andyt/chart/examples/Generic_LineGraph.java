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
package uk.ac.leeds.ccg.andyt.chart.examples;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import uk.ac.leeds.ccg.andyt.chart.core.Generic_AbstractLineGraph;
import uk.ac.leeds.ccg.andyt.math.Math_BigDecimal;
import uk.ac.leeds.ccg.andyt.generic.execution.Generic_Execution;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_Files;
import uk.ac.leeds.ccg.andyt.generic.util.Generic_Collections;
import uk.ac.leeds.ccg.andyt.generic.visualisation.Generic_Visualisation;

/**
 * An example of generating a Line Graph visualization.
 */
public class Generic_LineGraph extends Generic_AbstractLineGraph {

    /**
     * Iff set to true then a line is added to the graph at Y = 0.
     */
    boolean drawYZero;

    public Generic_LineGraph() {
    }

    /**
     *
     * @param es
     * @param file
     * @param format
     * @param title
     * @param dataWidth
     * @param dataHeight
     * @param xAxisLabel
     * @param yAxisLabel
     * @param yMax
     * @param yPin
     * @param yIncrement
     * @param numberOfYAxisTicks
     * @param drawYZero
     * @param decimalPlacePrecisionForCalculations
     * @param decimalPlacePrecisionForDisplay
     * @param r
     */
    public Generic_LineGraph(ExecutorService es, File file, String format,
            String title, int dataWidth, int dataHeight, String xAxisLabel,
            String yAxisLabel, BigDecimal yMax, ArrayList<BigDecimal> yPin,
            BigDecimal yIncrement, int numberOfYAxisTicks, boolean drawYZero,
            int decimalPlacePrecisionForCalculations,
            int decimalPlacePrecisionForDisplay, RoundingMode r) {
        this.yMax = yMax;
        this.yPin = yPin;
        this.yIncrement = yIncrement;
        this.numberOfYAxisTicks = numberOfYAxisTicks;
        this.drawYZero = drawYZero;
        init(es, file, format, title, dataWidth, dataHeight, xAxisLabel,
                yAxisLabel, false, decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay, r);
    }

    @Override
    public void drawData() {
        TreeMap<String, TreeMap<BigDecimal, BigDecimal>> maps;
        maps = (TreeMap<String, TreeMap<BigDecimal, BigDecimal>>) data[0];
        TreeMap<String, Boolean> nonZero = null;
        if (data.length > 7) {
            nonZero = (TreeMap<String, Boolean>) data[7];
        }
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
     *
     * @param map
     * @param c
     */
    public void drawMap(TreeMap<BigDecimal, BigDecimal> map, Color c) {
        int length;
        length = 3;
        int row0 = 0;
        int col0 = 0;
        setPaint(c);
        boolean first = true;
        Iterator<BigDecimal> ite;
        ite = map.keySet().iterator();
        while (ite.hasNext()) {
            BigDecimal x;
            x = ite.next();
            BigDecimal y;
            y = map.get(x);
            int row = coordinateToScreenRow(y);
            int col = coordinateToScreenCol(x);
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
                Line2D line;
                line = new Line2D.Double(col0, row0, col, row);
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
     *
     * @param col
     * @param row
     * @param l length of each cross part
     */
    public void drawCross(int col, int row, int l) {
        Line2D line;
        line = new Line2D.Double(col - l, row - l, col + l, row + l);
        draw(line);
        line = new Line2D.Double(col - l, row + l, col + l, row - l);
        draw(line);
    }

    public static void main(String[] args) {
        Generic_Visualisation.getHeadlessEnvironment();
        /*
         * Initialise title and File to write image to
         */
        String title;
        File file;
        String format = "PNG";
        if (args.length != 2) {
            System.out.println("Expected 2 args: args[0] title; args[1] File."
                    + " Recieved " + args.length + " args.");
            // Use defaults
            title = "Example Line Graph";
            System.out.println("Use default title: " + title);
            Generic_Files files = new Generic_Files();
            File outdir;
            outdir = files.getOutputDataDir();
            file = new File(outdir, title.replace(" ", "_") + "." + format);
            System.out.println("Use default File: " + file.toString());
        } else {
            title = args[0];
            file = new File(args[1]);
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
        ArrayList<BigDecimal> yPin;
        yPin = new ArrayList<>();
        yPin.add(BigDecimal.ZERO);
        //BigDecimal yIncrement = BigDecimal.ONE;
        BigDecimal yIncrement = null;
        //int yAxisStartOfEndInterval = 60;
        int decimalPlacePrecisionForCalculations = 10;
        int decimalPlacePrecisionForDisplay = 3;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        ExecutorService es = Executors.newSingleThreadExecutor();
        Generic_LineGraph chart = new Generic_LineGraph(es, file, format, title,
                dataWidth, dataHeight, xAxisLabel, yAxisLabel, yMax, yPin,
                yIncrement, numberOfYAxisTicks, drawYZero,
                decimalPlacePrecisionForCalculations,
                decimalPlacePrecisionForDisplay,
                roundingMode);
        chart.setData(chart.getDefaultData());
        chart.run();
        Future future = chart.future;
        Generic_Execution.shutdownExecutorService(es, future, chart);
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
            int zero = coordinateToScreenRow(BigDecimal.ZERO);
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

    @Override
    public Object[] getDefaultData() {
        Object[] result;
        result = new Object[7];
        TreeMap<String, TreeMap<BigDecimal, BigDecimal>> maps;
        maps = new TreeMap<>();
        TreeMap<BigDecimal, BigDecimal> map;
        map = new TreeMap<>();
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
        maps.put("map1", map);
        TreeMap<BigDecimal, BigDecimal> map2;
        map2 = new TreeMap<>();
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
        maps.put("map2", map2);
        BigDecimal[] minMaxBigDecimal;
        minMaxBigDecimal = Generic_Collections.getMinMaxBigDecimal(map);
        minY = minMaxBigDecimal[0];
        maxY = minMaxBigDecimal[1];
        minX = map.firstKey();
        maxX = map.lastKey();
        minMaxBigDecimal = Generic_Collections.getMinMaxBigDecimal(map2);
        if (minY.compareTo(minMaxBigDecimal[0]) == 1) {
            minY = minMaxBigDecimal[0];
        }
        if (maxY.compareTo(minMaxBigDecimal[1]) == -1) {
            maxY = minMaxBigDecimal[1];
        }
        if (minX.compareTo(map2.firstKey()) == 1) {
            minX = map2.firstKey();
        }
        if (maxX.compareTo(map2.lastKey()) == -1) {
            maxX = map2.lastKey();
        }
        result[0] = maps;
        result[1] = minY;
        result[2] = maxY;
        result[3] = minX;
        result[4] = maxX;
        ArrayList<String> labels;
        labels = new ArrayList<>();
        labels.addAll(maps.keySet());
        result[5] = labels;

        // Comment out the following section to have a normal axis instead of labels.
        TreeMap<BigDecimal, String> xAxisLabels;
        xAxisLabels = new TreeMap<>();
        xAxisLabels.put(new BigDecimal(0.0d), "2008 April");
        xAxisLabels.put(new BigDecimal(6.0d), "2008 October");
        xAxisLabels.put(new BigDecimal(12.0d), "2009 April");
        xAxisLabels.put(new BigDecimal(18.0d), "2009 October");
        xAxisLabels.put(new BigDecimal(24.0d), "2010 April");
        xAxisLabels.put(new BigDecimal(27.0d), "2010 July");
        xAxisLabels.put(new BigDecimal(30.0d), "2010 October");
        xAxisLabels.put(new BigDecimal(33.0d), "2010 January");
        xAxisLabels.put(new BigDecimal(36.0d), "2011 April");
        xAxisLabels.put(new BigDecimal(39.0d), "2011 July");
        xAxisLabels.put(new BigDecimal(42.0d), "2011 October");
        xAxisLabels.put(new BigDecimal(45.0d), "2012 January");
        xAxisLabels.put(new BigDecimal(48.0d), "2012 April");
        xAxisLabels.put(new BigDecimal(49.0d), "2012 May");
        xAxisLabels.put(new BigDecimal(50.0d), "2012 June");
        xAxisLabels.put(new BigDecimal(51.0d), "2012 April");
        xAxisLabels.put(new BigDecimal(52.0d), "2012 August");
        xAxisLabels.put(new BigDecimal(53.0d), "2012 September");
        xAxisLabels.put(new BigDecimal(54.0d), "2012 October");
        result[6] = xAxisLabels;
        return result;
    }

    @Override
    public void drawTitle(String title) {
        super.drawTitle(title);
        int barHeight = Math_BigDecimal.divideRoundIfNecessary(
                BigDecimal.valueOf(getAgeInterval()),
                getCellHeight(), 0, getRoundingMode()).intValue();
        extraHeightTop += barHeight;
    }

    protected void drawLegend() {
//        TreeMap<String, TreeMap<BigDecimal, BigDecimal>> maps;
//        maps = (TreeMap<String, TreeMap<BigDecimal, BigDecimal>>) data[0];
        TreeMap<String, Boolean> nonZero2 = null;
        if (data.length > 8) {
            nonZero2 = (TreeMap<String, Boolean>) data[8];
        }
        ArrayList<String> labels;
        labels = getLabels();
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
        ite = labels.iterator();
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
                Line2D line;
                line = new Line2D.Double(th, row, col - 2, row - th);
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
