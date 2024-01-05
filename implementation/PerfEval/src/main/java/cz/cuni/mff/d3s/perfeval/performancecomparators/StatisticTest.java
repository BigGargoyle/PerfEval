package cz.cuni.mff.d3s.perfeval.performancecomparators;

/**
 * Interface representing statistical test.
 */
public interface StatisticTest {
    /**
     * Calculates confidence interval for two sets of samples.
     * @param sampleSet1 first set of samples
     * @param sampleSet2 second set of samples
     * @return confidence interval for two sets of samples
     */
    double[] calcCIInterval(double[][] sampleSet1, double[][] sampleSet2);

    /**
     * Calculates minimal sample count for two sets of samples.
     * @param sampleSet1 first set of samples
     * @param sampleSet2 second set of samples
     * @param maxCIWidth maximal width of confidence interval
     * @return minimal sample count for two sets of samples to have confidence interval with given width
     */
    int calcMinSampleCount(double[][] sampleSet1, double[][] sampleSet2, double maxCIWidth);
}
