package org.example.performanceComparatorFactory;

import org.apache.commons.math3.stat.StatUtils;
import org.example.Samples;
import org.example.evaluation.MeasurementComparisonRecord;

public class TTestPerformanceComparator implements PerformanceComparator{
    static final int MINUS_ONE = -1;
    double critValue;
    double maxCIWidth;
    double tolerance;

    StatisticTTest statisticTTest;
    public TTestPerformanceComparator(double critValue, double maxCIWidth, double tolerance) {
        this.critValue = critValue;
        this.maxCIWidth = maxCIWidth;
        this.tolerance = tolerance;
        statisticTTest = new StatisticTTest(critValue);
        assert tolerance >= 0 && tolerance <= 1;
        assert critValue > 0 && critValue < 1;
        assert maxCIWidth > 0 && maxCIWidth < 1;
    }

    private MeasurementComparisonRecord constructRecord(Samples oldSamples, Samples newSamples, ComparisonResult comparisonResult, int minSampleCount){
        double oldAvg = ArrayUtilities.calculateAverage(oldSamples.getRawData());
        double newAvg = ArrayUtilities.calculateAverage(newSamples.getRawData());

        double performanceChange = oldSamples.getMetric().getMetricPerformanceDirection() ? newAvg/oldAvg : oldAvg/newAvg;
        performanceChange = performanceChange*100 - 100;

        boolean testVerdict;
        switch (comparisonResult) {
            case SameDistribution -> testVerdict=true;
            case DifferentDistribution -> {
                if(performanceChange>=0) testVerdict=true;
                if(Math.abs(performanceChange/100)>1-tolerance) testVerdict=true;
                testVerdict=false;
            }
            case NotEnoughSamples -> {
                testVerdict=true;
            }
            default -> testVerdict=false;
        }

        return new MeasurementComparisonRecord(oldAvg, newAvg, performanceChange, comparisonResult, testVerdict, minSampleCount, oldSamples, newSamples);
    }

    @Override
    public MeasurementComparisonRecord compareSets(Samples oldMeasurement, Samples newMeasurement) {
        if(statisticTTest.areSetsSame(oldMeasurement.getRawData(), newMeasurement.getRawData())){
            return constructRecord(oldMeasurement, newMeasurement, ComparisonResult.SameDistribution, MINUS_ONE);
        }
        double[] newArr = ArrayUtilities.mergeArrays(newMeasurement.getRawData());
        double[] oldArr = ArrayUtilities.mergeArrays(newMeasurement.getRawData());
        double[] newCI = HierarchicalBootstrap.calcCIInterval(newArr, 1-critValue);
        double[] oldCI = HierarchicalBootstrap.calcCIInterval(oldArr, 1-critValue);
        double newMean = StatUtils.mean(newArr);
        double oldMean = StatUtils.mean(oldArr);
        if (newCI[1]-newCI[0] <= newMean * maxCIWidth && oldCI[1]-oldCI[0] <= oldMean * maxCIWidth) {
            return constructRecord(oldMeasurement, newMeasurement, ComparisonResult.DifferentDistribution, MINUS_ONE);
        }
        int minSampleCount = Math.max(ArrayUtilities.calcMinSampleCount(newArr,critValue, maxCIWidth), ArrayUtilities.calcMinSampleCount(oldArr,critValue, maxCIWidth));
        return constructRecord(oldMeasurement, newMeasurement, ComparisonResult.SameDistribution, minSampleCount);
    }
}
