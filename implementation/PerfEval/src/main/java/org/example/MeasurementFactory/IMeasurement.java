package org.example.MeasurementFactory;

import java.util.List;

/**
 * An interface that represents measurements of one benchmark test.
 */
public interface IMeasurement {
    /**
     * @return name of test
     */
    String getName();

    /**
     * @return measured values
     */
    List<UniversalTimeUnit> getMeasuredTimes();

}
