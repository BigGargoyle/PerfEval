package org.example.PerformanceComparatorFactory;

import java.util.List;
/**
 * Implementation of IPerformanceComparator that always evaluate sets as they are from different distribution
 */
public class AlwaysDifferentComparator implements IPerformanceComparator{
    @Override
    public ComparisonResult CompareSets(double[] newSet, double[] oldSet) {
        return ComparisonResult.DifferentDistribution;
    }

    @Override
    public ComparisonResult GetLastComparisonResult() {
        return ComparisonResult.DifferentDistribution;
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
