package org.example.PerformanceComparatorFactory;

import org.apache.commons.math3.stat.inference.TTest;

import java.util.List;

/**
 * Implementation of IPerformanceComparator that only recognizes if the sets are not from the same distribution, if
 * T-test does not deny hypothesis that they are from the same distribution then evaluates sets as they are from
 * the same distribution
 */
public class BasicPerformanceComparator implements IPerformanceComparator{
    double criticalValue;
    ComparisonResult comparisonResult;
    public BasicPerformanceComparator(double pValue){
        this.criticalValue = pValue;
        comparisonResult = ComparisonResult.None;
    }

    @Override
    public ComparisonResult CompareSets(double[] newSet, double[] oldSet) {
        if(oldSet == null || newSet == null || oldSet.length==0 || newSet.length == 0){
            // an error has occurred
            comparisonResult = ComparisonResult.None;
            return comparisonResult;
        }

        TTest tTest = new TTest();
        double pValue = tTest.t(newSet, oldSet);
        if(Math.abs(pValue) > criticalValue)
            comparisonResult = ComparisonResult.DifferentDistribution;
        else
            comparisonResult = ComparisonResult.SameDistribution;
        return comparisonResult;
    }
    @Override
    public ComparisonResult GetLastComparisonResult() {
        return comparisonResult;
    }

    @Override
    public int GetMinSampleCount() {
        return -1;
    }

    @Override
    public double GetConfidenceIntervalWidth() {
        return -1;
    }
}
