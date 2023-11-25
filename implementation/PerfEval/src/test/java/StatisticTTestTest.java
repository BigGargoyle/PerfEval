import cz.cuni.mff.d3s.perfeval.performancecomparators.StatisticTTest;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticTTestTest {

    @Test
    public void testDifferentSets() {
        StatisticTTest tTest = new StatisticTTest(0.05);

        // Test when sampleSet1 and sampleSet2 are significantly different
        double[][] sampleSet1 = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        double[][] sampleSet2 = {{7.0, 8.0, 9.0}, {10.0, 11.0, 12.0}};
        boolean result = tTest.areSetsSame(sampleSet1, sampleSet2);
        assertFalse(result);
    }
    @Test
    public void testSimilarSets() {
        StatisticTTest tTest = new StatisticTTest(0.05);

        // Test when sampleSet1 and sampleSet2 are not significantly different
        double[][] sampleSet3 = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        double[][] sampleSet4 = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        boolean result2 = tTest.areSetsSame(sampleSet3, sampleSet4);
        assertTrue(result2);
    }
    @Test
    public void testEmptySets() {
        StatisticTTest tTest = new StatisticTTest(0.05);

        // Test when the sample sets are empty, which should throw a MathIllegalArgumentException
        double[][] emptySampleSet1 = {};
        double[][] emptySampleSet2 = {};

        assertThrows(MathIllegalArgumentException.class, () -> tTest.areSetsSame(emptySampleSet1, emptySampleSet2));
    }
    @Test
    public void testDifferentSetsLargeCount() {
        StatisticTTest tTest = new StatisticTTest(0.05);

        // Test when sampleSet1 and sampleSet2 are significantly different with a large count of elements
        double[][] sampleSet1 = new double[2][1000];
        double[][] sampleSet2 = new double[2][1000];

        // Populate sampleSet1 and sampleSet2 with different values
        for (int i = 0; i < 1000; i++) {
            sampleSet1[0][i] = i + 1;  // Values from 1 to 1000
            sampleSet1[1][i] = i + 1001;  // Values from 1001 to 2000

            sampleSet2[0][i] = i + 2001;  // Values from 2001 to 3000
            sampleSet2[1][i] = i + 3001;  // Values from 3001 to 4000
        }

        boolean result = tTest.areSetsSame(sampleSet1, sampleSet2);
        assertFalse(result);
    }

    @Test
    public void testSimilarSetsLargeCount() {
        StatisticTTest tTest = new StatisticTTest(0.05);

        // Test when sampleSet1 and sampleSet2 are not significantly different with a large count of elements
        double[][] sampleSet3 = new double[2][1000];
        double[][] sampleSet4 = new double[2][1000];

        // Populate sampleSet3 and sampleSet4 with similar values
        for (int i = 0; i < 1000; i++) {
            sampleSet3[0][i] = i + 1;  // Values from 1 to 1000
            sampleSet3[1][i] = i + 1001;  // Values from 1001 to 2000

            sampleSet4[0][i] = i + 1;  // Values from 1 to 1000
            sampleSet4[1][i] = i + 1001;  // Values from 1001 to 2000
        }

        boolean result2 = tTest.areSetsSame(sampleSet3, sampleSet4);
        assertTrue(result2);
    }

}
