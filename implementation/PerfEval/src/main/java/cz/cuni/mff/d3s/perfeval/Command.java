package cz.cuni.mff.d3s.perfeval;

import cz.cuni.mff.d3s.perfeval.init.PerfEvalCommandFailedException;

public interface Command {
    ExitCode execute() throws PerfEvalCommandFailedException;
}
