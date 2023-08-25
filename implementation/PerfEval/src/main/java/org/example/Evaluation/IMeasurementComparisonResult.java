package org.example.Evaluation;

import org.example.MeasurementFactory.IMeasurement;
import org.example.PerformanceComparatorFactory.ComparisonResult;

public interface IMeasurementComparisonResult {
    String getName();
    double getNewAvg();
    double getOldAvg();
    double getChange();
    ComparisonResult getComparisonResult();
    boolean getComparisonVerdict();
    int getMinSampleCount();
    IMeasurement getOldMeasurement();
    IMeasurement getNewMeasurement();
}
