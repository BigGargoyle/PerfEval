package cz.cuni.mff.hrdydo;

import cz.cuni.mff.hrdydo.perfevalInit.PerfEvalCommandFailedException;

public interface Command {
    ExitCode execute() throws PerfEvalCommandFailedException;
}
