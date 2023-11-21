package cz.cuni.mff.d3s.perfeval.evaluation;

import cz.cuni.mff.d3s.perfeval.performancecomparators.ComparisonResult;
import cz.cuni.mff.d3s.perfeval.Samples;

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
