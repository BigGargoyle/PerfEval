import java.util.List;

/**
 * An interface that represents Parser that converts tests from a source file into IMeasurement objects.
 */
public interface IMeasurementParser {
    /**
     *
     * @param fileName a path to the file with benchmark test results
     * @return List of measurements that were in the file
     */
    List<IMeasurement> GetTestsFromFile(String fileName);

    /**
     *
     * @return String representation of benchmark format type (JMH, BenchmarkDotNet etc.)
     */
    String GetParserType();

    /**
     * In the future it should be needed to identify each IMeasurement by a unique number. This function is used to
     * create a unique number identifying the whole set of IMeasurements based on file modified time and benchmark type
     * @return a unique number identifying source file
     */
    Long GetUniqueID();
}
