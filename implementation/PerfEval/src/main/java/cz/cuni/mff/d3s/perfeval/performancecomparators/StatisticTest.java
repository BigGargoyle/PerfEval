package cz.cuni.mff.d3s.perfeval.performancecomparators;

public interface StatisticTest {
    EvaluatorResult calcCIInterval(double[][] sampleSet1, double[][] sampleSet2);
    int calcMinSampleCount(double[][] sampleSet1, double[][] sampleSet2, double maxCIWidth);
}
