import cz.cuni.mff.d3s.perfeval.performancecomparators.HierarchicalBootstrap;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
}
