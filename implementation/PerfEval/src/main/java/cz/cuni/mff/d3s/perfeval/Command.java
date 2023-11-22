package cz.cuni.mff.d3s.perfeval;

import cz.cuni.mff.d3s.perfeval.init.PerfEvalCommandFailedException;

/**
 * Interface for commands.
 * Commands are used to perform actions on the database or evaluating results from the database.
 * Used because of the high number of possible commands.
 */
public interface Command {
    /**
     * Executes the command.
     *
     * @return Exit code of the command.
     * @throws PerfEvalCommandFailedException If the command fails.
     */
    ExitCode execute() throws PerfEvalCommandFailedException;
}
