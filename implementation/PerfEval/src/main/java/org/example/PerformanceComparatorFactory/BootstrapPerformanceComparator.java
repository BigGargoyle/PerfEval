package org.example.PerformanceComparatorFactory;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.TTest;
import org.example.MeasurementFactory.UniversalTimeUnit;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BootstrapPerformanceComparator implements IPerformanceComparator {

    final UniversalTimeUnit maxTestTime;
    final double criticalValue;
    // maxConfidenceIntervalWidth = 0.1 means confidence interval mean+-(0.1*mean)
    final double maxConfidenceIntervalWidth;
    int minSampleCount;
    ComparisonResult comparisonResult;

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
    public ComparisonResult compareSets(List<UniversalTimeUnit> newSet, List<UniversalTimeUnit> oldSet) {

        if (oldSet == null || newSet == null || oldSet.size() == 0 || newSet.size() == 0) {
            // an error has occurred
            comparisonResult = ComparisonResult.None;
            return comparisonResult;
        }

        SummaryStatistics newStat = listToStatistic(newSet);
        SummaryStatistics oldStat = listToStatistic(oldSet);
        if (Math.abs(pValueOfTTest(newStat, oldStat)) > criticalValue) {
            comparisonResult = ComparisonResult.DifferentDistribution;
            return comparisonResult;
        }
        double newSetCIRadius = calcMeanCI(newStat, 1 - criticalValue);
        double oldSetCIRadius = calcMeanCI(oldStat, 1 - criticalValue);

        double newMean = newStat.getMean();
        double oldMean = oldStat.getMean();

        if (newSetCIRadius <= newMean * maxConfidenceIntervalWidth
                && oldSetCIRadius <= oldMean * maxConfidenceIntervalWidth) {
            comparisonResult = ComparisonResult.SameDistribution;
            return comparisonResult;
        }

        comparisonResult = ComparisonResult.NotEnoughSamples;

        int oldMinSampleCount = calcMinSampleCount(oldStat, 1 - criticalValue, maxConfidenceIntervalWidth);
        int newMinSampleCount = calcMinSampleCount(newStat, 1 - criticalValue, maxConfidenceIntervalWidth);
        minSampleCount = Math.max(oldMinSampleCount, newMinSampleCount);

        if (newMean * minSampleCount > maxTestTime.getNanoSeconds() || oldMean * minSampleCount > maxTestTime.getNanoSeconds()) {
            comparisonResult = ComparisonResult.Bootstrap;
            return comparisonResult;
        }

        return comparisonResult;
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

    @Override
    public ComparisonResult getLastComparisonResult() {
        return comparisonResult;
    }

    @Override
    public int getMinSampleCount() {
        if (comparisonResult == ComparisonResult.NotEnoughSamples || comparisonResult == ComparisonResult.Bootstrap)
            return minSampleCount;
        return -1;
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