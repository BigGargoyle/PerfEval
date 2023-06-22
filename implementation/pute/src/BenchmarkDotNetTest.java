import pojoBenchmarkDotNet.Benchmark;
import pojoBenchmarkDotNet.Measurement;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkDotNetTest implements ITest {
    /*
    * testedIterationMode and testedIterationStage Strings are used for recognizing correct type of measured values
    * it is needed because the BenchmarkDotNet framework is measuring also warmup, jitting, overhead etc. phases
    * of the software running
    * */
    static String testedIterationMode = "Workload";
    static String testedIterationStage = "Actual";
    String Name;
    // TODO: find out use of this ID, maybe it will not be used
    int InternalTestID;
    List<Double> Values;
    public static ITest ConstructTest(Benchmark pattern){
        return new BenchmarkDotNetTest(pattern);
    }
    /*
    * This constructor is creating an instance of BenchmarkDotNetTest class that is based on values from
    * an instance of BenchmarkDotNetJSONBase class. This class is made from a JSON file that is a result
    * of BenchmarkDotNet run.
    * This class main usage is for simplifying and clarifying data package that is contained inside the
    * BenchmarkDotNetJSONBase class.
    * */
    private BenchmarkDotNetTest(Benchmark pattern){
        Name = pattern.getMethodTitle();
        InternalTestID = Name.hashCode();
        List<Measurement> measurements = pattern.getMeasurements();
        Values = new ArrayList<Double>();
        for(Measurement measurement:measurements){
            // I want to test only measurements with 'Workload' mode and 'Actual' stage
            if(measurement.getIterationMode().equals(testedIterationMode) &&
                    measurement.getIterationStage().equals(testedIterationStage)){
                Values.add(measurement.getNanoseconds().doubleValue());
            }
        }
    }

    @Override
    public String GetName() {
        return Name;
    }

    @Override
    public int GetInternalID() {
        return InternalTestID;
    }

    @Override
    public List<Double> GetValues() {
        return Values;
    }

    @Override
    public boolean HasAscendingPerformanceUnit() {
        // by default time -> more time -> worse performance
        return false;
    }
}
