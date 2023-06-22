import pojoJMH.BenchmarkJMHJSONBase;
import pojoJMH.PrimaryMetric;

import java.util.ArrayList;
import java.util.List;

public class JMHTest implements ITest{
    String Name;
    int InternalTestID;
    List<Double> Values;
    public static ITest ConstructTest(BenchmarkJMHJSONBase input){
        return new JMHTest(input);
    }
    private JMHTest(BenchmarkJMHJSONBase input){
        Name = input.getBenchmark();
        InternalTestID = Name.hashCode();
        Values = new ArrayList<Double>();
        PrimaryMetric primaryMetric = input.getPrimaryMetric();
        // !!! adding average of each measuredData, because rawData is a field of list of lists of Doubles
        for (List<Double> measuredData: primaryMetric.getRawData()){
            Values.add(
                measuredData.stream().mapToDouble(Double::doubleValue)
                        .average().orElse(0.0)
            );
        }
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public int getInternalID() {
        return InternalTestID;
    }

    @Override
    public List<Double> getValues() {
        return Values;
    }

    @Override
    public boolean hasAscendingPerformanceUnit() {
        // by default ops/us -> more ops/us -> better performance
        return true;
    }
}
