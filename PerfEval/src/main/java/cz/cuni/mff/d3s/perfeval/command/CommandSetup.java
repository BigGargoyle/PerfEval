package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import joptsimple.OptionSet;

/**
 * Interface for command setup.
 */
public interface CommandSetup {
    /**
     * Sets up the command.
     *
     * @param options Command line options.
     * @param config  Configuration of the program.
     * @return Command to be executed.
     * @throws DatabaseException If there is an error with the database.
     * @throws ParserException   If there is an error with parsing the command line arguments.
     */
    Command setup(OptionSet options, PerfEvalConfig config) throws DatabaseException, ParserException;
}
