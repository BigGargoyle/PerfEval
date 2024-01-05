package cz.cuni.mff.d3s.perfeval.measurementfactory;

/**
 * Exception thrown when there is an error during parsing.
 */
public class MeasurementParserException extends RuntimeException {
    /**
     * Message of exception.
     */
    private final String message;

    /**
     * Constructor for MeasurementParserException.
     *
     * @param message message of exception
     */
    public MeasurementParserException(String message) {
        this.message = message;
    }

    /**
     * Constructor for MeasurementParserException.
     *
     * @param message message of exception
     * @param cause cause of exception
     */
    public MeasurementParserException(String message, Throwable cause) {
        this.message = message;
        initCause(cause);
    }

    /**
     * @return message of exception
     */
    @Override
    public String toString() {
        return message;
    }
}
