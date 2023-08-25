package org.example.Evaluation;

import org.example.MeasurementFactory.IMeasurement;
import org.example.MeasurementFactory.UniversalTimeUnit;
import org.example.PerformanceComparatorFactory.Bootstrap;
import org.example.PerformanceComparatorFactory.ComparisonResult;
import org.example.PerformanceComparatorFactory.IPerformanceComparator;

import java.util.List;

public class MeasurementComparisonResult implements IMeasurementComparisonResult {
    private final double newAverage;
    private final double oldAverage;
    private final double performanceChange;
    // private ComparisonResult comparisonResult;
    private final boolean TestVerdict;
    private final IPerformanceComparator performanceComparator;
    private final IMeasurement oldMeasurement;
    private final IMeasurement newMeasurement;

    @Override
    public String getName() {
        return newMeasurement.getName();
    }

    @Override
    public double getNewAvg() {
        return newAverage;
    }

    @Override
    public double getOldAvg() {
        return oldAverage;
    }

    @Override
    public double getChange() {
        return performanceChange;
    }

    @Override
    public ComparisonResult getComparisonResult() {
        return performanceComparator.GetLastComparisonResult();
    }

    @Override
    public boolean getComparisonVerdict() {
        return TestVerdict;
    }

    @Override
    public int getMinSampleCount() {
        return performanceComparator.GetMinSampleCount();
    }

    @Override
    public IMeasurement getOldMeasurement() {
        return oldMeasurement;
    }

    @Override
    public IMeasurement getNewMeasurement() {
        return newMeasurement;
    }

    /**
     * Compares _newMeasurement and _oldMeasurement from arguments and then constructed instance is behaving as result of this comparison (IMeasurementComparisonResult)
     *
     * @param critValue              pValue that is critical for statistical tests
     * @param _newMeasurement        an instance of IMeasured that is viewed as current state of test
     * @param _oldMeasurement        an instance of IMeasured that is viewed as previous state of test
     * @param _performanceComparator instance od IPerformanceComparator that is used for comparing IMeasurements from arguments
     */

    public MeasurementComparisonResult(double critValue, IMeasurement _newMeasurement, IMeasurement _oldMeasurement,
                                       IPerformanceComparator _performanceComparator) {
        oldMeasurement = _oldMeasurement;
        newMeasurement = _newMeasurement;
        // performanceComparator = ComparatorIndustry.GetComparator(critValue, maxCIWidth, maxTestTime);
        performanceComparator = _performanceComparator;
        performanceComparator.CompareSets(newMeasurement.getMeasuredTimes(), oldMeasurement.getMeasuredTimes());
        newAverage = UniversalTimeAverage(newMeasurement.getMeasuredTimes());
        oldAverage = UniversalTimeAverage(oldMeasurement.getMeasuredTimes());
        performanceChange = 100 * oldAverage / newAverage - 100;
        if (performanceComparator.GetLastComparisonResult() != ComparisonResult.Bootstrap)
            TestVerdict = ResolveTestVerdict();
        else TestVerdict = Bootstrap.Evaluate(newMeasurement.getMeasuredTimes(), oldMeasurement.getMeasuredTimes(),
                critValue, performanceComparator.GetMinSampleCount());
    }

    /**
     * @param timeUnitList list of UniversalTimeUnits to compute average
     * @return average of nanoseconds of times from timeUnitList
     */
    private static double UniversalTimeAverage(List<UniversalTimeUnit> timeUnitList) {
        long sum = 0;
        for (UniversalTimeUnit unit : timeUnitList) {
            sum += unit.GetNanoSeconds();
        }
        return (double) sum / timeUnitList.size();
    }

    /**
     * @return true if performance gone better or if is not significantly worse
     */
    private boolean ResolveTestVerdict() {

        // Bootstrap state is missing because in case of bootstrap this method is not called

        switch (performanceComparator.GetLastComparisonResult()) {
            case SameDistribution -> {
                return true;
            }
            case DifferentDistribution -> {
                return performanceChange > 0;
            }
            case NotEnoughSamples -> {
                return false;
            }
            default -> throw new IllegalStateException("Unexpected value: " + performanceComparator.GetLastComparisonResult());
        }
    }
}
