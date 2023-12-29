package cz.cuni.mff.d3s.perfeval.performancecomparators;

import static cz.cuni.mff.d3s.perfeval.performancecomparators.HierarchicalBootstrap.evaluateCIInterval;

public class NonparametricTest implements StatisticTest {
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
    public NonparametricTest(double critValue, int bootstrapSampleCount) {
        this.critValue = critValue;
        this.bootstrapSampleCount = bootstrapSampleCount;
    }
    public NonparametricTest(double critValue) {
        this.critValue = critValue;
        this.bootstrapSampleCount = DEFAULT_BOOTSTRAP_SAMPLE_COUNT;
    }
    @Override
    public double[] calcCIInterval(double[][] sampleSet1, double[][] sampleSet2) {
        return evaluateCIInterval(sampleSet1, sampleSet2, 1 - critValue, bootstrapSampleCount);
    }

    @Override
    public int calcMinSampleCount(double[][] sampleSet1, double[][] sampleSet2, double maxCIWidth) {
        return Math.max(HierarchicalBootstrap.getMinSampleCount(sampleSet1,1-critValue, maxCIWidth, bootstrapSampleCount), HierarchicalBootstrap.getMinSampleCount(sampleSet2,1-critValue, maxCIWidth, bootstrapSampleCount));
    }
}
