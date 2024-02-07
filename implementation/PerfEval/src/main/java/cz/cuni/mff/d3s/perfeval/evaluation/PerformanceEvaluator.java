package cz.cuni.mff.d3s.perfeval.evaluation;

import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.printers.MeasurementComparisonRecord;

/**
 * Class that compares two sets of samples (with statistical tests).
 */
public class PerformanceEvaluator {

    /**
     * Constant that represents 1 as a percentage.
     */
    private static final int ONE_HUNDRED = 100;

    /**
     * Constant representing that the minimal sample count is not needed.
     */
    private static final int MINUS_ONE = -1;
    /**
     * Maximal width of the confidence interval.
     */
    private final double maxCIWidth;
    /**
     * Tolerance of the performance to get worse.
     */
    private final double tolerance;

    /**
     * Maximal count of tests to be performed.
     */
    private final int maxTestCount;

    /**
     * Statistic test to be used.
     */
    private final StatisticTest statisticTest;

    /**
     * Constructor for PerformanceEvaluator.
     *
     * @param maxCIWidth    maximal width of the confidence interval
     * @param tolerance     tolerance of the performance to get worse
     * @param maxTestCount  maximal count of tests to be performed
     * @param statisticTest statistic test to be used
     */
    public PerformanceEvaluator(double maxCIWidth, double tolerance, int maxTestCount, StatisticTest statisticTest) {
        this.maxCIWidth = maxCIWidth;
        this.tolerance = tolerance;
        this.maxTestCount = maxTestCount;
        this.statisticTest = statisticTest;
        assert tolerance >= 0 && tolerance <= 1;
        assert maxCIWidth > 0 && maxCIWidth < 1;
        assert statisticTest != null;
    }

    /**
     * Constructor for BootstrapPerformanceComparator.
     *
     * @param oldSamples      samples from old version
     * @param newSamples      samples from new version
     * @param ciInterval      confidence interval of the samples
     * @return record with results of comparison
     */
    private MeasurementComparisonRecord constructRecord(Samples oldSamples, Samples newSamples,
                                                        double[] ciInterval) {
        double oldAvg = ArrayUtilities.calculateHierarchicAverage(oldSamples.getRawData());
        double newAvg = ArrayUtilities.calculateHierarchicAverage(newSamples.getRawData());

        double performanceChange = oldSamples.getMetric().getMetricPerformanceDirection() ? newAvg / oldAvg : oldAvg / newAvg;
        performanceChange = performanceChange * ONE_HUNDRED - ONE_HUNDRED;

        ComparisonResult comparisonResult;
        int minSampleCount = MINUS_ONE;
        // does ciInterval contain 0?, if not, we can say that the performance is different
        if (!(ciInterval[1] > 0 && ciInterval[0] < 0)) {
            comparisonResult = ComparisonResult.DifferentDistribution;
        } else {
            //relative width of the confidence interval ((upperBound - lowerBound) / mean)
            double ciWidth = Math.abs((ciInterval[1] - ciInterval[0]) / (oldAvg + newAvg) / 2);
            //if the confidence interval is smaller than maxCIWidth, we can say that the performance is the same
            if (ciWidth <= maxCIWidth) {
                comparisonResult = ComparisonResult.SameDistribution;
            } else {
                minSampleCount = statisticTest.calcMinSampleCount(oldSamples.getRawData(), newSamples.getRawData(), maxCIWidth);
                assert minSampleCount >= Math.min(oldSamples.getRawData().length, newSamples.getRawData().length);
                if (minSampleCount > maxTestCount) {
                    comparisonResult = ComparisonResult.Bootstrap;
                } else {
                    comparisonResult = ComparisonResult.NotEnoughSamples;
                }
            }
        }

        boolean testVerdict = ((comparisonResult == ComparisonResult.SameDistribution // performance is the same
                || performanceChange > 0 // performance is better
                || Math.abs(performanceChange / ONE_HUNDRED) > 1 - tolerance)
                && comparisonResult!=ComparisonResult.NotEnoughSamples
                && comparisonResult!=ComparisonResult.Bootstrap); // performance is worse, but within tolerance


        return new MeasurementComparisonRecord(oldAvg, newAvg, performanceChange,
                comparisonResult, testVerdict,
                minSampleCount, oldSamples, newSamples);
    }

    /**
     * Compares two sets of samples (with statistical tests).
     *
     * @param oldSamples samples from old version
     * @param newSamples samples from new version
     * @return record with results of comparison
     */
    public MeasurementComparisonRecord compareSets(Samples oldSamples, Samples newSamples) {
        return constructRecord(oldSamples, newSamples,
                statisticTest.calcCIInterval(oldSamples.getRawData(), newSamples.getRawData()));

    }
}
