package cz.cuni.mff.d3s.perfeval.resultdatabase;

import cz.cuni.mff.d3s.perfeval.ExitCode;

/**
 * Class representing an exception that can occur during the database operations.
 */
public class DatabaseException extends Exception {
    //static String message = "PerfEval database error.";
    /**
     * Message of the exception.
     */
    private final String message;
    /**
     * Exit code of the exception.
     */
    public final ExitCode exitCode;

    /**
     * Constructor of the class.
     *
     * @param msg      message of the exception
     * @param cause    cause of the exception
     * @param exitCode exit code of the exception
     */
    public DatabaseException(String msg, Exception cause, ExitCode exitCode) {
        this.exitCode = exitCode;
        this.message = msg;
        this.initCause(cause);
    }

    /**
     * @return message of the exception
     */
    @Override
    public String toString() {
        if (getCause() != null) {
            return getCause().toString() + System.lineSeparator() + message;
        } else {
            return System.lineSeparator() + message;
        }
    }
}
