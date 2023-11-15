import cz.cuni.mff.d3s.perfeval.Metric;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonRecord;
import cz.cuni.mff.d3s.perfeval.performancecomparatorfactory.ComparisonResult;
import cz.cuni.mff.d3s.perfeval.performancecomparatorfactory.TTestPerformanceComparator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TTestPerformanceComparatorTest {

    @Test
    void testCompareSetsSameDistribution() {
        // Assuming Metric and ArrayUtilities are properly implemented
        double[][] rawData = {{1.0, 2.0}, {3.0, 4.0}};
        Metric metric = new Metric("metric", true);
        String name = "TestSamples";

        Samples oldSamples = new Samples(rawData, metric, name);
        Samples newSamples = new Samples(rawData, metric, name);

        TTestPerformanceComparator comparator = new TTestPerformanceComparator(0.05, 0.2, 0.1);

        MeasurementComparisonRecord record = comparator.compareSets(oldSamples, newSamples);

        assertEquals(ComparisonResult.SameDistribution, record.comparisonResult());
    }

    @Test
    void testCompareSetsNotEnoughSamplesDistribution() {
        // Assuming Metric and ArrayUtilities are properly implemented
        double[][] oldRawData = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] newRawData = {{5.0, 6.0}, {7.0, 8.0}};
        Metric metric = new Metric("metric", false);
        String name = "TestSamples";

        Samples oldSamples = new Samples(oldRawData, metric, name);
        Samples newSamples = new Samples(newRawData, metric, name);

        TTestPerformanceComparator comparator = new TTestPerformanceComparator(0.05, 0.2, 0.1);

        MeasurementComparisonRecord record = comparator.compareSets(oldSamples, newSamples);

        assertEquals(ComparisonResult.NotEnoughSamples, record.comparisonResult());
    }

    @Test
    void testCompareDifferentDistributionDistribution() {
        // Assuming Metric and ArrayUtilities are properly implemented
        double[][] oldRawData = {{3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3}};
        double[][] newRawData = {{2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}};
        Metric metric = new Metric("metric", false);
        String name = "TestSamples";

        Samples oldSamples = new Samples(oldRawData, metric, name);
        Samples newSamples = new Samples(newRawData, metric, name);

        TTestPerformanceComparator comparator = new TTestPerformanceComparator(0.05, 0.2, 0.1);

        MeasurementComparisonRecord record = comparator.compareSets(oldSamples, newSamples);

        assertEquals(ComparisonResult.DifferentDistribution, record.comparisonResult());
    }
}