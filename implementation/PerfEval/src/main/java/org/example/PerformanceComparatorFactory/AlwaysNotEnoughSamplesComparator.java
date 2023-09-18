package org.example.PerformanceComparatorFactory;

import org.example.MeasurementFactory.UniversalTimeUnit;

import java.util.List;
/**
 * Implementation of IPerformanceComparator that always evaluates that there are not enough samples in the sets
 */
public class AlwaysNotEnoughSamplesComparator implements IPerformanceComparator {
    @Override
    public ComparisonResult compareSets(List<UniversalTimeUnit> newSet, List<UniversalTimeUnit> oldSet) {
        return ComparisonResult.NotEnoughSamples;
    }

    @Override
    public ComparisonResult getLastComparisonResult() {
        return ComparisonResult.NotEnoughSamples;
    }

    @Override
    public int getMinSampleCount() {
        return 0;
    }
}
