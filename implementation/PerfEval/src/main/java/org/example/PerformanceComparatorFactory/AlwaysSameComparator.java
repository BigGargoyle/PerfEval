package org.example.PerformanceComparatorFactory;

import java.util.List;
/**
 * Implementation of IPerformanceComparator that always evaluates that sets are from the same distribution
 */
public class AlwaysSameComparator implements IPerformanceComparator{
    @Override
    public ComparisonResult CompareSets(double[] newSet, double[] oldSet) {
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

    @Override
    public double GetConfidenceIntervalWidth() {
        return 0;
    }
}
