package cz.cuni.mff.d3s.perfeval;

/**
 * Enum summarizing all possible exit codes of PerfEval system.
 */
public enum ExitCode {
    /**
     * Exit code for successful execution of command.
     */
    OK(0),
    //TODO: change to 1
    /**
     * Exit code for unsuccessful execution due to performance get worse.
     */
    atLeastOneWorseResult(0),
    /**
     * Exit code for unsuccessful execution due to invalid arguments.
     */
    invalidArguments(101),
    /**
     * Exit code for unsuccessful execution due to not initialized PerfEval.
     */
    notInitialized(102),
    /**
     * Exit code for unsuccessful execution due to already initialized PerfEval.
     */
    alreadyInitialized(103),
    /**
     * Exit code for unsuccessful execution due to failed evaluation.
     */
    evaluationFailed(104),
    /**
     * Exit code for unsuccessful execution due to database error.
     */
    databaseError(105),

    /**
     * Exit code for unsuccessful execution due to parser error.
     */
    parserError(106);
    /**
     * Integer value of the exit code.
     */
    final int exitCode;

    /**
     * Creates a new exit code.
     *
     * @param value Integer value of the exit code.
     */
    ExitCode(int value) {
        exitCode = value;
    }

    /**
     * Exits the program with the exit code.
     */
    public void exit() {
        System.exit(exitCode);
    }
}
