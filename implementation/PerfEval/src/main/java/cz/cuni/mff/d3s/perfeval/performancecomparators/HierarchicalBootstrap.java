package cz.cuni.mff.d3s.perfeval.performancecomparators;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.Random;

/**
 * Class for performing hierarchical bootstrap
 */
public class HierarchicalBootstrap {
    /**
     * Critical value for bootstrap statistical test
     */
    double critValue;
    /**
     * Count of samples used for bootstrap
     */
    int bootstrapSampleCount;

    /**
     * Constructor for HierarchicalBootstrap
     *
     * @param critValue            critical value for bootstrap statistical test
     * @param bootstrapSampleCount count of samples used for bootstrap
     */
    public HierarchicalBootstrap(double critValue, int bootstrapSampleCount) {
        this.critValue = critValue;
        this.bootstrapSampleCount = bootstrapSampleCount;
    }

    /**
     * Performs hierarchical bootstrap
     *
     * @param sampleSet1 first set of samples
     * @param sampleSet2 second set of samples
     * @return true if performance of second set of samples did not get worse, false otherwise
     */
    public boolean evaluateBootstrap(double[][] sampleSet1, double[][] sampleSet2) {
        double[] bootstrapSample = createBootstrapSample(sampleSet1, sampleSet2);
        double[] bootstrapInterval = calcCIInterval(bootstrapSample, 1 - critValue);
        assert bootstrapInterval.length == 2;
        return bootstrapInterval[0] <= 0 && bootstrapInterval[1] >= 0;
    }

    /**
     * Creates bootstrap sample from two sets of samples (difference of two random samples)
     *
     * @param sampleSet1 first set of samples
     * @param sampleSet2 second set of samples
     * @return bootstrap sample
     */
    private double[] createBootstrapSample(double[][] sampleSet1, double[][] sampleSet2) {
        Random random = new Random();
        double[] result = new double[bootstrapSampleCount];
        for (int i = 0; i < bootstrapSampleCount; i++) {
            var sampleArr1 = sampleSet1[random.nextInt(0, sampleSet1.length)];
            var sampleArr2 = sampleSet2[random.nextInt(0, sampleSet2.length)];
            var sample1 = sampleArr1[random.nextInt(0, sampleArr1.length)];
            var sample2 = sampleArr2[random.nextInt(0, sampleArr2.length)];
            result[i] = sample1 - sample2;
        }
        return result;
    }

    /**
     * Calculates confidence interval for given data and confidence level
     *
     * @param data            data
     * @param confidenceLevel confidence level
     * @return confidence interval for given data and confidence level
     */
    public static double[] calcCIInterval(double[] data, double confidenceLevel) {
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

        return new double[]{lowerBound, upperBound};
    }

}
