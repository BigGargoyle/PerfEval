package cz.cuni.mff.d3s.perfeval.performancecomparators;

import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.fitting.leastsquares.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.util.Pair;

import java.util.Random;


/**
 * Class for performing hierarchical bootstrap
 */
public class HierarchicalBootstrap {

    static int DEFAULT_BOOTSTRAP_SAMPLE_COUNT = 1_000;

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

    public static double[] evaluateCIInterval(double[][] sampleSet1, double[][] sampleSet2, double confidenceLevel, int bootstrapSampleCount) {
        double[] bootstrapSample = createBootstrapSample(sampleSet1, sampleSet2, bootstrapSampleCount);
        return calcCIInterval(bootstrapSample, confidenceLevel);
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

    private static double[] createBootstrappedDataset(double[] sampleSet, Random random) {
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
        double lowerBound = percentile.evaluate(data, confidenceLevel / 2);
        double upperBound = percentile.evaluate(data, 100 - confidenceLevel / 2);
        // Calculate the sample mean and standard deviation
        return new double[]{lowerBound, upperBound};
    }

    public static int getMinSampleCount(double[][] sampleSet, double confidenceLevel, double maxCIWidth, int bootstrapSampleCount) {
        double[][] functionPoints = calcFunctionPoints(sampleSet, confidenceLevel, bootstrapSampleCount);
        // y = a * 1 / sqrt(x) + b
        double[] abParameters = calcFunctionParameters(functionPoints);
        return calcMinSampleCountFromFunction(abParameters, maxCIWidth);
    }

    private static double[][] calcFunctionPoints(double[][] sampleSet, double confidenceLevel, int bootstrapSampleCount) {
        int pointsCount = sampleSet.length;
        double[][] functionPoints = new double[pointsCount][];
        Random random = new Random();
        for (int x = 0; x < pointsCount; x++) {
            double[][] set = new double[x + 1][];
            System.arraycopy(sampleSet, 0, set, 0, x + 1);
            double[] bootstrapSample = createBootstrapSample(set, random, bootstrapSampleCount);
            double[] bootstrapInterval = calcCIInterval(bootstrapSample, confidenceLevel);
            assert bootstrapInterval.length == 2;
            double y = StatUtils.mean(bootstrapInterval) / (bootstrapInterval[1] - bootstrapInterval[0]);
            functionPoints[x] = new double[]{x, y};
        }
        return functionPoints;
    }

    private static double[] createBootstrapSample(double[][] sampleSet, Random random, int bootstrapSampleCount) {
        double[] result = new double[bootstrapSampleCount];
        //create bootstrapSampleCount of bootstrap samples
        for (int i = 0; i < bootstrapSampleCount; i++) {
            //create bootstrap sample of sampleSet
            double sample = calcBoostrapValue(sampleSet, bootstrapSampleCount, random);
            result[i] = sample;
        }
        return result;
    }

    public static int calcMinSampleCountFromFunction(double[] abParameters, double maxCIWidth) {
        double a = abParameters[0];
        double b = abParameters[1];

        // y = a * 1 / sqrt(x) + b
        // x = a * a / ((y - b) * (y - b))
        // y = maxCIWidth
        double x = a * a / ((maxCIWidth - b) * (maxCIWidth - b));
        return (int) Math.ceil(x);
    }

    public static double[] calcFunctionParameters(double[][] functionPoints) {
        double[] xData = new double[functionPoints.length];
        double[] yData = new double[functionPoints.length];

        for (int i = 0; i < functionPoints.length; i++) {
            xData[i] = functionPoints[i][0];
            yData[i] = functionPoints[i][1];
        }

        double a = 1.0; // Initial guess for 'a'
        double b = 1.0; // Initial guess for 'b'
        double learningRate = 0.001; // Adjust the learning rate
        int maxIterations = 10000; // Maximum number of iterations

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            double gradientA = 0.0;
            double gradientB = 0.0;

            for (int i = 0; i < xData.length; i++) {
                // has to be positive
                assert xData[i] > 0;
                double sqrtX = Math.sqrt(xData[i]);
                double yModel = a / sqrtX + b;

                double error = yData[i] - yModel;

                // Partial derivatives of the function with respect to 'a' and 'b'
                double partialA = 1.0 / sqrtX; // Derivative with respect to 'a'
                double partialB = 1.0; // Derivative with respect to 'b'

                gradientA += -2 * error * partialA; // Update gradient for 'a'
                gradientB += -2 * error * partialB; // Update gradient for 'b'
            }

            // Update parameters using the gradients
            a -= learningRate * gradientA;
            b -= learningRate * gradientB;
        }

        return new double[]{ a, b };
    }


}

