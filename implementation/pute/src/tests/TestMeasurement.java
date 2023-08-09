import java.util.List;

public class MockMeasurement implements  IMeasurement{

    String Name;
    List<Double> Values;
    boolean AscendingPerfUnit;

    public MockMeasurement(String name, List<Double> values, boolean ascendingPerfUnit){
        Name = name;
        Values = values;
        AscendingPerfUnit = ascendingPerfUnit;
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
        return AscendingPerfUnit;
    }
}
