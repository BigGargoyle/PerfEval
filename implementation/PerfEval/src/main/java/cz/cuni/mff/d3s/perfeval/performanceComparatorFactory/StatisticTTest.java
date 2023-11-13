package cz.cuni.mff.d3s.perfeval.performanceComparatorFactory;

import org.apache.commons.math3.stat.inference.TTest;

public class StatisticTTest {
    double critValue;

    public StatisticTTest(double critValue) {
        this.critValue = critValue;
    }

    public boolean areSetsSame(double[][] sampleSet1, double[][] sampleSet2){
        double[] samples1 = ArrayUtilities.mergeArrays(sampleSet1);
        double[] samples2 = ArrayUtilities.mergeArrays(sampleSet2);
        TTest tTest = new TTest();
        double pValue = tTest.t(samples1, samples2);
        return Math.abs(pValue)<=critValue;
    }

}
