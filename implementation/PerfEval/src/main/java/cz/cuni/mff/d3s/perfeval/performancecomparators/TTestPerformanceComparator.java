package cz.cuni.mff.d3s.perfeval.performancecomparators;

import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonRecord;
import org.apache.commons.math3.stat.StatUtils;

import java.time.Duration;

/**
 * Class representing performance comparator using the statistical t-test.
 */
public class TTestPerformanceComparator implements PerformanceComparator {
    /**
     * Constant representing that the minimal sample count is not needed.
     */
    static final int MINUS_ONE = -1;
    /**
     * Critical value of the t-test.
     */
    double critValue;
    /**
     * Maximal width of the confidence interval.
     */
    double maxCIWidth;
    /**
     * Tolerance of the performance to get worse.
     */
    double tolerance;
    /**
     * Maximal time of the test.
     */
    Duration maxTestTime;

    /**
     * Constructor of the class.
     *
     * @param critValue   critical value of the t-test
     * @param maxCIWidth  maximal width of the confidence interval
     * @param tolerance   tolerance of the performance to get worse
     * @param maxTestTime maximal time of the test
     */
    public TTestPerformanceComparator(double critValue, double maxCIWidth, double tolerance, Duration maxTestTime) {
        this.critValue = critValue;
        this.maxCIWidth = maxCIWidth;
        this.tolerance = tolerance;
        this.maxTestTime = maxTestTime;
        assert tolerance >= 0 && tolerance <= 1;
        assert critValue > 0 && critValue < 1;
        assert maxCIWidth > 0 && maxCIWidth < 1;
    }

    /**
     * Method for constructing the record of the comparison.
     *
     * @param oldSamples       old samples
     * @param newSamples       new samples
     * @param comparisonResult result of the comparison
     * @param minSampleCount   minimal sample count
     * @return record of the comparison
     */
    private MeasurementComparisonRecord constructRecord(Samples oldSamples, Samples newSamples, ComparisonResult comparisonResult, int minSampleCount) {
        double oldAvg = ArrayUtilities.calculateAverage(oldSamples.getRawData());
        double newAvg = ArrayUtilities.calculateAverage(newSamples.getRawData());

        double performanceChange = oldSamples.getMetric().getMetricPerformanceDirection() ? newAvg / oldAvg : oldAvg / newAvg;
        performanceChange = performanceChange * 100 - 100;

        boolean testVerdict;
        switch (comparisonResult) {
            case SameDistribution -> testVerdict = true;
            case DifferentDistribution ->
                    testVerdict = performanceChange >= 0 || Math.abs(performanceChange / 100) > 1 - tolerance;
            // --> to default case NotEnoughSamples -> testVerdict = false;
            default -> testVerdict = false;
        }

        return new MeasurementComparisonRecord(oldAvg, newAvg, performanceChange, comparisonResult, testVerdict, minSampleCount, oldSamples, newSamples);
    }

    /**
     * Method for comparing two sets of samples using the t-test.
     *
     * @param oldMeasurement old measurement
     * @param newMeasurement new measurement
     * @return record of the comparison
     */
    @Override
    public MeasurementComparisonRecord compareSets(Samples oldMeasurement, Samples newMeasurement) {
        if (StatisticTTest.areSetsSame(oldMeasurement.getRawData(), newMeasurement.getRawData(), critValue)) {
            return constructRecord(oldMeasurement, newMeasurement, ComparisonResult.SameDistribution, MINUS_ONE);
        }
        double[] newArr = ArrayUtilities.mergeArrays(newMeasurement.getRawData());
        double[] oldArr = ArrayUtilities.mergeArrays(oldMeasurement.getRawData());
        double[] newCI = HierarchicalBootstrap.calcCIInterval(newArr, 1 - critValue);
        double[] oldCI = HierarchicalBootstrap.calcCIInterval(oldArr, 1 - critValue);
        double newMean = StatUtils.mean(newArr);
        double oldMean = StatUtils.mean(oldArr);
        if (newCI[1] - newCI[0] <= newMean * maxCIWidth && oldCI[1] - oldCI[0] <= oldMean * maxCIWidth) {
            return constructRecord(oldMeasurement, newMeasurement, ComparisonResult.DifferentDistribution, MINUS_ONE);
        }
        int minSampleCount = Math.max(ArrayUtilities.calcMinSampleCount(newArr, 1 - critValue, maxCIWidth), ArrayUtilities.calcMinSampleCount(oldArr, 1 - critValue, maxCIWidth));
        return constructRecord(oldMeasurement, newMeasurement, ComparisonResult.NotEnoughSamples, minSampleCount);
    }
}
