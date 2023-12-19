package cz.cuni.mff.d3s.perfeval.performancecomparators;

import org.apache.commons.math3.util.Pair;

public interface StatisticEvaluator {
    EvaluatorResult calcCIInterval(double[][] sampleSet1, double[][] sampleSet2);
    int calcMinSampleCount(double[][] sampleSet1, double[][] sampleSet2, double maxCIWidth);
}
