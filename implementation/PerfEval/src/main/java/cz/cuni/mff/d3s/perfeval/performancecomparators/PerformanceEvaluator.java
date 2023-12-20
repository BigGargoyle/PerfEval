package cz.cuni.mff.d3s.perfeval.performancecomparators;

import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonRecord;

import java.time.Duration;

public class PerformanceEvaluator {
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

    StatisticTest statisticTest;

    /**
     * Constructor of the class.
     *
     * @param critValue   critical value of the t-test
     * @param maxCIWidth  maximal width of the confidence interval
     * @param tolerance   tolerance of the performance to get worse
     * @param maxTestTime maximal time of the test
     */
    public PerformanceEvaluator(double critValue, double maxCIWidth, double tolerance, Duration maxTestTime, StatisticTest statisticTest) {
        this.critValue = critValue;
        this.maxCIWidth = maxCIWidth;
        this.tolerance = tolerance;
        this.maxTestTime = maxTestTime;
        this.statisticTest = statisticTest;
        assert tolerance >= 0 && tolerance <= 1;
        assert critValue > 0 && critValue < 1;
        assert maxCIWidth > 0 && maxCIWidth < 1;
        assert statisticTest != null;
    }

    /**
     * Constructor for BootstrapPerformanceComparator
     *
     * @param oldSamples      samples from old version
     * @param newSamples      samples from new version
     * @param statResult      result of statisticalEvaluator, true if performances seems not to be different, false otherwise
     * @return record with results of comparison
     */
    private MeasurementComparisonRecord constructRecord(Samples oldSamples, Samples newSamples,
                                                        EvaluatorResult statResult) {
        double oldAvg = ArrayUtilities.calculateAverage(oldSamples.getRawData());
        double newAvg = ArrayUtilities.calculateAverage(newSamples.getRawData());

        double performanceChange = oldSamples.getMetric().getMetricPerformanceDirection() ? newAvg / oldAvg : oldAvg / newAvg;
        performanceChange = performanceChange * 100 - 100;

        ComparisonResult comparisonResult = ComparisonResult.None;
        int minSampleCount = MINUS_ONE;
        if(!statResult.areSetsSame()){
            comparisonResult = ComparisonResult.DifferentDistribution;
        } else {
            //relative width of the confidence interval ((upperBound - lowerBound) / mean)
            double ciWidth = 2*(statResult.ciUpperBound() - statResult.ciLowerBound()) / (statResult.ciLowerBound()+statResult.ciUpperBound());
            //if the confidence interval is smaller than maxCIWidth, we can say that the performance is the same
            if(ciWidth <= maxCIWidth){
                comparisonResult = ComparisonResult.SameDistribution;
            }
            else {
                minSampleCount = statisticTest.calcMinSampleCount(oldSamples.getRawData(), newSamples.getRawData(), maxCIWidth);
                //TODO: pokud je to možné stihnout do maxTestTime, tak říct, že lepší to nebude
                comparisonResult = ComparisonResult.NotEnoughSamples;

            }
        }

        boolean testVerdict = comparisonResult == ComparisonResult.SameDistribution || performanceChange > 100
                || Math.abs(performanceChange / 100) > 1 - tolerance;


        return new MeasurementComparisonRecord(oldAvg, newAvg, performanceChange,
                comparisonResult, testVerdict,
                minSampleCount, oldSamples, newSamples);
    }

    /**
     * Compares two sets of samples (with statistical tests)
     *
     * @param oldSamples samples from old version
     * @param newSamples samples from new version
     * @return record with results of comparison
     */
    public MeasurementComparisonRecord compareSets(Samples oldSamples, Samples newSamples){
        return constructRecord(oldSamples, newSamples,
                statisticTest.calcCIInterval(oldSamples.getRawData(), newSamples.getRawData()));

    }
}
