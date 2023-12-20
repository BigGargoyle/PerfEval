import cz.cuni.mff.d3s.perfeval.Metric;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonRecord;
import cz.cuni.mff.d3s.perfeval.performancecomparators.NonparametricTest;
import cz.cuni.mff.d3s.perfeval.performancecomparators.PerformanceEvaluator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NonparametricEvaluatorTest {

    @Test
    public void newSamplesAreBetter() {
        NonparametricTest evaluator = new NonparametricTest(0.05, 1000);
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
        NonparametricTest evaluator = new NonparametricTest(0.05, 1000);
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
        NonparametricTest evaluator = new NonparametricTest(0.05, 1000);
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
        NonparametricTest evaluator = new NonparametricTest(0.05, 1000);
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
        NonparametricTest evaluator = new NonparametricTest(0.05, 1000);
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
