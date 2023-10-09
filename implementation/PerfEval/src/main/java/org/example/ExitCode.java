package org.example;

/**
 * Enum summarizing all possible exit codes of PerfEval system.
 */
public enum ExitCode {
    OK(0),
    //TODO: change to 1
    atLeastOneWorseResult(0),
    invalidArguments(101),
    notInitialized(102),
    evaluationFailed(103),
    databaseError(104),
    fileSystemError(105);
    final int exitCode;
    ExitCode(int value){
        exitCode = value;
    }

    public void exit() {
        System.exit(exitCode);
    }
}
