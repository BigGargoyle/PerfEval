import cz.cuni.mff.d3s.perfeval.Metric;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonRecord;
import cz.cuni.mff.d3s.perfeval.performancecomparators.ComparisonResult;
import cz.cuni.mff.d3s.perfeval.performancecomparators.TTestPerformanceComparator;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TTestPerformanceComparatorTest {

    @Test
    void testCompareSetsSameDistribution() {
        Metric metric = new Metric("metric", true);
        String name = "TestSamples";

        Samples oldSamples = new Samples(metric, name);
        oldSamples.addSample(new double[]{1.0, 2.0});
        oldSamples.addSample(new double[]{3.0, 4.0});

        Samples newSamples = new Samples(metric, name);
        newSamples.addSample(new double[]{1.0, 2.0});
        newSamples.addSample(new double[]{3.0, 4.0});

        TTestPerformanceComparator comparator = new TTestPerformanceComparator(0.05, 0.2, 0.1, Duration.ofHours(1));

        MeasurementComparisonRecord record = comparator.compareSets(oldSamples, newSamples);

        assertEquals(ComparisonResult.SameDistribution, record.comparisonResult());
    }

    @Test
    void testCompareSetsNotEnoughSamplesDistribution() {
        Metric metric = new Metric("metric", false);
        String name = "TestSamples";

        Samples oldSamples = new Samples(metric, name);
        oldSamples.addSample(new double[]{1.0, 2.0});
        oldSamples.addSample(new double[]{3.0, 4.0});

        Samples newSamples = new Samples(metric, name);
        newSamples.addSample(new double[]{5.0, 6.0});
        newSamples.addSample(new double[]{7.0, 8.0});

        TTestPerformanceComparator comparator = new TTestPerformanceComparator(0.05, 0.2, 0.1, Duration.ofHours(1));

        MeasurementComparisonRecord record = comparator.compareSets(oldSamples, newSamples);

        assertEquals(ComparisonResult.NotEnoughSamples, record.comparisonResult());
    }

    @Test
    void testCompareDifferentDistributionDistribution() {
        Metric metric = new Metric("metric", false);
        String name = "TestSamples";

        Samples oldSamples = new Samples(metric, name);
        oldSamples.addSample(new double[]{3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3});

        Samples newSamples = new Samples(metric, name);
        newSamples.addSample(new double[]{2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2});

        TTestPerformanceComparator comparator = new TTestPerformanceComparator(0.05, 0.2, 0.1, Duration.ofHours(1));

        MeasurementComparisonRecord record = comparator.compareSets(oldSamples, newSamples);

        assertEquals(ComparisonResult.DifferentDistribution, record.comparisonResult());
    }
}
