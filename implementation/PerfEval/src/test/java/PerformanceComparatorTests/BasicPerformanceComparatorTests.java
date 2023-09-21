package PerformanceComparatorTests;

import org.example.measurementFactory.UniversalTimeUnit;
import org.example.performanceComparatorFactory.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class BasicPerformanceComparatorTests {
    private BasicPerformanceComparator performanceComparator;


    private List<UniversalTimeUnit> DoubleToTimeList(double[] arr){
        if (arr==null) return null;
        List<UniversalTimeUnit> result = new ArrayList<>();
        for(double value : arr){
            result.add(new UniversalTimeUnit((long)Math.round(value), TimeUnit.NANOSECONDS));
        }
        return result;
    }

    @BeforeEach
    public void setUp() {
        // Set the critical value to 0.05 for testing purposes
        double pValue = 0.05;
        performanceComparator = new BasicPerformanceComparator(pValue);
    }

    @Test
    public void testSameDistribution() {
        // Test when the two sets are from the same distribution
        double[] newSet = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] oldSet = {1.01, 2.01, 3.01, 4.01, 5.01};
        ComparisonResult result = performanceComparator.compareSets(DoubleToTimeList(newSet), DoubleToTimeList(oldSet));
        assertEquals(ComparisonResult.SameDistribution, result);
        assertEquals(ComparisonResult.SameDistribution, performanceComparator.getLastComparisonResult());
    }
    @Test
    public void testSameDistributionHigherValues() {
        // Test when the two sets are from the same distribution
        double[] newSet = {1000.0, 1001.0, 1000.0, 1001.0, 1001.0, 999.0};
        double[] oldSet = {1000.01, 1001.02, 1000.0, 1001.01, 1001.01, 999.01};
        ComparisonResult result = performanceComparator.compareSets(DoubleToTimeList(newSet), DoubleToTimeList(oldSet));
        assertEquals(ComparisonResult.SameDistribution, result);
        assertEquals(ComparisonResult.SameDistribution, performanceComparator.getLastComparisonResult());
    }

    @Test
    public void testDifferentDistribution() {
        // Test when the two sets are from different distributions
        double[] newSet = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] oldSet = {10.0, 20.0, 30.0, 40.0, 50.0};
        ComparisonResult result = performanceComparator.compareSets(DoubleToTimeList(newSet), DoubleToTimeList(oldSet));
        assertEquals(ComparisonResult.DifferentDistribution, result);
        assertEquals(ComparisonResult.DifferentDistribution, performanceComparator.getLastComparisonResult());
    }

    @Test
    public void testEmptySets() {
        // Test with empty sets
        double[] newSet = {};
        double[] oldSet = {};
        ComparisonResult result = performanceComparator.compareSets(DoubleToTimeList(newSet), DoubleToTimeList(oldSet));
        assertEquals(ComparisonResult.None, result);
        assertEquals(ComparisonResult.None, performanceComparator.getLastComparisonResult());
    }

    @Test
    public void testNullSets() {
        // Test with null sets
        double[] newSet = null;
        double[] oldSet = null;
        ComparisonResult result = performanceComparator.compareSets(DoubleToTimeList(newSet), DoubleToTimeList(oldSet));
        assertEquals(ComparisonResult.None, result);
        assertEquals(ComparisonResult.None, performanceComparator.getLastComparisonResult());
    }

    @Test
    public void testOverlappingDistributions() {
        // Test when the two sets have overlapping distributions
        double[] newSet = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] oldSet = {4.0, 5.0, 6.0, 7.0, 8.0};
        ComparisonResult result = performanceComparator.compareSets(DoubleToTimeList(newSet), DoubleToTimeList(oldSet));
        assertEquals(ComparisonResult.DifferentDistribution, result);
        assertEquals(ComparisonResult.DifferentDistribution, performanceComparator.getLastComparisonResult());
    }

    @Test
    public void testDifferentSampleSizes() {
        // Test when the two sets have different sample sizes
        double[] newSet = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] oldSet = {1.0, 3.0, 5.0};
        ComparisonResult result = performanceComparator.compareSets(DoubleToTimeList(newSet), DoubleToTimeList(oldSet));
        assertEquals(ComparisonResult.SameDistribution, result);
        assertEquals(ComparisonResult.SameDistribution, performanceComparator.getLastComparisonResult());
    }

    @Test
    public void testConfidenceInterval() {
        // Test when the two sets have overlapping distributions with a narrower confidence interval
        double[] newSet = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] oldSet = {4.0, 4.5, 5.0, 5.5, 6.0};
        ComparisonResult result = performanceComparator.compareSets(DoubleToTimeList(newSet), DoubleToTimeList(oldSet));
        assertEquals(ComparisonResult.DifferentDistribution, result);
        assertEquals(ComparisonResult.DifferentDistribution, performanceComparator.getLastComparisonResult());
    }
}
