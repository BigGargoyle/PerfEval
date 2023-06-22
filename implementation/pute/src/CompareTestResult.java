// math library import
import org.apache.commons.math3.stat.inference.TTest;

class CompareTestResult{
    private final ITest newTest;
    private final ITest oldTest;
    private final double oldTestAvg;
    private final double newTestAvg;
    private double difference;
    private final boolean testOK;
    public CompareTestResult(double criticalValue, ITest test_new, ITest test_old){
        newTest = test_new;
        oldTest = test_old;

        double pValue = CompareTests(test_new, test_old);
        testOK = !(Math.abs(pValue) > criticalValue);

        oldTestAvg = test_old.GetValues().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        newTestAvg = test_new.GetValues().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);


        difference = newTestAvg/oldTestAvg;
        difference = (1-difference)*100;
        if(!test_new.HasAscendingPerformanceUnit()){
            difference *= -1;
        }
    }
    public double getDifference(){return difference;}
    public boolean getTestResult() {return testOK;}
    public ITest getNewTest() {return newTest;}
    public ITest getOldTest() {return oldTest;}
    public double getOldTestAvg() { return oldTestAvg; }
    public double getNewTestAvg() { return newTestAvg; }
    static double CompareTests(ITest test1, ITest test2) {
        TTest tTestClass = new TTest();
        double[] values1 = new double[test1.GetValues().size()];
        double[] values2 = new double[test1.GetValues().size()];
        for (int i = 0; i<values1.length;i++){
            values1[i] = test1.GetValues().get(i);
            values2[i] = test2.GetValues().get(i);
        }
        return tTestClass.t(values1,values2);
    }
}
