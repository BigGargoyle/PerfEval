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

    static int DEFAULT_BOOTSTRAP_SAMPLE_COUNT = 1_000;

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
    public static boolean evaluateBootstrap(double[][] sampleSet1, double[][] sampleSet2, double critValue) {
        return evaluateBootstrap(sampleSet1, sampleSet2, critValue, DEFAULT_BOOTSTRAP_SAMPLE_COUNT);
    }

    public static boolean evaluateBootstrap(double[][] sampleSet1, double[][] sampleSet2, double critValue, int bootstrapSampleCount) {
        double[] bootstrapSample = createBootstrapSample(sampleSet1, sampleSet2, bootstrapSampleCount);
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
    private static double[] createBootstrapSample(double[][] sampleSet1, double[][] sampleSet2, int bootstrapSampleCount) {
        Random random = new Random();
        double[] result = new double[bootstrapSampleCount];
        for (int i = 0; i < bootstrapSampleCount; i++) {
            double sample1 = calcBoostrapValue(sampleSet1, bootstrapSampleCount);
            double sample2 = calcBoostrapValue(sampleSet2, bootstrapSampleCount);
            result[i] = sample1 - sample2;
        }
        return result;
    }

    private static double calcBoostrapValue(double[][] sampleSet1, int bootstrapSampleCount) {
        Random random = new Random();
        double[] result = new double[bootstrapSampleCount];
        for (int i = 0; i < sampleSet1.length; i++) {
            result[i] = createBootstrapOf1DArray(sampleSet1[random.nextInt(0, sampleSet1.length)], bootstrapSampleCount);
        }
        return CalculateMean(result);
    }

    private static double createBootstrapOf1DArray(double[] sampleSet, int bootstrapSampleCount) {
        double[] result = new double[bootstrapSampleCount];
        for (int i = 0; i < bootstrapSampleCount; i++) {
            double[] bootstrappedDataset = createBootstrappedDataset(sampleSet);
            result[i] = CalculateMean(bootstrappedDataset);
        }
        return CalculateMean(result);
    }

    private static double[] createBootstrappedDataset(double[] sampleSet){
        Random random = new Random();
        double[] result = new double[sampleSet.length];
        for (int i = 0; i < sampleSet.length; i++) {
            result[i] = sampleSet[random.nextInt(0, sampleSet.length)];
        }
        return result;
    }

    private static double CalculateMean(double[] sampleSet){
        double sum = 0;
        for (double v : sampleSet) {
            sum += v;
        }
        return sum / sampleSet.length;
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
