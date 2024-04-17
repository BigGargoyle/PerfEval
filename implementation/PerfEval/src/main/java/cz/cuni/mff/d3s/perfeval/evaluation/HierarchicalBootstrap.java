package cz.cuni.mff.d3s.perfeval.evaluation;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.SimpleCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.util.Random;

import static org.apache.commons.math3.stat.StatUtils.mean;

/**
 * Class for performing hierarchical bootstrap.
 */
public class HierarchicalBootstrap {
    static int DEFAULT_BOOTSTRAP_SAMPLE_COUNT = 1_000;

    /**
     * Performs hierarchical bootstrap.
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
        int n = sampleSet1.length;
        int m = sampleSet2.length;
        //create bootstrapSampleCount of bootstrap samples
        for (int i = 0; i < bootstrapSampleCount; i++) {
            //create bootstrap sample of sampleSet1 and sampleSet2
            double[] sample1 = new double[n];
            for (int j = 0; j < n; j++) {
                sample1[j] = calcBoostrapValue(sampleSet1, 1, random);
            }
            double[] sample2 = new double[m];
            for (int k = 0; k < m; k++) {
                sample2[k] = calcBoostrapValue(sampleSet2, 1, random);
            }
            result[i] = mean(sample1) - mean(sample2);
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
        return mean(result);
    }

    private static double createBootstrapOf1DArray(double[] sampleSet, int bootstrapSampleCount, Random random) {
        double[] result = new double[bootstrapSampleCount];
        for (int i = 0; i < bootstrapSampleCount; i++) {
            //create bootstrapped dataset
            double[] bootstrappedDataset = createBootstrappedDataset(sampleSet, random);
            result[i] = mean(bootstrappedDataset);
        }
        return mean(result);
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
        return lowerBound <= upperBound ? new double[]{lowerBound, upperBound} : new double[]{upperBound, lowerBound};
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
        for (int x = 1; x <= pointsCount; x++) {
            double[][] set = new double[x][];
            System.arraycopy(sampleSet, 0, set, 0, x);
            double[] bootstrapSample = createBootstrapSample(set, random, bootstrapSampleCount);
            double[] bootstrapInterval = calcCIInterval(bootstrapSample, confidenceLevel);
            assert bootstrapInterval.length == 2;
            double y = (bootstrapInterval[1] - bootstrapInterval[0])/ mean(bootstrapSample);
            functionPoints[x-1] = new double[]{x, y};
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

        // Perform curve fitting
        ParametricUnivariateFunction function = new ParametricUnivariateFunction() {
            @Override
            public double value(double x, double... parameters) {
                // Function: y = a / sqrt(x)
                double a = parameters[0];
                return a / Math.sqrt(x);
            }

            @Override
            public double[] gradient(double x, double... parameters) {
                //double a = parameters[0];
                double[] gradient = new double[1];
                gradient[0] = 1 / Math.sqrt(x);
                return gradient;
            }
        };

        WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < xData.length; i++) {
            obs.add(new WeightedObservedPoint(1, xData[i], yData[i]));
        }

        // Initial guess for the parameter
        double[] initialGuess = {1.0};

        SimpleCurveFitter fitter = SimpleCurveFitter.create(function, initialGuess);
        fitter.withStartPoint(initialGuess);

        double[] bestFitParameters = fitter.fit(obs.toList());
        double bestFitA = bestFitParameters[0];

        return new double[]{ bestFitA, 0 };
    }

}

