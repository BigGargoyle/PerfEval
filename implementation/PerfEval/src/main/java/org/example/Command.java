package org.example;

import org.example.perfevalInit.PerfEvalCommandFailedException;

public interface Command {
    ExitCode execute() throws PerfEvalCommandFailedException;
}
