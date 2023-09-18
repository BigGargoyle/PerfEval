package PerformanceComparatorTests;

import org.example.MeasurementFactory.UniversalTimeUnit;
import org.example.PerformanceComparatorFactory.ComparisonResult;
import org.example.PerformanceComparatorFactory.BootstrapPerformanceComparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BootstrapPerformanceComparatorTest {
    private BootstrapPerformanceComparator performanceComparator;

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
        double maxWidth = 0.1;
        performanceComparator = new BootstrapPerformanceComparator(pValue, maxWidth, new UniversalTimeUnit(1, TimeUnit.HOURS));
    }

    @Test
    public void testSameDistribution() {
        // Test when the two sets are from the same distribution
        double[] newSet = {1000.0, 1001.0, 1000.0, 1001.0, 1001.0, 999.0};
        double[] oldSet = {1000.01, 1001.02, 1000.0, 1001.01, 1001.01, 999.01};
        ComparisonResult result = performanceComparator.compareSets(DoubleToTimeList(newSet), DoubleToTimeList(oldSet));
        assertEquals(ComparisonResult.SameDistribution, result);
        assertEquals(ComparisonResult.SameDistribution, performanceComparator.getLastComparisonResult());
    }

    @Test
    public void testNotEnoughSamples() {
        // Test when the two sets are from the same distribution
        double[] newSet = {1.0,2.0,3.0,4.0,5.0,6.0};
        double[] oldSet = {1.0,2.0,3.0,4.0,5.0,6.0};
        ComparisonResult result = performanceComparator.compareSets(DoubleToTimeList(newSet), DoubleToTimeList(oldSet));
        assertEquals(ComparisonResult.NotEnoughSamples, result);
        assertEquals(ComparisonResult.NotEnoughSamples, performanceComparator.getLastComparisonResult());
        assertTrue(performanceComparator.getMinSampleCount()>newSet.length || performanceComparator.getMinSampleCount()>oldSet.length);
    }

    @Test
    public void testBootstrapRecommend() {
        // Test when the two sets needs bootstrap
        performanceComparator = new BootstrapPerformanceComparator(0.05, 0.1,
                new UniversalTimeUnit(200, TimeUnit.NANOSECONDS));
        double[] newSet = {10.0,20.0,30.0,40.0,50.0,60.0};
        double[] oldSet = {10.0,20.0,30.0,40.0,50.0,60.0};
        ComparisonResult result = performanceComparator.compareSets(DoubleToTimeList(newSet), DoubleToTimeList(oldSet));
        assertEquals(ComparisonResult.Bootstrap, result);
        assertEquals(ComparisonResult.Bootstrap, performanceComparator.getLastComparisonResult());
        assertTrue(performanceComparator.getMinSampleCount()>newSet.length || performanceComparator.getMinSampleCount()>oldSet.length);
    }

    @Test
    public void testEnoughSamples() {
        // Test when the two sets are from the same distribution
        double[] newSet = {1.0,2.0,3.0,4.0,5.0,6.0,8.0,9.0,10.0};
        double[] oldSet = {1.0,2.0,3.0,4.0,5.0,6.0,8.0,9.0,10.0};
        ComparisonResult result = performanceComparator.compareSets(DoubleToTimeList(newSet), DoubleToTimeList(oldSet));
        assertEquals(ComparisonResult.NotEnoughSamples, result);
        assertEquals(ComparisonResult.NotEnoughSamples, performanceComparator.getLastComparisonResult());
        assertTrue(performanceComparator.getMinSampleCount()>newSet.length || performanceComparator.getMinSampleCount()>oldSet.length);

        double[] newSet2 = new double[performanceComparator.getMinSampleCount()];
        double[] oldSet2 = new double[performanceComparator.getMinSampleCount()];
        for (int i = 0; i < performanceComparator.getMinSampleCount(); i++){
            newSet2[i] = newSet[i%newSet.length];
            oldSet2[i] = oldSet[i%oldSet.length];
        }

        // indicates that calcMinSampleCount method is returning invalid values
        result = performanceComparator.compareSets(DoubleToTimeList(newSet2), DoubleToTimeList(oldSet2));
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
        // assertTrue(performanceComparator.GetMinSampleCount()>newSet.length || performanceComparator.GetMinSampleCount()>oldSet.length);
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
        assertEquals(ComparisonResult.NotEnoughSamples, result);
        assertEquals(ComparisonResult.NotEnoughSamples, performanceComparator.getLastComparisonResult());
        assertTrue(performanceComparator.getMinSampleCount()>newSet.length || performanceComparator.getMinSampleCount()>oldSet.length);
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
