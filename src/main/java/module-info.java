module uk.ac.leeds.ccg.chart {
    requires transitive java.logging;
    requires transitive java.desktop;
    requires transitive uk.ac.leeds.ccg.generic;
    requires uk.ac.leeds.ccg.math;
    requires uk.ac.leeds.ccg.data;
    requires transitive uk.ac.leeds.ccg.stats;
    /**
     * The big-math library is mostly used for representing and computing with
     * rational numbers as {@link ch.obermuhlner.math.big.BigRational}. It is
     * also for some functions that work on {@link java.math.BigDecimal}.
     */
    requires transitive ch.obermuhlner.math.big;

    
    requires commons.math3;
    exports uk.ac.leeds.ccg.chart.core;
    exports uk.ac.leeds.ccg.chart.data;
    exports uk.ac.leeds.ccg.chart.examples;
    exports uk.ac.leeds.ccg.chart.execution;
}