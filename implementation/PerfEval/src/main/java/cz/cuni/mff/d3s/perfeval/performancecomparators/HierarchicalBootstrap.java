package cz.cuni.mff.d3s.perfeval.performancecomparators;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.Random;

public class HierarchicalBootstrap {
    double critValue;
    int bootstrapSampleCount;

    public HierarchicalBootstrap(double critValue, int bootstrapSampleCount) {
        this.critValue = critValue;
        this.bootstrapSampleCount = bootstrapSampleCount;
    }

    public boolean evaluateBootstrap(double[][] sampleSet1, double[][] sampleSet2) {
        double[] bootstrapSample = createBootstrapSample(sampleSet1, sampleSet2);
        double[] bootstrapInterval = calcCIInterval(bootstrapSample, 1-critValue);
        assert bootstrapInterval.length == 2;
        return bootstrapInterval[0] <= 0 && bootstrapInterval[1] >= 0;
    }

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

    public static double[] calcCIInterval(double[] data, double confidenceLevel){
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

        return new double[] {lowerBound, upperBound};
    }

}
