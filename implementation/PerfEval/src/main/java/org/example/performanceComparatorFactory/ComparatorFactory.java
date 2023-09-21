package org.example.performanceComparatorFactory;

import org.example.measurementFactory.UniversalTimeUnit;

public class ComparatorFactory {
    /**
     * @param pValue                     max p value for statistical tests that will be done with given data sets inside IPerformanceComparator
     * @param maxConfidenceIntervalWidth maximum relative width of confidence interval of the set mean
     *                                   (for example 0.1 means that maximal width of interval is 0.1 * mean of set)
     * @param maxTimeToTest              maximum amount of time for doing this benchmark test
     * @return the best implementation of IPerformanceComparator
     */
    public static IPerformanceComparator getComparator(double pValue, double maxConfidenceIntervalWidth, UniversalTimeUnit maxTimeToTest) {
        return new BootstrapPerformanceComparator(pValue, maxConfidenceIntervalWidth, maxTimeToTest);
    }

    /**
     * @param pValue                        max p value for statistical tests that will be done with given data sets inside IPerformanceComparator
     * @param maxConfidenceIntervalWidth    maximum relative width of confidence interval of the set mean
     *                                      (for example 0.1 means that maximal width of interval is 0.1 * mean of set)
     * @param maxTimeToTest                 maximum amount of time for doing this benchmark test
     * @param preferredComparisonResultType used for returning implementation of IPerformanceComparator, that is always returning this ComparisonResult
     *                                      if value ComparisonResult.None is inserted then it will return normal implementation of IPerformanceComparator
     * @return the best implementation of IPerformanceComparator
     */
    public static IPerformanceComparator getComparator(double pValue, double maxConfidenceIntervalWidth, UniversalTimeUnit maxTimeToTest, ComparisonResult preferredComparisonResultType) {
        switch (preferredComparisonResultType) {
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
                return getComparator(pValue, maxConfidenceIntervalWidth, maxTimeToTest);
            }
        }
        return null;
    }
}
