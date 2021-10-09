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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import uk.ac.leeds.ccg.chart.core.Chart_AgeGender;
import uk.ac.leeds.ccg.chart.data.Chart_AgeGenderBarData;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.generic.execution.Generic_Execution;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.math.number.Math_BigRational;

/**
 * An implementation of <code>Chart_AgeGenderBarExample</code>
 *
 * If you run this class it will attempt to generate an Age by Gender Population
 * Bar Chart Visualization of some default data and write it out to file as a
 * PNG.
 */
public class Chart_AgeGenderBarExample extends Chart_AgeGender {

    public Chart_AgeGenderBarExample(Generic_Environment e) {
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
    public Chart_AgeGenderBarExample(Generic_Environment e, ExecutorService es,
            Path f, String format,
            String title, int dataWidth, int dataHeight, String xAxisLabel,
            String yAxisLabel, boolean drawOriginLinesOnPlot, int ageInterval,
            int startAgeOfEndYearInterval, int dpc, int dpd, RoundingMode rm) {
        super(e);
        init(es, f, format, title, dataWidth, dataHeight, xAxisLabel,
                yAxisLabel, drawOriginLinesOnPlot, ageInterval,
                startAgeOfEndYearInterval, dpc, dpd, rm);
    }

    @Override
    public void drawData() {
        drawBarChart(getAgeInterval());
    }

    /**
     * @param ageInterval
     */
    public void drawBarChart(int ageInterval) {
        setPaint(Color.DARK_GRAY);
        Math_BigRational cellWidth = getCellWidth();
        // Female Age_In_Years Population Counts
        Chart_AgeGenderBarData d = getData();
        TreeMap<Long, BigDecimal> f = d.female;
        // Male Age_In_Years Population Counts
        TreeMap<Long, BigDecimal> m = d.male;
        Iterator<Map.Entry<Long, BigDecimal>> ite;
        Map.Entry<Long, BigDecimal> entry;
        Long age;
        BigDecimal population;
        int barGap = 4;
        int barHeight;
        Math_BigRational cellHeight = getCellHeight();
        if (cellHeight.compareTo(Math_BigRational.ZERO) == 0) {
            barHeight = 1;
        } else {
            barHeight = Math_BigRational.valueOf(ageInterval).divide(getCellHeight()).integerPart().toBigDecimal().intValue() - barGap;
        }
        if (barHeight < 1) {
            barHeight = 1;
        }
        // Draw Female bars
        ite = f.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();

            // For some reason sometimes we get a Integer instead of a Long!
            if (!(entry.getKey() instanceof Long)) {
                System.out.println(entry.getKey());
                String s = String.valueOf(entry.getKey());
                age = Long.valueOf(s);
            } else {
                age = entry.getKey();
            }

            population = entry.getValue();
            int barWidth = Math_BigRational.valueOf(population).divide(cellWidth).integerPart().toBigDecimal().intValue();
//            int barTopRow = coordinateToScreenRow(
//                    BigDecimal.valueOf(age + ageInterval))
//                    + barGapDiv2;
            int barTopRow = coordinateToScreenRow(
                    Math_BigRational.valueOf(age + ageInterval))
                    + barGap;
            setPaint(Color.DARK_GRAY);
            fillRect(originCol, barTopRow, barWidth, barHeight);
        }
        // Draw Male bars
        ite = m.entrySet().iterator();
        while (ite.hasNext()) {
            entry = ite.next();

            // For some reason sometimes we get a Integer instead of a Long!
            if (!(entry.getKey() instanceof Long)) {
                System.out.println(entry.getKey());
                String s = String.valueOf(entry.getKey());
                age = Long.valueOf(s);
            } else {
                age = entry.getKey();
            }

            population = entry.getValue();
            int barWidth = Math_BigRational.valueOf(population).divide(cellWidth).integerPart().toBigDecimal().intValueExact();
//            int barTopRow = coordinateToScreenRow(
//                    BigDecimal.valueOf(age + ageInterval))
//                    + barGapDiv2;
            int barTopRow = coordinateToScreenRow(
                    Math_BigRational.valueOf(age + ageInterval))
                    + barGap;
            setPaint(Color.LIGHT_GRAY);
//            Rectangle2D r2 = new Rectangle2D.Double(
//                    originCol - barWidth,
//                    barTopRow,
//                    barWidth,
//                    barHeight);
            fillRect(
                    originCol - barWidth,
                    barTopRow,
                    barWidth,
                    barHeight);
//            setPaint(Color.BLACK);
//            draw(r2);
        }
    }

