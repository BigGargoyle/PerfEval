import cz.cuni.mff.d3s.perfeval.evaluation.ArrayUtilities;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayUtilitiesTest {
    @Test
    public void testCalculateAverageValidMatrix() {
        // Test with a valid matrix
        double[][] matrix = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        double expectedAverage = (1.0 + 2.0 + 3.0 + 4.0 + 5.0 + 6.0) / 6;
        double actualAverage = ArrayUtilities.calculateHierarchicAverage(matrix);
        assertEquals(expectedAverage, actualAverage, 0.001);
    }
    @Test
    public void testCalculateAverageEmptyMatrix() {
        // Test with an empty matrix
        double[][] emptyMatrix = {};
        double emptyMatrixAverage = ArrayUtilities.calculateHierarchicAverage(emptyMatrix);
        assertEquals(0.0, emptyMatrixAverage, 0.001);
    }
    @Test
    public void testCalculateAverageNullMatrix() {
        // Test with a null matrix
        double nullMatrixAverage = ArrayUtilities.calculateHierarchicAverage(null);
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
        double actualAverage = ArrayUtilities.calculateHierarchicAverage(matrix);
        assertEquals(expectedAverage, actualAverage, 0.001);
    }

}
