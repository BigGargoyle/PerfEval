package org.example.GlobalVariables;

public enum ExitCode {
    OK(0),
    atLeastOneWorseResult(1),
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