    public static void main(String[] args) {
        try {
            Generic_Environment e = new Generic_Environment(
                    new Generic_Defaults());

            /**
             * Initialise title and Path to write image to.
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
                title = "Age Gender Population Bar Chart";
                System.out.println("Use default title: " + title);
                file = Paths.get(System.getProperty("user.dir"),
                        title.replace(" ", "_") + "." + format);
                System.out.println("Use default Path: " + file.toString());
            } else {
                title = args[0];
                file = Paths.get(args[1]);
            }
            int dataWidth = 250;
            int dataHeight = 500;
            String xAxisLabel = "Population";
            String yAxisLabel = "Age";
            boolean drawOriginLinesOnPlot = true;
            int ageInterval = 5;
            int startAgeOfEndYearInterval = 60;
            /**
             * decimalPlacePrecisionForCalculations
             */
            int dpc = 10;
            /**
             * decimalPlacePrecisionForDisplay
             */
            int dpd = 3;
            RoundingMode rm = RoundingMode.HALF_UP;
            ExecutorService es = Executors.newSingleThreadExecutor();
            Chart_AgeGenderBarExample chart = new Chart_AgeGenderBarExample(e, es, file,
                    format, title, dataWidth, dataHeight, xAxisLabel,
                    yAxisLabel, drawOriginLinesOnPlot, ageInterval,
                    startAgeOfEndYearInterval, dpc, dpd, rm);
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

    /**
     * @return Default data for this type of chart.
     */
    public Chart_AgeGenderBarData getDefaultData() {
        int femalePopAge0 = 10000;
        int malePopAge0 = 9900;
        int ageInterval = 5;
        int startAgeOfEndYearInterval = 60;
        return getDefaultData(femalePopAge0, malePopAge0, ageInterval,
                startAgeOfEndYearInterval);
    }

    /**
     * @param femalePopAge0 Female population age 0.
     * @param malePopAge0 Male population age 0.
     * @param ageInterval Age interval.
     * @param saeyi startAgeOfEndYearInterval
     * @return Chart_AgeGenderBarData
     */
    public static Chart_AgeGenderBarData getDefaultData(int femalePopAge0,
            int malePopAge0, int ageInterval, int saeyi) {
        Chart_AgeGenderBarData r = getDefaultData(femalePopAge0, malePopAge0);
        Iterator<Long> ite;
        Long age;
        BigDecimal pop;
        BigDecimal maxPop = BigDecimal.ZERO;
        long ageGroup;
        BigDecimal popGroup;
        ageGroup = 0;
        popGroup = BigDecimal.ZERO;
        boolean reportedPenultimateGroup;
        reportedPenultimateGroup = false;
        ite = r.female.keySet().iterator();
        while (ite.hasNext()) {
            age = ite.next();
            pop = r.female.get(age);
            if (age.intValue() > saeyi) {
                if (!reportedPenultimateGroup) {
                    r.fapc.put(ageGroup, popGroup);
                    maxPop = maxPop.max(popGroup);
                    //ageGroup = ageGroup + ageInterval;
                    popGroup = new BigDecimal(pop.toString());
                    reportedPenultimateGroup = true;
                }
                ageGroup = saeyi;
                popGroup = popGroup.add(pop);
                maxPop = maxPop.max(popGroup);
                r.fapc.put(ageGroup, popGroup);
            } else {
                if (age > ageGroup + ageInterval) {
                    r.fapc.put(ageGroup, popGroup);
                    maxPop = maxPop.max(popGroup);
                    ageGroup = ageGroup + ageInterval;
                    popGroup = new BigDecimal(pop.toString());
                } else {
                    popGroup = popGroup.add(pop);
                }
            }
        }
        ageGroup = 0;
        popGroup = BigDecimal.ZERO;
        reportedPenultimateGroup = false;
        ite = r.male.keySet().iterator();
        while (ite.hasNext()) {
            age = ite.next();
            pop = r.male.get(age);
            if (age.intValue() > saeyi) {
                if (!reportedPenultimateGroup) {
                    r.mapc.put(ageGroup, popGroup);
                    maxPop = maxPop.max(popGroup);
                    //ageGroup = ageGroup + ageInterval;
                    popGroup = new BigDecimal(pop.toString());
                    reportedPenultimateGroup = true;
                }
                ageGroup = saeyi;
                popGroup = popGroup.add(pop);
                maxPop = maxPop.max(popGroup);
                r.mapc.put(ageGroup, popGroup);
            } else {
                if (age > ageGroup + ageInterval) {
                    r.mapc.put(ageGroup, popGroup);
                    maxPop = maxPop.max(popGroup);
                    ageGroup = ageGroup + ageInterval;
                    popGroup = new BigDecimal(pop.toString());
                } else {
                    popGroup = popGroup.add(pop);
                }
            }
        }
        return r;
    }

    private static Chart_AgeGenderBarData getDefaultData(int femalePopAge0,
            int malePopAge0) {
        Chart_AgeGenderBarData r = new Chart_AgeGenderBarData();
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

    @Override
    public void drawTitle(String title) {
        super.drawTitle(title);
        int barHeight = Math_BigRational.valueOf(getAgeInterval()).divide(getCellHeight()).integerPart().toBigDecimal().intValue();
        extraHeightTop += barHeight;
    }

    @Override
    public Chart_AgeGenderBarData getData() {
        return (Chart_AgeGenderBarData) data;
    }
}
