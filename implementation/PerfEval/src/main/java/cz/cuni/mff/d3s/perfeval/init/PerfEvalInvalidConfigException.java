package cz.cuni.mff.d3s.perfeval.init;

/**
 * Exception thrown when config file has invalid data.
 */
public class PerfEvalInvalidConfigException extends Exception {

    /**
     * Constructor for PerfEvalInvalidConfigException.
     */
    @Override
    public String toString() {
        String cause = getCause() != null ? getCause().toString() : "";
        String message = "Config file has invalid data.";
        return cause + System.lineSeparator() + message;
    }
}
