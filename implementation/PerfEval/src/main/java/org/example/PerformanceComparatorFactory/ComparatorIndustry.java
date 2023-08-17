package org.example.PerformanceComparatorFactory;

import org.example.MeasurementFactory.UniversalTimeUnit;

public class ComparatorIndustry {
    /**
     *
     * @param pValue max p value for statistical tests that will be done with given data sets inside IPerformanceComparator
     * @param maxConfidenceIntervalWidth maximum relative width of confidence interval of the set mean
     *                                   (for example 0.1 means that maximal width of interval is 0.1 * mean of set)
     * @param maxTimeToTest maximum amount of time for doing this benchmark test
     * @return the best implementation of IPerformanceComparator
     */
    public static IPerformanceComparator GetComparator(double pValue, double maxConfidenceIntervalWidth, UniversalTimeUnit maxTimeToTest){
        // return new BasicPerformanceComparator(pValue);
        // return new NormalPerformanceComparator(pValue, maxConfidenceIntervalWidth);
        return new BootstrapPerformanceComparator(pValue, maxConfidenceIntervalWidth, maxTimeToTest);
    }

    public static IPerformanceComparator GetComparator(double pValue, double maxConfidenceIntervalWidth, UniversalTimeUnit maxTimeToTest, ComparisonResult preferredComparisonResultType){
        switch(preferredComparisonResultType){
            case SameDistribution -> {
                return new AlwaysSameComparator();
            }
            case DifferentDistribution -> {
                return new AlwaysDifferentComparator();
            }
            case NotEnoughSamples -> {
                return new AlwaysNotEnoughSamplesComparator();
            }
            case Bootstrap -> {
                return new AlwaysBootstrapComparator();
            }
            case None -> {
                return GetComparator(pValue, maxConfidenceIntervalWidth, maxTimeToTest);
            }
        }
        return null;
    }
}
