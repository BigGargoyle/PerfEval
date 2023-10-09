package org.example.evaluation;

import org.example.measurementFactory.Measurement;
import org.example.measurementFactory.UniversalTimeUnit;
import org.example.performanceComparatorFactory.ComparisonResult;

public record MeasurementComparisonRecord(
        UniversalTimeUnit oldAverage,
        UniversalTimeUnit newAverage,
        double performanceChange,
        ComparisonResult comparisonResult,
        boolean testVerdict,
        int minSampleCount,
        Measurement oldMeasurement,
        Measurement newMeasurement
) {
}
