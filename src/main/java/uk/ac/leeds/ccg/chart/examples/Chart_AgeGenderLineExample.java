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
import java.math.BigInteger;
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
import uk.ac.leeds.ccg.stats.summary.Stats_BigRational;
import uk.ac.leeds.ccg.stats.summary.Stats_BigRational1;

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
     * @param oom The Order of Magnitude for rounding precision.
     * @param rm The RoundingMode.
     */
    public Chart_AgeGenderLineExample(Generic_Environment e, ExecutorService es,
            Path f, String format, String title, int dataWidth, int dataHeight,
            String xAxisLabel, String yAxisLabel, boolean drawOriginLinesOnPlot,
            int ageInterval, int startAgeOfEndYearInterval, int oomx, int oomy,
            RoundingMode rm) {
        super(e);
        init(es, f, format, title, dataWidth, dataHeight, xAxisLabel,
                yAxisLabel, drawOriginLinesOnPlot, ageInterval,
                startAgeOfEndYearInterval, oomx, oomy, rm);
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
                        "data", "output",
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
            int oomx = -2;
            int oomy = -1;
            RoundingMode rm = RoundingMode.HALF_UP;
            ExecutorService es = Executors.newSingleThreadExecutor();
            Chart_AgeGenderLineExample plot = new Chart_AgeGenderLineExample(e, es, file,
                    format, title, dataWidth, dataHeight, xAxisLabel,
                    yAxisLabel, drawOriginLinesOnPlot, ageInterval,
                    startAgeOfEndYearInterval, oomx, oomy, rm);
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
        int ageInterval = getAgeInterval().intValue();
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
        Iterator<Map.Entry<Integer, Stats_BigRational1>> ite;
        ite = d.fData.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<Integer, Stats_BigRational1> entry = ite.next();
            Integer age = entry.getKey();
            Stats_BigRational stats = entry.getValue();
            BigRational stdDev = BigRational.valueOf(stats.getMoments().getStandardDeviation(0, rm).toBigDecimal(0, rm)); // not sure if 0 is sensible for oom here!
            BigRational meanAddStdDev = stats.getMean().add(stdDev);
            BigRational meanSubtractStdDev = stats.getMean().subtract(stdDev);
            int meanPointCol = getCol(stats.getMean());
            int pointRow = getRow(BigRational.valueOf(age - ageInterval / 2));
            int meanAddStdDevPointCol = getCol(meanAddStdDev);
            int meanSubtractStdDevPointCol = getCol(meanSubtractStdDev);
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
            Map.Entry<Integer, Stats_BigRational1> entry = ite.next();
            Integer age = entry.getKey();
            Stats_BigRational stats = entry.getValue();
            BigRational stdDev = BigRational.valueOf(stats.getMoments().getStandardDeviation(0, rm).toBigDecimal(0, rm)); // not sure if 0 is sensible for oom here!
            BigRational meanAddStdDev = stats.getMean().add(stdDev);
            BigRational meanSubtractStdDev = stats.getMean().subtract(stdDev);
            int meanPointCol = getCol(stats.getMean().negate());
            int pointRow = getRow(BigRational.valueOf(age - ageInterval / 2));
            int meanAddStdDevPointCol = getCol(meanAddStdDev.negate());
            int meanSubtractStdDevPointCol = getCol(meanSubtractStdDev.negate());
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
        int ageInterval = getAgeInterval().intValue();
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
        Iterator<Map.Entry<Integer, Stats_BigRational1>> ite;
        ite = d.fData.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<Integer, Stats_BigRational1> entry = ite.next();
            Integer age = entry.getKey();
            Stats_BigRational1 stats = entry.getValue();
            int minPointCol = getCol(stats.getMin());
            int q1PointCol = getCol(stats.getQ1());
            int medianPointCol = getCol(stats.getMedian());
            int q3PointCol = getCol(stats.getQ3());
            int maxPointCol = getCol(stats.getMax());
            int pointRow = getRow(BigRational.valueOf(age - ageInterval / 2));
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
            Map.Entry<Integer, Stats_BigRational1> entry = ite.next();
            Integer age = entry.getKey();
            Stats_BigRational1 stats = entry.getValue();
            int minPointCol = getCol(stats.getMin().negate());
            int q1PointCol = getCol(stats.getQ1().negate());
            int medianPointCol = getCol(stats.getMedian().negate());
            int q3PointCol = getCol(stats.getQ3().negate());
            int maxPointCol = getCol(stats.getMax().negate());
            int pointRow = getRow(BigRational.valueOf(age - ageInterval / 2));
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
     * The survival rates for females are slightly higher than for males.
     * Survival probabilities are age dependent and are hard coded.
     *
     * @param fp0 Initial female population with Age in Years = 0.
     * @param mp0 Initial male population with Age in Years = 0.
     * @return AgeGenderPopulation
     */
    private AgeGenderPopulation getPopulationData(long fp0, long mp0) {
        AgeGenderPopulation agp = new AgeGenderPopulation();
        BigInteger pop;
        // Annual Survival Probability
        BigRational asp;
        int age;
        pop = BigInteger.valueOf(fp0);
        asp = BigRational.valueOf("0.94");
        for (age = 0; age < 5; age++) {
            agp.fp.put(age, pop);
            pop = asp.multiply(pop).toBigDecimal().toBigInteger();
        }
        asp = BigRational.valueOf("0.95");
        for (age = 5; age < 10; age++) {
            agp.fp.put(age, pop);
            pop = asp.multiply(pop).toBigDecimal().toBigInteger();
        }
        asp = BigRational.valueOf("0.96");
        for (age = 10; age < 15; age++) {
            agp.fp.put(age, pop);
            pop = asp.multiply(pop).toBigDecimal().toBigInteger();
        }
        asp = BigRational.valueOf("0.97");
        for (age = 15; age < 20; age++) {
            agp.fp.put(age, pop);
            pop = asp.multiply(pop).toBigDecimal().toBigInteger();
        }
        asp = BigRational.valueOf("0.99");
        for (age = 20; age < 60; age++) {
            agp.fp.put(age, pop);
            pop = asp.multiply(pop).toBigDecimal().toBigInteger();
        }
        asp = BigRational.valueOf("0.97");
        for (age = 60; age < 80; age++) {
            agp.fp.put(age, pop);
            pop = asp.multiply(pop).toBigDecimal().toBigInteger();
        }
        asp = BigRational.valueOf("0.75");
        for (age = 80; age < 100; age++) {
            agp.fp.put(age, pop);
            pop = asp.multiply(pop).toBigDecimal().toBigInteger();
        }
        pop = BigInteger.valueOf(mp0);
        asp = BigRational.valueOf("0.93");
        for (age = 0; age < 5; age++) {
            agp.mp.put(age, pop);
            pop = asp.multiply(pop).toBigDecimal().toBigInteger();
        }
        asp = BigRational.valueOf("0.94");
        for (age = 5; age < 10; age++) {
            agp.mp.put(age, pop);
            pop = asp.multiply(pop).toBigDecimal().toBigInteger();
        }
        asp = BigRational.valueOf("0.95");
        for (age = 10; age < 15; age++) {
            agp.mp.put(age, pop);
            pop = asp.multiply(pop).toBigDecimal().toBigInteger();
        }
        asp = BigRational.valueOf("0.96");
        for (age = 15; age < 20; age++) {
            agp.mp.put(age, pop);
            pop = asp.multiply(pop).toBigDecimal().toBigInteger();
        }
        asp = BigRational.valueOf("0.98");
        for (age = 20; age < 60; age++) {
            agp.mp.put(age, pop);
            pop = asp.multiply(pop).toBigDecimal().toBigInteger();
        }
        asp = BigRational.valueOf("0.7");
        for (age = 60; age < 70; age++) {
            agp.mp.put(age, pop);
            pop = asp.multiply(pop).toBigDecimal().toBigInteger();
        }
        asp = BigRational.valueOf("0.5");
        for (age = 70; age < 100; age++) {
            agp.mp.put(age, pop);
            pop = asp.multiply(pop).toBigDecimal().toBigInteger();
        }
        //Integer maxAge = 99;
        //BigRational maxCount = BigRational.valueOf("" + Math.max(fp0, mp0));
        //result[2] = maxAge;
        //result[3] = maxCount;
        return agp;
    }

    /**
     * POJO for Age Gender Population Data
     */
    public class AgeGenderPopulation {

        /**
         * Female Population: keys are ages in years, values are population
         * counts.
         */
        public TreeMap<Integer, BigInteger> fp;

        /**
         * Male Population: keys are ages in years, values are population
         * counts.
         */
        public TreeMap<Integer, BigInteger> mp;

        public AgeGenderPopulation() {
            fp = new TreeMap<>();
            mp = new TreeMap<>();
        }
    }

    /**
     * @return default data for this type of chart.
     */
    public Chart_AgeGenderLineData getDefaultData() {
        int ageInterval = 1;
        int startAgeOfEndYearInterval = 90;//95;
        int oom = 10;
//        int ageInterval = getAgeInterval();
//        int startAgeOfEndYearInterval = getStartAgeOfEndYearInterval();
//        int decimalPlacePrecisionForCalculations = getDecimalPlacePrecisionForCalculations();
        RoundingMode rm = getRoundingMode();
        return getDefaultData(ageInterval, startAgeOfEndYearInterval,
                oom, rm);
    }

    /**
     * Returns a sample data set.
     *
     * @param ageInterval Age interval.
     * @param saeyi startAgeOfEndYearInterval
     * @param oom The Order of Magnitude for precision.
     * @param rm RoundingMode
     * @return The data set.
     */
    public Chart_AgeGenderLineData getDefaultData(int ageInterval, int saeyi, int oom,
            RoundingMode rm) {
        Chart_AgeGenderLineData r = new Chart_AgeGenderLineData();
        TreeMap<Integer, Stats_BigRational1> fsss = new TreeMap<>();
        TreeMap<Integer, Stats_BigRational1> msss = new TreeMap<>();
        AgeGenderPopulation data10000 = getPopulationData(10000, 10000);
        AgeGenderPopulation data9000 = getPopulationData(9000, 9000);
        AgeGenderPopulation data9900 = getPopulationData(9900, 9900);
        AgeGenderPopulation data9950 = getPopulationData(9950, 9950);
        AgeGenderPopulation data9800 = getPopulationData(9800, 9800);
        r.max = BigRational.ZERO;
        ArrayList<BigRational> fvalues = new ArrayList<>();
        BigRational pop10000 = null;
        BigRational pop9000 = null;
        BigRational pop9900 = null;
        BigRational pop9950 = null;
        BigRational pop9800 = null;
        // Females
        Integer ageF = null;
        for (Integer age : data10000.fp.keySet()) {
            System.out.println("age " + age);
            pop10000 = BigRational.ZERO;
            pop9000 = BigRational.ZERO;
            pop9900 = BigRational.ZERO;
            pop9950 = BigRational.ZERO;
            pop9800 = BigRational.ZERO;
            for (int interval = 0; interval < ageInterval; interval++) {
                if (age >= saeyi) {
                    //pop10000 = fvalues.get(0).add(data10000.fp.get(age));
                    //pop9000 = fvalues.get(1).add(data9000.fp.get(age));
                    //pop9900 = fvalues.get(2).add(data9900.fp.get(age));
                    //pop9950 = fvalues.get(3).add(data9950.fp.get(age));
                    //pop9800 = fvalues.get(4).add(data9800.fp.get(age));
                } else {
                    pop10000 = pop10000.add(data10000.fp.get(age));
                    pop9000 = pop9000.add(data9000.fp.get(age));
                    pop9900 = pop9900.add(data9900.fp.get(age));
                    pop9950 = pop9950.add(data9950.fp.get(age));
                    pop9800 = pop9800.add(data9800.fp.get(age));
                }
            }
            ageF = age;
        }
        fvalues.add(pop10000);
        fvalues.add(pop9000);
        fvalues.add(pop9900);
        fvalues.add(pop9950);
        fvalues.add(pop9800);
        Stats_BigRational1 fss = new Stats_BigRational1(fvalues);
        System.out.println("Female age " + ageF.toString());
        // Set r.max to be the maximum of the median added to the standard 
        // deviation
        r.max = BigRational.max(r.max, fss.getMax().add(fss.getMedian()));
        if (ageF < saeyi) {
            fsss.put(ageF, fss);
        } else {
            fsss.put(saeyi + ageInterval, fss);
        }
        // Males
        Integer ageM = null;
        ArrayList<BigRational> mvalues = new ArrayList<>();
        for (Integer age : data10000.mp.keySet()) {
            pop10000 = BigRational.ZERO;
            pop9000 = BigRational.ZERO;
            pop9900 = BigRational.ZERO;
            pop9950 = BigRational.ZERO;
            pop9800 = BigRational.ZERO;
            for (int interval = 0; interval < ageInterval; interval++) {
                if (age >= saeyi) {
                    //pop10000 = mvalues.get(0).add(data10000.mp.get(age));
                    //pop9000 = mvalues.get(1).add(data9000.mp.get(age));
                    //pop9900 = mvalues.get(2).add(data9900.mp.get(age));
                    //pop9950 = mvalues.get(3).add(data9950.mp.get(age));
                    //pop9800 = mvalues.get(4).add(data9800.mp.get(age));
                } else {
                    pop10000 = pop10000.add(data10000.mp.get(age));
                    pop9000 = pop9000.add(data9000.mp.get(age));
                    pop9900 = pop9900.add(data9900.mp.get(age));
                    pop9950 = pop9950.add(data9950.mp.get(age));
                    pop9800 = pop9800.add(data9800.mp.get(age));
                }
            }
            ageM = age;
        }
        mvalues.add(pop10000);
        mvalues.add(pop9000);
        mvalues.add(pop9900);
        mvalues.add(pop9950);
        mvalues.add(pop9800);
        Stats_BigRational1 mss = new Stats_BigRational1(mvalues);
        System.out.println("Male age " + ageM.toString());
        // Set r.max to be the maximum of the median added to the standard 
        // deviation
        r.max = BigRational.max(r.max, mss.getMax().add(mss.getMedian()));
        if (ageM < saeyi) {
            msss.put(ageM, mss);
        } else {
            msss.put(saeyi + ageInterval, mss);
        }
        return r;
    }

    /**
     * Returns a sample data set.
     *
     * @param dpc decimalPlacePrecisionForCalculations
     * @param rm roundingMode
     * @return The data set.
     *
    public static Object[] getData() {
        Object[] result = new Object[2];
        TreeMap<Integer, Stats_BigRational> femaleBoxPlotStatistics = new TreeMap<>();
        TreeMap<Integer, Stats_BigRational> maleBoxPlotStatistics = new TreeMap<>();
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
            femaleBoxPlotStatistics.put(age, new Stats_BigRational(values));
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
            maleBoxPlotStatistics.put(age, new Stats_BigRational(values));
        }
        result[0] = femaleBoxPlotStatistics;
        result[1] = maleBoxPlotStatistics;
        return result;
    }
    */
    
    @Override
    public Chart_Data getData() {
        return (Chart_AgeGenderData) data;
    }
    
}
