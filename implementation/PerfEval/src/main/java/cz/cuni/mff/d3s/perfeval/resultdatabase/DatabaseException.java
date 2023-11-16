package cz.cuni.mff.d3s.perfeval.resultdatabase;

import cz.cuni.mff.d3s.perfeval.ExitCode;

public class DatabaseException extends Exception {
    //static String message = "PerfEval database error.";
    String message;
    public final ExitCode exitCode;
    public DatabaseException(String msg, Exception cause, ExitCode exitCode) {
        this.exitCode = exitCode;
        this.message = msg;
        this.initCause(cause);
    }
    @Override
    public String toString() {
        if(getCause()!=null)
            return getCause().toString() + System.lineSeparator() + message;
        else
            return System.lineSeparator() + message;
    }
}
