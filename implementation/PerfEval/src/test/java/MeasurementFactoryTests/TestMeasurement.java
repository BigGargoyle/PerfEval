package MeasurementFactoryTests;

import java.util.List;
import org.example.MeasurementFactory.IMeasurement;
import org.example.MeasurementFactory.UniversalTimeUnit;

public class TestMeasurement implements IMeasurement{

    final String Name;
    final List<UniversalTimeUnit> Values;
    final boolean AscendingPerfUnit;

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

}
