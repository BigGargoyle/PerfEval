package cz.cuni.mff.d3s.perfeval.performancecomparators;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Class with static methods for working with arrays
 */
public class ArrayUtilities {
    /**
     * Calculates average of all values in matrix
     *
     * @param matrix matrix of values
     * @return average of all values in matrix
     */
    public static double calculateAverage(double[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return 0.0;
        }

        int count = 0;
        double sum = 0.0;

        for (double[] doubles : matrix) {
            for (double d : doubles) {
                sum += d;
                count++;
            }
        }
        return sum / count;
    }

    /**
     * Calculates minimal sample count for given statistics, confidence level and maximum width
     *
     * @param statistics      array of statistics
     * @param confidenceLevel confidence level
     * @param maxWidth        maximum width of confidence interval
     * @return minimal sample count for given statistics, confidence level and maximum width
     */
    public static int calcMinSampleCount(double[] statistics, double confidenceLevel, double maxWidth) {
        // Create a NormalDistribution object
        NormalDistribution normalDistribution = new NormalDistribution();

        // Calculate the critical value (Z) for the given confidence level
        double criticalValue = normalDistribution.inverseCumulativeProbability(1 - (1 - confidenceLevel) / 2);

        double mean = StatUtils.mean(statistics);
        double standardDeviation = (new DescriptiveStatistics(statistics)).getStandardDeviation();
        double sampleCount = Math.pow(criticalValue * standardDeviation / (mean * maxWidth), 2);

        return (int) Math.ceil(sampleCount);
    }

    /**
     * Merges 2D array into 1D array
     *
     * @param sampleSet 2D array
     * @return 1D array
     */
    public static double[] mergeArrays(double[][] sampleSet) {
        int size = 0;
        for (var arr : sampleSet) {
            size += arr.length;
        }
        double[] mergedArray = new double[size];
        int innerArrayIndex = 0;
        int innerIndex = 0;
        for (int i = 0; i < mergedArray.length; i++) {
            mergedArray[i] = sampleSet[innerArrayIndex][innerIndex];
            innerIndex++;
            if (innerIndex == sampleSet[innerArrayIndex].length) {
                innerArrayIndex++;
                innerIndex = 0;
            }
        }
        return mergedArray;
    }
}
