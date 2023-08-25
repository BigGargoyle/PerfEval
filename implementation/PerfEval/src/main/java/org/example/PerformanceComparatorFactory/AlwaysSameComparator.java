package org.example.PerformanceComparatorFactory;

import org.example.MeasurementFactory.UniversalTimeUnit;

import java.util.List;
/**
 * Implementation of IPerformanceComparator that always evaluates that sets are from the same distribution
 */
public class AlwaysSameComparator implements IPerformanceComparator {
    @Override
    public ComparisonResult CompareSets(List<UniversalTimeUnit> newSet, List<UniversalTimeUnit> oldSet) {
        return ComparisonResult.SameDistribution;
    }

    @Override
    public ComparisonResult GetLastComparisonResult() {
        return ComparisonResult.SameDistribution;
    }

    @Override
    public int GetMinSampleCount() {
        return 0;
    }

}
