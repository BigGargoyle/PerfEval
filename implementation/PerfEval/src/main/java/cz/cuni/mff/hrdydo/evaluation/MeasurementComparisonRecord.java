package cz.cuni.mff.hrdydo.evaluation;

import cz.cuni.mff.hrdydo.Samples;
import cz.cuni.mff.hrdydo.performanceComparatorFactory.ComparisonResult;

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
