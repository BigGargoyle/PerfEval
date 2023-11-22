package cz.cuni.mff.d3s.perfeval.performancecomparators;

import org.apache.commons.math3.stat.inference.TTest;

/**
 * Class representing a statistical t-test.
 */
public class StatisticTTest {
    /**
     * Critical value of the t-test.
     */
    double critValue;

    /**
     * Constructor of the class.
     *
     * @param critValue Critical value of the t-test.
     */
    public StatisticTTest(double critValue) {
        this.critValue = critValue;
    }

    /**
     * Method for comparing two sets of samples using the t-test.
     *
     * @param sampleSet1 first set of samples
     * @param sampleSet2 second set of samples
     * @return true if the sets are of the same distribution, false otherwise
     */
    public boolean areSetsSame(double[][] sampleSet1, double[][] sampleSet2) {
        double[] samples1 = ArrayUtilities.mergeArrays(sampleSet1);
        double[] samples2 = ArrayUtilities.mergeArrays(sampleSet2);
        TTest tTest = new TTest();
        double pValue = tTest.t(samples1, samples2);
        return Math.abs(pValue) <= critValue;
    }

}
