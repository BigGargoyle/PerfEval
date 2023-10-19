import cz.cuni.mff.hrdydo.Metric;
import cz.cuni.mff.hrdydo.Samples;
import cz.cuni.mff.hrdydo.evaluation.MeasurementComparisonRecord;
import cz.cuni.mff.hrdydo.performanceComparatorFactory.BootstrapPerformanceComparator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BootstrapPerformanceComparatorTest {

    @Test
    public void newSamplesAreBetter() {
        BootstrapPerformanceComparator comparator = new BootstrapPerformanceComparator(0.05, 0.1, 1000);

        Samples oldSamples = new Samples(new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}}, new Metric("", true), "name1");
        Samples newSamples = new Samples(new double[][]{{7.0, 8.0, 9.0}, {10.0, 11.0, 12.0}}, new Metric("", true), "name1");

        MeasurementComparisonRecord result = comparator.compareSets(oldSamples, newSamples);
        assertTrue(result.testVerdict());
    }

    @Test
    public void oldSamplesAreBetter() {
        BootstrapPerformanceComparator comparator = new BootstrapPerformanceComparator(0.05, 0.1, 1000);

        Samples newSamples = new Samples(new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}}, new Metric("", true), "name1");
        Samples oldSamples = new Samples(new double[][]{{7.0, 8.0, 9.0}, {10.0, 11.0, 12.0}}, new Metric("", true), "name1");

        MeasurementComparisonRecord result = comparator.compareSets(oldSamples, newSamples);
        assertFalse(result.testVerdict());
    }

    @Test
    public void newSamplesAreBetterHigherIsWorse() {
        BootstrapPerformanceComparator comparator = new BootstrapPerformanceComparator(0.05, 0.1, 1000);

        Samples newSamples = new Samples(new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}}, new Metric("", false), "name1");
        Samples oldSamples = new Samples(new double[][]{{7.0, 8.0, 9.0}, {10.0, 11.0, 12.0}}, new Metric("", false), "name1");

        MeasurementComparisonRecord result = comparator.compareSets(oldSamples, newSamples);
        assertTrue(result.testVerdict());
    }

    @Test
    public void newSamplesAreSameAsOld() {
        BootstrapPerformanceComparator comparator = new BootstrapPerformanceComparator(0.05, 0.1, 1000);

        Samples oldSamples = new Samples(new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}}, new Metric("", true), "name1");
        Samples newSamples = new Samples(new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}}, new Metric("", true), "name1");

        MeasurementComparisonRecord result = comparator.compareSets(oldSamples, newSamples);
        assertTrue(result.testVerdict());
    }

    @Test
    public void newSamplesAreWorseButInTolerance() {
        BootstrapPerformanceComparator comparator = new BootstrapPerformanceComparator(0.05, 0.5, 1000);

        Samples oldSamples = new Samples(new double[][]{{7.0, 8.0, 9.0}, {10.0, 11.0, 12.0}}, new Metric("", true), "name1");
        Samples newSamples = new Samples(new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}}, new Metric("", true), "name1");

        MeasurementComparisonRecord result = comparator.compareSets(oldSamples, newSamples);
        assertTrue(result.testVerdict());
    }
}