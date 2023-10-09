package org.example.resultDatabase;

import org.example.ExitCode;

public class DatabaseException extends Exception {
    static String message = "PerfEval database error.";

    public final ExitCode exitCode;

    public DatabaseException(ExitCode exitCode) {
        this.exitCode = exitCode;
    }

    @Override
    public String toString() {
        return getCause().toString() + System.lineSeparator() + message;
    }
}
