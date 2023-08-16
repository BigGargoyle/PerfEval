package org.example.PerformanceComparatorFactory;

import org.example.MeasurementFactory.UniversalTimeUnit;

import java.util.List;

/**
 * Implementation of IPerformanceComparator that always "do bootstrap"
 */
public class AlwaysBootstrapComparator implements IPerformanceComparator{

    @Override
    public ComparisonResult CompareSets(List<UniversalTimeUnit> newSet, List<UniversalTimeUnit> oldSet) {
        return ComparisonResult.Bootstrap;
    }

    @Override
    public ComparisonResult GetLastComparisonResult() {
        return ComparisonResult.Bootstrap;
    }

    @Override
    public int GetMinSampleCount() {
        return 0;
    }
}
