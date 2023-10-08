package org.example;

import org.example.globalVariables.ExitCode;
import org.example.perfevalInit.PerfEvalCommandFailedException;

public interface ICommand {
    ExitCode execute() throws PerfEvalCommandFailedException;
}
