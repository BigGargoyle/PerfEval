package org.example.performanceComparatorFactory;

import org.example.Samples;
import org.example.evaluation.MeasurementComparisonRecord;

public interface PerformanceComparator {
    MeasurementComparisonRecord compareSets(Samples oldSamples, Samples newSamples);

}
