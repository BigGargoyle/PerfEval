import pojoJMH.BenchmarkJMHJSONBase;
import pojoJMH.PrimaryMetric;

import java.util.ArrayList;
import java.util.List;

public class JMHMeasurement implements IMeasurement {
    String Name;
    List<Double> Values;
    public static IMeasurement ConstructTest(BenchmarkJMHJSONBase input){
        return new JMHMeasurement(input);
    }
    private JMHMeasurement(BenchmarkJMHJSONBase input){
        Name = input.getBenchmark();
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
    public List<Double> getValues() {
        return Values;
    }

    @Override
    public boolean hasAscendingPerformanceUnit() {
        // by default ops/us -> more ops/us -> better performance
        return true;
    }
}
