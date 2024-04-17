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
        @JsonProperty("upperCIBound") double upperCIBound,
        @JsonProperty("lowerCIBound") double lowerCIBound,
        @JsonProperty("performanceChange") double performanceChange,
        @JsonProperty("comparisonResult") ComparisonResult comparisonResult,
        @JsonProperty("testVerdict") boolean testVerdict,
        @JsonProperty("minSampleCount") int minSampleCount,
        @JsonProperty("oldSamples") Samples oldSamples,
        @JsonProperty("newSamples") Samples newSamples
) {
    public String newAverageToString() {
        return newSamples.getMetric().circleciValueToString(newAverage, new double[]{lowerCIBound, upperCIBound});
    }

    public String oldAverageToString() {
        return oldSamples.getMetric().circleciValueToString(oldAverage, new double[]{lowerCIBound, upperCIBound});
    }
    private final static int TEN_THOUSAND = 10000;
    private final static int FIVE = 5;
    public String lowerCIBoundToString() {
        if(Math.abs(lowerCIBound) > TEN_THOUSAND){
            return String.format("%." + FIVE + "e", lowerCIBound);
        }
        return String.format("%+.3f", lowerCIBound);
    }

    public String upperCIBoundToString() {
        if(Math.abs(upperCIBound) > TEN_THOUSAND){
            return String.format("%." + FIVE + "e", upperCIBound);
        }
        return String.format("%+.3f", upperCIBound);
    }

    public String changeToString() {
        return String.format("%+.2f", performanceChange);
    }
}
