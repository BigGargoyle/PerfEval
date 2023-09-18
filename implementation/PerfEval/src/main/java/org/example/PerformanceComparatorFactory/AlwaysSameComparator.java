package org.example.PerformanceComparatorFactory;

import org.example.MeasurementFactory.UniversalTimeUnit;

import java.util.List;
/**
 * Implementation of IPerformanceComparator that always evaluates that sets are from the same distribution
 */
public class AlwaysSameComparator implements IPerformanceComparator {
    @Override
    public ComparisonResult compareSets(List<UniversalTimeUnit> newSet, List<UniversalTimeUnit> oldSet) {
        return ComparisonResult.SameDistribution;
    }

    @Override
    public ComparisonResult getLastComparisonResult() {
        return ComparisonResult.SameDistribution;
    }

    @Override
    public int getMinSampleCount() {
        return 0;
    }

}
