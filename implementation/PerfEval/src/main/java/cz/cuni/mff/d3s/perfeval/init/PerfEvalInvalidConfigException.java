package cz.cuni.mff.d3s.perfeval.init;

/**
 * Exception thrown when config file has invalid data.
 */
public class PerfEvalInvalidConfigException extends Exception {

    private final String message;

    public PerfEvalInvalidConfigException() {
        this.message = "Config file has invalid data.";
    }

    /**
     * Constructor for PerfEvalInvalidConfigException.
     */
    public PerfEvalInvalidConfigException(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        String cause = getCause() != null ? getCause().toString() : "";
        return cause + System.lineSeparator() + message;
    }
}
