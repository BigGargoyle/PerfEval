package org.example.performanceComparatorFactory;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.TTest;
import org.example.evaluation.MeasurementComparisonRecord;
import org.example.measurementFactory.Measurement;
import org.example.measurementFactory.UniversalTimeUnit;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BootstrapPerformanceComparator implements IPerformanceComparator {

    static final int MINUS_ONE = -1;
    final UniversalTimeUnit maxTestTime;
    final double criticalValue;
    // maxConfidenceIntervalWidth = 0.1 means confidence interval mean+-(0.1*mean)
    final double maxConfidenceIntervalWidth;

    /**
     * @param pValue                  max p value for statistical tests that will be done with given data sets
     * @param confidenceIntervalWidth maximum accepted relative width of confidence interval (relative to mean of data set)
     */
    public BootstrapPerformanceComparator(double pValue, double confidenceIntervalWidth, UniversalTimeUnit testTime) {
        maxConfidenceIntervalWidth = confidenceIntervalWidth;
        criticalValue = pValue;
        maxTestTime = new UniversalTimeUnit(testTime.getTime(TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS);
    }

    @Override
    public MeasurementComparisonRecord compareSets(Measurement oldMeasurement, Measurement newMeasurement) {
        
        UniversalTimeUnit oldAverage;
        UniversalTimeUnit newAverage;
        double performanceChange;
        int minSampleCount = MINUS_ONE;
        

        SummaryStatistics newStat = listToStatistic(oldMeasurement.measuredTimes());
        SummaryStatistics oldStat = listToStatistic(newMeasurement.measuredTimes());
        if (Math.abs(pValueOfTTest(newStat, oldStat)) > criticalValue) {
            ComparisonResult comparisonResult = ComparisonResult.DifferentDistribution;
            boolean testVerdict = false;
            return new MeasurementComparisonRecord(
                    oldAverage,
                    newAverage,
                    performanceChange,
                    comparisonResult,
                    testVerdict,
                    minSampleCount,
                    oldMeasurement,
                    newMeasurement
            );
        }
        double newSetCIRadius = calcMeanCI(newStat, 1 - criticalValue);
        double oldSetCIRadius = calcMeanCI(oldStat, 1 - criticalValue);

        double newMean = newStat.getMean();
        double oldMean = oldStat.getMean();

        if (newSetCIRadius <= newMean * maxConfidenceIntervalWidth
                && oldSetCIRadius <= oldMean * maxConfidenceIntervalWidth) {
            ComparisonResult comparisonResult = ComparisonResult.SameDistribution;
            boolean testVerdict = true;
            return new MeasurementComparisonRecord(
                    oldAverage,
                    newAverage,
                    performanceChange,
                    comparisonResult,
                    testVerdict,
                    minSampleCount,
                    oldMeasurement,
                    newMeasurement
            );
        }


        int oldMinSampleCount = calcMinSampleCount(oldStat, 1 - criticalValue, maxConfidenceIntervalWidth);
        int newMinSampleCount = calcMinSampleCount(newStat, 1 - criticalValue, maxConfidenceIntervalWidth);
        minSampleCount = Math.max(oldMinSampleCount, newMinSampleCount);

        if (newMean * minSampleCount > maxTestTime.getNanoSeconds() || oldMean * minSampleCount > maxTestTime.getNanoSeconds()) {
            ComparisonResult comparisonResult = ComparisonResult.Bootstrap;
            boolean testVerdict = Bootstrap.evaluate(newMeasurement.measuredTimes(), oldMeasurement.measuredTimes(), criticalValue);
            return new MeasurementComparisonRecord(
                    oldAverage,
                    newAverage,
                    performanceChange,
                    comparisonResult,
                    testVerdict,
                    minSampleCount,
                    oldMeasurement,
                    newMeasurement
            );
        }
        
        ComparisonResult comparisonResult = ComparisonResult.NotEnoughSamples;
        boolean testVerdict = false;
        return new MeasurementComparisonRecord(
                oldAverage,
                newAverage,
                performanceChange,
                comparisonResult,
                testVerdict,
                minSampleCount,
                oldMeasurement,
                newMeasurement
        );
    }
    
    /**
     * Calculates p value of T-test of hypothesis that values1 and values2 are from the same distribution
     *
     * @param values1 first statistic to be TTested
     * @param values2 second statistic to be TTested
     * @return pValue of TTest
     */
    private static double pValueOfTTest(SummaryStatistics values1, SummaryStatistics values2) {
        TTest tTest = new TTest();
        return tTest.t(values1, values2);
    }

    /**
     *
     * @param statSet list of UniversalTimeUnits that the SummaryStatistics will be created from
     * @return SummaryStatistics of times from list
     */
    private SummaryStatistics listToStatistic(List<UniversalTimeUnit> statSet) {
        SummaryStatistics stats = new SummaryStatistics();
        for (UniversalTimeUnit value : statSet) {
            stats.addValue(value.getNanoSeconds());
        }
        return stats;
    }


    //  start of code from https://gist.github.com/gcardone/5536578

    /**
     * @param stats statistic for which is the confidence interval calculated for
     * @param level probability that the mean of stats is inside the confidence interval
     * @return distance between mean and one of the ends of the confidence interval
     */
    private static double calcMeanCI(SummaryStatistics stats, double level) {
        try {
            // Create T Distribution with N-1 degrees of freedom
            TDistribution tDist = new TDistribution(stats.getN() - 1);
            // Calculate critical value
            double critVal = tDist.inverseCumulativeProbability(1.0 - (1 - level) / 2);
            // Calculate confidence interval
            return critVal * stats.getStandardDeviation() / Math.sqrt(stats.getN());
        } catch (MathIllegalArgumentException e) {
            return Double.NaN;
        }
    }

    // end of code from https://gist.github.com/gcardone/5536578

    // code inspired by these equations https://ecampusontario.pressbooks.pub/introstats/chapter/7-5-calculating-the-sample-size-for-a-confidence-interval/

    /**
     * Equations from @see <a href="URL#<a href="https://ecampusontario.pressbooks.pub/introstats/chapter/7-5-calculating-the-sample-size-for-a-confidence-interval/">
     *     </a>">this link</a> are used to compute minimal sample count
     *
     * @param statistics statistic to calculate minimal sample count from
     * @param confidenceLevel statistically
     * @param maxWidth maximal relative width of confidence interval
     * @return minimal count of measured samples to get statistically significant results
     */
    private static int calcMinSampleCount(SummaryStatistics statistics, double confidenceLevel, double maxWidth) {
        // Create a NormalDistribution object
        NormalDistribution normalDistribution = new NormalDistribution();

        // Calculate the critical value (Z) for the given confidence level
        double criticalValue = normalDistribution.inverseCumulativeProbability(1 - (1 - confidenceLevel) / 2);

        double mean = statistics.getMean();
        double standardDeviation = statistics.getStandardDeviation();
        double sampleCount = Math.pow(criticalValue * standardDeviation / (mean * maxWidth), 2);

        return (int) Math.ceil(sampleCount);
    }
}