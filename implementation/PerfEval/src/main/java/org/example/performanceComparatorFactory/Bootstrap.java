package org.example.performanceComparatorFactory;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.example.measurementFactory.UniversalTimeUnit;

import java.util.List;
import java.util.Random;

/**
 * Class with methods to execute statistical bootstrap on sets of performance measurements.
 */
public class Bootstrap {
    static final int DefaultBootstrapSampleSize = 10_000;

    /**
     *
     * @param newSet first set of measurements for bootstrap
     * @param oldSet second set of measurements for bootsrap
     * @param critValue maximal width of confidence interval for bootstrap test
     * @return true - if zero is inside the interval of two-sided bootstrap test, false - otherwise
     */
    public static boolean evaluate(List<UniversalTimeUnit> newSet, List<UniversalTimeUnit> oldSet,
                                   double critValue) {

        double[] sample = createBootstrapSample(newSet, oldSet);
        double[] confidenceInterval = calcCIInterval(sample, 1-critValue);
        // returns false if and only if it seems that input sets have different distribution
        return 0 > confidenceInterval[0] && 0 < confidenceInterval[1];
    }

    static double[] createBootstrapSample(List<UniversalTimeUnit> sample1, List<UniversalTimeUnit> sample2) {
        double[] result = new double[DefaultBootstrapSampleSize];
        for (int i = 0; i < DefaultBootstrapSampleSize; i++) {
            double bootstrapOfSample1 = calculateBootstrapMeanSample(sample1);
            double bootstrapOfSample2 = calculateBootstrapMeanSample(sample2);
            result[i] = bootstrapOfSample1 - bootstrapOfSample2;
        }
        return result;
    }

    static final Random random = new Random();
    static double calculateBootstrapMeanSample(List<UniversalTimeUnit> sample){
        double sum = 0;
        for (int i = 0; i< sample.size();i++){
            int randomIndex = random.nextInt(sample.size());
            sum+=sample.get(randomIndex).getNanoSeconds();
        }
        return sum/sample.size();
    }

    static double[] calcCIInterval(double[] data, double confidenceLevel){
        // Create a SummaryStatistics object to compute mean and standard deviation
        SummaryStatistics stats = new SummaryStatistics();
        for (double value : data) {
            stats.addValue(value);
        }

        // Calculate the sample mean and standard deviation
        double sampleMean = stats.getMean();
        double sampleStdDev = stats.getStandardDeviation();

        // Calculate the degrees of freedom for a t-distribution
        int degreesOfFreedom = data.length - 1;

        // Create a t-distribution with the specified degrees of freedom
        TDistribution tDistribution = new TDistribution(degreesOfFreedom);

        // Calculate the critical value for the confidence level
        double criticalValue = tDistribution.inverseCumulativeProbability(1.0 - (1.0 - confidenceLevel) / 2.0);

        // Calculate the margin of error
        double marginOfError = criticalValue * (sampleStdDev / Math.sqrt(data.length));

        // Calculate the lower and upper bounds of the confidence interval
        double lowerBound = sampleMean - marginOfError;
        double upperBound = sampleMean + marginOfError;

        return new double[] {lowerBound, upperBound};
    }

}