import pojoBenchmarkDotNet.Benchmark;
import pojoBenchmarkDotNet.Measurement;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkDotNetTest implements ITest {
    private static String testedIterationMode = "Workload";
    private static  String testedIterationStage = "Actual";
    String Name;
    int InternalTestID;
    List<Double> Values;
    public static ITest ConstructTest(Benchmark pattern){
        return new BenchmarkDotNetTest(pattern);
    }
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
}
