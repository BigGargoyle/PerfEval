package org.example.PerformanceComparatorFactory;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

/**
 * Implementation of IPerformanceComparator that is able to detect that given datasets have not enough samples to ensure
 * that they are from the same distribution
 */
public class NormalPerformanceComparator implements IPerformanceComparator{
    double criticalValue;
    // maxConfidenceIntervalWidth = 0.1 means confidence interval mean+-(0.1*mean)
    double maxConfidenceIntervalWidth;
    int minSampleCount;
    ComparisonResult comparisonResult;

    /**
     *
     * @param pValue max p value for statistical tests that will be done with given data sets
     * @param confidenceIntervalWidth maximum accepted relative width of confidence interval (relative to mean of data set)
     */
    public NormalPerformanceComparator(double pValue, double confidenceIntervalWidth){
        maxConfidenceIntervalWidth = confidenceIntervalWidth;
        criticalValue = pValue;
    }
    @Override
    public ComparisonResult CompareSets(double[] newSet, double[] oldSet) {

        if(oldSet == null || newSet == null || oldSet.length==0 || newSet.length == 0){
            // an error has occurred
            comparisonResult = ComparisonResult.None;
            return comparisonResult;
        }

        if(Math.abs(pValueOfTTest(newSet, oldSet)) > criticalValue) {
            comparisonResult = ComparisonResult.DifferentDistribution;
            return comparisonResult;
        }
        SummaryStatistics newStat = ArrayToStatistic(newSet);
        SummaryStatistics oldStat = ArrayToStatistic(oldSet);
        double newSetCIRadius = calcMeanCI(newStat, 1-criticalValue);
        double oldSetCIRadius = calcMeanCI(oldStat, 1-criticalValue);

        if(newSetCIRadius<=newStat.getMean()*maxConfidenceIntervalWidth
                && oldSetCIRadius<=oldStat.getMean()*maxConfidenceIntervalWidth){
            comparisonResult = ComparisonResult.SameDistribution;
            return comparisonResult;
        }

        comparisonResult = ComparisonResult.NotEnoughSamples;

        int oldMinSampleCount = calcMinSampleCount(oldStat, 1-criticalValue, maxConfidenceIntervalWidth);
        int newMinSampleCount = calcMinSampleCount(newStat, 1-criticalValue, maxConfidenceIntervalWidth);
        minSampleCount = Math.max(oldMinSampleCount, newMinSampleCount);

        return comparisonResult;
    }

    /**
     * Calculates p value of T-test of hypothesis that values1 and values2 are from the same distribution
     * @param values1
     * @param values2
     * @return
     */
    private static double pValueOfTTest(double[] values1, double[] values2){
        TTest tTest = new TTest();
        return tTest.t(values1, values2);
    }

    private SummaryStatistics ArrayToStatistic(double[] statSet){
        SummaryStatistics stats = new SummaryStatistics();
        for (double value : statSet){
            stats.addValue(value);
        }
        return stats;
    }

    @Override
    public ComparisonResult GetLastComparisonResult() {
        return comparisonResult;
    }

    @Override
    public int GetMinSampleCount() {
        if(comparisonResult == ComparisonResult.NotEnoughSamples)
            return minSampleCount;
        return -1;
    }

    @Override
    public double GetConfidenceIntervalWidth() {
        return 0;
    }

    //  start of code from https://gist.github.com/gcardone/5536578

    /**
     *
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

    private static int calcMinSampleCount(SummaryStatistics statistics, double confidenceLevel, double maxWidth){
        // Create a NormalDistribution object
        NormalDistribution normalDistribution = new NormalDistribution();

        // Calculate the critical value (Z) for the given confidence level
        double criticalValue = normalDistribution.inverseCumulativeProbability(1 - (1 - confidenceLevel) / 2);

        double mean = statistics.getMean();
        double standardDeviation = statistics.getStandardDeviation();
        double sampleCount = Math.pow(criticalValue*standardDeviation/(mean*maxWidth),2);

        return (int)Math.ceil(sampleCount);
    }
}
