package cz.cuni.mff.d3s.perfeval.init;

import cz.cuni.mff.d3s.perfeval.ExitCode;

/**
 * Exception thrown when PerfEval command fails.
 */
public class PerfEvalCommandFailedException extends Exception {
    /**
     * Message of exception.
     */
    private static final String DEFAULT_MESSAGE = "PerfEval command failed.";

    private final String message;

    /**
     * Exit code of failed command.
     */
    public final ExitCode exitCode;

    /**
     * Constructor for PerfEvalCommandFailedException.
     *
     * @param exitCode exit code of failed command
     */
    public PerfEvalCommandFailedException(ExitCode exitCode) {
        this.message = DEFAULT_MESSAGE;
        this.exitCode = exitCode;
    }

    public PerfEvalCommandFailedException(String message, ExitCode exitCode) {
        this.message = DEFAULT_MESSAGE + System.lineSeparator() + message;
        this.exitCode = exitCode;
    }

    /**
     * @return message of exception
     */
    @Override
    public String toString() {
        if (getCause() == null) {
            return message;
        }
        return getCause().toString() + System.lineSeparator() + message;
    }

    @Override
    public String getMessage() {
        return this.toString();
    }
}
