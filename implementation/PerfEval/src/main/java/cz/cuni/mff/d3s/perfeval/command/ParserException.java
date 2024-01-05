package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.ExitCode;

/**
 * Exception thrown when there is an error with parsing the command line arguments.
 */
public class ParserException extends Exception {
    /**
     * Exit code of the program.
     */
    public ExitCode exitCode = ExitCode.parserError;
    /**
     * Constructor.
     * @param message Message of the exception.
     */
    public ParserException(String message) {
        super(message);
    }
    /**
     * Constructor.
     * @param message Message of the exception.
     * @param exitCode Exit code of the program.
     */
    public ParserException(String message, ExitCode exitCode) {
        super(message);
        this.exitCode = exitCode;
    }

    /**
     * Constructor.
     * @param message Message of the exception.
     * @param e Exception that caused this exception.
     */
    public ParserException(String message, AssertionError e) {
        super(message, e);
    }
}
