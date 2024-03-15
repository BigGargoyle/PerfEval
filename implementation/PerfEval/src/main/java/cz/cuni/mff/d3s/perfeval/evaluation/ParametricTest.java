package cz.cuni.mff.d3s.perfeval.evaluation;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class ParametricTest implements StatisticTest {
    double critValue;
    public ParametricTest(double critValue) {
        this.critValue = critValue;
    }
    @Override
    public double[] calcCIInterval(double[][] sampleSet1, double[][] sampleSet2) {
        double[] samples1 = calcMean(sampleSet1);
        double[] samples2 = calcMean(sampleSet2);
        if(samples1.length == 1) {
            samples1 = sampleSet1[0];
        }
        if(samples2.length == 1) {
            samples2 = sampleSet2[0];
        }
        /*
        // Calculate degrees of freedom

        int df = Math.min(samples1.length - 1, samples2.length - 1);

        if(df <= 0) {
            if(samples1.length == 1 || samples2.length == 1) {
                return samples1[0]<=samples2[0] ? new double[]{samples1[0], samples2[0]} : new double[]{samples2[0], samples1[0]};
            }
            //throw new IllegalArgumentException("Samples are too small to perform the test.");
        }

        // Calculate critical value for the desired confidence level
        TDistribution tDistribution = new TDistribution(df);

        //  there should be critValue, because it is the probability of the value being in the confidence interval
        //  p-quantile is the tighter than (1-p)-quantile
        double inverseCumulativeProbability = tDistribution.inverseCumulativeProbability(critValue);

        // Calculate mean difference
        //double meanDifference = calculateMeanDifference(samples1, samples2);
        double meanDifference = StatUtils.mean(samples1) - StatUtils.mean(samples2);

        // Calculate pooled variance (assuming equal variances for simplicity)
        //double pooledVar = calculatePooledVariance(samples1, samples2);
        double pooledVar = ((samples1.length - 1) * StatUtils.variance(samples1) + (samples2.length - 1)
                * StatUtils.variance(samples2)) / (samples1.length + samples2.length - 2);

        // Calculate standard error
        double stdError = Math.sqrt((pooledVar / samples1.length) + (pooledVar / samples2.length));

        // Calculate margin of error using critical value and standard error
        double marginOfError = inverseCumulativeProbability * stdError;

        // Calculate confidence interval bounds
        double lowerBound = meanDifference - marginOfError;
        double upperBound = meanDifference + marginOfError;*/

        // Calculate sample statistics
        DescriptiveStatistics stats1 = new DescriptiveStatistics(samples1);
        DescriptiveStatistics stats2 = new DescriptiveStatistics(samples2);

        // Sample sizes
        int n1 = (int) stats1.getN();
        int n2 = (int) stats2.getN();

        if(n1 == 1 || n2 == 1) {
            return samples1[0]<=samples2[0] ? new double[]{samples1[0], samples2[0]} : new double[]{samples2[0], samples1[0]};
        }

        // Sample means
        double mean1 = stats1.getMean();
        double mean2 = stats2.getMean();

        // Sample variances
        double var1 = stats1.getVariance();
        double var2 = stats2.getVariance();

        // Degrees of freedom
        double df = welchsDegreesOfFreedom(var1, var2, n1, n2);

        /*if(df <= 0) {
            if(samples1.length == 1 || samples2.length == 1) {
                return samples1[0]<=samples2[0] ? new double[]{samples1[0], samples2[0]} : new double[]{samples2[0], samples1[0]};
            }
            //throw new IllegalArgumentException("Samples are too small to perform the test.");
        }*/

        // Significance level (e.g., 95% confidence interval)
        double alpha = critValue;

        // Calculate t-critical value
        TDistribution tDist = new TDistribution(df);
        System.out.println("df: "+df);
        double tCritical = tDist.inverseCumulativeProbability(1 - alpha / 2);

        // Calculate margin of error
        double marginOfError = tCritical * Math.sqrt(var1 / n1 + var2 / n2);

        // Calculate confidence interval bounds
        double lowerBound = (mean1 - mean2) - marginOfError;
        double upperBound = (mean1 - mean2) + marginOfError;

        return lowerBound <= upperBound ? new double[]{lowerBound, upperBound} : new double[]{upperBound, lowerBound};
        //return new EvaluatorResult(lowerBound, upperBound, Math.abs(pValue) <= critValue);
    }

    // Degrees of freedom for Welch's t-test
    private static double welchsDegreesOfFreedom(double var1, double var2, int n1, int n2) {
        double numerator = Math.pow((var1 / n1 + var2 / n2), 2);
        double denominator = Math.pow((var1 * var1) / (Math.pow(n1, 2) * (n1 - 1)) + (var2 * var2) / (Math.pow(n2, 2) * (n2 - 1)), 2);
        return numerator / denominator;
    }

    @Override
    public int calcMinSampleCount(double[][] sampleSet1, double[][] sampleSet2, double maxCIWidth) {
        return Math.max(
                ArrayUtilities.calcMinSampleCount(calcMean(sampleSet1), 1 - critValue, maxCIWidth),
                ArrayUtilities.calcMinSampleCount(calcMean(sampleSet2), 1 - critValue, maxCIWidth)
        );
    }

    private double[] calcMean(double[][] sampleSet) {
        double[] means = new double[sampleSet.length];
        for (int i = 0; i < sampleSet.length; i++) {
            means[i] = StatUtils.mean(sampleSet[i]);
        }
        return means;
    }

}
