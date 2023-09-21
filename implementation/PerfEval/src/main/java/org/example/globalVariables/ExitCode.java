package org.example.globalVariables;

public enum ExitCode {
    OK(0),
    //TODO: change to 1
    atLeastOneWorseResult(0),
    invalidArguments(101),
    notInitialized(102),
    evaluationFailed(103),
    databaseError(104) ;
    final int exitCode;
    ExitCode(int value){
        exitCode = value;
    }

    public int getExitCode() {
        return exitCode;
    }
}
