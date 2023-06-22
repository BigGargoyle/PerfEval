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
            Values.add(average(measuredData));
        }
    }

    private double average(List<Double> list){
        double sum = 0;
        for(Double d : list)sum+=d;
        return sum/list.size();
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
        // by default ops/us -> more ops/us -> better performance
        return true;
    }
}
