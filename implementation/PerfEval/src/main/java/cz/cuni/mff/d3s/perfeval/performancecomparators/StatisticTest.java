package cz.cuni.mff.d3s.perfeval.performancecomparators;

public interface StatisticTest {
    double[] calcCIInterval(double[][] sampleSet1, double[][] sampleSet2);
    int calcMinSampleCount(double[][] sampleSet1, double[][] sampleSet2, double maxCIWidth);
}
