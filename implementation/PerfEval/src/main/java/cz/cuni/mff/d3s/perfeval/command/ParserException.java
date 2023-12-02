package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.ExitCode;

public class ParserException extends Exception {
    public ExitCode exitCode = ExitCode.parserError;
    public ParserException(String message) {
        super(message);
    }
    public ParserException(String message, ExitCode exitCode) {
        super(message);
        this.exitCode = exitCode;
    }

    public ParserException(String message, AssertionError e) {
        super(message, e);
    }
}
