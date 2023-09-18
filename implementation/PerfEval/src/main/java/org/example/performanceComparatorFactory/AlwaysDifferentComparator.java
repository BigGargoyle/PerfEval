package org.example.performanceComparatorFactory;

import org.example.measurementFactory.UniversalTimeUnit;

import java.util.List;
/**
 * Implementation of IPerformanceComparator that always evaluate sets as they are from different distribution
 */
public class AlwaysDifferentComparator implements IPerformanceComparator {
    @Override
    public ComparisonResult compareSets(List<UniversalTimeUnit> newSet, List<UniversalTimeUnit> oldSet) {
        return ComparisonResult.DifferentDistribution;
    }

    @Override
    public ComparisonResult getLastComparisonResult() {
        return ComparisonResult.DifferentDistribution;
    }

    @Override
    public int getMinSampleCount() {
        return 0;
    }
}
