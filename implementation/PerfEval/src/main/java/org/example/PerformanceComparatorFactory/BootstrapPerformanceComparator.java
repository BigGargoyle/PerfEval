package org.example.PerformanceComparatorFactory;

import java.util.List;

public class BootstrapPerformanceComparator implements IPerformanceComparator{
    @Override
    public ComparisonResult CompareSets(double[] newSet, double[] oldSet) {
        return null;
    }

    @Override
    public ComparisonResult GetLastComparisonResult() {
        return null;
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
