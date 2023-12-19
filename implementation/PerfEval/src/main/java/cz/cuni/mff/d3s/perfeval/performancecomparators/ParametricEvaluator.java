package cz.cuni.mff.d3s.perfeval.performancecomparators;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.inference.TTest;

public class ParametricEvaluator implements StatisticEvaluator{
    double critValue;
    public ParametricEvaluator(double critValue) {
        this.critValue = critValue;
    }
    @Override
    public EvaluatorResult calcCIInterval(double[][] sampleSet1, double[][] sampleSet2) {
        double[] samples1 = calcMean(sampleSet1);
        double[] samples2 = calcMean(sampleSet2);
        TTest tTest = new TTest();
        double pValue = Math.abs(tTest.t(samples1, samples2));
        // Calculate degrees of freedom
        int df = Math.min(samples1.length - 1, samples2.length - 1);

        // Calculate critical value for the desired confidence level
        double confidenceLevel = 1-critValue; // Set your desired confidence level
        TDistribution tDistribution = new TDistribution(df);
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
        double upperBound = meanDifference + marginOfError;

        return new EvaluatorResult(lowerBound, upperBound, Math.abs(pValue) <= critValue);
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
