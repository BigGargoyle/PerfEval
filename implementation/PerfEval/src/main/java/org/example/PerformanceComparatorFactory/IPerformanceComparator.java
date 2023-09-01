package org.example.PerformanceComparatorFactory;

import org.example.MeasurementFactory.UniversalTimeUnit;

import java.util.List;

public interface IPerformanceComparator {
    /**
     * It is needed to compare two IMeasurement between themselves (their Lists od UniversalTimeUnits)
     *
     * @param newSet list of new measured values
     * @param oldSet list of old measured values
     * @return result of distribution comparison
     */
    ComparisonResult CompareSets(List<UniversalTimeUnit> newSet, List<UniversalTimeUnit> oldSet);

    /**
     * It is needed to get the information about last comparison
     *
     * @return result of the last comparison
     */
    ComparisonResult GetLastComparisonResult();

    /**
     * Just in case of NotEnoughSamples
     *
     * @return Minimum count of samples needed to determine if both of sets have the same distribution.
     */
    int GetMinSampleCount();

}
