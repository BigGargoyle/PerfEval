package org.example.PerformanceComparatorFactory;

import org.example.MeasurementFactory.UniversalTimeUnit;

import java.util.List;

public interface IPerformanceComparator {
    /**
     *
     * @param newSet list of new measured values
     * @param oldSet list of old measured values
     * @return result of distribution comparison
     */
    ComparisonResult CompareSets(List<UniversalTimeUnit> newSet, List<UniversalTimeUnit> oldSet);

    /**
     *
     * @return result of the last comparison
     */
    ComparisonResult GetLastComparisonResult();

    /**
     * Just in case of NotEnoughSamples
     * @return Minimum count of samples needed to determine if both of sets have the same distribution.
     */
    int GetMinSampleCount();

}
