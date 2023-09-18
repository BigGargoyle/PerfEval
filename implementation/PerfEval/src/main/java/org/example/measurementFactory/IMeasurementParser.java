package org.example.measurementFactory;
import java.util.List;

/**
 * An interface that represents Parser that converts tests from a source file into IMeasurement objects.
 */
public interface IMeasurementParser {
    /**
     * @param fileName a path to the file with benchmark test results
     * @return List of measurements that were in the file
     */
    List<Measurement> getTestsFromFile(String fileName);

}
