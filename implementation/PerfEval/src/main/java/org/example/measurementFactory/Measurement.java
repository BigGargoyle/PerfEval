package org.example.measurementFactory;

import java.util.List;

/**
 * A record that represents measurements of one benchmark test.
 */
public record Measurement(String name, List<UniversalTimeUnit> measuredTimes) {

    /**
     * @return name of test
     */
    public String name() {
        return name;
    }

    /**
     * @return measured values
     */
    public List<UniversalTimeUnit> measuredTimes() {
        return measuredTimes;
    }

}
