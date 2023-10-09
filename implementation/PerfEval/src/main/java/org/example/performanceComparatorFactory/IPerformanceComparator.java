package org.example.performanceComparatorFactory;

import org.example.evaluation.MeasurementComparisonRecord;
import org.example.measurementFactory.Measurement;

public interface IPerformanceComparator {
    MeasurementComparisonRecord compareSets(Measurement oldMeasurement, Measurement newMeasurement);

}
