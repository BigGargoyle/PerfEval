package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.resultdatabase.Database;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import joptsimple.OptionSet;

import java.nio.file.Path;

import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.PERFEVAL_DIR;

/**
 * Setup for the list-results command.
 */
public class ListResultsSetup implements CommandSetup{
    /**
     * Name of the command.
     */
    static final String commandName = "list-results";

    /**
     * @return Name of the command.
     */
    public static String getCommandName() {
        return commandName;
    }

    @Override
    public Command setup(OptionSet options, PerfEvalConfig config) throws DatabaseException, ParserException {
        Database database = SetupUtilities.constructDatabase(Path.of(System.getProperty("user.dir")).resolve(PERFEVAL_DIR));
        return new ListResultsCommand(database);
    }
}
