package org.example;

import org.example.MeasurementFactory.IMeasurement;
import org.example.MeasurementFactory.UniversalTimeUnit;
import org.example.PerformanceComparatorFactory.Bootstrap;
import org.example.PerformanceComparatorFactory.ComparatorIndustry;
import org.example.PerformanceComparatorFactory.ComparisonResult;
import org.example.PerformanceComparatorFactory.IPerformanceComparator;

import java.util.List;

public class MeasurementComparisonResult {
    private final double newAverage;
    private final double oldAverage;
    private final double performanceChange;
    // private ComparisonResult comparisonResult;
    private final boolean TestVerdict;
    private final IPerformanceComparator performanceComparator;
    private final IMeasurement oldMeasurement;
    private final IMeasurement newMeasurement;

    public String getName(){
        return newMeasurement.getName();
    }
    public double getNewAvg(){
        return newAverage;
    }
    public double getOldAvg(){
        return oldAverage;
    }
    public double getChange(){
        return performanceChange;
    }
    public ComparisonResult getComparisonResult(){
        return performanceComparator.GetLastComparisonResult();
    }
    public boolean getComparisonVerdict(){
        return TestVerdict;
    }
    public IMeasurement getOldMeasurement() {
        return oldMeasurement;
    }
    public IMeasurement getNewMeasurement(){
        return  newMeasurement;
    }
    public IPerformanceComparator getPerformanceComparator(){
        return  performanceComparator;
    }
    public MeasurementComparisonResult(double critValue, double maxCIWidth, UniversalTimeUnit maxTestTime,
                                       IMeasurement _newMeasurement, IMeasurement _oldMeasurement,
                                       IPerformanceComparator _performanceComparator){
        oldMeasurement = _oldMeasurement;
        newMeasurement = _newMeasurement;
        // performanceComparator = ComparatorIndustry.GetComparator(critValue, maxCIWidth, maxTestTime);
        performanceComparator = _performanceComparator;
        performanceComparator.CompareSets(newMeasurement.getMeasuredTimes(), oldMeasurement.getMeasuredTimes());
        newAverage = UniversalTimeAverage(newMeasurement.getMeasuredTimes());
        oldAverage = UniversalTimeAverage(oldMeasurement.getMeasuredTimes());
        performanceChange = 100 * oldAverage / newAverage - 100;
        if(performanceComparator.GetLastComparisonResult()!=ComparisonResult.Bootstrap)
            TestVerdict = ResolveTestVerdict();
        else TestVerdict = Bootstrap.Evaluate(newMeasurement.getMeasuredTimes(), oldMeasurement.getMeasuredTimes(),
                critValue, performanceComparator.GetMinSampleCount());
    }
    private static double UniversalTimeAverage(List<UniversalTimeUnit> timeUnitList)
    {
        long sum = 0;
        for(UniversalTimeUnit unit : timeUnitList){
            sum+=unit.GetNanoSeconds();
        }
        return (double) sum/timeUnitList.size();
    }

    private boolean ResolveTestVerdict(){
        switch (performanceComparator.GetLastComparisonResult()) {
            case SameDistribution -> {
                return true;
            }
            case DifferentDistribution -> {
                return performanceChange>0;
            }
            case NotEnoughSamples -> {
                return false;
            }
            default -> throw new IllegalStateException("Unexpected value: " + performanceComparator.GetLastComparisonResult());
        }
    }


}
