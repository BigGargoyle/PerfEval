package org.example.MeasurementFactory;

import java.util.List;

/**
 * An interface that represents measurements of one benchmark test.
 */
public interface IMeasurement {
    /**
     *
     * @return name of test
     */
    String getName();

    /**
     *
     * @return measured values
     */
    List<UniversalTimeUnit> getMeasuredTimes();

    // larger value implies better performance

    /**
     * It is needed to recognize if a higher value means better performance or not.
     * @return if a higher value means better performance
     */
    boolean hasAscendingPerformanceUnit();
}
