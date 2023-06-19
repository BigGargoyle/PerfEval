class CompareTestResult{
    private final ITest newTest;
    private final ITest oldTest;

    // newTest = difference*oldTest
    private final double difference;
    private final boolean testOK;
    public CompareTestResult(double diff, boolean compareResult, ITest test_new, ITest test_old){
        difference = diff;
        testOK = compareResult;
        newTest = test_new;
        oldTest = test_old;
    }
    public double getDiffernce(){return difference;}
    public boolean getTestResult() {return testOK;}
    public ITest getNewTest() {return newTest;}
    public ITest getOldTest() {return oldTest;}
}
