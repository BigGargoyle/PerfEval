import cz.cuni.mff.d3s.perfeval.evaluation.HierarchicalBootstrap;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class HierarchicalBootstrapTest {

    double DEFAULT_CRIT_VALUE = 0.05;
    int DEFAULT_BOOTSTRAP_SAMPLE_COUNT = 1_000;

    @Test
    public void testEvaluateBootstrapDifferentSets() {

        // Test evaluateBootstrap with sampleSet1 and sampleSet2
        double[][] sampleSet1 = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        double[][] sampleSet2 = {{7.0, 8.0, 9.0}, {10.0, 11.0, 12.0}};
        boolean result = HierarchicalBootstrap.evaluateBootstrap(sampleSet1, sampleSet2, DEFAULT_CRIT_VALUE, DEFAULT_BOOTSTRAP_SAMPLE_COUNT);
        assertFalse(result);
        boolean result2 = HierarchicalBootstrap.evaluateBootstrap(sampleSet1, sampleSet2, DEFAULT_CRIT_VALUE);
        assertFalse(result2);
    }
    @Test
    public void testEvaluateBootstrapSimilarSets() {

        // Test evaluateBootstrap with different data that should not satisfy the condition
        double[][] sampleSet3 = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        double[][] sampleSet4 = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        boolean result = HierarchicalBootstrap.evaluateBootstrap(sampleSet3, sampleSet4, DEFAULT_CRIT_VALUE, DEFAULT_BOOTSTRAP_SAMPLE_COUNT);
        assertTrue(result);
        boolean result2 = HierarchicalBootstrap.evaluateBootstrap(sampleSet3, sampleSet4, DEFAULT_CRIT_VALUE);
        assertTrue(result2);
    }

    @Test
    public void testEvaluateBootstrapDifferentSetsLargeArrays() {

        // Test evaluateBootstrap with larger sampleSet1 and sampleSet2
        int size = 10; // Adjust size as needed
        double[][] sampleSet1 = new double[size][size];
        double[][] sampleSet2 = new double[size][size];

        // Populate sampleSet1 and sampleSet2 with significantly different values
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sampleSet1[i][j] = i + j; // Some different values for sampleSet1
                sampleSet2[i][j] = i + j + 10; // Values shifted by 10 for sampleSet2
            }
        }

        boolean result = HierarchicalBootstrap.evaluateBootstrap(sampleSet1, sampleSet2, DEFAULT_CRIT_VALUE, DEFAULT_BOOTSTRAP_SAMPLE_COUNT);
        assertFalse(result);
        boolean result2 = HierarchicalBootstrap.evaluateBootstrap(sampleSet1, sampleSet2, DEFAULT_CRIT_VALUE);
        assertFalse(result2);
    }
    @Test
    public void testEvaluateBootstrapSimilarSetsLargeArrays() {

        // Test evaluateBootstrap with larger sampleSet3 and sampleSet4
        int size = 10; // Adjust size as needed
        double[][] sampleSet3 = new double[size][size];
        double[][] sampleSet4 = new double[size][size];

        // Populate sampleSet3 and sampleSet4 with similar values
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sampleSet3[i][j] = i + j; // Some values for sampleSet3
                sampleSet4[i][j] = i + j; // Same values for sampleSet4 as sampleSet3
            }
        }

        boolean result = HierarchicalBootstrap.evaluateBootstrap(sampleSet3, sampleSet4, DEFAULT_CRIT_VALUE, DEFAULT_BOOTSTRAP_SAMPLE_COUNT);
        assertTrue(result);
    }



    @Test
    public void testCalcCIInterval() {
        // Test calcCIInterval with a known data set
        double[] data = {1.0, 2.0, 3.0, 4.0, 5.0};
        double confidenceLevel = 0.95;
        double[] interval = HierarchicalBootstrap.calcCIInterval(data, confidenceLevel);
        assertNotNull(interval);
        assertEquals(2, interval.length);
        assertTrue(Arrays.stream(data).min().getAsDouble() <= interval[0]);
        assertTrue(Arrays.stream(data).max().getAsDouble() >= interval[1]);
    }

    @Test
    public void testAllValuesAreSame(){
        double[][] sample = new double[10][];
        for(int i = 0; i < sample.length; i++){
            sample[i] = new double[10];
            Arrays.fill(sample[i], 1);
        }
        boolean result = HierarchicalBootstrap.evaluateBootstrap(sample, sample, 0.05);
        assertTrue(result);
    }

    @Test
    public void testMatrixTransposed(){
        double[][] sample1 = new double[1][];
        double[][] sample2 = new double[1000][];
        sample1[0] = new double[1000];

        for(int i = 0; i < 1000; i++){
            sample1[0][i] = i;
            sample2[i] = new double[1];
            sample2[i][0] = i;
        }

        boolean result = HierarchicalBootstrap.evaluateBootstrap(sample1, sample2, 0.05);
        assertTrue(result);
    }

    @Test
    public void testJustOneValue(){
        double[][] sample1 = new double[1][];
        sample1[0] = new double[1000];
        Arrays.fill(sample1[0], 1);
        double[] ciInterval = HierarchicalBootstrap.calcCIInterval(sample1[0], 0.95);
        assertEquals(1, ciInterval[0]);
        assertEquals(1, ciInterval[1]);
    }

    @Test
    public void testTwoValuesWithNoise(){
        double noise = 0.01;
        Random random = new Random();
        double[][] sample1 = new double[2][];
        sample1[0] = new double[100];
        sample1[1] = new double[100];
        for (int i = 0; i<100; i++){
            sample1[0][i] = 1 + random.nextDouble(-noise, noise);
            sample1[1][i] = 2 + random.nextDouble(-noise, noise);
        }
        boolean result = HierarchicalBootstrap.evaluateBootstrap(sample1, sample1, 0.05);
        assertTrue(result);
    }

    @Test
    public void parameterCalcTest(){
        //y = 2/sqrt(x) + 0
        double[][] points = new double[][]{
                {0.01,20.3}, {1,2.02}, {4,1.0}, {9, 0.6}
        };
        double[] aParams = HierarchicalBootstrap.calcFunctionParameters(points);
        assertTrue(aParams[0] > 1.9 && aParams[0] < 2.1);
    }

}
