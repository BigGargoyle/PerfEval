package org.example.PerformanceComparatorFactory;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.TTest;
import org.example.MeasurementFactory.UniversalTimeUnit;

import java.util.List;

/**
 * Implementation of IPerformanceComparator that only recognizes if the sets are not from the same distribution, if
 * T-test does not deny hypothesis that they are from the same distribution then evaluates sets as they are from
 * the same distribution
 */
public class BasicPerformanceComparator implements IPerformanceComparator {
    final double criticalValue;
    ComparisonResult comparisonResult;

    public BasicPerformanceComparator(double pValue) {
        this.criticalValue = pValue;
        comparisonResult = ComparisonResult.None;
    }

    @Override
    public ComparisonResult compareSets(List<UniversalTimeUnit> newSet, List<UniversalTimeUnit> oldSet) {
        if (oldSet == null || newSet == null || oldSet.size() == 0 || newSet.size() == 0) {
            // an error has occurred
            comparisonResult = ComparisonResult.None;
            return comparisonResult;
        }

        TTest tTest = new TTest();
        double pValue = tTest.t(listToStatistic(newSet), listToStatistic(oldSet));
        if (Math.abs(pValue) > criticalValue)
            comparisonResult = ComparisonResult.DifferentDistribution;
        else
            comparisonResult = ComparisonResult.SameDistribution;
        return comparisonResult;
    }

    private SummaryStatistics listToStatistic(List<UniversalTimeUnit> statSet) {
        SummaryStatistics stats = new SummaryStatistics();
        for (UniversalTimeUnit value : statSet) {
            stats.addValue(value.getNanoSeconds());
        }
        return stats;
    }

    @Override
    public ComparisonResult getLastComparisonResult() {
        return comparisonResult;
    }

    @Override
    public int getMinSampleCount() {
        return -1;
    }
}
