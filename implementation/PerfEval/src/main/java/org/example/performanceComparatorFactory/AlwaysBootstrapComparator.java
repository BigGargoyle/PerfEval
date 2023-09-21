package org.example.performanceComparatorFactory;

import org.example.measurementFactory.UniversalTimeUnit;

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
