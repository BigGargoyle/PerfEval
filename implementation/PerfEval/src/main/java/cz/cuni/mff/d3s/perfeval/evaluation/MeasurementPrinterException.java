package cz.cuni.mff.d3s.perfeval.evaluation;

/**
 * Exception thrown when there is an error with printing the results.
 */
public class MeasurementPrinterException extends Exception {
    /**
     * Message of the exception.
     */
    private final String message;
    /**
     * Constructor.
     * @param message Message of the exception.
     * @param cause Exception that caused this exception.
     */
    public MeasurementPrinterException(String message, Throwable cause) {
        this.message = message;
        initCause(cause);
    }

    /**
     * Getter for message of the exception.
     */
    @Override
    public String getMessage() {
        return message;
    }
}
