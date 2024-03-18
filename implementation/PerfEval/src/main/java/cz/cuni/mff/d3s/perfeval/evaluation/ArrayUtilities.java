package cz.cuni.mff.d3s.perfeval.evaluation;

import org.apache.commons.math3.stat.StatUtils;

/**
 * Class with static methods for working with arrays.
 */
public class ArrayUtilities {
    /**
     * Calculates average of all values in matrix.
     *
     * @param matrix matrix of values
     * @return average of all values in matrix
     */
    public static double calculateHierarchicAverage(double[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return 0.0;
        }
        double sum = 0.0;

        for (double[] doubles : matrix) {
            sum += StatUtils.mean(doubles);
        }
        return sum / matrix.length;
    }
}
