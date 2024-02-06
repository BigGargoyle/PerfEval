package cz.cuni.mff.d3s.perfeval.printers;

import cz.cuni.mff.d3s.perfeval.evaluation.ComparisonResult;
import cz.cuni.mff.d3s.perfeval.Samples;
import org.codehaus.jackson.annotate.JsonProperty;

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
        @JsonProperty("oldAverage") double oldAverage,
        @JsonProperty("newAverage") double newAverage,
        @JsonProperty("performanceChange") double performanceChange,
        @JsonProperty("comparisonResult") ComparisonResult comparisonResult,
        @JsonProperty("testVerdict") boolean testVerdict,
        @JsonProperty("minSampleCount") int minSampleCount,
        @JsonProperty("oldSamples") Samples oldSamples,
        @JsonProperty("newSamples") Samples newSamples
) {
    public String newAverageToString() {
        return newSamples.getMetric().valueToString(newAverage);
    }

    public String oldAverageToString() {
        return oldSamples.getMetric().valueToString(oldAverage);
    }
}
