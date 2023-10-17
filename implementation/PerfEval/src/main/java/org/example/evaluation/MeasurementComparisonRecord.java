package org.example.evaluation;

import org.example.Samples;
import org.example.performanceComparatorFactory.ComparisonResult;

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
