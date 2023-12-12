package cz.cuni.mff.d3s.perfeval.performancecomparators;

import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.fitting.leastsquares.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.StatUtils;
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

    public int getMinSampleCount(double[][] sampleSet1, double[][] sampleSet2, double confidenceLevel, double maxCIWidth) {
        double[][] functionPoints = calcFunctionPoints(sampleSet1, sampleSet2, confidenceLevel);
        // y = b * 1 / sqrt(a * x) + c
        double[] abcParameters = calcFunctionParameters(functionPoints);
        return calcMinSampleCountFromFunction(abcParameters, maxCIWidth);
    }

    private int calcMinSampleCountFromFunction(double[] abcParameters, double maxCIWidth) {
        double a = abcParameters[0];
        double b = abcParameters[1];
        double c = abcParameters[2];

        // y = b *  1 / sqrt(a * x) + c
        // x = 1 / (a * (y - c) ^ 2 / b ^ 2)
        double x = 1 / (a * (maxCIWidth - c) * (maxCIWidth - c) / (b * b));
        return (int) Math.ceil(x);
    }

    private static double[][] calcFunctionPoints(double[][] sampleSet1, double[][] sampleSet2, double confidenceLevel) {
        int pointsCount = Math.min(sampleSet1.length, sampleSet2.length);
        double[][] functionPoints = new double[pointsCount][];
        for (int x = 0; x < pointsCount; x++) {
            double[][] set1 = new double[x + 1][];
            double[][] set2 = new double[x + 1][];
            for (int i = 0; i <= x; i++) {
                set1[i] = sampleSet1[i];
                set2[i] = sampleSet2[i];
            }
            double[] bootstrapSample = createBootstrapSample(set1, set2, DEFAULT_BOOTSTRAP_SAMPLE_COUNT);
            double[] bootstrapInterval = calcCIInterval(bootstrapSample, confidenceLevel);
            assert bootstrapInterval.length == 2;
            double y = StatUtils.mean(bootstrapInterval) / (bootstrapInterval[1] - bootstrapInterval[0]);
            functionPoints[x] = new double[]{x, y};
        }
        return functionPoints;
    }

    private static double[] calcFunctionParameters(double[][] functionPoints) {
        // Extract x and y data from functionPoints
        double[] xData = new double[functionPoints.length];
        double[] yData = new double[functionPoints.length];

        for (int i = 0; i < functionPoints.length; i++) {
            xData[i] = functionPoints[i][0];
            yData[i] = functionPoints[i][1];
        }

        // Initial guess for parameters a, b, and c
        double[] initialGuess = {1.0, 1.0, 1.0}; // You can adjust these initial values

        // Define the function y = b * (1 / sqrt(a * x)) + c
        MultivariateVectorFunction func = params -> {
            double a = params[0];
            double b = params[1];
            double c = params[2];

            double[] residuals = new double[yData.length];
            RealMatrix jacobian = new Array2DRowRealMatrix(yData.length, 3);

            for (int i = 0; i < xData.length; i++) {
                double sqrtTerm = Math.sqrt(a * xData[i]);
                double yModel = b / sqrtTerm + c; // Corrected yModel calculation
                residuals[i] = yData[i] - yModel;

                // Jacobian matrix calculation
                jacobian.setEntry(i, 0, -b / (2 * a * sqrtTerm)); // Derivative with respect to 'a'
                jacobian.setEntry(i, 1, 1 / sqrtTerm); // Derivative with respect to 'b'
                jacobian.setEntry(i, 2, 1.0); // Derivative with respect to 'c'
            }

            return residuals;
        };

        LeastSquaresOptimizer optimizer = new LevenbergMarquardtOptimizer();

        // Build the least squares problem
        LeastSquaresBuilder builder = new LeastSquaresBuilder()
                .model((MultivariateJacobianFunction) func)
                .target(new ArrayRealVector(yData))
                .start(new ArrayRealVector(initialGuess))
                .lazyEvaluation(false)
                .maxEvaluations(1000)
                .maxIterations(100);

        // Solve the least squares problem
        RealVector optimizedParameters = optimizer.optimize(builder.build()).getPoint();

        return optimizedParameters.toArray();
    }
}

