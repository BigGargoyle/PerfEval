package cz.cuni.mff.d3s.perfeval.init;

import cz.cuni.mff.d3s.perfeval.ExitCode;

/**
 * Exception thrown when PerfEval command fails
 */
public class PerfEvalCommandFailedException extends Exception {
    /**
     * Message of exception
     */
    static String message = "PerfEval command failed.";
    /**
     * Exit code of failed command
     */
    public final ExitCode exitCode;

    /**
     * Constructor for PerfEvalCommandFailedException
     *
     * @param exitCode exit code of failed command
     */
    public PerfEvalCommandFailedException(ExitCode exitCode) {
        this.exitCode = exitCode;
    }

    public PerfEvalCommandFailedException(String message, ExitCode exitCode) {
        super(message);
        this.exitCode = exitCode;
    }

    /**
     * @return message of exception
     */
    @Override
    public String toString() {
        return getCause().toString() + System.lineSeparator() + message;
    }
}
