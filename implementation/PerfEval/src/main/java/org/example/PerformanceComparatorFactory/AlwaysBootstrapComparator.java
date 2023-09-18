package org.example.PerformanceComparatorFactory;

import org.example.MeasurementFactory.UniversalTimeUnit;

import java.util.List;

/**
 * Implementation of IPerformanceComparator that always "do bootstrap"
 */
public class AlwaysBootstrapComparator implements IPerformanceComparator {

    @Override
    public ComparisonResult compareSets(List<UniversalTimeUnit> newSet, List<UniversalTimeUnit> oldSet) {
        return ComparisonResult.Bootstrap;
    }

    @Override
    public ComparisonResult getLastComparisonResult() {
        return ComparisonResult.Bootstrap;
    }

    @Override
    public int getMinSampleCount() {
        return 0;
    }
}
