package MeasurementFactoryTests;

import java.util.List;
import org.example.MeasurementFactory.IMeasurement;
import org.example.MeasurementFactory.UniversalTimeUnit;

public class TestMeasurement implements IMeasurement{

    String Name;
    List<UniversalTimeUnit> Values;
    boolean AscendingPerfUnit;

    public TestMeasurement(String name, List<UniversalTimeUnit> values, boolean ascendingPerfUnit){
        Name = name;
        Values = values;
        AscendingPerfUnit = ascendingPerfUnit;
    }
    @Override
    public String getName() {
        return Name;
    }

    @Override
    public List<UniversalTimeUnit> getMeasuredTimes() {
        return Values;
    }

    @Override
    public boolean hasAscendingPerformanceUnit() {
        return AscendingPerfUnit;
    }
}
