package cz.cuni.mff.hrdydo.resultDatabase;

import cz.cuni.mff.hrdydo.ExitCode;

public class DatabaseException extends Exception {
    //static String message = "PerfEval database error.";
    String message;
    public final ExitCode exitCode;

    public DatabaseException(ExitCode exitCode) {
        this.message = "PerfEval database error.";
        this.exitCode = exitCode;
    }

    public DatabaseException(String msg, Exception cause, ExitCode exitCode) {
        this.exitCode = exitCode;
        this.message = msg;
        this.initCause(cause);
    }
    @Override
    public String toString() {
        return getCause().toString() + System.lineSeparator() + message;
    }
}
