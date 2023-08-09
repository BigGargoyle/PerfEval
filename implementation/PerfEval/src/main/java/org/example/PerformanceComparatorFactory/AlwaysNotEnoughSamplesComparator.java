package org.example.PerformanceComparatorFactory;

import java.util.List;
/**
 * Implementation of IPerformanceComparator that always evaluates that there are not enough samples in the sets
 */
public class AlwaysNotEnoughSamplesComparator implements IPerformanceComparator {
    @Override
    public ComparisonResult CompareSets(double[] newSet, double[] oldSet) {
        return ComparisonResult.NotEnoughSamples;
    }

    @Override
    public ComparisonResult GetLastComparisonResult() {
        return ComparisonResult.NotEnoughSamples;
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
