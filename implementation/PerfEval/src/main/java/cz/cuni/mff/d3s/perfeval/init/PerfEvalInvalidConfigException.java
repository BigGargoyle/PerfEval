package cz.cuni.mff.d3s.perfeval.init;

/**
 * Exception thrown when config file has invalid data
 */
public class PerfEvalInvalidConfigException extends Exception {
    /**
     * Message of exception
     */
    static String message = "Config file has invalid data.";

    @Override
    public String toString() {
        String cause = getCause() != null ? getCause().toString() : "";
        return cause + System.lineSeparator() + message;
    }
}
