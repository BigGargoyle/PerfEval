package org.example.MeasurementFactory;

import java.util.List;

/**
 * An interface that represents measurements of one benchmark test.
 */
public class IMeasurement {
    final String name;
    final List<UniversalTimeUnit> measuredTimes;

    public IMeasurement(String name, List<UniversalTimeUnit> measuredTimes){
        this.name = name;
        this.measuredTimes = measuredTimes;
    }

    /**
     * @return name of test
     */
    public String getName(){
        return name;
    }

    /**
     * @return measured values
     */
    public List<UniversalTimeUnit> getMeasuredTimes(){
        return measuredTimes;
    }

}
