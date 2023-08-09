package PerformanceComparatorTests;

import org.example.PerformanceComparatorFactory.NormalPerformanceComparator;
import org.example.PerformanceComparatorFactory.ComparisonResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NormalPerformanceComparatorTest {
    private NormalPerformanceComparator performanceComparator;

    @BeforeEach
    public void setUp() {
        // Set the critical value to 0.05 for testing purposes
        double pValue = 0.05;
        double maxWidth = 0.1;
        performanceComparator = new NormalPerformanceComparator(pValue, maxWidth);
    }

    @Test
    public void testSameDistribution() {
        // Test when the two sets are from the same distribution
        double[] newSet = {1000.0, 1001.0, 1000.0, 1001.0, 1001.0, 999.0};
        double[] oldSet = {1000.01, 1001.02, 1000.0, 1001.01, 1001.01, 999.01};
        ComparisonResult result = performanceComparator.CompareSets(newSet, oldSet);
        assertEquals(ComparisonResult.SameDistribution, result);
        assertEquals(ComparisonResult.SameDistribution, performanceComparator.GetLastComparisonResult());
    }

    @Test
    public void testNotEnoughSamples() {
        // Test when the two sets are from the same distribution
        double[] newSet = {1.0,2.0,3.0,4.0,5.0,6.0};
        double[] oldSet = {1.0,2.0,3.0,4.0,5.0,6.0};
        ComparisonResult result = performanceComparator.CompareSets(newSet, oldSet);
        assertEquals(ComparisonResult.NotEnoughSamples, result);
        assertEquals(ComparisonResult.NotEnoughSamples, performanceComparator.GetLastComparisonResult());
        assertTrue(performanceComparator.GetMinSampleCount()>newSet.length || performanceComparator.GetMinSampleCount()>oldSet.length);
    }

    @Test
    public void testEnoughSamples() {
        // Test when the two sets are from the same distribution
        double[] newSet = {1.0,2.0,3.0,4.0,5.0,6.0,8.0,9.0,10.0};
        double[] oldSet = {1.0,2.0,3.0,4.0,5.0,6.0,8.0,9.0,10.0};
        ComparisonResult result = performanceComparator.CompareSets(newSet, oldSet);
        assertEquals(ComparisonResult.NotEnoughSamples, result);
        assertEquals(ComparisonResult.NotEnoughSamples, performanceComparator.GetLastComparisonResult());
        assertTrue(performanceComparator.GetMinSampleCount()>newSet.length || performanceComparator.GetMinSampleCount()>oldSet.length);

        double[] newSet2 = new double[performanceComparator.GetMinSampleCount()];
        double[] oldSet2 = new double[performanceComparator.GetMinSampleCount()];
        for (int i = 0; i < performanceComparator.GetMinSampleCount(); i++){
            newSet2[i] = newSet[i%newSet.length];
            oldSet2[i] = oldSet[i%oldSet.length];
        }

        // indicates that calcMinSampleCount method is returning invalid values
        result = performanceComparator.CompareSets(newSet2, oldSet2);
        assertEquals(ComparisonResult.SameDistribution, result);
        assertEquals(ComparisonResult.SameDistribution, performanceComparator.GetLastComparisonResult());

    }

    @Test
    public void testDifferentDistribution() {
        // Test when the two sets are from different distributions
        double[] newSet = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] oldSet = {10.0, 20.0, 30.0, 40.0, 50.0};
        ComparisonResult result = performanceComparator.CompareSets(newSet, oldSet);
        assertEquals(ComparisonResult.DifferentDistribution, result);
        assertEquals(ComparisonResult.DifferentDistribution, performanceComparator.GetLastComparisonResult());
        // assertTrue(performanceComparator.GetMinSampleCount()>newSet.length || performanceComparator.GetMinSampleCount()>oldSet.length);
    }

    @Test
    public void testEmptySets() {
        // Test with empty sets
        double[] newSet = {};
        double[] oldSet = {};
        ComparisonResult result = performanceComparator.CompareSets(newSet, oldSet);
        assertEquals(ComparisonResult.None, result);
        assertEquals(ComparisonResult.None, performanceComparator.GetLastComparisonResult());
    }

    @Test
    public void testNullSets() {
        // Test with null sets
        double[] newSet = null;
        double[] oldSet = null;
        ComparisonResult result = performanceComparator.CompareSets(newSet, oldSet);
        assertEquals(ComparisonResult.None, result);
        assertEquals(ComparisonResult.None, performanceComparator.GetLastComparisonResult());
    }

    @Test
    public void testOverlappingDistributions() {
        // Test when the two sets have overlapping distributions
        double[] newSet = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] oldSet = {4.0, 5.0, 6.0, 7.0, 8.0};
        ComparisonResult result = performanceComparator.CompareSets(newSet, oldSet);
        assertEquals(ComparisonResult.DifferentDistribution, result);
        assertEquals(ComparisonResult.DifferentDistribution, performanceComparator.GetLastComparisonResult());
    }

    @Test
    public void testDifferentSampleSizes() {
        // Test when the two sets have different sample sizes
        double[] newSet = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] oldSet = {1.0, 3.0, 5.0};
        ComparisonResult result = performanceComparator.CompareSets(newSet, oldSet);
        assertEquals(ComparisonResult.NotEnoughSamples, result);
        assertEquals(ComparisonResult.NotEnoughSamples, performanceComparator.GetLastComparisonResult());
        assertTrue(performanceComparator.GetMinSampleCount()>newSet.length || performanceComparator.GetMinSampleCount()>oldSet.length);
    }

    @Test
    public void testConfidenceInterval() {
        // Test when the two sets have overlapping distributions with a narrower confidence interval
        double[] newSet = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] oldSet = {4.0, 4.5, 5.0, 5.5, 6.0};
        ComparisonResult result = performanceComparator.CompareSets(newSet, oldSet);
        assertEquals(ComparisonResult.DifferentDistribution, result);
        assertEquals(ComparisonResult.DifferentDistribution, performanceComparator.GetLastComparisonResult());
    }
}
