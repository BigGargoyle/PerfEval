import cz.cuni.mff.d3s.perfeval.performancecomparatorfactory.StatisticTTest;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticTTestTest {

    @Test
    public void testAreSetsDifferent() {
        StatisticTTest tTest = new StatisticTTest(0.05);

        // Test when sampleSet1 and sampleSet2 are significantly different
        double[][] sampleSet1 = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        double[][] sampleSet2 = {{7.0, 8.0, 9.0}, {10.0, 11.0, 12.0}};
        boolean result = tTest.areSetsSame(sampleSet1, sampleSet2);
        assertFalse(result);

        // Test when sampleSet1 and sampleSet2 are not significantly different
        double[][] sampleSet3 = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        double[][] sampleSet4 = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        boolean result2 = tTest.areSetsSame(sampleSet3, sampleSet4);
        assertTrue(result2);

        // Test when the sample sets are empty, which should throw a MathIllegalArgumentException
        double[][] emptySampleSet1 = {};
        double[][] emptySampleSet2 = {};
        try {
            tTest.areSetsSame(emptySampleSet1, emptySampleSet2);
            fail("MathIllegalArgumentException should be thrown");
        } catch (MathIllegalArgumentException e) {
            // This exception is expected
        }
    }
}
