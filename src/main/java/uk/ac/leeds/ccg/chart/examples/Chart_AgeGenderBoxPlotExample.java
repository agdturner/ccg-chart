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
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import uk.ac.leeds.ccg.chart.core.Chart_AgeGender;
import uk.ac.leeds.ccg.chart.data.Chart_AgeGenderBoxPlotData;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.stats.summary.Stats_BigDecimal1;
import uk.ac.leeds.ccg.stats.summary.Stats_Moments;
import uk.ac.leeds.ccg.math.number.Math_BigRational;

/**
 * An example of generating an Age by Gender Population Box Plot Visualization.
 */
public class Chart_AgeGenderBoxPlotExample extends Chart_AgeGender {

    public Chart_AgeGenderBoxPlotExample(Generic_Environment e) {
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
     * @param ageInterval The age interval.
     * @param startAgeOfEndYearInterval The start age of the end year interval.
     * @param dpc The decimal place precision for calculations.
     * @param dpd The decimal place precision for display.
     * @param rm The RoundingMode.
     */
    public Chart_AgeGenderBoxPlotExample(Generic_Environment e, ExecutorService es,
            Path f, String format, String title, int dataWidth, int dataHeight,
            String xAxisLabel, String yAxisLabel, boolean drawOriginLinesOnPlot,
            int ageInterval, int startAgeOfEndYearInterval, int dpc, int dpd,
            RoundingMode rm) {
        super(e);
        init(es, f, format, title, dataWidth, dataHeight, xAxisLabel,
                yAxisLabel, drawOriginLinesOnPlot, ageInterval,
                startAgeOfEndYearInterval, dpc,
                dpd, rm);
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
                System.out.println("Expected 2 args: args[0] title; args[1] Path."
                        + " Recieved " + args.length + " args.");
                // Use defaults
                title = "Age Gender Population Box Plot";
                System.out.println("Use default title: " + title);
                Path outdir = e.files.getOutputDir();
                file = Paths.get(outdir.toString(), title.replace(" ", "_") + "." + format);
                System.out.println("Use default Path: " + file.toString());
            } else {
                title = args[0];
                file = Paths.get(args[1]);
            }
            int dataWidth = 1000;//250;
            int dataHeight = 500;
            String xAxisLabel = "Population";
            String yAxisLabel = "Age";
            boolean drawOriginLinesOnPlot = true;
            int ageInterval = 5;
            int startAgeOfEndYearInterval = 70;//95;
            int decimalPlacePrecisionForCalculations = 10;
            int decimalPlacePrecisionForDisplay = 3;
            RoundingMode rm = RoundingMode.HALF_UP;
            ExecutorService es = Executors.newSingleThreadExecutor();
            Chart_AgeGenderBoxPlotExample plot = new Chart_AgeGenderBoxPlotExample(e, es, file,
                    format, title, dataWidth, dataHeight, xAxisLabel, yAxisLabel,
                    drawOriginLinesOnPlot, ageInterval, startAgeOfEndYearInterval,
                    decimalPlacePrecisionForCalculations,
                    decimalPlacePrecisionForDisplay, rm);
            plot.setData(plot.getDefaultData());
            plot.vis.getHeadlessEnvironment();
            //plot.run();
            plot.start();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);

        }
    }
    
    /**
     *
     * @return
     */
    @Override
    public Chart_AgeGenderBoxPlotData getData() {
        return (Chart_AgeGenderBoxPlotData) data;
    }

    @Override
    public void drawData() {
        drawBoxplots();
    }

    public void drawBoxplots() {
        int ageInterval = getAgeInterval();
        Line2D abLine2D;
        Chart_AgeGenderBoxPlotData d = getData();
//        TreeMap<Integer, Stats_BigDecimal2> fdata;
//        fdata = (TreeMap<Integer, Stats_BigDecimal2>) data[0];
//        TreeMap<Integer, Stats_BigDecimal2> mdata;
//        mdata = (TreeMap<Integer, Stats_BigDecimal2>) data[1];

        Iterator<Map.Entry<Long, Stats_BigDecimal1>> ite;
        Map.Entry<Long, Stats_BigDecimal1> entry;
        Long age;
        /*
         * result[0] = sum;
         * result[1] = mean;
         * result[2] = median;
         * result[3] = q1;
         * result[4] = q3;
         * result[5] = mode;
         * result[6] = min;
         * result[7] = max;
         */
        // boxHeight is in pixels. 4 pixels are used to seperate boxPlots 
        // between age intervals. If boxHeight is less than 3 then each boxplot 
        // will display more like a line. If the boxHeight is an odd number 
        // then this allows for a central centre line that is one pixel thick.
//        int boxHeight = (Math_BigDecimal.divideRoundIfNecessary(
//                BigDecimal.valueOf(ageInterval),
//                getCellHeight(),
//                0,
//                getRoundingMode()).intValueExact()) - 4;
        int boxHeight = Math_BigRational.valueOf(ageInterval).divide(getCellHeight()).integerPart().toBigDecimal().intValue() - 4;
        int whiskerHeight = boxHeight / 2;

        // Calculate plot drawing metrics
        Math_BigRational cellWidth = getCellWidth();
        /*
         * Draw Female Box Plots
         */
        ite = d.fss.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            age = entry.getKey();
            Stats_BigDecimal1 stats = entry.getValue();
            Stats_Moments moments = stats.getMoments();
            // Calculate plot drawing metrics
//            int boxWidth = Math_BigDecimal.divideRoundIfNecessary(
//                    stats[4].subtract(stats[3]), cellWidth, 0,
//                    getRoundingMode()).intValueExact();
            int boxWidth = Math_BigRational.valueOf(stats.getQ3().subtract(stats.getQ1())).divide(cellWidth).integerPart().toBigDecimal().intValue();

            int boxTopRow = coordinateToScreenRow(Math_BigRational.valueOf(age + 1)) + 2;
            //int boxTopRow = coordinateToScreenRow(BigDecimal.valueOf(age));
            int boxMiddleRow = boxTopRow + (boxHeight / 2);
            int boxBottomRow = boxTopRow + boxHeight;
            int q1Col = coordinateToScreenCol(Math_BigRational.valueOf(stats.getQ1()));
            int q3Col = coordinateToScreenCol(Math_BigRational.valueOf(stats.getQ3()));

            // Draw min line
            setPaint(Color.DARK_GRAY);
            int minCol = coordinateToScreenCol(Math_BigRational.valueOf(stats.getMin()));
            System.out.println();
            abLine2D = new Line2D.Double(minCol, boxMiddleRow, q1Col, boxMiddleRow);
            draw(abLine2D);
            abLine2D = new Line2D.Double(
                    minCol, boxMiddleRow + (whiskerHeight / 2),
                    minCol, boxMiddleRow - (whiskerHeight / 2));
            draw(abLine2D);

            // Draw max line
            int maxCol = coordinateToScreenCol(Math_BigRational.valueOf(stats.getMax()));
            abLine2D = new Line2D.Double(maxCol, boxMiddleRow, q3Col,
                    boxMiddleRow);
            draw(abLine2D);
            abLine2D = new Line2D.Double(
                    maxCol, boxMiddleRow + (whiskerHeight / 2),
                    maxCol, boxMiddleRow - (whiskerHeight / 2));
            draw(abLine2D);
            // Draw box after drawing min and max lines to neaten overlaps
            setPaint(Color.WHITE);
            fillRect(q1Col, boxTopRow, boxWidth, boxHeight);
            setPaint(Color.DARK_GRAY);
            Rectangle2D r2;
            r2 = new Rectangle2D.Double(q1Col, boxTopRow, boxWidth, boxHeight);
            setPaint(Color.DARK_GRAY);
            draw(r2);

            // Draw median line
            int medianCol = coordinateToScreenCol(stats.getMedian());
            abLine2D = new Line2D.Double(medianCol, boxTopRow, medianCol,
                    boxBottomRow);
            draw(abLine2D);
        }
        /*
         * result[0] = sum;
         * result[1] = mean;
         * result[2] = median;
         * result[3] = q1;
         * result[4] = q3;
         * result[5] = mode;
         * result[6] = min;
         * result[7] = max;
         */
 /*
         * Draw Male Box Plots
         */
        ite = d.mss.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();
            age = entry.getKey();
            Stats_BigDecimal1 stats = entry.getValue();

            // Calculate plot drawing metrics
            int boxWidth = Math_BigRational.valueOf(stats.getQ3().subtract(stats.getQ1())).divide(getCellWidth()).integerPart().toBigDecimal().intValueExact();
            int boxTopRow = coordinateToScreenRow(Math_BigRational.valueOf(age + 1)) + 2;
            //int boxTopRow = coordinateToScreenRow(BigDecimal.valueOf(age));
            int boxMiddleRow = boxTopRow + (boxHeight / 2);
            int boxBottomRow = boxTopRow + boxHeight;
            int q1Col = coordinateToScreenCol(Math_BigRational.valueOf(stats.getQ1().negate()));
            int q3Col = coordinateToScreenCol(Math_BigRational.valueOf(stats.getQ3().negate()));

            // Draw min line
            setPaint(Color.DARK_GRAY);
            int minCol = coordinateToScreenCol(Math_BigRational.valueOf(stats.getMin().negate()));
            abLine2D = new Line2D.Double(minCol, boxMiddleRow, q1Col, boxMiddleRow);
            draw(abLine2D);
            abLine2D = new Line2D.Double(minCol, boxMiddleRow + (whiskerHeight / 2),
                    minCol, boxMiddleRow - (whiskerHeight / 2));
            draw(abLine2D);

            // Draw max line
            setPaint(Color.DARK_GRAY);
            int maxCol = coordinateToScreenCol(Math_BigRational.valueOf(stats.getMax().negate()));
            abLine2D = new Line2D.Double(maxCol, boxMiddleRow, q3Col, boxMiddleRow);
            draw(abLine2D);
            abLine2D = new Line2D.Double(maxCol, boxMiddleRow + (whiskerHeight / 2),
                    maxCol, boxMiddleRow - (whiskerHeight / 2));
            draw(abLine2D);

            // Draw box after drawing min and max lines to neaten overlaps
            setPaint(Color.DARK_GRAY);
            Rectangle2D r2;
            r2 = new Rectangle2D.Double(q3Col, boxTopRow, boxWidth, boxHeight);
            setPaint(Color.WHITE);
            fillRect(q3Col, boxTopRow, boxWidth, boxHeight);
            setPaint(Color.DARK_GRAY);
            draw(r2);

            // Draw median line
            int medianCol = coordinateToScreenCol(stats.getMedian().negate());
            abLine2D = new Line2D.Double(medianCol, boxTopRow, medianCol, boxBottomRow);
            draw(abLine2D);
        }
    }

    /**
     * @return default data for this type of chart. 
     */
    public Chart_AgeGenderBoxPlotData getDefaultData() {
        int ageInterval = 5;
        int startAgeOfEndYearInterval = 70;//95;
        dpc = 10;
        RoundingMode rm = RoundingMode.HALF_UP;
        return getDefaultData(ageInterval, startAgeOfEndYearInterval,
                dpc, rm);
    }

    /**
     * Override this method to use other data
     *
     * @param femalePopAge0
     * @param malePopAge0
     * @return
     */
    private static Chart_AgeGenderBoxPlotData getDefaultData(int femalePopAge0,
            int malePopAge0) {
        Chart_AgeGenderBoxPlotData r = new Chart_AgeGenderBoxPlotData();
        BigDecimal pop;
        BigDecimal change;
        long age;
        pop = new BigDecimal("" + femalePopAge0);
        change = new BigDecimal("0.94");
        for (age = 0; age < 5; age++) {
            r.female.put(age, pop);
            pop = pop.multiply(change);
        }
        change = new BigDecimal("0.95");
        for (age = 5; age < 10; age++) {
            r.female.put(age, pop);
            pop = pop.multiply(change);
        }
        change = new BigDecimal("0.96");
        for (age = 10; age < 15; age++) {
            r.female.put(age, pop);
            pop = pop.multiply(change);
        }
        change = new BigDecimal("0.97");
        for (age = 15; age < 20; age++) {
            r.female.put(age, pop);
            pop = pop.multiply(change);
        }
        change = new BigDecimal("0.99");
        for (age = 20; age < 60; age++) {
            r.female.put(age, pop);
            pop = pop.multiply(change);
        }
        change = new BigDecimal("0.97");
        for (age = 60; age < 80; age++) {
            r.female.put(age, pop);
            pop = pop.multiply(change);
        }
        change = new BigDecimal("0.75");
        for (age = 80; age < 100; age++) {
            r.female.put(age, pop);
            pop = pop.multiply(change);
        }
        pop = new BigDecimal("" + malePopAge0);
        change = new BigDecimal("0.93");
        for (age = 0; age < 5; age++) {
            r.male.put(age, pop);
            pop = pop.multiply(change);
        }
        change = new BigDecimal("0.94");
        for (age = 5; age < 10; age++) {
            r.male.put(age, pop);
            pop = pop.multiply(change);
        }
        change = new BigDecimal("0.95");
        for (age = 10; age < 15; age++) {
            r.male.put(age, pop);
            pop = pop.multiply(change);
        }
        change = new BigDecimal("0.96");
        for (age = 15; age < 20; age++) {
            r.male.put(age, pop);
            pop = pop.multiply(change);
        }
        change = new BigDecimal("0.98");
        for (age = 20; age < 60; age++) {
            r.male.put(age, pop);
            pop = pop.multiply(change);
        }
        change = new BigDecimal("0.7");
        for (age = 60; age < 70; age++) {
            r.male.put(age, pop);
            pop = pop.multiply(change);
        }
        change = new BigDecimal("0.5");
        for (age = 70; age < 100; age++) {
            r.male.put(age, pop);
            pop = pop.multiply(change);
        }
        return r;
    }

    private static void add(int p, Chart_AgeGenderBoxPlotData d) {
        Chart_AgeGenderBoxPlotData t = getDefaultData(p, p);
        d.femalePops.put(p, t.female);
        d.malePops.put(p, t.male);
    }
    
    /**
     * Returns a sample dataset.
     *
     * @param ageInterval Age interval.
     * @param rm The RoundingMode.
     * @param saeyi The start age of the end year interval.
     * @param dpc decimalPlacePrecisionForCalculations
     * @return The dataset.
     */
    public static Chart_AgeGenderBoxPlotData getDefaultData(int ageInterval,
            int saeyi, int dpc, RoundingMode rm) {
        //int startAgeOfEndYearInterval = getStartAgeOfEndYearInterval();
        Chart_AgeGenderBoxPlotData r = new Chart_AgeGenderBoxPlotData();
//        TreeMap<Integer, Stats_BigDecimal1> femaleBoxPlotStats = new TreeMap<>();
//        TreeMap<Integer, Stats_BigDecimal1> maleBoxPlotStats = new TreeMap<>();
        add(10000, r);
        add(9000, r);
        add(9900, r);
        add(9950, r);
        add(9800, r);
        add(99000, r);
        Iterator<Long> iterator;
        Long age = 0L;
        BigDecimal pop10000;
        BigDecimal pop9000;
        BigDecimal pop9900;
        BigDecimal pop9950;
        BigDecimal pop9800;
        r.max = Math_BigRational.ZERO;
        iterator = r.femalePops.get(10000).keySet().iterator();
        ArrayList<BigDecimal> values = null;
        while (iterator.hasNext()) {
            pop10000 = BigDecimal.ZERO;
            pop9000 = BigDecimal.ZERO;
            pop9900 = BigDecimal.ZERO;
            pop9950 = BigDecimal.ZERO;
            pop9800 = BigDecimal.ZERO;
            for (int interval = 0; interval < ageInterval; interval++) {
                age = iterator.next();
                if (age >= saeyi) {
                    pop10000 = values.get(0).add(r.femalePops.get(10000).get(age));
                    pop9000 = values.get(1).add(r.femalePops.get(9000).get(age));
                    pop9900 = values.get(2).add(r.femalePops.get(9900).get(age));
                    pop9950 = values.get(3).add(r.femalePops.get(9950).get(age));
                    pop9800 = values.get(4).add(r.femalePops.get(9800).get(age));
                } else {
                    pop10000 = pop10000.add(r.femalePops.get(10000).get(age));
                    pop9000 = pop9000.add(r.femalePops.get(9000).get(age));
                    pop9900 = pop9900.add(r.femalePops.get(9900).get(age));
                    pop9950 = pop9950.add(r.femalePops.get(9950).get(age));
                    pop9800 = pop9800.add(r.femalePops.get(9800).get(age));
                }
            }
            if (age < saeyi) {
                values = new ArrayList<>();
            }
            r.max = r.max.max(Math_BigRational.valueOf(pop10000));
            r.max = r.max.max(Math_BigRational.valueOf(pop9000));
            r.max = r.max.max(Math_BigRational.valueOf(pop9900));
            r.max = r.max.max(Math_BigRational.valueOf(pop9950));
            r.max = r.max.max(Math_BigRational.valueOf(pop9800));
            values.add(pop10000);
            values.add(pop9000);
            values.add(pop9900);
            values.add(pop9950);
            values.add(pop9800);
            Stats_BigDecimal1 ss = new Stats_BigDecimal1(values);
            System.out.println("Female age " + age);
            if (age < saeyi) {
                r.fss.put(age, ss);
            } else {
                r.fss.put((long) saeyi + ageInterval, ss);
            }
        }
        iterator = r.malePops.get(10000).keySet().iterator();
        while (iterator.hasNext()) {
            pop10000 = BigDecimal.ZERO;
            pop9000 = BigDecimal.ZERO;
            pop9900 = BigDecimal.ZERO;
            pop9950 = BigDecimal.ZERO;
            pop9800 = BigDecimal.ZERO;
            for (int interval = 0; interval < ageInterval; interval++) {
                age = iterator.next();
                if (age >= saeyi) {
                    pop10000 = values.get(0).add(r.malePops.get(10000).get(age));
                    pop9000 = values.get(1).add(r.malePops.get(9000).get(age));
                    pop9900 = values.get(2).add(r.malePops.get(9900).get(age));
                    pop9950 = values.get(3).add(r.malePops.get(9950).get(age));
                    pop9800 = values.get(4).add(r.malePops.get(9800).get(age));
                } else {
                    pop10000 = pop10000.add(r.malePops.get(10000).get(age));
                    pop9000 = pop9000.add(r.malePops.get(9000).get(age));
                    pop9900 = pop9900.add(r.malePops.get(9900).get(age));
                    pop9950 = pop9950.add(r.malePops.get(9950).get(age));
                    pop9800 = pop9800.add(r.malePops.get(9800).get(age));
                }
            }
            if (age < saeyi) {
                values = new ArrayList<>();
            }
            r.max = r.max.max(Math_BigRational.valueOf(pop10000));
            r.max = r.max.max(Math_BigRational.valueOf(pop9000));
            r.max = r.max.max(Math_BigRational.valueOf(pop9900));
            r.max = r.max.max(Math_BigRational.valueOf(pop9950));
            r.max = r.max.max(Math_BigRational.valueOf(pop9800));
            values.add(pop10000);
            values.add(pop9000);
            values.add(pop9900);
            values.add(pop9950);
            values.add(pop9800);
            System.out.println("Male age " + age);
            Stats_BigDecimal1 ss = new Stats_BigDecimal1(values);
            if (age < saeyi) {
                r.mss.put(age, ss);
            } else {
                r.mss.put((long) saeyi + ageInterval, ss);
            }
        }
//        r.m//minX = maxValue.negate();
//        r[2] = maxValue;
//        r[3] = BigDecimal.valueOf(100);
//        r[4] = BigDecimal.ZERO;
        return r;
    }

    /**
     * Returns a sample dataset.
     *
     * @return The data set.
     */
    private static Chart_AgeGenderBoxPlotData getDefaultData(RoundingMode rm, int dpc) {
        Chart_AgeGenderBoxPlotData r = new Chart_AgeGenderBoxPlotData();
        add(10000, r);
        add(9000, r);
        add(9900, r);
        add(9950, r);
        add(9800, r);
        Iterator<Long> iterator;
        Long age;
        BigDecimal pop;
        r.max = Math_BigRational.ZERO;
        iterator = r.femalePops.get(10000).keySet().iterator();
        while (iterator.hasNext()) {
            age = iterator.next();
            pop = r.femalePops.get(10000).get(age);
            ArrayList<BigDecimal> values = new ArrayList<>();
            r.max = r.max.max(Math_BigRational.valueOf(pop));
            values.add(pop);
            pop = r.femalePops.get(9000).get(age);
            r.max = r.max.max(Math_BigRational.valueOf(pop));
            values.add(pop);
            pop = r.femalePops.get(9900).get(age);
            r.max = r.max.max(Math_BigRational.valueOf(pop));
            values.add(pop);
            pop = r.femalePops.get(9950).get(age);
            r.max = r.max.max(Math_BigRational.valueOf(pop));
            values.add(pop);
            pop = r.femalePops.get(9800).get(age);
            r.max = r.max.max(Math_BigRational.valueOf(pop));
            values.add(pop);
            Stats_BigDecimal1 ss = new Stats_BigDecimal1(values);
            r.fss.put(age, ss);
        }
        r.max = Math_BigRational.ZERO;
        iterator = r.malePops.get(10000).keySet().iterator();
        while (iterator.hasNext()) {
            age = iterator.next();
            pop = r.malePops.get(10000).get(age);
            ArrayList<BigDecimal> values = new ArrayList<>();
            r.max = r.max.max(Math_BigRational.valueOf(pop));
            values.add(pop);
            pop = r.malePops.get(9000).get(age);
            r.max = r.max.max(Math_BigRational.valueOf(pop));
            values.add(pop);
            pop = r.malePops.get(9900).get(age);
            r.max = r.max.max(Math_BigRational.valueOf(pop));
            values.add(pop);
            pop = r.malePops.get(9950).get(age);
            r.max = r.max.max(Math_BigRational.valueOf(pop));
            values.add(pop);
            pop = r.malePops.get(9800).get(age);
            r.max = r.max.max(Math_BigRational.valueOf(pop));
            values.add(pop);
            Stats_BigDecimal1 ss = new Stats_BigDecimal1(values);
            r.mss.put(age, ss);
        }
        return r;
    }

    @Override
    public void drawTitle(String title) {
        super.drawTitle(title);
        int barHeight = Math_BigRational.valueOf(getAgeInterval()).divide(getCellHeight()).integerPart().toBigDecimal().intValue();
        extraHeightTop += barHeight;
    }
}
