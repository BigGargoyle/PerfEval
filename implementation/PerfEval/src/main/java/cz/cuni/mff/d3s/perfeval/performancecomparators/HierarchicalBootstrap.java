package cz.cuni.mff.d3s.perfeval.performancecomparators;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

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
        double[] result = new double[bootstrapSampleCount];
        Random random = new Random();
        //create bootstrapSampleCount of bootstrap samples
        for (int i = 0; i < bootstrapSampleCount; i++) {
            //create bootstrap sample of sampleSet1 and sampleSet2
            double sample1 = calcBoostrapValue(sampleSet1, bootstrapSampleCount, random);
            double sample2 = calcBoostrapValue(sampleSet2, bootstrapSampleCount, random);
            result[i] = sample1 - sample2;
        }
        return result;
    }

    private static double calcBoostrapValue(double[][] sampleSet, int bootstrapSampleCount, Random random) {
        double[] result = new double[sampleSet.length];
        //create bootstrapSampleCount of bootstrap samples
        for (int i = 0; i < sampleSet.length; i++) {
            //create 1 bootstrap sample of sampleSet
            //randomly select array from sampleSet
            //then calculate mean of bootstrapped dataset of that array
            result[i] = createBootstrapOf1DArray(sampleSet[random.nextInt(0, sampleSet.length)], bootstrapSampleCount, random);
        }
        return StatUtils.mean(result);
    }

    private static double createBootstrapOf1DArray(double[] sampleSet, int bootstrapSampleCount, Random random) {
        double[] result = new double[bootstrapSampleCount];
        for (int i = 0; i < bootstrapSampleCount; i++) {
            //create bootstrapped dataset
            double[] bootstrappedDataset = createBootstrappedDataset(sampleSet, random);
            result[i] = StatUtils.mean(bootstrappedDataset);
        }
        return StatUtils.mean(result);
    }

    private static double[] createBootstrappedDataset(double[] sampleSet, Random random){
        double[] result = new double[sampleSet.length];
        for (int i = 0; i < sampleSet.length; i++) {
            result[i] = sampleSet[random.nextInt(0, sampleSet.length)];
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
        Percentile percentile = new Percentile();
        double lowerBound = percentile.evaluate(data, confidenceLevel/2);
        double upperBound = percentile.evaluate(data, 100 - confidenceLevel/2);
        // Calculate the sample mean and standard deviation
        return new double[]{lowerBound, upperBound};
    }

}
