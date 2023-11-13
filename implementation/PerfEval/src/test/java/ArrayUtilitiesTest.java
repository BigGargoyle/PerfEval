import cz.cuni.mff.d3s.perfeval.performancecomparatorfactory.ArrayUtilities;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayUtilitiesTest {
    @Test
    public void testCalculateAverage() {
        // Test with a valid matrix
        double[][] matrix = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        double expectedAverage = (1.0 + 2.0 + 3.0 + 4.0 + 5.0 + 6.0) / 6;
        double actualAverage = ArrayUtilities.calculateAverage(matrix);
        assertEquals(expectedAverage, actualAverage, 0.001);

        // Test with an empty matrix
        double[][] emptyMatrix = {};
        double emptyMatrixAverage = ArrayUtilities.calculateAverage(emptyMatrix);
        assertEquals(0.0, emptyMatrixAverage, 0.001);

        // Test with a null matrix
        double[][] nullMatrix = null;
        double nullMatrixAverage = ArrayUtilities.calculateAverage(nullMatrix);
        assertEquals(0.0, nullMatrixAverage, 0.001);
    }

    @Test
    public void testCalcMinSampleCount() {
        // Test with sample statistics, confidence level, and max width
        double[] statistics = {10.0, 20.0, 30.0, 40.0, 50.0};
        double confidenceLevel = 0.95;
        double maxWidth = 0.1;
        int actualSampleCount = ArrayUtilities.calcMinSampleCount(statistics, confidenceLevel, maxWidth);
        assertTrue(actualSampleCount > statistics.length);
    }

    @Test
    public void testMergeArrays() {
        // Test merging arrays
        double[] array1 = {1.0, 2.0, 3.0};
        double[] array2 = {4.0, 5.0};
        double[] array3 = {6.0};
        double[][] sampleSet = {array1, array2, array3};
        double[] expectedMergedArray = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
        double[] actualMergedArray = ArrayUtilities.mergeArrays(sampleSet);
        assertArrayEquals(expectedMergedArray, actualMergedArray, 0.001);
    }
}
