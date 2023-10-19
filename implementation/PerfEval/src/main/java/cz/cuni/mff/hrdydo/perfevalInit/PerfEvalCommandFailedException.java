package cz.cuni.mff.hrdydo.perfevalInit;

import cz.cuni.mff.hrdydo.ExitCode;

public class PerfEvalCommandFailedException extends Exception {
    static String message = "PerfEval command failed.";

    public final ExitCode exitCode;
    public PerfEvalCommandFailedException(ExitCode exitCode){
        this.exitCode = exitCode;
    }

    @Override
    public String toString() {
        return getCause().toString() + System.lineSeparator() + message;
    }
}
