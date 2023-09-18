package org.example.PerformanceComparatorFactory;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.TTest;
import org.example.MeasurementFactory.UniversalTimeUnit;

import java.util.List;
import java.util.Random;

public class Bootstrap {
    static final int DefaultBootstrapSampleSize = 10_000;

    //TODO: velmi velká čísla, UniversalTimeUnit je asi nevhodný pro tato porovnání ??? --> zmenšení rozptylu
    // nebo Bootstrap není dobře
    public static boolean Evaluate(List<UniversalTimeUnit> newSet, List<UniversalTimeUnit> oldSet,
                                   double critValue, int minSampleCount) {

        double[] sample1 = CreateBootstrapSample(newSet, minSampleCount);
        double[] sample2 = CreateBootstrapSample(oldSet, minSampleCount);

        TTest tTest = new TTest();
        double pValue = tTest.t(sample1, sample2);
        // returns false if and only if it seems that input sets have different distribution
        return !(Math.abs(pValue) > critValue);
    }

    static double[] CreateBootstrapSample(List<UniversalTimeUnit> basicSample, int minSampleCount) {
        int sampleSize = Math.max(DefaultBootstrapSampleSize, minSampleCount);
        double[] result = new double[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            SummaryStatistics tempStat = new SummaryStatistics();
            for (int j = 0; j < basicSample.size(); j++) {
                Random rand = new Random();
                UniversalTimeUnit randomElement = basicSample.get(rand.nextInt(basicSample.size()));
                tempStat.addValue(randomElement.getNanoSeconds());
            }
            result[i] = tempStat.getMean();
        }
        return result;
    }

}