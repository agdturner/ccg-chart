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
import uk.ac.leeds.ccg.chart.core.Chart_AbstractAgeGender;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.math.Math_BigDecimal;
import uk.ac.leeds.ccg.generic.execution.Generic_Execution;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;

/**
 * An implementation of <code>Chart_AgeGenderBar</code>
 *
 * If you run this class it will attempt to generate an Age by Gender Population
 * Bar Chart Visualization of some default data and write it out to file as a
 * PNG.
 */
public class Chart_AgeGenderBar extends Chart_AbstractAgeGender {

    public Chart_AgeGenderBar(Generic_Environment e) {
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
    public Chart_AgeGenderBar(Generic_Environment e, ExecutorService es,
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

    public void drawBarChart(int ageInterval) {
        setPaint(Color.DARK_GRAY);
        BigDecimal cellWidth = getCellWidth();
        TreeMap<Long, BigDecimal> femaleAgeInYearsPopulationCount_TreeMap = (TreeMap<Long, BigDecimal>) data[0];
        TreeMap<Long, BigDecimal> maleAgeInYearsPopulationCount_TreeMap = (TreeMap<Long, BigDecimal>) data[1];
        Iterator<Map.Entry<Long, BigDecimal>> ite;
        Map.Entry<Long, BigDecimal> entry;
        Long age;
        BigDecimal population;
        int barGap = 4;
//        int barGapDiv2 = barGap / 2;
//        int barHeight = Math_BigDecimal.divideRoundIfNecessary(
//                BigDecimal.valueOf(ageInterval),
//                getCellHeight(),
//                0,
//                roundingMode).intValueExact() - barGap;
        int barHeight;
        BigDecimal cellHeight = getCellHeight();
        if (cellHeight.compareTo(BigDecimal.ZERO) == 0) {
            barHeight = 1;
        } else {
            barHeight = Math_BigDecimal.divideRoundIfNecessary(
                    BigDecimal.valueOf(ageInterval),
                    getCellHeight(),
                    0,
                    roundingMode).intValue() - barGap;
        }
        if (barHeight < 1) {
            barHeight = 1;
        }
        // Draw Female bars
        ite = femaleAgeInYearsPopulationCount_TreeMap.entrySet().iterator();
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
            int barWidth = Math_BigDecimal.divideRoundIfNecessary(
                    population,
                    cellWidth,
                    0,
                    //roundingMode).intValueExact();
                    roundingMode).intValue();
//            int barTopRow = coordinateToScreenRow(
//                    BigDecimal.valueOf(age + ageInterval))
//                    + barGapDiv2;
            int barTopRow = coordinateToScreenRow(
                    BigDecimal.valueOf(age + ageInterval))
                    + barGap;
            setPaint(Color.DARK_GRAY);
//            Rectangle2D r2 = new Rectangle2D.Double(
//                    originCol,
//                    barTopRow,
//                    barWidth,
//                    barHeight);
            fillRect(
                    originCol,
                    barTopRow,
                    barWidth,
                    barHeight);
//            setPaint(Color.BLACK);
//            draw(r2);
        }
        // Draw Male bars
        ite = maleAgeInYearsPopulationCount_TreeMap.entrySet().iterator();
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
            int barWidth = Math_BigDecimal.divideRoundIfNecessary(
                    population,
                    cellWidth,
                    0,
                    roundingMode).intValueExact();
//            int barTopRow = coordinateToScreenRow(
//                    BigDecimal.valueOf(age + ageInterval))
//                    + barGapDiv2;
            int barTopRow = coordinateToScreenRow(
                    BigDecimal.valueOf(age + ageInterval))
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
            Chart_AgeGenderBar chart = new Chart_AgeGenderBar(e, es, file,
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

    @Override
    public Object[] getDefaultData() {
        int femalePopAge0 = 10000;
        int malePopAge0 = 9900;
        int ageInterval = 5;
        int startAgeOfEndYearInterval = 60;
        return getDefaultData(femalePopAge0, malePopAge0, ageInterval, 
                startAgeOfEndYearInterval);
    }

    /**
     *
     * @param femalePopAge0
     * @param malePopAge0
     * @param ageInterval
     * @param startAgeOfEndYearInterval
     * @return Object[] result: result[0] =
     * femaleAgeInYearsPopulationCount_TreeMap; result[1] =
     * maleAgeInYearsPopulationCount_TreeMap; result[2] = maxPop;
     */
    public static Object[] getDefaultData(
            int femalePopAge0,
            int malePopAge0,
            int ageInterval,
            int startAgeOfEndYearInterval) {
        Object[] result = new Object[3];
        Object[] data = getDefaultData(femalePopAge0, malePopAge0);
        // fapc femaleAgeInYearsPopulationCounts
        TreeMap<Long, BigDecimal> fapc = new TreeMap<>();
        // mapc maleAgeInYearsPopulationCounts
        TreeMap<Long, BigDecimal> mapc = new TreeMap<>();
        // syfapc singleYearFemaleAgeInYearsPopulationCounts
        TreeMap<Long, BigDecimal> syapc = (TreeMap<Long, BigDecimal>) data[0];
        // symapc singleYearMaleAgeInYearsPopulationCounts
        TreeMap<Long, BigDecimal> symapc = (TreeMap<Long, BigDecimal>) data[1];
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
        ite = syapc.keySet().iterator();
        while (ite.hasNext()) {
            age = ite.next();
            pop = syapc.get(age);
            if (age.intValue() > startAgeOfEndYearInterval) {
                if (!reportedPenultimateGroup) {
                    fapc.put(ageGroup, popGroup);
                    maxPop = maxPop.max(popGroup);
                    //ageGroup = ageGroup + ageInterval;
                    popGroup = new BigDecimal(pop.toString());
                    reportedPenultimateGroup = true;
                }
                ageGroup = startAgeOfEndYearInterval;
                popGroup = popGroup.add(pop);
                maxPop = maxPop.max(popGroup);
                fapc.put(ageGroup, popGroup);
            } else {
                if (age > ageGroup + ageInterval) {
                    fapc.put(ageGroup, popGroup);
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
        ite = symapc.keySet().iterator();
        while (ite.hasNext()) {
            age = ite.next();
            pop = symapc.get(age);
            if (age.intValue() > startAgeOfEndYearInterval) {
                if (!reportedPenultimateGroup) {
                    mapc.put(ageGroup, popGroup);
                    maxPop = maxPop.max(popGroup);
                    //ageGroup = ageGroup + ageInterval;
                    popGroup = new BigDecimal(pop.toString());
                    reportedPenultimateGroup = true;
                }
                ageGroup = startAgeOfEndYearInterval;
                popGroup = popGroup.add(pop);
                maxPop = maxPop.max(popGroup);
                mapc.put(ageGroup, popGroup);
            } else {
                if (age > ageGroup + ageInterval) {
                    mapc.put(ageGroup, popGroup);
                    maxPop = maxPop.max(popGroup);
                    ageGroup = ageGroup + ageInterval;
                    popGroup = new BigDecimal(pop.toString());
                } else {
                    popGroup = popGroup.add(pop);
                }
            }
        }
        result[0] = fapc;
        result[1] = mapc;
        result[2] = maxPop;
        return result;
    }

    private static Object[] getDefaultData(
            int femalePopAge0,
            int malePopAge0) {
        Object[] result = new Object[2];
        TreeMap<Long, BigDecimal> femaleAgeInYearsPopulationCount_TreeMap = new TreeMap<>();
        TreeMap<Long, BigDecimal> maleAgeInYearsPopulationCount_TreeMap = new TreeMap<>();
        BigDecimal population_BigDecimal;
        BigDecimal change_BigDecimal;
        long age;
        population_BigDecimal = new BigDecimal("" + femalePopAge0);
        change_BigDecimal = new BigDecimal("0.94");
        for (age = 0; age < 5; age++) {
            femaleAgeInYearsPopulationCount_TreeMap.put(
                    age, population_BigDecimal);
            population_BigDecimal = population_BigDecimal.multiply(change_BigDecimal);
        }
        change_BigDecimal = new BigDecimal("0.95");
        for (age = 5; age < 10; age++) {
            femaleAgeInYearsPopulationCount_TreeMap.put(
                    age, population_BigDecimal);
            population_BigDecimal = population_BigDecimal.multiply(change_BigDecimal);
        }
        change_BigDecimal = new BigDecimal("0.96");
        for (age = 10; age < 15; age++) {
            femaleAgeInYearsPopulationCount_TreeMap.put(
                    age, population_BigDecimal);
            population_BigDecimal = population_BigDecimal.multiply(change_BigDecimal);
        }
        change_BigDecimal = new BigDecimal("0.97");
        for (age = 15; age < 20; age++) {
            femaleAgeInYearsPopulationCount_TreeMap.put(
                    age, population_BigDecimal);
            population_BigDecimal = population_BigDecimal.multiply(change_BigDecimal);
        }
        change_BigDecimal = new BigDecimal("0.99");
        for (age = 20; age < 60; age++) {
            femaleAgeInYearsPopulationCount_TreeMap.put(
                    age, population_BigDecimal);
            population_BigDecimal = population_BigDecimal.multiply(change_BigDecimal);
        }
        change_BigDecimal = new BigDecimal("0.97");
        for (age = 60; age < 80; age++) {
            femaleAgeInYearsPopulationCount_TreeMap.put(
                    age, population_BigDecimal);
            population_BigDecimal = population_BigDecimal.multiply(change_BigDecimal);
        }
        change_BigDecimal = new BigDecimal("0.75");
        for (age = 80; age < 100; age++) {
            femaleAgeInYearsPopulationCount_TreeMap.put(
                    age, population_BigDecimal);
            population_BigDecimal = population_BigDecimal.multiply(change_BigDecimal);
        }
        population_BigDecimal = new BigDecimal("" + malePopAge0);
        change_BigDecimal = new BigDecimal("0.93");
        for (age = 0; age < 5; age++) {
            maleAgeInYearsPopulationCount_TreeMap.put(
                    age, population_BigDecimal);
            population_BigDecimal = population_BigDecimal.multiply(change_BigDecimal);
        }
        change_BigDecimal = new BigDecimal("0.94");
        for (age = 5; age < 10; age++) {
            maleAgeInYearsPopulationCount_TreeMap.put(
                    age, population_BigDecimal);
            population_BigDecimal = population_BigDecimal.multiply(change_BigDecimal);
        }
        change_BigDecimal = new BigDecimal("0.95");
        for (age = 10; age < 15; age++) {
            maleAgeInYearsPopulationCount_TreeMap.put(
                    age, population_BigDecimal);
            population_BigDecimal = population_BigDecimal.multiply(change_BigDecimal);
        }
        change_BigDecimal = new BigDecimal("0.96");
        for (age = 15; age < 20; age++) {
            maleAgeInYearsPopulationCount_TreeMap.put(
                    age, population_BigDecimal);
            population_BigDecimal = population_BigDecimal.multiply(change_BigDecimal);
        }
        change_BigDecimal = new BigDecimal("0.98");
        for (age = 20; age < 60; age++) {
            maleAgeInYearsPopulationCount_TreeMap.put(
                    age, population_BigDecimal);
            population_BigDecimal = population_BigDecimal.multiply(change_BigDecimal);
        }
        change_BigDecimal = new BigDecimal("0.7");
        for (age = 60; age < 70; age++) {
            maleAgeInYearsPopulationCount_TreeMap.put(
                    age, population_BigDecimal);
            population_BigDecimal = population_BigDecimal.multiply(change_BigDecimal);
        }
        change_BigDecimal = new BigDecimal("0.5");
        for (age = 70; age < 100; age++) {
            maleAgeInYearsPopulationCount_TreeMap.put(
                    age, population_BigDecimal);
            population_BigDecimal = population_BigDecimal.multiply(change_BigDecimal);
        }
        //Integer maxAge = 99;
        //BigDecimal maxCount = new BigDecimal("" + Math.max(femalePopAge0, malePopAge0));
        result[0] = femaleAgeInYearsPopulationCount_TreeMap;
        result[1] = maleAgeInYearsPopulationCount_TreeMap;
        //result[2] = maxAge;
        //result[3] = maxCount;
        return result;
    }

    @Override
    public void drawTitle(String title) {
        super.drawTitle(title);
        int barHeight = Math_BigDecimal.divideRoundIfNecessary(
                BigDecimal.valueOf(getAgeInterval()), getCellHeight(),
                0, getRoundingMode()).intValue();
        extraHeightTop += barHeight;
    }
}
