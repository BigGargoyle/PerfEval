import cz.cuni.mff.d3s.perfeval.Metric;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonRecord;
import cz.cuni.mff.d3s.perfeval.performancecomparators.ParametricTest;
import cz.cuni.mff.d3s.perfeval.performancecomparators.PerformanceEvaluator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParametricEvaluatorTest {
    @Test
    public void newSamplesAreBetter() {
        ParametricTest evaluator = new ParametricTest(0.05);
        PerformanceEvaluator comparator = new PerformanceEvaluator(0.05, 0.1, 0.1, null, evaluator);

        Samples oldSamples = new Samples(new Metric("", true), "name1");
        oldSamples.addSample(new double[]{1.0, 2.0, 3.0});
        oldSamples.addSample(new double[]{4.0, 5.0, 6.0});

        Samples newSamples = new Samples(new Metric("", true), "name1");
        newSamples.addSample(new double[]{7.0, 8.0, 9.0});
        newSamples.addSample(new double[]{10.0, 11.0, 12.0});

        MeasurementComparisonRecord result = comparator.compareSets(oldSamples, newSamples);
        assertTrue(result.testVerdict());

    }

    @Test
    public void oldSamplesAreBetter(){
        ParametricTest evaluator = new ParametricTest(0.05);
        PerformanceEvaluator comparator = new PerformanceEvaluator(0.05, 0.1, 0.1, null, evaluator);

        Samples newSamples = new Samples(new Metric("", true), "name1");
        newSamples.addSample(new double[]{1.0, 2.0, 3.0});
        newSamples.addSample(new double[]{4.0, 5.0, 6.0});

        Samples oldSamples = new Samples(new Metric("", true), "name1");
        oldSamples.addSample(new double[]{7.0, 8.0, 9.0});
        oldSamples.addSample(new double[]{10.0, 11.0, 12.0});

        MeasurementComparisonRecord result = comparator.compareSets(oldSamples, newSamples);
        assertFalse(result.testVerdict());
    }
    @Test
    public void newSamplesAreBetterHigherIsWorse(){
        ParametricTest evaluator = new ParametricTest(0.05);
        PerformanceEvaluator comparator = new PerformanceEvaluator(0.05, 0.1, 0.1, null, evaluator);

        Samples newSamples = new Samples(new Metric("", false), "name1");
        newSamples.addSample(new double[]{1.0, 2.0, 3.0});
        newSamples.addSample(new double[]{4.0, 5.0, 6.0});

        Samples oldSamples = new Samples(new Metric("", false), "name1");
        oldSamples.addSample(new double[]{7.0, 8.0, 9.0});
        oldSamples.addSample(new double[]{10.0, 11.0, 12.0});

        MeasurementComparisonRecord result = comparator.compareSets(oldSamples, newSamples);
        assertTrue(result.testVerdict());
    }

    @Test
    public void newSamplesAreSameAsOld(){
        ParametricTest evaluator = new ParametricTest(0.05);
        PerformanceEvaluator comparator = new PerformanceEvaluator(0.05, 0.1, 0.1, null, evaluator);

        Samples oldSamples = new Samples(new Metric("", true), "name1");
        oldSamples.addSample(new double[]{1.0, 2.0, 3.0});
        oldSamples.addSample(new double[]{4.0, 5.0, 6.0});

        Samples newSamples = new Samples(new Metric("", true), "name1");
        newSamples.addSample(new double[]{1.0, 2.0, 3.0});
        newSamples.addSample(new double[]{4.0, 5.0, 6.0});

        MeasurementComparisonRecord result = comparator.compareSets(oldSamples, newSamples);
        assertTrue(result.testVerdict());
    }

    @Test
    public void newSamplesAreWorseButInTolerance(){
        ParametricTest evaluator = new ParametricTest(0.05);
        PerformanceEvaluator comparator = new PerformanceEvaluator(0.05, 0.1, 0.5, null, evaluator);

        Samples oldSamples = new Samples(new Metric("", true), "name1");
        oldSamples.addSample(new double[]{7.0, 8.0, 9.0});
        oldSamples.addSample(new double[]{10.0, 11.0, 12.0});

        Samples newSamples = new Samples(new Metric("", true), "name1");
        newSamples.addSample(new double[]{1.0, 2.0, 3.0});
        newSamples.addSample(new double[]{4.0, 5.0, 6.0});

        MeasurementComparisonRecord result = comparator.compareSets(oldSamples, newSamples);
        assertTrue(result.testVerdict());
    }
}
