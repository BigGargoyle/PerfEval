package org.example;
// math library import
import org.apache.commons.math3.stat.inference.TTest;
import org.example.MeasurementFactory.IMeasurement;
import org.example.MeasurementFactory.UniversalTimeUnit;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Represents result of comparing two IMeasurements
 */
public class CompareTestResult{
    private final IMeasurement newTest;
    private final IMeasurement oldTest;
    private final double oldTestAvg;
    private final double newTestAvg;
    private double difference;
    private final boolean testOK;

    /**
     * Constructor of this class compares two input IMeasurements by some statistical method and then compares resulting
     * p-value with critical value. If p-value is larger than the criticalValue, then getCompareResult method will be
     * returning true
     * @param criticalValue highest possible p-value of the statistic test result
     * @param test_new the newer one IMeasurement
     * @param test_old the older one IMeasurement
     */
    public CompareTestResult(double criticalValue, IMeasurement test_new, IMeasurement test_old){
        newTest = test_new;
        oldTest = test_old;

        double pValue = CompareMeasurements(test_new, test_old);
        testOK = !(Math.abs(pValue) > criticalValue);

        oldTestAvg = UniversalTimeUnitAverage(test_old.getMeasuredTimes()).GetNanoSeconds();
        newTestAvg = UniversalTimeUnitAverage(test_new.getMeasuredTimes()).GetNanoSeconds();


        difference = newTestAvg/oldTestAvg;
        difference = (1-difference)*100;
        if(!test_new.hasAscendingPerformanceUnit()){
            difference *= -1;
        }
    }

    private static UniversalTimeUnit UniversalTimeUnitAverage(List<UniversalTimeUnit> times){
        long sum = 0;
        for(UniversalTimeUnit time : times)
            sum+=time.GetNanoSeconds();
        return new UniversalTimeUnit(sum/times.size(), TimeUnit.NANOSECONDS);
    }

    /**
     *
     * @return the difference of performance between the newTest and the oldTest expressed in a percentage
     */
    public double getDifference() { return difference; }

    /**
     *
     * @return if the newTest has better performance than the oldTest
     */
    public boolean getCompareResult() { return testOK; }
    public IMeasurement getNewTest() { return newTest; }
    public IMeasurement getOldTest() { return oldTest; }

    /**
     *
     * @return Average of Values of the oldTest
     */
    public double getOldTestAvg() { return oldTestAvg; }
    /**
     *
     * @return Average of Values of the newTest
     */
    public double getNewTestAvg() { return newTestAvg; }
    static double CompareMeasurements(IMeasurement test1, IMeasurement test2) {
        TTest tTestClass = new TTest();
        double[] values1 = new double[test1.getMeasuredTimes().size()];
        double[] values2 = new double[test2.getMeasuredTimes().size()];
        for (int i = 0; i<values1.length;i++){
            values1[i] = test1.getMeasuredTimes().get(i).GetNanoSeconds();
            values2[i] = test2.getMeasuredTimes().get(i).GetNanoSeconds();
        }
        return tTestClass.t(values1,values2);
    }
}
