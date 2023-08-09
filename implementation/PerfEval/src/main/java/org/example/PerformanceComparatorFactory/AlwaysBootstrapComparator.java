package org.example.PerformanceComparatorFactory;

import java.util.List;

/**
 * Implementation of IPerformanceComparator that always "do bootstrap"
 */
public class AlwaysBootstrapComparator implements IPerformanceComparator{

    @Override
    public ComparisonResult CompareSets(double[] newSet, double[] oldSet) {
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

    @Override
    public double GetConfidenceIntervalWidth() {
        return 0;
    }
}
