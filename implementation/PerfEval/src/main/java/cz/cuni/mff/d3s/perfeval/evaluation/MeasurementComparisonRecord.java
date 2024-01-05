package cz.cuni.mff.d3s.perfeval.evaluation;

import cz.cuni.mff.d3s.perfeval.performancecomparators.ComparisonResult;
import cz.cuni.mff.d3s.perfeval.Samples;

/**
 * Record for storing results of comparison of two sets of samples.
 * @param oldAverage average of old samples
 * @param newAverage average of new samples
 * @param performanceChange percentage change of performance
 * @param comparisonResult result of comparison
 * @param testVerdict verdict of test (true if performance change is acceptable, false otherwise)
 * @param minSampleCount minimal number of samples needed for comparison
 * @param oldSamples samples of newer version
 * @param newSamples samples of older version
 */
public record MeasurementComparisonRecord(
        double oldAverage,
        double newAverage,
        double performanceChange,
        ComparisonResult comparisonResult,
        boolean testVerdict,
        int minSampleCount,
        Samples oldSamples,
        Samples newSamples
) {
}
