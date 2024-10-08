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

        if(var1 == 0 && var2 == 0) {
            return mean1<=mean2 ? new double[]{mean1, mean2} : new double[]{mean2, mean1};
        }

        // Degrees of freedom
        double df = welchsDegreesOfFreedom(var1, var2, n1, n2);

        // Calculate t-critical value
        TDistribution tDist = new TDistribution(df);
        double tCritical = tDist.inverseCumulativeProbability(critValue / 2);

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

        double varOverN1 = var1/n1;
        double varOverN2 = var2/n2;

        double numerator = Math.pow(varOverN1 + varOverN2, 2);
        double denominator = varOverN1*varOverN1/(n1-1) + varOverN2*varOverN2/(n2-1);

        return numerator/denominator;
    }

    @Override
    public int calcMinSampleCount(double[][] sampleSet1, double[][] sampleSet2, double maxCIWidth) {
        return Math.max(
                this.minSampleCount(calcMean(sampleSet1), maxCIWidth),
                this.minSampleCount(calcMean(sampleSet2), maxCIWidth)
        );
    }

    private int minSampleCount(double[] sampleSet, double maxCIWidth) {
        DescriptiveStatistics stats = new DescriptiveStatistics(sampleSet);
        int n = (int) stats.getN();
        double mean = stats.getMean();
        double sd = stats.getStandardDeviation();
        TDistribution tDist = new TDistribution(n - 1);
        double tCritical = tDist.inverseCumulativeProbability(1 - (critValue) / 2);
        double sampleCount = Math.pow(tCritical * sd / (mean * maxCIWidth), 2);
        return (int) Math.ceil(sampleCount);
    }

    private static double[] calcMean(double[][] sampleSet) {
        double[] means = new double[sampleSet.length];
        for (int i = 0; i < sampleSet.length; i++) {
            means[i] = StatUtils.mean(sampleSet[i]);
        }
        return means;
    }

}

