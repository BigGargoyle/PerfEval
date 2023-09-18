package org.example.Evaluation;

import org.example.MeasurementFactory.Measurement;
import org.example.PerformanceComparatorFactory.ComparisonResult;

public interface IMeasurementComparisonResult {
    /**
     * @return name of benchmark test that is represented by this instance
     */
    String getName();

    /**
     * @return average measured time of the newer one from benchmark tests that were compared
     */
    double getNewAvg();

    /**
     * @return average measured time of the older one from benchmark tests that were compared
     */
    double getOldAvg();

    /**
     * @return change of performance between newer and older performance IMeasurements
     */
    double getChange();

    /**
     * @return true result of comparison (NotEnoughSamples, Bootstrap, etc.), can give some extra info in case of ComparisonVerdict is false
     */
    ComparisonResult getComparisonResult();

    /**
     * @return true - if the performance gone better or is not significantly worse than before, false - otherwise
     */
    boolean getComparisonVerdict();

    /**
     * @return minimal samples in each of IMeasurement which are needed to have statistically significant result
     */
    int getMinSampleCount();

    /**
     * @return the instance of the older one IMeasurement that was compared with the newer one IMeasurement
     */
    Measurement getOldMeasurement();

    /**
     * @return the instance of the newer one IMeasurement that was compared with the older one IMeasurement
     */
    Measurement getNewMeasurement();
}
