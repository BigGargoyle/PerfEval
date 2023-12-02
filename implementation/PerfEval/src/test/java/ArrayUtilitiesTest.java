import cz.cuni.mff.d3s.perfeval.performancecomparators.ArrayUtilities;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayUtilitiesTest {
    @Test
    public void testCalculateAverageValidMatrix() {
        // Test with a valid matrix
        double[][] matrix = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        double expectedAverage = (1.0 + 2.0 + 3.0 + 4.0 + 5.0 + 6.0) / 6;
        double actualAverage = ArrayUtilities.calculateAverage(matrix);
        assertEquals(expectedAverage, actualAverage, 0.001);
    }
    @Test
    public void testCalculateAverageEmptyMatrix() {
        // Test with an empty matrix
        double[][] emptyMatrix = {};
        double emptyMatrixAverage = ArrayUtilities.calculateAverage(emptyMatrix);
        assertEquals(0.0, emptyMatrixAverage, 0.001);
    }
    @Test
    public void testCalculateAverageNullMatrix() {
        // Test with a null matrix
        double nullMatrixAverage = ArrayUtilities.calculateAverage(null);
        assertEquals(0.0, nullMatrixAverage, 0.001);
    }
    @Test
    public void testCalculateAverageValidLargeMatrix() {
        // Test with a large valid matrix
        int rowCount = 1000; // Define the number of rows
        int colCount = 1000; // Define the number of columns

        double[][] matrix = new double[rowCount][colCount];
        double expectedSum = 0.0;

        // Populate the matrix with values and calculate the expected sum
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                matrix[i][j] = i * colCount + j; // Assigning unique values to the matrix elements
                expectedSum += matrix[i][j];
            }
        }

        double expectedAverage = expectedSum / (rowCount * colCount);
        double actualAverage = ArrayUtilities.calculateAverage(matrix);
        assertEquals(expectedAverage, actualAverage, 0.001);
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
    @Test
    public void testMergeArraysInLargeMatrix() {
        // Test merging arrays within a larger matrix structure
        int rows = 1000; // Number of rows in the matrix
        int maxCols = 100; // Maximum number of columns in each row

        // Creating a larger matrix
        double[][] sampleSet = new double[rows][];
        int totalElements = 0;
        double[] expectedMergedArray = new double[rows * maxCols];
        int index = 0;

        // Populate the matrix with arrays of different lengths
        for (int i = 0; i < rows; i++) {
            int colCount = (int) (Math.random() * maxCols) + 1; // Random column count for each row
            double[] array = new double[colCount];

            // Populate array with values and update the expectedMergedArray
            for (int j = 0; j < colCount; j++) {
                double value = Math.random() * 100; // Random value for testing
                array[j] = value;
                expectedMergedArray[index++] = value;
            }

            sampleSet[i] = array;
            totalElements += colCount;
        }

        // Trimming the expectedMergedArray to the actual total elements added
        expectedMergedArray = Arrays.copyOf(expectedMergedArray, totalElements);

        double[] actualMergedArray = ArrayUtilities.mergeArrays(sampleSet);
        assertArrayEquals(expectedMergedArray, actualMergedArray, 0.001);
    }

}
