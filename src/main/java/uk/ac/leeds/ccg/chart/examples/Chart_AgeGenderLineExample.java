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
import java.awt.geom.Line2D;
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
import uk.ac.leeds.ccg.chart.core.Chart_AgeGender;
import uk.ac.leeds.ccg.chart.data.Chart_AgeGenderData;
import uk.ac.leeds.ccg.chart.data.Chart_AgeGenderLineData;
import uk.ac.leeds.ccg.chart.data.Chart_Data;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.stats.summary.Stats_BigDecimal1;

/**
 * An example of generating an Age by Gender Population Line Chart
 * Visualization.
 */
public class Chart_AgeGenderLineExample extends Chart_AgeGender {

    public Chart_AgeGenderLineExample(Generic_Environment e) {
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
    public Chart_AgeGenderLineExample(Generic_Environment e, ExecutorService es,
            Path f, String format, String title, int dataWidth, int dataHeight,
            String xAxisLabel, String yAxisLabel, boolean drawOriginLinesOnPlot,
            int ageInterval, int startAgeOfEndYearInterval, int dpc, int dpd,
            RoundingMode rm) {
        super(e);
        init(es, f, format, title, dataWidth, dataHeight, xAxisLabel,
                yAxisLabel, drawOriginLinesOnPlot, ageInterval,
                startAgeOfEndYearInterval, dpc, dpd, rm);
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
                title = "Age Gender Population Line Chart";
                System.out.println("Use default title: " + title);
                file = Paths.get(System.getProperty("user.dir"),
                        title.replace(" ", "_") + "." + format);
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
            int ageInterval = 1;
            int startAgeOfEndYearInterval = 90;//95;
            int dpc = 10;
            int dpd = 3;
            RoundingMode rm = RoundingMode.HALF_UP;
            ExecutorService es = Executors.newSingleThreadExecutor();
            Chart_AgeGenderLineExample plot = new Chart_AgeGenderLineExample(e, es, file,
                    format, title, dataWidth, dataHeight, xAxisLabel,
                    yAxisLabel, drawOriginLinesOnPlot, ageInterval,
                    startAgeOfEndYearInterval, dpc, dpd, rm);
            plot.setData(plot.getDefaultData());
            plot.vis.getHeadlessEnvironment();
            plot.run();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);

        }
    }

    @Override
    public void drawData() {
        //drawLineChartUsingMeanAndStandardDeviation();
        drawLineChartUsingMinQ1MedianQ3Max();
    }

    public void drawLineChartUsingMeanAndStandardDeviation() {
        int ageInterval = getAgeInterval();
        Line2D abLine2D;
        Chart_AgeGenderLineData d = (Chart_AgeGenderLineData) getData();
        boolean firstPoint = true;
        /*
         * Draw Female Lines
         */
        int lastMeanPointCol = 0;
        int lastMeanAddStdDevPointCol = 0;
        int lastMeanSubtractStdDevPointCol = 0;
        int lastPointRow = 0;
        Iterator<Map.Entry<Integer, Stats_BigDecimal1>> ite;
        ite = d.fData.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<Integer, Stats_BigDecimal1> entry = ite.next();
            Integer age = entry.getKey();
            Stats_BigDecimal1 stats = entry.getValue();
            BigRational stdDev = BigRational.valueOf(stats.getMoments().getStandardDeviation(0).toBigDecimal(0)); // not sure if 0 is sensible for oom here!
            BigRational meanAddStdDev = stats.getMean().add(stdDev);
            BigRational meanSubtractStdDev = stats.getMean().subtract(stdDev);
            int meanPointCol = coordinateToScreenCol(stats.getMean());
            int pointRow = coordinateToScreenRow(BigRational.valueOf(age - ageInterval / 2));
            int meanAddStdDevPointCol = coordinateToScreenCol(meanAddStdDev);
            int meanSubtractStdDevPointCol = coordinateToScreenCol(meanSubtractStdDev);
            if (firstPoint) {
                lastMeanPointCol = meanPointCol;
                lastMeanAddStdDevPointCol = meanAddStdDevPointCol;
                lastMeanSubtractStdDevPointCol = meanSubtractStdDevPointCol;
                lastPointRow = pointRow;
                firstPoint = false;
            }
            // Draw median add StdDev line
            abLine2D = new Line2D.Double(
                    lastMeanAddStdDevPointCol,
                    lastPointRow,
                    meanAddStdDevPointCol,
                    pointRow);
            setPaint(Color.LIGHT_GRAY);
            draw(abLine2D);
            // Draw median subtract StdDev line
            abLine2D = new Line2D.Double(
                    lastMeanSubtractStdDevPointCol,
                    lastPointRow,
                    meanSubtractStdDevPointCol,
                    pointRow);
            setPaint(Color.LIGHT_GRAY);
            draw(abLine2D);
            // Draw median line
            abLine2D = new Line2D.Double(
                    lastMeanPointCol, lastPointRow, meanPointCol, pointRow);
            setPaint(Color.DARK_GRAY);
            draw(abLine2D);
            // Set parameters
            lastMeanPointCol = meanPointCol;
            lastMeanAddStdDevPointCol = meanAddStdDevPointCol;
            lastMeanSubtractStdDevPointCol = meanSubtractStdDevPointCol;
            lastPointRow = pointRow;
        }
        /*
         * Draw Male Lines
         */
        firstPoint = true;
        ite = d.mData.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<Integer, Stats_BigDecimal1> entry = ite.next();
            Integer age = entry.getKey();
            Stats_BigDecimal1 stats = entry.getValue();
            BigRational stdDev = BigRational.valueOf(stats.getMoments().getStandardDeviation(0).toBigDecimal(0)); // not sure if 0 is sensible for oom here!
            BigRational meanAddStdDev = stats.getMean().add(stdDev);
            BigRational meanSubtractStdDev = stats.getMean().subtract(stdDev);
            int meanPointCol = coordinateToScreenCol(stats.getMean().negate());
            int pointRow = coordinateToScreenRow(BigRational.valueOf(age - ageInterval / 2));
            int meanAddStdDevPointCol = coordinateToScreenCol(meanAddStdDev.negate());
            int meanSubtractStdDevPointCol = coordinateToScreenCol(meanSubtractStdDev.negate());
            if (firstPoint) {
                lastMeanPointCol = meanPointCol;
                lastMeanAddStdDevPointCol = meanAddStdDevPointCol;
                lastMeanSubtractStdDevPointCol = meanSubtractStdDevPointCol;
                lastPointRow = pointRow;
                firstPoint = false;
            }
            // Draw median add StdDev line
            abLine2D = new Line2D.Double(
                    lastMeanAddStdDevPointCol,
                    lastPointRow,
                    meanAddStdDevPointCol,
                    pointRow);
            setPaint(Color.LIGHT_GRAY);
            draw(abLine2D);
            // Draw median subtract StdDev line
            abLine2D = new Line2D.Double(
                    lastMeanSubtractStdDevPointCol,
                    lastPointRow,
                    meanSubtractStdDevPointCol,
                    pointRow);
            setPaint(Color.LIGHT_GRAY);
            draw(abLine2D);
            // Draw median line
            abLine2D = new Line2D.Double(
                    lastMeanPointCol, lastPointRow, meanPointCol, pointRow);
            setPaint(Color.DARK_GRAY);
            draw(abLine2D);
            // Set parameters
            lastMeanPointCol = meanPointCol;
            lastMeanAddStdDevPointCol = meanAddStdDevPointCol;
            lastMeanSubtractStdDevPointCol = meanSubtractStdDevPointCol;
            lastPointRow = pointRow;
        }
    }

    public void drawLineChartUsingMinQ1MedianQ3Max() {
        int ageInterval = getAgeInterval();
        Line2D abLine2D;
        Chart_AgeGenderLineData d = (Chart_AgeGenderLineData) getData();
        boolean firstPoint = true;
        /*
         * Draw Female Lines
         */
        int last_minPointCol = 0;
        int last_q1PointCol = 0;
        int last_medianPointCol = 0;
        int last_q3PointCol = 0;
        int last_maxPointCol = 0;
        int lastPointRow = 0;
        Iterator<Map.Entry<Integer, Stats_BigDecimal1>> ite;
        ite = d.fData.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<Integer, Stats_BigDecimal1> entry = ite.next();
            Integer age = entry.getKey();
            Stats_BigDecimal1 stats = entry.getValue();
            int minPointCol = coordinateToScreenCol(BigRational.valueOf(stats.getMin()));
            int q1PointCol = coordinateToScreenCol(BigRational.valueOf(stats.getQ1()));
            int medianPointCol = coordinateToScreenCol(stats.getMedian());
            int q3PointCol = coordinateToScreenCol(BigRational.valueOf(stats.getQ3()));
            int maxPointCol = coordinateToScreenCol(BigRational.valueOf(stats.getMax()));
            int pointRow = coordinateToScreenRow(BigRational.valueOf(age - ageInterval / 2));
            if (firstPoint) {
                last_minPointCol = minPointCol;
                last_q1PointCol = q1PointCol;
                last_medianPointCol = medianPointCol;
                last_q3PointCol = q3PointCol;
                last_maxPointCol = maxPointCol;
                lastPointRow = pointRow;
                firstPoint = false;
            }
            // Draw min line
            abLine2D = new Line2D.Double(
                    last_minPointCol,
                    lastPointRow,
                    minPointCol,
                    pointRow);
            setPaint(Color.LIGHT_GRAY);
            draw(abLine2D);
            // Draw max line
            abLine2D = new Line2D.Double(
                    last_maxPointCol,
                    lastPointRow,
                    maxPointCol,
                    pointRow);
            setPaint(Color.LIGHT_GRAY);
            draw(abLine2D);
            // Draw q1 line
            abLine2D = new Line2D.Double(
                    last_q1PointCol,
                    lastPointRow,
                    q1PointCol,
                    pointRow);
            setPaint(Color.GRAY);
            draw(abLine2D);
            // Draw q3 line
            abLine2D = new Line2D.Double(
                    last_q3PointCol,
                    lastPointRow,
                    q3PointCol,
                    pointRow);
            setPaint(Color.GRAY);
            draw(abLine2D);
            // Draw median line
            abLine2D = new Line2D.Double(
                    last_medianPointCol,
                    lastPointRow,
                    medianPointCol,
                    pointRow);
            setPaint(Color.DARK_GRAY);
            draw(abLine2D);
            // Set parameters
            last_minPointCol = minPointCol;
            last_q1PointCol = q1PointCol;
            last_medianPointCol = medianPointCol;
            last_q3PointCol = q3PointCol;
            last_maxPointCol = maxPointCol;
            lastPointRow = pointRow;
        }
        /*
         * Draw Male Lines
         */
        firstPoint = true;
        ite = d.mData.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<Integer, Stats_BigDecimal1> entry = ite.next();
            Integer age = entry.getKey();
            Stats_BigDecimal1 stats = entry.getValue();
            int minPointCol = coordinateToScreenCol(BigRational.valueOf(stats.getMin().negate()));
            int q1PointCol = coordinateToScreenCol(BigRational.valueOf(stats.getQ1().negate()));
            int medianPointCol = coordinateToScreenCol(stats.getMedian().negate());
            int q3PointCol = coordinateToScreenCol(BigRational.valueOf(stats.getQ3().negate()));
            int maxPointCol = coordinateToScreenCol(BigRational.valueOf(stats.getMax().negate()));
            int pointRow = coordinateToScreenRow(BigRational.valueOf(age - ageInterval / 2));
            if (firstPoint) {
                last_minPointCol = minPointCol;
                last_q1PointCol = q1PointCol;
                last_medianPointCol = medianPointCol;
                last_q3PointCol = q3PointCol;
                last_maxPointCol = maxPointCol;
                lastPointRow = pointRow;
                firstPoint = false;
            }
            // Draw min line
            abLine2D = new Line2D.Double(
                    last_minPointCol,
                    lastPointRow,
                    minPointCol,
                    pointRow);
            setPaint(Color.LIGHT_GRAY);
            draw(abLine2D);
            // Draw max line
            abLine2D = new Line2D.Double(
                    last_maxPointCol,
                    lastPointRow,
                    maxPointCol,
                    pointRow);
            setPaint(Color.LIGHT_GRAY);
            draw(abLine2D);
            // Draw q1 line
            abLine2D = new Line2D.Double(
                    last_q1PointCol,
                    lastPointRow,
                    q1PointCol,
                    pointRow);
            setPaint(Color.GRAY);
            draw(abLine2D);
            // Draw q3 line
            abLine2D = new Line2D.Double(
                    last_q3PointCol,
                    lastPointRow,
                    q3PointCol,
                    pointRow);
            setPaint(Color.GRAY);
            draw(abLine2D);
            // Draw median line
            abLine2D = new Line2D.Double(
                    last_medianPointCol,
                    lastPointRow,
                    medianPointCol,
                    pointRow);
            setPaint(Color.DARK_GRAY);
            draw(abLine2D);
            // Set parameters
            last_minPointCol = minPointCol;
            last_q1PointCol = q1PointCol;
            last_medianPointCol = medianPointCol;
            last_q3PointCol = q3PointCol;
            last_maxPointCol = maxPointCol;
            lastPointRow = pointRow;
        }
    }

    /**
     * Override this method to use other data
     *
     * @param femalePopAge0
     * @param malePopAge0
     * @return
     */
    private static Object[] getPopulationData(
            int femalePopAge0,
            int malePopAge0) {
        Object[] result = new Object[2];
        TreeMap<Integer, BigDecimal> femaleAgeInYearsPopulationCount_TreeMap = new TreeMap<>();
        TreeMap<Integer, BigDecimal> maleAgeInYearsPopulationCount_TreeMap = new TreeMap<>();
        BigDecimal population_BigDecimal;
        BigDecimal change_BigDecimal;
        int age;
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
        Integer maxAge = 99;
        BigDecimal maxCount = new BigDecimal("" + Math.max(femalePopAge0, malePopAge0));
        result[0] = femaleAgeInYearsPopulationCount_TreeMap;
        result[1] = maleAgeInYearsPopulationCount_TreeMap;
        //result[2] = maxAge;
        //result[3] = maxCount;
        return result;
    }

    /**
     * @return default data for this type of chart. 
     */
    public Chart_AgeGenderLineData getDefaultData() {
        int ageInterval = 1;
        int startAgeOfEndYearInterval = 90;//95;
        int decimalPlacePrecisionForCalculations = 10;
//        int ageInterval = getAgeInterval();
//        int startAgeOfEndYearInterval = getStartAgeOfEndYearInterval();
//        int decimalPlacePrecisionForCalculations = getDecimalPlacePrecisionForCalculations();
        RoundingMode roundingMode = getRoundingMode();
        return getDefaultData(ageInterval, startAgeOfEndYearInterval,
                decimalPlacePrecisionForCalculations, roundingMode);
    }

    /**
     * Returns a sample data set.
     *
     * @param ageInterval Age interval.
     * @param rm RoundingMode
     * @param saeyi startAgeOfEndYearInterval
     * @param dp The decimal places.
     * @return The data set.
     */
    public static Chart_AgeGenderLineData getDefaultData(int ageInterval, int saeyi, int dp,
            RoundingMode rm) {
        Chart_AgeGenderLineData r = new Chart_AgeGenderLineData();
        TreeMap<Integer, Stats_BigDecimal1> fss = new TreeMap<>();
        TreeMap<Integer, Stats_BigDecimal1> mss = new TreeMap<>();
        Object[] data10000 = getPopulationData(10000, 10000);
        TreeMap<Integer, BigDecimal> female10000 = (TreeMap<Integer, BigDecimal>) data10000[0];
        TreeMap<Integer, BigDecimal> male10000 = (TreeMap<Integer, BigDecimal>) data10000[1];
        Object[] data9000 = getPopulationData(9000, 9000);
        TreeMap<Integer, BigDecimal> female9000 = (TreeMap<Integer, BigDecimal>) data9000[0];
        TreeMap<Integer, BigDecimal> male9000 = (TreeMap<Integer, BigDecimal>) data9000[1];
        Object[] data9900 = getPopulationData(9900, 9900);
        TreeMap<Integer, BigDecimal> female9900 = (TreeMap<Integer, BigDecimal>) data9900[0];
        TreeMap<Integer, BigDecimal> male9900 = (TreeMap<Integer, BigDecimal>) data9900[1];
        Object[] data9950 = getPopulationData(9950, 9950);
        TreeMap<Integer, BigDecimal> female9950 = (TreeMap<Integer, BigDecimal>) data9950[0];
        TreeMap<Integer, BigDecimal> male9950 = (TreeMap<Integer, BigDecimal>) data9950[1];
        Object[] data9800 = getPopulationData(9800, 9800);
        TreeMap<Integer, BigDecimal> female9800 = (TreeMap<Integer, BigDecimal>) data9800[0];
        TreeMap<Integer, BigDecimal> male9800 = (TreeMap<Integer, BigDecimal>) data9800[1];
        Iterator<Integer> iterator;
        Integer age = 0;
        BigDecimal pop10000;
        BigDecimal pop9000;
        BigDecimal pop9900;
        BigDecimal pop9950;
        BigDecimal pop9800;
        r.max = BigRational.ZERO;
        iterator = ((TreeMap<Integer, BigDecimal>) data10000[0]).keySet().iterator();
        ArrayList<BigDecimal> values = null;
        while (iterator.hasNext()) {
            pop10000 = BigDecimal.ZERO;
            pop9000 = BigDecimal.ZERO;
            pop9900 = BigDecimal.ZERO;
            pop9950 = BigDecimal.ZERO;
            pop9800 = BigDecimal.ZERO;
            for (int interval = 0; interval < ageInterval; interval++) {
                if (iterator.hasNext()) {
                    age = iterator.next();
                    if (age >= saeyi) {
                        pop10000 = values.get(0).add(female10000.get(age));
                        pop9000 = values.get(1).add(female9000.get(age));
                        pop9900 = values.get(2).add(female9900.get(age));
                        pop9950 = values.get(3).add(female9950.get(age));
                        pop9800 = values.get(4).add(female9800.get(age));
                    } else {
                        pop10000 = pop10000.add(female10000.get(age));
                        pop9000 = pop9000.add(female9000.get(age));
                        pop9900 = pop9900.add(female9900.get(age));
                        pop9950 = pop9950.add(female9950.get(age));
                        pop9800 = pop9800.add(female9800.get(age));
                    }
                }
            }
            if (age < saeyi) {
                values = new ArrayList<>();
            }
            values.add(pop10000);
            values.add(pop9000);
            values.add(pop9900);
            values.add(pop9950);
            values.add(pop9800);
            Stats_BigDecimal1 ss = new Stats_BigDecimal1(values);
            System.out.println("Female age " + age);
            // Set r.max to be the maximum of the median added to the standard 
            // deviation
            r.max = BigRational.max(r.max, BigRational.valueOf(ss.getMax()).add(ss.getMedian()));
            if (age < saeyi) {
                fss.put(age, ss);
            } else {
                fss.put(saeyi + ageInterval, ss);
            }
        }
        //maxX = r.max;
        //r.max = BigDecimal.ZERO;
        iterator = ((TreeMap<Integer, BigDecimal>) data10000[1]).keySet().iterator();
        while (iterator.hasNext()) {
            pop10000 = BigDecimal.ZERO;
            pop9000 = BigDecimal.ZERO;
            pop9900 = BigDecimal.ZERO;
            pop9950 = BigDecimal.ZERO;
            pop9800 = BigDecimal.ZERO;
            for (int interval = 0; interval < ageInterval; interval++) {
                if (iterator.hasNext()) {
                    age = iterator.next();
                    if (age >= saeyi) {
                        pop10000 = values.get(0).add(male10000.get(age));
                        pop9000 = values.get(1).add(male9000.get(age));
                        pop9900 = values.get(2).add(male9900.get(age));
                        pop9950 = values.get(3).add(male9950.get(age));
                        pop9800 = values.get(4).add(male9800.get(age));
                    } else {
                        pop10000 = pop10000.add(male10000.get(age));
                        pop9000 = pop9000.add(male9000.get(age));
                        pop9900 = pop9900.add(male9900.get(age));
                        pop9950 = pop9950.add(male9950.get(age));
                        pop9800 = pop9800.add(male9800.get(age));
                    }
                }
            }
            if (age < saeyi) {
                values = new ArrayList<>();
            }
            values.add(pop10000);
            values.add(pop9000);
            values.add(pop9900);
            values.add(pop9950);
            values.add(pop9800);
            System.out.println("Male age " + age);
            Stats_BigDecimal1 ss = new Stats_BigDecimal1(values);
            // Set r.max to be the maximum of the median added to the standard 
            // deviation
            r.max = BigRational.max(r.max, BigRational.valueOf(ss.getMax()).add(ss.getMedian()));
            if (age < saeyi) {
                mss.put(age, ss);
            } else {
                mss.put(saeyi + ageInterval, ss);
            }
        }
//        r[0] = fss;
//        r[1] = mss;
//        //minX = r.max.negate();
//        r[2] = r.max;
//        r[3] = BigDecimal.valueOf(100);
//        r[4] = BigDecimal.ZERO;
        return r;
    }

    /**
     * Returns a sample data set.
     *
     * @param dpc decimalPlacePrecisionForCalculations
     * @param rm roundingMode
     * @return The data set.
     */
    public static Object[] getData(int dpc, RoundingMode rm) {
        Object[] result = new Object[2];
        TreeMap<Integer, Stats_BigDecimal1> femaleBoxPlotStatistics = new TreeMap<>();
        TreeMap<Integer, Stats_BigDecimal1> maleBoxPlotStatistics = new TreeMap<>();
        Object[] data10000 = getPopulationData(10000, 10000);
        TreeMap<Integer, BigDecimal> female10000 = (TreeMap<Integer, BigDecimal>) data10000[0];
        TreeMap<Integer, BigDecimal> male10000 = (TreeMap<Integer, BigDecimal>) data10000[1];
        Object[] data9000 = getPopulationData(9000, 9000);
        TreeMap<Integer, BigDecimal> female9000 = (TreeMap<Integer, BigDecimal>) data9000[0];
        TreeMap<Integer, BigDecimal> male9000 = (TreeMap<Integer, BigDecimal>) data9000[1];
        Object[] data9900 = getPopulationData(9900, 9900);
        TreeMap<Integer, BigDecimal> female9900 = (TreeMap<Integer, BigDecimal>) data9900[0];
        TreeMap<Integer, BigDecimal> male9900 = (TreeMap<Integer, BigDecimal>) data9900[1];
        Object[] data9950 = getPopulationData(9950, 9950);
        TreeMap<Integer, BigDecimal> female9950 = (TreeMap<Integer, BigDecimal>) data9950[0];
        TreeMap<Integer, BigDecimal> male9950 = (TreeMap<Integer, BigDecimal>) data9950[1];
        Object[] data9800 = getPopulationData(9800, 9800);
        TreeMap<Integer, BigDecimal> female9800 = (TreeMap<Integer, BigDecimal>) data9800[0];
        TreeMap<Integer, BigDecimal> male9800 = (TreeMap<Integer, BigDecimal>) data9800[1];
        Iterator<Integer> iterator;
        Integer age;
        BigDecimal pop;
        BigDecimal maxValue;
        maxValue = BigDecimal.ZERO;
        iterator = ((TreeMap<Integer, BigDecimal>) data10000[0]).keySet().iterator();
        while (iterator.hasNext()) {
            age = iterator.next();
            pop = female10000.get(age);
            ArrayList<BigDecimal> values = new ArrayList<>();
            maxValue = maxValue.max(pop);
            values.add(pop);
            pop = female9000.get(age);
            maxValue = maxValue.max(pop);
            values.add(pop);
            pop = female9900.get(age);
            maxValue = maxValue.max(pop);
            values.add(pop);
            pop = female9950.get(age);
            maxValue = maxValue.max(pop);
            values.add(pop);
            pop = female9800.get(age);
            maxValue = maxValue.max(pop);
            values.add(pop);
            femaleBoxPlotStatistics.put(age, new Stats_BigDecimal1(values));
        }
        maxValue = BigDecimal.ZERO;
        iterator = ((TreeMap<Integer, BigDecimal>) data10000[0]).keySet().iterator();
        while (iterator.hasNext()) {
            age = iterator.next();
            pop = male10000.get(age);
            ArrayList<BigDecimal> values = new ArrayList<>();
            maxValue = maxValue.max(pop);
            values.add(pop);
            pop = male9000.get(age);
            maxValue = maxValue.max(pop);
            values.add(pop);
            pop = male9900.get(age);
            maxValue = maxValue.max(pop);
            values.add(pop);
            pop = male9950.get(age);
            maxValue = maxValue.max(pop);
            values.add(pop);
            pop = male9800.get(age);
            maxValue = maxValue.max(pop);
            values.add(pop);
            maleBoxPlotStatistics.put(age, new Stats_BigDecimal1(values));
        }
        result[0] = femaleBoxPlotStatistics;
        result[1] = maleBoxPlotStatistics;
        return result;
    }

    @Override
    public Chart_Data getData() {
        return (Chart_AgeGenderData) data;
    }
}
