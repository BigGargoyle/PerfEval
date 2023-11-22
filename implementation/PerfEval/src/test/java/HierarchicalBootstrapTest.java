import cz.cuni.mff.d3s.perfeval.performancecomparators.HierarchicalBootstrap;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class HierarchicalBootstrapTest {

    @Test
    public void testEvaluateBootstrap() {
        HierarchicalBootstrap bootstrap = new HierarchicalBootstrap(0.05, 10_000);

        // Test evaluateBootstrap with sampleSet1 and sampleSet2
        double[][] sampleSet1 = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        double[][] sampleSet2 = {{7.0, 8.0, 9.0}, {10.0, 11.0, 12.0}};
        boolean result = bootstrap.evaluateBootstrap(sampleSet1, sampleSet2);
        assertFalse(result);

        // Test evaluateBootstrap with different data that should not satisfy the condition
        double[][] sampleSet3 = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        double[][] sampleSet4 = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        boolean result2 = bootstrap.evaluateBootstrap(sampleSet3, sampleSet4);
        assertTrue(result2);
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
