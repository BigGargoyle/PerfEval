package org.example.PerformanceComparatorFactory;

import java.util.List;

public interface IPerformanceComparator {
    /**
     *
     * @param newSet list of new measured values
     * @param oldSet list of old measured values
     * @return result of distribution comparison
     */
    public ComparisonResult CompareSets(double[] newSet, double[] oldSet);

    /**
     *
     * @return result of the last comparison
     */
    public ComparisonResult GetLastComparisonResult();

    /**
     * Just in case of NotEnoughSamples
     * @return Minimum count of samples needed to determine if both of sets have the same distribution.
     */
    public int GetMinSampleCount();

    /**
     * Just in case of bootstrap.
     * @return The width of confidence interval relative to an average of values.
     */
    public double GetConfidenceIntervalWidth();
}
