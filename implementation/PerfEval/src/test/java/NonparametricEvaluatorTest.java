import cz.cuni.mff.d3s.perfeval.Metric;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.printers.MeasurementComparisonRecord;
import cz.cuni.mff.d3s.perfeval.evaluation.ComparisonResult;
import cz.cuni.mff.d3s.perfeval.evaluation.NonparametricTest;
import cz.cuni.mff.d3s.perfeval.evaluation.PerformanceEvaluator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NonparametricEvaluatorTest {
    static final int maxTestCount = 1000;
    @Test
    public void newSamplesAreBetter() {
        NonparametricTest evaluator = new NonparametricTest(0.05, 1000);
        PerformanceEvaluator comparator = new PerformanceEvaluator(0.05, 0.1, 0.1, maxTestCount, evaluator);

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
        PerformanceEvaluator comparator = new PerformanceEvaluator(0.05, 0.1, 0.1, maxTestCount, evaluator);

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
        PerformanceEvaluator comparator = new PerformanceEvaluator(0.05, 0.1, 0.1, maxTestCount, evaluator);

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
        PerformanceEvaluator comparator = new PerformanceEvaluator(0.05, 0.1, 0.1, maxTestCount, evaluator);

        Samples oldSamples = new Samples(new Metric("", true), "name1");
        // bad data, too large variance
        //oldSamples.addSample(new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0});

        oldSamples.addSample(new double[]{1.0, 2.0, 3.0, 1.0, 2.0, 3.0, 1.0, 2.0, 3.0});
        oldSamples.addSample(new double[]{1.0, 2.0, 3.0, 1.0, 2.0, 3.0, 1.0, 2.0, 3.0});

        Samples newSamples = new Samples(new Metric("", true), "name1");
        newSamples.addSample(new double[]{1.0, 2.0, 3.0, 1.0, 2.0, 3.0, 1.0, 2.0, 3.0});
        newSamples.addSample(new double[]{1.0, 2.0, 3.0, 1.0, 2.0, 3.0, 1.0, 2.0, 3.0});

        MeasurementComparisonRecord result = comparator.compareSets(oldSamples, newSamples);
        assertTrue(result.testVerdict() || result.comparisonResult() == ComparisonResult.NotEnoughSamples);
        if(result.comparisonResult()==ComparisonResult.NotEnoughSamples){
            for(int i = 0; i<result.minSampleCount();i++){
                oldSamples.addSample(new double[]{1.0, 2.0, 3.0, 1.0, 2.0, 3.0});
                newSamples.addSample(new double[]{1.0, 2.0, 3.0, 1.0, 2.0, 3.0});
            }
            result = comparator.compareSets(oldSamples, newSamples);
            assertTrue(result.testVerdict());
        }
    }

    @Test
    public void newSamplesAreWorseButInTolerance(){
        NonparametricTest evaluator = new NonparametricTest(0.05, 1000);
        PerformanceEvaluator comparator = new PerformanceEvaluator(0.05, 0.1, 0.5, maxTestCount, evaluator);

        Samples oldSamples = new Samples(new Metric("", true), "name1");
        oldSamples.addSample(new double[]{7.0, 8.0, 9.0});
        oldSamples.addSample(new double[]{10.0, 11.0, 12.0});

        Samples newSamples = new Samples(new Metric("", true), "name1");
        newSamples.addSample(new double[]{1.0, 2.0, 3.0});
        newSamples.addSample(new double[]{4.0, 5.0, 6.0});

        MeasurementComparisonRecord result = comparator.compareSets(oldSamples, newSamples);
        assertTrue(result.testVerdict());
    }

    @Test
    public void notEnoughSamples(){
        double[] sample = new double[]{100_000.0, 105_000.0, 95_0000.0};
        NonparametricTest evaluator = new NonparametricTest(0.04, 1000);
        PerformanceEvaluator comparator = new PerformanceEvaluator(0.04, 0.1, 0.1, maxTestCount, evaluator);

        Samples oldSamples = new Samples(new Metric("", true), "name1");
        oldSamples.addSample(sample);
        oldSamples.addSample(sample);
        oldSamples.addSample(sample);

        Samples newSamples = new Samples(new Metric("", true), "name1");
        newSamples.addSample(sample);
        newSamples.addSample(sample);
        newSamples.addSample(sample);

        MeasurementComparisonRecord result = comparator.compareSets(oldSamples, newSamples);
        if(result.comparisonResult() == ComparisonResult.NotEnoughSamples) {
            for(int i = 0; i<result.minSampleCount();i++){
                oldSamples.addSample(sample);
                newSamples.addSample(sample);
            }
            result = comparator.compareSets(oldSamples, newSamples);
            assertTrue(result.testVerdict());
        }
    }


}
