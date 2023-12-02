package cz.cuni.mff.d3s.perfeval.measurementfactory;
import cz.cuni.mff.d3s.perfeval.Samples;

import java.util.List;

/**
 * An interface that represents Parser that converts tests from a source file into Samples objects.
 * @see Samples
 */
public interface MeasurementParser {
    /**
     * Parses files with results of performance tests
     *
     * @param fileNames names of files with results of performance tests
     * @return list of Samples objects
     */
    List<Samples> getTestsFromFiles(String[] fileNames);

    /**
     * Parses file with results of performance tests
     * @param fileName name of file with results of performance tests
     * @return list of Samples objects
     */
    List<Samples> getTestsFromFile(String fileName);

}
