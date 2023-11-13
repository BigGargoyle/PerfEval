package cz.cuni.mff.d3s.perfeval.measurementFactory;
import cz.cuni.mff.d3s.perfeval.Samples;

import java.util.List;

/**
 * An interface that represents Parser that converts tests from a source file into IMeasurement objects.
 */
public interface MeasurementParser {
    /**
     * @param fileName a path to the file with benchmark test results
     * @return List of measurements that were in the file
     */
    List<Samples> getTestsFromFiles(String[] fileNames);

}
